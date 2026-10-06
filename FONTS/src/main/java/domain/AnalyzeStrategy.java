package domain;

import java.util.ArrayList;

import domaincontrollers.CtrlDomain;

public abstract class AnalyzeStrategy {
    protected Integer idForm;
    
    /**
     * Executes the analysis algorithm on a collection of user answers.
     * Each concrete strategy implements its own clustering logic.
     * 
     * @param answers the matrice of answers to analyze
     * @param nClusters number of clusters to create
     * @param idForm the form identifier to access question metadata
     * @return AnalysisResult containing cluster assignments and centroids/medoids
     * @throws IllegalArgumentException if answers is null or empty
     */
    public abstract AnalysisResult doAnalysis(ArrayList<ArrayList<AnswerToQuestion>> answers, int nClusters, Integer idForm);
    
    /**
     * Calculates the local distance between two answers.
     * Overloaded methods for each specific answer type.
     * 
     * @param answer1 first answer
     * @param answer2 second answer
     * @return local distance between the two answers
     */

    // Overloaded method for Multiple Choice Questions (unordered qualitative)
    protected double calculateDistanceLocal(AnswerMultipleChoiceQuestion answer1, AnswerMultipleChoiceQuestion answer2) {
        ArrayList<Integer> answerA = answer1.getSelectedOptionIndexes();
        ArrayList<Integer> answerB = answer2.getSelectedOptionIndexes();
        ArrayList<Integer> intersection = new ArrayList<>();
        ArrayList<Integer> union = new ArrayList<>();
        int midaA = answerA.size();
        int midaB = answerB.size();

        // Calculating intersection
        for (int i = 0; i < midaA; i++) {
            for (int j = 0; j < midaB; j++) {
                if (answerA.get(i).equals(answerB.get(j))) {
                    intersection.add(answerA.get(i));
                }
            }
        }

        // Calculating union
        union.addAll(answerA);
        union.addAll(answerB);
        union.removeAll(intersection);

        double sizeIntersection = (double) intersection.size();
        double sizeUnion = (double) union.size();

        return 1 - (sizeIntersection / sizeUnion);
    }

    // Overloaded method for Numerical Questions
    protected double calculateDistanceLocal(AnswerNumericalQuestion answer1, AnswerNumericalQuestion answer2) {
        Integer idQuestion = answer1.getIdQuestion();
        Integer idQuestion2 = answer2.getIdQuestion();

        if (!idQuestion.equals(idQuestion2)) {
            throw new IllegalArgumentException("The two questions are not the same type of question");
        }

        CtrlDomain ctrl = CtrlDomain.getInstance();
        Question question = ctrl.getQuestion(idForm, idQuestion);

        int max, min;
        if (question instanceof NumericalQuestion) {
            NumericalQuestion numQuestion = (NumericalQuestion) question;
            max = numQuestion.getMaxValue();
            min = numQuestion.getMinValue();
        } else {
            throw new IllegalArgumentException("Question " + idQuestion + " is not a NumericalQuestion");
        }

        int answerA = answer1.getNumericalValue();
        int answerB = answer2.getNumericalValue();
        double result = Math.abs(answerA - answerB);
        result /= (max-min);
        return result;
    }

    // Overloaded method for Single Choice Questions (ordered qualitative)
    protected double calculateDistanceLocal(AnswerSingleChoiceQuestion answer1, AnswerSingleChoiceQuestion answer2) {
        Integer idQuestion = answer1.getIdQuestion();
        Integer idQuestion2 = answer2.getIdQuestion();

        if (!idQuestion.equals(idQuestion2)) {
            throw new IllegalArgumentException("The two questions are not the same type of question");
        }

        CtrlDomain ctrl = CtrlDomain.getInstance();
        Question question = ctrl.getQuestion(idForm, idQuestion);

        int nModes;
        if (question instanceof OrderedSingleQuestion) {
            OrderedSingleQuestion ordQuestion = (OrderedSingleQuestion) question;
            nModes = ordQuestion.getOptionCount();
        } else {
            throw new IllegalArgumentException("Question " + idQuestion + " is not a OrderedSingleQuestion");
        }
        
        int answerA = answer1.getSelectedOptionIndex();
        int answerB = answer2.getSelectedOptionIndex();
        double result = Math.abs(answerA - answerB);
        result /= nModes;
        return result;
    }

    
    /**
     * Calculate the distance between 2 free answers using Levenshtein distance.
     *
     * @param answer1 first answer
     * @param answer2 second answer
     * @return local distance between the two answers (normalized)
     */
    // Overloaded method for Free Text Questions
    protected double calculateDistanceLocal(AnswerFreeQuestion answer1, AnswerFreeQuestion answer2) {
        String answerA = answer1.getAnswerText();
        String answerB = answer2.getAnswerText();
        int sizeA = answerA.length();
        int sizeB = answerB.length();
        // distances[i][j] = Levenshtein distance between the substrings answerA[0..i] and answerB[0..j]
        // This distance is the minimum of:
        //     Inserting answerB[j] at the end of answerA[0..i]
        //         This is distances[i][j - 1] + 1
        //     Deleting answerA[i]
        //         This is distances[i - 1][j] + 1
        //     Changing answerA[i] into answerB[j] (if not equal already)
        //         This is distances[i - 1][j -1] + 1 (if they are different. If equal we don't add the 1)
        int[][] distances = new int[sizeA + 1][sizeB + 1];
        
        for (int i = 0; i <= sizeA; i++) {
            distances[i][0] = i;
        }
        for (int j = 0; j <= sizeB; j++) {
            distances[0][j] = j;
        }

        int different = 0;
        for (int i = 1; i <= sizeA; i++) {
            for (int j = 1; j <= sizeB; j++) {
                different = 0;
                if (answerA.charAt(i - 1) != answerB.charAt(j - 1)) {
                    different = 1;
                }
                distances[i][j] = distances[i][j - 1] + 1;
                if (distances[i - 1][j - 1] + different < distances[i][j]) {
                    distances[i][j] = distances[i - 1][j - 1] + different;
                }
                if (distances[i - 1][j] + 1 < distances[i][j]) {
                    distances[i][j] = distances[i - 1][j] + 1;
                }
            }
        }

        // Normalize the Levenshtein distance
        double levDistance = (double)distances[sizeA][sizeB];
        double difLengths = (double)(sizeA - sizeB);
        if (difLengths < 0) difLengths = -difLengths;
        double maxLength = (double)sizeA;
        if (sizeB > sizeA) maxLength = (double)sizeB;

        return (levDistance - difLengths) / (maxLength - difLengths);
    }

    /**
     * Calculates Manhattan (L1) distance between two form answers.
     * This is the global distance metric for the clustering.
     * 
     * @param answer1 first answer
     * @param answer2 second answer
     * @return Manhattan distance between the two answers
     */
    public double distanceManhattan (ArrayList<AnswerToQuestion> answer1, ArrayList<AnswerToQuestion> answer2) {
        double distance = 0.0;
        AnswerToQuestion actualQuestion1, actualQuestion2;
        for (int i = 0; i < answer1.size(); i++) {
            actualQuestion1 = answer1.get(i);
            actualQuestion2 = answer2.get(i);
            
            if (actualQuestion1 instanceof AnswerNumericalQuestion && actualQuestion2 instanceof AnswerNumericalQuestion) {
                distance += calculateDistanceLocal((AnswerNumericalQuestion)actualQuestion1, (AnswerNumericalQuestion)actualQuestion2);
            } else if (actualQuestion1 instanceof AnswerSingleChoiceQuestion && actualQuestion2 instanceof AnswerSingleChoiceQuestion) {
                distance += calculateDistanceLocal((AnswerSingleChoiceQuestion)actualQuestion1, (AnswerSingleChoiceQuestion)actualQuestion2);
            } else if (actualQuestion1 instanceof AnswerMultipleChoiceQuestion && actualQuestion2 instanceof AnswerMultipleChoiceQuestion) {
                distance += calculateDistanceLocal((AnswerMultipleChoiceQuestion)actualQuestion1, (AnswerMultipleChoiceQuestion)actualQuestion2);
            } else if (actualQuestion1 instanceof AnswerFreeQuestion && actualQuestion2 instanceof AnswerFreeQuestion) {
                distance += calculateDistanceLocal((AnswerFreeQuestion)actualQuestion1, (AnswerFreeQuestion)actualQuestion2);
            } else {
                throw new IllegalArgumentException("Incompatible answer types or unsupported answer type");
            }
        }
        distance /= answer1.size();

        return distance;
    }

    /**
     * Calculates Euclidian (L2) distance between two form answers.
     * This is the global distance metric for the clustering.
     * 
     * @param answ1 first answer
     * @param answ2 second answer
     * @return Euclidian distance between the two answers
     */
    public double distanceEuclidian (ArrayList<AnswerToQuestion> answ1, ArrayList<AnswerToQuestion> answ2) {
        double distance = 0.0, distanceAux = 0.0;
        AnswerToQuestion actualQuestion1, actualQuestion2;
        for (int i = 0; i < answ1.size(); i++) {
            actualQuestion1 = answ1.get(i);
            actualQuestion2 = answ2.get(i);
            distanceAux = 0.0;
            
            if (actualQuestion1 instanceof AnswerNumericalQuestion && actualQuestion2 instanceof AnswerNumericalQuestion) {
                distanceAux = calculateDistanceLocal((AnswerNumericalQuestion)actualQuestion1, (AnswerNumericalQuestion)actualQuestion2);
            } else if (actualQuestion1 instanceof AnswerSingleChoiceQuestion && actualQuestion2 instanceof AnswerSingleChoiceQuestion) {
                distanceAux = calculateDistanceLocal((AnswerSingleChoiceQuestion)actualQuestion1, (AnswerSingleChoiceQuestion)actualQuestion2);
            } else if (actualQuestion1 instanceof AnswerMultipleChoiceQuestion && actualQuestion2 instanceof AnswerMultipleChoiceQuestion) {
                distanceAux = calculateDistanceLocal((AnswerMultipleChoiceQuestion)actualQuestion1, (AnswerMultipleChoiceQuestion)actualQuestion2);
            } else if (actualQuestion1 instanceof AnswerFreeQuestion && actualQuestion2 instanceof AnswerFreeQuestion) {
                distanceAux = calculateDistanceLocal((AnswerFreeQuestion)actualQuestion1, (AnswerFreeQuestion)actualQuestion2);
            } else {
                throw new IllegalArgumentException("Incompatible answer types or unsupported answer type");
            }
            distance += distanceAux * distanceAux;
        }
        distance = Math.sqrt(distance);
        distance /= answ1.size();

        return distance;
    }

    /**
	 * Computes the quality of the clustering using the Silhouette Coefficient.
	 * 
	 * @param answers the list of all user answers
	 * @param nClusters number of clusters
	 * @return Average Silhouette score for all points (between -1 and 1)
	 */
	protected double calculateQuality(ArrayList<ArrayList<AnswerToQuestion>> answers, int nClusters, int[] clusterAssignments) {
		if (nClusters <= 1) return 0.0; // Edge case
		
		// Build clusters
		ArrayList<ArrayList<Integer>> clusters = new ArrayList<>(nClusters);
		for (int i = 0; i < nClusters; i++) {
			clusters.add(new ArrayList<>());
		}

		for (int i = 0; i < answers.size(); i++) {
			int idCluster = clusterAssignments[i];
			clusters.get(idCluster).add(i);
		}

		// Compute quality (average Silhouette score)
		double sumQualities = 0;
		for (int i = 0; i < answers.size(); i++) {
			int idCluster = clusterAssignments[i];

			// Compute a
			ArrayList<Integer> sameCluster = clusters.get(idCluster);
			double a = 0;
			boolean onePoint = sameCluster.size() == 1;
			if (!onePoint) {
				for (int j : sameCluster) {
					if (i == j) continue;
					a += distanceManhattan(answers.get(i), answers.get(j));
				}
				a /= (sameCluster.size() - 1);
			}

			// Compute b: minimum average distance to points in other clusters
			double b = Double.MAX_VALUE;
			for (int j = 0; j < nClusters; j++) {
				if (j == idCluster) continue;
				ArrayList<Integer> otherCluster = clusters.get(j);
				if (otherCluster.size() == 0) continue;

				double avg = 0;
				for (int k : otherCluster) {
					avg += distanceManhattan(answers.get(i), answers.get(k));
				}
				avg /= otherCluster.size();
				if (avg < b) b = avg;
			}
			if (onePoint || b == Double.MAX_VALUE) sumQualities += 0; // One point in cluster silouhet score is 0.0
			else sumQualities += ((b - a)/Math.max(a,b));
		}
		sumQualities /= answers.size();
		return sumQualities;
	}
}
