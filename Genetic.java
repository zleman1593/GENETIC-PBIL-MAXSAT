import java.util.ArrayList;
import java.util.*;


public class Genetic {
	private ArrayList<ArrayList<Integer>> population = new ArrayList<ArrayList<Integer>>();


	public Genetic(int popSize, int literalNumber) {
		population = initPopulation(popSize,literalNumber);
	}

	public ArrayList<ArrayList<Integer>> initPopulation(int popSize,int literalNumber){
		ArrayList<ArrayList<Integer>> population = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < popSize ;i++){
			ArrayList<Integer> individual = new ArrayList<Integer>();
			for (int j = 0; j < literalNumber; j++){
				Random rand = new Random();
				Integer value = rand.nextInt(2);
				individual.add(value);
			}

			population.add(individual);

		}
		return population;
	}
	
	public ArrayList<ArrayList<Integer>> getPopulation(){
		
		return this.population;
	}
}

