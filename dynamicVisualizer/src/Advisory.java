import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Vector;


public class Advisory {
	
	public boolean rightOf(vec lhs,vec rhs){
		return (lhs.get(0)*rhs.get(1)-lhs.get(1)*rhs.get(0)) > 0.0;
	}
	public boolean leftOf(vec lhs,vec rhs){
		return !rightOf(lhs,rhs);
	}
	
	public class lseg{
		public vec l0;
		public vec l1;
		public vec dir;
		public double length;
		public lseg(vec nl0, vec nl1){
			l0 = nl0;
			l1 = nl1;
			dir = l1.sub(l0).normalized();
			length = l1.sub(l0).norm();
		}
		public vec project(vec x){
			return dir.scale(x.sub(l0).dot(dir));
		}
		public vec projectClamped(vec x){
			double run = x.sub(l0).dot(dir);
			if( run <   0.0  ) run = 0.0;
			if( run > length ) run = length;
			return dir.scale(run);
		}
		public double distance(vec x){
			return x.sub(projectClamped(x)).norm();
		}
		public boolean rightOf(vec x){
			vec xdir = x.sub(l0);
			return xdir.get(0)*dir.get(1)-xdir.get(1)*dir.get(0) < 0.0;
		}
		public boolean leftOf(vec x){
			return !rightOf(x);
		}
	};
	
	public lseg[] leftBoundingSegs;
	public lseg[] rightBoundingSegs;
	
	public void initBoundingSegments(){
		int N = leftBounds.length-1;
		leftBoundingSegs  = new lseg[N];
		rightBoundingSegs = new lseg[N];
		
		for(int i=0; i < N; ++i){
			leftBoundingSegs [i] = new lseg(project( leftBounds[i]),project( leftBounds[i+1]));
			rightBoundingSegs[i] = new lseg(project(rightBounds[i]),project(rightBounds[i+1]));
		}
	}
	
	public boolean inCone(vec x){
		double dl=Double.POSITIVE_INFINITY;
		double dr=Double.POSITIVE_INFINITY;
		int ndxl=0;
		int ndxr=0;
		double temp;
		for(int i=0; i < leftBoundingSegs.length-1; ++i){
			temp = leftBoundingSegs[i].distance(x);
			if(temp < dl){
				dl   = temp;
				ndxl = i;
			}
			temp = rightBoundingSegs[i].distance(x);
			if(temp < dr){
				dr   = temp;
				ndxr = i;
			}
		}
		return leftBoundingSegs[ndxl].rightOf(x) && rightBoundingSegs[ndxr].leftOf(x) ;
	}
	
	public Random rand = new Random();
	
	public double Bearing0, Speed0;
	
	private boolean TEST_ERROR_CONES = false;
    public vec[] TEST_leftBounds  = {vec.vec2(-85.0000, 24.5000)
    		                        ,vec.vec2(-85.3529, 24.3851)
    		                        ,vec.vec2(-85.7052, 24.2693)
    		                        ,vec.vec2(-86.0556, 24.1532)
    		                        ,vec.vec2(-86.5505, 24.2394)
    		                        ,vec.vec2(-87.0460, 24.3239)
    		                        ,vec.vec2(-87.5421, 24.4069)
    		                        ,vec.vec2(-88.0367, 24.4879)
    		                        ,vec.vec2(-88.5433, 24.8273)
    		                        ,vec.vec2(-89.0526, 25.1650)
    		                        ,vec.vec2(-89.5647, 25.5009)
    		                        ,vec.vec2(-90.0774, 25.8336)
    		                        ,vec.vec2(-90.4687, 26.3629)
    		                        ,vec.vec2(-90.8637, 26.8911)
    		                        ,vec.vec2(-91.2623, 27.4182)
    		                        ,vec.vec2(-91.6629, 27.9418)
    		                        ,vec.vec2(-91.9044, 28.6011)
    		                        ,vec.vec2(-92.1490, 29.2600)
    		                        ,vec.vec2(-92.3968, 30.9184)
    		                        ,vec.vec2(-92.6478, 30.5764)
    		                        ,vec.vec2(-92.9023, 31.2339)
    		                        ,vec.vec2(-93.1604, 31.8909)
    		                        ,vec.vec2(-93.4221, 32.5473)
    		                        ,vec.vec2(-93.6854, 33.1974)
    		                        ,vec.vec2(-93.6854, 33.1974)};
    public vec[] TEST_rightBounds = {vec.vec2(-85.0000,24.5000)
    		                        ,vec.vec2(-85.3141,24.6831)
    		                        ,vec.vec2(-85.6291,24.8656)
    		                        ,vec.vec2(-85.9440,25.0468)
    		                        ,vec.vec2(-86.2457,25.3140)
    		                        ,vec.vec2(-86.5487,25.5806)
    		                        ,vec.vec2(-86.8530,25.8466)
    		                        ,vec.vec2(-87.1574,26.1108)
    		                        ,vec.vec2(-87.3430,26.4734)
    		                        ,vec.vec2(-87.5298,26.8358)
    		                        ,vec.vec2(-87.7178,27.1979)
    		                        ,vec.vec2(-87.9061,27.5582)
    		                        ,vec.vec2(-87.9579,27.9779)
    		                        ,vec.vec2(-88.0100,28.3976)
    		                        ,vec.vec2(-88.0625,28.8173)
    		                        ,vec.vec2(-88.1153,29.2351)
    		                        ,vec.vec2(-87.7786,29.6674)
    		                        ,vec.vec2(-87.4390,30.0987)
    		                        ,vec.vec2(-87.0965,30.5292)
    		                        ,vec.vec2(-86.7509,30.9588)
    		                        ,vec.vec2(-86.4022,31.3874)
    		                        ,vec.vec2(-86.0503,31.8151)
    		                        ,vec.vec2(-85.6952,32.2418)
    		                        ,vec.vec2(-85.3399,32.6638)
    		                        ,vec.vec2(-85.3399,32.6638)};
	
    public Vector<vec> pathdata = new Vector<vec>();
    
    /*public vec[] projectedLeft;
    public vec[] projectedRight;
    public void initProjectedBounds(){
    	int N = leftBounds.length;
    	projectedLeft  = new vec[N];
    	projectedRight = new vec[N];
    	for(int i=0; i < N; ++i){
    		projectedLeft[i]  = project( leftBounds[i]);
    		projectedRight[i] = project(rightBounds[i]);
    	}
    }
    */
    
    //private Bounds latlonframe = new Bounds(-100,17,25,16);
    //private Bounds latlonframe = new Bounds(-100,33,25,-15.5);
    //private Bounds latlonframe = new Bounds(-100,17,25,16.0,true);
    private Bounds latlonframe = new Bounds(-100,17,25,16.0,true);
    public Bounds getFrame(){return latlonframe;}
    public vec project(vec p){
    	return latlonframe.project(p);
    }
    
    private int LAT      = 0;
    private int LON      = 1;
    private int BEARING  = 2;
    private int SPEED    = 3;
    
    private static double[] errorRad = {50.004, 100.471, 144.302, 187.515, 283.263, 390.463};
    //private static double[] errorRadSegs = null;
    
    private double getErrorDayi(int i){
    	return i==0 ? 0.0 : errorRad[Math.min(i-1, 5)];
    }
    
    //private double getErrorSegi(int seg){
    //	return errorRadSegs[Math.min(errorRadSegs.length, seg)];
    //}
    private double getErrorSegi(int seg){
    	int segwidth = hoursInSegi();
    	int hours = segwidth*seg;
    	int cursor = 0;
    	while((hours-=hoursInOriginalSegi(cursor))>0)++cursor;
    	hours+=hoursInOriginalSegi(cursor);
    	double t = ((double)hours)/((double)hoursInOriginalSegi(cursor));
    	
    	System.err.println(""+seg+":"+cursor+ " " + t);
    	return vizUtils.lerp(errorRad[cursor],errorRad[cursor+1],t);
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
    	return TEST_ERROR_CONES?TEST_leftBounds[i]:leftBounds[i];
    }
    public vec getRight(int i){
    	return TEST_ERROR_CONES?TEST_rightBounds[i]:rightBounds[i];
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
    public double getInitialBearing(){
    	return Bearing0;
    }
    public double getInitialSpeed(){
    	return Speed0;
    }
    
    public Advisory(String advfile){
    	/*
    	if(errorRadSegs==null){
    		int sum=0;
    		for(int i=0;i<errorRad.length;++i) 
    			sum+=hoursInOriginalSegi(i);
    		errorRadSegs = new double[sum/hoursInSegi()];
    		//System.err.println(sum);
    		int next=0;
    		double left=errorRad[0];
    		double right;
    		//[FIX] Begin at 0.0km through 7 days
    		for(int i=1 ; i < errorRad.length ; ++i){
    		   right=errorRad[i];
    		   int t_int = hoursInOriginalSegi(i);
    		   double t = (double)t_int;
    		   for(int j=(i==0?1:0);j<t_int/3;++j){
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
    	*/
    	//System.err.println(errorRadSegs.length);
    	
    	//for(int i=0;i<errorRadSegs.length;++i)System.err.println("Dbug["+i+"]: "+errorRadSegs[i]);
    	
    	InputStreamReader rdr = new InputStreamReader(this.getClass().getResourceAsStream(advfile));
    	BufferedReader    in  = new BufferedReader(rdr);
    	
    	//int count=0;
    	String line = "";
    	try {
    		{
    		   line = in.readLine().trim();
    		   String[] svals = line.split("\\s+");
    		   double[]  vals = new double[svals.length];
    		   
			   for(int i=0; i < vals.length; ++i)
					vals[i]=new Double(svals[i]);
    		   
    		   Bearing0 = vals[0];
    		   Speed0   = vals[1];
    		   System.err.println("Bearing0: " + vals[0]);
    		}
			while(in.ready() && (line = in.readLine().trim()) != "" && line != "\n"){
				String[] svals = line.split("\\s+");
				double[]  vals = new double[svals.length];
				for(int i=0; i < vals.length; ++i)
					vals[i]=new Double(svals[i]);
				
				//if(count++!=0)
				System.err.println(vals[LON] + " " + vals[LAT] + " " + vals[BEARING] + " " + vals[SPEED]);
				pathdata.add(vec.vec4(vals[LON], vals[LAT], vals[BEARING], vals[SPEED]));
				//pathdata.add(vec.vec2(vals[LON], vals[LAT]));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
    	processData();
    	//initProjectedBounds();
    	initBoundingSegments();
    	
    	/*
    	System.err.println("LEFT-----------------------------");
    	for(int i=0;i<leftBounds.length;++i) System.err.println(leftBounds[i]);
    	System.err.println("----------------------------RIGHT");
    	for(int i=0;i<rightBounds.length;++i) System.err.println(rightBounds[i]);
    	System.err.println("---------------------------------");
    	*/
    	
    	//System.err.println("Path Data:");
    	//for(int i=0;i<pathdata.size();++i)System.err.println(pathdata.get(i));
    	//System.err.println("Error Left:");
    	//for(int i=0;i<leftBounds.length;++i)System.err.println(leftBounds[i]);
    	//System.err.println("Error Right:");
    	//for(int i=0;i<rightBounds.length;++i)System.err.println(rightBounds[i]);
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
       
       vec[] leftErrProto  = new vec[6];
       vec[] rightErrProto = new vec[6];
       
       leftErrProto[0]  = pathdata.get(0).head(2);
       rightErrProto[0] = pathdata.get(0).head(2);
       
       int lastCursor = 0;
       int thisCursor = 0;
       for(int i=0; i < 5; ++i){
    	   int h = hoursInOriginalSegi(i);
    	   thisCursor += h/3;
    	   
    	   vec d0 = pathdata.get(lastCursor);
    	   vec d1 = pathdata.get(thisCursor);
    	   
    	   vec p0 = d0.head(2);
    	   vec p1 = d1.head(2);
    	   
    	   double bearO = (vizUtils.findBearing_2(p1,p0)+180)%360;
    	   //double bearL = bearO < 90.0 ? bearO + 270.0 : bearO - 90.0;
    	   //double bearR = (bearO + 90.0) % 360.0;
    	   double bearL = vizUtils.sanitizeBearing(bearO - 90.0);
    	   double bearR = vizUtils.sanitizeBearing(bearO + 90.0);
    	   
    	   //leftErrProto[i+1] = vizUtils.spherical_translation(p1, getErrorSegi(i), bearL);
    	   //rightErrProto[i+1] = vizUtils.spherical_translation(p1, getErrorSegi(i), bearR);
    	   leftErrProto[i+1] = vizUtils.spherical_translation(p1, getErrorDayi(i+1), bearL);
    	   rightErrProto[i+1] = vizUtils.spherical_translation(p1, getErrorDayi(i+1), bearR);
    	   
    	   System.err.println("lefErr " + (i+1) + ": " + leftErrProto[i+1]);
    	   
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
       for(int j = 0; j < rightBounds.length; j++){
    	   System.err.println("RightBounds[" + j + "]: " + rightBounds[j]);
       }
       for(int j = 0; j < leftBounds.length; j++){
    	   System.err.println("leftBounds[" + j + "]: " + leftBounds[j]);
       }
    }
}
