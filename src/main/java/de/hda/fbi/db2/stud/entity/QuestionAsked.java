package de.hda.fbi.db2.stud.entity;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Question_Asked class.
 *
 * @author Ruben van Laack
 */
@Entity
//@Table(name = "questionasked")
@Table(name = "questionasked", schema = "master_data_knowledge_test")
@SequenceGenerator(name = "master_data_knowledge_test.questionasked_id_seq", initialValue = 1,
    allocationSize = 1000000)
public class QuestionAsked {

    // Vars
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
        generator = "master_data_knowledge_test.questionasked_id_seq")
    private int id;
    private int selectedAnswer;
    //private boolean correct;

    @ManyToOne
    private Game game;

    @ManyToOne
    private Question question;



    // Constructor
    public QuestionAsked() {

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
        QuestionAsked that = (QuestionAsked) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // To String

    @Override
    public String toString() {
        return "QuestionAsked{" +
            "id=" + id +
            ", selectedAnswer=" + selectedAnswer +
            ", game=" + game +
            ", question=" + question +
            '}';
    }

    // Getter & Setter

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSelectedAnswer() {
        return selectedAnswer;
    }

    public void setSelectedAnswer(int selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}
