package domaincontrollers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import domain.Analyze;
import domain.AnalyzeStrategy;
import domain.AnonymousUser;
import domain.Answer;
import domain.AnswerFreeQuestion;
import domain.AnswerMultipleChoiceQuestion;
import domain.AnswerNumericalQuestion;
import domain.AnswerSingleChoiceQuestion;
import domain.AnswerToQuestion;
import domain.Form;
import domain.FreeQuestion;
import domain.KMeansStrategy;
import domain.KMedoidsStrategy;
import domain.MultipleChoiceQuestion;
import domain.NumericalQuestion;
import domain.OrderedSingleQuestion;
import domain.Question;
import domain.RegisteredUser;
import domain.User;
import persistence.CtrlPersistence;
import utils.DomainException;
import utils.EntityNotFoundException;
import utils.PersistenceException;

/**
 * 
 * The CtrlDomain class is responsible for controlling domain-related
 * operations.
 * It implements the project use cases, and is in charge of communicating the
 * domain
 * with the presentation and data layers.
 * This class is designed as a singleton to ensure there is only one instance
 * controlling the domain operations.
 *
 * Note: some use-cases are currently not implemented, such as form
 * import/export.
 * 
 * @author Artur Leivar (artur.leivar@estudiantat.upc.edu)
 */
public class CtrlDomain {
    private Integer loggedUser;
    private final CtrlPersistence _ctrlPersistence;
    private final CtrlTransformData _ctrlTransformData;
    private static CtrlDomain instance;

    /**
     * Private constructor to prevent direct instantiation.
     *
     */
    private CtrlDomain() {
        this.loggedUser = null;
        this._ctrlPersistence = CtrlPersistence.getInstance();
        this._ctrlTransformData = CtrlTransformData.getInstance(this);
    }

    /**
     * Returns the singleton instance of CtrlDomain.
     *
     * @return The singleton instance of CtrlDomain
     */
    public static CtrlDomain getInstance() {
        if (instance == null) {
            instance = new CtrlDomain();
        }
        return instance;
    }

    // ======================
    // User-related use cases
    // ======================

    /**
     * Creates a new registered user with the specified details.
     *
     * @param name                 The username of the new user.
     * @param password             The password for the new user.
     * @param passwordConfirmation The password confirmation.
     * @param email                The email address of the new user.
     * @return The unique User ID of the newly created user.
     * @throws IllegalArgumentException If passwords do not match.
     */
    public Integer createUser(String name, String password, String passwordConfirmation, String email) {
        if (!password.equals(passwordConfirmation))
            throw new IllegalArgumentException("A password and its confirmation must be the same: " + password
                    + " not equals " + passwordConfirmation);

        return addUser(new RegisteredUser(name, password, email));
    }

    /**
     * Creates a new anonymous user.
     *
     * @return The unique User ID of the newly created anonymous user.
     */
    public Integer createAnonymousUser() {
        return addUser(new AnonymousUser());
    }

    /**
     * @param idUser
     * @return
     */
    public String getUsername(Integer idUser) {
        User user = getUser(idUser);

        if (user instanceof RegisteredUser) {
            return ((RegisteredUser) user).getUsername();
        } else if (user instanceof AnonymousUser) {
            return "Guest_" + idUser;
        } else {
            return "Unknown";
        }
    }

    /**
     * @param idUser
     * @return
     */
    public String getUserEmail(Integer idUser) {
        return getRegisteredUser(idUser).getEmail();
    }

    /**
     * @param idUser
     * @param newName
     */
    public void changeUsername(Integer idUser, String newName) {
        getRegisteredUser(idUser).setUsername(newName);
    }

    /**
     * @param idUser
     * @param newEmail
     */
    public void changeUserEmail(Integer idUser, String newEmail) {
        getRegisteredUser(idUser).setEmail(newEmail);
    }

    /**
     * @param idUser
     * @param oldPassword
     * @param newPassword
     */
    public void changeUserPassword(Integer idUser, String oldPassword, String newPassword) {
        RegisteredUser ru = getRegisteredUser(idUser);
        if (ru.getPassword().equals(oldPassword)) {
            ru.setPassword(newPassword);
        } else {
            throw new SecurityException("Current password is incorrect");
        }
    }

    /**
     * Logs in a user by verifying their ID and password.
     *
     * @param idUser   The ID of the user.
     * @param password The password associated with the User idUser.
     * @throws IllegalArgumentException If the password is incorrect.
     */
    public void loginUser(Integer idUser, String password) {
        RegisteredUser ru = getRegisteredUser(idUser);
        if (!ru.getPassword().equals(password))
            throw new IllegalArgumentException(
                    "Given password " + password + " is incorrect for User with ID " + idUser);
        loggedUser = idUser;
    }

    /**
     * Logouts currently logged in user (if any).
     */
    public void logoutUser() {
        loggedUser = null;
    }

    public Integer getLoggedUser() {
        if (loggedUser == null) {
            throw new DomainException("Invalid state, there is no currently logged user.", null);
        }
        return loggedUser;
    }

    /**
     * Verifica si un usuario ya ha contestado un formulario.
     * 
     * @param idUser ID del usuario
     * @param idForm ID del formulario
     * @return true si ya ha contestado
     */
    public boolean hasUserAnsweredForm(Integer idUser, Integer idForm) {
        return existsAnswer(idUser, idForm);
    }

    // ======================
    // Form-related use cases
    // ======================

    /**
     * Creates a new form for a specific user.
     *
     * @param idPropietary The ID of the form's proprietary user.
     * @param name         The name of the form.
     * @return The ID of the newly created form.
     * @throws IllegalArgumentException If a form with the same name already exists,
     *                                  or idPropietary is not a RegisteredUser;
     */
    public Integer createForm(Integer idPropietary, String name) {
        User u = getUser(idPropietary);
        if (u instanceof RegisteredUser) {
            RegisteredUser ru = (RegisteredUser) u;
            ArrayList<Integer> forms = ru.getForms(); // cannot be null

            // Check if User ru has another Form with the same name
            if (forms != null) {
                for (Integer idForm : forms) {
                    if (idForm != null && existsForm(idForm)) {
                        Form f = getForm(idForm);
                        if (f.getName().equals(name)) {
                            throw new IllegalArgumentException("Already exists a form of the User with ID "
                                + idPropietary + " with the name " + name);
                        }
                    }
                }
            }
            else {
                ru.setForms(new ArrayList<>());
            }

            Integer idForm = addForm(new Form(name, idPropietary));
            return idForm;
        } else
            throw new IllegalArgumentException("User with ID " + idPropietary + " is not a RegisteredUser");
    }

    /**
     * + * Deletes the form idForm, owned by the currently logged-in user.
     * + *
     * + * @param idForm The ID of the form to delete.
     * + * @throws IllegalArgumentException If the logged-in user is not the owner
     * of the form idForm.
     */
    public void deleteFormPropietaryLogged(Integer idForm) {
        if (getForm(idForm).getUserPropietary() != getLoggedUser()) {
            throw new IllegalArgumentException(
                    "Propietary User with ID " + getForm(idForm).getUserPropietary() + " is not currently logged");
        }

        deleteForm(idForm);
    }

    /**
     * Sets the name of the Form idForm to newName
     *
     * @param idForm the external ID of the form to modify
     * @param newName the new name
     */
    public void modifyFormName(Integer idForm, String newName) {
        Form form = getForm(idForm);

        // Verify current user is the owner
        if (!form.getUserPropietary().equals(getLoggedUser())) {
            throw new IllegalArgumentException("You are not the owner of this form");
        }

        // Verify it has no answers
        if (!form.getAnswers().isEmpty()) {
            throw new IllegalArgumentException("Cannot modify a form with answers");
        }

        // Update name
        form.setName(newName);
    }

    /**
     * Publica un formulario, haciéndolo visible para otros usuarios.
     * 
     * @param idForm ID del formulario a publicar
     */
    public void publishForm(Integer idForm) {
        Form form = getForm(idForm);

        // Verificar que el usuario actual es el propietario
        if (!form.getUserPropietary().equals(getLoggedUser())) {
            throw new IllegalArgumentException("You are not the owner of this form");
        }

        // Publicar el formulario
        form.publish();
    }

    /**
     * Despublica un formulario, dejándolo invisible para otros usuarios.
     * 
     * @param idForm ID del formulario a despublicar
     */
    public void unpublishForm(Integer idForm) {
        Form form = getForm(idForm);

        // Verificar que el usuario actual es el propietario
        if (!form.getUserPropietary().equals(getLoggedUser())) {
            throw new IllegalArgumentException("You are not the owner of this form");
        }

        // Despublicar el formulario
        form.unpublish();
    }

    /**
     * Verifica si un formulario está publicado.
     * 
     * @param idForm ID del formulario
     * @return true si está publicado
     */
    public boolean isFormPublished(Integer idForm) {
        Form form = getForm(idForm);
        return form.isPublished();
    }

    public void reorderFormQuestions(Integer idForm, List<Integer> newOrder) {
        Form form = getForm(idForm);

        // Verify current user is the owner
        if (!form.getUserPropietary().equals(getLoggedUser())) {
            throw new IllegalArgumentException("You are not the owner of this form");
        }

        // Verify it has no answers
        if (!form.getAnswers().isEmpty()) {
            throw new IllegalArgumentException("Cannot reorder questions in a form with answers");
        }

        // Verify all IDs exist in the form
        List<Integer> currentQuestions = form.getQuestions();
        if (!currentQuestions.containsAll(newOrder) || !newOrder.containsAll(currentQuestions)) {
            throw new IllegalArgumentException("ID list doesn't match current questions");
        }

        // Update order
        form.setQuestions(new ArrayList<>(newOrder));
    }

    // ==========================
    // Question-related use cases
    // ==========================

    /**
     * @param idForm
     * @param statement
     * @param isOptional
     * @param minValue
     * @param maxValue
     * @throws IllegalArgumentException If the form has already been answered, or
     *                                  propietary user of the form is not currently
     *                                  logged in.
     * @return idQuestion: identifier of the newly created Question
     */
    public Integer addNumericalQuestion(Integer idForm, String statement,
            Boolean isOptional, Integer minValue, Integer maxValue) {
        Form f = getForm(idForm);

        if (f.getUserPropietary() != getLoggedUser()) {
            throw new IllegalArgumentException("Propietary User with ID " + idForm + " is not currently logged");
        }

        if (!f.getAnswers().isEmpty()) {
            throw new IllegalArgumentException(
                    "Form with ID " + idForm + " has already been answered. It cannot be edited");
        }

        NumericalQuestion nq = new NumericalQuestion(statement, isOptional, minValue, maxValue);
        nq.setIdForm(idForm);
        Integer idQuestion = addQuestion(nq);

        return idQuestion;
    }

    /**
     * @param idForm
     * @param statement
     * @param isOptional
     * @param options
     * @param minSelections
     * @param maxSelections
     * @throws IllegalArgumentException If the form has already been answered, or
     *                                  propietary user of the form is not currently
     *                                  logged in.
     * @return idQuestion: identifier of the newly created Question
     */
    public Integer addMultipleChoiceQuestion(Integer idForm, String statement, Boolean isOptional,
            ArrayList<String> options, Integer minSelections, Integer maxSelections) {
        Form f = getForm(idForm);

        if (f.getUserPropietary() != getLoggedUser()) {
            throw new IllegalArgumentException("Propietary User with ID " + idForm + " is not currently logged");
        }

        if (!f.getAnswers().isEmpty()) {
            throw new IllegalArgumentException(
                    "Form with ID " + idForm + " has already been answered. It cannot be edited");
        }

        MultipleChoiceQuestion mcq = new MultipleChoiceQuestion(statement, isOptional, options, minSelections,
                maxSelections);
        mcq.setIdForm(idForm);
        Integer idQuestion = addQuestion(mcq);

        return idQuestion;
    }

    /**
     * @param idForm
     * @param statement
     * @param isOptional
     * @param options
     * @throws IllegalArgumentException If the form has already been answered, or
     *                                  propietary user of the form is not currently
     *                                  logged in.
     * @return idQuestion: identifier of the newly created Question
     */
    public Integer addSingleChoiceQuestion(Integer idForm, String statement,
            Boolean isOptional, ArrayList<String> options) {
        Form f = getForm(idForm);

        if (f.getUserPropietary() != getLoggedUser()) {
            throw new IllegalArgumentException("Propietary User with ID " + idForm + " is not currently logged");
        }

        if (!f.getAnswers().isEmpty()) {
            throw new IllegalArgumentException(
                    "Form with ID " + idForm + " has already been answered. It cannot be edited");
        }

        Integer minSel = 1; Integer maxSel = 1;
        MultipleChoiceQuestion mcq = new MultipleChoiceQuestion(statement, isOptional, options, minSel, maxSel);
        mcq.setIdForm(idForm);
        Integer idQuestion = addQuestion(mcq);

        return idQuestion;
    }

    /**
     * @param idForm
     * @param statement
     * @param isOptional
     * @param maxLength
     * @throws IllegalArgumentException If the form has already been answered, or
     *                                  propietary user of the form is not currently
     *                                  logged in.
     * @return idQuestion: identifier of the newly created Question
     */
    public Integer addFreeQuestion(Integer idForm, String statement, Boolean isOptional, Integer maxLength) {
        Form f = getForm(idForm);

        if (f.getUserPropietary() != getLoggedUser()) {
            throw new IllegalArgumentException("Propietary User with ID " + idForm + " is not currently logged");
        }

        if (!f.getAnswers().isEmpty()) {
            throw new IllegalArgumentException(
                    "Form with ID " + idForm + " has already been answered. It cannot be edited");
        }

        FreeQuestion fq = new FreeQuestion(statement, isOptional, maxLength);
        fq.setIdForm(idForm);
        Integer idQuestion = addQuestion(fq);

        return idQuestion;
    }

    /**
     * @param idForm
     * @param statement
     * @param isOptional
     * @param options
     * @throws IllegalArgumentException If the form has already been answered, or
     *                                  propietary user of the form is not currently
     *                                  logged in.
     * @return idQuestion: identifier of the newly created Question
     */
    public Integer addOrderedSingleQuestion(Integer idForm, String statement, Boolean isOptional,
            ArrayList<String> options) {
        Form f = getForm(idForm);
        if (f.getUserPropietary() != getLoggedUser()) {
            throw new IllegalArgumentException("Propietary User with ID " + idForm + " is not currently logged");
        }

        if (!f.getAnswers().isEmpty()) {
            throw new IllegalArgumentException(
                    "Form with ID " + idForm + " has already been answered. It cannot be edited");
        }

        OrderedSingleQuestion osq = new OrderedSingleQuestion(statement, isOptional, options);
        osq.setIdForm(idForm);
        Integer idQuestion = addQuestion(osq);

        return idQuestion;
    }

    /**
     * @param idForm
     * @param idQuestion
     * @throws IllegalArgumentException If the propietary user of the form is not
     *                                  currently logged in.
     */
    public void deleteQuestionPropietaryLogged(Integer idForm, Integer idQuestion) {
        if (getForm(idForm).getUserPropietary() != getLoggedUser()) {
            throw new IllegalArgumentException("Propietary User with ID " + idForm + " is not currently logged");
        }

        deleteQuestion(idForm, idQuestion);
    }

    /**
     * @param idForm
     * @param idQuestion
     * @param statement
     * @param isOptional
     * @param minValue
     * @param maxValue
     * @throws IllegalArgumentException If the form has already been answered, or
     *                                  propietary user of the form is not currently
     *                                  logged in, or the Question is not
     *                                  of type NumericalQuestion
     */
    public void modifyNumericalQuestion(Integer idForm, Integer idQuestion, String statement,
            Boolean isOptional, Integer minValue, Integer maxValue) {
        Form f = getForm(idForm);

        if (f.getUserPropietary() != getLoggedUser()) {
            throw new IllegalArgumentException("Propietary User with ID " + idForm + " is not currently logged");
        }

        if (!f.getAnswers().isEmpty()) {
            throw new IllegalArgumentException(
                    "Form with ID " + idForm + " has already been answered. It cannot be edited");
        }

        Question q = getQuestion(idForm, idQuestion);
        if (!(q instanceof NumericalQuestion))
            throw new IllegalArgumentException(
                    "Question with Form ID " + idForm + ", and Question ID " + idQuestion + " is not a Numerical Question");

        NumericalQuestion nq = (NumericalQuestion) q;
        nq.setStatement(statement);
        nq.setOptional(isOptional);
        nq.setMinValue(minValue);
        nq.setMaxValue(maxValue);
    }

    /**
     * @param idForm
     * @param idQuestion
     * @param statement
     * @param isOptional
     * @param options
     * @param minSelections
     * @param maxSelections
     * @throws IllegalArgumentException If the form has already been answered, or
     *                                  propietary user of the form is not currently
     *                                  logged in, or the Question is not
     *                                  of type MultipleChoiceQuestion
     */
    public void modifyMultipleChoiceQuestion(Integer idForm, Integer idQuestion, String statement, Boolean isOptional,
            ArrayList<String> options, Integer minSelections, Integer maxSelections) {
        Form f = getForm(idForm);

        if (f.getUserPropietary() != getLoggedUser()) {
            throw new IllegalArgumentException("Propietary User with ID " + idForm + " is not currently logged");
        }

        if (!f.getAnswers().isEmpty()) {
            throw new IllegalArgumentException(
                    "Form with ID " + idForm + " has already been answered. It cannot be edited");
        }

        Question q = getQuestion(idForm, idQuestion);
        if (!(q instanceof MultipleChoiceQuestion))
            throw new IllegalArgumentException(
                    "Question with Form ID " + idForm + ", and Question ID " + idQuestion + " is not a Multiple Choice Question");

        MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) q;
        mcq.setStatement(statement);
        mcq.setOptional(isOptional);
        mcq.setOptions(options);
        mcq.setMinSelections(minSelections);
        mcq.setMaxSelections(maxSelections);
    }

    /**
     * @param idForm
     * @param idQuestion
     * @param statement
     * @param isOptional
     * @param options
     * @throws IllegalArgumentException If the form has already been answered, or
     *                                  propietary user of the form is not currently
     *                                  logged in, or the Question is not
     *                                  of type SingleChoiceQuestion
     */
    public void modifySingleChoiceQuestion(Integer idForm, Integer idQuestion, String statement,
            Boolean isOptional, ArrayList<String> options) {
        Form f = getForm(idForm);

        if (f.getUserPropietary() != getLoggedUser()) {
            throw new IllegalArgumentException("Propietary User with ID " + idForm + " is not currently logged");
        }

        if (!f.getAnswers().isEmpty()) {
            throw new IllegalArgumentException(
                    "Form with ID " + idForm + " has already been answered. It cannot be edited");
        }

        Question q = getQuestion(idForm, idQuestion);
        if (!(q instanceof MultipleChoiceQuestion))
            throw new IllegalArgumentException(
                    "Question with Form ID " + idForm + ", and Question ID " + idQuestion + " is not a Single Choice Question");

        MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) q;
        mcq.setStatement(statement);
        mcq.setOptional(isOptional);
        mcq.setOptions(options);
    }

    /**
     * @param idForm
     * @param idQuestion
     * @param statement
     * @param isOptional
     * @param maxLength
     * @throws IllegalArgumentException If the form has already been answered, or
     *                                  propietary user of the form is not currently
     *                                  logged in, or the Question is not
     *                                  of type FreeQuestion
     */
    public void modifyFreeQuestion(Integer idForm, Integer idQuestion, String statement, Boolean isOptional,
            Integer maxLength) {
        Form f = getForm(idForm);

        if (f.getUserPropietary() != getLoggedUser()) {
            throw new IllegalArgumentException("Propietary User with ID " + idForm + " is not currently logged");
        }

        if (!f.getAnswers().isEmpty()) {
            throw new IllegalArgumentException(
                    "Form with ID " + idForm + " has already been answered. It cannot be edited");
        }

        Question q = getQuestion(idForm, idQuestion);
        if (!(q instanceof FreeQuestion))
            throw new IllegalArgumentException(
                    "Question with Form ID " + idForm + ", and Question ID " + idQuestion + " is not a Free Question");

        FreeQuestion fq = (FreeQuestion) q;
        fq.setStatement(statement);
        fq.setOptional(isOptional);
        fq.setMaxLength(maxLength);
    }

    /**
     * @param idForm
     * @param idQuestion
     * @param statement
     * @param isOptional
     * @param options
     * @throws IllegalArgumentException If the form has already been answered, or
     *                                  propietary user of the form is not currently
     *                                  logged in, or the Question is not
     *                                  of type OrderedSingleQuestion
     */
    public void modifyOrderedSingleQuestion(Integer idForm, Integer idQuestion, String statement, Boolean isOptional,
            ArrayList<String> options) {
        Form f = getForm(idForm);

        if (f.getUserPropietary() != getLoggedUser()) {
            throw new IllegalArgumentException("Propietary User with ID " + idForm + " is not currently logged");
        }

        if (!f.getAnswers().isEmpty()) {
            throw new IllegalArgumentException(
                    "Form with ID " + idForm + " has already been answered. It cannot be edited");
        }

        Question q = getQuestion(idForm, idQuestion);
        if (!(q instanceof OrderedSingleQuestion))
            throw new IllegalArgumentException(
                    "Question with Form ID " + idForm + ", and Question ID " + idQuestion + " is not an Ordered Single Question");

        OrderedSingleQuestion osq = (OrderedSingleQuestion) q;
        osq.setStatement(statement);
        osq.setOptional(isOptional);
        osq.setOptions(options);
    }

    /**
     * @param idForm
     * @param idQuestion
     * @return the statement of the question with idForm and idQuestion
     */
    public String getQuestionStatement(Integer idForm, Integer idQuestion) {
        Question q = getQuestion(idForm, idQuestion);
        return q.getStatement();
    }

    // ==========================
    // Answer-related use cases
    // ==========================

    /**
     * @param idUser
     * @param idForm
     * @param idQuestion
     * @param answer
     */
    public void addNumericalAnswer(Integer idUser, Integer idForm, Integer idQuestion, Integer answer) {
        Answer a;
        try {
            a = getAnswer(idUser, idForm);
        } catch (EntityNotFoundException e) {
            a = new Answer(idForm, idUser);
            addAnswer(a);
        }

        AnswerNumericalQuestion anq = new AnswerNumericalQuestion(idUser, idForm, idQuestion, answer);
        addAnswerToQuestion(anq);
    }

    /**
     * @param idUser
     * @param idForm
     * @param idQuestion
     * @param answer
     */
    public void addFreeAnswer(Integer idUser, Integer idForm, Integer idQuestion, String answer) {
        Answer a;
        try {
            a = getAnswer(idUser, idForm);
        } catch (EntityNotFoundException e) {
            a = new Answer(idForm, idUser);
            addAnswer(a);
        }

        AnswerFreeQuestion afq = new AnswerFreeQuestion(idUser, idForm, idQuestion, answer);
        addAnswerToQuestion(afq);
    }

    /**
     * @param idUser
     * @param idForm
     * @param idQuestion
     * @param answers
     */
    public void addMultipleChoiceAnswer(Integer idUser, Integer idForm, Integer idQuestion,
            ArrayList<Integer> answers) {
        Answer a;
        try {
            a = getAnswer(idUser, idForm);
        } catch (EntityNotFoundException e) {
            a = new Answer(idForm, idUser);
            addAnswer(a);
        }

        AnswerMultipleChoiceQuestion amcq = new AnswerMultipleChoiceQuestion(idUser, idForm, idQuestion, answers);
        addAnswerToQuestion(amcq);
    }

    /**
     * @param idUser
     * @param idForm
     * @param idQuestion
     * @param selectedOptionIndex
     */
    public void addOrderedSingleAnswer(Integer idUser, Integer idForm, Integer idQuestion,
            Integer selectedOptionIndex) {
        Answer a;
        try {
            a = getAnswer(idUser, idForm);
        } catch (EntityNotFoundException e) {
            a = new Answer(idForm, idUser);
            addAnswer(a);
        }

        AnswerSingleChoiceQuestion ascq = new AnswerSingleChoiceQuestion(idUser, idForm, idQuestion,
                selectedOptionIndex);
        addAnswerToQuestion(ascq);
    }

    /**
     * @param idUser
     * @param idForm
     * @param idQuestion
     * @param newAnswer
     * @throws IllegalArgumentException if type of Question keyed by idQuestion is
     *                                  not a
     *                                  NumericalQuestion.
     */
    public void modifyNumericalAnswer(Integer idUser, Integer idForm, Integer idQuestion, Integer newAnswer) {
        AnswerToQuestion q = getAnswerToQuestion(getLoggedUser(), idForm, idQuestion);
        if (!(q instanceof AnswerNumericalQuestion)) {
            throw new IllegalArgumentException(
                    "Question" + idForm + ", " + idQuestion + " is not a Numerical Question");
        }

        AnswerNumericalQuestion anq = (AnswerNumericalQuestion) q;
        anq.setNumericalValue(newAnswer);
    }

    /**
     * @param idUser
     * @param idForm
     * @param idQuestion
     * @param newAnswer
     * @throws IllegalArgumentException if type of Question keyed by idQuestion is
     *                                  not a
     *                                  FreeQuestion.
     */
    public void modifyFreeAnswer(Integer idUser, Integer idForm, Integer idQuestion, String newAnswer) {
        AnswerToQuestion q = getAnswerToQuestion(getLoggedUser(), idForm, idQuestion);
        if (!(q instanceof AnswerFreeQuestion)) {
            throw new IllegalArgumentException("Question" + idForm + ", " + idQuestion + " is not a Free Question");
        }

        AnswerFreeQuestion afq = (AnswerFreeQuestion) q;
        afq.setAnswerText(newAnswer);
    }

    /**
     * @param idUser
     * @param idForm
     * @param idQuestion
     * @param newAnswers
     * @throws IllegalArgumentException if type of Question keyed by idQuestion is
     *                                  not a
     *                                  MultipleChoiceQuestion.
     */
    public void modifyMultipleChoiceAnswer(Integer idUser, Integer idForm, Integer idQuestion,
            ArrayList<Integer> newAnswers) {
        AnswerToQuestion q = getAnswerToQuestion(getLoggedUser(), idForm, idQuestion);
        if (!(q instanceof AnswerMultipleChoiceQuestion)) {
            throw new IllegalArgumentException(
                    "Question" + idForm + ", " + idQuestion + " is not a Multiple Choice Question");
        }

        AnswerMultipleChoiceQuestion amcq = (AnswerMultipleChoiceQuestion) q;
        amcq.setSelectedOptionIndexes(newAnswers);
    }

    /**
     * @param idUser
     * @param idForm
     * @param idQuestion
     * @param newSelectedOptionIndex
     * @throws IllegalArgumentException if type of Question keyed by idQuestion is
     *                                  not a
     *                                  OrderedSingleQuestion or
     *                                  MultipleChoiceQuestion with
     *                                  {@code minSelected==maxSelected==1}.
     */
    public void modifyOrderedSingleAnswer(Integer idUser, Integer idForm, Integer idQuestion,
            Integer newSelectedOptionIndex) {
        AnswerToQuestion q = getAnswerToQuestion(getLoggedUser(), idForm, idQuestion);
        if (!(q instanceof AnswerSingleChoiceQuestion)) {
            throw new IllegalArgumentException(
                    "Question" + idForm + ", " + idQuestion + " is not a Single Ordered Question" +
                            "or a Multiple Choice Question with minSelections == maxSelections == 1");
        }

        AnswerSingleChoiceQuestion ascq = (AnswerSingleChoiceQuestion) q;
        ascq.setSelectedOptionIndex(newSelectedOptionIndex);
    }

    /**
     * @param idForm
     * @param idQuestion
     * @throws IllegalArgumentException if type of Question keyed by idQuestion is
     *                                  not a
     *                                  NumericalQuestion.
     * @return value answered by User currently logged to Question idQuestion of
     *         Form idForm
     */
    public Integer getNumericalAnswer(Integer idForm, Integer idQuestion) {
        AnswerToQuestion q = getAnswerToQuestion(getLoggedUser(), idForm, idQuestion);
        if (!(q instanceof AnswerNumericalQuestion)) {
            throw new IllegalArgumentException(
                    "Question" + idForm + ", " + idQuestion + " is not a Numerical Question");
        }

        AnswerNumericalQuestion anq = (AnswerNumericalQuestion) q;
        return anq.getNumericalValue();
    }

    /**
     * @param idForm
     * @param idQuestion
     * @throws IllegalArgumentException if type of Question keyed by idQuestion is
     *                                  not a
     *                                  FreeQuestion.
     * @return value answered by User currently logged to Question idQuestion of
     *         Form idForm
     */
    public String getFreeAnswer(Integer idForm, Integer idQuestion) {
        AnswerToQuestion q = getAnswerToQuestion(getLoggedUser(), idForm, idQuestion);
        if (!(q instanceof AnswerFreeQuestion)) {
            throw new IllegalArgumentException("Question" + idForm + ", " + idQuestion + " is not a Free Question");
        }

        AnswerFreeQuestion afq = (AnswerFreeQuestion) q;
        return afq.getAnswerText();
    }

    /**
     * @param idForm
     * @param idQuestion
     * @throws IllegalArgumentException if type of Question keyed by idQuestion is
     *                                  not a
     *                                  MultipleChoiceQuestion.
     * @return value answered by User currently logged to Question idQuestion of
     *         Form idForm
     */
    public ArrayList<Integer> getMultipleChoiceAnswer(Integer idForm, Integer idQuestion) {
        AnswerToQuestion q = getAnswerToQuestion(getLoggedUser(), idForm, idQuestion);
        if (!(q instanceof AnswerMultipleChoiceQuestion)) {
            throw new IllegalArgumentException(
                    "Question" + idForm + ", " + idQuestion + " is not a Multiple Choice Question");
        }

        AnswerMultipleChoiceQuestion amcq = (AnswerMultipleChoiceQuestion) q;
        return amcq.getSelectedOptionIndexes();
    }

    /**
     * @param idForm
     * @param idQuestion
     * @throws IllegalArgumentException if type of Question keyed by idQuestion is
     *                                  not a
     *                                  OrderedSingleQuestion or
     *                                  MultipleChoiceQuestion with
     *                                  {@code minSelected==maxSelected==1}.
     * @return value answered by User currently logged to Question idQuestion of
     *         Form idForm
     */
    public Integer getOrderedSingleAnswer(Integer idForm, Integer idQuestion) {
        AnswerToQuestion q = getAnswerToQuestion(getLoggedUser(), idForm, idQuestion);
        if (!(q instanceof AnswerSingleChoiceQuestion)) {
            throw new IllegalArgumentException(
                    "Question" + idForm + ", " + idQuestion + " is not a Single Ordered Question" +
                            "or a Multiple Choice Question with minSelections == maxSelections == 1");
        }

        AnswerSingleChoiceQuestion ascq = (AnswerSingleChoiceQuestion) q;
        return ascq.getSelectedOptionIndex();
    }

    /**
     *
     * Performs an analysis of the Answers, using a clustering algorithm and
     * grouping
     * them by similarity into numClusters different clusters.
     *
     * @param idForm       the Form keyed by idForm on which to do the analysis
     * @param name         the name to set for the analysis done
     * @param numClusters  the number of clusters to use in the algorithm
     * @param analStrategy the strategy used to perform the analysis
     * @return the unique ID for the just-done analyze.
     * @throws IllegalArgumentException if Form with idForm has no Answers related
     *                                  to it.
     */
    private Integer analyseForm(Integer idForm, String name, Integer numClusters, AnalyzeStrategy analStrategy)
            throws IllegalArgumentException {
        ArrayList<ArrayList<AnswerToQuestion>> answerToQuestions = new ArrayList<ArrayList<AnswerToQuestion>>();
        Form f = getForm(idForm);
        ArrayList<Integer> answers = f.getAnswers();
        for (Integer idUser : answers) {
            Answer a = getAnswer(idUser, idForm);
            ArrayList<Integer> atqs = a.getResponses();
            ArrayList<AnswerToQuestion> atq = new ArrayList<AnswerToQuestion>();

            for (Integer idQuestion : atqs)
                atq.add(getAnswerToQuestion(idUser, idForm, idQuestion));
            answerToQuestions.add(atq);
        }

        try {
            Analyze analysis = new Analyze(idForm, name, numClusters, analStrategy, answerToQuestions);
            return addAnalyze(analysis);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Form" + idForm + "needs to have at least one Answer to do an analysis and/or the number of clusters must be lower than number of responses",e);
        }
    }

    /**
     *
     * Performs an analysis of the Answers using the clustering algorithm KMedoids,
     * grouping
     * them by similarity into numClusters different clusters.
     *
     * @param idForm      the Form keyed by idForm on which to do the analysis
     * @param name        the name to set for the analysis done
     * @param numClusters the number of clusters to use in the algorithm
     * @return the unique ID for the just-done analyze.
     * @throws IllegalArgumentException if Form with idForm has no Answers related
     *                                  to it.
     */
    public Integer analyseFormKMedoids(Integer idForm, String name, Integer numClusters) {
        return analyseForm(idForm, name, numClusters, KMedoidsStrategy.getInstance());
    }

    /**
     *
     * Performs an analysis of the Answers using the clustering algorithm KMeans,
     * grouping
     * them by similarity into numClusters different clusters.
     *
     * @param idForm      the Form keyed by idForm on which to do the analysis
     * @param name        the name to set for the analysis done
     * @param numClusters the number of clusters to use in the algorithm
     * @return the unique ID for the just-done analyze.
     * @throws IllegalArgumentException if Form with idForm has no Answers related
     *                                  to it.
     */
    public Integer analyseFormKMeans(Integer idForm, String name, Integer numClusters) {
        return analyseForm(idForm, name, numClusters, KMeansStrategy.getInstance());
    }

    // ==============================
    // Persistence-Related operations
    // ==============================

    /**
     * Retrieves a RegisteredUser by user ID.
     *
     * @param idUser the ID of the user.
     * @return the RegisteredUser corresponding to the given ID.
     * @throws IllegalArgumentException if the user is not a RegisteredUser.
     */
    private RegisteredUser getRegisteredUser(Integer idUser) {
        User user = getUser(idUser);
        if (user instanceof RegisteredUser) {
            return (RegisteredUser) user;
        } else {
            throw new IllegalArgumentException("User " + idUser + " is not a RegisteredUser");
        }
    }

    /**
     * @param idUser
     * @return
     */
    public ArrayList<Integer> getFormsFromUser(Integer idUser) {
        User user = getUser(idUser);

        if (user instanceof RegisteredUser) {
            RegisteredUser ru = (RegisteredUser) user;
            ArrayList<Integer> forms = ru.getForms();
            return forms != null ? forms : new ArrayList<>();
        } else {
            throw new IllegalArgumentException("User " + idUser + " is not a RegisteredUser");
        }
    }

    // ============================================
    // Opertions to be delegated to CtrlPersistence
    // ============================================

    // =====================================
    // User-related add, get, delete, exists
    // =====================================

    /**
     * @param user
     * @return
     */
    private Integer addUser(User user) {
        try {
            // 1. Add User + set Id
            Integer idUser = _ctrlPersistence.addUser(user);
            user.setIdUser(idUser);
            return idUser; 
        } catch (PersistenceException e) {
            throw new DomainException("CtrlDomain::addUser() method failed.", e);
        }
    }

    /**
     * @param idUser
     * @return
     */
    protected User getUser(Integer idUser) {
        try {
            return _ctrlPersistence.getUser(idUser);
        } catch (PersistenceException e) {
            throw new DomainException("CtrlDomain::getUser() method failed.", e);
        }
    }

    /**
     * @param idUser
     */
    public void deleteUser(Integer idUser) {
        User u = getUser(idUser);
        if (u instanceof RegisteredUser) {
            RegisteredUser ru = (RegisteredUser) u;
            // 1. Delete associated Forms (which is propietary)
            // Iterate over a copy, as deleteForm removes elements from the list
            ArrayList<Integer> forms = new ArrayList<>(ru.getForms());
            for (Integer idForm : forms) {
                deleteForm(idForm);
            }
        }

        // 2. Delete associated Answer
        // Iterate over a copy, as deleteAnswer removes elements from the list
        ArrayList<Integer> answers = new ArrayList<>(u.getAnswers());
        for (Integer idForm : answers) {
            deleteAnswer(idUser, idForm);
        }

        try {
            // 3. Delete User
            _ctrlPersistence.deleteUser(idUser);
        } catch (EntityNotFoundException | PersistenceException e) {
            throw new DomainException("CtrlDomain::deleteUser() method failed.", e);
        }
    }

    /**
     * @param idUser
     * @return
     */
    public Boolean existsUser(Integer idUser) {
        return _ctrlPersistence.existsUser(idUser);
    }

    // =====================================
    // Form-related add, get, delete, exists
    // =====================================

    /**
     * @param form
     * @return
     */
    private Integer addForm(Form form) {
        try {
            // 1. Add Form + setId
            Integer idForm = _ctrlPersistence.addForm(form);
            form.setIdForm(idForm);
        } catch (PersistenceException e) {
            throw new DomainException("CtrlDomain::addForm() method failed.", e);
        }

        // 2. Associate to User
        User u = getUser(form.getUserPropietary());
        if (u instanceof RegisteredUser) {
            RegisteredUser ru = (RegisteredUser) u;
            ru.addForm(form.getIdForm());
        }
        else {
            throw new IllegalArgumentException("User with ID " + form.getUserPropietary() + " is not a RegisteredUser");
        }

        return form.getIdForm();
    }

    /**
     * @param idForm
     * @return
     */
    protected Form getForm(Integer idForm) {
        try {
            return _ctrlPersistence.getForm(idForm);
        } catch (PersistenceException e) {
            throw new DomainException("CtrlDomain::getForm() method failed.", e);
        }
    }

    /**
     * @param idForm
     */
    public void deleteForm(Integer idForm) {
        Form f = getForm(idForm);

        // 1. Delete all associated Answer
        // Iterate over a copy, as deleteAnswer removes elements from the list
        ArrayList<Integer> answers = new ArrayList<>(f.getAnswers());
        for (Integer idUser : answers) {
            Answer a = getAnswer(idUser, idForm);
            deleteAnswer(a.getIdUser(), idForm);
        }

        // 2. Delete all associated Question
        // Iterate over a copy, as deleteQuestion removes elements from the list,
            // which invalidates the iterator and Java would throw ConcurrentModificationException.
        ArrayList<Integer> questions = new ArrayList<>(f.getQuestions());
        for (Integer idQuestion : questions) {
            deleteQuestion(idForm, idQuestion);
        }

        // 3. Delete all associated Analyze
        // Iterate over a copy, as deleteAnalyze removes elements from the list
        ArrayList<Integer> analysis = new ArrayList<>(f.getAnalyze());
        for (Integer id : analysis) {
            deleteAnalyze(idForm, id);
        }

        // 4. Disassociate from Propietary User
        RegisteredUser userPropietary = (RegisteredUser) getUser(f.getUserPropietary());
        userPropietary.removeForm(idForm);

        try {
            // 5. Delete Form
            _ctrlPersistence.deleteForm(idForm);
        } catch (EntityNotFoundException | PersistenceException e) {
            throw new DomainException("CtrlDomain::deleteForm() method failed.", e);
        }
    }

    /**
     * @param idForm
     * @return
     */
    public Boolean existsForm(Integer idForm) {
        return _ctrlPersistence.existsForm(idForm);
    }

    /**
     * Returns an array of the external IDs of all the existing forms
     *
     * @return
     */
    public ArrayList<Integer> getAllForms() {
        return _ctrlPersistence.getAllForms();
    }

    /**
     * Special function which returns basic form info for presentation layer.
     *
     * @param idForm
     * @return
     */
    public Map<String, Object> getFormBasicInfo(Integer idForm) {
        return _ctrlTransformData.getFormBasicInfo(idForm);
    };

    /**
     * Special function which returns info for answering a form for presentation layer.
     *
     * @param idForm
     * @return
     */
    public Map<String, Object> getFormForAnswering(Integer idForm) {
        return _ctrlTransformData.getFormForAnswering(idForm);
    }

    /**
     * Special function which returns info for editing a form for presentation layer.
     *
     * @param idForm
     * @return
     */
    public Map<String, Object> getFormForEditing(Integer idForm) {
        return _ctrlTransformData.getFormForEditing(idForm);
    }

    /**
     * Gets all the published forms a user can answer.
     * 
     * @param idUser User external ID
     * @return List of all the possible Forms which could be answered. 
     */
    public ArrayList<Integer> getContestableForms(Integer idUser) {
        try {
            ArrayList<Integer> contestableForms = new ArrayList<>();

            ArrayList<Integer> allForms = getAllForms();

            for (Integer idForm : allForms) {
                Form f = getForm(idForm);
                if (f.isPublished() == true && f.getUserPropietary() != idUser) {
                    contestableForms.add(idForm);
                }
            }

            return contestableForms;

        } catch (PersistenceException e) {
            throw new DomainException("Failed to getAllForms", e);
        }
    }


    /**
     * Gets different Form info, including its published status. Useful for presentation layer.
     * 
     * @param idForm Form external ID
     * @return Map with form info 
     */
    public Map<String, Object> getFormInfoWithStatus(Integer idForm) {
        Map<String, Object> formInfo = getFormBasicInfo(idForm);
        Form form = getForm(idForm);

        // Añadir información de publicación
        formInfo.put("isPublished", form.isPublished());

        // Añadir información del propietario
        User owner = getUser(form.getUserPropietary());
        if (owner instanceof RegisteredUser) {
            formInfo.put("owner", ((RegisteredUser) owner).getUsername());
        } else {
            formInfo.put("owner", "Guest_" + form.getUserPropietary());
        }

        return formInfo;
    }

    /**
     * Returns the current number of answers a form has.
     *
     * @param idForm the external ID form
     * @return
     */
    public int getFormCompletionCount(Integer idForm) {
        try {
            Form form = getForm(idForm);
            return form.getAnswers().size(); // Cada entrada en answers es un usuario diferente

        } catch (Exception e) {
            throw new DomainException("Getting completion count for form " + idForm + " failed.", e);
        }
    }

    // =======================================
    // Answer-related add, get, delete, exists
    // =======================================

    /**
     * @param answer
     */
    protected void addAnswer(Answer answer) {
        // 1. Associate Answer To Form
        Form f = getForm(answer.getIdForm());
        f.addAnswer(answer.getIdUser());

        // 2. Associate Answer to User
        User u = getUser(answer.getIdUser());
        u.addAnswer(answer.getIdForm());

        try {
            // 3. Add Answer
            _ctrlPersistence.addAnswer(answer);
        } catch (PersistenceException e) {
            throw new DomainException("CtrlDomain::addAnswer() method failed.", e);
        }
    }

    /**
     * @param idUser
     * @param idForm
     * @return
     */
    protected Answer getAnswer(Integer idUser, Integer idForm) {
        try {
            return _ctrlPersistence.getAnswer(idUser, idForm);
        } catch (PersistenceException e) {
            throw new DomainException("CtrlDomain::getAnswer() method failed.", e);
        }
    }

    /**
     * @param idUser
     * @param idForm
     */
    public void deleteAnswer(Integer idUser, Integer idForm) {
        Answer a = getAnswer(idUser, idForm);

        // 1. Delete associated answerToQuestions
        // Iterate over a copy, as deleteAnswerToQuestion removes elements from the list
        ArrayList<Integer> atqs = new ArrayList<>(a.getResponses());
        for (Integer idQuestion : atqs) {
            deleteAnswerToQuestion(idUser, idForm, idQuestion, false);
        }

        // 2. Disassociate from Form
        Form f = getForm(idForm);
        f.removeAnswer(idUser);

        // 3. Disassociate from User
        User u = getUser(idUser);
        u.removeAnswer(idForm);

        try {
            // 3. Delete Answer
            _ctrlPersistence.deleteAnswer(idUser, idForm);
        } catch (EntityNotFoundException | PersistenceException e) {
            throw new DomainException("CtrlDomain::deleteAnswer() method failed.", e);
        }
    }

    /**
     * @param idUser
     * @param idForm
     * @return
     */
    public Boolean existsAnswer(Integer idUser, Integer idForm) {
        return _ctrlPersistence.existsAnswer(idUser, idForm);
    }

    /**
     * Returns the array of idUsers which has answered the given idForm
     *
     * @param idForm
     * @return
     */
    public ArrayList<Integer> getAnswersFromForm(Integer idForm) {
        Form f = getForm(idForm);
        return f.getAnswers();
    }

    /**
     * Returns the array of idForms which has been answered by the given idUser
     *
     * @param idUser
     * @return
     */
    public ArrayList<Integer> getAnswersFromUser(Integer idUser) {
        User u = getUser(idUser);
        return u.getAnswers();
    }

    /**
     * Returns Answer info detailed for usage in ctrlPresentation.
     *
     * @param idForm
     * @param idUser
     * @return
     */
    public ArrayList<Object> getAnswerForViewing(Integer idForm, Integer idUser) {
        return _ctrlTransformData.getAnswer(idForm, idUser);
    }

    /**
     * Saves user answers to a form using data from a map.
     * 
     * @param idUser the user identifier
     * @param idForm the form identifier
     * @param answersData a Map containing questionId -> answer pairs
     * @throws RuntimeException if an error occurs while saving answers
     */
    public void saveAnswers(Integer idUser, Integer idForm, Map<Integer, Object> answersData) {
        _ctrlTransformData.saveAnswers(idUser, idForm, answersData);
    }

    // =========================================
    // Question-related add, get, delete, exists
    // =========================================

    /**
     * @param question
     * @return
     */
    private Integer addQuestion(Question question) {
        try {
            // 1. Add Question + set Id
            Integer idQuestion = _ctrlPersistence.addQuestion(question);
            question.setIdQuestion(idQuestion);

        } catch (PersistenceException e) {
            throw new DomainException("CtrlDomain::addQuestion() method failed.", e);
        }

        // 2. Associate Question to Form
        Form f = getForm(question.getIdForm());
        f.addQuestion(question.getIdQuestion());

        return question.getIdQuestion();
    }

    // Public: made public as outside-package domain uses it (AnalyzeStrategy,
    // KMeans)
    // BE-AWARE: The Presentation Layer is not intended to use it.
    /**
     * @param idForm
     * @param idQuestion
     * @return
     */
    public Question getQuestion(Integer idForm, Integer idQuestion) {
        try {
            return _ctrlPersistence.getQuestion(idForm, idQuestion);
        } catch (PersistenceException e) {
            throw new DomainException("CtrlDomain::getQuestion() method failed.", e);
        }
    }

    /**
     * @param idForm
     * @param idQuestion
     */
    public void deleteQuestion(Integer idForm, Integer idQuestion) {
        // 1. Delete associated AnswerToQuestions
        Form f = getForm(idForm);
        // Iterate over a copy, as deleteAnswerToQuestion, if an answer is left
            // empty, may remove that answer and remove elements from this list.
        ArrayList<Integer> answers = new ArrayList<>(f.getAnswers());
        for (Integer idUser : answers) {
            deleteAnswerToQuestion(idUser, idForm, idQuestion);
        }

        // 2. Disassociate from Form
        f.removeQuestion(idQuestion);

        try {
            // 3. Delete Question
            _ctrlPersistence.deleteQuestion(idForm, idQuestion);
        } catch (EntityNotFoundException | PersistenceException e) {
            throw new DomainException("CtrlDomain::deleteQuestion() method failed.", e);
        }
    }

    /**
     * @param idForm
     * @param idQuestion
     * @return
     */
    public Boolean existsQuestion(Integer idForm, Integer idQuestion) {
        return _ctrlPersistence.existsQuestion(idForm, idQuestion);
    }

    // =================================================
    // AnswerToQuestion-related add, get, delete, exists
    // =================================================

    /**
     * @param answerToQuestion
     */
    protected void addAnswerToQuestion(AnswerToQuestion answerToQuestion) {
        try {
            // 1. Associate to Answer
            Answer a = getAnswer(answerToQuestion.getIdUser(), answerToQuestion.getIdForm());
            a.addResponse(answerToQuestion.getIdQuestion());

            // 2. Add AnswerToQuestion
            _ctrlPersistence.addAnswerToQuestion(answerToQuestion);

        } catch (PersistenceException e) {
            throw new DomainException("CtrlDomain::addAnswerToQuestion() method failed.", e);
        }
    }

    /**
     * @param idUser
     * @param idForm
     * @param idQuestion
     * @return
     */
    protected AnswerToQuestion getAnswerToQuestion(Integer idUser, Integer idForm, Integer idQuestion) {
        try {
            return _ctrlPersistence.getAnswerToQuestion(idUser, idForm, idQuestion);

        } catch (PersistenceException e) {
            throw new DomainException("CtrlDomain::getAnswerToQuestion() method failed.", e);
        }
    }

    /**
     * @param idUser
     * @param idForm
     * @param idQuestion
     */
    public void deleteAnswerToQuestion(Integer idUser, Integer idForm, Integer idQuestion) {
        deleteAnswerToQuestion(idUser, idForm, idQuestion, true);
    }

    private void deleteAnswerToQuestion(Integer idUser, Integer idForm, Integer idQuestion, boolean checkEmptyAnswer) {
        try {
            // 1. Disassociate from Answer
            Answer a = getAnswer(idUser, idForm);
            a.deleteResponse(idQuestion);

            // 2. Delete AnswerToQuestion
            _ctrlPersistence.deleteAnswerToQuestion(idUser, idForm, idQuestion);

            if (checkEmptyAnswer && a.getResponses().isEmpty()) deleteAnswer(idUser, idForm);
        } catch (EntityNotFoundException | PersistenceException e) {
            throw new DomainException("CtrlDomain::deleteAnswerToQuestion() method failed.", e);
        }
    }

    /**
     * @param idUser
     * @param idForm
     * @param idQuestion
     * @return
     */
    public Boolean existsAnswerToQuestion(Integer idUser, Integer idForm, Integer idQuestion) {
        return _ctrlPersistence.existsAnswerToQuestion(idUser, idForm, idQuestion);
    }

    // =====================================
    // Analyze-related add, get, delete, exists
    // =====================================

    /**
     * @param analyze
     * @return
     */
    protected Integer addAnalyze(Analyze analyze) {
        try {
            // 1. Add Analyze + set Id
            Integer idAnalyze = _ctrlPersistence.addAnalyze(analyze);
            analyze.setIdAnalyze(idAnalyze);

        } catch (PersistenceException e) {
            throw new DomainException("CtrlDomain::addAnalyze() method failed.", e);
        }

        // 2. Associate to form
        Form f = getForm(analyze.getIdForm());
        f.addAnalyze(analyze.getIdAnalyze());

        return analyze.getIdAnalyze();
    }

    /**
     * @param idForm
     * @param idAnalyze
     * @return
     */
    protected Analyze getAnalyze(Integer idForm, Integer idAnalyze) {
        try {
            return _ctrlPersistence.getAnalyze(idForm, idAnalyze);
        } catch (PersistenceException e) {
            throw new DomainException("CtrlDomain::getAnalyze() method failed.", e);
        }
    }

    /**
     * @param idForm
     * @param idAnalyze
     */
    public void deleteAnalyze(Integer idForm, Integer idAnalyze) {
        try {
            // 1. Disassociate from form
            Form f = getForm(idForm);
            f.removeAnalyze(idAnalyze);

            // 2. Delete Analyze
            _ctrlPersistence.deleteAnalyze(idForm, idAnalyze);
        } catch (EntityNotFoundException | PersistenceException e) {
            throw new DomainException("CtrlDomain::deleteAnalyze() method failed.", e);
        }
    }

    /**
     * @param idForm
     * @param idAnalyze
     * @return
     */
    public Boolean existsAnalyze(Integer idForm, Integer idAnalyze) {
        return _ctrlPersistence.existsAnalyze(idForm, idAnalyze);
    }


    public double[][] getAnswerDistances (Integer idForm) {
        ArrayList<ArrayList<AnswerToQuestion>> answerToQuestions = new ArrayList<ArrayList<AnswerToQuestion>>();
        Form f = getForm(idForm);
        ArrayList<Integer> answers = f.getAnswers();
        for (Integer idUser : answers) {
            Answer a = getAnswer(idUser, idForm);
            ArrayList<Integer> atqs = a.getResponses();
            ArrayList<AnswerToQuestion> atq = new ArrayList<AnswerToQuestion>();

            for (Integer idQuestion : atqs)
                atq.add(getAnswerToQuestion(idUser, idForm, idQuestion));
            answerToQuestions.add(atq);
        }
        return _ctrlTransformData.getAnswersDistanceMatrix(answerToQuestions);
    }
    /**
       Given the ids of a from it returns an array of its answers converted into coordinates in a
       multidimensional plane with normalized coordinate values between 0 and 1.
    **/
    public ArrayList<ArrayList<Double>> getAnswerCoordinates (Integer idForm) {
        ArrayList<ArrayList<AnswerToQuestion>> answerToQuestions = new ArrayList<ArrayList<AnswerToQuestion>>();
        Form f = getForm(idForm);
        ArrayList<Integer> answers = f.getAnswers();
        for (Integer idUser : answers) {
            Answer a = getAnswer(idUser, idForm);
            ArrayList<Integer> atqs = a.getResponses();
            ArrayList<AnswerToQuestion> atq = new ArrayList<AnswerToQuestion>();

            for (Integer idQuestion : atqs)
                atq.add(getAnswerToQuestion(idUser, idForm, idQuestion));
            answerToQuestions.add(atq);
        }
        return _ctrlTransformData.getAnswersCoordinates(answerToQuestions);
    }

    public ArrayList<ArrayList<Double>> getCentroidsCoordinates (Integer idAnalysis, Integer idForm) {
        Analyze analysis = getAnalyze(idForm, idAnalysis);
        ArrayList<ArrayList<AnswerToQuestion>> centroids = analysis.getResult().getCentroids();
        return _ctrlTransformData.getAnswersCoordinates(centroids);
    }

    // public Map<String, Object> getAnalyzeForViewing(Integer idForm, Integer idAnalyze) {
    //     return _ctrlTransformData.getAnalyzeForViewing(Integer idForm, Integer idAnalyze);
    // }

    // Canvis a desfer:
    public ArrayList<Integer> getAnalysisIds (Integer idForm) {
        Form form = getForm(idForm);
        return form.getAnalyze();
    }

    public ArrayList<ArrayList<Object>> getAnalysisCentroids (Integer idAnalysis, Integer idForm) {
        Analyze analysis = getAnalyze(idForm, idAnalysis);
        ArrayList<ArrayList<AnswerToQuestion>> centroids = analysis.getResult().getCentroids();
        return _ctrlTransformData.transformAnswers(centroids);
    }

    public Integer getAnalysisNumClusters (Integer idAnalysis, Integer idForm) {
        Analyze analysis = getAnalyze(idForm, idAnalysis);
        return analysis.getNClusters();
    }

    public Double getAnalysisQuality (Integer idAnalysis, Integer idForm) {
        Analyze analysis = getAnalyze(idForm, idAnalysis);
        return analysis.getResult().getQuality();
    }

    public LocalDateTime getAnalysisDate (Integer idAnalysis, Integer idForm) {
        Analyze analysis = getAnalyze(idForm, idAnalysis);
        return analysis.getTime();
    }

    public String getAnalysisName (Integer idAnalysis, Integer idForm) {
        Analyze analysis = getAnalyze(idForm, idAnalysis);
        return analysis.getName();
    }

    public int[] getAnalysisAssignations (Integer idAnalysis, Integer idForm) {
        Analyze analysis = getAnalyze(idForm, idAnalysis);
        return analysis.getResult().getClusterAssignments();
    }

    /**
     * Closes the domain layer and triggers persistence of data.
     *
     * IT IS **MANDATORY** TO CALL IT BEFORE THE CLOSING OF THE PROGRAM FOR THE CORRECT
     *  FUNCTIONING OF THIS PROGRAM (FOR DATA TO BE PERSISTENT AND NOT CORRUPTED).
     *
     * @throws DomainException if an error occurs during closing
     */
    public void close() {
        try {
            _ctrlPersistence.close();
        } catch (PersistenceException e) {
            throw new DomainException("Failed to close domain layer", e);
        }
    }
}
