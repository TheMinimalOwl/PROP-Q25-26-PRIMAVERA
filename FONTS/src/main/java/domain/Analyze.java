package domain;

import java.util.*;
import java.time.LocalDateTime;

/**
 * Context class in strategy pattern, stores information about the analisis to perform and its result
 *
 * @author Tomeu Mestre (tomeu.mestre@estudiantat.upc.edu)
 */
public class Analyze {
    // ===== ATTRIBUTES / FIELDS =====
    private Integer idForm; // Key
    public static final Integer UNASSIGNED_ID = -1;
    private Integer idAnalyze = UNASSIGNED_ID; // Key

    // Analisys info
    private LocalDateTime time;
    private String name;
    private AnalyzeStrategy algorithm;
    private int nClusters;
    // Result
    private AnalysisResult result;

    // ===== CONSTRUCTORS =====

    public Analyze(Integer idForm, String name, int numClusters, AnalyzeStrategy chosenAlgorithm, ArrayList<ArrayList<AnswerToQuestion>> answers) {
        this.idAnalyze = UNASSIGNED_ID;
        this.idForm = idForm;
        this.name = name;
        this.nClusters = numClusters;
        this.algorithm = chosenAlgorithm;
        this.time = LocalDateTime.now();

        validateAnswers(answers);
        this.result = doAnalysis(answers);
    }

    // Parameterized constructor
    public Analyze(Integer idAnalyze, Integer idForm, String name, int numClusters, AnalyzeStrategy chosenAlgorithm, ArrayList<ArrayList<AnswerToQuestion>> answers) {
        this.idAnalyze = idAnalyze;
        this.idForm = idForm;
        this.name = name;
        this.nClusters = numClusters;
        this.algorithm = chosenAlgorithm;
        this.time = LocalDateTime.now();

        validateAnswers(answers);
        this.result = doAnalysis(answers);
    }

    // ===== GETTER AND SETTER METHODS =====
    public Integer getIdForm() {
        return this.idForm;
    }

    public void setIdForm(Integer idForm) {
        this.idForm = idForm;
    }

    public Integer getIdAnalyze() {
        return this.idAnalyze;
    }

    public void setIdAnalyze(Integer id) {
        this.idAnalyze = id;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AnalysisResult getResult() {
        return result;
    }

    public int getNClusters() {
        return nClusters;
    }

    /**
     * Returns true if the instance has been assigned an id.
     *
     */
    public Boolean hasAssignedId() {
        return (idAnalyze != UNASSIGNED_ID);
    }

    // set nclusters and setresult will never be called ?? It would imply a reexecution of the algorithm

    // ===== CLASS METHODS =====
    /**
     * Function called to do the cluster analysis.
     * Delegates to the strategy and returns the result.
     *
     * @param answers is a matrix containing all the answers to all the questions in the form.
     *                Each row represents an Answer (i.e. a full answer to the form)
     *                Each column represents said question in the form.
     * @return AnalysisResult containing cluster assignments and centroids/medoids
     */
    private AnalysisResult doAnalysis(ArrayList<ArrayList<AnswerToQuestion>> answers) {
        return algorithm.doAnalysis(answers, nClusters, idForm);
    }

    /**
     * Validates that the input answers list is valid for analysis.
     *
     * @param answers the list of answers to validate
     * @throws IllegalArgumentException if answers is null or empty
     */
    protected void validateAnswers(ArrayList<ArrayList<AnswerToQuestion>> answers) {
        if (answers == null) {
            throw new IllegalArgumentException("Answers list cannot be null");
        }
        if (answers.isEmpty()) {
            throw new IllegalArgumentException("Answers list cannot be empty");
        }
        if (nClusters > answers.size()) {
            throw new IllegalArgumentException("Number of clusters (" + nClusters + ") cannot be greater than number of answers (" + answers.size() + ")");
        }
    }
}
