package domain;

import org.junit.*;
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Unit tests for Analyze
 * 
 * @author Tomeu Mestre (tomeu.mestre@estudiantat.upc.edu)
 */
public class TestAnalyze {

    private ArrayList<ArrayList<AnswerToQuestion>> sampleAnswers;
    private MockStrategy mockStrategy;

    @Before
    public void setUp() {
        // Create sample answers data (3 users, 3 questions each: numerical, single choice, and free text)
        sampleAnswers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ArrayList<AnswerToQuestion> userAnswers = new ArrayList<>();
            // Question 0: Numerical answer (idUser=i, idForm=1, idQuestion=0, value=10+i*5)
            userAnswers.add(new AnswerNumericalQuestion(i, 1, 0, 10 + i * 5));
            // Question 1: Single choice answer (idUser=i, idForm=1, idQuestion=1, selectedOption=i)
            userAnswers.add(new AnswerSingleChoiceQuestion(i, 1, 1, i));
            // Question 2: Free text answer (idUser=i, idForm=1, idQuestion=2, text="Answer from user i")
            userAnswers.add(new AnswerFreeQuestion(i, 1, 2, "Answer from user " + i));
            sampleAnswers.add(userAnswers);
        }
        
        // Create a mock strategy that returns predictable results
        mockStrategy = new MockStrategy();
    }

    /**
     * Test objective: Test the Analyze constructor
     * Required data files: Manually entered data. No additional files needed.
     * Values studied: Gray box strategy. Creates an Analyze and verifies all fields are initialized correctly.
     * Operation: Create an Analyze with sample data and verify the attributes are initialized correctly.
     */
    @Test
    public void testConstructorInitializesFields() {
        Analyze analysis = new Analyze(1, 1, "Test Analysis", 2, mockStrategy, sampleAnswers);
        
        assertNotNull(analysis.getTime());
        assertEquals("Test Analysis", analysis.getName());
        assertEquals(2, analysis.getNClusters());
        assertNotNull(analysis.getResult());
        assertEquals(Integer.valueOf(1), analysis.getIdAnalyze());
        assertEquals(Integer.valueOf(1), analysis.getIdForm());
    }

    /**
     * Test objective: Test that the constructor executes the analysis automatically
     * Required data files: Manually entered data. No additional files needed.
     * Values studied: Gray box strategy. Verifies that the constructor calls doAnalysis.
     * Operation: Create an Analyze and verify that the result is already available.
     */
    @Test
    public void testConstructorExecutesAnalysis() {
        Analyze analysis = new Analyze(1, 1, "Test Analysis", 2, mockStrategy, sampleAnswers);
        
        AnalysisResult result = analysis.getResult();
        assertNotNull(result);
        assertTrue(mockStrategy.wasExecuted);
    }

    /**
     * Test objective: Test the getters and setters of Analyze
     * Required data files: Manually entered data. No additional files needed.
     * Values studied: Gray box strategy. Verifies that setters correctly update the values.
     * Operation: Create an Analyze, modify fields with setters, and verify with getters.
     */
    @Test
    public void testGettersAndSetters() {
        Analyze analysis = new Analyze(1, 1, "Original Name", 2, mockStrategy, sampleAnswers);
        
        // Test setName and getName
        analysis.setName("New Name");
        assertEquals("New Name", analysis.getName());
        
        // Test setTime and getTime
        LocalDateTime newTime = LocalDateTime.now().plusDays(1);
        analysis.setTime(newTime);
        assertEquals(newTime, analysis.getTime());
        
        // Test setIdForm and getIdForm
        analysis.setIdForm(42);
        assertEquals(Integer.valueOf(42), analysis.getIdForm());
        
        // Test setIdAnalyze and getIdAnalyze
        analysis.setIdAnalyze(99);
        assertEquals(Integer.valueOf(99), analysis.getIdAnalyze());

        // Test getNClusters (no setter as mentioned in comment)
        assertEquals(2, analysis.getNClusters());
    }

    /**
     * Test objective: Test that getResult returns the correct result
     * Required data files: Manually entered data. No additional files needed.
     * Values studied: Gray box strategy. Verifies that the result contains the correct data.
     * Operation: Create an Analyze with MockStrategy and verify the result is correct.
     */
    @Test
    public void testGetResultReturnsCorrectResult() {
        Analyze analysis = new Analyze(1, 1, "Test", 2, mockStrategy, sampleAnswers);
        
        AnalysisResult result = analysis.getResult();
        assertNotNull(result);
        assertEquals(0.85, result.getQuality(), 0.001);
    }

    /**
     * Test objective: Test with different numbers of clusters
     * Required data files: Manually entered data. No additional files needed.
     * Values studied: Gray box strategy. Verifies that analyses can be created with different k values.
     * Operation: Create multiple Analyze instances with different nClusters values.
     */
    @Test
    public void testDifferentClusterCounts() {
        for (int k = 1; k <= 3; k++) {
            Analyze analysis = new Analyze(1, 1, "Test K=" + k, k, mockStrategy, sampleAnswers);
            assertEquals(k, analysis.getNClusters());
            assertNotNull(analysis.getResult());
        }
    }

    /**
     * Test objective: Test that idForm is initialized to null by default
     * Required data files: Manually entered data. No additional files needed.
     * Values studied: Gray box strategy. Verifies that idForm has no initial value.
     * Operation: Create an Analyze and verify that idForm is null before being assigned.
     */
    @Test
    public void testIdFormInitiallyNull() {
        Analyze analysis = new Analyze(1, null, "Test", 2, mockStrategy, sampleAnswers);
        assertNull(analysis.getIdForm());
    }

    /**
     * Test objective: Test with different strategies
     * Required data files: Manually entered data. No additional files needed.
     * Values studied: Gray box strategy. Verifies that Analyze works with different strategies.
     * Operation: Create Analyze instances with different strategies and verify they work.
     */
    @Test
    public void testWithDifferentStrategies() {
        MockStrategy strategy1 = new MockStrategy();
        MockStrategy strategy2 = new MockStrategy();
        
        Analyze analysis1 = new Analyze(1, 1, "Analysis 1", 2, strategy1, sampleAnswers);
        Analyze analysis2 = new Analyze(2, 1, "Analysis 2", 3, strategy2, sampleAnswers);
        
        assertTrue(strategy1.wasExecuted);
        assertTrue(strategy2.wasExecuted);
        assertNotNull(analysis1.getResult());
        assertNotNull(analysis2.getResult());
    }

    /**
     * Mock strategy class for testing
     * Returns predictable results without performing actual calculations
     */
    private static class MockStrategy extends AnalyzeStrategy {
        boolean wasExecuted = false;

        @Override
        public AnalysisResult doAnalysis(ArrayList<ArrayList<AnswerToQuestion>> answers, int nClusters, Integer idForm) {
            wasExecuted = true;
            
            // Create mock cluster assignments (all users to cluster 0)
            int[] assignments = new int[answers.size()];
            for (int i = 0; i < assignments.length; i++) {
                assignments[i] = i % nClusters;
            }
            
            // Create mock centroids (use first nClusters users as centroids)
            ArrayList<ArrayList<AnswerToQuestion>> centroids = new ArrayList<>();
            for (int i = 0; i < nClusters && i < answers.size(); i++) {
                centroids.add(answers.get(i));
            }
            
            // Return mock result with quality 0.85
            return new AnalysisResult(assignments, centroids, 0.85);
        }
    }
}
