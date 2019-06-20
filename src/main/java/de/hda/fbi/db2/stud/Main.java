package de.hda.fbi.db2.stud;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import de.hda.fbi.db2.stud.controller.AnalysisController;
import de.hda.fbi.db2.stud.controller.GameController;
import de.hda.fbi.db2.stud.controller.SimulationController;
import de.hda.fbi.db2.stud.controller.queryresultclasses.GameEvaluation;

import de.hda.fbi.db2.stud.entity.Category;
import de.hda.fbi.db2.stud.entity.Player;





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
                    analyzeGameData(inputScanner);
                    break;

                case 3: // Run Simulation
                    runSimulation();
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

    public static Date fromString(String dateString){ // Format: 2019-06-16 16:55:03
        Date date = null;
        try {

            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMANY);
            date = format.parse(dateString);

        } catch (ParseException e){
            e.printStackTrace();
        }

        return date;
    }

    private static void runSimulation() {
        // Simulation Settings
        final int playerCount = 10000; //2500; //10000;
        final int countOfGames = 100;
        final int batchSize = 500;
        final int threadCount = 4;

        System.out.println("Simulationseinstellungen: ");
        System.out.println(playerCount + " Spieler");
        System.out.println("je " + countOfGames + " Spiele");
        System.out.println("Flush & Clear nach " + batchSize + " Spielern");
        //SimulationController sc =
        // new SimulationController(playerCount, countOfGames, batchSize, emf);

        // print info
        Date startDate = new Date();
        System.out.println("Start ... (Startzeit: " + startDate.toString() + ")");

        // run simulation
        //sc.runSimulation();
        SimulationController.multithreadedSimulation(threadCount,
            playerCount, countOfGames, batchSize, emf);

        // print info - finished & runtime
        Date endDate = new Date();
        System.out.println("Simulation abgeschlossen.");
        System.out.println("Endzeit: " + endDate.toString()
            + " (Start war: " + startDate.toString() + ")");
        long runtime = (endDate.getTime() - startDate.getTime());
        System.out.println("Differenz: " + runtime);
        System.out.println("Differenz in Minuten: " +
            (runtime / 60000) + "," + (runtime % 60000));

        //sc.close();
    }

    private static void analyzeGameData(Scanner inputScanner) {
        int chosenOption = -1;
        AnalysisController anCon = new AnalysisController(emf);
        // TODO(ruben): test all queries with a big dataset

        do {
            // Sub-menu Options
            System.out.println("--------------------------------------");
            System.out.println("===== Menu - Analyse der Spieldaten =====");
            System.out.println("1) Abfrage 1: 'Ausgabe aller Spieler (Spielername), "
                + "die in einem bestimmten Zeitraum gespielt hatten.'");
            System.out.println("2) Abfrage 2: 'Ausgabe zu einem bestimmten Spieler.[...]'");
            System.out.println("3) Abfrage 3: 'Ausgabe aller Spieler mit Anzahl der gespielten "
                + "Spiele, nach Anzahl absteigend geordnet.'");
            System.out.println("4) Abfrage 4: 'Ausgabe der am meisten gefragten Kategorie, "
                + "oder alternativ, die Beliebtheit der Kategorien nach Anzahl der Auswahl "
                + "absteigend sortiert.'");
            System.out.println("5) Zurueck zum Haupmenu");
            System.out.println("--------------------------------------");
            System.out.println("Ihre Wahl: ");
            chosenOption = inputScanner.nextInt();  // read int
            System.out.println(); // line break

            switch (chosenOption) {
                default:
                    break;

                case 1:
                    // get players in date - Parameter
                    System.out.print("Bitte Startdatum eingeben (z.B. '2019-06-16 16:55:03': ");
                    String startDateInput = inputScanner.nextLine();
                    System.out.print("Bitte Enddatum eingeben (z.B. '2019-06-16 16:55:23': ");
                    String endDateInput = inputScanner.nextLine();
                    Date startdate = fromString(startDateInput);
                    Date enddate = fromString(endDateInput);

                    System.out.println("Abfrage ...");
                    List<Player> players = anCon.playedBetween(startdate, enddate);

                    System.out.println("Ausgabe aller Spieler welche zwischen: " +
                        startdate.toString() +
                        " und " + enddate.toString() + " ein Spiel gespielt haben.");

                    int count = 0;
                    for (Player p : players) {
                        System.out.println(p.toString());

                        if ((count % 10) == 0 && count > 9) {
                            if (promptContinue(inputScanner)) {
                                break;
                            }
                        }

                        ++count;
                    }

                    break;

                case 2:
                    // get games of player x
                    System.out.print("Bitte Spieler-ID eingeben: ");
                    int playerId  = inputScanner.nextInt();

                    System.out.println("Abfrage ...");
                    List<GameEvaluation> gameEvaluations = anCon.gameResultsOfPlayer(playerId);

                    System.out.println("Ausgabe aller Spiele von Spieler mit ID "
                        + playerId + ": ");
                    for (GameEvaluation ge : gameEvaluations){
                        System.out.print("Spiel: [id=" + ge.getGame().getId()
                            + ", start=" + ge.getGame().getStartDatetime()
                            + ", ende=" + ge.getGame().getEndDatetime() + "]");
                        System.out.println(" - " + ge.getCountCorrectAnswers() + "/" +
                            ge.getQuestionCount() + " Fragen richtig. ("
                            + ge.percentageRight() + "%)");
                    }
                    break;

                case 3:
                    // get cound of games for each player
                    System.out.println("Abfrage ...");
                    List<Player> playerList = anCon.numberofGamesPerPlayer();

                    System.out.println("Ausgabe der " + playerList.size() +
                        " Spieler und der Anzahl deren Spiele: ");
                    for (int i = 0; i < playerList.size(); ++i) {
                        System.out.println("Spieler: " + playerList.get(i).toString());

                        if ((i % 10) == 0 && i > 9) {
                            if (promptContinue(inputScanner)) {
                                break;
                            }
                        }
                    }
                    break;

                case 4:
                    // get cound of games for each player
                    System.out.println("Abfrage ...");
                    Category cat = anCon.categorieUsage();

                    System.out.println("Ausgabe der meistgewählten Kategorie: ");
                    System.out.println(cat.info());

                    break;
            }

        } while (chosenOption != 5);

        // free resources
        anCon.close();
    }

    private static boolean promptContinue(Scanner inputScanner) {
        System.out.println("Abbrechen (J/Ja): ");
        String input = inputScanner.nextLine();

        if (input.equals("J") || input.equals("j") ||
            input.equals("ja") || input.equals("Ja")) {
            return true;
        }

        return false;
    }
}
