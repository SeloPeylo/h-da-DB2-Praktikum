package de.hda.fbi.db2.stud;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import de.hda.fbi.db2.stud.controller.CategoryController;
import de.hda.fbi.db2.stud.entity.Category;
import de.hda.fbi.db2.stud.entity.Question;
import de.hda.fbi.db2.tools.CsvDataReader;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

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
    public static EntityManager em;

    /**
     * Main Method and Entry-Point.
     *
     * @param args Command-Line Arguments.
     */
    public static void main(String[] args) {
        System.out.println("- Main Start -");

        try {
            //Read default csv
            final List<String[]> defaultCsvLines = CsvDataReader.read();

            //Read (if available) additional csv-files and default csv-file
            List<String> availableFiles = CsvDataReader.getAvailableFiles();
            for (String availableFile : availableFiles) {
                final List<String[]> additionalCsvLines = CsvDataReader.read(availableFile);
            }

            // Create categories & questions
            CategoryController catCon = new CategoryController();
            catCon.build(defaultCsvLines);

            //Print categories and total count of questions
            System.out.println(catCon.toString());
            System.out.println("Total of: " + catCon.getQuestions().size() + " questions in " +
                catCon.getCategories().size() + " categories created.");

            // Get DB Entitiy Manager
            emf = Persistence.createEntityManagerFactory("postgresPU");
            em = emf.createEntityManager();

            // Add Questions & Categories to Persistence
            for (Category cat : catCon.getCategories()){
                //em.persist(cat);
            }

            // commit changes
            em.getTransaction().commit();
            em.close();


        } catch (URISyntaxException use) {
            System.out.println(use);
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }

    public String getGreeting() {
        return "app should have a greeting";
    }
}
