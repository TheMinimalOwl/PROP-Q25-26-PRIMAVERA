package domain;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * @author Joel Forcadell (joel.forcadell@estudiantat.upc.edu)
 * 
 *         Represents a user's submission to a form.
 *         Stores the form identifier, the user identifier,
 *         the timestamp when the answer was created,
 *         and the list of question IDs that have been answered.
 */
public class Answer {

    // ===== ATTRIBUTES =====

    /** Identifier of the form associated with this answer. */
    private Integer idForm;

    /** Identifier of the user who submitted the answer. */
    private Integer idUser;

    /** Timestamp when the answer was created. */
    private LocalDateTime time;

    /** List of identifiers of the questions answered. */
    private ArrayList<Integer> responses;

    // ========= CONSTRUCTORS ==========

    /**
     * Constructs a new Answer object associated with a form and a user.
     * The timestamp is automatically set to the current time.
     *
     * @param idForm identifier of the form
     * @param idUser identifier of the user
     */

    public Answer(Integer idForm, Integer idUser) {
        this.idForm = idForm;
        this.idUser = idUser;
        this.time = LocalDateTime.now();
        this.responses = new ArrayList<>();
    }
    // ===== GETTERS AND SETTERS =====

    /**
     * Returns the user identifier.
     *
     * @return the user ID
     */
    public Integer getIdUser() {
        return idUser;
    }

    /**
     * Sets a new user identifier.
     *
     * @param idUser the new user ID
     */
    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    /**
     * Returns the form identifier.
     *
     * @return the form ID
     */
    public Integer getIdForm() {
        return idForm;
    }

    /**
     * Sets a new form identifier.
     *
     * @param idForm the new form ID
     */
    public void setIdForm(Integer idForm) {
        this.idForm = idForm;
    }

    /**
     * Returns the timestamp when the answer was created.
     *
     * @return the creation time
     */
    public LocalDateTime getTime() {
        return time;
    }

    /**
     * Returns the list of answered question IDs.
     *
     * @return list of question IDs
     */
    public ArrayList<Integer> getResponses() {
        return responses;
    }

    // ===== CLASS METHODS =====

    /**
     * Adds a question ID to the list of answered questions.
     *
     * @param idQuestion the identifier of the answered question
     */
    public void addResponse(Integer idQuestion) {
        if (!responses.contains(idQuestion)) {
            responses.add(idQuestion);
        }
    }

    /**
     * Removes a question ID from the list of answered questions.
     *
     * @param idQuestion the identifier of the question to remove
     */
    public void deleteResponse(Integer idQuestion) {
        responses.remove(idQuestion);
    }

    /**
     * Checks if a specific question has been answered.
     *
     * @param idQuestion the identifier of the question
     * @return true if the question has been answered, false otherwise
     */
    public boolean existsResponseToQuestion(Integer idQuestion) {
        return responses.contains(idQuestion);
    }

    /**
     * Imports a list of answered question IDs.
     *
     * @param newResponses list of question IDs to import
     * @throws IllegalArgumentException if the list is null
     */
    public void importResponses(ArrayList<Integer> newResponses) {
        if (newResponses == null) {
            throw new IllegalArgumentException("Responses cannot be null");
        }
        this.responses.addAll(newResponses);
    }

    /**
     * Checks if the answer is complete, meaning all questions in the form
     * have been answered.
     *
     * @param totalQuestions the total number of questions in the form
     * @return true if the number of answered questions matches the total, false
     *         otherwise
     */
    public boolean isComplete(int totalQuestions) {
        return idForm != null &&
                totalQuestions == responses.size();
    }

}
