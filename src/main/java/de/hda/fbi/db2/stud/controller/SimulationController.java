package de.hda.fbi.db2.stud.controller;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
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
    private CategoryController categoryController = null;
    private Random random;

    private int countPlayer;
    private int countGamesEach;
    private int batchSize;

    public static boolean multithreadedSimulation(final int threads, final int totalPlayer,
        final int gamesPerPlayer, final int batchSize, final EntityManagerFactory emf){

        // check parameter
        if (threads <= 0) {
            throw new Error("Error: 0 threads is not an option!");
        }
        if ((totalPlayer % threads) != 0) {
            throw new Error("Error players have to be dividable equally to all threads.");
        }

        // Variables
        final int playerPerThread = totalPlayer / threads;  // e.g. 10.000 / 4 = 2.500
        boolean finishedSuccessfull = true;
        List<Thread> simulationThreads = new ArrayList<>();

        // create threads
        for (int i = 0; i < threads; ++i) {

            // create thread
            Thread simulationThread = new Thread("Simulation " + i){
                public void run(){
                    // print
                    System.out.println("Start des Simulationsthead: " + this.getName() +
                        " (" + playerPerThread + " Spieler mit je " + gamesPerPlayer
                            + " Spielen und " +
                        "'flush' nach: " + batchSize + " Entities)"
                        );

                    // create new simulation
                    SimulationController sc =
                        new SimulationController(playerPerThread, gamesPerPlayer, batchSize, emf);

                    // run simulation
                    Date start = new Date();
                    sc.runSimulation();
                    Date end = new Date();

                    // calc simulation time
                    long simtime = end.getTime() - start.getTime();

                    // close
                    sc.close();

                    // print
                    System.out.println("Simulationsthead: " + this.getName() +
                        " nach " + (simtime / 60000) + "," + (simtime % 60000) +
                        " min abgeschlossen.");
                }
            };

            // start thread
            simulationThread.start();

            // add to list
            simulationThreads.add(simulationThread);
        }

        // block calling thread until all simulations are finished
        for (Thread currSimulationThread : simulationThreads) {

            try {
                currSimulationThread.join();

            } catch (InterruptedException e) {
                System.out.println("InterruptedException while joining "
                    + currSimulationThread.getName());
                e.printStackTrace();
                finishedSuccessfull = false;
            }
        }

        return finishedSuccessfull;
    }

    public SimulationController(final int countPlayer, final int countGamesEach,
        final int batchSize, final EntityManagerFactory emf) {

        this.entityManager = emf.createEntityManager();
        random = new Random();

        // simulation settings
        this.countPlayer = countPlayer;
        this.countGamesEach = countGamesEach;
        this.batchSize = batchSize;

        // load list of possible categories
        categoryController = new CategoryController();
        categoryController.load(this.entityManager);
    }

    public void close() {
        entityManager.close();
    }

    public void runSimulation() {
        final int playerCount = this.countPlayer;
        final int gamesCount = this.countGamesEach;
        final int batchSize = this.batchSize;
        final Long simulationName = new Date().getTime();

        // Get all Categories
        List<Category> allCategories = categoryController.getCategories();
        entityManager.clear();  // remove cache from loading

        // create entities & persist then commit
        EntityTransaction transaction = null;
        try {
            // Start Database transaction
            transaction = entityManager.getTransaction();
            transaction.begin();

            // ---- create, persist, flush and clear ----
            // for each player
            int batchCounter = 0;
            for (int p = 0; p < playerCount; ++p) {

                // create player
                Player player = new Player();
                player.setName("player" + simulationName + "_" + p);

                //persist player
                entityManager.persist(player);
                ++batchCounter; // increase for every persist


                // for each game
                for (int g = 0; g < gamesCount; ++g){

                    // create game
                    Game game = genGame(player, allCategories);

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

        } catch (RuntimeException e){
            e.printStackTrace();

            // Rollback changes
            if (transaction != null && transaction.isActive()){
                transaction.rollback();
            }

            throw new Error("Error in Simulation or Commit");
        }
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
        */

        // add end date to game = start date + x
        int hoursToAdd = random.nextInt(24); // 0 - 23
        Date endDate = addToDate(gameStartDate, 0, hoursToAdd, 0, 0);
        game.setEndDatetime(endDate);

        // return game obj.
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

    public static Date addToDate(final Date date, int days, int hours, int minutes, int seconds) {
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
