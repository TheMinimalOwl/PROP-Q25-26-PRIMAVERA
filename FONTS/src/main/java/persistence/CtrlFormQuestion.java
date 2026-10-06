package persistence;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import domain.Form;
import domain.Question;
import utils.EntityNotFoundException;

/**
 * The CtrlFormQuestion class is responsible for controlling persistence-related
 *  operations on the classes Form and Question.
 * It's intended to be called by the controller of the persistence.
 *
 * Stores Forms and Questions in memory and disk through the Cache. IDs are auto-generated
 *  if not provided.
 * This class follows the singleton pattern.
 *
 * The CtrlFormQuestion::close() method needs to be run manually **before** this class
 *    is destructed.
 * 
 * @author Tomeu Mestre (tomeu.mestre@estudiantat.upc.edu)
 */
public class CtrlFormQuestion {

    /** Inner static class for lazy-loaded singleton. */
    private static class CtrlFormQuestionSingleton {
        private static final CtrlFormQuestion instance = new CtrlFormQuestion();
    }

    private static final String FORMS_DIR = "Forms"; // FORMS_DIR
    private static final String QUESTIONS_DIR = "Questions"; // FORMS_DIR/formId/

    private final ArrayList<Integer> formsToDeleteDir;

    /**
     * Returns the singleton instance of this class.
     *
     * @return the singleton {@link CtrlFormQuestion}
     */
    protected static CtrlFormQuestion getInstance() {
        return CtrlFormQuestionSingleton.instance;
    }

    /**
     * Returns the directory path where forms are stored.
     *
     * @return the path to the forms directory
     */
    protected static Path getFormsDir() {
        return Paths.get(FORMS_DIR);
    }

    /**
     * Returns the file path for a specific form.
     *
     * @param idForm the external form ID
     * @return the path to the form's file
     */
    protected static Path getFormPath(Integer idForm) {
        return getFormsDir().resolve(idForm.toString() + ".json");
    }

    /**
     * Returns the directory path where questions for a specific form are stored.
     *
     * @param idForm the external form ID
     * @return the path to the questions directory for the form
     */
    protected static Path getQuestionsDir(Integer idForm) {
        return getFormsDir().resolve(idForm.toString()).resolve(QUESTIONS_DIR);
    }

    /**
     * Returns the file path for a specific question.
     *
     * @param idForm the external form ID
     * @param idQuestion the external question ID
     * @return the path to the question's file
     */
    protected static Path getQuestionPath(Integer idForm, Integer idQuestion) {
        return getQuestionsDir(idForm).resolve(idQuestion + ".json");
    }

    /**
     * Resets the cache
     */
    protected void resetForTests() {
        formsCache.resetForTests();
        questionsCache.resetForTests();
    }

    // =========================
    // Variables And Constructor
    // =========================

    private final Cache<Form> formsCache;
    private final Cache<Question> questionsCache;

    /** Private constructor for singleton. Initializes all in-memory storage. */
    private CtrlFormQuestion () {
        this.formsCache = new Cache<Form>(Form.class);
        this.questionsCache = new Cache<Question>(Question.class);
        this.formsToDeleteDir = new ArrayList<>();
    }

    // ==========================
    // Form-related operations
    // ==========================

    /**
     * Stores a new Form and returns its external ID.
     *
     * @param form the Form to store
     * @return idForm, the external Form ID assigned to the new Form
     * @throws IllegalArgumentException if the form is null
     * @throws IOException if an I/O error occurs
     */
    protected Integer addForm(Form form) throws IllegalArgumentException, IOException {
        if (form == null) {
            throw new IllegalArgumentException("Form cannot be null");
        }

        if (form.hasAssignedId()) {
            throw new IllegalArgumentException("Form ID must be unassigned");
        }

        Integer idForm = formsCache.putAndReturnId(getFormsDir(), form);
        form.setIdForm(idForm);

        return form.getIdForm();
    }

    /**
     * Returns the Form associated with the given external ID.
     *
     * @param idForm the external Form ID
     * @return the Form associated with idForm
     * @throws EntityNotFoundException if no Form exists with the given ID
     * @throws IOException if an I/O error occurs
     */
    protected Form getForm(Integer idForm) throws EntityNotFoundException, IOException {
        try {
            return formsCache.get(getFormPath(idForm));
        }

        catch (NoSuchFileException e) {
            throw new NoSuchFileException("Form with ID" + idForm + " not found.");
        }
    }

    /**
     * Deletes the Form associated with the given external ID.
     *
     * @param idForm the external Form ID
     * @throws EntityNotFoundException if no Form exists with the given ID
     * @throws IOException if an I/O error occurs
     */
    protected void deleteForm(Integer idForm) throws EntityNotFoundException, IOException {
        try {
            formsCache.remove(getFormPath(idForm));
            // recursively remove Forms/idForm directory from disk
            FileManager.getInstance().deleteDirectoryRecursively(getFormsDir().resolve(idForm.toString()));
            
            // Need to do this as Cache has questions/answers/analyzes cached, and we
                // do not know when will it save them to disk.
            // Another fix would be to invalidate all Caches after deleteForm: won't implement
            formsToDeleteDir.add(idForm);
        }

        catch (NoSuchFileException e) {
            throw new NoSuchFileException("Form with ID" + idForm + " not found.");
        }
    }

    /**
     * Checks if a Form exists with the given external ID.
     *
     * @param idForm the form to check if exists
     * @return True if the Form with key idForm exists
     */
    protected Boolean existsForm(Integer idForm) {
        return formsCache.containsKey(getFormPath(idForm));
    }

    /**
     * Returns all the forms stored in the system.
     *
     * @return a list of all form IDs
     * @throws IOException if an I/O error occurs
     */
    protected ArrayList<Integer> getAllForms() throws IOException {
        formsCache.persistCache();
        ArrayList<Integer> allIdForm = new ArrayList<>();
        List<Path> l = FileManager.getInstance().listFiles(getFormsDir());
        for (Path p : l) {
            String fileName = p.getFileName().toString();
            if (fileName.equals("counter.json") || !Files.isRegularFile(p))
                continue; // skip counter.json, only treat regular files

            Integer idForm = Integer.parseInt(
                fileName.replace(".json", "")
            );
            allIdForm.add(idForm);
        }
        return allIdForm;
    }

    // ==========================
    // Question-related operations
    // ==========================

    /**
     * Stores a new Question and returns its external ID.
     *
     * @param question the Question to store
     * @return idQuestion, the external Question ID assigned to the new Question
     * @throws IllegalArgumentException if the question is null
     * @throws EntityNotFoundException if no Form exists related with the given Question
     * @throws IOException if an I/O error occurs
     */
    protected Integer addQuestion(Question question) throws IllegalArgumentException, EntityNotFoundException, IOException {
        if (question == null) {
            throw new IllegalArgumentException("Question cannot be null");
        }

        if (!existsForm(question.getIdForm())) {
            throw new EntityNotFoundException(
                "Form with ID " + question.getIdForm() + " does not exist");
        }

        if (question.hasAssignedId()) {
            throw new IllegalArgumentException("Question id must be unassigned");
        }

        Integer idForm = question.getIdForm();
        Integer idQuestion = questionsCache.putAndReturnId(getQuestionsDir(idForm), question);
        question.setIdQuestion(idQuestion);

        return question.getIdQuestion(); 
    }

    /**
     * Returns the Question for the given form ID and question ID.
     *
     * @param idForm the external form ID
     * @param idQuestion the external question ID
     * @return the Question associated with the given IDs
     * @throws EntityNotFoundException if no Question exists for the given IDs, or if the given IDs do not exist
     * @throws IOException if an I/O error occurs
     */
    protected Question getQuestion(Integer idForm, Integer idQuestion) throws EntityNotFoundException, IOException {
        try {
            return questionsCache.get(getQuestionPath(idForm, idQuestion));
        }

        catch (NoSuchFileException | FileNotFoundException e) {
            throw new EntityNotFoundException(
                "Question for Form" + idForm + ", with ID " + idQuestion + " not found.");
        }
    }

    /**
     * Deletes the Question for the given form ID and question ID.
     *
     * @param idForm the external form ID
     * @param idQuestion the external question ID
     * @throws EntityNotFoundException if no Question exists for the given IDs
     * @throws IOException if an I/O error occurs
     */
    protected void deleteQuestion(Integer idForm, Integer idQuestion) throws EntityNotFoundException, IOException {
        try {
            questionsCache.remove(getQuestionPath(idForm, idQuestion));
        }

        catch (NoSuchFileException e) {
            throw new EntityNotFoundException(
                "Question for Form" + idForm + ", with ID " + idQuestion + " not found.");
        }
    }

    /**
     * Checks if a Question exists with the given form ID and question ID.
     *
     * @param idForm the form to check the question from
     * @param idQuestion the question to check if exists
     * @return True if the Question with key idForm+idQuestion exists
     */
    protected Boolean existsQuestion(Integer idForm, Integer idQuestion) {
        return existsForm(idForm) && questionsCache.containsKey(getQuestionPath(idForm, idQuestion)); 
    }

    // ==================
    // General operations
    // ==================

    /**
     * Operation needed to be run before this class is destructed
     *
     * @throws IOException
     */
    protected void close() throws IOException {
        formsCache.persistCache();
        questionsCache.persistCache();
        formsCache.flushCache();
        questionsCache.flushCache();
        
        for (Integer idForm : formsToDeleteDir) {
            FileManager.getInstance().deleteDirectoryRecursively(getFormsDir().resolve(idForm.toString()));
        }
    }

    /**
     * Clears all in-memory data.
     */
    protected void clear() throws IOException {
        formsCache.flushCache();
        questionsCache.flushCache();
    }
}
