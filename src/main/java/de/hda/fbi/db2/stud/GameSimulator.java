package de.hda.fbi.db2.stud;

import java.util.Date;
import java.util.HashMap;
import de.hda.fbi.db2.stud.controller.CategoryController;
import de.hda.fbi.db2.stud.entity.Category;
import de.hda.fbi.db2.stud.entity.Game;
import de.hda.fbi.db2.stud.entity.Player;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

public class GameSimulator {

  private int countPlayer;
  private int countGamesEach;
  private int countPersist = 0;
  private int countPersistBeforeFlush = 1000; //Number of Persists after which to flush
  private EntityManager entityManager;
  private CategoryController catCon;
  HashMap<String, Category> categories = null;

  public GameSimulator(int countPlayer, int countGamesEach, EntityManagerFactory emf) {
    this.countPlayer = countPlayer;
    this.countGamesEach = countGamesEach;
    this.entityManager = emf.createEntityManager();
  }

  public void runSimulation() {
    EntityTransaction transaction = null;

    catCon = new CategoryController();
    catCon.load(entityManager);
    categories = catCon.getHashSet();

    try {
      transaction = entityManager.getTransaction();
      transaction.begin();

      // Generates Players and several Games for each Player
      for (int i = 0; i < this.countPlayer; i++) {
        Player player = createPlayer("player" + i);
        System.out.println("Player "+i);
        for (int j = 0; j < this.countGamesEach; j++) {
          int dayOfMonth = j % 31; //To have Games played on different days
          Game game = createGame(player, dayOfMonth);
          System.out.println("Game "+j);
        }
        entityManager.flush();
        entityManager.clear();
        System.out.println("Flush and Clear!!");
      }

      transaction.commit();

    } catch (RuntimeException e) {
      // Rollback changes
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }

      throw new Error("Could not create new player.");


    }
  }

  private Player createPlayer(String playerName) {

    Player newPlayer = new Player();
    entityManager.persist(newPlayer);
    System.out.println("Player created!");

    newPlayer.setName(playerName);

    this.countPersist++;
    return newPlayer;
  }

  private Game createGame(Player player, int dayOfMonth) {
    // count questions
    // choose Categories count? 2-5?
    // get questions / make guess / count right answers
    // save count of right answers in game
    // save game

    Game game = new Game();
    entityManager.persist(game);
    entityManager.persist(player);
    System.out.println("Game created!");

    int maxQuestions = 5;
    game.setMaxQuestions(maxQuestions);

    Date date = new Date();
    date.setDate(dayOfMonth);
    game.setStartDatetime(date);
    game.setEndDatetime(date);

    player.getGames().add(game);
    game.setPlayer(player);

    /*
    for (Category cat : catego) {
      entityManager.persist(cat);
      cat.getGames().add(game);
    }
    game.getCategories().addAll(gameCategories);
    */
    return game;
  }

}
