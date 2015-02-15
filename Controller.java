import java.util.ArrayList;

public class Controller {

	private static final String PATH_ZACK = "/Users/zackleman/Desktop/t3pm3-5555.spn.cnf";
	private static final String PATH_IVY = "/Users/mxing/Desktop/t3pm3-5555.spn.cnf";

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

			// PBIL parameters.
			int PBIL_samples = 100;
			double PBIL_learningRate = 0.1;
			double PBIL_negLearningRate = .075;
			int PBIL_length = numberOfLiterals;
			double PBIL_mutProb = 0.02;
			double PBIL_mutShift = 0.05;
			int PBIL_maxIterations = 2000;

			// Run PBIL.
			PBIL test = new PBIL(PBIL_samples, PBIL_learningRate, PBIL_negLearningRate, PBIL_length, PBIL_mutProb,
					PBIL_mutShift, PBIL_maxIterations, satProblem);

			test.initProbVector();
			double[] prob = test.iteratePBIL();
		}

	}
}
