
public interface bin {

	void resolve();

	void add(gridPoint npoint);

	double genBearingDelta(double nextDouble);

	double genSpeedDelta(double nextDouble);

	void printBDF();

	void printBP();

	void printSDF();

	void printSP();

	void printSF();

	void printBF();

}
