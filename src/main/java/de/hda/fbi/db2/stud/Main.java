package de.hda.fbi.db2.stud;


import java.util.Date;
import java.util.Scanner;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import de.hda.fbi.db2.stud.controller.GameController;
import de.hda.fbi.db2.stud.controller.SimulationController;




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
            // Close EMF
            md.close();
            emf.close();
            System.exit(0);
        }

        int chosenOption = -1;
        do {
            // Menu Options
            System.out.println("--------------------------------------");
            System.out.println("===== Hauptmenu =====");
            System.out.println("0) Stammdaten aus csv-Datei laden");
            System.out.println("1) Wissenstest spielen");
            System.out.println("2) Analyse der Spieldaten");
            System.out.println("3) Simulation von 10.000 Spielern mit je 100 Spielen.");
            System.out.println("4) Programm beenden.");
            System.out.println("--------------------------------");
            System.out.println("Ihre Wahl: ");
            chosenOption = inputScanner.nextInt();  // read int
            System.out.println(); // line break

            //TODO(ruben): check for option 1-3 if at least one category & one question exist!


            switch (chosenOption){
                default:
                    System.out.println("Default");
                    break;
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
                    System.out.println("- Funktion noch nicht implementiert. -");
                    break;

                case 3: // Run Simulation
                    // Simulation Settings
                    int playerCount = 10000;
                    int countOfGames = 100;
                    int commitAfter = 10000;

                    System.out.println("Simulationseinstellungen: ");
                    System.out.println(playerCount + " Spieler");
                    System.out.println("je " + countOfGames + " Spiele");
                    System.out.println("Commit nach " + commitAfter + " Spielern");
                    SimulationController sc =
                        new SimulationController(playerCount, countOfGames, commitAfter, emf);

                    // print info
                    Date startDate = new Date();
                    System.out.println("Simulation von 1.000.000 Spielen (10.000 Spieler).");
                    System.out.println("Startzeit: " + startDate.toString());
                    System.out.println("Start ...");

                    // run simulation
                    sc.runSimulation();

                    // print info - finished & runtime
                    Date endDate = new Date();
                    System.out.println("Simulation abgeschlossen.");
                    System.out.println("Endzeit: " + endDate.toString()
                        + " (Start war: " + startDate.toString() + ")");
                    long runtime = (endDate.getTime() - startDate.getTime());
                    System.out.println("Differenz: " + runtime);
                    long runtimeMinutes = runtime / 60000; // 1s = 1000ms;
                    long runtimeRest = runtime % 60000; // 1s = 1000ms;
                    System.out.println("Differenz in Minuten: " + runtimeMinutes + "," + runtimeRest);

                    sc.close();
                    break;
                case 4: // End Programm
                    System.out.println("Programm wird gestoppt.");
                    break;

            }
        } while (chosenOption != 4);



        // Close EMF
        md.close();
        emf.close();
    }

    public String getGreeting() {
        return "app should have a greeting";
    }
}
