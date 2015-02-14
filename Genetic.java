import java.util.*;

public class Genetic extends GAAlgorithms {
	public ArrayList<ArrayList<Integer>> population = new ArrayList<ArrayList<Integer>>();
	private ArrayList<ArrayList<Integer>> satProblem = new ArrayList<ArrayList<Integer>>();
	private Random random; 
	private int maxIteration;
	private int maxFitnessSoFar = 0;
	public ArrayList<Integer> bestSolution; 
	private double crossOverProb; 
	private double mutateProb; 
	boolean foundSATSolution = false;


	public Genetic(int popSize, int literalNumber, int maxIteration, double crossOverProb, double mutateProb,ArrayList<ArrayList<Integer>> satProblem) {
		this.satProblem = satProblem;
		population = initPopulation(popSize,literalNumber);
		this.random = new Random();
		this.maxIteration = maxIteration;
		this.crossOverProb = crossOverProb;
		this.mutateProb = mutateProb;
	}

	public void updateMaxFitness(int fitness, ArrayList<Integer> values) {
		if(fitness > this.maxFitnessSoFar) {
			maxFitnessSoFar = fitness;
			bestSolution = new ArrayList<Integer>();
			bestSolution.addAll(values);
			if (fitness == satProblem.size()) {
				System.out.println("Fully Satisfied");
				foundSATSolution = true;
			}
		}
	}

	public void evolve(ArrayList<ArrayList<Integer>> popToEvolve, String selectionMethod){
		//ArrayList<Integer> count = new ArrayList<Integer>(Collections.nCopies(popToEvolve.size(), 0));//delete
		for (int i = 0; i < maxIteration && !foundSATSolution; i++){
			
		
			//rankSelectTest(popToEvolve, count);//delete
			
			select(popToEvolve,selectionMethod);
			if(foundSATSolution){
				System.out.println("Fully Satisfied Clauses");
				break;
			}
			singlePointCrossover(crossOverProb, popToEvolve);
			mutate(mutateProb, popToEvolve);
		}
		//System.out.println("test" + count);//delete
		//System.out.println("Sum" + count.stream().mapToInt(Integer::intValue).sum());//delete
		System.out.println( "Max Fitness so far:"+ maxFitnessSoFar);
		System.out.println("Best Solution" +bestSolution);
	}

	private void select(ArrayList<ArrayList<Integer>> popToEvolve, String selectionMethod){
		if (selectionMethod.equalsIgnoreCase("rank")){
			  rankSelect(popToEvolve);
		}else if(selectionMethod.equalsIgnoreCase("boltzman")){
			//  boltzmanSelect(popToEvolve);
		}else{
			  tournamentSelect(popToEvolve,1,5);//Todo make variables
		}
	}




	private void tournamentSelect(ArrayList<ArrayList<Integer>> popToEvolve,int winners, int sample) {

		ArrayList<ArrayList<Integer>> winnerPool = new ArrayList<ArrayList<Integer>>();

		while( winnerPool.size() < population.size()){
			//Get array of random numbers
			ArrayList<Integer> randomNumbers  = new ArrayList<Integer>();
			while(randomNumbers.size() < sample){
				int number;
				do
				{
					number = random.nextInt(popToEvolve.size());
				} while (randomNumbers.contains(number));
				randomNumbers.add(number);
			}
			ArrayList<ArrayWithFitness> withFitness = new ArrayList<ArrayWithFitness>();
			for (int i = 0; i < randomNumbers.size(); i++){
				// Pass in each individual and get back a fitness and merge with individual
				ArrayWithFitness memberWithFitness = new ArrayWithFitness(popToEvolve.get(i));
				memberWithFitness.fitness = evaluateCandidate(satProblem, popToEvolve.get(i));
				updateMaxFitness(memberWithFitness.fitness, popToEvolve.get(i));
				withFitness.add(memberWithFitness);
			}

			//Sort
			Collections.sort(withFitness);

			//Pick top x individuals and add to winnerPool until it is full
			for (int i = 0; i < winners && ( winnerPool.size() < population.size()) ; i++){
				winnerPool.add(withFitness.get(i).individual);	
			}

		}


		popToEvolve = winnerPool;//Replace current population with the breeding pool
	}


	private void rankSelect(ArrayList<ArrayList<Integer>> popToEvolve) {
		ArrayList<ArrayList<Integer>> winnerPool = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayWithFitness> withFitness = new ArrayList<ArrayWithFitness>();
		
		// Pass in each individual and get back a fitness and merge with individual
		for (int i = 0; i < popToEvolve.size(); i++){
			ArrayWithFitness memberWithFitness = new ArrayWithFitness(popToEvolve.get(i));
			memberWithFitness.fitness = evaluateCandidate(satProblem, popToEvolve.get(i));
			updateMaxFitness(memberWithFitness.fitness, popToEvolve.get(i));
			withFitness.add(memberWithFitness);
		}
		
		//Sort so  that position zero has individual with highest fitness
		Collections.sort(withFitness);


		//Generate one random double per member of the population
		double[] probability = new double[popToEvolve.size()];
		for (int i = 0; i < popToEvolve.size() ;i++){
			probability[i] = random.nextDouble();
		}
		
		//Sort ascending
		Arrays.sort(probability);

		int pickUpFrom = 0;
		double cumulativeProbabilityLag = 0;
		double cumulativeProbabilityLead = probFromRank((double) popToEvolve.size() ,(double) popToEvolve.size());
		
		for (int i = 0; i < popToEvolve.size();i++){
			for (int j = pickUpFrom; j < probability.length;j++){
				if ((probability[j] >= cumulativeProbabilityLag) && (probability[j] < cumulativeProbabilityLead) ){
					winnerPool.add(withFitness.get(i).individual);
				}else{
					pickUpFrom = j;
					break;
				}
			}

			cumulativeProbabilityLag = cumulativeProbabilityLead;
			cumulativeProbabilityLead += probFromRank((double) (popToEvolve.size() - (i+1)),(double) popToEvolve.size());
		}

		popToEvolve = winnerPool;//Replace current population with the breeding pool

	}

	private double probFromRank (double rank,double popsize){
		return  rank / ((popsize*(popsize+1)/2));
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
				List<Integer> c = new ArrayList<Integer>(popToCross.get(i).subList(0, crossOverLocation));
				//Replace first half of A with First half of B
				for (int j = 0; j < crossOverLocation ;j++){ 
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
}



/*
	private boolean rankSelectTest(ArrayList<ArrayList<Integer>> popToEvolve, ArrayList<Integer> count ) {
		ArrayList<ArrayWithFitness> withFitness = new ArrayList<ArrayWithFitness>();
		
		// Pass in each individual and get back a fitness and merge with individual
		for (int i = 0; i < popToEvolve.size(); i++){
			ArrayWithFitness memberWithFitness = new ArrayWithFitness(popToEvolve.get(i));
			memberWithFitness.fitness = evaluateCandidate(popToEvolve.get(i));
			withFitness.add(memberWithFitness);
		}
		
		//Sort so  that position zero has individual with highest fitness
		Collections.sort(withFitness);


		//Generate one random double per member of the population
		double[] probability = new double[popToEvolve.size()];
		for (int i = 0; i < popToEvolve.size() ;i++){
			probability[i] = random.nextDouble();
		}
		
		//Sort ascending
		Arrays.sort(probability);

		int pickUpFrom = 0;
		double cumulativeProbabilityLag = 0;
		double cumulativeProbabilityLead = probFromRank((double) popToEvolve.size() ,(double) popToEvolve.size());
		
		for (int i = 0; i < popToEvolve.size();i++){
			for (int j = pickUpFrom; j < probability.length;j++){
				if ((probability[j] >= cumulativeProbabilityLag) && (probability[j] < cumulativeProbabilityLead) ){
					//winnerPool.add(withFitness.get(i).individual);
					count.set( i,count.get(i) + 1);
				} else{
					pickUpFrom = j;
					break;
				}
			}

			cumulativeProbabilityLag = cumulativeProbabilityLead;
			cumulativeProbabilityLead += probFromRank((double) (popToEvolve.size() - (i+1)),(double) popToEvolve.size());
			//System.out.println(probFromRank((double) (popToEvolve.size() - (i+1)),(double) popToEvolve.size()));
			if (cumulativeProbabilityLag > 1){
				System.out.println("This is bad");
			}
		}

		return false;//Return whether all clauses have been satisfied by any candidate
	}
 * */
