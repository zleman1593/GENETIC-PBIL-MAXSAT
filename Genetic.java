import java.util.*;


public class Genetic {
	public ArrayList<ArrayList<Integer>> population = new ArrayList<ArrayList<Integer>>();
	private ArrayList<ArrayList<Integer>> satProblem = new ArrayList<ArrayList<Integer>>();
	private Random random; 
	private int maxIteration;
	private int maxFitnessSoFar = 0;
	public ArrayList<Integer> bestSolution; 
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


	public void evolve(ArrayList<ArrayList<Integer>> popToEvolve, String selectionMethod){
		boolean foundSATSolution = false;
		for (int i = 0; i < maxIteration && !foundSATSolution; i++){
			foundSATSolution = select(popToEvolve,selectionMethod);
			if(foundSATSolution){
				System.out.println("Fully Satisfied Clauses");
				break;
			}
			singlePointCrossover(crossOverProb, popToEvolve);
			mutate(mutateProb, popToEvolve);
		}
		System.out.println( "Max Fitness so far:"+ maxFitnessSoFar);
		System.out.println("Best Solution" +bestSolution);
	}

	private boolean select(ArrayList<ArrayList<Integer>> popToEvolve, String selectionMethod){
		boolean foundSATSolution = false;
		if (selectionMethod.equalsIgnoreCase("rank")){
			foundSATSolution = rankSelect(popToEvolve);
		}else if(selectionMethod.equalsIgnoreCase("boltzman")){
			//foundSATSolution = boltzmanSelect(popToEvolve);
		}else{
			foundSATSolution = tournamentSelect(popToEvolve,1,5);//Todo make variables
		}
		return foundSATSolution;
	}




	private boolean tournamentSelect(ArrayList<ArrayList<Integer>> popToEvolve,int winners, int sample) {
		boolean foundSATSolution = false;
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
				memberWithFitness.fitness = evaluateCandidate(popToEvolve.get(i));
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
		return foundSATSolution;//Return whether all clauses have been satisfied by any candidate
	}


//todo make sure off by 1 errors are removed. Diff between array position and rank
	private boolean rankSelect(ArrayList<ArrayList<Integer>> popToEvolve) {
		ArrayList<ArrayList<Integer>> winnerPool = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayWithFitness> withFitness = new ArrayList<ArrayWithFitness>();
		// Pass in each individual and get back a fitness and merge with individual
		for (int i = 0; i < popToEvolve.size(); i++){
			ArrayWithFitness memberWithFitness = new ArrayWithFitness(popToEvolve.get(i));
			memberWithFitness.fitness = evaluateCandidate(popToEvolve.get(i));
			withFitness.add(memberWithFitness);
		}
		//Sort
		Collections.sort(withFitness);
		
	
		
		double[] probability = new double[popToEvolve.size()];
		for (int i = 0; i < popToEvolve.size() ;i++){
			probability[i] = random.nextDouble();
		}
		Arrays.sort(probability);
		
		
		double cumulativeProbabilityLag = 0;
		double cumulativeProbabilityLead = probFromRank((double) popToEvolve.size() ,(double) popToEvolve.size());
		for (int i = 0; i < popToEvolve.size();i++){
			for (int j = 0; j < probability.length;j++){
			if ((probability[j] >= cumulativeProbabilityLag) && (probability[j] < cumulativeProbabilityLead) ){
				winnerPool.add(withFitness.get(popToEvolve.size() - (i + 1)).individual);
			}
			}
			
			
			cumulativeProbabilityLag = cumulativeProbabilityLead;
			cumulativeProbabilityLead += probFromRank((double) (popToEvolve.size() - (i+1)),(double) popToEvolve.size());
		}

		popToEvolve = winnerPool;//Replace current population with the breeding pool
		return false;//Return whether all clauses have been satisfied by any candidate
	}
	
	
private double probFromRank (double rank,double popsize){
	return rank / ((rank*(rank+1)/2));
}

private double rankFromFitness (double rank,double popsize){
	return rank / ((rank*(rank+1)/2));
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
				if(((literalTruth < 0) && values.get(Math.abs(literalTruth) -1 ) == 0)  || ((literalTruth > 0) && values.get(Math.abs(literalTruth) -1 ) == 1) ){
					//Count clause as satisfied
					fitness++;
					break;
				} 
			}
		}

		if(fitness > this.maxFitnessSoFar){
			maxFitnessSoFar = fitness;

			bestSolution = (ArrayList<Integer>)values.clone();;
			//System.out.println(fitness);
			//System.out.println(values);
			//System.out.println( satProblem.size()-1);
			if(fitness == satProblem.size()){
				System.out.println("Fully Satisfied");
			}
		}
		return fitness;
	}
}

