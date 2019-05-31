package de.hda.fbi.db2.stud;

import de.hda.fbi.db2.stud.controller.CategoryController;
import de.hda.fbi.db2.stud.controller.GameController;
import de.hda.fbi.db2.stud.entity.Category;
import de.hda.fbi.db2.stud.entity.Game;
import de.hda.fbi.db2.stud.entity.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

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
        // create new game
        Game game = createNewGame();

        // start game play
        playGame();
    }

    private Game createNewGame(){


        // Ask for player name
        System.out.print("Spielername: ");
        String playerName = System.console().readLine().replace(" ", "");
        System.out.println();

        // Show possible categories
        System.out.print("Alle bekannten Kategorien: ");
        System.out.print("-----------------------------");
        HashMap<String, Category> categories = gameCon.getCategories();
        for (Category cat : categories.values()){
            System.out.print(cat.getName());
        }
        System.out.print("-----------------------------");


        // Ask for game categories
        System.out.print("Kategorien? (z.B. \"Wintersport, TV\": ");
        String[] chosenCategories = System.console().readLine().split(", ");
        List<Category> gameCategories = new ArrayList<>();
        for (String chosenCatName : chosenCategories){
            Category gameCategory = categories.get(chosenCatName);

            if (gameCategory != null) {
                gameCategories.add(gameCategory);
            }
        }
        System.out.println();

        // Ask for max questions for this game
        System.out.print("Maximale Anzahl an Fragen: ");
        String maxQuestCountText = System.console().readLine();
        int maxQuestCount = Integer.parseInt(maxQuestCountText);
        System.out.println();

        // Get or create player
        Player player = gameCon.getPlayer(playerName);
        if (player == null){
            player = gameCon.createPlayer(playerName);
        }

        // Create new Game
        Game game = gameCon.createGame(player, gameCategories);

        // return game
        return game;
    }

    private void playGame(){

    }


}
