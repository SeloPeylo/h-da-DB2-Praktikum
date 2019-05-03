package de.hda.fbi.db2.stud.entity;

import java.util.List;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
//@Table(name = "Category", schema = "base_date_knowledge_test")
public class Category {
  // Needed Functions: default constructor, getter & setter, toString, @Entity, @Id
  // @override equals(...) & hash

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id;

  private String name;

  @OneToMany(targetEntity = Question.class, mappedBy = "ofCategory")
  private List<Question> questions;



  // Constructor
  public Category(){
    //Will stay Empty??
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
    return id == category.id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  // To String
  @Override
  public String toString() {
    return "Category{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", questions=" + questions +
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

}
