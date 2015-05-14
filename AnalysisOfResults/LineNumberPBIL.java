package AnalysisOfResults;
// Line number in results files specific to PBIL setting
public enum LineNumberPBIL {
	POP_SIZE(21),
	LEARNING_RATE(22),
	NEG_LEARNING_RATE(23),
	MUTATION_PROB(24),
	MUTATION_SHIFT(25);
	
	private int numVal;

    LineNumberPBIL(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
