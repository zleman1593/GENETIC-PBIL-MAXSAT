import java.util.ArrayList;


public class controller {

	
	public static void main(String [ ] args) {
		SatProblem problem = new SatProblem();
		String infile = "/Users/zackleman/Desktop/brock200_1.clq.cnf";
		problem.readSatFile(infile);
		int numberOfLiterals = problem.getNumLiterals();
		
		//problem.evaluateCandidate();
		int popSize = 10;
		Genetic pop = new Genetic(popSize,numberOfLiterals);
		ArrayList<ArrayList<Integer>> current = pop.getPopulation();
		
	}
}
