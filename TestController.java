import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TestController {
	static String filename = "/Users/zackleman/Desktop/assign1-ga-pbil-for-maxsat/maxsat-problems/maxsat-crafted/MAXCUT/DIMACS_MOD/brock800_1.clq.cnf";
	
	// Set these for GA
	static int popSize = 200;
	static String selectionType = "ts";
	static String crossoverType = "1c";
	static Double crossoverProb = 0.7;
	static Double mutationProb = 0.01;
	static int maxIterations = 1000;
	//
	
	// Set these for PBIL
	static int PBIL_samples = 100;
	static double PBIL_learningRate = 0.1;
	static double PBIL_negLearningRate = .075;
	static double PBIL_mutProb = 0.02;
	static double PBIL_mutShift = 0.05;
	static int PBIL_maxIterations = 2000;
	//
	public static void main(String[] args) throws IOException {
		//Todo make stats reflect up until either best solution or until correct "goodness level" is reached
		//tests(10, "g", filename);
		tests(10, "p", filename);


	}
	public static void tests(int numOfTrials, String algorithm, String problemLocation) throws IOException {
		// TESTS variables
		ArrayList<Results> results = new ArrayList<Results>();
		// Import and format MAXSAT problem.
		SatProblem problem = new SatProblem();
		problem.createSatProblemFromFile(problemLocation);
		int numberOfLiterals = problem.getNumLiterals();
		ArrayList<ArrayList<Integer>> satProblem = problem.getProblem();
/*
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
*/
		if (algorithm.equalsIgnoreCase("g")) {
			for (int i = 0; i < numOfTrials; i++) {
				Genetic geneticAlgo = new Genetic(popSize, numberOfLiterals, maxIterations, crossoverType,
						crossoverProb, mutationProb, satProblem);
				results.add(geneticAlgo.evolve(selectionType));
			}
			reportStats(results, numOfTrials,algorithm);

		} else {
			for (int i = 0; i < numOfTrials; i++) {

				PBIL PBILAlgorithm = new PBIL(PBIL_samples, PBIL_learningRate, PBIL_negLearningRate, numberOfLiterals,
						PBIL_mutProb, PBIL_mutShift, PBIL_maxIterations, satProblem);
				results.add(PBILAlgorithm.evolve());

			}
			reportStats(results, numOfTrials,algorithm);
		}

	}

	/* Report averages */
	public static void reportStats(ArrayList<Results> results, int numberofTrials, String algorithm) throws IOException { 
		BufferedWriter outputWriter = null;
		String randomString = Double.toString(Math.random());
		File file = new File("/Users/zackleman/Desktop/Results/" + randomString + ".txt");

		// If file does not exists, then create it.
		if (!file.exists()) {
			file.createNewFile();
		}
		outputWriter = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
		
		System.out.println("File: " + filename);
		outputWriter.write("File: " + filename);
		System.out.println("Algorithm: " + algorithm);
		outputWriter.write("File: " + filename);
		System.out.println(numberofTrials + " trials were run.");
		outputWriter.write(numberofTrials + " trials were run.");
		outputWriter.newLine();
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
		outputWriter.newLine();
		System.out.println("Average Output for " + numberofTrials + " trials:");
		outputWriter.write("Average Output for " + numberofTrials + " trials:");
		outputWriter.newLine();
		System.out.println("Number Of Variables: " + results.get(0).numVariables);
		outputWriter.write("Number Of Variables: " + results.get(0).numVariables);
		outputWriter.newLine();
		System.out.println("Number Of Clauses: " + results.get(0).numClauses);
		outputWriter.write("Number Of Clauses: " + results.get(0).numClauses);
		outputWriter.newLine();
		System.out.println("Average Percent satisfied: " + averagePercentSatisfiedClauses + "%");
		outputWriter.write("Average Percent satisfied: " + averagePercentSatisfiedClauses + "%");
		outputWriter.newLine();
		System.out.println("Average Best Generation:" + averageBestGeneration);
		outputWriter.write("Average Best Generation:" + averageBestGeneration);
		outputWriter.newLine();
		System.out.println("Average execution time: " + averageTime + " milliseconds");
		outputWriter.write("Average execution time: " + averageTime + " milliseconds");
		outputWriter.newLine();
		
		/*
		 double sum = 0.0;
	        for (int i = 0; i < results.size(); i++) {
	            sum += Math.pow(((double)results.get(i).executionTime - (double) averageTime), 2);
	        }
	        double stdev = Math.sqrt( sum / (results.size() - 1));
	
	
	System.out.println("STDEV of execution time: " + stdev + " milliseconds");
	outputWriter.write("STDEV of execution time: " + stdev + " milliseconds");*/
		outputWriter.write("Settings GA");
		outputWriter.newLine();
		outputWriter.write(""+popSize);
		outputWriter.newLine();
		outputWriter.write(""+selectionType);
		outputWriter.newLine();
		outputWriter.write(""+crossoverType);
		outputWriter.newLine();
		outputWriter.write(""+crossoverProb);
		outputWriter.newLine();
		outputWriter.write(""+mutationProb);
		outputWriter.newLine();
		outputWriter.write(""+mutationProb);
		outputWriter.newLine();
		outputWriter.write(""+mutationProb);
		outputWriter.newLine();
		
		
		outputWriter.write("Settings PBIL");
		outputWriter.newLine();
		outputWriter.write(""+PBIL_samples);
		outputWriter.newLine();
		outputWriter.write(""+PBIL_learningRate);
		outputWriter.newLine();
		outputWriter.write(""+PBIL_negLearningRate);
		outputWriter.newLine();
		outputWriter.write(""+PBIL_mutProb);
		outputWriter.newLine();
		outputWriter.write(""+PBIL_mutShift);
		outputWriter.newLine();
		outputWriter.write(""+PBIL_maxIterations);
		outputWriter.newLine();
	
	
	outputWriter.flush();
	outputWriter.close();
	}
	
	
	

}
