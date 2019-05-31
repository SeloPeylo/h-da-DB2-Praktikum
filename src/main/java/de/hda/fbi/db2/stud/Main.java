package de.hda.fbi.db2.stud;

import de.hda.fbi.db2.stud.controller.GameController;
import java.io.Console;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import de.hda.fbi.db2.stud.controller.CategoryController;
import de.hda.fbi.db2.stud.entity.Category;
import de.hda.fbi.db2.stud.entity.Question;
import de.hda.fbi.db2.tools.CsvDataReader;


/**
 * Main Class.
 *
 * @author A. Hofmann
 * @author B.-A. Mokroß
 * @version 0.1.1
 * @since 0.1.0
 */
public class Main {

    //DB - Entity Manager
    private static EntityManagerFactory emf;
    private static EntityManager em;

    /**
     * Main Method and Entry-Point.
     *
     * @param args Command-Line Arguments.
     */
    public static void main(String[] args) {

        // Create EMF
        emf = Persistence.createEntityManagerFactory("postgresPU");

        System.out.println("- Main Start -");

        // Menu Options
        System.out.println("0) Stammdaten aus csv-Datei laden");
        System.out.println("1) Wissenstest spielen");
        System.out.println("2) Analyse der Spieldaten");
        System.out.println("--------------------------------");
        System.out.print("Ihre Wahl: ");
        //String enterValue = System.console().readLine();
        int chosenOption = 2; //Integer.parseInt(enterValue);

        switch (chosenOption){
            case 0: // Read file & create master data
                try {
                    // Get DB Entitiy Manager
                    em = emf.createEntityManager();

                    //Read default csv
                    final List<String[]> defaultCsvLines = CsvDataReader.read();

                    //Read (if available) additional csv-files and default csv-file
                    List<String> availableFiles = CsvDataReader.getAvailableFiles();
                    for (String availableFile : availableFiles) {
                        final List<String[]> additionalCsvLines = CsvDataReader.read(availableFile);
                    }

                    // TODO: Delete old master data??

                    // Create Entities ?
                    EntityTransaction transaction = null;
                    CategoryController catCon = null;
                    try {
                        // Start Database transaction
                        transaction = em.getTransaction();
                        transaction.begin();

                        // Create categories & questions and add to persis
                        catCon = new CategoryController();
                        catCon.build(defaultCsvLines, em);

                        // commit changes
                        transaction.commit();

                    } catch (RuntimeException e){
                        // Rollback changes
                        if (transaction != null && transaction.isActive()){
                            transaction.rollback();
                        }

                    } finally {
                        // Close Entity Manager
                        em.close();
                    }

                    //Print categories and total count of questions
                    if (catCon != null){
                        System.out.println(catCon.toString());
                        System.out.println("Total of: " + catCon.getQuestions().size() + " questions in " +
                            catCon.getCategories().size() + " categories created.");
                    }

                } catch (URISyntaxException use) {
                    System.out.println(use);
                } catch (IOException ioe) {
                    System.out.println(ioe);
                }
                break;

            case 1: // Play Game
                // Create game play view and start
                GameController gameController = new GameController(emf);
                Gameplay gp = new Gameplay(gameController);
                gp.start();
                break;

            case 2: // Test
                em = emf.createEntityManager();
                CategoryController catCon = new CategoryController();
                catCon.load(em);

                System.out.println(catCon.toString());
                System.out.println("Total of: " + catCon.getQuestions().size() + " questions in " +
                    catCon.getCategories().size() + " categories created.");


                break;
        }

        // Close EMF
        emf.close();
    }

    public String getGreeting() {
        return "app should have a greeting";
    }
}
