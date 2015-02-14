import java.util.ArrayList;


public class controller {

	
	public static void main(String [ ] args) {
		String infile = "/Users/zackleman/Desktop/t3pm3-5555.spn.cnf";
		int numberOfLiterals;
		int popSize = 200;
		int maxIteration = 1000;
		double crossOverProb = 0.7; 
	    double mutateProb = 0.01; 
		
		SatProblem problem = new SatProblem();
		problem.createSatProblemFromFile(infile);
		numberOfLiterals = problem.getNumLiterals();
		
		
		//PBIL test = new PBIL(10, 0.2, 0.2, numberOfLiterals,mutateProb, mutateProb, maxIteration,problem.getProblem()); 
		
		//test.initProbVector();
		//double[] prob = test.iteratePBIL();
		
		
		
		Genetic geneticAlgo = new Genetic(popSize, numberOfLiterals, maxIteration, crossOverProb, mutateProb,problem.getProblem());
		geneticAlgo.evolve("rank");

		
	}
}
