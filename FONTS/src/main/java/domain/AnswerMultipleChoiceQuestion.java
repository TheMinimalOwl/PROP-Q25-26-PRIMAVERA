package domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an answer to a multiple choice question (MultipleChoiceQuestion)
 */
public class AnswerMultipleChoiceQuestion extends AnswerToQuestion {
    
    private List<Integer> selectedOptionIndexes;
    
    // ===== CONSTRUCTORS =====
    
    /**
     * Default constructor
     */
    public AnswerMultipleChoiceQuestion() {
        super();
        this.selectedOptionIndexes = new ArrayList<>();
    }
    
    /**
     * Parameterized constructor
     * @param idUser the user identifier
     * @param idForm the form identifier
     * @param idQuestion the question identifier
     * @param selectedOptionIndexes the list of selected option indexes
     */
    public AnswerMultipleChoiceQuestion(int idUser, int idForm, int idQuestion, List<Integer> selectedOptionIndexes) {
        super(idUser, idForm, idQuestion);
        setSelectedOptionIndexes(selectedOptionIndexes);
    }
    
    // ===== GETTERS & SETTERS =====
    
    /**
     * Gets the list of selected option indexes
     * @return a new ArrayList containing the selected indexes
     */
    public ArrayList<Integer> getSelectedOptionIndexes() {
        return new ArrayList<>(selectedOptionIndexes);
    }
    
    /**
     * Sets the list of selected option indexes
     * @param selectedOptionIndexes the list of indexes to set
     */
    public void setSelectedOptionIndexes(List<Integer> selectedOptionIndexes) {
        if (selectedOptionIndexes == null) {
            this.selectedOptionIndexes = new ArrayList<>();
        } else {
            // Validate all indexes are non-negative
            for (Integer index : selectedOptionIndexes) {
                if (index != null && index < 0) {
                    throw new IllegalArgumentException("Option index cannot be negative: " + index);
                }
            }
            this.selectedOptionIndexes = new ArrayList<>(selectedOptionIndexes);
        }
    }
    
    // ===== CLASS METHODS =====
    
    /**
     * Checks if the answer is empty
     * @return true if no options are selected
     */
    @Override
    public boolean isEmpty() {
        return selectedOptionIndexes.isEmpty();
    }
    
    /**
     * Adds an option index to the selection list
     * @param index the option index to add (must be non-negative and not already present)
     */
    public void addOptionIndex(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Option index cannot be negative: " + index);
        }
        if (!selectedOptionIndexes.contains(index)) {
            selectedOptionIndexes.add(index);
        }
    }
    
    /**
     * Removes an option index from the selection list
     * @param index the option index to remove
     */
    public void removeOptionIndex(int index) {
        selectedOptionIndexes.remove((Integer) index);
    }
    
    /**
     * Checks if all selected indexes are valid for a given number of options
     * @param optionCount the total number of available options
     * @return true if all selected indexes are valid {@code (not null, >= 0, and < optionCount)}
     */
    public boolean areValidIndexes(int optionCount) {
        if (optionCount <= 0) {
            throw new IllegalArgumentException("Option count must be positive: " + optionCount);
        }
        for (Integer index : selectedOptionIndexes) {
            if (index == null || index < 0 || index >= optionCount) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Checks if the number of selected options is within the allowed range
     * @param minSelections the minimum number of required selections
     * @param maxSelections the maximum number of allowed selections
     * @return true if the selection count is within the specified range
     */
    public boolean isInSelectionRange(int minSelections, int maxSelections) {
        if (minSelections < 0) {
            throw new IllegalArgumentException("Minimum selections cannot be negative: " + minSelections);
        }
        if (maxSelections < 0) {
            throw new IllegalArgumentException("Maximum selections cannot be negative: " + maxSelections);
        }
        if (minSelections > maxSelections) {
            throw new IllegalArgumentException("Minimum selections cannot exceed maximum selections: " + minSelections + " > " + maxSelections);
        }
        
        int count = selectedOptionIndexes.size();
        return count >= minSelections && count <= maxSelections;
    }
    
    /**
     * Gets the number of selected options
     * @return the count of selected options
     */
    public int getSelectionCount() {
        return selectedOptionIndexes.size();
    }
    
    /**
     * Validates the answer against a MultipleChoiceQuestion's constraints
     * @param question the MultipleChoiceQuestion to validate against
     * @return true if the answer meets the question's constraints
     */
    public boolean isValidForQuestion(MultipleChoiceQuestion question) {
        if (question == null) {
            throw new IllegalArgumentException("Question cannot be null");
        }
        
        if (isEmpty() && !question.isOptional()) {
            return false;
        }
        
        if (!isEmpty()) {
            // Check if all indexes are valid
            if (!areValidIndexes(question.getOptionCount())) {
                return false;
            }
            
            // Check if selection count is within allowed range
            if (!isInSelectionRange(question.getMinSelections(), question.getMaxSelections())) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Returns a string representation of the multiple choice answer
     * @return string representation
     */
    @Override
    public String toString() {
        return "AnswerMultipleChoiceQuestion{" +
                "idUser=" + getIdUser() +
                ", idForm=" + getIdForm() +
                ", idQuestion=" + getIdQuestion() +
                ", selectedOptionIndexes=" + selectedOptionIndexes +
                '}';
    }
}