package de.hda.fbi.db2.stud.controller;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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

    public void close(){
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

        //entityManager.getTransaction().commit();
    }

    private void runGroup(int playerCount, int gamesCount, List<Category> allCategories, boolean print,boolean printTime){
        EntityTransaction transaction = null;
        Long groupName = new Date().getTime();

        try {
            // print start message
            if (print) {  // print message?
                if (printTime) {
                    System.out.print("> " + (new Date()).toString() + ": ");
                }
                System.out.println("Neue Transaktion (" + playerCount + " Spieler erstellen ...)");
            }

            // Start Database transaction
            transaction = entityManager.getTransaction();
            transaction.begin();

            // -----------------------

            for (int i = 0; i < playerCount; ++i){
                // create player
                Player player = new Player();
                entityManager.persist(player);

                // set name
                player.setName("player" + groupName + "_" + i);

                // play games
                for (int j = 0; j < gamesCount; ++j){
                    genGame(player, allCategories);
                }

            }
            // -----------------------

            // commit changes
            if (print) {  // print message?
                if (printTime) {
                    System.out.print("> " + (new Date()).toString() + ": ");
                }
                System.out.println("Erstellung abgeschlossen; Transaktion abschliessen ...");
            }
            transaction.commit();

            // clear ram
            if (print) {  // print message?
                if (printTime) {
                    System.out.print("> " + (new Date()).toString() + ": ");
                }
                System.out.println("Transaktion abgeschlossen; Speicher leeren ...");
            }
            entityManager.clear();

        } catch (RuntimeException e){
            // Rollback changes
            if (transaction != null && transaction.isActive()){
                transaction.rollback();
            }

            throw new Error("Could not commit group of players and games.");
        }
    }

    private void genGame(Player player, List<Category> allCategories) {
        // create game
        Game game = new Game();

        // persist game
        // TODO(ruben): persist at the end = more performace??
        entityManager.persist(game);

        // game settings
        game.setPlayer(player);
        player.getGames().add(game);

        // count questions = 1 - 10
        //int questCount = (int) (Math.random() * 10) + 1;
        int questCount = random.nextInt(10) + 1;  // 1 - 10
        game.setMaxQuestions(questCount);

        // choose how may categories (random) 2-5
        //int categoriesCount = (int) (Math.random() * 4) + 2;
        int categoriesCount = random.nextInt(4) + 2;  // 2 - 5
        List<Category> gameCategories = new ArrayList<>();

        // get random categories
        // TODO(ruben): make faster
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
        //int daysToAdd = (int) (Math.random() * 100); // 0 - 99
        int daysToAdd = random.nextInt(100); // 0 - 99
        Date gameStartDate = new Date();
        gameStartDate = addToDate(gameStartDate, daysToAdd, 0, 0, 0);
        game.setStartDatetime(gameStartDate);

        // play game
        simulateGameplay(game, random);

        // add end date =  start date + x
        Date endDate = new Date();
        endDate = addToDate(endDate, daysToAdd, 0, 0, 0);

        game.setEndDatetime(endDate);
    }

    private void simulateGameplay(Game game, Random random) {
        Question currentQuestion = null;
        int questCount = 0;
        int correctAnwers = 0;

        do {
            // get questions / make guess / count right answers
            // TODO(ruben): make question selection faster
            //System.out.println(">> GetQuestion - Start - " + (new Date()).getTime());
            currentQuestion = GameController.getRandomQuestion(game);
            //System.out.println(">> GetQuestion - End - " + (new Date()).getTime());

            if (currentQuestion == null) {
                // no more questions for the chosen categories
                break;
            }

            //int selectedAnswer = (int) (Math.random() * 4) + 1; // 1 - 4
            int selectedAnswer = random.nextInt(4) + 1;  // 1 - 4

            System.out.println(">> addQuestionAnswer - Start - " + (new Date()).getTime());
            boolean correct = addQuestionAnswer(game, currentQuestion,
                selectedAnswer, entityManager);
            System.out.println(">> addQuestionAnswer - End - " + (new Date()).getTime());

            // save count of right answers in game
            if (correct) {
                ++correctAnwers;
            }
            // TODO(ruben): save count of correct questions in game

            ++questCount;
        } while (questCount < game.getMaxQuestions());
    }

    public static boolean addQuestionAnswer(Game game, Question question,
        int chosenAnswer, EntityManager entityManager){
        // TODO(ruben): mave to game controller
        // TODO(ruben): persist at the end = more performace??

        // get return value
        boolean answerCorrect = (question.getCorrectAnswer() == chosenAnswer);

        // create & add to persist
        QuestionAsked newQuestAnswer = new QuestionAsked();
        //entityManager.persist(newQuestAnswer);  // Now at the end

        // modify
        newQuestAnswer.setSelectedAnswer(chosenAnswer);

        // - add question / bidirectional
        //entityManager.persist(question);  // takes to much time
        newQuestAnswer.setQuestion(question);
        question.getAsked().add(newQuestAnswer);  // takes to much time

        // - add game / bidirectional
        //entityManager.persist(game); // takes to much time
        newQuestAnswer.setGame(game);
        game.getAskesQuestions().add(newQuestAnswer);  // takes to much time

        // persist finished entity
        entityManager.persist(newQuestAnswer);

        return answerCorrect;
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
