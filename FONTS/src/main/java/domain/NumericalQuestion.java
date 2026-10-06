package domain;

/**
 * Represents a numerical question that accepts numeric responses within an optional range
 */
public class NumericalQuestion extends Question {

    // ===== ATTRIBUTES / FIELDS =====
    private Integer minValue;
    private Integer maxValue;

    // ===== CONSTRUCTORS =====

    /**
     * Default constructor
     */
    public NumericalQuestion() {
        super();
        this.minValue = null;
        this.maxValue = null;
    }

    /**
     * Parameterized constructor without value range
     * @param idQuestion the question identifier
     * @param statement the question statement
     * @param isOptional whether the question is optional
     */
    public NumericalQuestion(int idQuestion, String statement, boolean isOptional) {
        super(idQuestion, statement, isOptional);
        this.minValue = null;
        this.maxValue = null;
    }

    /**
     * Constructor with value range
     * @param idQuestion the question identifier
     * @param statement the question statement
     * @param isOptional whether the question is optional
     * @param minValue the minimum allowed value
     * @param maxValue the maximum allowed value
     */
    public NumericalQuestion(int idQuestion, String statement, boolean isOptional, Integer minValue, Integer maxValue) {
        super(idQuestion, statement, isOptional);
        setValueRange(minValue, maxValue);
    }

    /**
     * Constructor with value range, witihout idQuestion
     * @param statement the question statement
     * @param isOptional whether the question is optional
     * @param minValue the minimum allowed value
     * @param maxValue the maximum allowed value
     */
    public NumericalQuestion(String statement, boolean isOptional, Integer minValue, Integer maxValue) {
        super(statement, isOptional);
        setValueRange(minValue, maxValue);
    }
    
    // ===== GETTER AND SETTER METHODS =====

    /**
     * Gets the minimum allowed value
     * @return the minimum value, or null if not set
     */
    public Integer getMinValue() {
        return minValue;
    }

    /**
     * Sets the minimum allowed value
     * @param minValue the minimum value to set, or null for no minimum
     */
    public void setMinValue(Integer minValue) {
        if (minValue != null && maxValue != null && minValue > maxValue) {
            throw new IllegalArgumentException("Minimum value cannot be greater than maximum value: " + minValue + " > " + maxValue);
        }
        this.minValue = minValue;
    }

    /**
     * Gets the maximum allowed value
     * @return the maximum value, or null if not set
     */
    public Integer getMaxValue() {
        return maxValue;
    }

    /**
     * Sets the maximum allowed value
     * @param maxValue the maximum value to set, or null for no maximum
     */
    public void setMaxValue(Integer maxValue) {
        if (minValue != null && maxValue != null && minValue > maxValue) {
            throw new IllegalArgumentException("Maximum value cannot be less than minimum value: " + maxValue + " < " + minValue);
        }
        this.maxValue = maxValue;
    }

    /**
     * Sets both min and max values with validation
     * @param minValue the minimum value
     * @param maxValue the maximum value
     */
    public void setValueRange(Integer minValue, Integer maxValue) {
        if (minValue != null && maxValue != null) {
            if (minValue > maxValue) {
                throw new IllegalArgumentException("Invalid value range: min=" + minValue + " cannot be greater than max=" + maxValue);
            }
        }
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    
    // ===== CLASS METHODS =====
    
    /**
     * Returns a string representation of the numerical question
     * @return string representation
     */
    @Override
    public String toString() {
        String base = super.toString();
        return base.substring(0, base.length() - 1) +
               ", range=[" + (minValue != null ? minValue : "no min") + 
               " to " + (maxValue != null ? maxValue : "no max") + "]}";
    }

    /**
     * Checks if the question has a defined value range
     * @return true if both minValue and maxValue are defined
     */
    public boolean hasRange() {
        return minValue != null && maxValue != null;
    }

    /**
     * Gets the size of the allowed value range
     * @return the range size (maxValue - minValue + 1), or -1 if no range is defined
     */
    public int getRangeSize() {
        if (!hasRange()) {
            return -1;
        }
        return maxValue - minValue + 1;
    }
}
