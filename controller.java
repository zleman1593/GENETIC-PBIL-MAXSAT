import java.util.ArrayList;


public class controller {

	
	public static void main(String [ ] args) {
		String infile = "/Users/zackleman/Desktop/brock200_1.clq.cnf";
		int numberOfLiterals = 0;
		int popSize = 10;
		
		SatProblem problem = new SatProblem();
		problem.createSatProblemFromFile(infile);
		numberOfLiterals = problem.getNumLiterals();
		
		
		Genetic geneticAlgo = new Genetic(popSize,numberOfLiterals,problem.getProblem());
		//ArrayList<ArrayList<Integer>>  pop = geneticAlgo.population;
		//geneticAlgo.mutate(0.5, pop);

		
	}
}
