package domain;

/**
 * Represents an answer to a free text question (FreeQuestion)
 */
public class AnswerFreeQuestion extends AnswerToQuestion {
    
    private String answerText;
    
    // ===== CONSTRUCTORS =====
    
    /**
     * Default constructor
     */
    public AnswerFreeQuestion() {
        super();
        this.answerText = "";
    }
    
    /**
     * Parameterized constructor
     * @param idUser the user identifier
     * @param idForm the form identifier
     * @param idQuestion the question identifier
     * @param answerText the text content of the answer
     */
    public AnswerFreeQuestion(int idUser, int idForm, int idQuestion, String answerText) {
        super(idUser, idForm, idQuestion);
        setAnswerText(answerText);
    }
    
    // ===== GETTERS & SETTERS =====
    
    /**
     * Gets the answer text content
     * @return the answer text
     */
    public String getAnswerText() {
        return answerText;
    }
    
    /**
     * Sets the answer text content
     * @param answerText the text to set
     */
    public void setAnswerText(String answerText) {
        if (answerText == null) {
            throw new IllegalArgumentException("Answer text cannot be null");
        }
        this.answerText = answerText;
    }
    
    // ===== CLASS METHODS =====
    
    /**
     * Checks if the answer is empty
     * @return true if the answer text is null, empty, or contains only whitespace
     */
    @Override
    public boolean isEmpty() {
        return answerText.trim().isEmpty();
    }
    
    /**
     * Gets the length of the answer text
     * @return the number of characters in the answer text
     */
    public int getLength() {
        return answerText.length();
    }
    
    /**
     * Validates the answer against a FreeQuestion's constraints
     * @param question the FreeQuestion to validate against
     * @return true if the answer meets the question's constraints
     */
    public boolean isValidForQuestion(FreeQuestion question) {
        if (question == null) {
            throw new IllegalArgumentException("Question cannot be null");
        }
        
        if (isEmpty() && !question.isOptional()) {
            return false;
        }
        
        Integer maxLength = question.getMaxLength();
        if (maxLength != null && getLength() > maxLength) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Returns a string representation of the free answer
     * @return string representation
     */
    @Override
    public String toString() {
        return "AnswerFreeQuestion{" +
                "idUser=" + getIdUser() +
                ", idForm=" + getIdForm() +
                ", idQuestion=" + getIdQuestion() +
                ", answerText='" + answerText + '\'' +
                '}';
    }
}