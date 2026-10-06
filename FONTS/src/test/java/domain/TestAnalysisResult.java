package domain;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.ArrayList;

/**
 * Unit tests for AnalysisResults
 * 
 * @author Tomeu Mestre (tomeu.mestre@estudiantat.upc.edu)
 */
public class TestAnalysisResult {

    /**
     * Test objective: Test the AnalysisResult constructor
     * Required data files: Manually entered data. No additional files needed.
     * Values studied: Gray box strategy. Verifies that the constructor initializes all fields.
     * Operation: Create an AnalysisResult and verify that getters return the correct values.
     */
    @Test
    public void testConstructorInitializesFields() {
        int[] assignments = {0, 1, 0, 1, 2};
        ArrayList<ArrayList<AnswerToQuestion>> centroids = createMockCentroids(3);
        double quality = 0.75;
        
        AnalysisResult result = new AnalysisResult(assignments, centroids, quality);
        
        assertArrayEquals(assignments, result.getClusterAssignments());
        assertEquals(3, result.getCentroids().size());
        assertEquals(quality, result.getQuality(), 0.0001);
    }

    /**
     * Test objective: Test with different quality values
     * Required data files: Manually entered data. No additional files needed.
     * Values studied: Gray box strategy. Verifies that quality can be stored correctly.
     * Operation: Create AnalysisResult with different quality values (negative, zero, positive).
     */
    @Test
    public void testDifferentQualityValues() {
        int[] assignments = {0};
        ArrayList<ArrayList<AnswerToQuestion>> centroids = createMockCentroids(1);
        
        // Test negative quality (poor clustering)
        AnalysisResult result1 = new AnalysisResult(assignments, centroids, -1.0);
        assertEquals(-1.0, result1.getQuality(), 0.0001);
        
        // Test zero quality (undefined or single cluster)
        AnalysisResult result2 = new AnalysisResult(assignments, centroids, 0.0);
        assertEquals(0.0, result2.getQuality(), 0.0001);
        
        // Test positive quality (good clustering)
        AnalysisResult result3 = new AnalysisResult(assignments, centroids, 1.0);
        assertEquals(1.0, result3.getQuality(), 0.0001);
        
        // Test intermediate quality
        AnalysisResult result4 = new AnalysisResult(assignments, centroids, 0.623);
        assertEquals(0.623, result4.getQuality(), 0.0001);
    }

    /**
     * Test objective: Test with empty arrays
     * Required data files: Manually entered data. No additional files needed.
     * Values studied: Gray box strategy. Verifies that AnalysisResult handles empty arrays.
     * Operation: Create an AnalysisResult with empty arrays and verify it works correctly.
     */
    @Test
    public void testEmptyArrays() {
        int[] emptyAssignments = {};
        ArrayList<ArrayList<AnswerToQuestion>> emptyCentroids = new ArrayList<>();
        
        AnalysisResult result = new AnalysisResult(emptyAssignments, emptyCentroids, 0.0);
        
        assertNotNull(result.getClusterAssignments());
        assertNotNull(result.getCentroids());
        assertEquals(0, result.getClusterAssignments().length);
        assertEquals(0, result.getCentroids().size());
        assertEquals(0.0, result.getQuality(), 0.0001);
    }

    /**
     * Test objective: Test with multiple users and clusters
     * Required data files: Manually entered data. No additional files needed.
     * Values studied: Gray box strategy. Verifies a typical case with multiple users and clusters.
     * Operation: Create an AnalysisResult with 10 users distributed across 3 clusters.
     */
    @Test
    public void testMultipleUsersAndClusters() {
        // 10 users distributed in 3 clusters
        int[] assignments = {0, 0, 1, 1, 1, 2, 2, 0, 1, 2};
        ArrayList<ArrayList<AnswerToQuestion>> centroids = createMockCentroids(3);
        double quality = 0.82;
        
        AnalysisResult result = new AnalysisResult(assignments, centroids, quality);
        
        assertEquals(10, result.getClusterAssignments().length);
        assertEquals(3, result.getCentroids().size());
        assertEquals(0.82, result.getQuality(), 0.0001);
        
        // Verify specific assignments
        assertEquals(0, result.getClusterAssignments()[0]);
        assertEquals(1, result.getClusterAssignments()[2]);
        assertEquals(2, result.getClusterAssignments()[5]);
        
        // Verify centroids are not null
        assertNotNull(result.getCentroids().get(0));
        assertNotNull(result.getCentroids().get(1));
        assertNotNull(result.getCentroids().get(2));
    }

    /**
     * Test objective: Test that multiple getter calls return independent copies
     * Required data files: Manually entered data. No additional files needed.
     * Values studied: Gray box strategy. Verifies that each getter call returns a new copy.
     * Operation: Call getters multiple times and verify they are independent.
     */
    @Test
    public void testMultipleGettersReturnIndependentCopies() {
        int[] assignments = {0, 1, 2};
        ArrayList<ArrayList<AnswerToQuestion>> centroids = createMockCentroids(3);
        
        AnalysisResult result = new AnalysisResult(assignments, centroids, 0.9);
        
        // Get two copies
        int[] copy1 = result.getClusterAssignments();
        int[] copy2 = result.getClusterAssignments();
        
        // Modify first copy
        copy1[0] = 999;
        
        // Verify second copy is not affected
        assertEquals(0, copy2[0]);
        assertEquals(0, result.getClusterAssignments()[0]);
    }

    /**
     * Test objective: Test with all users in the same cluster
     * Required data files: Manually entered data. No additional files needed.
     * Values studied: Gray box strategy. Extreme case where there is only one cluster.
     * Operation: Create an AnalysisResult with all users assigned to cluster 0.
     */
    @Test
    public void testSingleCluster() {
        int[] assignments = {0, 0, 0, 0, 0};
        ArrayList<ArrayList<AnswerToQuestion>> centroids = createMockCentroids(1);
        
        AnalysisResult result = new AnalysisResult(assignments, centroids, 0.0);
        
        assertEquals(5, result.getClusterAssignments().length);
        assertEquals(1, result.getCentroids().size());
        
        // Verify all assigned to cluster 0
        for (int assignment : result.getClusterAssignments()) {
            assertEquals(0, assignment);
        }
    }

    /**
     * Test objective: Test with unbalanced clusters
     * Required data files: Manually entered data. No additional files needed.
     * Values studied: Gray box strategy. Case where clusters have different sizes.
     * Operation: Create an AnalysisResult with clusters of different sizes (1, 2, 7 users).
     */
    @Test
    public void testUnbalancedClusters() {
        // Cluster 0: 1 user, Cluster 1: 2 users, Cluster 2: 7 users
        int[] assignments = {2, 2, 1, 2, 2, 0, 1, 2, 2, 2};
        ArrayList<ArrayList<AnswerToQuestion>> centroids = createMockCentroids(3);
        double quality = 0.65;
        
        AnalysisResult result = new AnalysisResult(assignments, centroids, quality);
        
        assertArrayEquals(assignments, result.getClusterAssignments());
        assertEquals(3, result.getCentroids().size());
        assertEquals(0.65, result.getQuality(), 0.0001);
    }
    
    // Helper method to create mock centroids
    private ArrayList<ArrayList<AnswerToQuestion>> createMockCentroids(int numCentroids) {
        ArrayList<ArrayList<AnswerToQuestion>> centroids = new ArrayList<>();
        for (int i = 0; i < numCentroids; i++) {
            ArrayList<AnswerToQuestion> centroid = new ArrayList<>();
            // Add a mock answer for each centroid
            centroid.add(new AnswerFreeQuestion(i, 1, 0, "centroid" + i));
            centroids.add(centroid);
        }
        return centroids;
    }
}
