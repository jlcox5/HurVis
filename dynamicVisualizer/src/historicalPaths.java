
public class historicalPaths implements pathStrategy {
	
	public Advisory adv;
	
	public gridStruct2 grid;
	public String     filename;
	
	public int N;
	
	public historicalPaths(Advisory nadv, String nfilename){
		adv = nadv;
		N   = adv.pathdata.size()-1;
		
		filename = nfilename;
		
		grid = new gridStruct2(adv,filename);
	}

	@Override
	public vec genDeltas(vec x, double b, int day) {
		bin node = grid.findBin(x, b);
		return vec.vec2(node.genBearingDelta(adv.rand.nextDouble()),node.genSpeedDelta(adv.rand.nextDouble()));
	}

	@Override
	public int getDays() {
		return N;
	}

}
