import java.util.ArrayList;


public class TestController {
	public static void main(String[] args) {
		//Todo make stats reflect up until either best solution or until correct "goodness level" is reached
		//tests(10, "g", "/Users/zackleman/Desktop/assign1-ga-pbil-for-maxsat/t3pm3-5555.spn.cnf");
		tests(10, "p", "/Users/zackleman/Desktop/assign1-ga-pbil-for-maxsat/maxsat-problems/maxsat-crafted/MAXCUT/DIMACS_MOD/brock800_1.clq.cnf");


	}
	public static void tests(int numOfTrials, String algorithm, String problemLocation) {
		// TESTS variables
		ArrayList<Results> results = new ArrayList<Results>();
		// Import and format MAXSAT problem.
		SatProblem problem = new SatProblem();
		problem.createSatProblemFromFile(problemLocation);
		int numberOfLiterals = problem.getNumLiterals();
		ArrayList<ArrayList<Integer>> satProblem = problem.getProblem();

		// Set these for GA
		int popSize = 200;
		String selectionType = "ts";
		String crossoverType = "1c";
		Double crossoverProb = 0.7;
		Double mutationProb = 0.01;
		int maxIterations = 1000;
		//

		// Set these for PBIL
		int PBIL_samples = 100;
		double PBIL_learningRate = 0.1;
		double PBIL_negLearningRate = .075;
		double PBIL_mutProb = 0.02;
		double PBIL_mutShift = 0.05;
		int PBIL_maxIterations = 2000;
		//

		if (algorithm.equalsIgnoreCase("g")) {
			for (int i = 0; i < numOfTrials; i++) {
				Genetic geneticAlgo = new Genetic(popSize, numberOfLiterals, maxIterations, crossoverType,
						crossoverProb, mutationProb, satProblem);
				results.add(geneticAlgo.evolve(selectionType));
			}
			reportStats(results, numOfTrials);

		} else {
			for (int i = 0; i < numOfTrials; i++) {

				PBIL PBILAlgorithm = new PBIL(PBIL_samples, PBIL_learningRate, PBIL_negLearningRate, numberOfLiterals,
						PBIL_mutProb, PBIL_mutShift, PBIL_maxIterations, satProblem);
				results.add(PBILAlgorithm.evolve());

			}
			reportStats(results, numOfTrials);
		}

	}

	/* Report averages */
	public static void reportStats(ArrayList<Results> results, int numberofTrials) {
		System.out.println(numberofTrials + " trials were run.");
		long averageTime = 0;
		int averageBestGeneration = 0;
		int averageUnsatisfiedClauses = 0;
		double averagePercentSatisfiedClauses = 0;

		for (int i = 0; i < results.size(); i++) {
			averageTime += results.get(i).executionTime;
			averageBestGeneration += results.get(i).bestgeneration;
			averageUnsatisfiedClauses += results.get(i).numUnsatisifiedClauses;
			averagePercentSatisfiedClauses += results.get(i).percentSatisfied;
		}
		averageTime = averageTime / (long) numberofTrials;
		averageBestGeneration = averageBestGeneration / numberofTrials;
		averageUnsatisfiedClauses = averageUnsatisfiedClauses / numberofTrials;
		averagePercentSatisfiedClauses = averagePercentSatisfiedClauses / (double) numberofTrials;

		System.out.println("Average Genetic Algorithm Output for " + numberofTrials + " trials:");
		System.out.println("Number Of Variables: " + results.get(0).numVariables);
		System.out.println("Number Of Clauses: " + results.get(0).numClauses);
		System.out.println("Average Percent satisfied: " + averagePercentSatisfiedClauses + "%");
		System.out.println("Average Best Generation:" + averageBestGeneration);
		System.out.println("Average execution time: " + averageTime + " milliseconds");
	}

}
