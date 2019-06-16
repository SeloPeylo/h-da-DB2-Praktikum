package de.hda.fbi.db2.stud;

import de.hda.fbi.db2.stud.controller.AnalysisController;
import de.hda.fbi.db2.stud.entity.Player;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
                AnalysisController anCon = new AnalysisController(emf);
                // TODO(ruben): build menu
                // TODO(ruben): test all queries with a big dataset

                // get players in date
                Date startdate = fromString("2019-06-16 16:55:03");
                Date enddate = fromString("2019-06-16 16:55:23");
                List<Player> players = anCon.playedBetween(startdate, enddate);

                System.out.println("Ausgabe aller Spieler welche zwischen: " +
                    startdate.toString() +
                    " und " + enddate.toString() + " ein Spiel gespielt haben.");
                for (Player p : players) {
                    System.out.println(p.toString());
                }

                anCon.close();
                break;
        }

        // Close EMF
        md.close();
        emf.close();
    }

    public String getGreeting() {
        return "app should have a greeting";
    }

    public static Date fromString(String dateString){ // Format: 2019-06-16 16:55:03
        Date date = null;
        try{

            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMANY);
            date = format.parse(dateString);

        } catch (ParseException e){
            e.printStackTrace();
        }

        return date;
    }
}
