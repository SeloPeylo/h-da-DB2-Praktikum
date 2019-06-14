package de.hda.fbi.db2.stud;


import java.util.Random;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import de.hda.fbi.db2.stud.entity.Game;
import de.hda.fbi.db2.stud.entity.Player;

/**
 * GameSimulator Class.
 *
 * @author Ruben van Laack
 * @author ##
 */
public class GameSimulator {

    private EntityManager entityManager;
    private int countPlayer;
    private int countGamesEach;
    private static final int commitSize = 100;


    public GameSimulator(int countPlayer, int countGamesEach, EntityManagerFactory emf) {
        this.countPlayer = countPlayer;
        this.countGamesEach = countGamesEach;
        this.entityManager = emf.createEntityManager();
    }

    public void close(){
        entityManager.close();
    }

    public void runSimulation() {
        EntityTransaction transaction = null;

        try {
            // Start Database transaction
            transaction = entityManager.getTransaction();
            transaction.begin();

            // -----------------------

            for (int i = 0; i < countPlayer; ++i){
                // create player
                Player player = new Player();
                entityManager.persist(player);

                // set name
                player.setName("player" + i);

                // play games
                for (int j = 0; j < countGamesEach; ++j){
                    genGame(player);
                }

            }
            // -----------------------

            // commit changes
            transaction.commit();

        } catch (RuntimeException e){
            // Rollback changes
            if (transaction != null && transaction.isActive()){
                transaction.rollback();
            }

            throw new Error("Could not create new player.");
        }
    }

    private void genGame(Player player) {
        Random random = new Random();
        // create game
        Game game = new Game();

        // persist game
        entityManager.persist(game);

        // count questions = 1 - 10
        //int questCount = (int) (Math.random() * 10) + 1;
        int questCount = random.nextInt(10) + 1;
        game.setMaxQuestions(questCount);

        // choose Categories count? 2-5?



        // get start date -> Random
        // play game
        // add end date =  start date + x
    }

    private void simulateGameplay(Game game) {
        // get questions / make guess / count right answers
        // save count of right answers in game
        // save game
    }

}
