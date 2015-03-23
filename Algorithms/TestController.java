package Algorithms;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TestController {

	static String root = "/Users/mxing/Desktop/GENETIC-PBIL-MAXSAT/Results/";
	public static String[] files = {"SPINGLASS/t4pm3-6666.spn.cnf","SPINGLASS/t5pm3-7777.spn.cnf","SPINGLASS/t7pm3-9999.spn.cnf","SPINGLASS/t6pm3-8888.spn.cnf", "140v/s2v140c1600-10.cnf",
		"140v/s2v140c1200-10.cnf", "140v/s2v140c1300-10.cnf","140v/s2v140c1400-10.cnf", "140v/s2v140c1500-10.cnf","maxcut-140-630-0.8/maxcut-140-630-0.8-9.cnf", 
		"maxcut-140-630-0.8/maxcut-140-630-0.8-8.cnf","maxcut-140-630-0.7/maxcut-140-630-0.7-9.cnf", "maxcut-140-630-0.7/maxcut-140-630-0.7-8.cnf","60v/s3v60c800-1.cnf",
		"60v/s3v60c1000-1.cnf", "60v/s3v60c1200-1.cnf","4SAT/HG-4SAT-V100-C900-1.cnf","4SAT/HG-4SAT-V150-C1350-100.cnf", "5SAT/HG-5SAT-V50-C900-1.cnf", 
		"5SAT/HG-5SAT-V50-C900-5.cnf","5SAT/HG-5SAT-V100-C1800-100.cnf"};
	public static int[] maxValues= {38,78,0,0, 226,140,170,188,200,165,  167,166,165,35,53,69,1,0,0,0,0};

	// Set these for GA experiments
	static int[] popSize = {200, 400, 700, 1000, /* end pop*/ 200, 200, /*end selection*/ 200, /*end crossover*/ 200, 200, 200, /*end crossover prob*/ 200, 200, 200};
	static String[] selectionType = {"rs", "rs", "rs", "rs", /* end pop*/ "ts", "bs", /*end selection*/ "rs", /*end crossover*/ "rs", "rs", "rs", /*end crossover prob*/ "rs", "rs", "rs"};
	static String[] crossoverType = {"1c", "1c", "1c", "1c", /* end pop*/ "1c", "1c", /*end selection*/ "uc", /*end crossover*/ "1c", "1c", "1c", /*end crossover prob*/ "1c", "1c", "1c"};
	static Double[] crossoverProb = {0.7, 0.7, 0.7, 0.7, /* end pop*/ 0.7, 0.7, /*end selection*/ 0.7, /*end crossover*/ 0.1, 0.3, 1.0, /*end crossover prob*/ 0.7, 0.7, 0.7};
	static Double[] mutationProb = {0.01, 0.01, 0.01, 0.01, /* end pop*/ 0.01, 0.01, /*end selection*/ 0.01, /*end crossover*/ 0.01, 0.01, 0.01, /*end crossover prob*/ 0.1, 0.3, 0.5};
	static int maxIterations = Integer.MAX_VALUE;
	//

	// Set these for PBIL experiments
	static int[] PBIL_samples = {100, 300, 600, 1000, /* end pop*/ 100, 100, 100, 100, /*end lr*/ 100, 100, 100, /*end -lr*/ 100, 100, 100, /*end mutProb*/ 100, 100, 100, 100 /*end mutShift*/};
	static double[] PBIL_learningRate = {0.1, 0.1, 0.1, 0.1, /* end pop*/ 0.01, 0.3, 0.5, 1, /*end lr*/ 0.1, 0.1, 0.1, /*end -lr*/ 0.1, 0.1, 0.1, /*end mutProb*/ 0.1, 0.1, 0.1, 0.1 /*end mutShift*/};
	static double[] PBIL_negLearningRate = {0.075, 0.075, 0.075, 0.075, /* end pop*/ 0.075, 0.075, 0.075, 0.075, /*end lr*/ 0.02, 0.15, 0.3, /*end -lr*/ 0.075, 0.075, 0.075, /*end mutProb*/ 0.075, 0.075, 0.075, 0.075 /*end mutShift*/};
	static double[] PBIL_mutProb = {0.02, 0.02, 0.02, 0.02, /* end pop*/ 0.02, 0.02, 0.02, 0.02, /*end lr*/ 0.02, 0.02, 0.02, /*end -lr*/ 0.1, 0.3, 0.5, /*end mutProb*/ 0.02, 0.02, 0.02, 0.02 /*end mutShift*/};
	static double[] PBIL_mutShift = {0.05, 0.05, 0.05, 0.05, /* end pop*/ 0.05, 0.05, 0.05, 0.05, /*end lr*/ 0.05, 0.05, 0.05, /*end -lr*/ 0.05, 0.05, 0.05, /*end mutProb*/ 0.01, 0.1, 0.2, 0.5 /*end mutShift*/};
	static int PBIL_maxIterations = Integer.MAX_VALUE;

	//
	public static void main(String[] args) throws IOException {
		// Todo make stats reflect up until either best solution or until
		// correct "goodness level" is reached
		Runnable r1 = new Runnable() {
			public void run() {
				// Tests the first quarter of the input data
				try {
					testThread(0, files.length / 4);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		Runnable r2 = new Runnable() {
			public void run() {
				// Tests the second fourth of the input data
				try {
					testThread((files.length * 1) / 4, (files.length * 2) / 4);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		Runnable r3 = new Runnable() {
			public void run() {
				// Tests the third fourth of the input data
				try {
					testThread((files.length * 2) / 4, (files.length * 3) / 4);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		Runnable r4 = new Runnable() {
			public void run() {
				// Tests the last fourth of the input data
				try {
					testThread((files.length * 3) / 4, files.length);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		// Starts the 8 threads
		Thread thr1 = new Thread(r1);
		Thread thr2 = new Thread(r2);
		Thread thr3 = new Thread(r3);
		Thread thr4 = new Thread(r4);

		thr1.start();
		thr2.start();
		thr3.start();
		thr4.start();
		try {
			thr1.join();
			thr2.join();
			thr3.join();
			thr4.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void tests(int numOfTrials, String algorithm,
			String problemLocation, int index, int maxValue, int i)
			throws IOException {
		// TESTS variables
		ArrayList<Results> results = new ArrayList<Results>();
		// Import and format MAXSAT problem.
		SatProblem problem = new SatProblem();
		problem.createSatProblemFromFile(problemLocation);
		int numberOfLiterals = problem.getNumLiterals();
		ArrayList<ArrayList<Integer>> satProblem = problem.getProblem();

		if (algorithm.equalsIgnoreCase("g")) {
			for (int t = 0; t < numOfTrials; t++) {
				Genetic geneticAlgo = new Genetic(popSize[i], numberOfLiterals,
						maxIterations, crossoverType[i], crossoverProb[i],
						mutationProb[i], satProblem, maxValue);
				results.add(geneticAlgo.evolve(selectionType[i]));
			}
			reportStats(results, numOfTrials, algorithm, index, maxValue, i);

		} else {
			for (int t = 0; t < numOfTrials; t++) {

				PBIL PBILAlgorithm = new PBIL(PBIL_samples[i],
						PBIL_learningRate[i], PBIL_negLearningRate[i],
						numberOfLiterals, PBIL_mutProb[i], PBIL_mutShift[i],
						PBIL_maxIterations, satProblem, maxValue);
				results.add(PBILAlgorithm.evolve());

			}
			reportStats(results, numOfTrials, algorithm, index, maxValue, i);
		}
	}

	/* Report averages */
	public static void reportStats(ArrayList<Results> results,
			int numberofTrials, String algorithm, int problem, int maxValue,
			int index) throws IOException {
		BufferedWriter outputWriter = null;
		String randomString = Double.toString(Math.random());
		File file = new File(root + randomString + ".txt");

		// If file does not exists, then create it.
		if (!file.exists()) {
			file.createNewFile();
		}
		outputWriter = new BufferedWriter(
				new FileWriter(file.getAbsoluteFile()));

		System.out.println("File: " + files[problem]);
		outputWriter.write("File: " + files[problem]);
		System.out.println(numberofTrials + " trials were run.");
		// outputWriter.write(numberofTrials + " trials were run.");
		outputWriter.write(numberofTrials);
		outputWriter.newLine();
		long averageTime = 0;
		int averageBestGeneration = 0;
		int averageUnsatisfiedClauses = 0;
		int fewestUnsatisfiedClauses = Integer.MAX_VALUE;
		double averagePercentSatisfiedClauses = 0;
		int bestGeneration = Integer.MAX_VALUE;
		long bestExecutionTime = Integer.MAX_VALUE;
		//
		int averageBestGenerationT = 0;
		int averageUnsatisfiedClausesT = 0;
		int fewestUnsatisfiedClausesT = Integer.MAX_VALUE;
		double averagePercentSatisfiedClausesT = 0;
		int bestGenerationT = Integer.MAX_VALUE;

		int numTimeouts = 0; // Number of times the algorithm timed out.
		for (int i = 0; i < results.size(); i++) {
			int temp = results.get(i).numUnsatisifiedClauses;
			long totalTime = results.get(i).executionTime;
			if (totalTime > 0) {
				// Only add and average execution time if the algorithm didn't
				// time out.
				averageTime += totalTime;
				averagePercentSatisfiedClauses += results.get(i).percentSatisfied;
				averageBestGeneration += results.get(i).bestgeneration;
				averageUnsatisfiedClauses += temp;

				if (temp < fewestUnsatisfiedClauses) {
					fewestUnsatisfiedClauses = temp;
				}
				if (results.get(i).executionTime < bestExecutionTime) {
					bestExecutionTime = results.get(i).executionTime;
					;
				}
				if (results.get(i).bestgeneration < bestGeneration) {
					bestGeneration = results.get(i).bestgeneration;
					;
				}
			} else {
				numTimeouts++;
				// Only add and average execution time if the algorithm did time
				// out.

				averageBestGenerationT += results.get(i).bestgeneration;
				averageUnsatisfiedClausesT += temp;

				if (temp < fewestUnsatisfiedClausesT) {
					fewestUnsatisfiedClausesT = temp;
				}

				if (results.get(i).bestgeneration < bestGenerationT) {
					bestGenerationT = results.get(i).bestgeneration;
					;
				}
			}
		}

		int totalNumClauses = results.get(0).numClauses;
		if (numberofTrials - numTimeouts != 0) {
			averageTime = averageTime / (long) (numberofTrials - numTimeouts);
			averageBestGeneration = averageBestGeneration
					/ (numberofTrials - numTimeouts);
			averageUnsatisfiedClauses = averageUnsatisfiedClauses
					/ (numberofTrials - numTimeouts);
			averagePercentSatisfiedClauses = (totalNumClauses - averageUnsatisfiedClauses)
					/ (totalNumClauses - maxValue);
			outputWriter.newLine();
		} else {
			averageTime = -1;
			averageBestGeneration = -1;
			averageUnsatisfiedClauses = -1;
			averagePercentSatisfiedClauses = -1;
			outputWriter.newLine();
		}

		if (numTimeouts != 0) {
			averageBestGenerationT = averageBestGenerationT / numTimeouts;
			averageUnsatisfiedClausesT = averageUnsatisfiedClausesT
					/ numTimeouts;
			averagePercentSatisfiedClausesT = (double) (totalNumClauses - averageUnsatisfiedClausesT)
					/ (double) (totalNumClauses - maxValue);
			outputWriter.newLine();
		} else {
			averageBestGenerationT = -1;
			averageUnsatisfiedClausesT = -1;
			averagePercentSatisfiedClausesT = -1;
		}

		// System.out.println("Average Output for " + numberofTrials +
		// " trials:");
		// outputWriter.write("Average Output for " + numberofTrials +
		// " trials:");
		// outputWriter.newLine();
		// System.out.println("Number Of Variables: " +
		// results.get(0).numVariables);
		// outputWriter.write("Number Of Variables: " +
		// results.get(0).numVariables);
		// outputWriter.newLine();
		// System.out.println("Number Of Clauses: " +
		// results.get(0).numClauses);
		// outputWriter.write("Number Of Clauses: " +
		// results.get(0).numClauses);
		// outputWriter.newLine();
		// System.out.println("Average Percent satisfied: " +
		// averagePercentSatisfiedClauses + "%");
		// outputWriter.write("Average Percent satisfied: " +
		// averagePercentSatisfiedClauses + "%");
		// outputWriter.newLine();
		// System.out.println("Average Best Generation:" +
		// averageBestGeneration);
		// outputWriter.write("Average Best Generation:" +
		// averageBestGeneration);
		// outputWriter.newLine();
		// System.out.println("Average # Unsatisfied  Clauses:" +
		// averageUnsatisfiedClauses);
		// outputWriter.write("Average # Unsatisfied  Clauses:" +
		// averageUnsatisfiedClauses);
		// outputWriter.newLine();
		// System.out.println("Fewest # Unsatisfied  Clauses:" +
		// fewestUnsatisfiedClauses);
		// outputWriter.write("Fewest # Unsatisfied  Clauses:" +
		// fewestUnsatisfiedClauses);
		// outputWriter.newLine();
		// System.out.println("Average execution time: " + averageTime +
		// " milliseconds");
		// outputWriter.write("Average execution time: " + averageTime +
		// " milliseconds");
		//
		System.out.println("SAT PROBLEM");
		outputWriter.write("SAT PROBLEM");
		outputWriter.newLine();
		System.out.println("Average Output for " + numberofTrials + " trials:");
		outputWriter.write("" + numberofTrials);
		outputWriter.newLine();
		System.out.println("Number Of Variables: "
				+ results.get(0).numVariables);
		outputWriter.write("" + results.get(0).numVariables);
		outputWriter.newLine();
		System.out.println("Number Of Clauses: " + results.get(0).numClauses);
		outputWriter.write("" + results.get(0).numClauses);
		outputWriter.newLine();

		System.out.println("Stats for trials that did not timeout");
		outputWriter.write("Stats for trials that did not timeout");
		outputWriter.newLine();

		System.out.println("Average Best Generation:" + averageBestGeneration);
		outputWriter.write("" + averageBestGeneration);
		outputWriter.newLine();
		System.out.println("Best Generation:" + bestGeneration);
		outputWriter.write("" + bestGeneration);
		outputWriter.newLine();
		System.out.println("Average # Unsatisfied  Clauses:"
				+ averageUnsatisfiedClauses);
		outputWriter.write("" + averageUnsatisfiedClauses);
		outputWriter.newLine();
		System.out.println("Fewest # Unsatisfied  Clauses:"
				+ fewestUnsatisfiedClauses);
		outputWriter.write("" + fewestUnsatisfiedClauses);
		outputWriter.newLine();
		System.out.println("Average execution time: " + averageTime
				+ " milliseconds");
		outputWriter.write("" + averageTime);
		outputWriter.newLine();
		System.out.println("Best execution time: " + bestExecutionTime
				+ " milliseconds");
		outputWriter.write("" + bestExecutionTime);
		outputWriter.newLine();

		System.out.println("Stats for timeouts");
		outputWriter.write("Stats for timeouts");
		outputWriter.newLine();

		System.out.println("Average Best Generation:" + averageBestGenerationT);
		outputWriter.write("" + averageBestGenerationT);
		outputWriter.newLine();
		System.out.println("Best Generation:" + bestGenerationT);
		outputWriter.write("" + bestGenerationT);
		outputWriter.newLine();
		System.out.println("Average # Unsatisfied  Clauses:"
				+ averageUnsatisfiedClausesT);
		outputWriter.write("" + averageUnsatisfiedClausesT);
		outputWriter.newLine();
		System.out.println("Fewest # Unsatisfied  Clauses:"
				+ fewestUnsatisfiedClausesT);
		outputWriter.write("" + fewestUnsatisfiedClausesT);
		outputWriter.newLine();
		System.out.println("Number of time outs: " + numTimeouts);
		outputWriter.write("" + numTimeouts);
		outputWriter.newLine();
		System.out.println("Average Percent satisfied: "
				+ averagePercentSatisfiedClausesT + "%");
		outputWriter.write("" + averagePercentSatisfiedClausesT);
		outputWriter.newLine();

		if (algorithm.equalsIgnoreCase("g")) {
			outputWriter.write("Settings GA");
			outputWriter.newLine();
			outputWriter.write("" + popSize[index]);
			outputWriter.newLine();
			outputWriter.write("" + selectionType[index]);
			outputWriter.newLine();
			outputWriter.write("" + crossoverType[index]);
			outputWriter.newLine();
			outputWriter.write("" + crossoverProb[index]);
			outputWriter.newLine();
			outputWriter.write("" + mutationProb[index]);
			outputWriter.newLine();
			outputWriter.write("" + PBIL_maxIterations);
			outputWriter.newLine();

		} else {
			outputWriter.write("Settings PBIL");
			outputWriter.newLine();
			outputWriter.write("" + PBIL_samples[index]);
			outputWriter.newLine();
			outputWriter.write("" + PBIL_learningRate[index]);
			outputWriter.newLine();
			outputWriter.write("" + PBIL_negLearningRate[index]);
			outputWriter.newLine();
			outputWriter.write("" + PBIL_mutProb[index]);
			outputWriter.newLine();
			outputWriter.write("" + PBIL_mutShift[index]);
			outputWriter.newLine();
			outputWriter.write("" + PBIL_maxIterations);
			outputWriter.newLine();
		}

		outputWriter.flush();
		outputWriter.close();
	}

	static void testThread(int start, int end) throws IOException {
		for (int p = start; p < end; p++) {
			for (int trialIndex = 0; trialIndex < popSize.length; trialIndex++) {
				tests(10, "p", root + files[p], p, maxValues[p], trialIndex);
				tests(10, "g", root + files[p], p, maxValues[p], trialIndex);
			}
		}
	}
}
