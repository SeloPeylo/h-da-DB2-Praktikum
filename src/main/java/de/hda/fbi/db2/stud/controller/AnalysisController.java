package de.hda.fbi.db2.stud.controller;

import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import de.hda.fbi.db2.stud.controller.queryresultclasses.GameEvaluation;
import de.hda.fbi.db2.stud.entity.Category;
import de.hda.fbi.db2.stud.entity.Player;


// TODO(ruben): move all to saved queries once they work correctly
// Should improve the performance on the database

/**
 * AnalysisController class.
 *
 * @author Ruben van Laack
 */
public class AnalysisController {

    private EntityManager entityManager;



    public AnalysisController(EntityManagerFactory emf){
        entityManager = emf.createEntityManager();
    }

    public void close(){
        entityManager.close();
    }

    public List<Player> playedBetween(Date startDate, Date endDate){  // Practice 5 - JPQL Query 1
        entityManager.clear();  // make space for query
        List<Player> players = null;

        try {
            // Get players query
            // TODO(ruben): change to saved query!
            // TODO(ruben): use result class with start & enddate to reduce loading when printing
            String jpqlText = (
                "select p from Player p "
                        + "inner join Game g on g.player = p "
                    + "where g.startDatetime >= :startdate and g.endDatetime >= :enddate "
                    + "group by p "
                    + "order by MAX(g.startDatetime) DESC, MAX(g.endDatetime) DESC"
            );

            // run query
            Query playerQuery = entityManager.createQuery(jpqlText);
            playerQuery.setParameter("startdate", startDate);
            playerQuery.setParameter("enddate", endDate);
            players = playerQuery.getResultList();

            return players;

        } catch (NoResultException e){
            // No players fround
            return null;
        }
    }

    public List<GameEvaluation> gameResultsOfPlayer(int playerId){  // Practice 5 - JPQL Query 2
        entityManager.clear();  // make space for query
        List<GameEvaluation> queryResults = null;

        try {
            // Get players query
            // TODO(ruben): change to saved query!
            String jpqlText = (
                "select new de.hda.fbi.db2.stud.controller.queryresultclasses."
                    + "GameEvaluation(g , count(q), "
                    + "sum(case when qa.selectedAnswer = q.correctAnswer then 1 else 0 end)) "
                    + "from Game g "
                        + "inner join QuestionAsked qa on qa.game = g "
                        + "inner join Question q on qa.question = q "
                    + "where g.player.id = :playerId "
                    + "group by g "
                    + "order by g.id asc"
            );

            // run query
            Query gameQuery = entityManager.createQuery(jpqlText);
            gameQuery.setParameter("playerId", playerId);
            queryResults = gameQuery.getResultList();

            return queryResults;

        } catch (NoResultException e){
            // No games fround
            return null;
        }
    }

    public List<Player> numberofGamesPerPlayer(){  // Practice 5 - JPQL Query 3
        entityManager.clear();  // make space for query
        List<Player> queryResults = null;

        try {
            // Get players query
            // TODO(ruben): change to saved query!
            String jpqlText = (
                "select p "
                + "from Player p "
                + "inner join Game g on g.player = p "
                + "group by p "
                + "order by COUNT(g) DESC"
            );
            /*
            "select p "
                    + "from Player p "
                        + "inner join Game g on g.player = p "
                    + "group by p "
                    + "order by COUNT(g) DESC"
             */

            // run query
            Query playerGamesQuery = entityManager.createQuery(jpqlText);
            queryResults = playerGamesQuery.getResultList();

            return queryResults;

        } catch (NoResultException e){
            // No games fround
            return null;
        }
    }

    public Category categorieUsage(){  // Practice 5 - JPQL Query 4
        entityManager.clear();  // make space for query
        //List<Category> queryResults = null;
        Category result;

        try {
            // Get players query
            // TODO(ruben): change to saved query!
            String jpqlText = (
                "select c "
                    + "from Category c "
                        + "inner join c.games g "
                    + "group by c.id "
                    + "order by count(g.id)"
            );

            // run query
            Query categoriesQuery = entityManager.createQuery(jpqlText);
            //categoriesQuery.setMaxResults(1);
            //queryResults = categoriesQuery.getResultList();
            result = (Category) categoriesQuery.getSingleResult();

            return result;

        } catch (NoResultException e){
            // No games fround
            return null;
        }
    }

}
