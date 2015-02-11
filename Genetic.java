import java.util.ArrayList;


public class Genetic {
	ArrayList<ArrayList<Integer>> population = new ArrayList<ArrayList<Integer>>();


	public Genetic(int popSize) {
		population = initPopulation(popSize);
		//cadence = startCadence;
		//speed = startSpeed;
	}

	public ArrayList<ArrayList<Integer>> initPopulation(int popSize){
		ArrayList<ArrayList<Integer>> population = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < popSize ;i++){
			ArrayList<Integer> individual = new ArrayList<Integer>();
			for (int j = 0; j < popSize; j++){
				Integer value = 1;
				individual.add(value);
			}

			population.add(individual);

		}
		return population;
	}
}

