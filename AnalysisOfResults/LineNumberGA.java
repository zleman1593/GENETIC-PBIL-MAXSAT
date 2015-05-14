package AnalysisOfResults;

// Line number in results files specific to GA setting
public enum LineNumberGA {
	POP_SIZE(21),
	SELECTION_TYPE(22),
	CROSSOVER_TYPE(23),
	CROSSOVER_PROB(24),
	MUTATION_PROB(25);
	
	private int numVal;

    LineNumberGA(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
