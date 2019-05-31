package de.hda.fbi.db2.stud.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.persistence.EntityManager;
import de.hda.fbi.db2.stud.entity.Category;
import de.hda.fbi.db2.stud.entity.Question;


/**
 * CategoryController class.
 *
 * @author Ruben van Laack
 */
public class CategoryController {
    private HashMap<String, Category> categories;

    public CategoryController(){
        categories = new HashMap<>();
    }

    public CategoryController(HashMap<String, Category> categories){
        this.categories = categories;
    }

    public void build(List<String[]> csvLines, EntityManager entityManager){

        //Start with empty list
        categories.clear();

        // Remove first Row:
        // = ?ID, _frage, _antwort_1, _antwort_2, _antwort_3, _antwort_4, _loesung, _kategorie
        csvLines.remove(0);

        // iterate through rows
        for (String[] rowColumns : csvLines){

            // Create new Question
            int qId = Integer.parseInt(rowColumns[0]);
            int qCorrectAnswer = Integer.parseInt(rowColumns[6]);
            Question newQuestion = new Question(qId , rowColumns[1], rowColumns[2], rowColumns[3],
                rowColumns[4], rowColumns[5], qCorrectAnswer);

            // Persist Question
            entityManager.persist(newQuestion);

            // Get Category
            String categoryName = rowColumns[7];
            Category categoryForCurrentQuestion = categories.get(categoryName);

            // Category does not exist -> create new
            if (categoryForCurrentQuestion == null){
                // Create new Category
                categoryForCurrentQuestion = new Category(categoryName);

                // Persist new Category
                entityManager.persist(categoryForCurrentQuestion);

                // Add new Category to list / HashMap
                categories.put(categoryName, categoryForCurrentQuestion);
            }

            // Set Category in for Question
            newQuestion.setCategory(categoryForCurrentQuestion);

            // Print & add Question to Category
            categoryForCurrentQuestion.addQuestion(newQuestion);
        }
    }

    public void load(EntityManager entityManager){
        // Empty current list
        categories.clear();

        // Load all categories & questions from DB
        List resultL = entityManager.createQuery("select c from Category c order by c.name asc").getResultList();

        // Go through results
        for (Iterator i = resultL.iterator(); i.hasNext();) {

            // Get Category
            Category cat = (Category) i.next();

            // Add Category to hashmap
            categories.put(cat.getName(), cat);
        }
    }

    public List<Category> getCategories(){
        if (!categories.isEmpty()) {
            List<Category> listOfCategories = new ArrayList<>(categories.values());
            return listOfCategories;
        } else {
            return null;
        }
    }

    public List<Question> getQuestions(){
        if (!categories.isEmpty()) {
            List<Question> allQuestions = new ArrayList<>();

            for (Category currCat : categories.values()){
                allQuestions.addAll(currCat.getQuestions());
            }

            return allQuestions;
        } else {
            return null;
        }

    }

    public Category getCategory(String name){
        Category cat = categories.get(name);
        return cat;
    }

    public HashMap<String, Category> getHashSet(){
        return categories;
    }

    @Override
    public String toString(){
        StringBuffer buf = new StringBuffer();

        buf.append("CategoryController{");
        buf.append("categories={\r\n");
        for (Category cat : categories.values()) {
            buf.append(cat.toString());
            buf.append("\r\n");
        }
        buf.append("}, questions={\r\n");
        for (Question quest : getQuestions()) {
            buf.append(quest.toString());
            buf.append("\r\n");
        }
        buf.append("}}");
        return buf.toString();
    }

}

