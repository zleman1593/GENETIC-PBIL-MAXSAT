package Algorithms;

import java.util.*;


public class GeneticSuper extends EvolAlgorithms {
	// Population that is evolved
	protected ArrayList<ArrayList<Integer>> population;
	protected double boltzmannSum;
	// Maximum fitness found so far in any generation
	protected int maxFitnessSoFar = 0;
	// Individual Solution with Maximum fitness found so far in any generation
	protected ArrayList<Integer> bestSolution;
	// Tournament variables
	protected int winners, sample;
	// Probabilities
	protected double crossOverProb, mutateProb;
	// Keeps track of whether a full solution has been found
	protected boolean foundSATSolution = false;
	// Initialize population
	// Maximum iterations allowed
	protected int maxIteration;
	// Which method to use for Crossover
	protected String crossOverMethod;
	protected long endTime;
	protected int optimalUnSat = Integer.MAX_VALUE;
	protected ArrayList<ArrayList<Integer>> initPopulation(int popSize, int literalNumber) {
		ArrayList<ArrayList<Integer>> population = new ArrayList<ArrayList<Integer>>();
		population.clear();
		for (int i = 0; i < popSize; i++) {
			ArrayList<Integer> individual = new ArrayList<Integer>();
			for (int j = 0; j < literalNumber; j++) {
				Random rand = new Random();
				Integer value = rand.nextInt(2);
				individual.add(value);
			}
			population.add(individual);
		}
		return population;
	}


	/* Get probability of selecting individual based on its rank */
	protected double probFromRank(double rank, double popsize) {
		return rank / ((popsize * (popsize + 1) / 2));
	}

	protected void tournamentSelect() {
		ArrayList<ArrayList<Integer>> winnerPool = new ArrayList<ArrayList<Integer>>();
		// While the next generation pool is smaller than the population limit
		while (winnerPool.size() < population.size()) {

			// Get array of unique random numbers (AKA. randomly choose "sample"
			// number of individuals)
			ArrayList<Integer> randomNumbers = new ArrayList<Integer>();
			while (randomNumbers.size() < sample) {
				int number;
				do {
					number = randomGenerator.nextInt(population.size());
				} while (randomNumbers.contains(number));
				randomNumbers.add(number);
			}

			/*
			 * Evaluate each individual in the random sample, and add it with
			 * its fitness to a new object
			 */
			ArrayList<ArrayWithFitness> allIndividualsWithFitness = new ArrayList<ArrayWithFitness>();
			for (int i = 0; i < randomNumbers.size(); i++) {
				ArrayList<Integer> individual = population.get(randomNumbers.get(i));
				ArrayWithFitness memberWithFitness = new ArrayWithFitness(individual);
				memberWithFitness.fitness = evaluateCandidate(individual);
				allIndividualsWithFitness.add(memberWithFitness);
				// Update the global variable if a new individual is more fit
				// than the current best
				updateMaxFitness(memberWithFitness.fitness, individual);
			}

			// Sort the individuals by their fitness
			Collections.sort(allIndividualsWithFitness);

			// Pick top "winners" individuals and add to winnerPool until it is
			// full
			for (int i = 0; i < winners && (winnerPool.size() < population.size()); i++) {
				winnerPool.add((ArrayList<Integer>) allIndividualsWithFitness.get(i).individual.clone());
			}

		}
		population = winnerPool;// Replace current population with the
		// newly selected pool
	}

	protected ArrayList<ArrayWithFitness> getFitnessForAllIndividuals() {
		ArrayList<ArrayWithFitness> allIndividualsWithFitness = new ArrayList<ArrayWithFitness>();
		// Pass in each individual and get back a fitness and merge with
		// individual
		for (int i = 0; i < population.size(); i++) {
			ArrayList<Integer> individual = population.get(i);
			ArrayWithFitness memberWithFitness = new ArrayWithFitness(individual);
			memberWithFitness.fitness = evaluateCandidate(individual);
			allIndividualsWithFitness.add(memberWithFitness);

			updateMaxFitness(memberWithFitness.fitness, individual);
		}

		// Sort by fitness so that position zero has individual with highest
		// fitness
		Collections.sort(allIndividualsWithFitness);

		return allIndividualsWithFitness;

	}

	/* Method that runs Rank and Boltzmann selection */
	protected void rankBoltzSelect(String option) {
		ArrayList<ArrayList<Integer>> winnerPool = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayWithFitness> allIndividualsWithFitness = getFitnessForAllIndividuals();
 
		// Generate one random double per member of the population
		double[] probability = new double[population.size()];
		for (int i = 0; i < population.size(); i++) {
			probability[i] = randomGenerator.nextDouble();
		}

		// Sort ascending
		Arrays.sort(probability);

		int pickUpFrom = 0;// Prevents rerunning of elements in the probability
		// vector
		int indexOfIndividual = 1;// Used for Boltzmann

		double cumulativeProbabilityLag = 0;
		double cumulativeProbabilityLead;
		// Initialize two range indices that bracket probability ranges
		if (option.equalsIgnoreCase("rs")) {
			cumulativeProbabilityLead = probFromRank((double) population.size(), (double) population.size());
		} else {
			calcBoltzmannSum(allIndividualsWithFitness); // calculate the
			// denominator of
			// the
			// Boltzmann function once per generation
			cumulativeProbabilityLead = probFromBoltz(0, allIndividualsWithFitness);
		}

		for (int i = 0; i < population.size(); i++) {

			for (int j = pickUpFrom; j < probability.length; j++) {
				// If random value/selection is within the range indicated by
				// the two indices then
				// add the individual associated with that range to the winner
				// pool
				if ((probability[j] >= cumulativeProbabilityLag) && (probability[j] < cumulativeProbabilityLead)) {
					winnerPool.add((ArrayList<Integer>) allIndividualsWithFitness.get(i).individual.clone());
				} else {
					pickUpFrom = j;// pick up iteration on j during the next
					// iteration of the i for loop
					break;
				}
			}
			// lead position becomes the lag position
			cumulativeProbabilityLag = cumulativeProbabilityLead;
			// New lead is old lead position plus additional probability of the
			// next individual (using rank/Boltzmann)
			if (option.equalsIgnoreCase("rs")) {
				cumulativeProbabilityLead += probFromRank((double) population.size(), (double) population.size());
			} else if (i < population.size() - 1) {
				// If boltzmann
				cumulativeProbabilityLead += probFromBoltz(indexOfIndividual, allIndividualsWithFitness);
				indexOfIndividual++;
			}

		}
		population = winnerPool;
	}


	/* Get probability of selecting individual based on its fitness */
	protected double probFromBoltz(int index, ArrayList<ArrayWithFitness> popWithFitness) {
		double num = popWithFitness.get(index).fitness;
		if (Double.isInfinite(num)){
			num = Double.MAX_VALUE;
		}
		return Math.exp( num / boltzmannSum);
	}

	/*
	 * Calculate once per generation the sum used in the denominator of the
	 * Boltzmann equation
	 */
	protected void calcBoltzmannSum(ArrayList<ArrayWithFitness> popWithFitness) {
		double sum = 0;
		for (int i = 0; i < popWithFitness.size(); i++) {
			
			if (Double.isInfinite(sum)){
				sum = + Double.MAX_VALUE;
				System.out.println("infinity");
				break;
			} else{
				sum = + Math.exp((popWithFitness.get(i).fitness));
			}
		}
		boltzmannSum = sum;
	}

	/* Mutates the current population */
	protected void mutate(double mutateProb) {
		for (int i = 0; i < population.size(); i++) {
			for (int j = 0; j < population.get(i).size(); j++) {
				boolean flip = randomGenerator.nextDouble() < mutateProb;
				if (flip && population.get(i).get(j) == 1) {
					population.get(i).set(j, 0);
				} else if (flip && population.get(i).get(j) == 0) {
					population.get(i).set(j, 1);
				}
			}

		}
	}

	/*
	 * Randomly crosses over adjacent individuals in the array using a single
	 * random crossover point
	 */
	protected void singlePointCrossover(double crossProb) {
		for (int i = 0; i < population.size(); i += 2) {
			boolean cross = randomGenerator.nextDouble() < crossProb;
			int crossOverLocation;
			if (cross) {
				// Pick cross over location
				crossOverLocation = randomGenerator.nextInt(population.get(i).size());
				// Copy first part of A into C
				List<Integer> c = new ArrayList<Integer>(population.get(i).subList(0, crossOverLocation));
				// Replace first part of A with First part of B
				for (int j = 0; j < crossOverLocation; j++) {
					int value = population.get(i + 1).get(j);
					population.get(i).set(j, value);
				}
				// Replace first part of B with C
				for (int j = 0; j < c.size(); j++) {
					int value = c.get(j);
					population.get(i + 1).set(j, value);
				}

			}

		}
	}

	/*
	 * Randomly crosses over adjacent individuals in the array using uniform
	 * random crossover
	 */
	protected void uniformCrossover(double crossProb) {
		for (int i = 0; i < population.size(); i += 2) {
			boolean cross;
			for (int j = 0; j < population.get(i).size(); j++) {
				cross = randomGenerator.nextDouble() < crossProb;
				if (cross) {
					// Swap two elements
					int temp = population.get(i).get(j);
					population.get(i).set(j, population.get(i + 1).get(j));
					population.get(i + 1).set(j, temp);
				}
			}
		}
	}


	// Update the max fitness encountered so far
	protected void updateMaxFitness(int fitness, ArrayList<Integer> values) {
		if (fitness > maxFitnessSoFar) {
			bestGeneration = currentGeneration;
			maxFitnessSoFar = fitness;
			bestSolution = new ArrayList<Integer>();
			bestSolution.clear();
			bestSolution.addAll(values);
			// If all clauses are satisfied
			if (fitness == satProblem.size()) {
				foundSATSolution = true;
			}
			endTime = System.currentTimeMillis();//Time to find best solution
		}
	}


}
