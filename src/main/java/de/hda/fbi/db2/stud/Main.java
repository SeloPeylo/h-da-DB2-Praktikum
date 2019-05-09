package de.hda.fbi.db2.stud;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
        HashMap<String, Category> categories = new HashMap<>();

        try {
            //Read default csv
            final List<String[]> defaultCsvLines = CsvDataReader.read();

            //Read (if available) additional csv-files and default csv-file
            List<String> availableFiles = CsvDataReader.getAvailableFiles();
            for (String availableFile : availableFiles) {
                final List<String[]> additionalCsvLines = CsvDataReader.read(availableFile);
            }

            // Fill TreeSet with Categories and Questions
            // Remove first Row:
            defaultCsvLines.remove(0); // = ?ID, _frage, _antwort_1, _antwort_2, _antwort_3,
                                            // _antwort_4, _loesung, _kategorie

            // iterate through rows
            System.out.println();
            System.out.println("Print questions when creating and adding them: ");
            for (String[] rowColumns : defaultCsvLines){

                // Create new Question
                int qId = Integer.parseInt(rowColumns[0]);
                int qCorrectAnswer = Integer.parseInt(rowColumns[6]);
                Question newQuestion = new Question(qId , rowColumns[1],
                    rowColumns[2], rowColumns[3],
                    rowColumns[4], rowColumns[5], qCorrectAnswer);

                // Get Category
                String categoryName = rowColumns[7];
                Category categoryForCurrentQuestion = categories.get(categoryName);

                // Category does not exist -> create new
                if (categoryForCurrentQuestion == null){
                    categoryForCurrentQuestion = new Category(categoryName);
                    categories.put(categoryName, categoryForCurrentQuestion);
                }

                // Set Category in for Question
                newQuestion.setCategory(categoryForCurrentQuestion);

                // Print & add Question to Category
                System.out.println(newQuestion.toString());
                categoryForCurrentQuestion.addQuestion(newQuestion);
            }

            //Print categories and total count of questions
            System.out.println();
            System.out.println("Reading done");
            int countOfQuestions = 0;
            for (Category cat : categories.values()){
                System.out.println(cat.toString());
                countOfQuestions += cat.getQuestions().size();
            }

            System.out.println("Total of: " + countOfQuestions + " questions in " +
                categories.size() + " categories created.");



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
