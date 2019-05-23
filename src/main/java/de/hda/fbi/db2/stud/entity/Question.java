package de.hda.fbi.db2.stud.entity;

import java.util.List;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Question class.
 *
 * @author Ruben van Laack
 */
@Entity
@Table(name = "question", schema = "master_data_knowledge_test")
public class Question {
    // Needed Functions:
    // default constructor, getter & setter, toString, @Entity, @Id
    // @override equals(...) & hash

    @Id
    private int id;
    private String questionText;
    private String answers1;
    private String answers2;
    private String answers3;
    private String answers4;
    private int correctAnswers;

    @ManyToOne
    private Category category;

    @OneToMany(targetEntity = QuestionAsked.class, mappedBy = "question")
    private List<QuestionAsked> asked;


    // Constructor
    public Question() {
        //Will stay Empty??
    }

    public Question(int id, String questionText, String answers1, String answers2, String answers3,
        String answers4, int correctAnswers) {

        this.id = id;
        this.questionText = questionText;
        this.answers1 = answers1;
        this.answers2 = answers2;
        this.answers3 = answers3;
        this.answers4 = answers4;
        this.correctAnswers = correctAnswers;
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
        Question question = (Question) o;
        return id == question.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    //toString
    @Override
    public String toString() {
        return "Question{" +
            "id=" + id +
            ", questionText='" + questionText + '\'' +
            ", answers1='" + answers1 + '\'' +
            ", answers2='" + answers2 + '\'' +
            ", answers3='" + answers3 + '\'' +
            ", answers4='" + answers4 + '\'' +
            ", correctAnswers=" + correctAnswers +
            ", category=" + category.getName() +
            '}';
    }

    //Getter & Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getAnswers1() {
        return answers1;
    }

    public void setAnswers1(String answers1) {
        this.answers1 = answers1;
    }

    public String getAnswers2() {
        return answers2;
    }

    public void setAnswers2(String answers2) {
        this.answers2 = answers2;
    }

    public String getAnswers3() {
        return answers3;
    }

    public void setAnswers3(String answers3) {
        this.answers3 = answers3;
    }

    public String getAnswers4() {
        return answers4;
    }

    public void setAnswers4(String answers4) {
        this.answers4 = answers4;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
