package domain;

/**
 * Abstract base class representing an answer to an individual question
 */
public abstract class AnswerToQuestion {
    
    private int idUser;     // ID of the user who answered
    private int idForm;     // ID of the form containing the question
    private int idQuestion; // ID of the question being answered
    
    // ===== CONSTRUCTORS =====
    
    /**
     * Default constructor
     */
    public AnswerToQuestion() {
        this.idUser = 0;
        this.idForm = 0;
        this.idQuestion = 0;
    }
    
    /**
     * Parameterized constructor
     * @param idUser the user identifier
     * @param idForm the form identifier
     * @param idQuestion the question identifier
     */
    public AnswerToQuestion(int idUser, int idForm, int idQuestion) {
        setIdUser(idUser);
        setIdForm(idForm);
        setIdQuestion(idQuestion);
    }
    
    // ===== GETTERS & SETTERS =====
    
    /**
     * Gets the user identifier
     * @return the user ID
     */
    public int getIdUser() {
        return idUser;
    }
    
    /**
     * Sets the user identifier
     * @param idUser the user ID to set
     */
    public void setIdUser(int idUser) {
        if (idUser < 0) {
            throw new IllegalArgumentException("User ID cannot be negative: " + idUser);
        }
        this.idUser = idUser;
    }
    
    /**
     * Gets the form identifier
     * @return the form ID
     */
    public int getIdForm() {
        return idForm;
    }
    
    /**
     * Sets the form identifier
     * @param idForm the form ID to set
     */
    public void setIdForm(int idForm) {
        if (idForm < 0) {
            throw new IllegalArgumentException("Form ID cannot be negative: " + idForm);
        }
        this.idForm = idForm;
    }
    
    /**
     * Gets the question identifier
     * @return the question ID
     */
    public int getIdQuestion() {
        return idQuestion;
    }
    
    /**
     * Sets the question identifier
     * @param idQuestion the question ID to set
     */
    public void setIdQuestion(int idQuestion) {
        if (idQuestion < 0) {
            throw new IllegalArgumentException("Question ID cannot be negative: " + idQuestion);
        }
        this.idQuestion = idQuestion;
    }
    
    // ===== ABSTRACT METHODS =====
    
    /**
     * Checks if the answer is empty
     * @return true if the answer contains no meaningful data
     */
    public abstract boolean isEmpty();
    
    // ===== CLASS METHODS =====
    
    /**
     * Returns a string representation of the answer
     * @return string representation
     */
    @Override
    public String toString() {
        return "AnswerToQuestion{" +
                "idUser=" + idUser+
                ", idForm=" + idForm +
                ", idQuestion=" + idQuestion +
                ", type=" + getClass().getSimpleName() +
                '}';
    }
    
    /**
     * Compares this answer to another object for equality
     * @param obj the object to compare with
     * @return true if the objects are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AnswerToQuestion that = (AnswerToQuestion) obj;
        return idUser == that.idUser && 
               idForm == that.idForm && 
               idQuestion == that.idQuestion;
    }
    
    /**
     * Returns a hash code value for the object
     * @return the hash code
     */
    @Override
    public int hashCode() {
        int result = idUser;
        result = 31 * result + idForm;
        result = 31 * result + idQuestion;
        return result;
    }
}