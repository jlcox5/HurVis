import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;


public class Advisory {
    public Vector<vec> pathdata = new Vector<vec>();
    
    //private Bounds latlonframe = new Bounds(-100,17,25,16);
    private Bounds latlonframe = new Bounds(-100,33,25,-15.5);
    public vec project(vec p){
    	return latlonframe.project(p);
    }
    
    private int LAT      = 0;
    private int LON      = 1;
    private int BEARING  = 2;
    private int SPEED    = 3;
    
    private static double[] errorRad = {50.004, 100.471, 144.302, 187.515, 283.263, 390.463};
    private static double[] errorRadSegs = null;
    
    private double getErrorDayi(int i){
    	return i==0 ? 0.0 : errorRad[Math.min(i-1, 5)];
    }
    
    private double getErrorSegi(int seg){
    	return errorRadSegs[Math.min(errorRadSegs.length, seg)];
    }
    
    public static int[] hours = {9,12,12,12,24};
    //MAKE THIS BETTER :(
    public double hoursInOriginalSeg(int seg){
    	return hours[Math.min(seg,4)];
    }
    public int hoursInOriginalSegi(int seg){
    	return hours[Math.min(seg,4)];
    }
    
    public double hoursInSeg(){
    	//return hours[ Math.min(seg, 4) ];
    	return 3;
    }
    public int hoursInSegi(){
    	return 3;
    }
    
    public vec getPos(int i){
    	return pathdata.get(i).head(2);
    }
    public vec getLeft(int i){
    	return leftBounds[i];
    }
    public vec getRight(int i){
    	return rightBounds[i];
    }
    public double getLon(int i){
    	return pathdata.get(i).get(0);
    }
    public double getLat(int i){
    	return pathdata.get(i).get(1);
    }
    public double getBear(int i){
    	return pathdata.get(i).get(2);
    }
    public double getSpeed(int i){
    	return pathdata.get(i).get(3);
    }
    
    public Advisory(String advfile){
    	if(errorRadSegs==null){
    		int sum=0;
    		for(int i=0;i<errorRad.length;++i) 
    			sum+=hoursInOriginalSegi(i);
    		errorRadSegs = new double[sum/hoursInSegi()];
    		//System.err.println(sum);
    		int next=0;
    		double left=0.0;
    		double right;
    		//[FIX] Begin at 0.0km through 7 days
    		for(int i=0;i<errorRad.length;++i){
    		   right=errorRad[i];
    		   int t_int = hoursInOriginalSegi(i);
    		   double t = (double)t_int;
    		   for(int j=i==0?1:0;j<t_int/3;++j){
    			   double j_dub = (double)j;
    			   double aleph = j_dub/t;
    			   errorRadSegs[next++]=(1.0-aleph)*left + (aleph)*right;
    		   }
    		   left=right;
    		}
    		//System.err.println("-----------------");
    		//for(int i=0;i<errorRadHours.length;++i) System.err.println(""+errorRadHours[i]);
    		//System.err.println("-----------------");
    	}
    	
    	for(int i=0;i<errorRadSegs.length;++i)System.err.println("Dbug["+i+"]: "+errorRadSegs[i]);
    	
    	InputStreamReader rdr = new InputStreamReader(this.getClass().getResourceAsStream(advfile));
    	BufferedReader    in  = new BufferedReader(rdr);
    	
    	String line = "";
    	try {
			while(in.ready() && (line = in.readLine().trim()) != "" && line != "\n"){
				String[] svals = line.split("\\s+");
				double[]   vals = new double[svals.length];
				for(int i=0; i < vals.length; ++i)
					vals[i]=new Double(svals[i]);
				
				pathdata.add(vec.vec4(vals[LON], vals[LAT], vals[BEARING], vals[SPEED]));
				//pathdata.add(vec.vec2(vals[LON], vals[LAT]));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	processData();
    }
    
    public vec p_from_e12(vec p){
    	return vec.vec2(-p.get(1),p.get(0));
    }
    
    public vec[] two_circle_tans(vec c0, vec c1, double r0, double r1){    	
    	vec d01 = c0.sub(c1).normalized();
    	
    	vec drot = p_from_e12(d01);
    	
    	return new vec[]{ c0.add(drot.scale(r0)), c1.add(drot.scale(r1)), c0.add(drot.scale(-r0)), c1.add(drot.scale(-r1)) };
    }
    
    public vec[] leftBounds;
    public vec[] rightBounds;
    
    public void processData(){
       leftBounds = new vec[pathdata.size()];
       rightBounds = new vec[pathdata.size()];
       
       //leftBounds[0] = pathdata.get(0).head(2);
       //rightBounds[0] = pathdata.get(0).head(2);
       
       vec[] leftErrProto  = new vec[7];
       vec[] rightErrProto = new vec[7];
       
       leftErrProto[0]  = pathdata.get(0).head(2);
       rightErrProto[0] = pathdata.get(0).head(2);
       
       int lastCursor = 0;
       int thisCursor = 0;
       for(int i=1; i < 6; ++i){
    	   int h = hoursInOriginalSegi(i-1);
    	   thisCursor += h/3;
    	   
    	   vec d0 = pathdata.get(lastCursor);
    	   vec d1 = pathdata.get(thisCursor);
    	   
    	   vec p0 = d0.head(2);
    	   vec p1 = d1.head(2);
    	   
    	   double bearO = vizUtils.findBearing_2(p0,p1);
    	   double bearL = bearO < 90.0 ? bearO + 270.0 : bearO - 90.0;
    	   double bearR = (bearO + 90.0) % 360.0;
    	   
    	   leftErrProto[i] = vizUtils.spherical_translation(p1, getErrorDayi(i), bearL);
    	   rightErrProto[i] = vizUtils.spherical_translation(p1, getErrorDayi(i), bearR);
    	   
    	   lastCursor=thisCursor;
       }
       //Interpolate over segments for true error bounds
       
       int segments=0;
       for(int i=0; i < 5; ++i){
    	   int J = hoursInOriginalSegi(i)/3;
    	   double dubJ = (double)J;
    	   
    	   vec L0 = leftErrProto[i];
    	   vec L1 = leftErrProto[i+1];
    	   vec R0 = rightErrProto[i];
    	   vec R1 = rightErrProto[i+1];
    	   
    	   for(int j=0; j < (J=hoursInOriginalSegi(i)/3); ++j){
    		   double dubj = (double)j;
    		   double t = dubj/dubJ;
    		   
    		   leftBounds[segments] = L0.lerp(L1,t);
    		   rightBounds[segments] = R0.lerp(R1,t);
    		   
    		   ++segments;
    	   }
       }
       
       while( segments < pathdata.size() ){
    	   //Handle extrapolation at end of bounds
    	   double bearing = vizUtils.findBearing_2(pathdata.get(segments-1), pathdata.get(segments));
    	   
    	   leftBounds[segments]  = vizUtils.spherical_translation(leftBounds[segments-1],getErrorDayi(segments),bearing);
    	   rightBounds[segments] = vizUtils.spherical_translation(rightBounds[segments-1],getErrorDayi(segments),bearing);
    	   
    	   ++segments;
       }
/*
       for(int i=0; i < pathdata.size()-1; ++i){
    	   vec d0 = pathdata.get(i);
    	   vec d1 = pathdata.get(i+1);
    	   
    	   vec p0 = d0.head(2);
    	   vec p1 = d1.head(2);
    	   
    	   double bearO = vizUtils.findBearing_2(p0,p1);
    	   //System.err.println("!bear true: "+bearO);
    	   double bearL = bearO < 90.0 ? bearO + 270.0 : bearO - 90.0;
    	   double bearR = (bearO + 90.0) % 360.0;
    	   //System.err.println("!bear err: "+bearL + " " + bearR);
    	   
    	   //System.err.println(i + " " + getError(i));
    	   
    	   leftBounds[i+1] = vizUtils.spherical_translation(p1, getErrorSegi(i), bearL);
    	   rightBounds[i+1] = vizUtils.spherical_translation(p1, getErrorSegi(i), bearR);
       }
*/
    }
}
