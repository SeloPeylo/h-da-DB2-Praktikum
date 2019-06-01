package de.hda.fbi.db2.stud.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Category class.
 *
 * @author Ruben van Laack
 */
@Entity
//@Table(name = "category")
@Table(name = "category", schema = "master_data_knowledge_test")
public class Category {
    // Needed Functions: default constructor, getter & setter, toString, @Entity, @Id
    // @override equals(...) & hash

    @Id
    //@GeneratedValue(strategy = GenerationType.SEQUENCE,
    // - generator = "master_data_knowledge_test.id_cat")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @Column(unique = true)
    private String name;

    @OneToMany(targetEntity = Question.class, mappedBy = "category")
    private List<Question> questions;

    @ManyToMany
    private List<Game> games;


    // Constructor
    public Category() {
        questions = new ArrayList<>();
        games = new ArrayList<>();
    }

    public Category(String name) {
        this.name = name;
        this.questions = new ArrayList<>();
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
        Category category = (Category) o;
        return id == category.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // To String
    @Override
    public String toString() {
        return "Category{" +
            "id=" + id + '\'' +
            ", name='" + name + '\'' +
            ", questions.size()=" + questions.size() + '\'' +
            ", games.size()=" + games.size() +
            '}';
    }

    //Getter & Setter
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

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }

    //other Functions
    public boolean addQuestion(Question newQuest){
        if (!questions.contains(newQuest)){
            questions.add(newQuest);
            return true;
        }
        return false;
    }

}
