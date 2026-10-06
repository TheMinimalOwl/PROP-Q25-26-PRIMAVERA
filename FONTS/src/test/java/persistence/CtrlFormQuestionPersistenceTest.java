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

import domain.Form;
import domain.Question;
import domain.NumericalQuestion;
import domain.FreeQuestion;
import utils.EntityNotFoundException;

/**
 * Unit test for the class CtrlFormQuestionPersistence
 * @author Tomeu Mestre (tomeu.mestre@estudiantat.upc.edu)
 */
public class CtrlFormQuestionPersistenceTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    private PropertySetter p = new PropertySetter();

    private CtrlFormQuestion ctrlFormQuestion;

    @Before
    public void setUp() throws IOException {
        p.set(tempDir.getRoot().toPath());

        ctrlFormQuestion = CtrlFormQuestion.getInstance();
        ctrlFormQuestion.resetForTests();
        ctrlFormQuestion.clear();
    }

    @After
    public void tearDown() {
        p.restore();

        ctrlFormQuestion.resetForTests();
    }

    @Test
    public void testSaveAndLoadSingleForm() throws IOException, EntityNotFoundException {
        // Create and add a Form
        Form form = new Form("Test Form", 1); // name, userPropietary
        Integer id = ctrlFormQuestion.addForm(form);

        // Clear in-memory data
        ctrlFormQuestion.close();

        // Load single form from file
        Form loadedForm = ctrlFormQuestion.getForm(id);

        // Verify form is restored
        assertNotNull(loadedForm);
        assertEquals("Test Form", loadedForm.getName());
        assertEquals(Integer.valueOf(1), loadedForm.getUserPropietary());
    }

    @Test
    public void testSaveAndLoadMultipleForms() throws IOException, EntityNotFoundException {
        // Create multiple forms
        Form form1 = new Form("Form 1", 1);
        Form form2 = new Form("Form 2", 2);

        Integer id1 = ctrlFormQuestion.addForm(form1);
        Integer id2 = ctrlFormQuestion.addForm(form2);

        // Clear and reload
        ctrlFormQuestion.close();

        // Verify all forms are restored
        Form loaded1 = ctrlFormQuestion.getForm(id1);
        assertEquals("Form 1", loaded1.getName());
        
        Form loaded2 = ctrlFormQuestion.getForm(id2);
        assertEquals("Form 2", loaded2.getName());
    }

    @Test
    public void testIdCounterPersistenceForm() throws IOException {
        // Add some forms
        Integer id1 = ctrlFormQuestion.addForm(new Form("Form 1", 1));
        Integer id2 = ctrlFormQuestion.addForm(new Form("Form 2", 1));

        // Clear and reload
        ctrlFormQuestion.close();

        // Add a new form - should get next ID
        Form newForm = new Form("Form 3", 1);
        Integer newId = ctrlFormQuestion.addForm(newForm);

        // Assuming IDs start at 1 and increment
        assertEquals(Integer.valueOf(3), newId);
    }

    @Test(expected = FileNotFoundException.class)
    public void testLoadNonExistentForm() throws IOException, EntityNotFoundException {
        // Load should throw exception for non-existent form
        ctrlFormQuestion.getForm(999);
    }

    @Test
    public void testDeleteForm() throws IOException, EntityNotFoundException {
        // Create and save a form
        Form form = new Form("Test Form", 1);
        Integer id = ctrlFormQuestion.addForm(form);

        // Verify file exists
        assertTrue(ctrlFormQuestion.existsForm(id));

        // Delete the form
        ctrlFormQuestion.deleteForm(id);

        // Verify file no longer exists
        assertFalse(ctrlFormQuestion.existsForm(id));
    }

    @Test
    public void testSaveAndLoadQuestion() throws IOException, EntityNotFoundException {
        // 1. Create a Form first (needed for question)
        Form form = new Form("Form for Question", 1);
        Integer idForm = ctrlFormQuestion.addForm(form);

        // 2. Create and add a Question
        FreeQuestion question = new FreeQuestion("How are you?", false, 100);
        question.setIdForm(idForm);
        
        Integer idQuestion = ctrlFormQuestion.addQuestion(question);

        // 3. Clear in-memory data
        ctrlFormQuestion.close();

        // 4. Load question from file
        Question loadedQuestion = ctrlFormQuestion.getQuestion(idForm, idQuestion);

        // 5. Verify question is restored
        assertNotNull(loadedQuestion);
        assertTrue(loadedQuestion instanceof FreeQuestion);
        assertEquals("How are you?", loadedQuestion.getStatement());
        assertEquals(idForm, Integer.valueOf(loadedQuestion.getIdForm()));
        assertEquals(idQuestion, Integer.valueOf(loadedQuestion.getIdQuestion()));
    }

    @Test
    public void testIdCounterPersistenceQuestion() throws IOException, EntityNotFoundException {
        // 1. Create a Form
        Form form = new Form("Form for Questions", 1);
        Integer idForm = ctrlFormQuestion.addForm(form);

        // 2. Add some questions
        FreeQuestion q1 = new FreeQuestion("Q1", false, null);
        q1.setIdForm(idForm);
        ctrlFormQuestion.addQuestion(q1);

        FreeQuestion q2 = new FreeQuestion("Q2", false, null);
        q2.setIdForm(idForm);
        ctrlFormQuestion.addQuestion(q2);

        // 3. Clear and reload
        ctrlFormQuestion.close();

        // 4. Add a new question - should get next ID
        FreeQuestion q3 = new FreeQuestion("Q3", false, null);
        q3.setIdForm(idForm);
        Integer newId = ctrlFormQuestion.addQuestion(q3);

        // Assuming IDs start at 1 and increment
        assertEquals(Integer.valueOf(3), newId);
    }

    @Test
    public void testDeleteQuestion() throws IOException, EntityNotFoundException {
        // 1. Create Form and Question
        Form form = new Form("Form", 1);
        Integer idForm = ctrlFormQuestion.addForm(form);

        FreeQuestion q = new FreeQuestion("Q", false, null);
        q.setIdForm(idForm);
        Integer idQuestion = ctrlFormQuestion.addQuestion(q);

        // 2. Verify exists
        assertTrue(ctrlFormQuestion.existsQuestion(idForm, idQuestion));

        // 3. Delete
        ctrlFormQuestion.deleteQuestion(idForm, idQuestion);

        // 4. Verify not exists
        assertFalse(ctrlFormQuestion.existsQuestion(idForm, idQuestion));
    }

    @Test(expected = EntityNotFoundException.class)
    public void testLoadNonExistentQuestion() throws IOException, EntityNotFoundException {
        // Create a form so the directory exists (optional, but good for realism)
        Form form = new Form("Form", 1);
        Integer idForm = ctrlFormQuestion.addForm(form);
        
        // Load should throw exception for non-existent question
        ctrlFormQuestion.getQuestion(idForm, 999);
    }
    
    @Test(expected = EntityNotFoundException.class)
    public void testAddQuestionToNonExistentForm() throws IOException, EntityNotFoundException {
        FreeQuestion q = new FreeQuestion("Q", false, null);
        q.setIdForm(999); // Non-existent form ID
        ctrlFormQuestion.addQuestion(q);
    }

    @Test
    public void testGetAllForms() throws IOException {
        Integer idF1 = ctrlFormQuestion.addForm(new Form("Form", 1));
        Integer idF2 = ctrlFormQuestion.addForm(new Form("Form", 2));
        Integer idF3 = ctrlFormQuestion.addForm(new Form("Form", 3));

        Question q = new NumericalQuestion("example?", true, 0, 1);
        q.setIdForm(idF1);
        ctrlFormQuestion.addQuestion(q);
        ctrlFormQuestion.close(); // persist + flush cache
        
        ArrayList<Integer> forms = ctrlFormQuestion.getAllForms();
        assertTrue(forms.contains(idF1));
        assertTrue(forms.contains(idF2));
        assertTrue(forms.contains(idF3));
    }
}
