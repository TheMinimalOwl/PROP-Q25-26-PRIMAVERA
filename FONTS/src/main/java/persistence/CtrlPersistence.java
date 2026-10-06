package persistence;

import java.io.IOException;
import java.util.ArrayList;

import domain.Analyze;
import domain.Answer;
import domain.AnswerToQuestion;
import domain.Form;
import domain.Question;
import domain.User;

import utils.EntityNotFoundException;
import utils.PersistenceException;

/**
 *
 * The CtrlPersistence class is responsible for controlling persistence-related
 *  operations. It implements the communication between domain and persistence
 *  layers, such that data storage and retrieval is at the same time fast and
 *  persistent. It's intended to be called by the controller of the domain.
 *
 * Stores Users, Forms, Questions, Answers, AnswerToQuestions, and Analyzes
 *  in memory and disk. The responsability to attain this task is relegated
 *  to specific controllers: CtrlUser, CtrlFormQuestion, CtrlAnswerQuestion and
 *  CtrlAnalye.
 * This class follows the singleton pattern.
 *
 * @author Artur Leivar (artur.leivar@estudiantat.upc.edu)
 */
public class CtrlPersistence {
    private final CtrlUser _ctrlUser;
    private final CtrlFormQuestion _ctrlFormQuestion;
    private final CtrlAnswerQuestion _ctrlAnswerQuestion;
    private final CtrlAnalyze _ctrlAnalyze;

    /** Private constructor for singleton. Initializes all in-memory storage. */
    private CtrlPersistence() {
        _ctrlUser = CtrlUser.getInstance();
        _ctrlFormQuestion = CtrlFormQuestion.getInstance();
        _ctrlAnswerQuestion = CtrlAnswerQuestion.getInstance();
        _ctrlAnalyze = CtrlAnalyze.getInstance();
    }

    /** Inner static class for lazy-loaded singleton. */
    private static class CtrlPersistenceSingleton {
        private static final CtrlPersistence instance = new CtrlPersistence();
    }

    /**
     * Returns the singleton instance of this class.
     *
     * @return the singleton {@link CtrlPersistence}
     */
    public static CtrlPersistence getInstance() {
        return CtrlPersistenceSingleton.instance;
    }

    // ==========================
    // User-related operations
    // ==========================

    /**
     * Stores a new User and returns its external ID.
     *
     * @param user the User to store
     * @return idUser, the external user ID assigned to the new User
     * @throws IllegalArgumentException if the user is null
     * @throws PersistenceException to indicate an internal error related to persistence
     */
    public Integer addUser(User user) throws PersistenceException {
        try {
            return _ctrlUser.addUser(user);
        }
        catch (IOException e) {
            throw new PersistenceException("CtrlPersistence::addUser() method failed.", e);
        }
    }

    /**
     * Returns the User associated with the given external ID.
     *
     * @param idUser the external user ID
     * @return the User associated with idUser
     * @throws EntityNotFoundException if no User exists with the given ID
     * @throws PersistenceException to indicate an internal error related to persistence
     */
    public User getUser(Integer idUser) throws EntityNotFoundException, PersistenceException {
        try {
            return _ctrlUser.getUser(idUser);
        }
        catch (IOException e) {
            throw new PersistenceException("CtrlPersistence::getUser() method failed.", e);
        }
    }

    /**
     * Deletes the User associated with the given external ID.
     *
     * @param idUser the external user ID
     * @throws EntityNotFoundException if no User exists with the given ID
     * @throws PersistenceException to indicate an internal error related to persistence
     */
    public void deleteUser(Integer idUser) throws EntityNotFoundException, PersistenceException {
        try {
            _ctrlUser.deleteUser(idUser);
        }
        catch (IOException e) {
            throw new PersistenceException("CtrlPersistence::deleteUser() method failed.", e);
        }
    }

    public Boolean existsUser(Integer idUser) {
        return _ctrlUser.existsUser(idUser);
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
     * @throws PersistenceException to indicate an internal error related to persistence
     */
    public Integer addForm(Form form) throws PersistenceException {
        try {
            return _ctrlFormQuestion.addForm(form);
        }
        catch (IOException e) {
            throw new PersistenceException("CtrlPersistence::addForm() method failed.", e);
        }
    }

    /**
     * Returns the Form associated with the given external ID.
     *
     * @param idForm the external Form ID
     * @return the Form associated with idForm
     * @throws EntityNotFoundException if no Form exists with the given ID
     * @throws PersistenceException to indicate an internal error related to persistence
     */
    public Form getForm(Integer idForm) throws EntityNotFoundException, PersistenceException {
        try {
            return _ctrlFormQuestion.getForm(idForm);
        }
        catch (IOException e) {
            throw new PersistenceException("CtrlPersistence::getForm() method failed.", e);
        }
    }

    /**
     * Deletes the Form associated with the given external ID.
     *
     * @param idForm the external Form ID
     * @throws EntityNotFoundException if no Form exists with the given ID
     * @throws PersistenceException to indicate an internal error related to persistence
     */
    public void deleteForm(Integer idForm) throws EntityNotFoundException, PersistenceException {
        try {
            _ctrlFormQuestion.deleteForm(idForm);
        }
        catch (IOException e) {
            throw new PersistenceException("CtrlPersistence::deleteForm() method failed.", e);
        }
    }

    /**
     * Checks if a Form exists with the given external ID.
     *
     * @param idForm the form to check if exists
     * @return True if the Form with key idForm exists
     */
    public Boolean existsForm(Integer idForm) {
        return _ctrlFormQuestion.existsForm(idForm);
    }

    /**
     * Returns all the existing Form external IDS.
     *
     * @return list of all forms ids
     * @throws PersistenceException to indicate an internal error related to persistence
     */
    public ArrayList<Integer> getAllForms() throws PersistenceException {
        try {
            return _ctrlFormQuestion.getAllForms();
        } catch (Exception e) {
            throw new PersistenceException("CtrlPersistence::getAllForms() method failed.", e);
        }
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
     * @throws PersistenceException to indicate an internal error related to
     *    persistence has occurred.
     */
    public void addAnswer(Answer answer) throws EntityNotFoundException, PersistenceException {
        try {
            _ctrlAnswerQuestion.addAnswer(answer);
        }
        catch (IOException e) {
            throw new PersistenceException("CtrlPersistence::addAnswer() method failed.", e);
        }
    }

    /**
     * Returns the Answer for the given user and form IDs.
     *
     * @param idUser the external user ID
     * @param idForm the external form ID
     * @return the Answer associated with the given IDs
     * @throws EntityNotFoundException if no Answer exists for the given IDs, or if the given IDs do not exist
     * @throws PersistenceException to indicate an internal error related to
     *    persistence has occurred.
     */
    public Answer getAnswer(Integer idUser, Integer idForm) throws EntityNotFoundException, PersistenceException {
        try {
            return _ctrlAnswerQuestion.getAnswer(idUser, idForm);
        }
        catch (IOException e) {
            throw new PersistenceException("CtrlPersistence::getAnswer() method failed.", e);
        }
    }

    /**
     * Deletes the Answer for the given user and form IDs.
     *
     * @param idUser the external user ID
     * @param idForm the external form ID
     * @throws EntityNotFoundException if no Answer exists for the given IDs, or if the given IDs do not exist
     * @throws PersistenceException to indicate an internal error related to
     *    persistence has occurred.
     */
    public void deleteAnswer(Integer idUser, Integer idForm) throws EntityNotFoundException, PersistenceException {
        try {
            _ctrlAnswerQuestion.deleteAnswer(idUser, idForm);
        }
        catch (IOException e) {
            throw new PersistenceException("CtrlPersistence::deleteAnswer() method failed.", e);
        }
    }

    /**
     * Checks if the Answer for the given user ID and form ID exists.
     *
     * @param idUser the external user ID
     * @param idForm the external form ID
     * @return True if the Answer associated with the given user ID and form ID exists
     */
    public Boolean existsAnswer(Integer idUser, Integer idForm) {
        return _ctrlAnswerQuestion.existsAnswer(idUser, idForm);
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
     * @throws PersistenceException to indicate an internal error related to persistence
     */
    public Integer addQuestion(Question question) throws EntityNotFoundException, PersistenceException {
        try {
            return _ctrlFormQuestion.addQuestion(question);
        }
        catch (IOException e) {
            throw new PersistenceException("CtrlPersistence::addQuestion() method failed.", e);
        }
    }

    /**
     * Returns the Question for the given form ID and question ID.
     *
     * @param idForm the external form ID
     * @param idQuestion the external question ID
     * @return the Question associated with the given IDs
     * @throws EntityNotFoundException if no Question exists for the given IDs, or if the given IDs do not exist
     * @throws PersistenceException to indicate an internal error related to persistence
     */
    public Question getQuestion(Integer idForm, Integer idQuestion) throws EntityNotFoundException, PersistenceException {
        try {
            return _ctrlFormQuestion.getQuestion(idForm, idQuestion);
        }
        catch (IOException e) {
            throw new PersistenceException("CtrlPersistence::getQuestion() method failed.", e);
        }
    }

    /**
     * Deletes the Question for the given form ID and question ID.
     *
     * @param idForm the external form ID
     * @param idQuestion the external question ID
     * @throws EntityNotFoundException if no Question exists for the given IDs
     * @throws PersistenceException to indicate an internal error related to persistence
     */
    public void deleteQuestion(Integer idForm, Integer idQuestion) throws EntityNotFoundException, PersistenceException {
        try {
            _ctrlFormQuestion.deleteQuestion(idForm, idQuestion);
        }
        catch (IOException e) {
            throw new PersistenceException("CtrlPersistence::deleteQuestion() method failed.", e);
        }
    }

    /**
     * Checks if a Question exists with the given form ID and question ID.
     *
     * @param idForm the form to check the question from
     * @param idQuestion the question to check if exists
     * @return True if the Question with key idForm+idQuestion exists
     */
    public Boolean existsQuestion(Integer idForm, Integer idQuestion) {
        return _ctrlFormQuestion.existsQuestion(idForm, idQuestion);
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
     * @throws PersistenceException to indicate an internal error related to
     *    persistence has occurred.
     */
    public void addAnswerToQuestion(AnswerToQuestion answerToQuestion) throws EntityNotFoundException, PersistenceException {
        try {
            _ctrlAnswerQuestion.addAnswerToQuestion(answerToQuestion);
        }
        catch (IOException e) {
            throw new PersistenceException("CtrlPersistence::addAnswerToQuestion() method failed.", e);
        }
    }

    /**
     * Returns the AnswerToQuestion for the given user ID, form ID, and question ID.
     *
     * @param idUser the external user ID
     * @param idForm the external form ID
     * @param idQuestion the external question ID
     * @return the AnswerToQuestion associated with the given IDs
     * @throws EntityNotFoundException if no AnswerToQuestion exists for the given IDs, or if the given IDs do not exist
     * @throws PersistenceException to indicate an internal error related to
     *    persistence has occurred.
     */
    public AnswerToQuestion getAnswerToQuestion(Integer idUser, Integer idForm, Integer idQuestion) throws EntityNotFoundException, PersistenceException {
        try {
            return _ctrlAnswerQuestion.getAnswerToQuestion(idUser, idForm, idQuestion);
        }
        catch (IOException e) {
            throw new PersistenceException("CtrlPersistence::getAnswerToQuestion() method failed.", e);
        }
    }

    /**
     * Deletes the AnswerToQuestion for the given user ID, form ID, and question ID.
     *
     * @param idUser the external user ID
     * @param idForm the external form ID
     * @param idQuestion the external question ID
     * @throws EntityNotFoundException if no AnswerToQuestion exists for the given IDs, or if the given IDs do not exist
     * @throws PersistenceException to indicate an internal error related to
     *    persistence has occurred.
     */
    public void deleteAnswerToQuestion(Integer idUser, Integer idForm, Integer idQuestion) throws EntityNotFoundException, PersistenceException {
        try {
            _ctrlAnswerQuestion.deleteAnswerToQuestion(idUser, idForm, idQuestion);
        }
        catch (IOException e) {
            throw new PersistenceException("CtrlPersistence::deleteAnswerToQuestion() method failed.", e);
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
    public Boolean existsAnswerToQuestion(Integer idUser, Integer idForm, Integer idQuestion) {
        return _ctrlAnswerQuestion.existsAnswerToQuestion(idUser, idForm, idQuestion);
    }

    // ==========================
    // Analyze-related operations
    // ==========================

    /**
     * Stores a new Analyze.
     *
     * @param analyze the Analyze to store
     * @throws IllegalArgumentException if the analyze is null
     * @throws EntityNotFoundException if no User exists related with the given Analyze
     * @throws PersistenceException to indicate an internal error related to
     *    persistence has occurred.
     */
    public Integer addAnalyze(Analyze analyze) throws EntityNotFoundException, PersistenceException {
        try {
            return _ctrlAnalyze.addAnalyze(analyze);
        }
        catch (IOException e) {
            throw new PersistenceException("CtrlPersistence::addAnalyze() method failed.", e);
        }
    }

    /**
     * Returns the Analyze for the given form ID and analyze ID.
     *
     * @param idForm the external form ID
     * @param idAnalyze the external analyze ID
     * @return the Analyze associated with the given form ID and analyze ID
     * @throws EntityNotFoundException if no Analyze exists for the given form ID and analyze ID
     * @throws PersistenceException to indicate an internal error related to
     *    persistence has occurred.
     */
    public Analyze getAnalyze(Integer idForm, Integer idAnalyze) throws EntityNotFoundException, PersistenceException {
        try {
            return _ctrlAnalyze.getAnalyze(idForm, idAnalyze);
        }
        catch (IOException e) {
            throw new PersistenceException("CtrlPersistence::getAnalyze() method failed.", e);
        }
    }

    /**
     * Deletes the Analyze for the given form ID and analyze ID.
     *
     * @param idForm the external form ID
     * @param idAnalyze the external analyze ID
     * @throws EntityNotFoundException if no Analyze exists for the given form ID and analyze ID
     * @throws PersistenceException to indicate an internal error related to
     *    persistence has occurred.
     */
    public void deleteAnalyze(Integer idForm, Integer idAnalyze) throws EntityNotFoundException, PersistenceException {
        try {
            _ctrlAnalyze.deleteAnalyze(idForm, idAnalyze);
        }
        catch (IOException e) {
            throw new PersistenceException("CtrlPersistence::deleteAnalyze() method failed.", e);
        }
    }

    /**
     * Checks if the Analyze for the given form ID and analyze ID exists.
     *
     * @param idForm the external form ID
     * @param idAnalyze the external analyze ID
     * @return True if the Analyze associated with the given form ID and analyze ID exists
     */
    public Boolean existsAnalyze(Integer idForm, Integer idAnalyze) {
        return _ctrlAnalyze.existsAnalyze(idForm, idAnalyze);
    }

    /**
     * Closes all persistence controllers, ensuring data is saved to disk.
     *
     * IT IS **MANDATORY** TO CALL IT BEFORE THE CLOSING OF THE PROGRAM FOR THE CORRECT
     *  FUNCTIONING OF THIS PROGRAM (FOR DATA TO BE PERSISTENT AND NOT CORRUPTED).
     *
     * @throws PersistenceException if an error occurs during closing
     */
    public void close() throws PersistenceException {
        try {
            _ctrlUser.close();
            _ctrlAnswerQuestion.close();
            _ctrlAnalyze.close();
            _ctrlFormQuestion.close();
        } catch (IOException e) {
            throw new PersistenceException("Failed to close persistence layer", e);
        }
    }

}
