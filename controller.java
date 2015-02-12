import java.util.ArrayList;


public class controller {

	
	public static void main(String [ ] args) {
		String infile = "/Users/zackleman/Desktop/brock200_1.clq.cnf";
		int numberOfLiterals = 10;
		int popSize = 10;
		int maxIteration = 10;
		double crossOverProb = 0.5; 
	    double mutateProb = 0.5; 
		
		SatProblem problem = new SatProblem();
		problem.createSatProblemFromFile(infile);
		numberOfLiterals = problem.getNumLiterals();
		
		
		Genetic geneticAlgo = new Genetic(popSize, numberOfLiterals, maxIteration, crossOverProb, mutateProb,problem.getProblem());
		
		ArrayList<ArrayList<Integer>>  pop = geneticAlgo.population;
		geneticAlgo.evolve(pop,"tournament");
		//geneticAlgo.singlePointCrossover(1, pop);
//int test = 0;
		
	}
}
