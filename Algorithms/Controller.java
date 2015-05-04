package Algorithms;
import java.util.ArrayList;

public class Controller {

	public static void main(String[] args) {
	

		// Path to MXSAT input file
		String infile = "/Users/zackleman/Documents/Current Projects/GENETIC-PBIL-MAXSAT/Problems/140v/s2v140c1200-10.cnf";//args[0];
		// Solution technique to use
		String solutionTechnique = "sa";//args[7];

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

		} else if (solutionTechnique.equalsIgnoreCase("p")){

			// PBIL parameters
			int PBIL_samples = Integer.parseInt(args[1]);
			double PBIL_learningRate = Double.parseDouble(args[2]);
			double PBIL_negLearningRate = Double.parseDouble(args[3]);
			double PBIL_mutProb = Double.parseDouble(args[4]);
			double PBIL_mutShift = Double.parseDouble(args[5]);
			int PBIL_maxIterations = Integer.parseInt(args[6]);

			// Run PBIL.
			PBIL PBILAlgorithm = new PBIL(PBIL_samples, PBIL_learningRate, PBIL_negLearningRate, numberOfLiterals,
					PBIL_mutProb, PBIL_mutShift, PBIL_maxIterations, satProblem);
			PBILAlgorithm.evolve();
		} else if (solutionTechnique.equalsIgnoreCase("sa")){
			
			double minTemp = 0.0001;
			double maxTemp = 0.5;
			
			 SimulatedAnnealing anneal = new SimulatedAnnealing(numberOfLiterals,satProblem,2,minTemp,maxTemp);
			 anneal.anneal();
			
			
		
		
			
		}

	}


}
