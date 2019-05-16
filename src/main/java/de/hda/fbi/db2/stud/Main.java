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

/**
 * Main Class.
 *
 * @author A. Hofmann
 * @author B.-A. Mokroß
 * @version 0.1.1
 * @since 0.1.0
 */
public class Main {

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
