import java.util.ArrayList;


public class controller {

	
	public static void main(String [ ] args) {
		SatProblem problem = new SatProblem();
		String infile = "/Users/zackleman/Desktop/brock200_1.clq.cnf";
		problem.createSatProblemFromFile(infile);
		int numberOfLiterals = problem.getNumLiterals();
		
		
		int popSize = 10;
		Genetic pop = new Genetic(popSize,numberOfLiterals);
		ArrayList<ArrayList<Integer>> current = pop.getPopulation();
		int test = problem.evaluateCandidate(current.get(0));
		System.out.println(test);
	}
}
