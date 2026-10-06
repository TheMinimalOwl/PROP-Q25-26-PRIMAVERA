package domain;

import java.util.ArrayList;
import java.util.Random;

/**
 * Class representing the K-Medoids strategy algorith, executes KMedoids
 * 
 * @author Tomeu Mestre (tomeu.mestre@estudiantat.upc.edu)
 */
public class KMedoidsStrategy extends AnalyzeStrategy {
    private int[] medoidIndices; // Indices of current medoids
    private int[] clusterAssignments; // Cluster assignment for each data point (index of the medoid)
	private int maxIterations;
	private double[][] distances; // Precalculation matrice of local distances between users i j distances[i][j]
    
    // FastPAM1
    private double[] distanceToNearest; // Distance of each point (user) to the closest medoid
    private double[] distanceToSecondNearest; // Distance of each point (user) to the second closest medoid

    /**
     * Default constructor initializing with default values.
     */
    private KMedoidsStrategy() {
        this.maxIterations = 100; // Max iterations by default, TODO: How many should we have by default 100-300-1000???
    }

    private static class KMedoidsStrategySingleton {
        private static final KMedoidsStrategy instance = new KMedoidsStrategy();  
    }

    public static KMedoidsStrategy getInstance() {
        return KMedoidsStrategySingleton.instance;
    }
    
    /**
     * Executes K-Medoids clustering analysis on the provided answers.
     * 
     * @param answers the list of answers to analyze
     * @param nClusters number of clusters to create
     * @param form the form identifier to access question metadata
     * @return AnalysisResult containing cluster assignments and medoids
     * @throws IllegalArgumentException if answers is null or empty
     */
    @Override
    public AnalysisResult doAnalysis(ArrayList<ArrayList<AnswerToQuestion>> answers, int nClusters, Integer form) {  
        idForm = form;
        int nUsers = answers.size();
        
        medoidIndices = new int[nClusters];
        clusterAssignments = new int[nUsers];

        precalculateDistances(answers);
        initializeMedoids(nUsers, nClusters);
        updateDistanceNearestMedoids(nUsers, nClusters);
        assignClusters(answers, nClusters);
        
		// Iterate swapping medoids till converging or max iterations
		boolean hasChanged = true;
        int iteration = 0;
        
        while (hasChanged && iteration < maxIterations) {
            hasChanged = false;

            // Find the BEST swap (medoid <--> no medoid)
            int swap[] = findBestSwap(nUsers, nClusters);

            if (swap[0] != -1 && swap[1] != -1) {
                medoidIndices[swap[0]] = swap[1];
                hasChanged = true;
                assignClusters(answers, nClusters);
                updateDistanceNearestMedoids(nUsers, nClusters);
            }
            iteration++;

            // This implementation was incorrect, half steepest descend
            // Try to swap each medoid with a non-medoid point
            //for (int i = 0; i < nClusters; i++) {
            //    int bestSwap = findBestSwap(i, nUsers, answers, nClusters);
            //    if (bestSwap != -1 && bestSwap != medoidIndices[i]) {
            //        medoidIndices[i] = bestSwap;
            //        hasChanged = true;
            //    }
            //}
        }
        
        double quality = calculateQuality(answers, nClusters, clusterAssignments);
        return buildResult(quality, answers);
    }

	// Helper methods

    /**
     * Updates the distances to the nearest and second nearest medoids for all datapoints (users).
     * This is part of the FastPAM optimization to speed up the swap step.
     * 
     * @param nUsers total number of users (data points)
     * @param nClusters number of clusters (medoids)
     */
    void updateDistanceNearestMedoids(int nUsers, int nClusters) {
        distanceToNearest = new double[nUsers];
        distanceToSecondNearest = new double[nUsers];
        
        for (int i = 0; i < nUsers; i++) {
            double minDistance = Double.MAX_VALUE;
            double secondMinDistance = Double.MAX_VALUE;
            
            for (int j = 0; j < nClusters; j++) {
                double dist = distances[i][medoidIndices[j]];
                if (dist < minDistance) {
                    secondMinDistance = minDistance;
                    minDistance = dist;
                } else if (dist < secondMinDistance) {
                    secondMinDistance = dist;
                }
            }
            distanceToNearest[i] = minDistance;
            distanceToSecondNearest[i] = secondMinDistance;
        }
    }
    

    /**
     * Precalculates all pairwise distances between users.
     * This is done once at the start to avoid redundant distance calculations.
     * Way more eficient when nUsers is large and nQuestions per user is large
     * 
     * @param answers the list of user answers
     */
    private void precalculateDistances(ArrayList<ArrayList<AnswerToQuestion>> answers) {
        int nUsers = answers.size();
        distances = new double[nUsers][nUsers];
        
        // Matrice is symetric, so we only need to compute n/2
        for (int i = 0; i < nUsers; i++) {
            distances[i][i] = 0.0;
            for (int j = i + 1; j < nUsers; j++) {
                double dist = distanceManhattan(answers.get(i), answers.get(j));
                distances[i][j] = dist;
                distances[j][i] = dist;
            }
        }
    }

    /**
     * Builds the AnalysisResult from the clustering results.
     * 
	 * @param quality quality metric of the clustering
     * @param answers answers matrice
     * @return AnalysisResult containing the clustering information
     */
    private AnalysisResult buildResult(double quality, ArrayList<ArrayList<AnswerToQuestion>> answers) {
        ArrayList<ArrayList<AnswerToQuestion>> medoids = new ArrayList<>(medoidIndices.length);

        for (int i = 0; i < medoidIndices.length; i++) {
            medoids.add(answers.get(medoidIndices[i]));
        }

        return new AnalysisResult(clusterAssignments, medoids, quality);
    }

    
    /**
     * Initializes the algotithm by randomly selecting n distinct data points (users) as initial medoids
     * 
     * @param n total number of data points
     * @param nClusters number of clusters
     */
    private void initializeMedoids(int n, int nClusters) {
        Random rand = new Random();
        boolean[] selected = new boolean[n];
        
        for (int i = 0; i < nClusters; i++) {
            int index;
			while (true) {
				index = rand.nextInt(n);
				if (!selected[index]) break;
			}

            medoidIndices[i] = index;
            selected[index] = true;
        }
    }
    
    /**
     * Assigns each user answers to the nearest medoid
     * 
     * @param answers answers matrice
     * @param nCluster number of clusters
     */
    private void assignClusters(ArrayList<ArrayList<AnswerToQuestion>> answers, int nClusters) {
        for (int i = 0; i < answers.size(); i++) {
            double minDistance = Double.MAX_VALUE;
            int nearestMedoid = 0;
            
            for (int j = 0; j < nClusters; j++) {
                double distance = distances[i][medoidIndices[j]];
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestMedoid = j;
                }
            }
            clusterAssignments[i] = nearestMedoid;
        }
    }
    
    /**
     * Finds the best swap (medoid, non-medoid) using FastPAM1 optimization.
     * Evaluates all possible swaps and returns the one with the best (most negative) delta.
     * 
     * @param nUsers number of users
     * @param nClusters number of clusters
     * @return int array with [medoid index to swap, candidate point index], or [-1, -1] if no improving swap found
     */
    private int[] findBestSwap(int nUsers, int nClusters) {
        double bestDelta = 0.0;
        int[] bestSwap = new int[] {-1, -1}; // (medoid index to swap, non-medoid to swap)

        for (int candidate = 0; candidate < nUsers; candidate++) {
            if (isMedoid(candidate)) continue;

            double[] deltas = new double[nClusters]; // Increment de sa distancia de candidat (user) si el canviam per medoide i
            for (int i = 0; i < nClusters; i++) {
                deltas[i] = -distanceToNearest[candidate];
            }

            // For each point/user, compute contribution to delta for each medoid swap
            for (int i = 0; i < nUsers; i++) {
                if (i == candidate) continue;
                
                double dCandidate = distances[i][candidate];
                double dNearest = distanceToNearest[i];
                double dSecond = distanceToSecondNearest[i];
                int assignedMedoid = clusterAssignments[i];
                
                deltas[assignedMedoid] += Math.min(dCandidate, dSecond) - dNearest;
                if (dCandidate < dNearest) {
                    for (int m = 0; m < nClusters; m++) {
                        if (m == assignedMedoid) continue;
                        deltas[m] += dCandidate - dNearest;
                    }
                }
            }
            
            // Find the best delta among all possible medoid swaps calculated
            for (int m = 0; m < nClusters; m++) {
                if (deltas[m] < bestDelta) {
                    bestDelta = deltas[m];
                    bestSwap[0] = m;
                    bestSwap[1] = candidate;
                }
            }
        }
        return bestSwap;
    }
    
    /**
     * Checks if a given point index is currently a medoid.
     * 
     * @param pointIndex index to check
     * @return true if the point is a medoid
     */
    private boolean isMedoid(int pointIndex) {
        for (int medoid : medoidIndices) {
            if (medoid == pointIndex) return true;
        }
        return false;
    }
    
    /**
     * Calculates the total cost (sum of distances from each User to its medoid).
     * 
     * @param answers answers matrice
     * 
     * @return total cost of current clustering
     */
    private double calculateTotalCost(ArrayList<ArrayList<AnswerToQuestion>> answers) {
        double totalCost = 0.0;
        
        for (int i = 0; i < answers.size(); i++) {
            int medoidCluster = clusterAssignments[i];
            int medoidIndex = medoidIndices[medoidCluster];
            totalCost += distances[i][medoidIndex];
        }
        
        return totalCost;
    }
    
    
	// Getters and setters

    public int getMaxIterations() {
        return maxIterations;
    }
    
    public void setMaxIterations(int maxIterations) {
        if (maxIterations < 1) {
            throw new IllegalArgumentException("Max iterations must be at least 1");
        }
        this.maxIterations = maxIterations;
    }
}
