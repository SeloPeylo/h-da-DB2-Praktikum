package de.hda.fbi.db2.stud;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
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

    public void clearDB(){
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

}
