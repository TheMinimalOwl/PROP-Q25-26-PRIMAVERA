package domain;

/**
 * Represents an answer to a single choice question (MultipleChoiceQuestion and OrderedSingleQuestion)
 */
public class AnswerSingleChoiceQuestion extends AnswerToQuestion {
    
    private Integer selectedOptionIndex;
    
    // ===== CONSTRUCTORS =====
    
    /**
     * Default constructor
     */
    public AnswerSingleChoiceQuestion() {
        super();
        this.selectedOptionIndex = null;
    }
    
    /**
     * Parameterized constructor
     * @param idUser the user identifier
     * @param idForm the form identifier
     * @param idQuestion the question identifier
     * @param selectedOptionIndex the index of the selected option
     */
    public AnswerSingleChoiceQuestion(int idUser, int idForm, int idQuestion, Integer selectedOptionIndex) {
        super(idUser, idForm, idQuestion);
        setSelectedOptionIndex(selectedOptionIndex);
    }
    
    // ===== GETTERS & SETTERS =====
    
    /**
     * Gets the index of the selected option
     * @return the selected option index
     */
    public Integer getSelectedOptionIndex() {
        return selectedOptionIndex;
    }
    
    /**
     * Sets the index of the selected option
     * @param selectedOptionIndex the option index to set
     */
    public void setSelectedOptionIndex(Integer selectedOptionIndex) {
        if (selectedOptionIndex != null && selectedOptionIndex < 0) {
            throw new IllegalArgumentException("Option index cannot be negative: " + selectedOptionIndex);
        }
        this.selectedOptionIndex = selectedOptionIndex;
    }
    
    // ===== CLASS METHODS =====
    
    /**
     * Checks if the answer is empty
     * @return true if no option is selected
     */
    @Override
    public boolean isEmpty() {
        return selectedOptionIndex == null;
    }
    
    /**
     * Checks if the selected index is valid for a given number of options
     * @param optionCount the total number of available options
     * @return true if the index is valid {@code (not null, >= 0, and < optionCount)}
     */
    public boolean isValidIndex(int optionCount) {
        if (optionCount <= 0) {
            throw new IllegalArgumentException("Option count must be positive: " + optionCount);
        }
        return selectedOptionIndex != null && 
               selectedOptionIndex >= 0 && 
               selectedOptionIndex < optionCount;
    }
    
    /**
     * Validates the answer against a question's constraints
     * @param question the question to validate against (MultipleChoiceQuestion or OrderedSingleQuestion)
     * @return true if the answer meets the question's constraints
     */
    public boolean isValidForQuestion(Question question) {
        if (question == null) {
            throw new IllegalArgumentException("Question cannot be null");
        }
        
        if (isEmpty() && !question.isOptional()) {
            return false;
        }
        
        if (!isEmpty()) {
            if (question instanceof MultipleChoiceQuestion) {
                MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) question;
                return isValidIndex(mcq.getOptionCount());
            } else if (question instanceof OrderedSingleQuestion) {
                OrderedSingleQuestion osq = (OrderedSingleQuestion) question;
                return isValidIndex(osq.getOptionCount());
            }
        }
        
        return true;
    }
    
    /**
     * Returns a string representation of the single choice answer
     * @return string representation
     */
    @Override
    public String toString() {
        return "AnswerSingleChoiceQuestion{" +
                "idUser=" + getIdUser() +
                ", idForm=" + getIdForm() +
                ", idQuestion=" + getIdQuestion() +
                ", selectedOptionIndex=" + selectedOptionIndex +
                '}';
    }
}