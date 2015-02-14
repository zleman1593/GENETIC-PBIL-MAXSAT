import java.util.ArrayList;


public class Controller {

	private static final String PATH_ZACK = "/Users/zackleman/Desktop/t3pm3-5555.spn.cnf"; 
	private static final String PATH_IVY = "/Users/mxing/Desktop/t3pm3-5555.spn.cnf";
	
	public static void main(String [ ] args) {
		String infile = PATH_ZACK;
//		String infile = PATH_IVY;
		
		// Import and format MAXSAT problem.
		SatProblem problem = new SatProblem();
		problem.createSatProblemFromFile(infile);
		int numberOfLiterals = problem.getNumLiterals();
		ArrayList<ArrayList<Integer>> satProblem = problem.getProblem();
		
		// GA parameters.
		int GA_popSize = 200;
		int GA_literalNumber = numberOfLiterals;
		int GA_maxIteration = 1000;
		double GA_crossOverProb = 0.7; 
	    double GA_mutateProb = 0.01; 
		
	    // PBIL parameters.
	    int PBIL_samples= 100;
	    double PBIL_learningRate = 0.1; 
	    double PBIL_negLearningRate = .075; 
	    int PBIL_length = numberOfLiterals;
	    double PBIL_mutProb = 0.02;
		double PBIL_mutShift = 0.05; 
		int PBIL_maxIterations = 1000;
		
	    // Run PBIL.
		PBIL test = new PBIL(PBIL_samples, PBIL_learningRate, PBIL_negLearningRate, 
				PBIL_length, PBIL_mutProb, PBIL_mutShift, PBIL_maxIterations, satProblem); 
		
		test.initProbVector();
		double[] prob = test.iteratePBIL();
		
		// Run Genetic Algorithms.
		Genetic geneticAlgo = new Genetic(GA_popSize, GA_literalNumber, GA_maxIteration, 
				GA_crossOverProb, GA_mutateProb, satProblem);
		
		geneticAlgo.evolve("rank");
	
	}
}
