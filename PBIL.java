import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

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
	// Others.
	private int maxFitness = 0;
	private int minFitness;
	private int maxIterations;
	private long endTime;
	private long timeout = Long.MAX_VALUE;
	private int optimalUnsat = Integer.MAX_VALUE; 

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

		minFitness = satProblem.size();
		evaluations = new int[samples];
		initProbVector();
	}
	
	// Constructor with optimal unsat value and timeout
	public PBIL(int samples, double learningRate, double negLearningRate, int length, double mutProb, double mutShift,
			int maxIterations, ArrayList<ArrayList<Integer>> satProblem, long timeout, int optimalUnsat) {
		this.samples = samples;
		this.learningRate = learningRate;
		this.negLearningRate = negLearningRate;
		this.length = length;
		this.mutProb = mutProb;
		this.mutShift = mutShift;
		this.maxIterations = maxIterations;
		this.satProblem = satProblem;
		this.timeout = timeout;
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
		long startTime = System.currentTimeMillis();
		int iterations = 0;
		boolean foundSATSolution = false;
		while (iterations < maxIterations) {
			currentGeneration = iterations;
			// Generate all individuals and evaluate them.
			for (int i = 0; i < samples; i++) {
				int[] individual = generateSampleVector(probVector);
				sampleVectors.add(individual);
				// Evaluate each candidate and store results in array.
				evaluations[i] = evaluateCandidate(satProblem, toObject(individual));
				// Found solution to all clauses.
				int fitness = evaluations[i];
				
				// Time out, do not run next iteration.
				long totalTimeElapsed = System.currentTimeMillis() - startTime;
				if (totalTimeElapsed > timeout) {
					foundSATSolution = true;
				}
				
				// If reached optimal number of clauses satisfied, return result
				int currentUnsat = satProblem.size() - fitness;
				if (currentUnsat <= optimalUnsat || fitness == satProblem.size()) {
					foundSATSolution = true;
				}
				
				if (foundSATSolution) {
					System.out.println("All clauses satisfied.");
					long endTime = System.currentTimeMillis();
					long executionTime = endTime - startTime;
					double percent = ((double) maxFitness * 100 / (double) satProblem.size());
					Results result = new Results("PBIL Algorithm", probVector.length, satProblem.size(), executionTime,
							(satProblem.size() - maxFitness), percent, bestVector, bestGeneration);
					return result;
				} else {
					updateFitness(fitness, individual);
				}
			}

			// Update and mutate probability vector.
			updateProbVector();
			mutateProbVector();

			iterations++;
		}

		System.out.println("PBIL Algorithm Output:");
		System.out.println("Number Of Variables: " + probVector.length);
		System.out.println("Number Of Clauses: " + satProblem.size());
		System.out.println("Satisfied Clauses: " + maxFitness + " out of " + satProblem.size() + " ("
				+ (satProblem.size() - maxFitness) + " unsatisfied clauses).");
		System.out.println("Best Variable Assignment: " + Arrays.toString(binaryToNumber(bestVector)));
		double percent = ((double) maxFitness * 100 / (double) satProblem.size());
		System.out.println("Percent satisfied: " + percent + "%");
		System.out.println("Best Generation:" + bestGeneration);
		
		long executionTime = endTime - startTime;
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
		// Cast int[] to Integer[]
		Integer[] result = new Integer[intArray.length];
		for (int i = 0; i < intArray.length; i++) {
			result[i] = Integer.valueOf(intArray[i]);
		}
		// Convert array to ArrayList
		ArrayList<Integer> resultList = new ArrayList<Integer>(Arrays.asList(result));
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