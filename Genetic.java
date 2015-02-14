import java.util.*;

public class Genetic extends EvolAlgorithms {

	// Population that is evolved
	public ArrayList<ArrayList<Integer>> population = new ArrayList<ArrayList<Integer>>();
	// The MAXSAT problem
	private ArrayList<ArrayList<Integer>> satProblem = new ArrayList<ArrayList<Integer>>();
	// Random Number generator
	private Random random;
	// Maximum iterations allowed
	private int maxIteration;
	// Maximum fitness found so far in any generation
	private int maxFitnessSoFar = 0;
	// Individual Solution with Maximum fitness found so far in any generation
	public ArrayList<Integer> bestSolution;
	// Probabilities
	private double crossOverProb;
	private double mutateProb;
	private double boltzmannSum;
	// Keeps track of whether a full solution has been found
	boolean foundSATSolution = false;

	// Constructor.
	public Genetic(int popSize, int literalNumber, int maxIteration,
			double crossOverProb, double mutateProb,
			ArrayList<ArrayList<Integer>> satProblem) {
		this.satProblem = satProblem;
		this.population = initPopulation(popSize, literalNumber);
		this.random = new Random();
		this.maxIteration = maxIteration;
		this.crossOverProb = crossOverProb;
		this.mutateProb = mutateProb;
	}

	// Update the max fitness encountered so far
	public void updateMaxFitness(int fitness, ArrayList<Integer> values) {
		if (fitness > maxFitnessSoFar) {
			maxFitnessSoFar = fitness;
			bestSolution = new ArrayList<Integer>();
			bestSolution.addAll(values);
			// If all clauses are satisfied
			if (fitness == satProblem.size()) {
				foundSATSolution = true;
			}
		}
	}

	public void evolve(String selectionMethod) {
		for (int i = 0; i < maxIteration && !foundSATSolution; i++) {

			if (selectionMethod.equalsIgnoreCase("rank")
					|| selectionMethod.equalsIgnoreCase("boltzmann")) {
				rankBoltzSelect(selectionMethod);
			} else {
				tournamentSelect(1, 5);// Todo make variables
			}

			if (foundSATSolution) {
				System.out.println("Fully Satisfied Clauses");
				break;
			}
			singlePointCrossover(crossOverProb);
			mutate(mutateProb);
		}
		System.out.println("Max Fitness so far:" + maxFitnessSoFar);
		System.out.println("Best Solution" + bestSolution);
	}

	private void tournamentSelect(int winners, int sample) {
		ArrayList<ArrayList<Integer>> winnerPool = new ArrayList<ArrayList<Integer>>();
		// While the next generation pool is smaller than the population limit
		while (winnerPool.size() < population.size()) {

			// Get array of unique random numbers (AKA. randomly choose "sample"
			// number of individuals)
			ArrayList<Integer> randomNumbers = new ArrayList<Integer>();
			while (randomNumbers.size() < sample) {
				int number;
				do {
					number = random.nextInt(population.size());
				} while (randomNumbers.contains(number));
				randomNumbers.add(number);
			}

			/*
			 * Evaluate each individual in the random sample, and add it with
			 * its fitness to a new object
			 */
			ArrayList<ArrayWithFitness> withFitness = new ArrayList<ArrayWithFitness>();
			for (int i = 0; i < randomNumbers.size(); i++) {
				ArrayList<Integer> individual = population.get(i);
				ArrayWithFitness memberWithFitness = new ArrayWithFitness(
						individual);
				memberWithFitness.fitness = evaluateCandidate(satProblem,
						individual);
				withFitness.add(memberWithFitness);
				// Update the global variable if a new individual is more fit
				// than the current best
				updateMaxFitness(memberWithFitness.fitness, individual);
			}

			// Sort the individuals by their fitness
			Collections.sort(withFitness);

			// Pick top "winners" individuals and add to winnerPool until it is
			// full
			for (int i = 0; i < winners
					&& (winnerPool.size() < population.size()); i++) {
				winnerPool
				.add((ArrayList<Integer>) withFitness.get(i).individual
						.clone());
			}

		}
		this.population = winnerPool;// Replace current population with the
		// newly selected pool
	}

	/* Method that runs rank and Boltzmann selection */
	private void rankBoltzSelect(String option) {
		ArrayList<ArrayList<Integer>> winnerPool = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayWithFitness> withFitness = new ArrayList<ArrayWithFitness>();

		// Pass in each individual and get back a fitness and merge with
		// individual
		for (int i = 0; i < this.population.size(); i++) {
			ArrayList<Integer> individual = population.get(i);
			ArrayWithFitness memberWithFitness = new ArrayWithFitness(
					individual);
			memberWithFitness.fitness = evaluateCandidate(satProblem,
					individual);
			updateMaxFitness(memberWithFitness.fitness, individual);
			withFitness.add(memberWithFitness);
		}

		// Sort by fitness so that position zero has individual with highest
		// fitness
		Collections.sort(withFitness);

		// Generate one random double per member of the population
		double[] probability = new double[this.population.size()];
		for (int i = 0; i < this.population.size(); i++) {
			probability[i] = random.nextDouble();
		}

		// Sort ascending
		Arrays.sort(probability);

		int pickUpFrom = 0;// Prevents rerunning of elements in the probability
		// vector
		int indexOfIndividual = 1;// Used for Boltzmann

		double cumulativeProbabilityLag = 0;
		double cumulativeProbabilityLead;
		// Initialize two range indices that bracket probability ranges
		if (option.equalsIgnoreCase("rank")) {
			cumulativeProbabilityLead = probFromRank(
					(double) this.population.size(),
					(double) this.population.size());
		} else {
			calcBoltzmannSum(withFitness); // calculate the denominator of the
			// boltzmann function once per
			// generation
			cumulativeProbabilityLead = probFromBoltz(0, withFitness);
		}

		for (int i = 0; i < this.population.size(); i++) {

			for (int j = pickUpFrom; j < probability.length; j++) {
				// If random value/selection is within the range indicated by
				// the two indices then
				// add the individual associated with that range to the winner
				// pool
				if ((probability[j] >= cumulativeProbabilityLag)
						&& (probability[j] < cumulativeProbabilityLead)) {
					winnerPool
					.add((ArrayList<Integer>) withFitness.get(i).individual
							.clone());
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
			if (option.equalsIgnoreCase("rank")) {
				cumulativeProbabilityLead += probFromRank(
						(double) this.population.size(),
						(double) this.population.size());
			} else if (i < population.size() - 1) {
				// If boltzmann
				cumulativeProbabilityLead += probFromBoltz(indexOfIndividual,
						withFitness);
				indexOfIndividual++;
			}

		}
		population = winnerPool;
	}

	/* Get probability of selecting individual based on its rank */
	private double probFromRank(double rank, double popsize) {
		return rank / ((popsize * (popsize + 1) / 2));
	}

	/* Get probability of selecting individual based on its fitness */
	private double probFromBoltz(int index,
			ArrayList<ArrayWithFitness> popWithFitness) {
		return Math.exp(popWithFitness.get(index).fitness / boltzmannSum);
	}

	/*
	 * Calculate once per generation the sum used in the denominator of the
	 * Boltzmann equation
	 */
	private void calcBoltzmannSum(ArrayList<ArrayWithFitness> popWithFitness) {
		double sum = 0;
		for (int i = 0; i < popWithFitness.size(); i++) {
			sum = +Math.exp((popWithFitness.get(i).fitness));
		}
		boltzmannSum = sum;
	}

	/* Mutates the current population */
	private void mutate(double mutateProb) {
		for (int i = 0; i < this.population.size(); i++) {
			for (int j = 0; j < this.population.get(i).size(); j++) {
				boolean flip = random.nextDouble() < mutateProb;
				if (flip && this.population.get(i).get(j) == 1) {
					this.population.get(i).set(j, 0);
				} else if (flip && this.population.get(i).get(j) == 0) {
					this.population.get(i).set(j, 1);
				}
			}

		}
	}

	/*
	 * Randomly crosses over adjacent individuals in the array using a single
	 * random crossover point
	 */
	private void singlePointCrossover(double crossProb) {
		for (int i = 0; i < this.population.size(); i += 2) {
			boolean cross = random.nextDouble() < crossProb;
			int crossOverLocation;
			if (cross) {
				// Pick cross over location
				crossOverLocation = random.nextInt(this.population.get(i)
						.size());
				// Copy first part of A into C
				List<Integer> c = new ArrayList<Integer>(this.population.get(i)
						.subList(0, crossOverLocation));
				// Replace first part of A with First part of B
				for (int j = 0; j < crossOverLocation; j++) {
					int value = this.population.get(i + 1).get(j);
					this.population.get(i).set(j, value);
				}
				// Replace first part of B with C
				for (int j = 0; j < c.size(); j++) {
					int value = c.get(j);
					this.population.get(i + 1).set(j, value);
				}

			}

		}
	}

	/*
	 * Randomly crosses over adjacent individuals in the array using uniform
	 * random crossover
	 */
	private void uniformCrossover(double crossProb) {
		for (int i = 0; i < this.population.size(); i += 2) {
			boolean cross = random.nextDouble() < crossProb;
			int crossOverLocation;
			if (cross) {
				// Pick cross over location
				crossOverLocation = random.nextInt(this.population.get(i)
						.size());
				// Copy first part of A into C
				List<Integer> c = new ArrayList<Integer>(this.population.get(i)
						.subList(0, crossOverLocation));
				// Replace first part of A with First part of B
				for (int j = 0; j < crossOverLocation; j++) {
					int value = this.population.get(i + 1).get(j);
					this.population.get(i).set(j, value);
				}
				// Replace first part of B with C
				for (int j = 0; j < c.size(); j++) {
					int value = c.get(j);
					this.population.get(i + 1).set(j, value);
				}

			}

		}
	}

	// Initialize population
	public ArrayList<ArrayList<Integer>> initPopulation(int popSize,
			int literalNumber) {
		ArrayList<ArrayList<Integer>> population = new ArrayList<ArrayList<Integer>>();
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
}
