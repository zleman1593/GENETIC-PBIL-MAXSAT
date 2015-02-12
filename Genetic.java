import java.util.*;


public class Genetic {
	public ArrayList<ArrayList<Integer>> population = new ArrayList<ArrayList<Integer>>();
	private ArrayList<ArrayList<Integer>> satProblem = new ArrayList<ArrayList<Integer>>();
	private Random random; 
	private int maxIteration;
	private double crossOverProb; 
	private double mutateProb; 


	public Genetic(int popSize, int literalNumber, int maxIteration, double crossOverProb, double mutateProb,ArrayList<ArrayList<Integer>> satProblem) {
		this.satProblem = satProblem;
		population = initPopulation(popSize,literalNumber);
		this.random = new Random();
		this.maxIteration = maxIteration;
		this.crossOverProb = crossOverProb;
		this.mutateProb = mutateProb;
	}

	
	public void evolve(ArrayList<ArrayList<Integer>> popToEvolve){
		boolean foundSATSolution = false;
		for (int i = 0; i < maxIteration && !foundSATSolution; i++){
			
			singlePointCrossover(crossOverProb, popToEvolve);
			mutate(mutateProb, popToEvolve);
			
		
			
		}
	}
	
	
	private void mutate(double mutateProb,ArrayList<ArrayList<Integer>> popToMutate ) {
		for (int i = 0; i < popToMutate.size() ;i++){
			for (int j = 0; j < popToMutate.get(i).size() ;j++){
				boolean flip = random.nextDouble() < mutateProb;
				if (flip &&  popToMutate.get(i).get(j) == 1){
					popToMutate.get(i).set(j,0);
				}else if(flip && popToMutate.get(i).get(j) == 0){
					popToMutate.get(i).set(j,1);
				}
			}

		}
	}

	private void singlePointCrossover(double crossProb,ArrayList<ArrayList<Integer>> popToCross ) {
		for (int i = 0; i < popToCross.size() ;i+= 2){
			boolean cross = random.nextDouble() < crossProb;
			int crossOverLocation;
			if (cross){
				//Pick cross over location
				crossOverLocation = random.nextInt(popToCross.get(i).size());
				//Copy first half of A into C
				List<Integer> c =  popToCross.get(i).subList(0, crossOverLocation); 
				//Replace first half of A with First half of B
				for (int j = 0; j < popToCross.get(i).size() ;j++){
					int value = popToCross.get(i+1).get(j);
					popToCross.get(i).set(j, value);
				}
				//Replace first half of B with C
				for (int j = 0; j < c.size() ;j++){
					int value = c.get(j);
					popToCross.get(i+1).set(j, value);
				}

			}

		}
	}

	/*
	public ArrayList<ArrayList<Integer>> tournament(int winners, int sample) {
		ArrayList<ArrayList<Integer>> winnerPool = new ArrayList<ArrayList<Integer>>();
		while( winnerPool.size() < population.size()){

			ArrayList<Integer> individual = new ArrayList<Integer>();
			winnerPool.add(individual);
		}
		Random random = new Random;
		int number;
		do
		{
			number = random.Next();
		} while (randomNumbers.Contains(number));

		return winnerPool;
	}*/
	
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


	/*Fitness Function*/
	public int  evaluateCandidate(ArrayList<Integer> values){
		int fitness = 0;
		//Look at every clause
		for (int i = 0; i < satProblem.size();i++){
			//Look at every literal
			for (int j = 0; j <  satProblem.get(i).size();j++){
				int literalTruth = satProblem.get(i).get(j);
				if(((literalTruth < 0) && values.get(Math.abs(literalTruth) -1 ) == 0)  || ((literalTruth >= 0) && values.get(Math.abs(literalTruth) -1 ) == 1) ){
					//Count clause as satisfied
					fitness++;
					break;
				} 
			}
		}
		return fitness;
	}
}

