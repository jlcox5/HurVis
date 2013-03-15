
public abstract class bin {

	abstract void resolve();

	abstract void add(gridPoint npoint);

	abstract double genBearingDelta(double nextDouble);

	abstract double genSpeedDelta(double nextDouble);

	abstract void printBDF();

	abstract void printBP();

	abstract void printSDF();

	abstract void printSP();

	abstract void printSF();

	abstract void printBF();
	
	boolean _debug=false;
	void DBUG(){_debug=true;}

}
