package domain;

import java.util.ArrayList;

/**
 * Class representing a Form
 * 
 * @author Tomeu Mestre (tomeu.mestre@estudiantat.upc.edu)
 */

public class Form {
    // ===== ATTRIBUTES / FIELDS =====
    public static final Integer UNASSIGNED_ID = -1;
    private int idForm = UNASSIGNED_ID;
    private String name;
    private Integer userPropietary; // idUser
    private ArrayList<Integer> questions; // idQuestion
    private ArrayList<Integer> answers; // idUser
    private ArrayList<Integer> idAnalyze; // Key of analyze
    private boolean isPublished;

    // ===== CONSTRUCTORS =====

    /**
     * Constructor for creating a new unpublished form.
     */
    public Form(String name, Integer userPropietary) {
        this.idForm = UNASSIGNED_ID;
        this.name = name;
        this.userPropietary = userPropietary;
        this.questions = new ArrayList<>();
        this.answers = new ArrayList<>();
        this.idAnalyze = new ArrayList<>();
        this.isPublished = false; // Por defecto, no publicado
    }

    /**
     * Constructor for creating a form with ID.
     */
    public Form(int idForm, String name, Integer userPropietary) {
        this.idForm = idForm;
        this.name = name;
        this.userPropietary = userPropietary;
        this.questions = new ArrayList<>();
        this.answers = new ArrayList<>();
        this.idAnalyze = new ArrayList<>();
        this.isPublished = false; // Por defecto, no publicado
    }

    /**
     * Parameterized constructor for creating a form with all attributes.
     */
    public Form(int id, String name, Integer userPropietary, 
                ArrayList<Integer> questions, ArrayList<Integer> answers, 
                ArrayList<Integer> analyze, boolean isPublished) {
        this.idForm = id;
        this.name = name;
        this.userPropietary = userPropietary;
        this.questions = questions;
        this.answers = answers;
        this.idAnalyze = analyze;
        this.isPublished = isPublished;
    }

    // ===== GETTER AND SETTER METHODS =====

    public int getIdForm() {
        return idForm;
    }

    public void setIdForm(int idForm) {
        this.idForm = idForm;
    }

    public Integer getUserPropietary() {
        return userPropietary;
    }

    public void setUserPropietary(Integer userPropietary) {
        this.userPropietary = userPropietary;
    }

    public ArrayList<Integer> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Integer> questions) {
        this.questions = questions;
    }

    public ArrayList<Integer> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<Integer> answers) {
        this.answers = answers;
    }

    public ArrayList<Integer> getAnalyze() {
        return idAnalyze;
    }

    public void setAnalyze(ArrayList<Integer> analyze) {
        this.idAnalyze = analyze;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public void setPublished(boolean published) {
        isPublished = published;
    }

    // ===== CLASS METHODS =====

    /**
     * Returns true if the instance has been assigned an id.
     */
    public Boolean hasAssignedId() {
        return (idForm != UNASSIGNED_ID);
    }

    /**
     * Adds a question to the form if it's not published.
     * 
     * @param question the question ID to add
     * @return true if the question was added, false if the form is published
     */
    public boolean addQuestion(Integer question) {
        if (isPublished) {
            return false; // No se pueden modificar formularios publicados
        }
        questions.add(question);
        return true;
    }

    /**
     * Adds an answer to the form.
     * Las respuestas sí se pueden añadir aunque esté publicado.
     */
    public void addAnswer(Integer answer) {
        answers.add(answer);
    }

    /**
     * Adds an analysis to the form.
     */
    public void addAnalyze(Integer analyze) {
        this.idAnalyze.add(analyze);
    }

    /**
     * Removes a question from the form if it's not published.
     * 
     * @param question the question ID to remove
     * @return true if the question was removed, false if the form is published
     */
    public boolean removeQuestion(Integer question) {
        if (isPublished) {
            return false; // No se pueden modificar formularios publicados
        }
        return questions.remove(question);
    }

    /**
     * Removes an answer from the form.
     */
    public boolean removeAnswer(Integer answer) {
        return answers.remove(answer);
    }
    
    /**
     * Removes an analysis from the form.
     */
    public boolean removeAnalyze(Integer analyze) {
        return this.idAnalyze.remove(analyze);
    }

    /**
     * Publishes the form, making it read-only for modifications.
     */
    public void publish() {
        this.isPublished = true;
    }

    /**
     * Unpublishes the form, allowing modifications again.
     */
    public void unpublish() {
        this.isPublished = false;
    }

    /**
     * Checks if the form can be modified.
     * 
     * @return true if the form is not published and can be modified
     */
    public boolean isModifiable() {
        return !isPublished;
    }

    /**
     * Compares this form to another object for equality.
     * Two forms are considered equal if they have the same idForm.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Form form = (Form) obj;
        return idForm == form.idForm;
    }

    /**
     * Returns a hash code value for this form based on its idForm.
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(idForm);
    }

    /**
     * Returns a string representation of this form including its ID, name,
     * publication status, and the number of questions, answers, and analyses.
     */
    @Override
    public String toString() {
        return "Form{" +
                "idForm=" + idForm +
                ", name='" + name + '\'' +
                ", userPropietary=" + (userPropietary != null ? userPropietary.getClass().getSimpleName() : "null") +
                ", isPublished=" + isPublished +
                ", questions=" + (questions != null ? questions.size() : 0) +
                ", answers=" + (answers != null ? answers.size() : 0) +
                ", analyses=" + (idAnalyze != null ? idAnalyze.size() : 0) +
                '}';
    }
}
