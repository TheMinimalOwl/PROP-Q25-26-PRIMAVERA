package domain;

/**
 * Represents a free text question that allows open-ended responses
 */
public class FreeQuestion extends Question {

    // ===== ATTRIBUTES / FIELDS =====
    private Integer maxLength;

    // ===== CONSTRUCTORS =====

    /**
     * Default constructor
     */
    public FreeQuestion() {
        super();
        this.maxLength = null;
    }

    /**
     * Parameterized constructor without length limit
     * @param idQuestion the question identifier
     * @param statement the question statement
     * @param isOptional whether the question is optional
     */
    public FreeQuestion(int idQuestion, String statement, boolean isOptional) {
        super(idQuestion, statement, isOptional);
        this.maxLength = null;
    }

    /**
     * Constructor with length limit
     * @param idQuestion the question identifier
     * @param statement the question statement
     * @param isOptional whether the question is optional
     * @param maxLength the maximum allowed response length
     */
    public FreeQuestion(int idQuestion, String statement, boolean isOptional, Integer maxLength) {
        super(idQuestion, statement, isOptional);
        setMaxLength(maxLength);
    }

    /**
     * Constructor with length limit, without idQuestion
     * @param statement the question statement
     * @param isOptional whether the question is optional
     * @param maxLength the maximum allowed response length
     */
    public FreeQuestion(String statement, boolean isOptional, Integer maxLength) {
        super(statement, isOptional);
        setMaxLength(maxLength);
    }


    // ===== GETTER AND SETTER METHODS =====

    /**
     * Gets the maximum allowed response length
     * @return the maximum length, or null if unlimited
     */
    public Integer getMaxLength() {
        return maxLength;
    }

    /**
     * Sets the maximum allowed response length
     * @param maxLength the maximum length to set, or null for unlimited
     */
    public void setMaxLength(Integer maxLength) {
        if (maxLength != null) {
            if (maxLength <= 0) {
                throw new IllegalArgumentException("Maximum length must be positive: " + maxLength);
            }
            if (maxLength > 10000) {
                throw new IllegalArgumentException("Maximum length cannot exceed 10000 characters: " + maxLength);
            }
        }
        this.maxLength = maxLength;
    }

    // ===== CLASS METHODS =====
    
    /**
     * Returns a string representation of the free question
     * @return string representation
     */
    @Override
    public String toString() {
        String base = super.toString();
        return base.substring(0, base.length() - 1) +
               ", maxLength=" + (maxLength != null ? maxLength : "unlimited") + "}";
    }

    /**
     * Checks if the question has a character length limit
     * @return true if maxLength is defined
     */
    public boolean hasLengthLimit() {
        return maxLength != null;
    }

    /**
     * Sets the question statement, converting null to empty string
     * @param statement the statement to set
     */
    @Override
    public void setStatement(String statement) {
        if (statement == null) {
            throw new IllegalArgumentException("Question statement cannot be null");
        }
        super.setStatement(statement);
    }
}
