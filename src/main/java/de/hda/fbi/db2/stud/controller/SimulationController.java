package de.hda.fbi.db2.stud.controller;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import de.hda.fbi.db2.stud.entity.Category;
import de.hda.fbi.db2.stud.entity.Game;
import de.hda.fbi.db2.stud.entity.Player;
import de.hda.fbi.db2.stud.entity.Question;
import de.hda.fbi.db2.stud.entity.QuestionAsked;


/**
 * SimulationController class.
 *
 * @author Ruben van Laack
 */
public class SimulationController {

    private EntityManager entityManager;
    private CategoryController categoryController;
    private Random random;

    private ArrayList<Player> batchPlayer = null;
    private ArrayList<List<QuestionAsked>> batchAskedQuestions = null;

    private int countPlayer;
    private int countGamesEach;
    private int commitAfter;


    public SimulationController(int countPlayer, int countGamesEach,
        int commitAfter, EntityManagerFactory emf) {

        this.entityManager = emf.createEntityManager();
        random = new Random();
        this.countPlayer = countPlayer;
        this.countGamesEach = countGamesEach;

        categoryController = new CategoryController();
        categoryController.load(this.entityManager);
        this.commitAfter = commitAfter;
    }

    public void close() {
        entityManager.close();
    }

    public void runSimulation() {
        //TODO(ruben): try-catch-block
        //TODO(ruben): paramenter
        final int playerCount = 1000;
        final int gamesCount = 100;
        final int batchSize = 50;
        Long groupName = new Date().getTime();

        // Get all Categories
        List<Category> allCategories = categoryController.getCategories();
        entityManager.clear();

        // Start Database transaction
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        // ---- persist, flush, clear ----
        // for each player
        int batchCounter = 0;
        for (int p = 0; p < playerCount; ++p) {

            // create player
            Player player = new Player();
            player.setName("player" + groupName + "_" + p);

            //persist player
            entityManager.persist(player);
            ++batchCounter; // increase for every persist


            // for each game
            for (int g = 0; g < gamesCount; ++g){

                // create game
                Game game = genGame(player, allCategories);

                // add end date to game = start date + x
                int hoursToAdd = random.nextInt(24); // 0 - 23
                Date endDate = addToDate(
                    game.getStartDatetime(), 0, hoursToAdd, 0, 0);
                game.setEndDatetime(endDate);

                //persist game
                entityManager.persist(game);
                ++batchCounter; // increase for every persist

                //simulate game play
                List<QuestionAsked> gameQuestions = simulateGameplay(game);

                // for each question in game
                for (QuestionAsked gameQuestion : gameQuestions) {
                    //persist gameQuestion
                    entityManager.persist(gameQuestion);
                    ++batchCounter; // increase for every persist

                    // flush every 20th entity that is persisted
                    if ((batchCounter % batchSize) == 0) {  // should be same as JDBC batch size
                        //flush a batch of inserts and release memory:
                        entityManager.flush();
                        entityManager.clear();
                    }
                }
            }
        }

        // ---- Commit --------
        transaction.commit();
        entityManager.clear();
    }

    private Game genGame(Player player, List<Category> allCategories) {
        // create game
        Game game = new Game();

        // game settings
        game.setPlayer(player);
        player.getGames().add(game);

        // count questions = 1 - 10
        int questCount = random.nextInt(10) + 1;  // 1 - 10
        game.setMaxQuestions(questCount);

        // choose how may categories (random) 2-5
        int categoriesCount = random.nextInt(4) + 2;  // 2 - 5
        List<Category> gameCategories = new ArrayList<>();
        //HashSet<Integer> gameCatIndices = new HashSet<>();

        // get random categories
        for (int i = 0; i < categoriesCount; ++i) {
            //int randomQuestionIndex = (int) (Math.random() * allCategories.size());
            int randomQuestionIndex = random.nextInt(allCategories.size()); // 0 - size
            Category selectedCategory = allCategories.get(randomQuestionIndex);

            // is this category already selected for this game?
            if (!gameCategories.contains(selectedCategory)) {
                gameCategories.add(selectedCategory);
            } else {
                --i; // need new category, run loop again
            }

        }
        game.setCategories(gameCategories);

        // get start date Doday+(Random * 100days)
        int daysToAdd = random.nextInt(100); // 0 - 99
        Date gameStartDate = new Date();
        gameStartDate = addToDate(gameStartDate, daysToAdd, 0, 0, 0);
        game.setStartDatetime(gameStartDate);

        /*
        // play game
        List<QuestionAsked> qs = simulateGameplay(game);

        // add end date = start date + x
        Date endDate = new Date();
        endDate = addToDate(endDate, daysToAdd, 0, 0, 0);
        game.setEndDatetime(endDate);
         */

        return game;
    }

    private List<QuestionAsked> simulateGameplay(Game game) {
        HashSet<Integer> usedQuestionIds = new HashSet<>();  // cache for used questions (only ids)
        List<QuestionAsked> qs = new ArrayList<>();  // list of asked questions for new game

        Question currentQuestion = null;
        int questCount = 0;
        int correctAnwers = 0;

        do {
            // get questions / make guess / count right answers
            currentQuestion = GameController.getRandomQuestion(game, usedQuestionIds);

            if (currentQuestion == null) {
                // no more questions for the chosen categories
                break;
            }

            // cache id of current question
            usedQuestionIds.add(currentQuestion.getId());

            // select answer; random
            int selectedAnswer = random.nextInt(4) + 1;  // 1 - 4

            // save the selected answer
            QuestionAsked newQuestAnswer = new QuestionAsked();
            newQuestAnswer.setSelectedAnswer(selectedAnswer);
            newQuestAnswer.setQuestion(currentQuestion);
            newQuestAnswer.setGame(game);
            qs.add(newQuestAnswer);

            // evaluate answer
            boolean correct = (selectedAnswer == currentQuestion.getCorrectAnswer());

            // save count of right answers in game
            if (correct) {
                ++correctAnwers;
            }

            // TODO(ruben): save count of correct questions in game

            ++questCount;
        } while (questCount < game.getMaxQuestions());

        return qs;
    }

    public static Date addToDate(Date date, int days, int hours, int minutes, int seconds) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if (days != 0) {
            cal.add(Calendar.DATE, days);
        }

        if (hours != 0) {
            cal.add(Calendar.HOUR, hours);
        }

        if (minutes != 0) {
            cal.add(Calendar.MINUTE, minutes);
        }

        if (seconds != 0) {
            cal.add(Calendar.SECOND, seconds);
        }

        return cal.getTime();
    }
}
