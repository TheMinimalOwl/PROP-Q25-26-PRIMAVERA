package domain;
import java.util.ArrayList;

/**
 * Represents an ordered single choice question where options are presented in a specific order
 */
public class OrderedSingleQuestion extends Question {

    private ArrayList<String> options;

    // ===== CONSTRUCTORS =====

    /**
     * Default constructor
     */
    public OrderedSingleQuestion() {
        super();
        this.options = new ArrayList<>();
    }

    /**
     * Basic constructor
     * @param idQuestion the question identifier
     * @param statement the question statement
     * @param isOptional whether the question is optional
     */
    public OrderedSingleQuestion(int idQuestion, String statement, boolean isOptional) {
        super(idQuestion, statement, isOptional);
        this.options = new ArrayList<>();
    }

    /**
     * Constructor with options
     * @param idQuestion the question identifier
     * @param statement the question statement
     * @param isOptional whether the question is optional
     * @param options the list of ordered options
     */
    public OrderedSingleQuestion(int idQuestion, String statement, boolean isOptional, ArrayList<String> options) {
        super(idQuestion, statement, isOptional);
        setOptions(options);
    }

    /**
     * Constructor with options, without idQuestion
     * @param statement the question statement
     * @param isOptional whether the question is optional
     * @param options the list of ordered options
     */
    public OrderedSingleQuestion(String statement, boolean isOptional, ArrayList<String> options) {
        super(statement, isOptional);
        setOptions(options);
    }

    // ===== GETTERS & SETTERS =====

    /**
     * Gets the list of ordered options
     * @return a new ArrayList containing the options
     */
    public ArrayList<String> getOptions() {
        return new ArrayList<>(options);
    }

    /**
     * Sets the list of ordered options
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
     * Returns a string representation of the ordered single question
     * @return string representation
     */
    @Override
    public String toString() {
        String base = super.toString();
        return base.substring(0, base.length() - 1) +
               ", options=" + options.size() +
               ", type=ordered-single}";
    }
}
