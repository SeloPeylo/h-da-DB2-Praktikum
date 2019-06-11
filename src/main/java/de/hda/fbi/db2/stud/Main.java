package de.hda.fbi.db2.stud;

import java.util.Scanner;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import de.hda.fbi.db2.stud.controller.GameController;





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
        Mastadata md = new Mastadata(emf);

        // Check if all master data tables exist in db
        boolean tablesExist = true;
        if (!md.checkMasterdataTables()){
            System.err.println("Die Stammdatentabellen wurden nicht gefunden!");
            System.err.println("Bitte erstellen sie die Tabellen und "
                + "starten sie das Programm erneut.");

            tablesExist = false;

        }

        // check if gameplay tables exist
        if (!md.checkGameplayTables()){
            System.err.println("Die Spieldatentabellen wurden nicht gefunden!");
            System.err.println("Bitte erstellen sie die Tabellen und "
                + "starten sie das Programm erneut.");

            tablesExist = false;
        }

        if (!tablesExist){
            System.exit(0);
        }


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
                // clear master data
                System.out.println("Stammdaten löschen ...");
                md.clearMasterdata();

                // clear gamedata if it exists
                System.out.println("Spieldaten löschen ...");
                md.clearGamedata();

                System.out.println("Lesen der csv-Datei ...");
                boolean error = !md.readToDB();
                if (error){
                    System.out.println("Fehler beim Einlesen & Speichern der Stammdaten!");
                }

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
        md.close();
        emf.close();
    }

    public String getGreeting() {
        return "app should have a greeting";
    }
}
