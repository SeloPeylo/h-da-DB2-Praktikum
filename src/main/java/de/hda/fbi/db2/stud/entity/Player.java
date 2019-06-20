package de.hda.fbi.db2.stud.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Player class.
 *
 * @author Ruben van Laack
 */
@Entity
//@Table(name = "player")
@Table(name = "player", schema = "master_data_knowledge_test")
@SequenceGenerator(name = "master_data_knowledge_test.player_id_seq",
    initialValue = 1, allocationSize = 10000)
public class Player {

    // Vars
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
        generator = "master_data_knowledge_test.player_id_seq")
    private int id;

    @Column(unique = true)
    private String name;

    @OneToMany(targetEntity = Game.class, mappedBy = "player")
    private List<Game> games;


    // default constructor
    public Player() {
        games = new ArrayList<>();
    }

    // Equals & Hash
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Player player = (Player) o;
        return id == player.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // To String
    @Override
    public String toString() {
        return "Player{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", games.size()=" + games.size() +
            '}';
    }

    // Getter & Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }
}
