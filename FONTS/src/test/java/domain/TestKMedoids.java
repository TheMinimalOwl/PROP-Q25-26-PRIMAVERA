package domain;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.ArrayList;

/**
 * Unit tests for KMedoidsStrategy
 * Tests the PAM (Partitioning Around Medoids) clustering algorithm
 * 
 * @author Tomeu Mestre (tomeu.mestre@estudiantat.upc.edu)
 */
public class TestKMedoids {

    private KMedoidsStrategy strategy;

    @Before
    public void setUp() {
        strategy = KMedoidsStrategy.getInstance();
    }

    /**
     * Test objective: Verify that KMedoids correctly clusters similar text data into distinct groups
     * Required data files: None - manually created data
     * Values studied: Gray box - clustering correctness with well-separated groups
     * Operation: Create 6 users with 2 clear groups (animal-related words) and verify they cluster correctly
     */
    @Test
    public void testClusteringSeparatesDistinctGroups() {
        // Create 6 users with clear separation:
        // Group 1: related words (hola, hole, holuu)
        // Group 2: related words (abcdef, abcdaa, abcdea)

        // We use free question type of answer so we don't have to initialize questions
        ArrayList<ArrayList<AnswerToQuestion>> answers = new ArrayList<>();
        String[] texts = {"hola", "hole", "holuu", "abcdef", "abcdaa", "abcdea"};
        
        for (int i = 0; i < 6; i++) {
            ArrayList<AnswerToQuestion> userAnswers = new ArrayList<>();
            userAnswers.add(new AnswerFreeQuestion(i, 1, 0, texts[i]));
            answers.add(userAnswers);
        }

        // Run clustering with k=2
        AnalysisResult result = strategy.doAnalysis(answers, 2, 1);
        
        // Verify basic structure
        assertNotNull("Result should not be null", result);
        assertEquals("Should have 6 cluster assignments", 6, result.getClusterAssignments().length);
        assertEquals("Should have 2 centroids", 2, result.getCentroids().size());
        
        // Verify quality is in valid range [-1, 1]
        assertTrue("Quality should be >= -1", result.getQuality() >= -1.0);
        assertTrue("Quality should be <= 1", result.getQuality() <= 1.0);
        
        // For well-separated groups, quality should be positive
        assertTrue("Quality should be positive for well-separated groups", result.getQuality() > 0);
        
        // Verify clustering logic: similar words should be in the same cluster
        int[] assignments = result.getClusterAssignments();
        
        // Indices 0, 1, 2 are related (should be in same cluster)
        int ans0 = assignments[0];
        assertEquals("ans1 should be in the same cluster as ans2", ans0, assignments[1]);
        assertEquals("ans1 should be in the same cluster as ans3", ans0, assignments[2]);
        
        // Indices 3, 4, 5 are related (should be in the other cluster)
        int ans3 = assignments[3];
        assertEquals("ans1 should be in the same cluster as ans2", ans3, assignments[4]);
        assertEquals("ans1 should be in the same cluster as ans3", ans3, assignments[5]);
        
        // Cat and dog clusters should be different
        assertNotEquals("ans1 and ans2 should not be in the same cluster", ans0, ans3);
        
        System.out.println("  Quality: " + result.getQuality());
    }

    /**
     * Test objective: Verify KMedoids clusters users with multiple answers per user correctly
     * Required data files: None - manually created data
     * Values studied: Gray box - clustering with multiple questions per user
     * Operation: Create 6 users with 3 answers each, forming 2 distinct groups based on preferences
     */
    @Test
    public void testClusteringWithMultipleAnswersPerUser() {
        // Create 6 users with 3 answers each
        ArrayList<ArrayList<AnswerToQuestion>> answers = new ArrayList<>();
        
        // Group 1: words with a lot of h
        // User 0: hhhi, hehhy, 
        ArrayList<AnswerToQuestion> user0 = new ArrayList<>();
        user0.add(new AnswerFreeQuestion(0, 1, 0, "hhhi"));
        user0.add(new AnswerFreeQuestion(0, 1, 1, "hehhy"));
        user0.add(new AnswerFreeQuestion(0, 1, 2, "hhhh"));
        answers.add(user0);
        
        // User 1: hohha, adheh, hhhes
        ArrayList<AnswerToQuestion> user1 = new ArrayList<>();
        user1.add(new AnswerFreeQuestion(1, 1, 0, "hohha"));
        user1.add(new AnswerFreeQuestion(1, 1, 1, "adheh"));
        user1.add(new AnswerFreeQuestion(1, 1, 2, "hhhes"));
        answers.add(user1);
        
        // User 2: hash, hhh, hlh
        ArrayList<AnswerToQuestion> user2 = new ArrayList<>();
        user2.add(new AnswerFreeQuestion(2, 1, 0, "hash"));
        user2.add(new AnswerFreeQuestion(2, 1, 1, "hhh"));
        user2.add(new AnswerFreeQuestion(2, 1, 2, "hlh"));
        answers.add(user2);
        
        // Group 2: words with a lot of a
        // User 3: aaaa, asdaa, asdffdaaa
        ArrayList<AnswerToQuestion> user3 = new ArrayList<>();
        user3.add(new AnswerFreeQuestion(3, 1, 0, "aaaa"));
        user3.add(new AnswerFreeQuestion(3, 1, 1, "asdaa"));
        user3.add(new AnswerFreeQuestion(3, 1, 2, "asdffdaaa"));
        answers.add(user3);
        
        // User 4: aasa, saaas, sdaaa
        ArrayList<AnswerToQuestion> user4 = new ArrayList<>();
        user4.add(new AnswerFreeQuestion(4, 1, 0, "aasa"));
        user4.add(new AnswerFreeQuestion(4, 1, 1, "saaas"));
        user4.add(new AnswerFreeQuestion(4, 1, 2, "sdaaa"));
        answers.add(user4);
        
        // User 5: aaswa, aaassa, aasda
        ArrayList<AnswerToQuestion> user5 = new ArrayList<>();
        user5.add(new AnswerFreeQuestion(5, 1, 0, "aaswa"));
        user5.add(new AnswerFreeQuestion(5, 1, 1, "aaassa"));
        user5.add(new AnswerFreeQuestion(5, 1, 2, "aasda"));
        answers.add(user5);

        // Run clustering with k=2
        AnalysisResult result = strategy.doAnalysis(answers, 2, 1);
        
        
        // Verify basic structure
        assertNotNull("Result should not be null", result);
        assertEquals("Should have 6 cluster assignments", 6, result.getClusterAssignments().length);
        assertEquals("Should have 2 centroids", 2, result.getCentroids().size());
        
        // Verify quality is positive for well-separated groups
        assertTrue("Quality should be >= -1", result.getQuality() >= -1.0);
        assertTrue("Quality should be <= 1", result.getQuality() <= 1.0);
        assertTrue("Quality should be positive for well-separated groups", result.getQuality() > 0);
        
        // Verify clustering logic: similar preference profiles should cluster together
        int[] assignments = result.getClusterAssignments();

        
        System.out.println("  Quality: " + result.getQuality());
        for (int i = 0; i < 6; i++) {
            System.out.println("User " + i + " cluster --> " + assignments[i]);
        }
        
        int shortCluster = assignments[0];
        int largeCluster = assignments[3];
        // Users 0, 1, 2 should be in the same cluster
        assertEquals("User 1 should be in same cluster as User 0", shortCluster, assignments[1]);
        assertEquals("User 2 should be in same cluster as User 0", shortCluster, assignments[2]);
        
        // Users 3, 4, 5 should be in the same cluster
        assertEquals("User 4 should be in same cluster as User 3", largeCluster, assignments[4]);
        assertEquals("User 5 should be in same cluster as User 3", largeCluster, assignments[5]);        
    }

    /**
     * Test objective: Test singleton pattern
     * Required data files: None
     * Values studied: Black box - singleton implementation
     * Operation: Verify getInstance() returns the same instance
     */
    @Test
    public void testSingletonPattern() {
        KMedoidsStrategy instance1 = KMedoidsStrategy.getInstance();
        KMedoidsStrategy instance2 = KMedoidsStrategy.getInstance();
        
        assertSame("getInstance() should return the same instance", instance1, instance2);
    }

    /**
     * Test objective: Test with single cluster (edge case)
     * Required data files: None - manually created data
     * Values studied: Edge case - k=1
     * Operation: Verify all users assigned to cluster 0
     */
    @Test
    public void testSingleCluster() {
        ArrayList<ArrayList<AnswerToQuestion>> answers = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            ArrayList<AnswerToQuestion> userAnswers = new ArrayList<>();
            userAnswers.add(new AnswerFreeQuestion(i, 1, 0, "text" + i));
            answers.add(userAnswers);
        }

        AnalysisResult result = strategy.doAnalysis(answers, 1, 1);
        
        assertNotNull("Result should not be null", result);
        assertEquals("Should have 1 centroid", 1, result.getCentroids().size());
        
        // All users should be in cluster 0
        int[] assignments = result.getClusterAssignments();
        for (int i = 0; i < assignments.length; i++) {
            assertEquals("All users should be in cluster 0", 0, assignments[i]);
        }
    }

    /**
     * Test objective: Test maxIterations configuration
     * Required data files: None
     * Values studied: Black box - configuration
     * Operation: Test getter/setter for maxIterations
     */
    @Test
    public void testMaxIterationsConfiguration() {
        assertEquals("Default max iterations should be 100", 100, strategy.getMaxIterations());
        
        strategy.setMaxIterations(50);
        assertEquals("Max iterations should be 50", 50, strategy.getMaxIterations());
        
        // Reset to default
        strategy.setMaxIterations(100);
    }

    /**
     * Test objective: Test invalid maxIterations
     * Required data files: None
     * Values studied: Black box - input validation
     * Operation: Verify exception thrown for invalid values
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidMaxIterations() {
        strategy.setMaxIterations(0);
    }

    /**
     * Test objective: Test with identical data points
     * Required data files: None - manually created data
     * Values studied: Edge case - no separation between points
     * Operation: Verify algorithm handles identical data
     */
    @Test
    public void testIdenticalDataPoints() {
        ArrayList<ArrayList<AnswerToQuestion>> answers = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            ArrayList<AnswerToQuestion> userAnswers = new ArrayList<>();
            userAnswers.add(new AnswerFreeQuestion(i, 1, 0, "same"));
            answers.add(userAnswers);
        }

        AnalysisResult result = strategy.doAnalysis(answers, 2, 1);
        
        assertNotNull("Result should not be null", result);
        assertTrue("Quality should be in valid range", result.getQuality() >= -1.0 && result.getQuality() <= 1.0);
        
        // With identical points, quality should be around 0
        assertTrue("Quality should be near 0 for identical points", Math.abs(result.getQuality()) < 0.1);
    }
}
