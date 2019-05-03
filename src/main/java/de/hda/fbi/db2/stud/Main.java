package de.hda.fbi.db2.stud;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import de.hda.fbi.db2.tools.CsvDataReader;

/**
 * Main Class.
 * @version 0.1.1
 * @since 0.1.0
 * @author A. Hofmann
 * @author B.-A. Mokroß
 */
public class Main {
    /**
     * Main Method and Entry-Point.
     * @param args Command-Line Arguments.
     */
    public static void main(String[] args) {
        System.out.println("Hello World 2");

        try {
            //Read default csv
            final List<String[]> defaultCsvLines = CsvDataReader.read();
            
            //Read (if available) additional csv-files and default csv-file
            List<String> availableFiles = CsvDataReader.getAvailableFiles();
            for (String availableFile: availableFiles){
                final List<String[]> additionalCsvLines = CsvDataReader.read(availableFile);
            }
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
