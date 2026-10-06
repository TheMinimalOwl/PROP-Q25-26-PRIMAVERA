package domaincontrollers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import domain.Answer;
import domain.AnswerFreeQuestion;
import domain.AnswerMultipleChoiceQuestion;
import domain.AnswerNumericalQuestion;
import domain.AnswerSingleChoiceQuestion;
import domain.AnswerToQuestion;
import domain.Form;
import domain.FreeQuestion;
import domain.KMeansStrategy;
import domain.MultipleChoiceQuestion;
import domain.NumericalQuestion;
import domain.OrderedSingleQuestion;
import domain.Question;
import utils.EntityNotFoundException;

/**
 * Controller for transforming form data between model and view layers.
 * Handles form creation, editing, answering, and data presentation.
 * Provides methods to convert domain objects to presentation-friendly maps and vice versa.
 *
 * @author Joel Forcadell (joel.forcadell@estudiantat.upc.edu)
 * @author Artur Leivar (artur.leivar@estudiantat.upc.edu)
 * @author Pau Turró Gómez (pau.turro.gomez@estudiantat.upc.edu)
 **/
public class CtrlTransformData {

    private static CtrlTransformData instance;

    /**
     * Returns the singleton instance of CtrlTransformData.
     *
     * @return The singleton instance of {@link CtrlTransformData}
     */
    protected static CtrlTransformData getInstance(CtrlDomain domain) {
        if (instance == null) {
            instance = new CtrlTransformData(domain);
        }
        return instance;
    }
    
    // ========= CONSTRUCTOR ==========
    
    // Instance variables
    private CtrlDomain domainCtrl; // Domain controller for data access

    /**
     * Constructs a new CtrlTransformData controller with the specified domain controller
     * and logged user identifier.
     * 
     */
    private CtrlTransformData(CtrlDomain domain) {
        this.domainCtrl = domain;
    }

    // ========= PRESENTATION METHODS USING MAPS ==========

    // ============
    // Form getters
    // ============
    
     /**
     * Retrieves basic information about a form in a presentation-friendly map format.
     * 
     * @param formId the form identifier
     * @return a Map containing: id, name, ownerId, questionCount, answerCount
     *         or an error entry if an exception occurs
     */
    protected Map<String, Object> getFormBasicInfo(Integer formId) {
        Map<String, Object> info = new LinkedHashMap<>();

        Form form = getForm(formId);
        info.put("id", form.getIdForm());
        info.put("name", form.getName());
        info.put("ownerId", form.getUserPropietary());
        info.put("questionCount", form.getQuestions().size());
        info.put("answerCount", form.getAnswers().size());

        return info;
    }
    
     /**
     * Retrieves complete form data for answering purposes in a presentation-friendly map format.
     * 
     * @param formId the form identifier
     * @return a Map containing: id, name, ownerId, questions list, and totalQuestions
     *         or an error entry if an exception occurs
     */
    protected Map<String, Object> getFormForAnswering(Integer formId) {
        Map<String, Object> formData = new LinkedHashMap<>();
        
        Form form = getForm(formId);
        
        // Basic form data
        formData.put("id", form.getIdForm());
        formData.put("name", form.getName());
        formData.put("ownerId", form.getUserPropietary());
        
        // Get all questions as maps
        List<Map<String, Object>> questions = new ArrayList<>();
        for (Integer questionId : form.getQuestions()) {
            Question question = getQuestion(formId, questionId);
            questions.add(getQuestionDataForAnswering(question));
        }
        formData.put("questions", questions);
        formData.put("totalQuestions", questions.size());
        
        return formData;
    }
    
     /**
     * Retrieves form data for editing purposes in a presentation-friendly map format.
     * 
     * @param formId the form identifier
     * @return a Map containing: id, name, ownerId, hasAnswers, canEdit, questions list, and questionCount
     *         or an error entry if an exception occurs
     */
    protected Map<String, Object> getFormForEditing(Integer formId) {
        Form form = getForm(formId);
        
        // Check if it has answers
        boolean hasAnswers = !form.getAnswers().isEmpty();
        
        Map<String, Object> formData = new LinkedHashMap<>();
        formData.put("id", form.getIdForm());
        formData.put("name", form.getName());
        formData.put("ownerId", form.getUserPropietary());
        formData.put("hasAnswers", hasAnswers);
        formData.put("canEdit", !hasAnswers); // Can only edit if no answers
        
        // Get existing questions (read-only)
        List<Map<String, Object>> questions = new ArrayList<>();
        for (Integer questionId : form.getQuestions()) {
            Question question = getQuestion(formId, questionId);
            questions.add(getQuestionDataForEditing(question, hasAnswers));
        }
        
        formData.put("questions", questions);
        formData.put("questionCount", questions.size());
        
        return formData;
    }

    // ==========================================
    // Answer-related for presentation operations
    // ==========================================

    /**
     * Transfomrs an answer to question into an Object containing the value of the answer
     * @param ansActual the transformed answer
     * @return an Object containing the value of the answer
     */
    protected Object transformATQ (AnswerToQuestion ansActual) {
        String questionType = ansActual.getClass().getName();
        if (questionType.equals("AnswerNumericalQuestion")) {
            AnswerNumericalQuestion ansAux = (AnswerNumericalQuestion) ansActual;
            return ansAux.getNumericalValue();

        } else if  (questionType.equals("AnswerSingleChoiceQuestion")) {
            AnswerSingleChoiceQuestion ansAux = (AnswerSingleChoiceQuestion) ansActual;
            return ansAux.getSelectedOptionIndex();

        } else if (questionType.equals("AnswerMultipleChoiceQuestion")) {
            AnswerMultipleChoiceQuestion ansAux = (AnswerMultipleChoiceQuestion) ansActual;
            return ansAux.getSelectedOptionIndexes();

        } else {
            AnswerFreeQuestion ansAux = (AnswerFreeQuestion) ansActual;
            return ansAux.getAnswerText();
        }
    }

    /**
     * Transforms an answer into an array of objects containing the value of its answer to question
     * @param idForm id of the form the answer belongs to
     * @param idUser id of the user who answered
     * @return an array of objects containing the answer to questions of the answer transformed using transformATQ
     */
    protected ArrayList<Object> getAnswer(Integer idForm, Integer idUser) {
        Answer ans = domainCtrl.getAnswer(idForm, idUser);
        ArrayList<Integer> ids = ans.getResponses();
        int nQuest = ids.size();
        ArrayList<Object> result = new ArrayList<> (nQuest);

        for (int j = 0; j < nQuest; j++) {
            AnswerToQuestion ansActual = getAnswerToQuestion(idUser, idForm, ids.get(j));
            result.add(transformATQ(ansActual));
        }
        return result;
    }


    /**
     * Returns an array representing the coordinates of a given answer in a (dimensionMax * nAnswers)-space with values between 0 and 1
     * @param answer        The answer which we want to calculate its coordinates
     * @param dimensionMax  The amount of dimensions we want for each answer to question to have
     * @return The total coordinates are calculated from the coordinates of every answer to question, concatenating
     *         their values. The coordinates for an answer to question are calculated as follows:
     *         - If the answer to question is numerical its coordinates is a single value. That value represents where
     *           the answer is between the max and min of the question
     *         - If the answer is a Single ordred question the calculation is simple but with nOptions
     *         - It the answer is of muliple choice then the coordinates are equal to the characteristic vector of
     *           the selecion. That is a vector of size nOptions where v[i] = 1 iff the option i is selected (0 if not)
     *         - If the answer is a free answer then the coordinates are four random numbers due to the complexity of
     *           this calculation. It could be done using TF-IDF on all the answers to that question, but it would have
     *           a very large dimensionality and would require a dimensionality reduction/scaling. And its implementation is too complex
     *
     *         Once we have calculated the coordinate for a single answer to question we extend all the coordinates up to
     *         dimensionMax so as to answers with higher dimensionality aren't more rellevant.
     *         The extension is done duplicating the values. ej. (0,0,1) to dimensionMax = 9 -> (0,0,0,0,0,0,1,1,1).
     *         If it is not multiple 0s are added as padding
     */
    private ArrayList<Double> getAnsCoords (ArrayList<AnswerToQuestion> answer, int dimensionMax) {
        ArrayList<Double> result = new ArrayList<>();
        // Primer calculem coordenades per a cada resposta a pregunta i després ho ajuntem en un sol vector
        ArrayList<ArrayList<Double>> resultAux = new ArrayList<ArrayList<Double>>();
        int nQuest = answer.size();
        Random random = new Random();
        // Calculem coordenades per a cada AnswerToQuestion
        for (int i = 0; i < nQuest; i++) {
            ArrayList<Double> coordsAux = new ArrayList<>();
            AnswerToQuestion ansAux = answer.get(i);
            Question question = getQuestion(ansAux.getIdForm(), ansAux.getIdQuestion());
            if (ansAux instanceof AnswerNumericalQuestion) {
                Double value = (Double)(((AnswerNumericalQuestion) ansAux).getNumericalValue().doubleValue());
                Double min = ((NumericalQuestion) question).getMinValue().doubleValue();
                Double max = ((NumericalQuestion) question).getMaxValue().doubleValue();
                coordsAux.add((value - min) / (max - min));
            } else if (ansAux instanceof AnswerSingleChoiceQuestion) {
                Integer nOptions = ((OrderedSingleQuestion) question).getOptionCount();
                Double nOptsD = nOptions.doubleValue();
                Double optionSelected = ((AnswerSingleChoiceQuestion) ansAux).getSelectedOptionIndex().doubleValue();
                coordsAux.add(optionSelected/nOptsD);
            } else if (ansAux instanceof AnswerMultipleChoiceQuestion) {
                Integer nOptions = ((MultipleChoiceQuestion) question).getOptionCount();
                ArrayList<Integer> selectedOptions = ((AnswerMultipleChoiceQuestion) ansAux).getSelectedOptionIndexes();
                for (int j = 0; j < nOptions; j++) {
                    if (selectedOptions.contains(j)) {
                        coordsAux.add(1.0);
                    } else {
                        coordsAux.add(0.0);
                    }
                }
            } else if (ansAux instanceof AnswerFreeQuestion) {
                for (int j = 0; j < 4; j++) {
                    coordsAux.add(random.nextDouble());
                }
            } else {
                throw new IllegalArgumentException("AnswerToQuestion type unknown");
            }

            resultAux.add(coordsAux);
        }

        // Estenem coordenades per a que totes tinguin la mateixa dimensionalitat.
        // Per a que les que originalment en tenien més no tinguin més pes estenem les coordenades de les que tenien menys
        for (int i = 0; i < nQuest; i++) {
            int dimensioAux = resultAux.get(i).size();
            // quants cops hem d'estendre cada coordenada
            // es -1 perquè ja tenim cada coordenada un cop i la divisió ens dona quants cops ha d'aparèixer cada coordenada
            int quantAEstendre = dimensionMax / dimensioAux - 1;
            for (int j = 0; j < dimensioAux; j++) {
                for (int k = 0; k < quantAEstendre; k++) {
                    Double duplicat = resultAux.get(i).get(j * (quantAEstendre + 1));
                    resultAux.get(i).add(j * (quantAEstendre + 1), duplicat);
                }
            }
            // En cas que no siguin divisibles omplim la resta amb zeros
            int padding = dimensionMax % dimensioAux;
            for (int j = 0; j < padding; j++) {
                resultAux.get(i).add(0.0);
            }
        }

        // Passem el resultat a un sol vector llegint la matriu per files
        for (int i = 0; i < nQuest; i++) {
            result.addAll(resultAux.get(i));
        }
        return result;
    }

    /**
     * Calculates the distance matrix for the given answers
     * @param answerToQuestions the answers represented as an arrays of answers to questions
     * @return for result[i][j] the distance between the i-th answer and j-th answer as stated in the analysisStrategy
     */
    public  double[][] getAnswersDistanceMatrix(ArrayList<ArrayList<AnswerToQuestion>> answerToQuestions) {
        int nAns = answerToQuestions.size();
        double[][] result = new double[nAns][nAns];
        for (int i = 0; i < nAns; i++) {
            result[i][i] = 0.0;
        }
        KMeansStrategy strategy = KMeansStrategy.getInstance();
        if (nAns > 0)
            strategy.setForm(answerToQuestions.get(0).get(0).getIdForm());
        for (int i = 0; i < nAns; i++) {
            for (int j = i + 1; j < nAns; j++) {
                double distance = strategy.distanceEuclidian(answerToQuestions.get(i), answerToQuestions.get(j));
                result[i][j] = distance;
                result[j][i] = distance;
            }
        }
        return result;
    }

    /**
     * Calculates he coordinates of all the answers given. The calculation for each answer is the same as stated in getAnsCoord
     * @param answerToQuestions  array of answers represented each as an array of its answer to question
     * @return An array containing all the answer coordinates
     */
    public ArrayList<ArrayList<Double>> getAnswersCoordinates(ArrayList<ArrayList<AnswerToQuestion>> answerToQuestions) {
        // Calculem quina és l'AnswertoQuestion amb més dimensionalitat per a assegurar-nos de que totes les
        // respostes tinguin la mateixa dimensionalitat
        int dimensionMax = 0;
        int nAns = answerToQuestions.size();
        int nQuest = 0;
        if (nAns > 0) nQuest = answerToQuestions.get(0).size();
        for (int i = 0; i < nAns; i++) {
            for (int j = 0; j < nQuest; j++) {
                int dimension = 0;
                AnswerToQuestion ansAux = answerToQuestions.get(i).get(j);
                Question question = getQuestion(ansAux.getIdForm(), ansAux.getIdQuestion());
                if (ansAux instanceof AnswerNumericalQuestion) {
                    dimension = 1;
                } else if (ansAux instanceof AnswerSingleChoiceQuestion) {
                    Integer nOptions = ((OrderedSingleQuestion) question).getOptionCount();
                    dimension = Integer.valueOf(nOptions);
                } else if (ansAux instanceof AnswerMultipleChoiceQuestion) {
                    Integer nOptions = ((MultipleChoiceQuestion) question).getOptionCount();
                    dimension = Integer.valueOf(nOptions);
                } else if (ansAux instanceof AnswerFreeQuestion) {
                    // TODO: implement actual calculation
                    dimension = 4;
                } else {
                    throw new IllegalArgumentException("AnswerToQuestion type unknown");
                }
                if (dimensionMax < dimension) dimensionMax = dimension;
            }
        }
        ArrayList<ArrayList<Double>> result = new ArrayList<ArrayList<Double>>();
        for (int i = 0; i < nAns; i++) {
            result.add(getAnsCoords(answerToQuestions.get(i), dimensionMax));
        }
        return result;
    }


    /**
     * Transforms given answers into objects
     * @param answers answers we wish to transform
     * @return Said answers tranformed into Objects with their value
     */
    public ArrayList<ArrayList<Object>> transformAnswers(ArrayList<ArrayList<AnswerToQuestion>> answers) {
        int nAns = answers.size();
        int nQuest = 0;
        if (nAns >= 1) {
            nQuest = answers.get(0).size();
        }
        ArrayList<ArrayList<Object>> result = new ArrayList<>(nAns);
        for (int i = 0; i < nAns; i++) {
            result.add(new ArrayList<Object> ());
            for (int j = 0; j < nQuest; j++) {
                result.get(i).add(transformATQ(answers.get(i).get(j)));
            }
        }
        return result;
    }
    
    /**
     * Saves user answers to a form using data from a map.
     * 
     * @param userId the user identifier
     * @param formId the form identifier
     * @param answersData a Map containing questionId -> answer pairs
     * @throws RuntimeException if an error occurs while saving answers
     */
    protected void saveAnswers(Integer userId, Integer formId, Map<Integer, Object> answersData) {
        for (Map.Entry<Integer, Object> entry : answersData.entrySet()) {
            Integer questionId = entry.getKey();
            Object answerValue = entry.getValue();
            Question question = getQuestion(formId, questionId);
            
            // Save based on question type
            if (question instanceof FreeQuestion) {
                domainCtrl.addFreeAnswer(userId, formId, questionId, (String) answerValue);
            } else if (question instanceof NumericalQuestion) {
                domainCtrl.addNumericalAnswer(userId, formId, questionId, (Integer) answerValue);
            } else if (question instanceof MultipleChoiceQuestion) {
                if (answerValue instanceof List<?>) {
                    @SuppressWarnings("unchecked")
                    List<Integer> list = (List<Integer>) answerValue;

                    // Convert to ArrayList<Integer> because the domain method requires it
                    ArrayList<Integer> arrayList = new ArrayList<>(list);
                    domainCtrl.addMultipleChoiceAnswer(userId, formId, questionId, arrayList);
                } else {
                    throw new IllegalArgumentException("Expected List<Integer> for MultipleChoiceQuestion");
                }
            } else if (question instanceof OrderedSingleQuestion) {
                domainCtrl.addOrderedSingleAnswer(userId, formId, questionId, (Integer) answerValue);
            }
        }
    }

    // ========================================================================
    // Data access helper methods
    // ========================================================================

    // ======================================
    // Question getters (just for THIS class)
    // ======================================

    /**
     * Converts a Question object to a map representation for answering purposes.
     * 
     * @param question the Question object to convert
     * @return a Map containing question data in presentation-friendly format
     */
    private Map<String, Object> getQuestionDataForAnswering(Question question) {
        Map<String, Object> qData = new LinkedHashMap<>();
        
        // Common data for all questions
        qData.put("id", question.getIdQuestion());
        qData.put("statement", question.getStatement());
        qData.put("optional", question.isOptional());
        qData.put("type", getQuestionTypeString(question));
        
        // Type-specific data
        if (question instanceof FreeQuestion) {
            FreeQuestion fq = (FreeQuestion) question;
            qData.put("maxLength", fq.getMaxLength());
            
        } else if (question instanceof NumericalQuestion) {
            NumericalQuestion nq = (NumericalQuestion) question;
            qData.put("minValue", nq.getMinValue());
            qData.put("maxValue", nq.getMaxValue());
            
        } else if (question instanceof MultipleChoiceQuestion) {
            MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) question;
            qData.put("options", mcq.getOptions());
            qData.put("minSelections", mcq.getMinSelections());
            qData.put("maxSelections", mcq.getMaxSelections());
            
        } else if (question instanceof OrderedSingleQuestion) {
            OrderedSingleQuestion osq = (OrderedSingleQuestion) question;
            qData.put("options", osq.getOptions());
        }
        
        return qData;
    }

    
    /**
     * Converts a Question object to a map representation for editing purposes.
     * 
     * @param question the Question object to convert
     * @param hasAnswers whether the form has any answers
     * @return a Map containing question data in presentation-friendly format for editing
     */
    private Map<String, Object> getQuestionDataForEditing(Question question, boolean hasAnswers) {
        Map<String, Object> qData = new LinkedHashMap<>();
        
        // Basic data
        qData.put("id", question.getIdQuestion());
        qData.put("statement", question.getStatement());
        qData.put("optional", question.isOptional());
        qData.put("type", getQuestionTypeString(question));
        qData.put("readOnly", true); // Always read-only in editing
        qData.put("canDelete", !hasAnswers); // Can only delete if no answers
        
        // Type-specific data
        if (question instanceof FreeQuestion) {
            FreeQuestion fq = (FreeQuestion) question;
            qData.put("maxLength", fq.getMaxLength());
            
        } else if (question instanceof NumericalQuestion) {
            NumericalQuestion nq = (NumericalQuestion) question;
            qData.put("minValue", nq.getMinValue());
            qData.put("maxValue", nq.getMaxValue());
            
        } else if (question instanceof MultipleChoiceQuestion) {
            MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) question;
            qData.put("options", mcq.getOptions());
            qData.put("minSelections", mcq.getMinSelections());
            qData.put("maxSelections", mcq.getMaxSelections());
            
        } else if (question instanceof OrderedSingleQuestion) {
            OrderedSingleQuestion osq = (OrderedSingleQuestion) question;
            qData.put("options", osq.getOptions());
        }
        
        return qData;
    }

    /**
     * Retrieves a form by its identifier.
     * 
     * @param formId the form identifier
     * @return the Form object
     * @throws EntityNotFoundException if the form does not exist
     */
    private Form getForm(Integer formId) throws EntityNotFoundException {
        return domainCtrl.getForm(formId);
    }

    /**
     * Retrieves a question by form and question identifiers.
     * 
     * @param formId the form identifier
     * @param questionId the question identifier
     * @return the Question object
     * @throws EntityNotFoundException if the question does not exist
     */
    private Question getQuestion(Integer formId, Integer questionId) throws EntityNotFoundException {
        return domainCtrl.getQuestion(formId, questionId);
    }
    
    /**
     * @param idUser
     * @param idForm
     * @param idQuestion
     * @return
     */
    private AnswerToQuestion getAnswerToQuestion(Integer idUser, Integer idForm, Integer idQuestion) {
        return domainCtrl.getAnswerToQuestion(idUser, idForm, idQuestion);
    }
 
    /**
     * Returns a string representation of the question type.
     * 
     * @param question the Question object
     * @return a string representing the question type (FREE, NUMERICAL, MULTIPLE_CHOICE, ORDERED_SINGLE, or UNKNOWN)
     */
    private String getQuestionTypeString(Question question) {
        if (question instanceof FreeQuestion)
            return "FREE";
        if (question instanceof NumericalQuestion)
            return "NUMERICAL";
        if (question instanceof MultipleChoiceQuestion)
            return "MULTIPLE_CHOICE";
        if (question instanceof OrderedSingleQuestion)
            return "ORDERED_SINGLE";
        return "UNKNOWN";
    }
}
