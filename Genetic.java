import java.util.*;

public class Genetic extends EvolAlgorithms {

	//Population that is evolved
	public ArrayList<ArrayList<Integer>> population = new ArrayList<ArrayList<Integer>>();
	//The MAXSAT problem
	private ArrayList<ArrayList<Integer>> satProblem = new ArrayList<ArrayList<Integer>>();
	//Random Number generator
	private Random random; 
	//Maximum iterations allowed
	private int maxIteration;
	//Maximum fitness found so far in any generation
	private int maxFitnessSoFar = 0;
	//Individual Solution with Maximum fitness found so far in any generation
	public ArrayList<Integer> bestSolution; 
	//Probabilities
	private double crossOverProb; 
	private double mutateProb; 
	//Keeps track of whether a full solution has been found
	boolean foundSATSolution = false;

	// Constructor. 
	public Genetic(int popSize, int literalNumber, int maxIteration, double crossOverProb, 
			double mutateProb, ArrayList<ArrayList<Integer>> satProblem) {
		this.satProblem = satProblem;
		this.population = initPopulation(popSize,literalNumber);
		this.random = new Random();
		this.maxIteration = maxIteration;
		this.crossOverProb = crossOverProb;
		this.mutateProb = mutateProb;
	}
	// Update the max fitness encountered so far
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

	public void evolve(String selectionMethod) {
		for (int i = 0; i < maxIteration && !foundSATSolution; i++){
			
			if (selectionMethod.equalsIgnoreCase("rank")){
				  rankSelect();
			}else if(selectionMethod.equalsIgnoreCase("boltzman")){
				//  boltzmanSelect(popToEvolve);
			}else{
				  tournamentSelect(1,5);//Todo make variables
			}
			
			if(foundSATSolution){
				System.out.println("Fully Satisfied Clauses");
				break;
			}
			singlePointCrossover(crossOverProb);
			mutate(mutateProb);
		}
		System.out.println( "Max Fitness so far:"+ maxFitnessSoFar);
		System.out.println("Best Solution" +bestSolution);
	}





	private void tournamentSelect(int winners, int sample) {
		ArrayList<ArrayList<Integer>> winnerPool = new ArrayList<ArrayList<Integer>>();
		while( winnerPool.size() < population.size()){
			//Get array of random numbers
			ArrayList<Integer> randomNumbers  = new ArrayList<Integer>();
			while(randomNumbers.size() < sample){
				int number;
				do
				{
					number = random.nextInt(this.population.size());
				} while (randomNumbers.contains(number));
				randomNumbers.add(number);
			}
			ArrayList<ArrayWithFitness> withFitness = new ArrayList<ArrayWithFitness>();
			for (int i = 0; i < randomNumbers.size(); i++){
				// Pass in each individual and get back a fitness and merge with individual
				ArrayWithFitness memberWithFitness = new ArrayWithFitness(this.population.get(i));
				memberWithFitness.fitness = evaluateCandidate(satProblem, this.population.get(i));
				updateMaxFitness(memberWithFitness.fitness, this.population.get(i));
				withFitness.add(memberWithFitness);
			}

			//Sort
			Collections.sort(withFitness);

			//Pick top x individuals and add to winnerPool until it is full
			for (int i = 0; i < winners && ( winnerPool.size() < population.size()) ; i++){
				winnerPool.add((ArrayList<Integer>)withFitness.get(i).individual.clone());	
			}

		}
		this.population = winnerPool;//Replace current population with the breeding pool
	}


	private void rankSelect() {
		ArrayList<ArrayList<Integer>> winnerPool = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayWithFitness> withFitness = new ArrayList<ArrayWithFitness>();
		
		// Pass in each individual and get back a fitness and merge with individual
		for (int i = 0; i < this.population.size(); i++){
			ArrayWithFitness memberWithFitness = new ArrayWithFitness(this.population.get(i));
			memberWithFitness.fitness = evaluateCandidate(satProblem, this.population.get(i));
			updateMaxFitness(memberWithFitness.fitness, this.population.get(i));
			withFitness.add(memberWithFitness);
		}
		
		//Sort so  that position zero has individual with highest fitness
		Collections.sort(withFitness);


		//Generate one random double per member of the population
		double[] probability = new double[this.population.size()];
		for (int i = 0; i < this.population.size() ;i++){
			probability[i] = random.nextDouble();
		}
		
		//Sort ascending
		Arrays.sort(probability);

		int pickUpFrom = 0;
		double cumulativeProbabilityLag = 0;
		double cumulativeProbabilityLead = probFromRank((double) this.population.size() ,(double) this.population.size());
		
		for (int i = 0; i < this.population.size();i++){
			for (int j = pickUpFrom; j < probability.length;j++){
				if ((probability[j] >= cumulativeProbabilityLag) && (probability[j] < cumulativeProbabilityLead) ){
					winnerPool.add((ArrayList<Integer>)withFitness.get(i).individual.clone());
				}else{
					pickUpFrom = j;
					break;
				}
			}

			cumulativeProbabilityLag = cumulativeProbabilityLead;
			cumulativeProbabilityLead += probFromRank((double) (this.population.size() - (i+1)),(double) this.population.size());
		}
		this.population = winnerPool;//Replace current population with the breeding pool
	}

	private double probFromRank (double rank,double popsize){
		return  rank / ((popsize*(popsize+1)/2));
	}



	private void mutate(double mutateProb) {
		for (int i = 0; i < this.population.size() ;i++){
			for (int j = 0; j < this.population.get(i).size() ;j++){
				boolean flip = random.nextDouble() < mutateProb;
				if (flip &&  this.population.get(i).get(j) == 1){
					this.population.get(i).set(j,0);
				}else if(flip && this.population.get(i).get(j) == 0){
					this.population.get(i).set(j,1);
				}
			}

		}
	}

	private void singlePointCrossover(double crossProb) {
		for (int i = 0; i < this.population.size() ;i+= 2){
			boolean cross = random.nextDouble() < crossProb;
			int crossOverLocation;
			if (cross){
				//Pick cross over location
				crossOverLocation = random.nextInt(this.population.get(i).size());
				//Copy first part of A into C
				List<Integer> c = new ArrayList<Integer>(this.population.get(i).subList(0, crossOverLocation));
				//Replace first part of A with First part of B
				for (int j = 0; j < crossOverLocation ;j++){ 
					int value = this.population.get(i+1).get(j);
					this.population.get(i).set(j, value);
				}
				//Replace first part of B with C
				for (int j = 0; j < c.size() ;j++){
					int value = c.get(j);
					this.population.get(i+1).set(j, value);
				}

			}

		}
	}
	// Initialize population.
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

