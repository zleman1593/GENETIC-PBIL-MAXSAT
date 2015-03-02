import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TestController {

	static String root = "/Users/mxing/Desktop/GENETIC-PBIL-MAXSAT/Results/";
	static String[] files= {"SPINGLASS/t4pm3-6666.spn.cnf","SPINGLASS/t5pm3-7777.spn.cnf","SPINGLASS/t7pm3-9999.spn.cnf","SPINGLASS/t6pm3-8888.spn.cnf", "140v/s2v140c1600-10.cnf",
		"140v/s2v140c1200-10.cnf", "140v/s2v140c1300-10.cnf","140v/s2v140c1400-10.cnf", "140v/s2v140c1500-10.cnf","maxcut-140-630-0.8/maxcut-140-630-0.8-9.cnf", 
		"maxcut-140-630-0.8/maxcut-140-630-0.8-8.cnf","maxcut-140-630-0.7/maxcut-140-630-0.7-9.cnf","maxcut-140-630-0.7/maxcut-140-630-0.7-8.cnf","60v/s3v60c800-1.cnf",
		"60v/s3v60c1000-1.cnf","60v/s3v60c1200-1.cnf","4SAT/HG-4SAT-V100-C900-1.cnf","4SAT/HG-4SAT-V150-C1350-100.cnf","5SAT/HG-5SAT-V50-C900-1.cnf","5SAT/HG-5SAT-V50-C900-5.cnf","5SAT/HG-V100-C1800-100.cnf"};
	static int[] maxValues= {38,78,0,0, 226,140,170,188,200,165,167,166,165,35,53,69,1,0,0,0,0};

	// Set these for GA
	static int popSize = 200;
	static String selectionType = "ts";
	static String crossoverType = "1c";
	static Double crossoverProb = 0.7;
	static Double mutationProb = 0.01;
	static int maxIterations = Integer.MAX_VALUE;
	//

	// Set these for PBIL
	static int PBIL_samples = 100;
	static double PBIL_learningRate = 0.1;
	static double PBIL_negLearningRate = .075;
	static double PBIL_mutProb = 0.02;
	static double PBIL_mutShift = 0.05;
	static int PBIL_maxIterations = Integer.MAX_VALUE;

	//
	public static void main(String[] args) throws IOException {
		//Todo make stats reflect up until either best solution or until correct "goodness level" is reached

		for(int i = 0; i < files.length ;i++){
			tests(10, "p", root + files[i],i,maxValues[i]);
			tests(10, "g", root + files[i],i,maxValues[i]);
		}


	}
	public static void tests(int numOfTrials, String algorithm, String problemLocation, int index, int maxValue) throws IOException {
		// TESTS variables
		ArrayList<Results> results = new ArrayList<Results>();
		// Import and format MAXSAT problem.
		SatProblem problem = new SatProblem();
		problem.createSatProblemFromFile(problemLocation);
		int numberOfLiterals = problem.getNumLiterals();
		ArrayList<ArrayList<Integer>> satProblem = problem.getProblem();

		if (algorithm.equalsIgnoreCase("g")) {
			for (int i = 0; i < numOfTrials; i++) {
				Genetic geneticAlgo = new Genetic(popSize, numberOfLiterals, maxIterations, crossoverType,
						crossoverProb, mutationProb, satProblem, maxValue);
				results.add(geneticAlgo.evolve(selectionType));
			}
			reportStats(results, numOfTrials,algorithm,index,maxValue);

		} else {
			for (int i = 0; i < numOfTrials; i++) {

				PBIL PBILAlgorithm = new PBIL(PBIL_samples, PBIL_learningRate, PBIL_negLearningRate, numberOfLiterals,
						PBIL_mutProb, PBIL_mutShift, PBIL_maxIterations, satProblem, maxValue);
				results.add(PBILAlgorithm.evolve());

			}
			reportStats(results, numOfTrials,algorithm,index,maxValue);
		}

	}

	/* Report averages */
	public static void reportStats(ArrayList<Results> results, int numberofTrials, String algorithm, int problem,int maxValue) throws IOException { 
		BufferedWriter outputWriter = null;
		String randomString = Double.toString(Math.random());
		File file = new File(root + randomString + ".txt");

		// If file does not exists, then create it.
		if (!file.exists()) {
			file.createNewFile();
		}
		outputWriter = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));

		System.out.println("File: " + files[problem]);
		outputWriter.write("File: " + files[problem]);
		System.out.println(numberofTrials + " trials were run.");
		//outputWriter.write(numberofTrials + " trials were run.");
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
				// Only add and average execution time if the algorithm didn't time out.
				averageTime += totalTime;
				averagePercentSatisfiedClauses += results.get(i).percentSatisfied;
				averageBestGeneration += results.get(i).bestgeneration;
				averageUnsatisfiedClauses += temp;


				if (temp < fewestUnsatisfiedClauses){
					fewestUnsatisfiedClauses = temp;
				}
				if (results.get(i).executionTime < bestExecutionTime){
					bestExecutionTime = results.get(i).executionTime;;
				}
				if (results.get(i).bestgeneration < bestGeneration){
					bestGeneration = results.get(i).bestgeneration;;
				}
			} else {
				numTimeouts++;
				// Only add and average execution time if the algorithm did time out.


				averageBestGenerationT += results.get(i).bestgeneration;
				averageUnsatisfiedClausesT += temp;

				if (temp < fewestUnsatisfiedClausesT){
					fewestUnsatisfiedClausesT = temp;
				}

				if (results.get(i).bestgeneration < bestGenerationT){
					bestGenerationT = results.get(i).bestgeneration;;
				}
			}

		}

		if(numberofTrials - numTimeouts != 0){
			averageTime = averageTime / (long) (numberofTrials - numTimeouts);
			averageBestGeneration = averageBestGeneration / (numberofTrials - numTimeouts);
			averageUnsatisfiedClauses = averageUnsatisfiedClauses / (numberofTrials - numTimeouts);
			averagePercentSatisfiedClauses = averagePercentSatisfiedClauses / (double) (numberofTrials - numTimeouts);
			outputWriter.newLine();
		}else{
			averageTime = -1;
			averageBestGeneration = -1;
			averageUnsatisfiedClauses = -1;
			averagePercentSatisfiedClauses = -1;
			outputWriter.newLine();
		}

		if( numTimeouts != 0){
			averageBestGenerationT = averageBestGenerationT / numTimeouts;
			averageUnsatisfiedClausesT = averageUnsatisfiedClausesT / numTimeouts;
			averagePercentSatisfiedClausesT = averageUnsatisfiedClausesT / (double) maxValue;
			outputWriter.newLine();
		}else{
			averageBestGenerationT = -1;
			averageUnsatisfiedClausesT = -1;
			averagePercentSatisfiedClausesT = -1;
		}

		//		System.out.println("Average Output for " + numberofTrials + " trials:");
		//		outputWriter.write("Average Output for " + numberofTrials + " trials:");
		//		outputWriter.newLine();
		//		System.out.println("Number Of Variables: " + results.get(0).numVariables);
		//		outputWriter.write("Number Of Variables: " + results.get(0).numVariables);
		//		outputWriter.newLine();
		//		System.out.println("Number Of Clauses: " + results.get(0).numClauses);
		//		outputWriter.write("Number Of Clauses: " + results.get(0).numClauses);
		//		outputWriter.newLine();
		//		System.out.println("Average Percent satisfied: " + averagePercentSatisfiedClauses + "%");
		//		outputWriter.write("Average Percent satisfied: " + averagePercentSatisfiedClauses + "%");
		//		outputWriter.newLine();
		//		System.out.println("Average Best Generation:" + averageBestGeneration);
		//		outputWriter.write("Average Best Generation:" + averageBestGeneration);
		//		outputWriter.newLine();
		//		System.out.println("Average # Unsatisfied  Clauses:" + averageUnsatisfiedClauses);
		//		outputWriter.write("Average # Unsatisfied  Clauses:" + averageUnsatisfiedClauses);
		//		outputWriter.newLine();
		//		System.out.println("Fewest # Unsatisfied  Clauses:" + fewestUnsatisfiedClauses);
		//		outputWriter.write("Fewest # Unsatisfied  Clauses:" + fewestUnsatisfiedClauses);
		//		outputWriter.newLine();
		//		System.out.println("Average execution time: " + averageTime + " milliseconds");
		//		outputWriter.write("Average execution time: " + averageTime + " milliseconds");
		//		
		System.out.println("SAT PROBLEM");
		outputWriter.write("SAT PROBLEM");
		outputWriter.newLine();
		System.out.println("Average Output for " + numberofTrials + " trials:");
		outputWriter.write(""+numberofTrials);
		outputWriter.newLine();
		System.out.println("Number Of Variables: " + results.get(0).numVariables);
		outputWriter.write(""+results.get(0).numVariables);
		outputWriter.newLine();
		System.out.println("Number Of Clauses: " + results.get(0).numClauses);
		outputWriter.write(""+results.get(0).numClauses);
		outputWriter.newLine();

		System.out.println("Stats for trials that did not timeout");
		outputWriter.write("Stats for trials that did not timeout");
		outputWriter.newLine();


		System.out.println("Average Best Generation:" + averageBestGeneration);
		outputWriter.write(""+averageBestGeneration);
		outputWriter.newLine();
		System.out.println("Best Generation:" + bestGeneration);
		outputWriter.write(""+bestGeneration);
		outputWriter.newLine();
		System.out.println("Average # Unsatisfied  Clauses:" + averageUnsatisfiedClauses);
		outputWriter.write(""+averageUnsatisfiedClauses);
		outputWriter.newLine();
		System.out.println("Fewest # Unsatisfied  Clauses:" + fewestUnsatisfiedClauses);
		outputWriter.write(""+fewestUnsatisfiedClauses);
		outputWriter.newLine();
		System.out.println("Average execution time: " + averageTime + " milliseconds");
		outputWriter.write(""+averageTime);
		outputWriter.newLine();
		System.out.println("Best execution time: " + bestExecutionTime + " milliseconds");
		outputWriter.write(""+bestExecutionTime);
		outputWriter.newLine();



		System.out.println("Stats for timeouts");
		outputWriter.write("Stats for timeouts");
		outputWriter.newLine();

		System.out.println("Average Best Generation:" + averageBestGenerationT);
		outputWriter.write(""+averageBestGenerationT);
		outputWriter.newLine();
		System.out.println("Best Generation:" + bestGenerationT);
		outputWriter.write(""+bestGenerationT);
		outputWriter.newLine();
		System.out.println("Average # Unsatisfied  Clauses:" + averageUnsatisfiedClausesT);
		outputWriter.write(""+averageUnsatisfiedClausesT);
		outputWriter.newLine();
		System.out.println("Fewest # Unsatisfied  Clauses:" + fewestUnsatisfiedClausesT);
		outputWriter.write(""+fewestUnsatisfiedClausesT);
		outputWriter.newLine();
		System.out.println("Number of time outs: " + numTimeouts);
		outputWriter.write(""+numTimeouts);
		outputWriter.newLine();
		System.out.println("Average Percent satisfied: " + averagePercentSatisfiedClausesT + "%");
		outputWriter.write(""+averagePercentSatisfiedClausesT);
		outputWriter.newLine();




		if (algorithm.equalsIgnoreCase("g")) {
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
		}else{

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
		}


		outputWriter.flush();
		outputWriter.close();
	}




}
