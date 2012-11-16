import java.util.Random;
import java.util.Vector;


public class predictedPaths implements pathStrategy {

	Advisory adv;
	
	//double supportSpeed = 3.0;
	//double supportBearing = 1.5;
	double supportSpeed = 0.1;
	double supportBearing = 0.0001;
	
	Vector<pdf> bearingPDFs;
	Vector<pdf> speedPDFs;
	
	public int N;
	

	Random rand = new Random();	
	@Override
	public vec genDeltas(vec x, int day) {
		return vec.vec2(bearingPDFs.get(day).generate(rand)
				       ,speedPDFs.get(day).generate(rand));
	}
	
	
	public double minDiff(double a_i, double a_f){
		return a_f>=a_i?a_f-a_i:a_f+360.0-a_i;
	}
	
	double getSpeedDelta(vec p0, vec p1, vec p2, double dt0, double dt1){
		double sf = vizUtils.haversine(p1,p2)/dt1;
		double si = vizUtils.haversine(p0, p1)/dt0;
		
		return sf-si;
	}
	
	double getBearingDelta(vec p0, vec p1, vec p2){
		double bi = 180.0+vizUtils.findBearing_2(p1, p0);
		double bf = vizUtils.findBearing_2(p1, p2);
		
		double bdel = bf-bi;
		double bsign = Math.signum(bdel);
		return bsign*(Math.abs(bdel)%360.0);
	}

	@Override
	public int getDays() {
		return N;
	}

	public predictedPaths(Advisory nadv){
		adv = nadv;
		
		N = adv.pathdata.size()-1;
		bearingPDFs = new Vector<pdf>(N);
		bearingPDFs.setSize(N);
		speedPDFs = new Vector<pdf>(N);
		speedPDFs.setSize(N);
		
        for(int i=0; i < N; ++i){
			
			int iprev = Math.max(0,i-1);
			int inext = i+1;
			
			double c_b = getBearingDelta(adv.getPos(iprev), adv.getPos(i), adv.getPos(inext));
			
			double bL  = getBearingDelta(adv.getLeft(iprev), adv.getLeft(i), adv.getLeft(inext));
			double bR  = getBearingDelta(adv.getRight(iprev),adv.getRight(i),adv.getRight(inext));
			
			double m_b = c_b + supportBearing*(Math.min(bL,bR) - c_b);
			double M_b = c_b + supportBearing*(Math.max(bL,bR) - c_b);
			
            bearingPDFs.set(i,new pdf(m_b,M_b,c_b));
            
			double c_s = getSpeedDelta(adv.getPos(iprev),adv.getPos(i),adv.getPos(inext),adv.hoursInSeg(iprev),adv.hoursInSeg(i));
			double sL  = getSpeedDelta(adv.getLeft(iprev),adv.getLeft(i),adv.getLeft(inext),adv.hoursInSeg(iprev),adv.hoursInSeg(i));
			double sR  = getSpeedDelta(adv.getRight(iprev),adv.getRight(i),adv.getRight(inext),adv.hoursInSeg(iprev),adv.hoursInSeg(i));
			
			double m_s = c_s + supportSpeed*(Math.min(sL,sR) - c_s);
			double M_s = c_s + supportSpeed*(Math.max(sL,sR) - c_s);
			
			speedPDFs.set(i,new pdf(m_s,M_s,c_s));
	   }
   }
}
