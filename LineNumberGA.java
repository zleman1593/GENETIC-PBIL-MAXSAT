
// Line number in results files specific to GA setting
public enum LineNumberGA {
	POP_SIZE(24),
	SELECTION_TYPE(25),
	CROSSOVER_TYPE(26),
	CROSSOVER_PROB(27),
	MUTATION_PROB(28);
	
	private int numVal;

    LineNumberGA(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
