package persistence;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import domain.Answer;
import domain.AnswerFreeQuestion;
import domain.AnswerToQuestion;
import domain.Form;
import domain.Question;
import domain.FreeQuestion;
import domain.RegisteredUser;
import utils.EntityNotFoundException;

/**
 * Unit test for the class CtrlAnswerQuestion.
 *
 * @author Artur Leivar (artur.leivar@estudiantat.upc.edu)
 */
public class CtrlAnswerQuestionPersistenceTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    private CtrlAnswerQuestion ctrlAnswerQuestion;
    private CtrlUser ctrlUser;
    private CtrlFormQuestion ctrlFormQuestion;
    
    private Integer validUserId;
    private Integer validFormId;
    private Integer validQuestionId;

    private PropertySetter p = new PropertySetter();

    @Before
    public void setUp() throws IOException {
        p.set(tempDir.getRoot().toPath());

        ctrlUser = CtrlUser.getInstance();
        ctrlUser.resetForTests();
        ctrlUser.clear();
        
        ctrlFormQuestion = CtrlFormQuestion.getInstance();
        ctrlFormQuestion.resetForTests();
        ctrlFormQuestion.clear();
        
        ctrlAnswerQuestion = CtrlAnswerQuestion.getInstance();
        ctrlAnswerQuestion.resetForTests();
        ctrlAnswerQuestion.clear();
        
        // Create dummy User
        RegisteredUser user = new RegisteredUser("testuser", "pass1234", "test@example.com");
        validUserId = ctrlUser.addUser(user);
        
        // Create dummy Form
        Form form = new Form("Test Form", validUserId);
        validFormId = ctrlFormQuestion.addForm(form);

        Question question = new FreeQuestion("testQuestion", false, 1);
        question.setIdForm(validFormId);
        validQuestionId = ctrlFormQuestion.addQuestion(question);
    }

    @After
    public void tearDown() {
        p.restore();

        ctrlAnswerQuestion.resetForTests();
        ctrlFormQuestion.resetForTests();
        ctrlUser.resetForTests();
    }

    @Test
    public void testSaveAndLoadAnswer() throws IOException, EntityNotFoundException {
        Answer answer = new Answer(validFormId, validUserId);
        
        ctrlAnswerQuestion.addAnswer(answer);
        
        // Clear cache
        ctrlAnswerQuestion.close();
        
        Answer loaded = ctrlAnswerQuestion.getAnswer(validUserId, validFormId);
        
        assertNotNull(loaded);
        assertEquals(validUserId, loaded.getIdUser());
        assertEquals(validFormId, loaded.getIdForm());
    }
    
    @Test
    public void testSaveAndLoadAnswerToQuestion() throws IOException, EntityNotFoundException {
        AnswerToQuestion atq = new AnswerFreeQuestion(validUserId, validFormId, validQuestionId, "My Answer");

        ctrlAnswerQuestion.addAnswerToQuestion(atq);

        // Clear cache
        ctrlAnswerQuestion.close();

        AnswerToQuestion loaded = ctrlAnswerQuestion.getAnswerToQuestion(validUserId, validFormId, validQuestionId);

        assertNotNull(loaded);
        assertTrue(loaded instanceof AnswerFreeQuestion);
        assertEquals("My Answer", ((AnswerFreeQuestion)loaded).getAnswerText());
        assertEquals(validUserId.intValue(), loaded.getIdUser());
        assertEquals(validFormId.intValue(), loaded.getIdForm());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testDeleteAnswer() throws IOException, FileNotFoundException {
        Answer answer = new Answer(validFormId, validUserId);
        ctrlAnswerQuestion.addAnswer(answer);
        
        ctrlAnswerQuestion.deleteAnswer(validUserId, validFormId);
        
        ctrlAnswerQuestion.getAnswer(validUserId, validFormId);
    }
    
    @Test(expected = EntityNotFoundException.class)
    public void testDeleteAnswerToQuestion() throws IOException, EntityNotFoundException {
        AnswerToQuestion atq = new AnswerFreeQuestion(validUserId, validFormId, validQuestionId, "My Answer");
        ctrlAnswerQuestion.addAnswerToQuestion(atq);
        
        ctrlAnswerQuestion.deleteAnswerToQuestion(validUserId, validFormId, validQuestionId);
        
        ctrlAnswerQuestion.getAnswerToQuestion(validUserId, validFormId, validQuestionId);
    }
    
    @Test(expected = EntityNotFoundException.class)
    public void testAddAnswerWithInvalidUser() throws IOException, EntityNotFoundException {
        Answer answer = new Answer(validFormId, 9999); // Invalid User ID
        ctrlAnswerQuestion.addAnswer(answer);
    }
    
    @Test(expected = EntityNotFoundException.class)
    public void testAddAnswerWithInvalidForm() throws IOException, EntityNotFoundException {
        Answer answer = new Answer(9999, validUserId); // Invalid Form ID
        ctrlAnswerQuestion.addAnswer(answer);
    }
}
