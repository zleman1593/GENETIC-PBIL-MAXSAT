import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class PBIL {
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
	private int iterations = 0;
	private Random randomGenerator =  new Random();
		
	// Constructor. 
	public PBIL (int s, double lr, double neglr, int l, double mProb, double mShift, int i) {
		samples = s;
		learningRate = lr;
		negLearningRate = neglr;
		length = l;
		mutProb = mProb;
		mutShift = mShift;
		maxIterations = i;
		probVector = new double[l];
	}
	
	// Initialize probability vector.
	public void initProbVector() {
		for (int i = 0; i < probVector.length; i++) {
			probVector[i] = 0.5;
		}
	}
	
	public double[] iteratePBIL() {
		while (iterations < maxIterations) {
			// Generate all individuals and evaluate them.
			for (int i = 0; i < samples; i++) {
				int[] individual = generateSampleVector(probVector);
				sampleVectors.add(individual);
				// Evaluate each candidate and store results in array.
				evaluations[i] = evaluateCandidate(toObject(individual));
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
