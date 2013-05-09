
public abstract class bin {

	abstract void resolve();

	abstract void add(gridPoint npoint);

	abstract double genBearingDelta(double nextDouble);

	abstract double genSpeedDelta(double nextDouble);
	
	abstract void printpoints2();

	abstract void printBDF();

	abstract void printBP();

	abstract void printSDF();

	abstract void printSP();

	abstract void printSF();

	abstract void printBF();
	
	boolean _debug=false;
	void DBUG(){_debug=true;}

	double getOriginalArea(){return 0.0;}

	public double getRevisedArea() {
		// TODO Auto-generated method stub
		return 0;
	}

}
