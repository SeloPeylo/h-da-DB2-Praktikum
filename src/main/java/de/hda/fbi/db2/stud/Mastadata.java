package de.hda.fbi.db2.stud;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import de.hda.fbi.db2.stud.controller.CategoryController;
import de.hda.fbi.db2.tools.CsvDataReader;


/**
 * Masterdata class.
 *
 * @author Ruben van Laack
 */
public class Mastadata {

    private EntityManager entityManager;

    public Mastadata(EntityManagerFactory emf){
        entityManager = emf.createEntityManager();
    }

    public void close() {
        entityManager.close();
    }

    public void clearMasterdata(){
        EntityTransaction transaction = null;
        String deleteQuery = null;

        try {
            // Start Database transaction
            transaction = entityManager.getTransaction();
            transaction.begin();

            // - Delete table contents -
            // delete questions
            String questionsDeleteQuery = ("delete from Question q");
            entityManager.createQuery(questionsDeleteQuery).executeUpdate();

            // delete categories
            String controllerDeleteQuery = ("delete from Category c");
            entityManager.createQuery(controllerDeleteQuery).executeUpdate();
            // --

            // commit changes
            transaction.commit();

        } catch (RuntimeException e){
            // Rollback changes
            if (transaction != null && transaction.isActive()){
                transaction.rollback();
            }

            throw new Error("Could not delete table contents.");
        }
    }

    public void clearGamedata(){
        EntityTransaction transaction = null;
        String deleteQuery = null;

        try {
            // Start Database transaction
            transaction = entityManager.getTransaction();
            transaction.begin();

            // - Delete table contents -
            // delete question_asked
            deleteQuery = ("delete from QuestionAsked");
            entityManager.createQuery(deleteQuery).executeUpdate();

            // delete game
            deleteQuery = ("delete from Game");
            entityManager.createQuery(deleteQuery).executeUpdate();

            // delete player
            // delete game
            deleteQuery = ("delete from Player");
            entityManager.createQuery(deleteQuery).executeUpdate();
            // --

            // commit changes
            transaction.commit();

        } catch (RuntimeException e){
            // Rollback changes
            if (transaction != null && transaction.isActive()){
                transaction.rollback();
            }

            throw new Error("Could not delete table contents.");
        }
    }

    public boolean readToDB(){
        boolean successful = false;
        List<String[]> defaultCsvLines = null;
        List<String> availableFiles = null;


        try {
            //Read default csv
            defaultCsvLines = CsvDataReader.read();

            /*
            //Read (if available) additional csv-files and default csv-file
            availableFiles = CsvDataReader.getAvailableFiles();
            for (String availableFile : availableFiles) {
                final List<String[]> additionalCsvLines = CsvDataReader.read(availableFile);
            }
             */

        } catch (URISyntaxException use) {
            System.out.println(use);
        } catch (IOException ioe) {
            System.out.println(ioe);
        }

        if (defaultCsvLines != null){
            // Create Entities ?
            EntityTransaction transaction = null;
            CategoryController catCon = null;
            try {
                // Start Database transaction
                transaction = entityManager.getTransaction();
                transaction.begin();

                // Create categories & questions and add to persis
                catCon = new CategoryController();
                catCon.build(defaultCsvLines, entityManager);

                // commit changes
                transaction.commit();

                //Print categories and total count of questions
                if (catCon != null){
                    System.out.println(catCon.toString());
                    System.out.println("Total of: " + catCon.getQuestions().size()
                        + " questions in " + catCon.getCategories().size()
                        + " categories created.");
                }


                successful = true;
            } catch (RuntimeException e){
                // Rollback changes
                if (transaction != null && transaction.isActive()){
                    transaction.rollback();
                }

            }
        }

        return successful;
    }

    public boolean checkGameplayTables(){
        boolean game = tableExists("select c from Game c");
        boolean player = tableExists("select c from Player c");
        boolean questA = tableExists("select c from QuestionAsked c");

        if (!game){
            System.out.println("Tabelle 'Game' exisitiert nicht!");
        }

        if (!player){
            System.out.println("Tabelle 'Player' exisitiert nicht!");
        }

        if (!questA){
            System.out.println("Tabelle 'QuestionAsked' exisitiert nicht!");
        }

        // check Game & Player & QuestionAsked
        if (game && player && questA){
            // all tables exist
            return true;

        } else {
            // one or more tables dont exist
            return false;
        }
    }

    public boolean checkMasterdataTables(){
        // check Category & Question
        boolean cat = tableExists("select c from Category c");
        boolean quest = tableExists("select t from Question t");

        if (!cat){
            System.out.println("Tabelle 'Category' exisitiert nicht!");
        }

        if (!quest){
            System.out.println("Tabelle 'Question' exisitiert nicht!");
        }

        if (cat && quest){
            // all tables exist
            return true;

        } else {
            // one or more tables dont exist
            return false;
        }
    }

    /*
    public void createGameplayTables(){

    }

    public void createMasterdataTables(){

    }
    */

    // private methods
    private boolean tableExists(String sql){
        try {
            // create query for table
            //String sql = ("select t from " + table + " t");
            Query query = entityManager.createQuery(sql);
            query.setFirstResult(0);
            query.setMaxResults(1);

            // test query - try to access table
            // Object obj = query.getSingleResult();
            query.getSingleResult();

            //System.out.println("Tabelle '" + table.name() + "' exisitiert nicht!");

            return true;

        } catch (NoResultException e){
            // table exists, but no entries
            return true;

        } catch (PersistenceException e){
            // Table does not exist
            //System.out.println("Error: " + e.toString());
            return false;
        }

    }

}
