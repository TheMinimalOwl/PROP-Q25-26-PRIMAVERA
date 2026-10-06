package domain;

import org.junit.*;

import static org.junit.Assert.*;

import java.util.ArrayList;

public class TestAnswer {

    private Answer answer;

    @Before
    public void setUp() {
        // Create an Answer for formId=1 and userId=10
        answer = new Answer(1, 10);
    }

    /**
     * Test object: Test the constructor of Answer
     * Required data files: Data entered manually. No additional files needed.
     * Values studied: Gray-box strategy. A new Answer object is created with form and user IDs.
     * Operation: Create an Answer and check that the fields form, user, time, and responses are initialized correctly.
     */
    @Test
    public void testConstructorInitializesFields() {
        assertEquals(Integer.valueOf(1), answer.getIdForm());
        assertEquals(Integer.valueOf(10), answer.getIdUser());
        assertNotNull(answer.getTime());
        assertTrue(answer.getResponses().isEmpty());
    }

    /**
     * Test object: Test the addResponse() method
     * Operation: Add an answered question and check that it exists in the list.
     */
    @Test
    public void testAddResponse() {
        answer.addResponse(100);
        assertTrue(answer.existsResponseToQuestion(100));
        assertTrue(answer.getResponses().contains(100));
    }

    /**
     * Test object: Test the deleteResponse() method
     * Operation: Add a question, delete it, and check that it no longer exists.
     */
    @Test
    public void testDeleteResponse() {
        answer.addResponse(100);
        answer.deleteResponse(100);
        assertFalse(answer.existsResponseToQuestion(100));
    }

    /**
     * Test object: Test the isComplete() method
     * Operation: Add responses to all questions of a simulated form and check that isComplete() returns true.
     */
    @Test
    public void testIsComplete() {
        answer.addResponse(1);
        answer.addResponse(2);
        assertTrue(answer.isComplete(2));

        answer.deleteResponse(2);
        assertFalse(answer.isComplete(2));
    }

    /**
     * Test object: Test the existsResponseToQuestion() method with the false case
     * Operation: Check that a question without a response returns false.
     */
    @Test
    public void testExistsResponseToQuestionFalse() {
        assertFalse(answer.existsResponseToQuestion(999));
    }

    /**
     * Test object: Test the importResponses() method with valid data
     * Operation: Create a list of questions, import them, and check that they were added correctly.
     */
    @Test
    public void testImportResponsesValid() {
        ArrayList<Integer> imported = new ArrayList<>();
        imported.add(1);
        imported.add(2);

        answer.importResponses(imported);

        assertTrue(answer.existsResponseToQuestion(1));
        assertTrue(answer.existsResponseToQuestion(2));
    }

    /**
     * Test object: Test the importResponses() method with an empty list
     * Operation: Import an empty list and check that responses are not modified.
     */
    @Test
    public void testImportResponsesEmptyList() {
        ArrayList<Integer> emptyList = new ArrayList<>();
        answer.importResponses(emptyList);
        assertTrue(answer.getResponses().isEmpty());
    }
}
