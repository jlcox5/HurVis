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
  
  public bin grid[][][];
  
  public gridStruct(Advisory nadv, String nfilename){
	  adv = nadv;
	  filename = nfilename;
	  
	  degLat = (int)adv.getFrame().height;
	  degLon = (int)adv.getFrame().width;
	  minLat = (int)adv.getFrame().y;
	  minLon = (int)adv.getFrame().x;
	  maxLat = minLat+degLat;
	  maxLon = minLon+degLon;
	  
	  System.err.println(""+degLat+" "+degLon);
	  
	  degPerBin = 360/6;
	  
	  grid = new bin[degLat][degLon][6];
	  
	  loadFile();
	  
	  for(int i=0; i < grid.length; ++i)
		  for(int j=0; j < grid[i].length; ++j)
			  for(int k=0; k < grid[i][j].length; ++k)
				  grid[i][j][k].resolve();
  }
  
  public bin findBin(vec x, double b){
	  int i = (int)Math.floor(x.get(1));
	  int j = (int)Math.floor(x.get(0));
	  int k = (int)Math.floor((b%360.0)/60.0);
	  
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
					  grid[clat][clon][cbin].add(npoint);
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
