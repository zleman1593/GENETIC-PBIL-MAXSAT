import java.util.ArrayList;

public class Controller {
//Todo include ts smaple size and winner size

	public static void main(String[] args) {
		// Path to MXSAT input file
		String infile = args[0];
		// Solution technique to use
		String solutionTechnique = args[7];

		// Import and format MAXSAT problem.
		SatProblem problem = new SatProblem();
		problem.createSatProblemFromFile(infile);
		int numberOfLiterals = problem.getNumLiterals();
		ArrayList<ArrayList<Integer>> satProblem = problem.getProblem();

		if (solutionTechnique.equalsIgnoreCase("g")) {
			
			int popSize = Integer.parseInt(args[1]);
			String selectionType = args[2];
			String crossoverType = args[3];
			Double crossoverProb = Double.parseDouble(args[4]);
			Double mutationProb = Double.parseDouble(args[5]);
			int maxIterations = Integer.parseInt(args[6]);

			// Run Genetic Algorithms.
			Genetic geneticAlgo = new Genetic(popSize, numberOfLiterals, maxIterations, crossoverType, crossoverProb,
					mutationProb, satProblem);
			geneticAlgo.evolve(selectionType);

		} else {

			// PBIL parameters
//			int PBIL_samples = Integer.parseInt(args[1]);
//			double PBIL_learningRate = Double.parseDouble(args[2]);
//			double PBIL_negLearningRate = Double.parseDouble(args[3]);
//			double PBIL_mutProb = Double.parseDouble(args[4]);
//			double PBIL_mutShift =Double.parseDouble(args[5]);
//			int PBIL_maxIterations = Integer.parseInt(args[6]);
			
			
			int PBIL_samples = 100;
			double PBIL_learningRate = 0.1;
			double PBIL_negLearningRate = .075;
			double PBIL_mutProb = 0.02;
			double PBIL_mutShift = 0.05;
			int PBIL_maxIterations = 2000;

			// Run PBIL.
			PBIL test = new PBIL(PBIL_samples, PBIL_learningRate, PBIL_negLearningRate, numberOfLiterals, PBIL_mutProb,
					PBIL_mutShift, PBIL_maxIterations, satProblem);
			test.initProbVector();
			double[] prob = test.iteratePBIL();
		}

	}
}
