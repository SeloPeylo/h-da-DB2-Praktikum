package de.hda.fbi.db2.stud;

import java.io.Console;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import de.hda.fbi.db2.stud.controller.CategoryController;
import de.hda.fbi.db2.stud.controller.GameController;
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

    /**
     * Main Method and Entry-Point.
     *
     * @param args Command-Line Arguments.
     */
    public static void main(String[] args) {

        // Create EMF
        emf = Persistence.createEntityManagerFactory("postgresPU");
        Scanner inputScanner = new Scanner(System.in, "utf-8");

        System.out.println("- Main Start -");


        // Menu Options
        System.out.println("0) Stammdaten aus csv-Datei laden");
        System.out.println("1) Wissenstest spielen");
        System.out.println("2) Analyse der Spieldaten");
        System.out.println("--------------------------------");
        System.out.println("Ihre Wahl: ");
        int chosenOption = inputScanner.nextInt();  // read int
        System.out.println(); // line break

        switch (chosenOption){
            case 0: // Read file & create master data
                Mastadata md = new Mastadata(emf);
                md.clearDB();
                boolean error = !md.readToDB();
                if (error){
                    System.out.println("Fehler beim Einlesen & Speichern der Stammdaten!");
                }
                md.close();
                break;

            case 1: // Play Game
                // Create game play view and start
                GameController gameController = new GameController(emf);
                Gameplay gp = new Gameplay(gameController);
                gp.start();
                gameController.close();  // closes the EntityManager
                break;

            case 2: // Analyze Game data
                break;
        }

        // Close EMF
        emf.close();
    }

    public String getGreeting() {
        return "app should have a greeting";
    }
}
