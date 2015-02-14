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
		
		
		Genetic geneticAlgo = new Genetic(popSize, numberOfLiterals, maxIteration, crossOverProb, mutateProb,problem.getProblem());
		
		ArrayList<ArrayList<Integer>>  pop = geneticAlgo.population;
		geneticAlgo.evolve(pop,"rank");
		//geneticAlgo.singlePointCrossover(1, pop);

		
	}
}
