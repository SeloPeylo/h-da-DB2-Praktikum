package de.hda.fbi.db2.stud.controller;

import de.hda.fbi.db2.stud.entity.Category;
import de.hda.fbi.db2.stud.entity.Game;
import de.hda.fbi.db2.stud.entity.Player;
import java.util.HashMap;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;

/**
 * GameController class.
 *
 * @author Ruben van Laack
 */
public class GameController {

    private EntityManager entityManager;
    private CategoryController catCon;

    public GameController(EntityManagerFactory emf) {
        entityManager = emf.createEntityManager();
        catCon = new CategoryController();
    }

    public void close(){
        entityManager.close();
    }


    // Queries
    public HashMap<String, Category> getCategories() {
        try{
            // Get list of categories
            catCon.load(entityManager);
            HashMap<String, Category> categories = catCon.getHashSet();
            return categories;

        } catch (NoResultException e){
            return null;
        }
    }

    public Player getPlayer(String name){
        try{
            // Get player
            String query = ("select p from Player p where p.name = :name");
            Player player = (Player) entityManager.createQuery(query)
                .setParameter("name", name).getSingleResult();
            return player;

        } catch (NoResultException e){
            return null;
        }
    }

    public Player createPlayer(String playerName){
        EntityTransaction transaction = null;
        Player newPlayer = null;

        try{
            // Start Database transaction
            transaction = entityManager.getTransaction();
            transaction.begin();

            // Create new player object
            newPlayer = new Player();

            // add to persist
            entityManager.persist(newPlayer);

            // change the name
            newPlayer.setName(playerName);

            // commit changes
            transaction.commit();

        } catch (RuntimeException e){
            // Rollback changes
            if (transaction != null && transaction.isActive()){
                transaction.rollback();
            }

            newPlayer = null;
        }

        return newPlayer;
    }

    public Game createGame(Player player, List<Category> gameCategories){
        EntityTransaction transaction = null;
        Game game = null;

        try{
            // Start Database transaction
            transaction = entityManager.getTransaction();
            transaction.begin();

            // Create new game
            game = new Game();

            // add to persist
            entityManager.persist(game);

            // set & persist player
            entityManager.persist(player);
            player.getGames().add(game);
            game.setPlayer(player);

            // persist and add gameCategories
            for(Category cat : gameCategories){
                entityManager.persist(cat);
                cat.getGames().add(game);
            }
            game.getCategories().addAll(gameCategories);

            // commit changes
            transaction.commit();

        } catch (RuntimeException e){
            // Rollback changes
            if (transaction != null && transaction.isActive()){
                transaction.rollback();
            }

            // in case it was already created
            game = null;
        }

        return game;
    }



}
