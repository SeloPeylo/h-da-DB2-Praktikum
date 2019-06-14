package de.hda.fbi.db2.stud.controller;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import de.hda.fbi.db2.stud.entity.Category;
import de.hda.fbi.db2.stud.entity.Game;
import de.hda.fbi.db2.stud.entity.Player;
import de.hda.fbi.db2.stud.entity.Question;
import de.hda.fbi.db2.stud.entity.QuestionAsked;

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
    public HashMap<String, Category> getAllCategories() {
        try {
            // Get list of categories
            catCon.load(entityManager);
            HashMap<String, Category> categories = catCon.getHashSet();
            return categories;

        } catch (NoResultException e){
            return null;
        }
    }

    public Player getPlayer(String name){
        try {
            // Get player
            String query = ("select p from Player p where p.name = :name");
            Player player = (Player) entityManager.createQuery(query)
                .setParameter("name", name).getSingleResult();
            return player;

        } catch (NoResultException e){
            return null;
            // no error, player just does not exist
        }
    }

    public Player createPlayer(String playerName){
        EntityTransaction transaction = null;
        Player newPlayer = null;

        try {
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

            throw new Error("Could not create new player.");
        }

        return newPlayer;
    }

    public Game createGame(Player player, List<Category> gameCategories,
        int maxQuestions, Date startDate){

        EntityTransaction transaction = null;
        Game game = null;

        try {
            // Start Database transaction
            transaction = entityManager.getTransaction();
            transaction.begin();

            // Create new game
            game = new Game();
            entityManager.persist(game);
            game.setMaxQuestions(maxQuestions);
            game.setStartDatetime(startDate);

            // set & persist player
            entityManager.persist(player);
            player.getGames().add(game);
            game.setPlayer(player);

            // persist and add gameCategories
            for (Category cat : gameCategories){
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

            throw new Error("Could not create new game.");
        }

        return game;
    }

    public void setGameEndDate(Game game, Date endDate){
        EntityTransaction transaction = null;

        try {
            // Start Database transaction
            transaction = entityManager.getTransaction();
            transaction.begin();

            // persist & change
            entityManager.persist(game);
            game.setEndDatetime(endDate);

            // commit changes
            transaction.commit();

        } catch (RuntimeException e){
            // Rollback changes
            if (transaction != null && transaction.isActive()){
                transaction.rollback();
            }

            throw new Error("Could not change game end date");
        }
    }

    public boolean addQuestionAnswer(Game game, Question question, int chosenAnswer){
        EntityTransaction transaction = null;
        boolean answerCorrect = false;

        try {
            // Start Database transaction
            transaction = entityManager.getTransaction();
            transaction.begin();

            // -- modify database here --
            answerCorrect = SimulationController.addQuestionAnswer(game, question, chosenAnswer, entityManager);

            // -- end modify --

            // commit changes
            transaction.commit();

        } catch (RuntimeException e){
            // Rollback changes
            if (transaction != null && transaction.isActive()){
                transaction.rollback();
            }

            throw new Error("Could not store question answer");
        }

        return answerCorrect;
    }

    // non database methods
    public static Question getRandomQuestion(Game game){
        // get HashSet of used questions
        HashSet<Integer> usedQuestionIds = new HashSet<>();
        for (QuestionAsked qs : game.getAskesQuestions()){
            usedQuestionIds.add(qs.getQuestion().getId());
        }

        // get list of all unused questions
        ArrayList<Question> unusedQuestions = new ArrayList<>();
        for (Category cat : game.getCategories()){
            for (Question que : cat.getQuestions()){

                // question not used
                if (!usedQuestionIds.contains(que.getId())){
                    unusedQuestions.add(que);
                }
            }
        }

        // -- Math.random() = rand ∈ (rand >= 0 && rand < 1)
        // -- (rand * size) = randIndex ∈(randIndex >= 0 && randIndex < size)

        if (unusedQuestions.size() > 0){
            int randomQuestionIndex = (int) (Math.random() * unusedQuestions.size());
            Question randomQuestion = unusedQuestions.get(randomQuestionIndex);

            return randomQuestion;
        } else {
            return null;
        }
    }


}
