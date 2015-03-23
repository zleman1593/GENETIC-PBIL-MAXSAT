package Algorithms;
import java.util.ArrayList;
import java.util.Arrays;

public class PBIL extends EvolAlgorithms {
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
	private int[] bestVector;
	private int[] worstVector;
	private ArrayList<int[]> sampleVectors = new ArrayList<int[]>();
	// Iterations and fitness.
	private int maxFitness = 0;
	private int minFitness;
	private int maxIterations;
	// The minimum number of unsatisfied clauses according to the best algorithm run on the website you provided.
	private int optimalUnsat;
	// The time the algorithm took to find the best solution.
	private long endTime;

	// Constructor.
	public PBIL(int samples, double learningRate, double negLearningRate, int length, double mutProb, double mutShift,
			int maxIterations, ArrayList<ArrayList<Integer>> satProblem) {
		this.samples = samples;
		this.learningRate = learningRate;
		this.negLearningRate = negLearningRate;
		this.length = length;
		this.mutProb = mutProb;
		this.mutShift = mutShift;
		this.maxIterations = maxIterations;
		this.satProblem = satProblem;
		this.timeout = Long.MAX_VALUE;
		this.optimalUnsat = Integer.MAX_VALUE;
		
		minFitness = satProblem.size();
		evaluations = new int[samples];
		initProbVector();
	}
	
	// Constructor with optimal unsat value and timeout
	public PBIL(int samples, double learningRate, double negLearningRate, int length, double mutProb, double mutShift,
			int maxIterations, ArrayList<ArrayList<Integer>> satProblem, int optimalUnsat) {
		this.samples = samples;
		this.learningRate = learningRate;
		this.negLearningRate = negLearningRate;
		this.length = length;
		this.mutProb = mutProb;
		this.mutShift = mutShift;
		this.maxIterations = maxIterations;
		this.satProblem = satProblem;
		this.optimalUnsat = optimalUnsat;

		minFitness = satProblem.size();
		evaluations = new int[samples];
		initProbVector();
	}

	// Initialize probability vector.
	public void initProbVector() {
		probVector = new double[length];
		for (int i = 0; i < probVector.length; i++) {
			probVector[i] = 0.5;
		}
	}

	public Results evolve() {
		sampleVectors.clear();
		long startTime = System.currentTimeMillis();
		int iterations = 0;
		while (iterations < maxIterations) {
			currentGeneration = iterations;
			// Generate all individuals and evaluate them.
			for (int i = 0; i < samples; i++) {
				int[] individual = generateSampleVector(probVector);
				sampleVectors.add(individual);
				// Evaluate each candidate and store results in array.
				evaluations[i] = evaluateCandidate(toObject(individual));
				// Found solution to all clauses.
				int fitness = evaluations[i];
				
				// If reached optimal number of clauses satisfied or time out, return result
				long totalTimeElapsed = System.currentTimeMillis() - startTime;
				int currentUnsat = satProblem.size() - maxFitness;
				if (totalTimeElapsed >= timeout) {
					// Timed out, do not factor in execution time.
					return getResult(endTime + 1);
				} else if (currentUnsat <= optimalUnsat || maxFitness == satProblem.size()) {
					// Calculate execution time.
					return getResult(startTime);
				}
				else {
					updateFitness(fitness, individual);
				}
			}
			// Update and mutate probability vector.
			updateProbVector();
			mutateProbVector();

			iterations++;
		}
		
		return getResult(startTime);
	}
	
	private Results getResult(long startTime) {
		System.out.println("PBIL Algorithm Output:");
		System.out.println("Number Of Variables: " + probVector.length);
		System.out.println("Number Of Clauses: " + satProblem.size());
		System.out.println("Satisfied Clauses: " + maxFitness + " out of " + satProblem.size() + " ("
				+ (satProblem.size() - maxFitness) + " unsatisfied clauses).");
		System.out.println("Best Variable Assignment: " + Arrays.toString(binaryToNumber(bestVector)));
		
		double percent = ((double) maxFitness * 100 / (double) satProblem.size());
		long executionTime = endTime - startTime; // -1 if timed out
		
		System.out.println("Percent satisfied: " + percent + "%");
		System.out.println("Best Generation:" + bestGeneration);		
		System.out.println("Total execution time: " + executionTime + " milliseconds");

		Results result = new Results("PBIL Algorithm", probVector.length, satProblem.size(), executionTime,
				(satProblem.size() - maxFitness), percent, bestVector, bestGeneration);
		return result;		
	}

	// Generate the individual according to probability.
	private int[] generateSampleVector(double[] prob) {
		int[] individual = new int[prob.length];
		// Generate according to probability
		for (int i = 0; i < individual.length; i++) {
			double p = randomGenerator.nextDouble();
			individual[i] = (p < prob[i]) ? 1 : 0;
		}
		return individual;
	}

	// Update probability vector.
	private void updateProbVector() {
		// Update probability vector toward best solution.
		for (int i = 0; i < probVector.length; i++) {
			probVector[i] = probVector[i] * (1.0 - learningRate) + bestVector[i] * learningRate;
		}
		// Update probability vector away from worst solution.
		for (int i = 0; i < probVector.length; i++) {
			if (bestVector[i] != worstVector[i]) {
				probVector[i] = probVector[i] * (1.0 - negLearningRate) + bestVector[i] * negLearningRate;
			}
		}
	}

	// Mutate probability vector.
	private void mutateProbVector() {
		// Mutate probability vector.
		for (int i = 0; i < probVector.length; i++) {
			if (randomGenerator.nextDouble() < mutProb) {
				int mutateDirection = randomGenerator.nextDouble() > 0.5 ? 1 : 0;
				probVector[i] = probVector[i] * (1.0 - mutShift) + mutateDirection * mutShift;
			}
		}
	}

	// Keep track of current max and min fitness and update individual
	// accordingly.
	private void updateFitness(int fitness, int[] individual) {
		if (fitness > maxFitness) {
			bestGeneration = currentGeneration;
			bestVector = individual;
			maxFitness = fitness;
			endTime = System.currentTimeMillis();
		}
		if (fitness < minFitness) {
			worstVector = individual;
			minFitness = fitness;
		}
	}

	// Helper method to convert an int array to an ArrayList of Integer.
	public static ArrayList<Integer> toObject(int[] intArray) {
		// Convert array to ArrayList
		ArrayList<Integer> resultList = new ArrayList<Integer>();
		resultList.clear();
		for (int i = 0; i < intArray.length; i++) {
			resultList.add(Integer.valueOf(intArray[i]));
		}
		
		Runtime r = Runtime.getRuntime();
		r.gc();
		
		return resultList;
	}

	/* Makes the solution more human readable */
	private int[] binaryToNumber(int[] solution) {
		int[] display = new int[solution.length];
		for (int i = 0; i < solution.length; i++) {
			if (solution[i] < 1) {
				display[i] = -1 * (i + 1);
			} else {
				display[i] = (i + 1);
			}
		}
		return display;
	}
}