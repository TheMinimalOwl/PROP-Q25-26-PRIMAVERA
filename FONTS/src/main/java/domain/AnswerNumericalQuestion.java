package domain;

/**
 * Represents an answer to a numerical question (NumericalQuestion)
 */
public class AnswerNumericalQuestion extends AnswerToQuestion {
    
    private Integer numericalValue;
    
    // ===== CONSTRUCTORS =====
    
    /**
     * Default constructor
     */
    public AnswerNumericalQuestion() {
        super();
        this.numericalValue = null;
    }
    
    /**
     * Parameterized constructor
     * @param idUser the user identifier
     * @param idForm the form identifier
     * @param idQuestion the question identifier
     * @param numericalValue the numerical value of the answer
     */
    public AnswerNumericalQuestion(int idUser, int idForm, int idQuestion, Integer numericalValue) {
        super(idUser, idForm, idQuestion);
        setNumericalValue(numericalValue);
    }
    
    // ===== GETTERS & SETTERS =====
    
    /**
     * Gets the numerical value of the answer
     * @return the numerical value
     */
    public Integer getNumericalValue() {
        return numericalValue;
    }
    
    /**
     * Sets the numerical value of the answer
     * @param numericalValue the numerical value to set
     */
    public void setNumericalValue(Integer numericalValue) {
        this.numericalValue = numericalValue;
    }
    
    // ===== CLASS METHODS =====
    
    /**
     * Checks if the answer is empty
     * @return true if the numerical value is null
     */
    @Override
    public boolean isEmpty() {
        return numericalValue == null;
    }
    
    /**
     * Checks if the numerical value is within a specified range
     * @param minValue the minimum allowed value (inclusive, can be null for no minimum)
     * @param maxValue the maximum allowed value (inclusive, can be null for no maximum)
     * @return true if the value is within the specified range
     */
    public boolean isInRange(Integer minValue, Integer maxValue) {
        if (numericalValue == null) return false;
        if (minValue != null && numericalValue < minValue) return false;
        if (maxValue != null && numericalValue > maxValue) return false;
        return true;
    }
    
    /**
     * Validates the answer against a NumericalQuestion's constraints
     * @param question the NumericalQuestion to validate against
     * @return true if the answer meets the question's constraints
     */
    public boolean isValidForQuestion(NumericalQuestion question) {
        if (question == null) {
            throw new IllegalArgumentException("Question cannot be null");
        }
        
        if (isEmpty() && !question.isOptional()) {
            return false;
        }
        
        if (!isEmpty() && !isInRange(question.getMinValue(), question.getMaxValue())) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Returns a string representation of the numerical answer
     * @return string representation
     */
    @Override
    public String toString() {
        return "AnswerNumericalQuestion{" +
                "idUser=" + getIdUser() +
                ", idForm=" + getIdForm() +
                ", idQuestion=" + getIdQuestion() +
                ", numericalValue=" + numericalValue +
                '}';
    }
}