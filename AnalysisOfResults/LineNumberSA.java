
package AnalysisOfResults;

//Line number in results files specific to GA setting
public enum LineNumberSA {
	MIN_TEMP(21),
	MAX_TEMP(22);

	private int numVal;

	LineNumberSA(int numVal) {
     this.numVal = numVal;
 }

 public int getNumVal() {
     return numVal;
 }
}


