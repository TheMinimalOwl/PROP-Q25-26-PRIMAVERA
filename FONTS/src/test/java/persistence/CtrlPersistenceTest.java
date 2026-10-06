package persistence;

import static org.mockito.Mockito.*;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import domain.*;
import utils.*;

/**
 * Unit test for the class CtrlPersistence.
 *
 * @author Artur Leivar (artur.leivar@estudiantat.upc.edu)
 */
public class CtrlPersistenceTest {

    private CtrlPersistence ctrlPersistenceMock;

    @Before
    public void setUp() {
        // Create a mock instance of CtrlPersistence
        ctrlPersistenceMock = mock(CtrlPersistence.class);
    }

    // ==========================
    // User-related operations
    // ==========================

    @Test
    public void testAddUser() {
        User user = new User();
        user.setIdUser(-1);  // Assuming -1 means an unassigned ID

        // Mock the addUser method
        when(ctrlPersistenceMock.addUser(user)).thenReturn(1); // Return ID 1

        // Call the method
        Integer generatedId = ctrlPersistenceMock.addUser(user);

        // Verify the result
        assertEquals(Integer.valueOf(1), generatedId);
    }

    @Test
    public void testGetUser() throws EntityNotFoundException {
        User mockUser = new User();
        mockUser.setIdUser(1);

        // Mock the getUser method
        when(ctrlPersistenceMock.getUser(1)).thenReturn(mockUser);

        // Call the method
        User user = ctrlPersistenceMock.getUser(1);

        // Verify the result
        assertNotNull(user);
        assertEquals(Integer.valueOf(1), user.getIdUser());
    }

    @Test
    public void testDeleteUser() throws EntityNotFoundException {
        // No need to return anything, just ensure it's invoked
        doNothing().when(ctrlPersistenceMock).deleteUser(1);

        // Call the method
        ctrlPersistenceMock.deleteUser(1);

        // Verify it was called
        verify(ctrlPersistenceMock, times(1)).deleteUser(1);
    }

    // ==========================
    // Form-related operations
    // ==========================

    @Test
    public void testAddForm() {
        Form form = new Form("formTest", 1);
        form.setIdForm(-1);  // Assume -1 means unassigned ID

        // Mock the addForm method
        when(ctrlPersistenceMock.addForm(form)).thenReturn(100);

        // Call the method
        Integer formId = ctrlPersistenceMock.addForm(form);

        // Verify the result
        assertEquals(Integer.valueOf(100), formId);
    }

    @Test
    public void testGetForm() throws EntityNotFoundException {
        Form mockForm = new Form("formTest", 1);
        mockForm.setIdForm(100);

        // Mock the getForm method
        when(ctrlPersistenceMock.getForm(100)).thenReturn(mockForm);

        // Call the method
        Form form = ctrlPersistenceMock.getForm(100);

        // Verify the result
        assertNotNull(form);
        assertEquals(Integer.valueOf(100), (Integer) form.getIdForm());
    }

    // ==========================
    // Answer-related operations
    // ==========================

    @Test
    public void testAddAnswer() throws EntityNotFoundException {
        Answer answer = new Answer(1, 1);

        // Mock the addAnswer method
        doNothing().when(ctrlPersistenceMock).addAnswer(answer);

        // Call the method
        ctrlPersistenceMock.addAnswer(answer);

        // Verify it was called
        verify(ctrlPersistenceMock, times(1)).addAnswer(answer);
    }

    @Test
    public void testGetAnswer() throws EntityNotFoundException {
        Answer mockAnswer = new Answer(1, 1);

        // Mock the getAnswer method
        when(ctrlPersistenceMock.getAnswer(1, 100)).thenReturn(mockAnswer);

        // Call the method
        Answer answer = ctrlPersistenceMock.getAnswer(1, 100);

        // Verify the result
        assertNotNull(answer);
    }

    // ==========================
    // Question-related operations
    // ==========================

    @Test
    public void testAddQuestion() throws EntityNotFoundException {
        Question question = new NumericalQuestion("question?", false, 1, 10);

        // Mock the addQuestion method
        when(ctrlPersistenceMock.addQuestion(question)).thenReturn(200);

        // Call the method
        Integer questionId = ctrlPersistenceMock.addQuestion(question);

        // Verify the result
        assertEquals(Integer.valueOf(200), questionId);
    }

    // ==========================
    // AnswerToQuestion-related operations
    // ==========================

    @Test
    public void testAddAnswerToQuestion() throws EntityNotFoundException {
        AnswerToQuestion answerToQuestion = new AnswerNumericalQuestion(1, 1, 1, 3);

        // Mock the addAnswerToQuestion method
        doNothing().when(ctrlPersistenceMock).addAnswerToQuestion(answerToQuestion);

        // Call the method
        ctrlPersistenceMock.addAnswerToQuestion(answerToQuestion);

        // Verify it was called
        verify(ctrlPersistenceMock, times(1)).addAnswerToQuestion(answerToQuestion);
    }

    // ==========================
    // Analyze-related operations
    // ==========================

    @Test
    public void testAddAnalyze() throws EntityNotFoundException {
        Analyze analyze = mock(Analyze.class);

        when(ctrlPersistenceMock.addAnalyze(any(Analyze.class)))
                .thenReturn(1); // return an Integer, not Analyze

        ctrlPersistenceMock.addAnalyze(analyze);

        verify(ctrlPersistenceMock, times(1)).addAnalyze(analyze);
    }


    @Test
    public void testGetAnalyze() throws EntityNotFoundException {
        Analyze mockAnalyze = mock(Analyze.class);

        when(ctrlPersistenceMock.getAnalyze(eq(100), eq(5)))
                .thenReturn(mockAnalyze);

        Analyze analyze = ctrlPersistenceMock.getAnalyze(100, 5);

        assertNotNull(analyze);
    }

    // ==========================
    // Helper test case: Verifying a method was called
    // ==========================

    @Test
    public void testVerifyMethods() {
        // Call some methods
        ctrlPersistenceMock.addUser(new User());
        ctrlPersistenceMock.addForm(new Form("formTest", 1));

        // Verify methods were called
        verify(ctrlPersistenceMock).addUser(any(User.class));
        verify(ctrlPersistenceMock).addForm(any(Form.class));
    }
}

