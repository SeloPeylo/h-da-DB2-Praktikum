package de.hda.fbi.db2.stud;

import de.hda.fbi.db2.stud.controller.GameController;
import java.util.Date;
import de.hda.fbi.db2.stud.controller.CategoryController;
import de.hda.fbi.db2.stud.entity.Category;
import de.hda.fbi.db2.stud.entity.Game;
import de.hda.fbi.db2.stud.entity.Player;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

public class GameSimulator {

  private int numberOfPlayers;
  private int numberOfGamesPerPlayer;
  private int questionsPerGame = 8;
  private int persistCounter = 0;
  private EntityManager entityManager;
  EntityTransaction transaction;

  private CategoryController catCon;
  private List<Category> gameCategories;

  public GameSimulator(int numberOfPlayers, int numberOfGamesPerPlayer, EntityManagerFactory emf) {
    this.numberOfPlayers = numberOfPlayers;
    this.numberOfGamesPerPlayer = numberOfGamesPerPlayer;
    this.entityManager = emf.createEntityManager();
    this.catCon = new CategoryController();
  }

  public void runSimulation() {

    try {
      transaction = entityManager.getTransaction();
      transaction.begin();

      this.catCon.load(entityManager);
      this.gameCategories = catCon.getCategories();

      // Generates Players and several Games for each Player
      for (int i = 0; i < this.numberOfPlayers; i++) {
        Player player = createPlayer("player" + i);
        System.out.print("Player " + i);
        for (int j = 0; j < this.numberOfGamesPerPlayer; j++) {
          int dayOfMonth = j % 31; //To have Games played on different days
          Game game = createGame(player, dayOfMonth);
          System.out.print("Game " + j);
        }
      }


      transaction.commit();
    } catch (RuntimeException e) {
      // Rollback changes
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }

      throw new Error("Transaction error!");


    }
  }

  private Player createPlayer(String playerName) {

    Player newPlayer = new Player();
    persist(newPlayer);
    newPlayer.setName(playerName);
    return newPlayer;
  }

  private Game createGame(Player player, int dayOfMonth) {
    // count questions
    // choose Categories count? 2-5?
    // get questions / make guess / count right answers
    // save count of right answers in game
    // save game

    Game game = new Game();
    persist(game);

    game.setMaxQuestions(questionsPerGame);

    Date date = new Date();
    date.setDate(dayOfMonth);
    game.setStartDatetime(date);
    game.setEndDatetime(date);

    player.getGames().add(game);
    game.setPlayer(player);

    /*for(int i=0; i<gameCategories.size(); i++)
    {
      game.getCategories().addAll(gameCategories);
    }
    */

    /*
    for (Category cat : gameCategories) {
      //entityManager.refresh(cat);
      game.getCategories().addAll(gameCategories);
    }*/

    return game;
  }

  private void testMassData() {

  }

  private void persist(Object o){
    if(persistCounter == 5000){
      persistCounter = 0;
      entityManager.flush();
      entityManager.clear();
    }
    entityManager.persist(o);
    persistCounter++;
  }

}
