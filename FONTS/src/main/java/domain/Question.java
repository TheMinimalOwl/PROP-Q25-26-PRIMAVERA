package domain;

/**
 * Abstract class representing a question in a survey or form
 * Serves as the base class for all specific question types
 */
public abstract class Question {
    // ===== ATTRIBUTES / FIELDS =====
    public static final Integer UNASSIGNED_ID = -1;
    private int idQuestion = UNASSIGNED_ID;
    private int idForm;
    private String statement;
    private boolean isOptional;

    // ===== CONSTRUCTORS =====
    
    /**
     * Default constructor
     * Initializes question with default values
     */
    public Question() {
        this.idQuestion = UNASSIGNED_ID;
        this.statement = "";
        this.isOptional = false;
    }

    /**
     * Parameterized constructor
     * @param idQuestion the unique identifier for the question
     * @param statement the text content of the question
     * @param isOptional indicates if answering the question is optional
     */
    public Question(int idQuestion, String statement, boolean isOptional) {
        setIdQuestion(idQuestion);
        setStatement(statement);
        this.isOptional = isOptional;
    }

    /**
     * Parameterized constructor without idQuestion
     * @param statement the text content of the question
     * @param isOptional indicates if answering the question is optional
     */
    public Question(String statement, boolean isOptional) {
        idQuestion = UNASSIGNED_ID;
        setStatement(statement);
        this.isOptional = isOptional;
    }

    // ===== GETTER AND SETTER METHODS =====
    
    /**
     * Gets the question identifier
     * @return the unique question ID
     */
    public int getIdQuestion() {
        return idQuestion;
    }

    /**
     * Sets the question identifier
     * @param idQuestion the unique question ID to set
     */
    public void setIdQuestion(int idQuestion) {
        if (idQuestion < 0) {
            throw new IllegalArgumentException("Question ID cannot be negative: " + idQuestion);
        }
        this.idQuestion = idQuestion;
    }

    /**
     * Gets the form identifier that contains this question
     * @return the form ID
     */
    public int getIdForm() {
        return idForm;
    }

    /**
     * Sets the form identifier that contains this question
     * @param idForm the form ID to set
     */
    public void setIdForm(int idForm) {
        if (idForm < 0) {
            throw new IllegalArgumentException("Form ID cannot be negative: " + idForm);
        }
        this.idForm = idForm;
    }

    /**
     * Gets the question statement text
     * @return the question statement
     */
    public String getStatement() {
        return statement;
    }

    /**
     * Sets the question statement text
     * @param statement the statement text to set
     */
    public void setStatement(String statement) {
        if (statement == null) {
            throw new IllegalArgumentException("Question statement cannot be null");
        }
        if (statement.trim().isEmpty()) {
            throw new IllegalArgumentException("Question statement cannot be empty");
        }
        this.statement = statement.trim();
    }

    /**
     * Checks if the question is optional
     * @return true if answering the question is optional, false if required
     */
    public boolean isOptional() {
        return isOptional;
    }

    /**
     * Sets whether the question is optional
     * @param optional true to make the question optional, false to make it required
     */
    public void setOptional(boolean optional) {
        isOptional = optional;
    }

    // ===== ABSTRACT METHODS =====

    // ===== CLASS METHODS =====

    /**
     * Returns true if the instance has been assigned an id.
     *
     */
    public Boolean hasAssignedId() {
        return (idQuestion != UNASSIGNED_ID);
    }
    
    /**
     * Returns a string representation of the question
     * @return string containing question details including actual class name
     */
    @Override
    public String toString() {
        return "Question{" +
                "id=" + idQuestion +
                ", class='" + getClass().getSimpleName() + '\'' +  // Shows the actual class
                ", statement='" + statement + '\'' +
                ", optional=" + isOptional +
                '}';
    }

    /**
     * Compares this question to another object for equality
     * Questions are considered equal if they have the same ID
     * @param obj the object to compare with
     * @return true if the objects represent the same question
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Question question = (Question) obj;
        return idQuestion == question.idQuestion;
    }
}
