import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class PBIL extends GAAlgorithms {
	// User inputs.
	private int samples;
	private double learningRate;
	private double negLearningRate;
	private int length;
	private double mutProb;
	private double mutShift;
	// Vectors.
	private double[] probVector;
	private int[] evaluations;
	private ArrayList<int[]> sampleVectors = new ArrayList<int[]>();
	// Others.
	private int maxIterations;
	private Random randomGenerator;
	private ArrayList<ArrayList<Integer>> satProblem = new ArrayList<ArrayList<Integer>>();
	
	
	// Constructor. 
	public PBIL (int samples, double learningRate, double negLearningRate, int length, double mutProb, 
			double mutShift, int maxIterations, ArrayList<ArrayList<Integer>> satProblem) {
		this.samples = samples;
		this.learningRate = learningRate;
		this.negLearningRate = negLearningRate;
		this.length = length;
		this.mutProb = mutProb;
		this.mutShift = mutShift;
		this.maxIterations = maxIterations;
		this.satProblem = satProblem;
		this.randomGenerator = new Random();
	}
	
	// Initialize probability vector.
	public void initProbVector() {
		probVector = new double[length];
		for (int i = 0; i < probVector.length; i++) {
			probVector[i] = 0.5;
		}
	}
	
	public double[] iteratePBIL() {
		int iterations = 0;
		while (iterations < maxIterations) {
			// Generate all individuals and evaluate them.
			for (int i = 0; i < samples; i++) {
				int[] individual = generateSampleVector(probVector);
				sampleVectors.add(individual);
				// Evaluate each candidate and store results in array.
				evaluations[i] = evaluateCandidate(satProblem, toObject(individual));
				// Found solution to all clauses.
				if (evaluations[i] == satProblem.size()) {
					return probVector;
				}
			}
			// Keep track of the best and worst candidate.
			int[] bestVector = findBestVector();
			int[] worstVector = findWorstVector();
			
			// Update probability vector toward best solution.
			for (int i = 0; i < probVector.length; i++) {
				probVector[i] = probVector[i] * (1.0 - learningRate) + 
						bestVector[i] * learningRate;
			}
			// Update probability vector away from worst solution.
			for (int i = 0; i < probVector.length; i++) {
				if (bestVector[i] != worstVector[i]) {
					probVector[i] = probVector[i] * (1.0 - negLearningRate) + 
							bestVector[i] * negLearningRate;
				}
			}
			
			// Mutate probability vector.
			for (int i = 0; i < probVector.length; i++) {
				if (randomGenerator.nextDouble() < mutProb) {
					int mutateDirection = randomGenerator.nextDouble() > 0.5 ? 1 : 0;
					probVector[i] = probVector[i] * (1.0 - mutShift) + mutateDirection * mutShift;
				}
			}
			iterations++;
		}	
		return probVector;
	}
	
	// Helper method to convert an int array to an ArrayList of Integer.
	public static ArrayList<Integer> toObject(int[] intArray) {
		// Cast int[] to Integer[]
		Integer[] result = new Integer[intArray.length];
		for (int i = 0; i < intArray.length; i++) {
			result[i] = Integer.valueOf(intArray[i]);
		}
		// Convert array to ArrayList
		ArrayList<Integer> resultList = new ArrayList<Integer>(Arrays.asList(result));
		return resultList;
	}
	
	// Generate the individual according to probability.
	private int[] generateSampleVector(double[] prob) {
		int[] individual = new int[length];
		// Generate according to probability
		for (int i = 0; i < individual.length; i++) {
			double p = randomGenerator.nextDouble();
			individual[i] = (p < prob[i]) ? 1 : 0;
		}
		return individual;
	}
	
	// Return the strongest candidate.
	private int[] findBestVector() {
		int maxIndex = 0;
		int max = evaluations[0];
		for (int i = 1; i < evaluations.length; i++) {
			if (evaluations[i] > max) {
				maxIndex = i;
			}
		}
		// Candidate at the corresponding index.
		return sampleVectors.get(maxIndex);
	}
	
	// Return the weakest candidate.
	private int[] findWorstVector(){
		int minIndex = 0;
		int min = evaluations[0];
		for (int i = 1; i < evaluations.length; i++) {
			if (evaluations[i] < min) {
				minIndex = i;
			}
		}
		// Get candidate at the corresponding index.
		return sampleVectors.get(minIndex);
	}
}
