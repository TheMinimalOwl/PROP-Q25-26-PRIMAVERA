package domain;
import java.util.ArrayList;

/**
 * Represents a multiple choice question that allows single or multiple selections
 */
public class MultipleChoiceQuestion extends Question {

    // ===== ATTRIBUTES / FIELDS =====
    private ArrayList<String> options;
    private int minSelections;
    private int maxSelections;

    // ===== CONSTRUCTORS =====

    /**
     * Default constructor
     */
    public MultipleChoiceQuestion() {
        super();
        this.options = new ArrayList<>();
        this.minSelections = 1;
        this.maxSelections = 1;
    }

    /**
     * Basic constructor for single selection questions
     * @param idQuestion the question identifier
     * @param statement the question statement
     * @param isOptional whether the question is optional
     * @param options the list of available options
     */
    public MultipleChoiceQuestion(int idQuestion, String statement, boolean isOptional, ArrayList<String> options) {
        super(idQuestion, statement, isOptional);
        setOptions(options);
        validateSelectionLimits(1, 1);
        this.minSelections = 1;
        this.maxSelections = 1;
    }

    /**
     * Complete constructor with selection limits
     * @param idQuestion the question identifier
     * @param statement the question statement
     * @param isOptional whether the question is optional
     * @param options the list of available options
     * @param minSelections the minimum number of required selections
     * @param maxSelections the maximum number of allowed selections
     */
    public MultipleChoiceQuestion(int idQuestion, String statement, boolean isOptional, 
                                 ArrayList<String> options, int minSelections, int maxSelections) {
        super(idQuestion, statement, isOptional);
        setOptions(options);
        setSelectionLimits(minSelections, maxSelections);
    }

    /**
     * Constructor idQuestion
     * @param statement the question statement
     * @param isOptional whether the question is optional
     * @param options the list of available options
     * @param minSelections the minimum number of required selections
     * @param maxSelections the maximum number of allowed selections
     */
    public MultipleChoiceQuestion(String statement, boolean isOptional,
                                 ArrayList<String> options, int minSelections, int maxSelections) {
        super(statement, isOptional);
        setOptions(options);
        setSelectionLimits(minSelections, maxSelections);
    }

    // ===== GETTER AND SETTER METHODS =====

    /**
     * Gets the list of available options
     * @return the options list
     */
    public ArrayList<String> getOptions() {
        return new ArrayList<>(options);
    }

    /**
     * Sets the list of available options
     * @param options the options list to set
     */
    public void setOptions(ArrayList<String> options) {
        if (options == null) {
            this.options = new ArrayList<>();
        } else {
            // Validate that no option is null or empty
            for (String option : options) {
                if (option == null || option.trim().isEmpty()) {
                    throw new IllegalArgumentException("Options cannot contain null or empty strings");
                }
            }
            this.options = new ArrayList<>(options);
        }
    }

    /**
     * Gets the minimum number of required selections
     * @return the minimum selections count
     */
    public int getMinSelections() {
        return minSelections;
    }

    /**
     * Sets the minimum number of required selections
     * @param minSelections the minimum selections count to set
     */
    public void setMinSelections(int minSelections) {
        validateSelectionLimits(minSelections, this.maxSelections);
        this.minSelections = minSelections;
    }

    /**
     * Gets the maximum number of allowed selections
     * @return the maximum selections count
     */
    public int getMaxSelections() {
        return maxSelections;
    }

    /**
     * Sets the maximum number of allowed selections
     * @param maxSelections the maximum selections count to set
     */
    public void setMaxSelections(int maxSelections) {
        validateSelectionLimits(this.minSelections, maxSelections);
        this.maxSelections = maxSelections;
    }

    /**
     * Sets both min and max selections with validation
     * @param minSelections the minimum selections
     * @param maxSelections the maximum selections
     */
    public void setSelectionLimits(int minSelections, int maxSelections) {
        validateSelectionLimits(minSelections, maxSelections);
        this.minSelections = minSelections;
        this.maxSelections = maxSelections;
    }

    /**
     * Validates selection limits
     * @param minSelections the minimum selections
     * @param maxSelections the maximum selections
     */
    private void validateSelectionLimits(int minSelections, int maxSelections) {
        if (minSelections < 0) {
            throw new IllegalArgumentException("Minimum selections cannot be negative: " + minSelections);
        }
        if (maxSelections < 0) {
            throw new IllegalArgumentException("Maximum selections cannot be negative: " + maxSelections);
        }
        if (minSelections > maxSelections) {
            throw new IllegalArgumentException("Minimum selections cannot exceed maximum selections: " + minSelections + " > " + maxSelections);
        }
        if (!options.isEmpty() && maxSelections > options.size()) {
            throw new IllegalArgumentException("Maximum selections cannot exceed number of options: " + maxSelections + " > " + options.size());
        }
    }

    // ===== CLASS METHODS =====
    
    /**
     * Adds an option to the question
     * @param option the option to add
     */
    public void addOption(String option) {
        if (option == null || option.trim().isEmpty()) {
            throw new IllegalArgumentException("Option cannot be null or empty");
        }
        options.add(option.trim());
    }
     
    /**
     * Removes an option by index
     * @param index the index of the option to remove
     */
    public void removeOption(int index) {
        if (index < 0 || index >= options.size()) {
            throw new IndexOutOfBoundsException("Invalid option index: " + index + ". Valid range: 0 to " + (options.size() - 1));
        }
        options.remove(index);
    }
    
    /**
     * Gets the number of available options
     * @return the option count
     */
    public int getOptionCount() {
        return options.size();
    }
    
    /**
     * Returns a string representation of the multiple choice question
     * @return string representation
     */
    @Override
    public String toString() {
        String base = super.toString();
        return base.substring(0, base.length() - 1) +
               ", options=" + options.size() +
               ", selections=" + minSelections + "-" + maxSelections + "}";
    }

    /**
     * Clears all options and resets selection limits
     */
    public void clearOptions() {
        options.clear();
        minSelections = 0;
        maxSelections = 0;
    }
    
    /**
     * Returns a detailed string representation of all options
     * @return formatted options string
     */
    public String getOptionsAsString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < options.size(); i++) {
            sb.append((i + 1)).append(". ").append(options.get(i));
            if (i < options.size() - 1) {
                sb.append("; ");
            }
        }
        return sb.toString();
    }
}
