package domain;

import java.util.ArrayList;
import java.util.Random;

import domaincontrollers.CtrlDomain;

/**
 * @author Pau Turró Gómez (pau.turro.gomez@estudiantat.upc.edu)
 *
 * Contains all the methods to implement the k-means algorithm with k-means++ initialization.
 */
public class KMeansStrategy extends AnalyzeStrategy {

    private int maxIterations;
    
    private KMeansStrategy() {
        this.maxIterations = 100; // Max iterations by default
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    private static class KMeansStrategySingleton {
        private static final KMeansStrategy instance = new KMeansStrategy();  
    }

    public static KMeansStrategy getInstance() {
        return KMeansStrategySingleton.instance;
    }

    public void setForm (Integer idForm) {
        this.idForm = idForm;
    }



// ===================== K-means++ =======================================================
    
    /**
     * Given a probability distribution, it returns an index from said distribution.
     * Each index i has a distribution[i] probability of being returned
     * @param distribution a probability distribution. It is asumed to be correct
     * @return an index from the distribution
     * @throws IllegalArgumentException if the distribution doesn't add up to 1 and randNum is greater than that sum
     */
    private int randNum (ArrayList<Double> distribution) {
        double randNum = Math.random();
        double cumulativeProbability = 0.0;
        boolean found = false;
        int index;
        for (index = 0; index < distribution.size() && (!found); index++) {
            cumulativeProbability += distribution.get(index);
            if (randNum <= cumulativeProbability) {
                found = true;
            }
        }
        if (found) return index - 1;
        
        throw new IllegalArgumentException("Incorrect distribution");
    }

    /**
     * Given an answer it calculates its distance to the nearest centroid (which all are existing answers)
     *
     * @param answers all the answers to the form
     * @param indexAnswer the index of the answer we want to know the distance from
     * @param indexCentroids the indexes of all the centroids we want to know the distance to
     * @return the distance from the answer in answers[indexAnswer] to its nearest centroid
     */
    private double distanceToCentroids (ArrayList<ArrayList<AnswerToQuestion>> answers,
                                        int indexAnswer, ArrayList<Integer> indexCentroids) {
        double distance = Double.MAX_VALUE;
        int nCentroids = indexCentroids.size();
        for (int j = 0; j < nCentroids; j++) {
            double auxDistance = super.distanceManhattan(answers.get(indexAnswer), answers.get(j));
            if (auxDistance < distance) distance = auxDistance;
        }
        return distance;
    }

    /**
     * We calculate the sum of the distance to their nearest centroid from all the answers.
     * All centroids are existing answers.
     *
     * @param answers all the answers
     * @param indexCentroids the indexes of all the centroids we want to know the distance to
     * @return the sum of all distances
     */
    private double calculateAllDistances (ArrayList<ArrayList<AnswerToQuestion>> answers, ArrayList<Integer> indexCentroids) {
        double totalDistance = 0.0;
        int nAnswers = answers.size();
        for (int i = 0; i < nAnswers; i++) {
            double localDistance = distanceToCentroids(answers, i, indexCentroids);
            totalDistance += localDistance * localDistance;
        }
        return totalDistance;
    }

    /**
     * Implements the K-means++ initial centroid selection
     *
     * @param answers all the answers we want to chose a centroid from
     * @param nClusters the number of clusters (and therefore centroids) we will create with k-means
     * @return a list of indices representing each an answer
     */
    private ArrayList<Integer> kPlusPlus (ArrayList<ArrayList<AnswerToQuestion>> answers, int nClusters) {
        int nAnswers = answers.size();
        ArrayList<Integer> centroids = new ArrayList<Integer>();
        Random rand = new Random();
        int initialCentroid = rand.nextInt(nAnswers);
        centroids.add(initialCentroid);
        ArrayList<Double> probabilities = new ArrayList<Double>(nAnswers);
        for (int i = 0; i < nAnswers; i++) {
            probabilities.add(0.0);
        }
        for (int i = 1; i < nClusters; i++) {
            double totalDist = calculateAllDistances(answers, centroids);
            for (int j = 0; j < nAnswers; j++) {
                double localDist = distanceToCentroids(answers, j, centroids);
                probabilities.set(j, localDist / totalDist);
            }
            int newCentroid = randNum(probabilities);
            centroids.add(newCentroid);
        }
        return centroids;
    }

    
// =======================================================================================


    /**
     * Given an array of AnswerNumericalQuestions, it returns an AnswerNumericalQuestions with the average value
     * of those inside the array.
     *
     * @param answers the array of answers.
     * @return an AnswerToQuestion with the average of the values in the array as value
     */
    private AnswerToQuestion getAverage(ArrayList<AnswerToQuestion> answers) {
        int nAnswers = answers.size();
        int sum = 0;
        for (int i = 0; i < nAnswers; i++) {
            AnswerNumericalQuestion ans = (AnswerNumericalQuestion) answers.get(i);
            sum += ans.getNumericalValue();
        }
        // We create a new AnswerToQuestion which doesn't belong to any form which contains the average calculated
        Integer average = sum / nAnswers;
        AnswerNumericalQuestion result = new AnswerNumericalQuestion (-1, -1, -1, average);
        return result;
    }

    /**
     * Given an array of SingleAnswers it returns an AnswerSingleChoiceQuestion which's selected option is the mode
     * of the options selected in the array
     * @param answers the array of answers
     * @return an AnswerToQuestion with the mode of the values in the array as value
     */
    private AnswerToQuestion getMode(ArrayList<AnswerToQuestion> answers) {
        Integer idQuestion = answers.get(0).getIdQuestion();

        CtrlDomain ctrl = CtrlDomain.getInstance();
        Question question = ctrl.getQuestion(idForm, idQuestion);

        int nModes;
        if (question instanceof OrderedSingleQuestion) {
            OrderedSingleQuestion ordQuestion = (OrderedSingleQuestion) question;
            nModes = ordQuestion.getOptionCount();
        } else {
            throw new IllegalArgumentException("Question " + idQuestion + " is not a OrderedSingleQuestion");
        }
        
        ArrayList<Integer> repetitions = new ArrayList<Integer> (nModes);
        for (int i = 0; i < nModes; i++) {
            repetitions.add(0);
        }
        // We save all the times a mode has been answered
        int nAnswers = answers.size();
        for (int i = 0; i < nAnswers; i++) {
            AnswerSingleChoiceQuestion ans = (AnswerSingleChoiceQuestion) answers.get(i);
            int modeAux = ans.getSelectedOptionIndex();
            int nReps = repetitions.get(modeAux);
            repetitions.set(modeAux, nReps + 1);
        }
        // We see what mode has the most repetitions (in case of a draw we keep the first one)
        int mostRepeated = 0;
        int nRepetitions = repetitions.get(mostRepeated);
        for (int i = 1; i < nModes; i++) {
            if (repetitions.get(i) > nRepetitions) {
                mostRepeated = i;
                nRepetitions = repetitions.get(i);
            }
        }
        AnswerSingleChoiceQuestion result = new AnswerSingleChoiceQuestion(-1, -1, -1, mostRepeated);
        return result;
    }

    /**
     * Returns the AnswerToQuestion which is has the lower average distance to the other answers (center)
     * @param answers the answers we want to find the center of. They are MultipleChoice
     * @return the center
     */
    private AnswerToQuestion getCenterMultiple(ArrayList<AnswerToQuestion> answers) {
        int nAnswers = answers.size();
        double nAnsDouble = (double)nAnswers;
        ArrayList<Double> averageDistance = new ArrayList<Double> (nAnswers);
        for (int i = 0; i < nAnswers; i++) {
            Double distance = 0.0;
            for (int j = 0; j < nAnswers; j++) {
                distance += calculateDistanceLocal((AnswerMultipleChoiceQuestion) answers.get(i), (AnswerMultipleChoiceQuestion)answers.get(j));
            }
            averageDistance.add(distance / (Double)nAnsDouble);
        }
        
        AnswerToQuestion result = answers.get(0);
        Double minDistance = averageDistance.get(0);
        for (int i = 1; i < nAnswers; i++) {
            if (averageDistance.get(i) < minDistance) {
                minDistance = averageDistance.get(i);
                result = answers.get(i);
            }
        }
        return result;
    }

    /**
     * Returns the AnswerToQuestion which is has the lower average distance to the other answers (center)
     * @param answers the answers we want to find the center of
     * @return the center
     */
    private AnswerToQuestion getCentreFree(ArrayList<AnswerToQuestion> answers) {
        int nAnswers = answers.size();
        double nAnsDouble = (double)nAnswers;
        ArrayList<Double> averageDistance = new ArrayList<Double> (nAnswers);
        for (int i = 0; i < nAnswers; i++) {
            Double distance = 0.0;
            for (int j = 0; j < nAnswers; j++) {
                distance += calculateDistanceLocal((AnswerFreeQuestion) answers.get(i), (AnswerFreeQuestion)answers.get(j));
            }
            averageDistance.add(distance / (Double)nAnsDouble);
        }
        
        AnswerToQuestion result = answers.get(0);
        Double minDistance = averageDistance.get(0);
        for (int i = 1; i < nAnswers; i++) {
            if (averageDistance.get(i) < minDistance) {
                minDistance = averageDistance.get(i);
                result = answers.get(i);
            }
        }
        return result;
    }


    /**
     * It calculates the new centroid of a cluster
     * @param answers the answers inside the cluster
     * @return an array containing AnswersToQuestions which contain the values of the centroid of the cluster
     */
    private ArrayList<AnswerToQuestion> calculateCentroid (ArrayList<ArrayList<AnswerToQuestion>> answers) {
        int nAnswers = answers.size();
        int nQuestions = 0;
        if (nAnswers >= 1)
            nQuestions = answers.get(0).size();
        ArrayList<AnswerToQuestion> centroid = new ArrayList<AnswerToQuestion> (nQuestions);
        for (int i = 0; i < nQuestions; i++) {
            // We calculate the "average" for the question in column i
            ArrayList<AnswerToQuestion> answersToQuestion = new ArrayList<AnswerToQuestion> (nAnswers);
            for (int j = 0; j < nAnswers; j++) {
                // we add the answer to question i in the answer j
                answersToQuestion.add(answers.get(j).get(i));
            }
            AnswerToQuestion value;
            String questionType = answersToQuestion.get(0).getClass().getName();
            if (questionType.equals("AnswerNumericalQuestion")) {
                value = getAverage(answersToQuestion);
            } else if  (questionType.equals("AnswerSingleChoiceQuestion")) {
                value = getMode(answersToQuestion);
            } else if (questionType.equals("AnswerMultipleChoiceQuestion")) {
                value = getCenterMultiple(answersToQuestion);
            } else {
                value = getCentreFree(answersToQuestion);
            }
            centroid.add(i, value);
        }
        return centroid;
    }
    
    /**
     * @param clusters represents the clusters. clusters[i] is the cluster i, clusters[i][j] is the answer j of cluster i.
     * @return an array of answers that contain the values of the centroid. It doesn't have to exist in answers
     */
    private ArrayList<ArrayList<AnswerToQuestion>> newCentroids (ArrayList<ArrayList<ArrayList<AnswerToQuestion>>> clusters) {
        int nClusters = clusters.size();
        ArrayList<ArrayList<AnswerToQuestion>> centroids = new ArrayList<ArrayList<AnswerToQuestion>> ();

        for (int i = 0; i < nClusters; i++) {
            ArrayList<AnswerToQuestion> localCentroid = calculateCentroid(clusters.get(i));
            centroids.add(localCentroid);
        }
        return centroids;
    }

    /**
     * It generates clusters around the given centroids with the answers given
     * @param answers answers which will form the clusters
     * @param centroids points around which we will create the clusters
     * @return a clustering of the answers around the centroids. Each point is assigned to its nearer centroid.
     * result.get(i) represents the i-th cluster generated (its answers)
     */
    private ArrayList<ArrayList<ArrayList<AnswerToQuestion>>> generateClusters (ArrayList<ArrayList<AnswerToQuestion>> answers,
                                                                                ArrayList<ArrayList<AnswerToQuestion>> centroids) {
        int nAnswers = answers.size();
        int nClusters = centroids.size();
        ArrayList<ArrayList<ArrayList<AnswerToQuestion>>> clusters = new ArrayList<ArrayList<ArrayList<AnswerToQuestion>>> (nClusters);
        for (int i = 0; i < nClusters; i++) {
            ArrayList<ArrayList<AnswerToQuestion>> aux = new ArrayList<ArrayList<AnswerToQuestion>> ();
            clusters.add(aux);
        }
        // For every answer we search to which centroid it is nearer and we assign it to the cluster represented by said centroid
        for (int i = 0; i < nAnswers; i++) {
            double minDistance = super.distanceManhattan(answers.get(i), centroids.get(0));
            int assignedCentroid = 0;
            for (int j = 1; j < nClusters; j++) {
                double auxDistance = super.distanceManhattan(answers.get(i), centroids.get(j));
                if (auxDistance < minDistance) {
                    minDistance = auxDistance;
                    assignedCentroid = j;
                }
            }
            ArrayList<ArrayList<AnswerToQuestion>> answersCentroid = clusters.get(assignedCentroid);
            answersCentroid.add(answers.get(i));
            clusters.set(assignedCentroid, answersCentroid);
        }
        return clusters;
    }

    /**
     * We generate a clustering with nClusters of the form with id idForm
     * @param answers the matrice of answers to analyze
     * @param nClusters number of clusters to create
     * @param form the form identifier to access question metadata
     * @return for each answer, to which cluster it is assigned to (starting with 0); for each cluster, its centroid;
     * the Silhouete's coefficient for the clustering.
     */
    @Override
    public AnalysisResult doAnalysis(ArrayList<ArrayList<AnswerToQuestion>> answers, int nClusters, Integer form) {
        idForm = form;
        int nAnswers = answers.size();
        int nQuestions = answers.get(0).size();
        ArrayList<Integer> indexInitialCentroids = kPlusPlus(answers, nClusters);
        ArrayList<ArrayList<AnswerToQuestion>> centroids = new ArrayList<ArrayList<AnswerToQuestion>> (nClusters);
        for (int i = 0; i < nClusters; i++) {
            int indexCentroide = indexInitialCentroids.get(i);
            ArrayList<AnswerToQuestion> answer = answers.get(indexCentroide);
            centroids.add(answer);
        }
        ArrayList<ArrayList<ArrayList<AnswerToQuestion>>> clustering = new ArrayList<ArrayList<ArrayList<AnswerToQuestion>>> ();
        for (int i = 0; i < maxIterations; i++) {
            clustering = generateClusters(answers, centroids);
            centroids = newCentroids (clustering);
        }
        int[] userAssignations = new int[nAnswers];
        for (int i = 0; i < nAnswers; i++) {
            boolean found = false;
            for (int j = 0; j < nClusters && !found; j++) {
                if (clustering.get(j).contains(answers.get(i))) {
                    userAssignations[i] = j;
                    found = true;
                }
            }
        }
        double quality = calculateQuality(answers, nClusters, userAssignations);
        AnalysisResult result = new AnalysisResult(userAssignations, centroids, quality);
        return result;
    }
}
