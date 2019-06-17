package de.hda.fbi.db2.stud;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import de.hda.fbi.db2.stud.controller.GameController;
import de.hda.fbi.db2.stud.entity.Category;
import de.hda.fbi.db2.stud.entity.Game;
import de.hda.fbi.db2.stud.entity.Player;
import de.hda.fbi.db2.stud.entity.Question;


/**
 * Gameplay class.
 *
 * @author Ruben van Laack
 */
public class Gameplay {

    private GameController gameCon;

    Gameplay(GameController gameCon){
        this.gameCon = gameCon;
    }

    public void start(){
        // get start date
        Date gameStartDate = new Date();

        System.out.println(" - Start des Spiels -");

        // create new game
        Game game = createNewGame(gameStartDate);

        // start game play
        HashSet<Integer> usedQuestionIds = new HashSet<>();  // cache for used questions (only ids)
        int[] score = playGame(game, usedQuestionIds);

        // get end date
        Date gameEndDate = new Date();
        gameCon.setGameEndDate(game, gameEndDate);

        // End of Game
        System.out.println("Ergebnis: " + score[0] + " von " + score[1] + " Antworten richtig.");
    }

    private Game createNewGame(Date gameStartDate){
        Scanner inputScanner = new Scanner(System.in, "utf-8");

        // Ask for player name
        System.out.println("Spielername: ");
        //System.out.flush(); // show pring before starting input
        String playerName = inputScanner.nextLine().replace(" ", "");
        System.out.println();

        // Get or create player
        Player player = gameCon.getPlayer(playerName);
        if (player == null){
            player = gameCon.createPlayer(playerName);
            System.out.println("Neuer Spieler erstellt: " + player.toString());
        } else {
            System.out.println("Spieler gefunden: " + player.toString());
        }

        // Show possible categories
        System.out.println("");
        System.out.println("Alle bekannten Kategorien: ");
        System.out.println("-----------------------------");
        HashMap<String, Category> categories = gameCon.getAllCategories();
        for (Category cat : categories.values()){
            System.out.println(cat.getName());
        }
        System.out.println("-----------------------------");


        // Ask for game categories
        System.out.println("Kategorien? (z.B. \"Wintersport, TV\": ");
        String[] chosenCategories = inputScanner.nextLine().split(", ");
        ArrayList<Category> gameCategories = new ArrayList<>();
        for (String chosenCatName : chosenCategories){
            Category gameCategory = categories.get(chosenCatName);

            if (gameCategory != null) {
                gameCategories.add(gameCategory);
            }
        }
        System.out.println(); // line break

        // Ask for max questions for this game
        System.out.println("Maximale Anzahl an Fragen: ");
        int maxQuestCount = inputScanner.nextInt();
        System.out.println();

        // Create new Game
        Game game = gameCon.createGame(player, gameCategories, maxQuestCount, gameStartDate);

        // print game
        System.out.println("Spiel erstellt: " + game.toString());

        // return game
        return game;
    }

    private int[] playGame(Game game, HashSet<Integer> usedQuestionIds){
        Scanner inputScanner = new Scanner(System.in, "utf-8");

        // while game not done
        int correctAnswerdQuestions = 0;
        int questCount = 0;
        for (; questCount < game.getMaxQuestions(); ++questCount){

            // get next question
            Question nextQuestion = GameController.getRandomQuestion(game, usedQuestionIds);

            if (nextQuestion == null){
                System.out.println("Keine neuen Fragen mehr vorhanden.");
                break;
            }

            // cache id of current question
            usedQuestionIds.add(nextQuestion.getId());

            // print questions
            System.out.println();
            System.out.println("Question " + questCount + ": " + nextQuestion.getQuestionText());
            System.out.println("--- Antworten ---");
            System.out.println("1) " + nextQuestion.getAnswers1());
            System.out.println("2) " + nextQuestion.getAnswers2());
            System.out.println("3) " + nextQuestion.getAnswers3());
            System.out.println("4) " + nextQuestion.getAnswers4());

            // get answer
            // --while syntax wrong
            String chosenAnswerText;
            do {
                System.out.println("Ihre Antwort (1 | 2 | 3 | 4): ");
                chosenAnswerText = inputScanner.nextLine();
            } while (// too long or wrong char
                chosenAnswerText.length() > 1 ||
                    !(
                        chosenAnswerText.contains("1") || chosenAnswerText.contains("2") ||
                            chosenAnswerText.contains("3") || chosenAnswerText.contains("4")
                    )
            );
            int chosenAnswer = Integer.parseInt(chosenAnswerText);
            System.out.println(); // line break

            // validate answer & store
            boolean answerCorrect = gameCon.addQuestionAnswer(game, nextQuestion, chosenAnswer);

            // print question evaluation
            if (answerCorrect) {
                System.out.println("=> Richtige Antwort!");
                ++correctAnswerdQuestions;
            } else {
                System.out.println("=> Falsche Antwort!");
            }

            // stop game
            System.out.println("Spiel beenden (j/nein oder <Enter>): ");
            String stopGame = inputScanner.nextLine().replace(" ", "");
            if (stopGame.equals("Ja") || stopGame.equals("J") || stopGame.equals("j")
                || stopGame.equals("JA")){
                break;
            }
        }

        // End of Gameplay / return score
        System.out.println("Spiel beendet");
        int[] score = {correctAnswerdQuestions, questCount};
        return score;
    }


}
