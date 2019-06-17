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
        // Get all Categories
        List<Category> allCategories = categoryController.getCategories();
        entityManager.clear();

        int count = 0;
        while (count < countPlayer) {
            int groupSize = commitAfter;
            if (count + groupSize > countPlayer) {
                groupSize = (countPlayer - count);
            }

            runGroup(groupSize, countGamesEach, allCategories, true, true);

            count += groupSize;
        }
    }

    private void runGroup(int playerCount, int gamesCount, List<Category> allCategories,
        boolean print, boolean printTime) {
        // print start message
        if (print) {  // print message?
            if (printTime) {
                Date timestempt = new Date();
                System.out.print("> " + timestempt.toString() +
                    " (" + timestempt.getTime() + "): ");
            }
            System.out.println("Spieler erstellen ...)");
        }

        // create player & games
        Long groupName = new Date().getTime();
        for (int i = 0; i < playerCount; ++i) {
            // create player
            Player player = new Player();
            List<QuestionAsked> allAskedQuestions = new ArrayList<>();

            // set name
            player.setName("player" + groupName + "_" + i);

            // play games
            for (int j = 0; j < gamesCount; ++j) {
                allAskedQuestions.addAll(genGame(player, allCategories));
            }

            // commit
            addToBatch(player, allAskedQuestions);
        }
    }

    private List<QuestionAsked> genGame(Player player, List<Category> allCategories) {
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

        // play game
        List<QuestionAsked> qs = simulateGameplay(game);

        // add end date = start date + x
        Date endDate = new Date();
        endDate = addToDate(endDate, daysToAdd, 0, 0, 0);
        game.setEndDatetime(endDate);

        return qs;
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

    private void addToBatch(Player player, List<QuestionAsked> asked) {
        // batch size
        final int batchSize = 100;

        // create new batch?
        if (batchPlayer == null || batchAskedQuestions == null ) {
            batchPlayer = new ArrayList<>();
            batchAskedQuestions = new ArrayList<>();
        }

        batchPlayer.add(player);
        batchAskedQuestions.add(asked);

        if (batchPlayer.size() >= batchSize){
            commitAll(true, true);
        }
    }

    private void commitAll(boolean print, boolean printTime) {

        if (batchPlayer.size() != batchAskedQuestions.size()){
            throw new Error("Array size of players does not match array size of asked questions.");
        }

        // print start message
        if (print) {  // print message?
            if (printTime) {
                Date timestempt = new Date();
                System.out.print("> " + timestempt.toString() +
                    " (" + timestempt.getTime() + "): ");
            }
            System.out.println("Start commit of batch (" + batchPlayer.size() + " Spieler) ...)");
        }

        //
        EntityTransaction transaction = null;
        try {
            // Start Database transaction
            transaction = entityManager.getTransaction();
            transaction.begin();

            // persist all player
            for (int i = 0; i < batchPlayer.size(); ++i) {
                Player player = batchPlayer.get(i);

                // player
                entityManager.persist(player);
            }

            // persist all games
            for (int i = 0; i < batchPlayer.size(); ++i) {
                Player player = batchPlayer.get(i);

                // games
                for (Game g : player.getGames()) {
                    entityManager.persist(g);
                }
            }

            // persist all askedQuestions
            for (int i = 0; i < batchAskedQuestions.size(); ++i) {
                List<QuestionAsked> asked = batchAskedQuestions.get(i);

                // asked questions for games
                for (QuestionAsked qs : asked) {
                    entityManager.persist(qs);
                }
            }

            // ---- Commit --------
            transaction.commit();
            entityManager.clear();

            // ---- Empty Batch --------
            batchPlayer = null;
            batchAskedQuestions = null;

        } catch (RuntimeException e) {
            // Rollback changes
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            throw new Error("Could not commit group of players and games.");
        }
    }

    /*
    for (int i = 0; i < batchPlayer.size(); ++i) {
                Player player = batchPlayer.get(i);
                List<QuestionAsked> asked = batchAskedQuestions.get(i);

                // ---- Persist Elements ---------
                // player
                entityManager.persist(player);

                // games
                for (Game g : player.getGames()) {
                    entityManager.persist(g);
                }

                // asked questions for games
                for (QuestionAsked qs : asked) {
                    entityManager.persist(qs);
                }
                // -------------------------------
            }
     */

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
