package AnalysisOfResults;

// Line number in results files specific to GA setting
public enum LineNumberSAGA {
	POP_SIZE(21),
	CROSSOVER_PROB(22),
	MUTATION_PROB(23),
	WITHOUT_IMPROVEMENT(25);
	
	private int numVal;

    LineNumberSAGA(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}


