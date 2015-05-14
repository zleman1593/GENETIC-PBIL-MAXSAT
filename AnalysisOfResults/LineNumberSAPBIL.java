package AnalysisOfResults;
// Line number in results files specific to PBIL setting
public enum LineNumberSAPBIL {
	POP_SIZE(24),
	LEARNING_RATE(25),
	NEG_LEARNING_RATE(26),
	MUTATION_PROB(27),
	MUTATION_SHIFT(28),
	SA_FREQUENCY(29);
	
	private int numVal;

    LineNumberSAPBIL(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
