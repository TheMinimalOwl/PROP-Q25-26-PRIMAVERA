package persistence;

import java.nio.file.NoSuchFileException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

import domain.Answer;
import domain.AnswerToQuestion;
import utils.EntityNotFoundException;

/**
 *
 * The CtrlAnswerQuestion class is responsible for controlling persistence-related
 *  operations on the classes Answer and AnswerToQuestion.
 * It's intended to be called by the controller of the persistence.
 *
 * Stores Answers and AnswersToQuestion in memory and disk through the Cache.
 * This class follows the singleton pattern.
 *
 * The CtrlAnswerQuestion::close() method needs to be run manually **before** this class
 *    is destructed.
 *
 * @author Artur Leivar (artur.leivar@estudiantat.upc.edu)
 */
public class CtrlAnswerQuestion {

    /** Inner static class for lazy-loaded singleton. */
    private static class CtrlAnswerQuestionSingleton {
        private static final CtrlAnswerQuestion instance = new CtrlAnswerQuestion();
    }

    private static final String ANSWERS_DIR = "Answers"; // FORMS_DIR/formId/Answers
    /* AnswerToQuestions are in FORMS_DIR/formId/ANSWERS_DIR/userId/ */

    private static final CtrlUser _ctrlUser = CtrlUser.getInstance();
    private static final CtrlFormQuestion _ctrlFormQuestion = CtrlFormQuestion.getInstance();

    // ==============
    // Static Getters
    // ==============

    /**
     * Returns the singleton instance of this class.
     *
     * @return the singleton {@link CtrlAnswerQuestion}
     */
    protected static CtrlAnswerQuestion getInstance() {
        return CtrlAnswerQuestionSingleton.instance;
    }

    /**
     * @param idForm the external form ID
     * @return the correct directory to save answers with the given idForm.
     */
    protected static Path getAnswersDir(Integer idForm) {
        return CtrlFormQuestion.getFormsDir().resolve(idForm.toString()).resolve(ANSWERS_DIR);
    }

    /**
     * @param idUser the external user ID
     * @param idForm the external form ID
     * @return the path where Answer with IDs idUser and idForm is saved.
     */
    protected static Path getAnswerPath(Integer idUser, Integer idForm) {
        return getAnswersDir(idForm).resolve(idUser.toString() + ".json");
    }

    /**
     * @param idUser the external user ID
     * @param idForm the external form ID
     * @return the correct directory to save answerToQuestions with the given idForm
     *    and idUser.
     */
    protected static Path getAnswerToQuestionsDir(Integer idUser, Integer idForm) {
        return getAnswersDir(idForm).resolve(idUser.toString());
    }

    /**
     * @param idUser the external user ID
     * @param idForm the external form ID
     * @param idQuestion the external Question ID
     * @return the path where AnswerToQuestion with IDs idUser, idForm and
     *    idQuestion is saved.
     */
    protected static Path getAnswerToQuestionPath(Integer idUser, Integer idForm, Integer idQuestion) {
        return getAnswerToQuestionsDir(idUser, idForm).resolve(idQuestion.toString() + ".json");
    }

    protected void resetForTests() {
        answersCache.resetForTests();
        answerToQuestionsCache.resetForTests();
    }

    // =========================
    // Variables And Constructor
    // =========================

    private final Cache<Answer> answersCache;
    private final Cache<AnswerToQuestion> answerToQuestionsCache;

    /** Private constructor for singleton. Initializes all in-memory storage. */
    private CtrlAnswerQuestion() {
        this.answersCache = new Cache<Answer>(Answer.class);
        this.answerToQuestionsCache = new Cache<AnswerToQuestion>(AnswerToQuestion.class);
    }

    // ==========================
    // Answer-related operations
    // ==========================

    /**
     * Stores a new Answer.
     *
     * @param answer the Answer to store
     * @throws IllegalArgumentException if the answer is null
     * @throws EntityNotFoundException if no User or Form exists related with the given Answer
     * @throws IOException 
     */
    protected void addAnswer(Answer answer) throws IllegalArgumentException, EntityNotFoundException, IOException {
        if (answer == null) {
            throw new IllegalArgumentException("Answer cannot be null");
        }

        if (!_ctrlUser.existsUser(answer.getIdUser())) {
            throw new EntityNotFoundException(
                "User with ID " + answer.getIdUser() + " does not exist");
        }

        if (!_ctrlFormQuestion.existsForm(answer.getIdForm())) {
            throw new EntityNotFoundException(
                "Form with ID " + answer.getIdForm() + " does not exist");
        }

        answersCache.put(getAnswerPath(answer.getIdUser(), answer.getIdForm()), answer);
    }

    /**
     * Returns the Answer for the given user and form IDs.
     *
     * @param idUser the external user ID
     * @param idForm the external form ID
     * @return the Answer associated with the given IDs
     * @throws EntityNotFoundException if no Answer exists for the given IDs, or if the given IDs do not exist
     * @throws IOException 
     */
    protected Answer getAnswer(Integer idUser, Integer idForm) throws EntityNotFoundException, IOException {
        try {
            return answersCache.get(getAnswerPath(idUser, idForm));
        }

        catch (NoSuchFileException | FileNotFoundException e) {
            throw new EntityNotFoundException(
                "Answer by User with ID " + idUser+ " to Form with ID " + idForm + " not found.");
        }
    }

    /**
     * Deletes the Answer for the given user and form IDs.
     *
     * @param idUser the external user ID
     * @param idForm the external form ID
     * @throws EntityNotFoundException if no Answer exists for the given IDs, or if the given IDs do not exist
     * @throws IOException 
     */
    protected void deleteAnswer(Integer idUser, Integer idForm) throws EntityNotFoundException, IOException {
        try {
            answersCache.remove(getAnswerPath(idUser, idForm));
        }

        catch (NoSuchFileException e) {
            throw new EntityNotFoundException(
                "Answer by User with ID " + idUser+ " to Form with ID " + idForm + " not found.");
        }
    }

    /**
     * Checks if the Answer for the given user ID and form ID exists.
     *
     * @param idUser the external user ID
     * @param idForm the external form ID
     * @return True if the Answer associated with the given user ID and form ID exists
     */
    protected Boolean existsAnswer(Integer idUser, Integer idForm) {
        return (_ctrlUser.existsUser(idUser) && _ctrlFormQuestion.existsForm(idForm) && answersCache.containsKey(getAnswerPath(idUser, idForm)));
    }

    // ==========================
    // AnswerToQuestion-related operations
    // ==========================

    /**
     * Stores a new AnswerToQuestion.
     *
     * @param answerToQuestion the AnswerToQuestion to store
     * @throws IllegalArgumentException if the answerToQuestion is null
     * @throws EntityNotFoundException if no User or Form exists related with the given AnswerToQuestion
     * @throws IOException 
     */
    protected void addAnswerToQuestion(AnswerToQuestion answerToQuestion) throws IllegalArgumentException, EntityNotFoundException, IOException {
        if (answerToQuestion == null) {
            throw new IllegalArgumentException("AnswerToQuestion cannot be null");
        }

        Integer idUser = answerToQuestion.getIdUser();
        Integer idForm = answerToQuestion.getIdForm();
        Integer idQuestion = answerToQuestion.getIdQuestion();

        if (!_ctrlUser.existsUser(idUser)) {
            throw new EntityNotFoundException(
                "User with ID " + idUser + " does not exist");
        }

        if (!_ctrlFormQuestion.existsForm(idForm)) {
            throw new EntityNotFoundException(
                "Form with ID " + idForm + " does not exist");
        }

        if (!_ctrlFormQuestion.existsQuestion(idForm, idQuestion)) {
            throw new EntityNotFoundException(
                "Question with ID " + idQuestion + " and with Form ID " +
                    idForm + " does not exist");
        }

        answerToQuestionsCache.put(getAnswerToQuestionPath(idUser, idForm, idQuestion), answerToQuestion);
    }

    /**
     * Returns the AnswerToQuestion for the given user ID, form ID, and question ID.
     *
     * @param idUser the external user ID
     * @param idForm the external form ID
     * @param idQuestion the external question ID
     * @return the AnswerToQuestion associated with the given IDs
     * @throws EntityNotFoundException if no AnswerToQuestion exists for the given IDs, or if the given IDs do not exist
     * @throws IOException 
     */
    protected AnswerToQuestion getAnswerToQuestion(Integer idUser, Integer idForm, Integer idQuestion) throws EntityNotFoundException, IOException {
        try {
            return answerToQuestionsCache.get(getAnswerToQuestionPath(idUser, idForm, idQuestion));
        }

        catch (NoSuchFileException | FileNotFoundException e) {
            throw new EntityNotFoundException(
                "AnswerToQuestion for User " + idUser + ", Form " + idForm + ", Question "
                + idQuestion + " not found.");
        }
  }

    /**
     * Deletes the AnswerToQuestion for the given user ID, form ID, and question ID.
     *
     * @param idUser the external user ID
     * @param idForm the external form ID
     * @param idQuestion the external question ID
     * @throws EntityNotFoundException if no AnswerToQuestion exists for the given IDs, or if the given IDs do not exist
     * @throws IOException 
     */
    protected void deleteAnswerToQuestion(Integer idUser, Integer idForm, Integer idQuestion) throws EntityNotFoundException, IOException {
        try {
            answerToQuestionsCache.remove(getAnswerToQuestionPath(idUser, idForm, idQuestion));
        }

        catch (NoSuchFileException e) {
            throw new EntityNotFoundException(
                "AnswerToQuestion for User " + idUser + ", Form " + idForm + ", Question "
                + idQuestion + " not found.");
        }
    }

    /**
     * Checks if the Answer for the given user ID and form ID exists.
     *
     * @param idUser the external user ID
     * @param idForm the external form ID
     * @param idQuestion the external question ID
     * @return True if the AnswerToQuestion associated with the given user ID,
     *    form ID and question ID exists
     */
    protected Boolean existsAnswerToQuestion(Integer idUser, Integer idForm, Integer idQuestion) {
        return _ctrlUser.existsUser(idUser) && _ctrlFormQuestion.existsForm(idForm) &&
            _ctrlFormQuestion.existsQuestion(idForm, idQuestion) &&
            answerToQuestionsCache.containsKey(getAnswerToQuestionPath(idUser, idForm, idQuestion)); 
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
        answersCache.persistCache();
        answerToQuestionsCache.persistCache();
        answersCache.flushCache();
        answerToQuestionsCache.flushCache();
    }

    /**
     * Clears all in-memory data (answers, answerToQuestions and counters).
     */
    protected void clear() {
        answersCache.flushCache();
        answerToQuestionsCache.flushCache();
    }
}
