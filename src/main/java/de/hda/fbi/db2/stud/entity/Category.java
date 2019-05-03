package de.hda.fbi.db2.stud.entity;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
//@Table(name = "Category", schema = "base_date_knowledge_test")
public class Category {
  // Needed Functions: default constructor, getter & setter, toString, @Entity, @Id
  // @override equals(...) & hash

  @Id
  private int id;
  private String name;

  // Constructor
  public Category(){

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
}
