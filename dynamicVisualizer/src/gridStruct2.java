import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;


public class gridStruct2 {
	public Advisory adv;
	public String   filename;

	public int degLat;
	public int degLon;
	public int degPerBin;
	public int binsPerCell;
	public int minLat;
	public int minLon;
	public int maxLat;
	public int maxLon;

	public int LonShift;
	public int LatShift;

	//public bin grid[][][];
	public HashMap< coord, bin[] > grid;
	
	coord c(int i, int j){ return new coord(i,j); }
	bin[] newbin(){
		bin[] bar = new bin[binsPerCell];
		for(int i=0; i < binsPerCell; ++i) bar[i]=new gridbin();
		
		return bar;
	}
	bin[] newbin(int lon, int lat){
		bin[] bar = new bin[binsPerCell];
		for(int i=0; i < binsPerCell; ++i) bar[i]=new gridbin(lon,lat);
		
		return bar;
	}
	
	boolean exists(int i, int j){ return grid.containsKey(c(i,j)); }
	boolean doesNotExist(int i, int j){ return !exists(i,j); }
	
	bin[] getBin(int i, int j){
      if(doesNotExist(i,j)) grid.put(c(i,j), newbin(i,j));
      
      return grid.get( c(i,j) );
	}
	
	bin getBin(int i, int j, int c){ return getBin(i,j)[c]; }
	
	//Fix negative bins by modulating bearing to be positive
	bin getBinOrDont(int i, int j, int c){ return grid.get( c(i,j) )[c]; }
	
	//Indexing is apparently longitude,latitude
	void printNode(int i, int j){
		bin[] bins = getBin(i,j);
		
		System.err.println("Grid Lat Lon: " + j + ", " + i);
		for(int c=0; c < binsPerCell; ++c){
		  System.err.println("   bin: " + c);
		  System.err.println("   Original Area: " + bins[c].getOriginalArea() );
		  System.err.println("   Revised  Area: " + bins[c].getRevisedArea() );
		  System.err.println("      Bearing:");
		  System.err.print  ("         Value:        ");
		  bins[c].printBP();
		  System.err.println();
		  System.err.print  ("         Segment Area: ");
		  bins[c].printBDF();
		  System.err.println();
		  System.err.println("      Speed:");
		  System.err.print  ("         Value:        ");
		  bins[c].printSP();
		  System.err.println();
		  System.err.print  ("         Segment Area: ");
		  bins[c].printSDF();
		  System.err.println();
		}
	}

	public gridStruct2(Advisory nadv, String nfilename){
		adv = nadv;
		filename = nfilename;

		//degLat = Math.abs((int)adv.getFrame().height);
		//degLon = Math.abs((int)adv.getFrame().width);
		degLat = Math.abs((int)adv.getFrame().height)+1;
		degLon = Math.abs((int)adv.getFrame().width)+1;
		minLat = (int)adv.getFrame().y;
		minLon = (int)adv.getFrame().x;
		maxLat = minLat+degLat;
		maxLon = minLon+degLon;

		//LonShift=Math.min(Math.abs(minLon),Math.abs(degLon+minLon));
		//LatShift=Math.min(Math.abs(minLat),Math.abs(degLat+minLat));
		
		LonShift=0;
		LatShift=0;

		degPerBin = 360/6;
		binsPerCell = (int)(360.0/(float)degPerBin);

		grid = new HashMap< coord, bin[] >((int)(1.25f*(float)degLat*(float)degLon), 0.8001f);

		loadFile();
		System.err.println("Lattitude: 24");
		System.err.println("     Longitude: -85");
		for(int i=0; i < 5; ++i){
			System.err.println("          Bin size: "+((double)i)*60.0+" - "+((double)i+1.0)*60.0);
		    getBin(-85,24)[i].printpoints2();
		}
		
		{
			//bin[] bins = getBin(-85,24); for(int c=0; c < binsPerCell; ++c) bins[c].DBUG();
			
			//bin[]bins = getBin(-90,24); for(int c=0; c < binsPerCell; ++c) bins[c].DBUG();
		}
		{
			//for(int i=0;i<6;++i)getBin(-85,24)[i].DBUG();
			getBin(-85,24)[1].DBUG();
		}
		System.err.println("---------------------------------------------");
		Iterator<bin[]> i = grid.values().iterator();
		bin[] bins = null;
		for(bins = i.next(); i.hasNext(); bins=i.next())
			for(int c=0; c < binsPerCell; ++c) bins[c].resolve();
		for(int c=0; c < binsPerCell; ++c) bins[c].resolve();
		System.err.println("---------------------------------------------");
		//for(int i=-10; i < degLon+10; ++i)
	    //  for(int j=-10; j < degLat+10; ++j)
	    //    if( exists(i,j) ) for( int c=0; c < binsPerCell; ++c ) getBin(i,j,c).resolve();			  
		
		/*
		printNode(-85,24);
		printNode(-84,24);
		printNode(-85,25);
		printNode(-84,25);
		{
			System.err.println("-=-=-=-=-=-=-=-=-=-");
			bin[] dbins = getBin(-89,25);
			for(int c=0; c < binsPerCell; ++c)
			   System.err.println(((gridbin)dbins[c]).pointlist.size());
			System.err.println("-=-=-=-=-=-=-=-=-=-");
		}
		printNode(-90,24);
		*/
		printNode(-85,24);
		//printNode(-85,25);
		//printNode(-84,24);
		//printNode(-84,25);
	}
	
	public bin findBin(vec x, double b){
		//System.err.println("B0: "+b);
		b %= 360.0;
		//System.err.println("B1: "+b);
		b = (b<0.0)?360.0+b:b;
		//System.err.println("B2: "+b);
		
		//System.err.println(x);
		
		int i = (int)Math.floor(x.get(0));
		int j = (int)Math.floor(x.get(1));
		int k = (int)Math.floor(b/degPerBin);

		//System.err.println(""+i+" "+j+" "+k);
		int oldi = i;
		//i = Math.abs(i)-LonShift;

		int oldj = j;
		//j = Math.abs(j)-LatShift;
	
		return exists(i,j)?getBinOrDont(i,j,k):new identityBin();
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
                int state=0;
				switch(svals[0]){
				case "A":
					clat = new Integer(svals[1]);
//					if(clat==24||clat==25)state=state==0?1:state;
//					else state=0;
					break;
				case "O":
					//Negation because input always positive
					clon = -(new Integer(svals[1]));
//					if(clon==-84||clon==-85)state=state==1?2:state;
//					else state=0;
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
					//System.err.println(""+(clat)+" "+(clon)+" "+cbin);
					//System.err.println("!: "+clat+" "+clon+" "+cbin);
//					if(state==2){
//						System.err.println("Lattitude: "+clat);
//						System.err.println("     Longitude: "+clon);
//						System.err.println("          Bin size: "+((double)cbin)*60.0+" - "+((double)cbin+1.0)*60.0);
//						state=3;
//				    }
//					if(state==3){
//						npoint.print();
//					}
					getBin(clon, clat, cbin).add(npoint);
					
					//getBin(clat-LatShift, clon-LonShift, cbin).add(npoint);
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
