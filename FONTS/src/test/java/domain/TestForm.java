package domain;
import org.junit.*;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Unit test for the class form
 * 
 * @author Tomeu Mestre (tomeu.mestre@estudiantat.upc.edu)
 */
public class TestForm {

    private Form form;
    private Integer testUser;
    private ArrayList<Integer> questions;
    private ArrayList<Integer> answers;
    private ArrayList<Integer> analyses;
    private Boolean isPublished;

    @Before
    public void setUp() {        
        // Initialize collections
        questions = new ArrayList<>();
        questions.add(1);
        questions.add(2);
        questions.add(3);
        
        answers = new ArrayList<>();
        answers.add(11);
        answers.add(12);
        
        testUser = 111;

        analyses = new ArrayList<>();
        analyses.add(1);

        isPublished = false;
        
        // Create form with constructor (id, name, userPropietary, questions, answers, analyses, isPublished)
        form = new Form(1, "Test Form", testUser, questions, answers, analyses, isPublished);
    }

    /**
     * Test objective: Test the default constructor of Form
     * Required data files: Manually entered data. No additional files needed.
     * Values studied: Gray box strategy. Creates a new Form object with the default constructor.
     * Operation: Create an empty Form and verify that idForm is initialized to -1.
     */
    // Default constructor is no longer used
    //@Test
    //public void testDefaultConstructor() {
    //    Form emptyForm = new Form();
    //    assertEquals(-1, emptyForm.getIdForm());
    //}

    /**
     * Test objective: Test the parameterized constructor of Form
     * Required data files: Manually entered data. No additional files needed.
     * Values studied: Gray box strategy. Creates a Form with all fields initialized.
     * Operation: Create a Form with data and verify that all fields are initialized correctly.
     */
    @Test
    public void testParameterizedConstructor() {
        assertEquals(1, form.getIdForm());
        assertEquals("Test Form", form.getName());
        assertEquals(testUser, form.getUserPropietary());
        assertEquals(questions, form.getQuestions());
        assertEquals(answers, form.getAnswers());
        assertEquals(analyses, form.getAnalyze());
        assertEquals(3, form.getQuestions().size());
        assertEquals(2, form.getAnswers().size());
        assertEquals(1, form.getAnalyze().size());
    }

    /**
     * Test objective: Test the getters and setters of Form
     * Required data files: Manually entered data. No additional files needed.
     * Values studied: Gray box strategy. Verifies that setters correctly update the fields.
     * Operation: Modify each field with setters and verify that getters return the correct values.
     */
    @Test
    public void testGettersAndSetters() {
        // Test setIdForm and getIdForm
        form.setIdForm(99);
        assertEquals(99, form.getIdForm());
        
        // Test setName and getName
        form.setName("New Form Name");
        assertEquals("New Form Name", form.getName());
        
        // Test setUserPropietary and getUserPropietary
        Integer newUser = 100;
        form.setUserPropietary(newUser);
        assertEquals(newUser, form.getUserPropietary());
        
        // Test setQuestions and getQuestions
        ArrayList<Integer> newQuestions = new ArrayList<>();
        newQuestions.add(10);
        newQuestions.add(20);
        form.setQuestions(newQuestions);
        assertEquals(newQuestions, form.getQuestions());
        assertEquals(2, form.getQuestions().size());
        
        // Test setAnswers and getAnswers
        ArrayList<Integer> newAnswers = new ArrayList<>();
        newAnswers.add(100);
        form.setAnswers(newAnswers);
        assertEquals(newAnswers, form.getAnswers());
        assertEquals(1, form.getAnswers().size());
        
        // Test setAnalyze and getAnalyze
        ArrayList<Integer> newAnalyses = new ArrayList<>();
        Integer newAnalysis = 2;
        newAnalyses.add(newAnalysis);
        form.setAnalyze(newAnalyses);
        assertEquals(newAnalyses, form.getAnalyze());
        assertEquals(1, form.getAnalyze().size());
    }

    /**
     * Test objective: Test the addQuestion() method
     * Required data files: Manually entered data. No additional files needed.
     * Values studied: Gray box strategy. Verifies that adding a question increases the list.
     * Operation: Add a new question and verify it has been added correctly.
     */
    @Test
    public void testAddQuestion() {
        int initialSize = form.getQuestions().size();
        form.addQuestion(4);
        assertEquals(initialSize + 1, form.getQuestions().size());
        assertTrue(form.getQuestions().contains(4));
    }

    /**
     * Test objective: Test the addAnswer() method
     * Required data files: Manually entered data. No additional files needed.
     * Values studied: Gray box strategy. Verifies that adding an answer increases the list.
     * Operation: Add a new answer and verify it has been added correctly.
     */
    @Test
    public void testAddAnswer() {
        int initialSize = form.getAnswers().size();
        form.addAnswer(103);
        assertEquals(initialSize + 1, form.getAnswers().size());
        assertTrue(form.getAnswers().contains(103));
    }

    /**
     * Test objective: Test the addAnalyze() method
     * Required data files: Manually entered data. No additional files needed.
     * Values studied: Gray box strategy. Verifies that adding an analysis increases the list.
     * Operation: Add a new analysis and verify it has been added correctly.
     */
    @Test
    public void testAddAnalyze() {
        Integer newAnalysis = 2;
        int initialSize = form.getAnalyze().size();
        form.addAnalyze(newAnalysis);
        assertEquals(initialSize + 1, form.getAnalyze().size());
        assertTrue(form.getAnalyze().contains(newAnalysis));
    }

    /**
     * Test objective: Test the removeQuestion() method
     * Required data files: Manually entered data. No additional files needed.
     * Values studied: Gray box strategy. Verifies that removing a question decreases the list.
     * Operation: Remove an existing question and verify it is no longer in the list.
     */
    @Test
    public void testRemoveQuestion() {
        int initialSize = form.getQuestions().size();
        assertTrue(form.getQuestions().contains(2));
        form.removeQuestion(Integer.valueOf(2));
        assertEquals(initialSize - 1, form.getQuestions().size());
        assertFalse(form.getQuestions().contains(2));
    }

    /**
     * Test objective: Test the removeAnswer() method
     * Required data files: Manually entered data. No additional files needed.
     * Values studied: Gray box strategy. Verifies that removing an answer decreases the list.
     * Operation: Remove an existing answer and verify it is no longer in the list.
     */
    @Test
    public void testRemoveAnswer() {
        int initialSize = form.getAnswers().size();
        assertTrue(form.getAnswers().contains(11));
        form.removeAnswer(Integer.valueOf(11));
        assertEquals(initialSize - 1, form.getAnswers().size());
        assertFalse(form.getAnswers().contains(11));
    }

    /**
     * Test objective: Test the removeAnalyze() method
     * Required data files: Manually entered data. No additional files needed.
     * Values studied: Gray box strategy. Verifies that removing an analysis decreases the list.
     * Operation: Add an analysis, remove it, and verify it is no longer in the list.
     */
    @Test
    public void testRemoveAnalyze() {
        Integer newAnalysis = 2;
        
        form.addAnalyze(newAnalysis);
        int initialSize = form.getAnalyze().size();
        assertTrue(form.getAnalyze().contains(newAnalysis));
        
        form.removeAnalyze(newAnalysis);
        assertEquals(initialSize - 1, form.getAnalyze().size());
        assertFalse(form.getAnalyze().contains(newAnalysis));
    }

    /**
     * Test objective: Test the equals() method
     * Required data files: Manually entered data. No additional files needed.
     * Values studied: Gray box strategy. Verifies that two forms with the same idForm are equal.
     * Operation: Create two forms with the same ID and verify that equals() returns true.
     */
    @Test
    public void testEquals() {
        Form form1 = new Form(1, "Form 1", testUser, questions, answers, analyses, isPublished);
        Form form2 = new Form(1, "Form 2", null, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false);
        Form form3 = new Form(2, "Form 3", testUser, questions, answers, analyses, isPublished);
        
        // Same ID - should be equal
        assertTrue(form1.equals(form2));
        assertTrue(form2.equals(form1));
        
        // Different ID - should not be equal
        assertFalse(form1.equals(form3));
        
        // Same object - should be equal
        assertTrue(form1.equals(form1));
        
        // Null - should not be equal
        assertFalse(form1.equals(null));
        
        // Different class - should not be equal
        assertFalse(form1.equals("Not a Form"));
    }

    /**
     * Test objective: Test the hashCode() method
     * Required data files: Manually entered data. No additional files needed.
     * Values studied: Gray box strategy. Verifies that two forms with the same idForm have the same hashCode.
     * Operation: Create two forms with the same ID and verify their hashCodes are equal.
     */
    @Test
    public void testHashCode() {
        Form form1 = new Form(5, "Form 1", testUser, questions, answers, analyses, isPublished);
        Form form2 = new Form(5, "Form 2", null, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false);
        Form form3 = new Form(10, "Form 3", testUser, questions, answers, analyses, isPublished);
        
        // Same ID - should have same hashCode
        assertEquals(form1.hashCode(), form2.hashCode());
        
        // Different ID - should have different hashCode
        assertNotEquals(form1.hashCode(), form3.hashCode());
    }

    /**
     * Test objective: Test the toString() method
     * Required data files: Manually entered data. No additional files needed.
     * Values studied: Gray box strategy. Verifies that toString() returns a correct representation of the form.
     * Operation: Call toString() and verify it contains the expected information.
     */
    @Test
    public void testToString() {
        String result = form.toString();
        
        assertNotNull(result);
        assertTrue(result.contains("Form{"));
        assertTrue(result.contains("idForm=1"));
        assertTrue(result.contains("name='Test Form'"));
        assertTrue(result.contains("userPropietary=Integer"));
        assertTrue(result.contains("questions=3"));
        assertTrue(result.contains("answers=2"));
        assertTrue(result.contains("analyses=1"));
    }

    /**
     * Test objective: Test toString() with null values
     * Required data files: Manually entered data. No additional files needed.
     * Values studied: Gray box strategy. Verifies that toString() handles null values correctly.
     * Operation: Create a form with null fields and verify that toString() does not throw exceptions.
     */
    @Test
    public void testToStringWithNullValues() {
        Form emptyForm = new Form(999, "Empty Form", null, null, null, null, false);
        String result = emptyForm.toString();
        
        assertNotNull(result);
        assertTrue(result.contains("idForm=999"));
        assertTrue(result.contains("name='Empty Form'"));
        assertTrue(result.contains("userPropietary=null"));
        assertTrue(result.contains("isPublished=false"));
        assertTrue(result.contains("questions=0"));
        assertTrue(result.contains("answers=0"));
        assertTrue(result.contains("analyses=0"));
    }

    /**
     * Test objective: Test adding multiple questions
     * Required data files: Manually entered data. No additional files needed.
     * Values studied: Gray box strategy. Verifies that multiple questions can be added consecutively.
     * Operation: Add several questions and verify that all are added correctly.
     */
    @Test
    public void testAddMultipleQuestions() {
        int initialSize = form.getQuestions().size();
        form.addQuestion(10);
        form.addQuestion(20);
        form.addQuestion(30);
        
        assertEquals(initialSize + 3, form.getQuestions().size());
        assertTrue(form.getQuestions().contains(10));
        assertTrue(form.getQuestions().contains(20));
        assertTrue(form.getQuestions().contains(30));
    }

    /**
     * Test objective: Test adding multiple analyses
     * Required data files: Manually entered data. No additional files needed.
     * Values studied: Gray box strategy. Verifies that multiple analyses can be added consecutively.
     * Operation: Add several analyses and verify that all are added correctly in order.
     */
    @Test
    public void testAddMultipleAnalyses() {
        Integer analysis1 = 10;
        Integer analysis2 = 20;
        Integer analysis3 = 30;
        
        int iniSize = form.getAnalyze().size();

        form.addAnalyze(analysis1);
        form.addAnalyze(analysis2);
        form.addAnalyze(analysis3);
        
        assertEquals(iniSize + 3, form.getAnalyze().size());
        assertEquals(analysis1, form.getAnalyze().get(iniSize + 0));
        assertEquals(analysis2, form.getAnalyze().get(iniSize + 1));
        assertEquals(analysis3, form.getAnalyze().get(iniSize + 2));
    }

    /**
     * Test objective: Test list modification after creating the form
     * Required data files: Manually entered data. No additional files needed.
     * Values studied: Gray box strategy. Verifies that lists can be modified after creation.
     * Operation: Create a form, modify its lists, and verify the changes.
     */
    @Test
    public void testListModificationAfterCreation() {
        Form testForm = new Form(100, "Test", testUser, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), isPublished);
        
        // Initially empty
        assertEquals(0, testForm.getQuestions().size());
        assertEquals(0, testForm.getAnswers().size());
        assertEquals(0, testForm.getAnalyze().size());
        
        // Add elements
        testForm.addQuestion(1);
        testForm.addAnswer(100);
        testForm.addAnalyze(1);
        
        // Verify additions
        assertEquals(1, testForm.getQuestions().size());
        assertEquals(1, testForm.getAnswers().size());
        assertEquals(1, testForm.getAnalyze().size());
    }

    @Test
    public void testIsPublishedTrue() {
        Form testForm = new Form(100, "Test", testUser, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), true);
        
        assertTrue(testForm.isPublished());
    }

    @Test
    public void testIsPublished() {
        Form testForm = new Form(100, "Test", testUser, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false);
        
        assertFalse(testForm.isPublished());
    }

    @Test
    public void testPublishUnpublish() {
        Form testForm = new Form(100, "Test", testUser, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false);
        
        assertFalse(testForm.isPublished());
        
        testForm.publish();

        assertTrue(testForm.isPublished());

        testForm.publish();

        assertTrue(testForm.isPublished());

        testForm.unpublish();

        assertFalse(testForm.isPublished());
        
        testForm.unpublish();

        assertFalse(testForm.isPublished());

        testForm.publish();

        assertTrue(testForm.isPublished());
    }

    /**
     * Test objective: Test that questions cannot be added or removed when the form is published
     * Required data files: Manually entered data. No additional files needed.
     * Values studied: Gray box strategy. Verifies that modifications fail when isPublished is true.
     * Operation: Publish the form and try to add/remove questions.
     */
    @Test
    public void testModificationOnPublishedForm() {
        form.publish();
        assertTrue(form.isPublished());
        
        int initialQuestions = form.getQuestions().size();
        
        // Try to add question
        boolean added = form.addQuestion(999);
        assertFalse("Should not be able to add question to published form", added);
        assertEquals(initialQuestions, form.getQuestions().size());
        
        // Try to remove question
        boolean removed = form.removeQuestion(Integer.valueOf(1));
        assertFalse("Should not be able to remove question from published form", removed);
        assertEquals(initialQuestions, form.getQuestions().size());
        
        // Answers can still be modified
        int initialAnswers = form.getAnswers().size();
        form.addAnswer(999);
        assertEquals(initialAnswers + 1, form.getAnswers().size());
    }
}
