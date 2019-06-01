package de.hda.fbi.db2.stud.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Game class.
 *
 * @author Ruben van Laack
 */
@Entity
//@Table(name = "game")
@Table(name = "game", schema = "master_data_knowledge_test")
public class Game {

    // Vars
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    private Date startDatetime;
    private Date endDatetime;
    private int maxQuestions;

    @ManyToOne
    private Player player;

    @OneToMany(targetEntity = QuestionAsked.class, mappedBy = "game")
    private List<QuestionAsked> askesQuestions;

    @ManyToMany(targetEntity = Category.class, mappedBy = "games")
    private List<Category> categories;


    // default constructor
    public Game() {
        this.startDatetime = null;
        this.endDatetime = null;
        maxQuestions = 0;
        player = null;
        askesQuestions = new ArrayList<>();
        categories = new ArrayList<>();
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
        Game game = (Game) o;
        return id == game.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // toString

    @Override
    public String toString() {
        return "Game{" +
            "id=" + id +
            ", startDatetime=" + startDatetime +
            ", endDatetime=" + endDatetime +
            ", maxQuestions=" + maxQuestions +
            ", player.getId()=" + player.getId() +
            ", askesQuestions.size()=" + askesQuestions.size() +
            ", categories=" + categories +
            '}';
    }

    // Getter & Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getStartDatetime() {
        return (new Date(this.startDatetime.getTime()));
    }

    public void setStartDatetime(Date start) {
        Date copy = new Date(start.getTime());
        this.startDatetime = copy;
    }

    public Date getEndDatetime() {
        return (new Date(this.endDatetime.getTime()));
    }

    public void setEndDatetime(Date end) {
        Date copy = new Date(end.getTime());
        this.endDatetime = copy;
    }

    public int getMaxQuestions() {
        return maxQuestions;
    }

    public void setMaxQuestions(int maxQuestions) {
        this.maxQuestions = maxQuestions;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public List<QuestionAsked> getAskesQuestions() {
        return askesQuestions;
    }

    public void setAskesQuestions(List<QuestionAsked> askesQuestions) {
        this.askesQuestions = askesQuestions;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}

