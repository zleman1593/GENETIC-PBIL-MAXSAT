import java.util.ArrayList;


public class controller {

	
	public static void main(String [ ] args) {
		int popSize = 10;
		int literalNumber = 15 ;
		Genetic pop = new Genetic(popSize,literalNumber);
		ArrayList<ArrayList<Integer>> current = pop.getPopulation();
		 popSize = 11;
		
	}
}
