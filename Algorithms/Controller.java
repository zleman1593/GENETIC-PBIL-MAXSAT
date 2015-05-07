package Algorithms;
import java.util.ArrayList;

public class Controller {

	public static void main(String[] args) {


		// Path to MXSAT input file
		String infile = "/Users/zackleman/Downloads/assign1-ga-pbil-for-maxsat/maxsat-problems/maxsat-crafted/bipartite/maxcut-140-630-0.8/maxcut-140-630-0.8-7.cnf";//args[0];
		//String infile = "/Users/zackleman/Documents/Current Projects/GENETIC-PBIL-MAXSAT/Problems/140v/s2v140c1200-10.cnf";
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




			int popSize = 100;
			String selectionType ="bs";
			String crossoverType ="1c";
			double crossoverProb = 0.7;
			Double mutationProb = 0.01;
			int maxIterations = 9000;
			
			// Run Genetic Algorithms.
			//		Genetic geneticAlgo = new Genetic(popSize, numberOfLiterals, maxIterations, crossoverType, crossoverProb,mutationProb, satProblem,10);	
			//		geneticAlgo.evolve(selectionType);
			//		
				
			
			
					HybridSAGA hybrid = new HybridSAGA(numberOfLiterals, 5, "1c", 0.7, 0.3,satProblem, 2);
					hybrid.solve();


		
			
			// SAPBIL parameters
			int PBIL_samples = 30;
			double PBIL_learningRate = 0.2;
			double PBIL_negLearningRate = 0.075;
			double PBIL_mutProb = 0.02;
			double PBIL_mutShift = 0.05;
			int PBIL_maxIterations = 1000;

//			SAPBIL SAPBILAlgorithm = new SAPBIL(PBIL_samples, PBIL_learningRate, PBIL_negLearningRate, numberOfLiterals,
//						PBIL_mutProb, PBIL_mutShift, PBIL_maxIterations, satProblem,166);
//				
//				SAPBILAlgorithm.evolve();
			
//			PBIL PBILAlgorithm = new PBIL(PBIL_samples, PBIL_learningRate, PBIL_negLearningRate, numberOfLiterals,
//				PBIL_mutProb, PBIL_mutShift, PBIL_maxIterations, satProblem,166);
//			
//			PBILAlgorithm.evolve();

			
//			double alpha = 1;
//			Hybrid2 hybrid2 = new Hybrid2(popSize, numberOfLiterals,10000, crossoverType, crossoverProb,satProblem, 1, maxTemp, minTemp,alpha);
//			hybrid2.evolve(selectionType);
			

//						 SimulatedAnnealing anneal = new SimulatedAnnealing(numberOfLiterals,satProblem,2,minTemp,maxTemp);
//						 anneal.anneal();





		}

	}


}
