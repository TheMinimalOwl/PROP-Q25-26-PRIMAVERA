package persistence;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import domain.Analyze;
import domain.AnswerFreeQuestion;
import domain.AnswerToQuestion;
import domain.Form;
import domain.KMeansStrategy;
import utils.EntityNotFoundException;

/**
 * Unit test for the class CtrlAnalyze.
 *
 * @author Artur Leivar (artur.leivar@estudiantat.upc.edu)
 */
public class CtrlAnalyzePersistenceTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    private CtrlAnalyze ctrlAnalyze;
    private CtrlFormQuestion ctrlFormQuestion;
    private Integer validFormId;

    private PropertySetter p = new PropertySetter();

    @Before
    public void setUp() throws IOException {
        p.set(tempDir.getRoot().toPath());

        ctrlAnalyze = CtrlAnalyze.getInstance();
        ctrlAnalyze.resetForTests();
        ctrlAnalyze.clear();
        
        ctrlFormQuestion = CtrlFormQuestion.getInstance();
        ctrlFormQuestion.resetForTests();
        ctrlFormQuestion.clear();
        
        // Create a dummy form to attach analyzes to
        Form form = new Form("Test Form", 1);
        validFormId = ctrlFormQuestion.addForm(form);
    }

    @After
    public void tearDown() {
        p.restore();

        ctrlFormQuestion.resetForTests();
        ctrlAnalyze.resetForTests();
    }

    @Test
    public void testSaveAndLoadSingleAnalyze() throws IOException, EntityNotFoundException {
        // Prepare dummy data
        ArrayList<ArrayList<AnswerToQuestion>> answers = new ArrayList<>();
        ArrayList<AnswerToQuestion> row = new ArrayList<>();
        // Use valid constructor: idUser, idForm, idQuestion, answerText
        row.add(new AnswerFreeQuestion(1, validFormId, 1, "test answer"));
        answers.add(row);
        
        Analyze analyze = new Analyze(validFormId, "Test Analyze", 1, KMeansStrategy.getInstance(), answers);
        
        // Save
        Integer idAnalyze = ctrlAnalyze.addAnalyze(analyze);
        
        // Clear cache
        ctrlAnalyze.close();
        
        // Load
        Analyze loadedAnalyze = ctrlAnalyze.getAnalyze(validFormId, idAnalyze);
        
        // Verify
        assertNotNull(loadedAnalyze);
        assertEquals("Test Analyze", loadedAnalyze.getName());
        assertEquals(validFormId, loadedAnalyze.getIdForm());
        assertEquals(1, loadedAnalyze.getNClusters());
    }
    
    @Test
    public void testSaveAndLoadMultipleAnalyzes() throws IOException, EntityNotFoundException {
        ArrayList<ArrayList<AnswerToQuestion>> answers = new ArrayList<>();
        ArrayList<AnswerToQuestion> row = new ArrayList<>();
        row.add(new AnswerFreeQuestion(1, validFormId, 1, "test answer"));
        answers.add(row);

        Analyze a1 = new Analyze(validFormId, "Analyze 1", 1, KMeansStrategy.getInstance(), answers);
        Analyze a2 = new Analyze(validFormId, "Analyze 2", 1, KMeansStrategy.getInstance(), answers);
        
        Integer id1 = ctrlAnalyze.addAnalyze(a1);
        Integer id2 = ctrlAnalyze.addAnalyze(a2);
        
        ctrlAnalyze.close();
        
        Analyze loaded1 = ctrlAnalyze.getAnalyze(validFormId, id1);
        Analyze loaded2 = ctrlAnalyze.getAnalyze(validFormId, id2);
        
        assertEquals("Analyze 1", loaded1.getName());
        assertEquals("Analyze 2", loaded2.getName());
    }

    @Test(expected = FileNotFoundException.class)
    public void testDeleteAnalyze() throws IOException, FileNotFoundException {
        ArrayList<ArrayList<AnswerToQuestion>> answers = new ArrayList<>();
        ArrayList<AnswerToQuestion> row = new ArrayList<>();
        row.add(new AnswerFreeQuestion(1, validFormId, 1, "test answer"));
        answers.add(row);
        
        Analyze analyze = new Analyze(validFormId, "To Delete", 1, KMeansStrategy.getInstance(), answers);
        Integer id = ctrlAnalyze.addAnalyze(analyze);
        
        ctrlAnalyze.deleteAnalyze(validFormId, id);
        
        // Should throw EntityNotFoundException
        ctrlAnalyze.getAnalyze(validFormId, id);
    }
    
    @Test(expected = EntityNotFoundException.class)
    public void testAddAnalyzeToNonExistentForm() throws IOException, EntityNotFoundException {
        ArrayList<ArrayList<AnswerToQuestion>> answers = new ArrayList<>();
        ArrayList<AnswerToQuestion> row = new ArrayList<>();
        row.add(new AnswerFreeQuestion(1, validFormId, 1, "test answer"));
        answers.add(row);
        
        // Use an invalid form ID
        Analyze analyze = new Analyze(9999, "Invalid Form", 1, KMeansStrategy.getInstance(), answers);
        
        ctrlAnalyze.addAnalyze(analyze);
    }
}
