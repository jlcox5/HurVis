import java.util.Random;
import java.util.Vector;


public class predictedPaths implements pathStrategy {
	
//	vec TEST_PDF_VALUES = {
//			 vec.vec3()
//			,vec.vec3()
//	};

	Advisory adv;
	
	double supportSpeed = 1.0;
	double supportBearing = 1.5;
	//double supportSpeed = 0.0001;
	//double supportBearing = 0.0001;
	//double supportScale = (double)10e-2;
	double supportScale = 1.0;
	
	double supportSpeed(){
		return supportSpeed*supportScale;
	}
	double supportBearing(){
		return supportBearing*supportScale;
	}
	
	Vector<pdf> bearingPDFs;
	Vector<pdf> speedPDFs;
	
	public int N;
	
	@Override
	public vec genDeltas(vec x, double b, int day) {
		return vec.vec2(bearingPDFs.get(day).generate(adv.rand)
				       ,speedPDFs.get(day).generate(adv.rand));
		//return vec.vec2(bearingPDFs.get(day).generate(adv.rand)
		//		       ,1.0);
	}
	
	
	//public double minDiff(double a_i, double a_f){
		//return a_f>=a_i?a_f-a_i:a_f+360.0-a_i;
		//return vizUtils.sanitizeBearing(a_f-a_i);
	//}
	
	double getSpeedDelta(vec p0, vec p1, vec p2, double dt0, double dt1){
		double sf = vizUtils.haversine(p1,p2)/dt1;
		double si = vizUtils.haversine(p0,p1)/dt0;
		
		return sf-si;
	}
	
	double _getBearingDelta(double bi, double bf){
		double toRet;
		
		toRet = bf - bi;
		if(toRet > 180){
			toRet = toRet - 360;
		}
		if(toRet < -180){
			toRet = toRet + 360;
		}
		return toRet;
		///if(true==true)return vizUtils.fromTrueNorth(bdel);
		///return vizUtils.sanitizeBearing(bdel);
		//double bdel = minDiff(bi,bf);
		
		////double bsign = Math.signum(bdel);
		////double bmag  = Math.abs(bdel)%360.0;
		
		////return bsign*((bmag>180.0)?(bmag-360.0):bmag);
		//return bsign*(Math.abs(bdel)%360.0);
	}
	
	double getBearingDelta(vec p0, vec p1, vec p2){
		double bf = vizUtils.findBearing_2(p1, p2);
		double bi = (180.0+vizUtils.findBearing_2(p1, p0))%360.0;
		
		System.err.println("     bf: " + bf + "     bi: " + bi);
		
		return _getBearingDelta(bi,bf);
	}

	@Override
	public int getDays() {
		return N;
	}
	
	double minSample(double l, double c, double r, double support){
		return c + support*(Math.min(l,r) - c);
		//return c + support*(l - c);
	}
	double maxSample(double l, double c, double r, double support){
		return c + support*(Math.max(l,r) - c);
		//return c + support*(r - c);
	}
	
	public void printBearingPDFs(){
		for(int i=0;i<bearingPDFs.size();++i)
			System.err.println(
			   "minB: "   +bearingPDFs.get(i).min   +" ; "+
			   "centerB: "+bearingPDFs.get(i).center+" ; "+
			   "maxB: "   +bearingPDFs.get(i).max
					          );
	}

	public void printSpeedPDFs(){
		for(int i=0;i<speedPDFs.size();++i)
			System.err.println(
			   "minS: "   +speedPDFs.get(i).min   +" ; "+
			   "centerS: "+speedPDFs.get(i).center+" ; "+
			   "maxS: "   +speedPDFs.get(i).max
					          );
	}
	
	private void swap(double[] ar, int a, int b){
		double temp = ar[a];
		ar[a]=ar[b];
		ar[b]=temp;
	}
	public double[] sanitizePDF_Params(double m, double c, double M){
		double[] ar = {m,c,M};
		if( ar[1] < ar[0] ) swap(ar,0,1);
		if( ar[1] > ar[2] ) swap(ar,1,2);
		if( ar[1] < ar[0] ) swap(ar,0,1);
		return ar;
	}

	public predictedPaths(Advisory nadv){
		adv = nadv;
		
		N = adv.pathdata.size()-1;
		bearingPDFs = new Vector<pdf>(N);
		bearingPDFs.setSize(N);
		speedPDFs = new Vector<pdf>(N);
		speedPDFs.setSize(N);
		
		{
			double bear0  = adv.Bearing0;
			//double bear0  = adv.getBear(0);
			double speed0 = adv.Speed0;
			//double speed0 = adv.getSpeed(0);
			//double c_b = _getBearingDelta(bear0=adv.getBear(0),vizUtils.findBearing_2(adv.getPos(0), adv.getPos(1)));
			double c_b = _getBearingDelta(bear0,vizUtils.findBearing_2(adv.getPos(0), adv.getPos(1)));
			double bL  = _getBearingDelta(bear0,vizUtils.findBearing_2(adv.getLeft(0),adv.getLeft(1)));
			double bR  = _getBearingDelta(bear0,vizUtils.findBearing_2(adv.getRight(0),adv.getRight(1)));
			//double m_b = c_b + supportBearing()*(Math.min(bL,bR) - c_b);
			//double M_b = c_b + supportBearing()*(Math.max(bL,bR) - c_b);
			double m_b = minSample(bL,c_b,bR,supportBearing());
			double M_b = maxSample(bL,c_b,bR,supportBearing());
			
			//System.err.println("Bear PDF 0 - Min: " + m_b + "   Cen: " + c_b + "   Max: " + M_b);
			
            bearingPDFs.set(0,new pdf(m_b,M_b,c_b));
            //bearingPDFs.get(0).Display();
            //System.exit(0);
            //bearingPDFs.get(0).Display();
			//System.err.println("BEAR 0: " + adv.getBear(0));
			//double c_s = (vizUtils.haversine(adv.getPos(0), adv.getPos(1))/adv.hoursInSeg())-(speed0=adv.getSpeed(0));
			double c_s = (vizUtils.haversine(adv.getPos(0), adv.getPos(1))/adv.hoursInSeg())-(speed0);
			double sL  = (vizUtils.haversine(adv.getLeft(0), adv.getLeft(1))/adv.hoursInSeg())-speed0;
			double sR  = (vizUtils.haversine(adv.getRight(0), adv.getRight(1))/adv.hoursInSeg())-speed0;
			//double m_s = c_s + supportSpeed()*(Math.min(sL,sR) - c_s);
			//double M_s = c_s + supportSpeed()*(Math.max(sL,sR) - c_s);
			double m_s = minSample(sL,c_s,sR,supportSpeed());
			double M_s = maxSample(sL,c_s,sR,supportSpeed());
			
			System.err.println("Speed PDF 0 - Min: " + m_s + "   Cen: " + c_s + "   Max: " + M_s);
			/*{
			   double[] san = sanitizePDF_Params(m_s,c_s,M_s);
			   m_s = san[0];
			   c_s = san[1];
			   M_s = san[2];
			}*/
			
			speedPDFs.set(0,new pdf(m_s,M_s,c_s));
			
			System.err.println("Speed PDF 0 - Min: " + m_s + "   Cen: " + c_s + "   Max: " + M_s);
			
			//speedPDFs.get(0).Display(); System.exit(0);
		}
		
        //for(int i=0; i < N; ++i){
        for(int i=1; i < N; ++i){
			
			int iprev = Math.max(0,i-1);
			int inext = i+1;
			
			//double c_b = getBearingDelta(adv.getPos(iprev), adv.getPos(i), adv.getPos(inext));
			double c_b = adv.getBear(i);
			
			double bL  = getBearingDelta(adv.getLeft(iprev), adv.getLeft(i), adv.getLeft(inext));
			double bR  = getBearingDelta(adv.getRight(iprev),adv.getRight(i),adv.getRight(inext));
			/*if(i == 15 || i == 16){
			  System.err.println("Left   prev: " + adv.getLeft(iprev) + "  cur: " + adv.getLeft(i) + "   next: " + adv.getLeft(inext));
			  System.err.println("Right  prev: " + adv.getRight(iprev) + "  cur: " + adv.getRight(i) + "   next: " + adv.getRight(inext));
			  System.err.println("bL: " + bL + "   bR: " + bR);
			}*/

			//double m_b = c_b + supportBearing()*(Math.min(bL,bR) - c_b);
			//double M_b = c_b + supportBearing()*(Math.max(bL,bR) - c_b);
			double m_b = minSample(bL,c_b,bR,supportBearing());
			double M_b = maxSample(bL,c_b,bR,supportBearing());
			
			//System.err.println("Bear PDF " + i + " - Min: " + m_b + "   Cen: " + c_b + "   Max: " + M_b);
            bearingPDFs.set(i,new pdf(m_b,M_b,c_b));
            
			//double c_s = getSpeedDelta(adv.getPos(iprev),adv.getPos(i),adv.getPos(inext),adv.hoursInSeg(iprev),adv.hoursInSeg(i));
			//double sL  = getSpeedDelta(adv.getLeft(iprev),adv.getLeft(i),adv.getLeft(inext),adv.hoursInSeg(iprev),adv.hoursInSeg(i));
			//double sR  = getSpeedDelta(adv.getRight(iprev),adv.getRight(i),adv.getRight(inext),adv.hoursInSeg(iprev),adv.hoursInSeg(i));
			//double c_s = getSpeedDelta(adv.getPos(iprev),adv.getPos(i),adv.getPos(inext),adv.hoursInSeg(),adv.hoursInSeg());
            double c_s = adv.getSpeed(i);
			double sL  = getSpeedDelta(adv.getLeft(iprev),adv.getLeft(i),adv.getLeft(inext),adv.hoursInSeg(),adv.hoursInSeg());
			double sR  = getSpeedDelta(adv.getRight(iprev),adv.getRight(i),adv.getRight(inext),adv.hoursInSeg(),adv.hoursInSeg());
			
			//double m_s = c_s + supportSpeed()*(Math.min(sL,sR) - c_s);
			//double M_s = c_s + supportSpeed()*(Math.max(sL,sR) - c_s);
			double m_s = minSample(sL,c_s,sR,supportSpeed());
			double M_s = maxSample(sL,c_s,sR,supportSpeed());
			
			/*{
				   double[] san = sanitizePDF_Params(m_s,c_s,M_s);
				   m_s = san[0];
				   c_s = san[1];
				   M_s = san[2];
			}*/
			System.err.println("Speed PDF " + i + " - Min: " + m_s + "   Cen: " + c_s + "   Max: " + M_s);
			speedPDFs.set(i,new pdf(m_s,M_s,c_s));
	   }
	   printBearingPDFs();
	   printSpeedPDFs();
   }
}
