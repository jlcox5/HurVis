import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class gridStruct {
  public Advisory adv;
  public String   filename;
	
  public int degLat;
  public int degLon;
  public int degPerBin;
  public int minLat;
  public int minLon;
  public int maxLat;
  public int maxLon;
  
  public int LonShift;
  public int LatShift;
  
  public bin grid[][][];
  
  public gridStruct(Advisory nadv, String nfilename){
	  adv = nadv;
	  filename = nfilename;
	  
	  degLat = Math.abs((int)adv.getFrame().height);
	  degLon = Math.abs((int)adv.getFrame().width);
	  minLat = (int)adv.getFrame().y;
	  minLon = (int)adv.getFrame().x;
	  maxLat = minLat+degLat;
	  maxLon = minLon+degLon;
	  
	  LonShift=Math.min(Math.abs(minLon),Math.abs(degLon+minLon));
	  LatShift=Math.min(Math.abs(minLat),Math.abs(degLat+minLat));
	  
	  degPerBin = 360/6;
	  
	  grid = new bin[degLat+1][degLon+1][6];

	  for(int i=0; i < grid.length; ++i)
		  for(int j=0; j < grid[i].length; ++j)
			  for(int k=0; k < grid[i][j].length; ++k){
				  grid[i][j][k] = new bin();
			  }
	  
	  loadFile();
	  
	  for(int i=0; i < grid.length; ++i)
		  for(int j=0; j < grid[i].length; ++j)
			  for(int k=0; k < grid[i][j].length; ++k)
				  grid[i][j][k].resolve();
  }
  
  public bin findBin(vec x, double b){
	  System.err.println(x);
	  int i = (int)Math.floor(x.get(1));
	  int j = (int)Math.floor(x.get(0));
	  int k = (int)Math.floor((b%360.0)/degPerBin);
	  
	  int oldi = i;
	  i = Math.abs(i)-LatShift;
	  if( i < 0 || i > degLat){ 
		  System.err.println("[E,i]: "+oldi+" --> "+i+" / " + degLat);
		  i = Math.max(0,Math.min(i, degLat));
	  }
	  int oldj = j;
	  j = Math.abs(j)-LonShift;
	  if( j < 0 || j > degLon){
		  System.err.println("[E,j]: "+oldj+" --> "+j+" / " + degLon);
		  j = Math.max(0,Math.min(j,degLon));
	  }
	  return grid[i][j][k];
  }
  
  private void loadFile(){
	InputStreamReader rdr = new InputStreamReader(this.getClass().getResourceAsStream(filename));
  	BufferedReader    in  = new BufferedReader(rdr);
  	
  	int clat=0,clon=0,cbin=0;
  	
  	//int count=0;
  	String line = "";
  	try {
  		//{
  		//   line = in.readLine().trim();
  		//   String[] svals = line.split("\\s+");
  		//   double[]  vals = new double[svals.length];
  		//   
  		//   Bearing0 = vals[0];
  		//   Speed0   = vals[1];
  		//}
		while(in.ready() && (line = in.readLine().trim()) != "" && line != "\n"){
				String[] svals = line.split("\\s+");
			    
				switch(svals[0]){
				  case "A":
					  clat = new Integer(svals[1]);
					  break;
				  case "O":
					  clon = new Integer(svals[1]);
					  break;
				  case "D":
					  cbin = (new Integer(svals[1]))/degPerBin;
					  break;
				  case "P":
					  double vals[] = new double[svals.length-1];
					  for(int i=0; i < svals.length-1; ++i)
					    vals[i]=new Double(svals[i+1]);
					  gridPoint npoint = new gridPoint(vals[0],vals[1],vals[2],vals[3],vals[4],vals[5],vals[6]);
					  //Speed check?
					  System.err.println(""+(clat)+" "+(clon)+" "+cbin);
					  grid[clat-LatShift][clon-LonShift][cbin].add(npoint);
					  break;
				  default:
					  System.err.println("Unrecognized command: " + svals[0]);
					  break;
				}
		}
	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	}
  }
}
