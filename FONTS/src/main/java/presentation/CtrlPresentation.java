package presentation;

import domaincontrollers.CtrlDomain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.time.LocalDateTime;

/**
 * @author Joel Forcadell (joel.forcadell@estudiantat.upc.edu)
 * @author Artur Leivar (artur.leivar@estudiantat.upc.edu)
 * @author Pau Turró Gómez (pau.turro.gomez@estudiantat.upc.edu)
 * @author Samuel Gil (samuel.gil@estudiantat.upc.edu)
 *
 * Responsible of communicating the domain layer with the presentation layer.
 *
 **/
public class CtrlPresentation {
    private LoginView vistaLogIn;
    private CtrlDomain _ctrlDomain;
    private static CtrlPresentation instance;

    /**
     * Retrieves the singleton instance of the Presentation Controller.
     * Creates a new instance if one does not already exist.
     *
     * @return The singleton instance of CtrlPresentation.
     * @throws IllegalStateException If the instance cannot be created.
     */
    public static CtrlPresentation getInstance() throws IllegalStateException {
        if (instance == null) {
            instance = new CtrlPresentation();
        }
        return instance;
    }

    private CtrlPresentation() {
        _ctrlDomain = CtrlDomain.getInstance();
        vistaLogIn = new LoginView(this);
    }

    /**
     * Initializes the main presentation logic, making the initial Login View visible.
     */
    public void inicializarPresentacion() {
        vistaLogIn.makeVisible();
    }

    // ======================
    // User
    // ======================

    /**
     * Checks if a user exists in the system.
     *
     * @param userId The unique identifier of the user.
     * @return True if the user exists, false otherwise.
     */
    public boolean existsUser (Integer userId) {
        return _ctrlDomain.existsUser(userId);
    }

    /**
     * Attempts to login a user with password.
     * 
     * @param userId   the user ID
     * @param password the password
     * @throws IllegalArgumentException if login fails
     */
    public void loginUser(Integer userId, String password) throws IllegalArgumentException {
        try {
            _ctrlDomain.loginUser(userId, password);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Login failed: " + e.getMessage(), e);
        }
    }

    /**
     * Creates a new temporary anonymous user (Guest) in the system.
     *
     * @return The ID assigned to the new anonymous user.
     * @throws RuntimeException If the creation fails in the domain layer.
     */
    public Integer createAnonymousUser() {
        try {
            return _ctrlDomain.createAnonymousUser();
        } catch (Exception e) {
            System.err.println("Error creating anonymous user: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Could not create guest user: " + e.getMessage());
        }
    }

    /**
     * Registers a new registered user with the provided credentials.
     *
     * @param username             The desired display name.
     * @param password             The user's password.
     * @param passwordConfirmation The password confirmation (must match password).
     * @param email                The user's email address.
     * @return The ID of the newly created user.
     */
    public Integer createUser(String username, String password, String passwordConfirmation, String email) {
        return _ctrlDomain.createUser(username, password, passwordConfirmation, email);
    }

    /**
     * Deletes a user from the system permanently.
     *
     * @param idUser The ID of the user to delete.
     */
    public void deleteUser(Integer idUser) {
        _ctrlDomain.deleteUser(idUser);
    }

    /**
     * Gets the username for a user.
     * 
     * @param userId the user ID
     * @return the username or display name
     */
    public String getUsername(Integer userId) {
        try {
            // Llama al método ya implementado en CtrlDomain
            return _ctrlDomain.getUsername(userId);
        } catch (Exception e) {
            System.err.println("Error getting username for user " + userId + ": " + e.getMessage());
            e.printStackTrace();
            return "Unknown";
        }
    }

    /**
     * Logs out the currently active user in the domain layer.
     */
    public void logout()
    {
        _ctrlDomain.logoutUser();
    }

    /**
     * Retrieves the ID of the currently logged-in user.
     *
     * @return The user ID, or -1/null if no user is logged in.
     */
    public Integer getLoggedUser() {
        return _ctrlDomain.getLoggedUser();
    }

    /**
     * Gets the email address associated with a user.
     *
     * @param idUser The user ID.
     * @return The email string.
     */
    public String getUserEmail(Integer idUser) {
        return _ctrlDomain.getUserEmail(idUser);
    }

    /**
     * Updates the username of a specific user.
     *
     * @param idUser  The ID of the user to update.
     * @param newName The new username to set.
     */
    public void changeUsername(Integer idUser, String newName) {
        _ctrlDomain.changeUsername(idUser, newName);
    }

    /**
     * Updates the email address of a specific user.
     *
     * @param idUser   The ID of the user to update.
     * @param newEmail The new email address to set.
     */
    public void changeUserEmail(Integer idUser, String newEmail) {
        _ctrlDomain.changeUserEmail(idUser, newEmail);
    }

    /**
     * Attempts to change the password for a user.
     *
     * @param userId          The ID of the user.
     * @param currentPassword The current password for verification.
     * @param newPassword     The new password to set.
     * @return True if the change was successful.
     * @throws SecurityException        If the current password is incorrect.
     * @throws IllegalArgumentException If the new password format is invalid.
     */
    public boolean changePassword(Integer userId, String currentPassword, String newPassword)
            throws SecurityException, IllegalArgumentException {
        _ctrlDomain.changeUserPassword(userId, currentPassword, newPassword);
        return true;
    }

    /**
     * Retrieves the list of Form IDs created by a specific user.
     *
     * @param idUser The user ID.
     * @return An ArrayList containing the IDs of the forms owned by the user.
     */
    public ArrayList<Integer> getForms(Integer idUser) {
        try {
            return _ctrlDomain.getFormsFromUser(idUser);
        } catch (Exception e) {
            System.err.println("Error getting user forms: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene todos los formularios que el usuario puede contestar.
     *
     * @param userId ID del usuario
     * @return Lista de IDs de formularios contestables
     */
    public ArrayList<Integer> getContestableForms(Integer userId) {
        try {
            return _ctrlDomain.getContestableForms(userId);
        } catch (Exception e) {
            System.err.println("Error getting contestable forms for user " + userId + ": " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Gets the count of forms owned by a user.
     *
     * @param userId The user ID.
     * @return The number of forms.
     */
    public int getActiveFormsCount(Integer userId) {
        return getForms(userId).size();
    }

    /**
     * Calculates the total number of responses a user has submitted across all forms.
     *
     * @param userId The user ID.
     * @return The total count of answers submitted.
     */
    public int getTotalResponsesCount(Integer userId) {
        try {
            return _ctrlDomain.getAnswersFromUser(userId).size();
        } catch (Exception e) {
            System.err.println("Error getting user Answers: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    // ====================
    // Form
    // ====================

    /**
     * Retrieves basic information about a form in a presentation-friendly map format.
     *
     * @param idForm the form identifier
     * @return a Map containing: id, name, ownerId, questionCount, answerCount
     *         or an error entry if an exception occurs
     */
    public Map<String, Object> getFormBasicInfo(Integer idForm) {
        try {
            return _ctrlDomain.getFormBasicInfo(idForm);
        } catch (Exception e) {
            System.err.println("Error getting form info: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> fallbackInfo = new HashMap<>();
            fallbackInfo.put("name", "Form " + idForm);
            fallbackInfo.put("responseCount", 0);
            fallbackInfo.put("createdDate", "Unknown date");
            return fallbackInfo;
        }
    }

    /**
     * Retrieves complete form data for answering purposes in a presentation-friendly map format.
     *
     * @param idForm the form identifier
     * @return a Map containing: id, name, ownerId, questions list, and totalQuestions
     *         or an error entry if an exception occurs
     */
    public Map<String, Object> getFormForAnswering(Integer idForm) {
        try {
            return _ctrlDomain.getFormForAnswering(idForm);

        } catch (Exception e) {
            System.err.println("Error getting form for answering: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> fallbackInfo = new HashMap<>();
            fallbackInfo.put("name", "Form " + idForm);
            fallbackInfo.put("responseCount", 0);
            fallbackInfo.put("createdDate", "Unknown date");
            return fallbackInfo;
        }
    }

    /**
     * Retrieves form data for editing purposes in a presentation-friendly map format.
     *
     * @param idForm the form identifier
     * @return a Map containing: id, name, ownerId, hasAnswers, canEdit, questions list, and questionCount
     *         or an error entry if an exception occurs
     */
    public Map<String, Object> getFormForEditing(Integer idForm) {
        try {
            return _ctrlDomain.getFormForEditing(idForm);

        } catch (Exception e) {
            System.err.println("Error getting form for editing: " + e.getMessage());
            Map<String, Object> fallbackInfo = new HashMap<>();
            fallbackInfo.put("name", "Form " + idForm);
            fallbackInfo.put("responseCount", 0);
            fallbackInfo.put("createdDate", "Unknown date");
            return fallbackInfo;
        }
    }

    /**
     * Obtiene información completa del formulario incluyendo estado de publicación.
     *
     * @param formId ID del formulario
     * @return Mapa con información del formulario
     */
    public Map<String, Object> getFormInfoWithStatus(Integer formId) {
        try {
            return _ctrlDomain.getFormInfoWithStatus(formId);
        } catch (Exception e) {
            System.err.println("Error getting form info with status: " + e.getMessage());
            e.printStackTrace();

            // Información de fallback
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("name", "Form " + formId);
            fallback.put("owner", "Unknown");
            fallback.put("isPublished", false);
            fallback.put("createdDate", "Unknown date");
            return fallback;
        }
    }

    /**
     * Retrieves all answers submitted for a specific form.
     *
     * @param idForm The ID of the form.
     * @return A list of answers each one represented as a list of Object. Each object represents
     * an answer to a question and can be an Integer (numerical answers or for ordered single choice
     * answers), a set of Strings (multiple choice) or a single String (Free answers)
     */
    public ArrayList<ArrayList<Object>> getFormAnswers(Integer idForm) {
        ArrayList<Integer> idAnswers = _ctrlDomain.getAnswersFromForm(idForm);
        int nAns = idAnswers.size();
        ArrayList<ArrayList<Object>> result = new ArrayList<ArrayList<Object>>(nAns);
        for (int i = 0; i < nAns; i++) {
            result.add(_ctrlDomain.getAnswerForViewing(idForm, idAnswers.get(i)));
        }
        return result;
    }

    /**
     * Obtiene el número de veces que un formulario ha sido completado.
     *
     * @param formId ID del formulario
     * @return Número de envíos completos
     */
    public int getFormCompletionCount(Integer formId) {
        try {
            return _ctrlDomain.getFormCompletionCount(formId);
        } catch (Exception e) {
            System.err.println("Error getting form completion count: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Verifica si un usuario ya ha contestado un formulario.
     *
     * @param userId ID del usuario
     * @param formId ID del formulario
     * @return true si ya ha contestado
     */
    public boolean hasUserAnsweredForm(Integer userId, Integer formId) {
        try {
            return _ctrlDomain.hasUserAnsweredForm(userId, formId);
        } catch (Exception e) {
            System.err.println("Error checking if user answered form: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Verifica si un formulario está publicado.
     *
     * @param formId ID del formulario
     * @return true si está publicado
     */
    public boolean isFormPublished(Integer formId) {
        try {
            return _ctrlDomain.isFormPublished(formId);
        } catch (Exception e) {
            System.err.println("Error checking form publish status " + formId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Publica un formulario.
     *
     * @param formId ID del formulario a publicar
     * @return true si se publicó correctamente
     */
    public boolean publishForm(Integer formId) {
        try {
            _ctrlDomain.publishForm(formId);
            return true;
        } catch (Exception e) {
            System.err.println("Error publishing form " + formId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Despublica un formulario.
     *
     * @param formId ID del formulario a despublicar
     * @return true si se despublicó correctamente
     */
    public boolean unpublishForm(Integer formId) {
        try {
            _ctrlDomain.unpublishForm(formId);
            return true;
        } catch (Exception e) {
            System.err.println("Error unpublishing form " + formId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Creates a new empty form.
     *
     * @param idPropietary The ID of the user creating the form.
     * @param name         The title of the new form.
     * @return The ID of the newly created form.
     */
    public Integer createForm(Integer idPropietary, String name) {
        return _ctrlDomain.createForm(idPropietary, name);
    }

    /**
     * Renames an existing form.
     *
     * @param idForm    The ID of the form to modify.
     * @param formTitle The new title for the form.
     */
    public void modifyForm(Integer idForm, String formTitle) {
        _ctrlDomain.modifyFormName(idForm, formTitle);
    }

    /**
     * Elimina un formulario si el usuario actual es el propietario.
     *
     * @param formId ID del formulario a eliminar
     * @throws IllegalArgumentException si no se puede eliminar el formulario
     */
    public void deleteForm(Integer formId) {
        _ctrlDomain.deleteFormPropietaryLogged(formId);
    }

    /**
     * Updates the order of questions within a form.
     *
     * @param idForm   The ID of the form.
     * @param newOrder A list of Question IDs representing the new sequence.
     */
    public void reorderFormQuestions(Integer idForm, List<Integer> newOrder) {
        _ctrlDomain.reorderFormQuestions(idForm, newOrder);
    }

    // ====================
    // Question CRUD
    // ====================

    /**
     * Adds a new Numerical Question to a form.
     *
     * @param idForm     The form ID.
     * @param statement  The question text.
     * @param isOptional Whether the question is optional.
     * @param minValue   The minimum acceptable value.
     * @param maxValue   The maximum acceptable value.
     * @return The ID of the created question.
     */
    public Integer addNumericalQuestion(Integer idForm, String statement,
            Boolean isOptional, Integer minValue, Integer maxValue) {
        return _ctrlDomain.addNumericalQuestion(idForm, statement, isOptional, minValue, maxValue);
    }

    /**
     * Adds a new Multiple Choice Question to a form.
     *
     * @param idForm        The form ID.
     * @param statement     The question text.
     * @param isOptional    Whether the question is optional.
     * @param options       List of text options.
     * @param minSelections Minimum number of options to select.
     * @param maxSelections Maximum number of options to select.
     * @return The ID of the created question.
     */
    public Integer addMultipleChoiceQuestion(Integer idForm, String statement, Boolean isOptional,
            ArrayList<String> options, Integer minSelections, Integer maxSelections) {
        return _ctrlDomain.addMultipleChoiceQuestion(idForm, statement, isOptional, options, minSelections,
                maxSelections);
    }

    /**
     * Adds a new Free Question to a form.
     *
     * @param idForm        The form ID.
     * @param statement     The question text.
     * @param isOptional    Whether the question is optional.
     * @param maxLength     maximum length of the answer
     * @return The ID of the created question.
     */
    public Integer addFreeQuestion(Integer idForm, String statement, Boolean isOptional, Integer maxLength) {
        return _ctrlDomain.addFreeQuestion(idForm, statement, isOptional, maxLength);
    }

    /**
     * Adds a new Single Choice Question to a form.
     *
     * @param idForm        The form ID.
     * @param statement     The question text.
     * @param isOptional    Whether the question is optional.
     * @param options       List of text options.
     * @return The ID of the created question.
     */
    public Integer addOrderedSingleQuestion(Integer idForm, String statement, Boolean isOptional,
            ArrayList<String> options) {
        return _ctrlDomain.addOrderedSingleQuestion(idForm, statement, isOptional, options);
    }

    /**
     * Modifies an existing Numerical Question.
     *
     * @param idForm     The form ID.
     * @param idQuestion The ID of the question to modify.
     * @param statement  The new question text.
     * @param isOptional The new optional status.
     * @param minValue   The new minimum value.
     * @param maxValue   The new maximum value.
     */
    public void modifyNumericalQuestion(Integer idForm, Integer idQuestion, String statement,
            Boolean isOptional, Integer minValue, Integer maxValue) {
        _ctrlDomain.modifyNumericalQuestion(idForm, idQuestion, statement, isOptional, minValue, maxValue);
    }

    /**
     * Modifies an existing Numerical Question.
     *
     * @param idForm     The form ID.
     * @param idQuestion The ID of the question to modify.
     * @param statement  The new question text.
     * @param isOptional The new optional status.
     * @param options    The new list of options.
     * @param minSelections   The new minimum amount of selections allowed.
     * @param maxSelections   The new maximum amount of selections allowed.
     */
    public void modifyMultipleChoiceQuestion(Integer idForm, Integer idQuestion, String statement, Boolean isOptional,
            ArrayList<String> options, Integer minSelections, Integer maxSelections) {
        _ctrlDomain.modifyMultipleChoiceQuestion(idForm, idQuestion, statement, isOptional, options, minSelections,
                maxSelections);
    }

    /**
     * Modifies an existing Numerical Question.
     *
     * @param idForm     The form ID.
     * @param idQuestion The ID of the question to modify.
     * @param statement  The new question text.
     * @param isOptional The new optional status.
     * @param maxLength  The new maximum length.
     */
    public void modifyFreeQuestion(Integer idForm, Integer idQuestion, String statement, Boolean isOptional,
            Integer maxLength) {
        _ctrlDomain.modifyFreeQuestion(idForm, idQuestion, statement, isOptional, maxLength);
    }

    /**
     * Modifies an existing Numerical Question.
     *
     * @param idForm     The form ID.
     * @param idQuestion The ID of the question to modify.
     * @param statement  The new question text.
     * @param isOptional The new optional status.
     * @param options    The new options to select
     */
    public void modifyOrderedSingleQuestion(Integer idForm, Integer idQuestion, String statement, Boolean isOptional,
            ArrayList<String> options) {
        _ctrlDomain.modifyOrderedSingleQuestion(idForm, idQuestion, statement, isOptional, options);
    }

    /**
     * Deletes a specific question from a form.
     *
     * @param idForm     The form ID.
     * @param idQuestion The ID of the question to delete.
     */
    public void deleteQuestion(Integer idForm, Integer idQuestion) {
        _ctrlDomain.deleteQuestionPropietaryLogged(idForm, idQuestion);
    }

    // ===============================
    // Answers
    // ===============================

    /**
     * Creates a new answer to question of a free question
     *
     * @param userId     The id of the user who answered
     * @param formId     The ID of the form which the user is answering.
     * @param questionId The ID of the question being answered
     * @param text       The value of the answer.
     */
    public void addFreeAnswer(Integer userId, Integer formId, Integer questionId, String text) {
        _ctrlDomain.addFreeAnswer(userId, formId, questionId, text);
    }

    /**
     * Creates a new answer to question of a numerical question
     *
     * @param userId     The id of the user who answered
     * @param formId     The ID of the form which the user is answering.
     * @param questionId The ID of the question being answered
     * @param value       The value of the answer.
     */
    public void addNumericalAnswer(Integer userId, Integer formId, Integer questionId, Integer value) {
        _ctrlDomain.addNumericalAnswer(userId, formId, questionId, value);
    }

    /**
     * Creates a new answer to question of a multiple choice question
     *
     * @param userId     The id of the user who answered
     * @param formId     The ID of the form which the user is answering.
     * @param questionId The ID of the question being answered
     * @param selected   The options selected by the user.
     */
    public void addMultipleChoiceAnswer(Integer userId, Integer formId, Integer questionId,
            ArrayList<Integer> selected) {
        _ctrlDomain.addMultipleChoiceAnswer(userId, formId, questionId, selected);
    }

    /**
     * Creates a new answer to question of a free question
     *
     * @param userId        The id of the user who answered
     * @param formId        The ID of the form which the user is answering.
     * @param questionId    The ID of the question being answered
     * @param selectedIndex The selected index by the user
     */
    public void addOrderedSingleAnswer(Integer userId, Integer formId, Integer questionId, Integer selectedIndex) {
        _ctrlDomain.addOrderedSingleAnswer(userId, formId, questionId, selectedIndex);
    }

    /**
     * Persists a set of answers for a user and form.
     *
     * @param idUser      The ID of the user submitting the answers.
     * @param idForm      The ID of the form being answered.
     * @param answersData A map containing question IDs and their corresponding answer values.
     */
    public void saveAnswers(Integer idUser, Integer idForm, Map<Integer, Object> answersData) {
        _ctrlDomain.saveAnswers(idUser, idForm, answersData);
    }

    // =========================
    // Analysis
    // =========================

    /**
     * Creates a new analysis for a form using the clustering algorithm specified
     * @param idForm        The id of the analyzed form
     * @param name          The name given to the analysis
     * @param numClusters   The number of clusters of the analysis
     * @param algorithm     The used algorithm. Two options: "KMedoids", "KMeans"
     * @return  the id of the created analysis
     */
    public Integer doAnalysis(Integer idForm, String name, Integer numClusters, String algorithm) {
        try {
            if (algorithm.equals("KMedoids")) {
                return _ctrlDomain.analyseFormKMedoids(idForm, name, numClusters);
            } else if (algorithm.equals("KMeans")){
                return _ctrlDomain.analyseFormKMeans(idForm, name, numClusters);
            } else {
                throw new Exception("Algorisme seleccionat erròniament");
            }
        } catch (Exception e) {
            System.err.println("Anàlisis fallit amb missatge: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Given an id for a form returns a distance matrix of its answers
     * @param idForm Id of the form
     * @return distance matrix of the form's answers.\
     *         Result[i][j] contains the distance between the answers i-th and j-th answer of the form
     *         following the distance calculations used for the analysis
     */
    public double[][] getAnswersDistances (Integer idForm) {
        return _ctrlDomain.getAnswerDistances(idForm);
    }

    /**
     * Returns the ids of the analysis of a given form
     * @param idForm id of the form
     * @return A list of the ids of all the analysis made for the form
     */
    public ArrayList<Integer> getAnalysisIds (Integer idForm) {
        return _ctrlDomain.getAnalysisIds(idForm);
    }

    /**
     * Given an analysis it returns the value of the centroids of the analysis
     * @param idAnalysis id of the analysis
     * @param idForm     id of the form the analysis analyses
     * @return A list of answers which are the value of the centroids of the analysis (or medoids)
     *         Each answer is represented using an array of objects as in { {@code @Link}  getFormAnswers(Integer) }
     */
    public ArrayList<ArrayList<Object>> getAnalysisCentroids (Integer idAnalysis, Integer idForm) {
        return _ctrlDomain.getAnalysisCentroids(idAnalysis, idForm);
    }

    /**
     * Given an analysis it returns the coordenates of its centroids
     * @param idAnalysis id of the analysis
     * @param idForm     id of the form being analyzed
     * @return An array of the coordinates of the centroids of the analysis.
     *         How coordinates values are calculated is specified in ctrlTransformData
     */
    public ArrayList<ArrayList<Double>> getAnalysisCentroidsCoords (Integer idAnalysis, Integer idForm) {
        return _ctrlDomain.getCentroidsCoordinates(idAnalysis, idForm);
    }

    /**
     * Returns the number of clusters of an analysis
     * @param idAnalysis id of the analysis
     * @param idForm     id of the form being analyzed
     * @return the number of clusters
     */
    public Integer getAnalysisNumClusters (Integer idAnalysis, Integer idForm) {
        return _ctrlDomain.getAnalysisNumClusters(idAnalysis, idForm);
    }

    /**
     * Returns the quality of an analysis following Silhouete's method
     * @param idAnalysis id of the analysis
     * @param idForm     id of the form being analyzed
     * @return quality of clustering
     */
    public Double getAnalysisQuality (Integer idAnalysis, Integer idForm) {
        return _ctrlDomain.getAnalysisQuality(idAnalysis, idForm);
    }

    /**
     * Returns the time an analysis was made
     * @param idAnalysis id of the analysis
     * @param idForm     id of the form being analyzed
     * @return the date and time when the analysis was made
     */
    public LocalDateTime getAnalysisDate (Integer idAnalysis, Integer idForm) {
        return _ctrlDomain.getAnalysisDate(idAnalysis, idForm);
    }

    /**
     * Returns the name of an analysis
     * @param idAnalysis id of the analysis
     * @param idForm     id of the form being analyzed
     * @return the analysis' name
     */
    public String getAnalysisName (Integer idAnalysis, Integer idForm) {
        return _ctrlDomain.getAnalysisName(idAnalysis, idForm);
    }

    /**
     * Returns the cluster assignations in an analysis
     * @param idAnalysis id of the analysis
     * @param idForm     id of the form being analyzed
     * @return the cluster assignations. That is, the i-th answer to the form is in cluster assignation[i]
     */
    public int[] getAnalysisAssignations (Integer idAnalysis, Integer idForm) {
        return _ctrlDomain.getAnalysisAssignations(idAnalysis, idForm);
    }

    // ==========================
    // General
    // =========================

    /**
     * Closes the application and ensures data persistence.
     */
    public void close() {
        try {
            _ctrlDomain.close();
        } catch (Exception e) {
            System.err.println("Error closing application: " + e.getMessage());
            e.printStackTrace();
        }
    }



}
