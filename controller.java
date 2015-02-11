import java.util.ArrayList;


public class controller {

	
	public static void main(String [ ] args) {
		SatProblem problem = new SatProblem();
		String infile = "/Users/zackleman/Desktop/brock200_1.clq.cnf";
		problem.readSatFile(infile);
		//problem.getProblem();
		//problem.evaluateCandidate();
		int popSize = 10;
		int literalNumber = 15 ;
		Genetic pop = new Genetic(popSize,literalNumber);
		ArrayList<ArrayList<Integer>> current = pop.getPopulation();
		 popSize = 11;
		
	}
}
