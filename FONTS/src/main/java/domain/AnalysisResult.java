package domain;
import java.util.*;

/**
 * Represents the result of a clustering analysis performed on form responses.
 *
 * @author Tomeu Mestre (tomeu.mestre@estudiantat.upc.edu)
 */
public class AnalysisResult {

    private final int[] clusterAssignments;
    private final ArrayList<ArrayList<AnswerToQuestion>> centroids;
    private final double quality;

    /**
     * Constructor for AnalysisResult with quality metric
     *
     * @param clusterAssignments array where index i contains the cluster number for user i
     * @param centroids matrice of answerToQuestion representing the centroids/medoids (the centers of each cluster)
     * @param quality quality metric of the clustering
     */
    public AnalysisResult(int[] clusterAssignments, ArrayList<ArrayList<AnswerToQuestion>> centroids, double quality) {
        this.clusterAssignments = clusterAssignments;
        this.centroids = centroids;
        this.quality = quality;
    }

    /**
     * Gets all cluster assignments
     *
     * @return array of cluster assignments
     */
    public int[] getClusterAssignments() {
        return clusterAssignments.clone();
    }

    /**
     * Gets the matrice of centroids/medoids
     *
     * @return array of answerToQuestion representing the centroid/medoid
     */
    public ArrayList<ArrayList<AnswerToQuestion>> getCentroids() {
        return centroids;
    }

    /**
     * Gets the quality metric of the clustering, should be {@code -1 < 0 < 1}
     * 
     * @return quality metric value
     */
    public double getQuality() {
        return quality;
    }
}
