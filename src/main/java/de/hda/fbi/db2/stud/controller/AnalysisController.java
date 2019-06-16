package de.hda.fbi.db2.stud.controller;

import de.hda.fbi.db2.stud.entity.Player;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;

public class AnalysisController {

    private EntityManager entityManager;

    public AnalysisController(EntityManagerFactory emf){
        entityManager = emf.createEntityManager();
    }

    public void close(){
        entityManager.close();
    }

    public List<Player> playedBetween(Date startDate, Date endDate){
        List<Player> players = null;
        try {
            // Get players query
            // TODO(ruben): change to saved query!
            String jpql_text = (
                "select p from Player p inner join Game g on g.player = p "
                    + "where g.startDatetime >= :startdate and g.endDatetime >= :enddate "
                    + "group by p "
                    + "order by MAX(g.startDatetime) DESC, MAX(g.endDatetime) DESC"
            );

            // run query
            Query playerQuery = entityManager.createQuery(jpql_text);
            playerQuery.setParameter("startdate", startDate);
            playerQuery.setParameter("enddate", endDate);
            players = playerQuery.getResultList();

            return players;

        } catch (NoResultException e){
            // No players fround
            return null;
        }
    }

}
