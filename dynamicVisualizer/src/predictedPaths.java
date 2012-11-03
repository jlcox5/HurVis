import java.util.Date;
import java.util.Vector;
import java.util.Random;


public class predictedPaths implements pathStrategy{
	
	Advisory adv;
	
	public Vector< Vector<Double> > bearingPredictor;
	public Vector< Vector<Double> > bPredAreas;
	public Vector< Vector<Double> > speedPredictor;
	public Vector< Vector<Double> > sPredAreas;
	
	public int N;
	//public int getDays(){return N;}
	public int getDays(){return Math.min(5,N);}
	
	private static double C0 = 1/(Math.sqrt(2*Math.PI));
	
	public double estimator(double x, double c, double s){
		double invS = 1.0/s;
		double invS2 = invS*invS;
		return invS*C0*Math.exp(-0.5*(x-c)*(x-c)*invS2);
	}
	
	public double minDiff(double a_i, double a_f){
		return a_f>=a_i?a_f-a_i:a_f+360.0-a_i;
	}
	
	double getBearingDelta(vec p0, vec p1, vec p2){
		double bi = 180.0+vizUtils.findBearing_2(p1, p0);
		double bf = vizUtils.findBearing_2(p1, p2);
		
		return (bf - bi) % 360.0;
	}
	
	double trapezoidalIntegrator(double a, double b, double dt){
		dt = Math.abs(dt);
		if(b<a){
			double temp = a;
			a=b;
			b=temp;
		}
		return 0.5*dt*(b+a);
	}
	
	double getSpeedDelta(vec p0, vec p1, vec p2, double dt0, double dt1){
		//double bf = (1.0/dt1)*p2.sub(p1).norm();
		//double bi = (1.0/dt0)*p1.sub(p0).norm();
		
		double sf = vizUtils.haversine(p1,p2)/dt1;
		double si = vizUtils.haversine(p0, p1)/dt0;
		
		return sf-si;
	}
	
	public predictedPaths(Advisory nadv){
		adv = nadv;
		N = adv.pathdata.size()-1;
		//N=1;
		
		bearingPredictor = new Vector< Vector<Double> >(N);
		bearingPredictor.setSize(N);
		for(int i=0; i < N; ++i){
			bearingPredictor.set(i, new Vector<Double>(101));
			bearingPredictor.get(i).setSize(101);
		}
		speedPredictor   = new Vector< Vector<Double> >(N);
		speedPredictor.setSize(N);
		for(int i=0; i < N; ++i){
			speedPredictor.set(i, new Vector<Double>(101));
			speedPredictor.get(i).setSize(101);
		}
		bPredAreas = new Vector< Vector<Double> >(N);
		bPredAreas.setSize(N);
		for(int i=0; i < N; ++i){
			bPredAreas.set(i, new Vector<Double>(100));
			bPredAreas.get(i).setSize(100);
		}
		sPredAreas = new Vector< Vector<Double> >(N);
		sPredAreas.setSize(N);
		for(int i=0; i < N; ++i){
			sPredAreas.set(i, new Vector<Double>(100));
			sPredAreas.get(i).setSize(100);
		}
		
		//DO FIRST POINT
		
		for(int i=0; i < N; ++i){
			
			int iprev = Math.max(0,i-1);
			int inext = i+1;
			
			//BEARING TIME!
			
			double c_b = getBearingDelta(adv.getPos(iprev), adv.getPos(i), adv.getPos(inext));
			
			double bL  = getBearingDelta(adv.getLeft(iprev), adv.getLeft(i), adv.getLeft(inext));
			double bR  = getBearingDelta(adv.getRight(iprev),adv.getRight(i),adv.getRight(inext));
			
			System.err.println(""+c_b+" "+bL+" "+bR);
			
			double m_b = c_b + 1.5*(Math.min(bL,bR) - c_b);
			double M_b = c_b + 1.5*(Math.max(bL,bR) - c_b);
			
			//double stddev = m_b < c_b ? (c_b-m_b/3.0) : (M_b-c_b/3.0);
			double stddev = (c_b-m_b/3.0);
			double dj = (c_b-m_b)/50.0;
			
			double left,right;
			bearingPredictor.get(i).set(0,left=estimator(m_b,c_b,stddev));
			for(int j = 1; j < 50; ++j){
				double J = (double)j;
				double sample_j = m_b+J*dj;
				
				bearingPredictor.get(i).set(j,right=estimator(sample_j,c_b,stddev));
				bPredAreas.get(i).set(j-1,trapezoidalIntegrator(left,right,dj));
				
				left = right;
			}
			
			bearingPredictor.get(i).set(50,right=estimator(c_b,c_b,stddev));
			bPredAreas.get(i).set(49,trapezoidalIntegrator(left,right,dj));
			
			left = right;
			
			//stddev = M_b < c_b ? (c_b-m_b/3.0) : (M_b-c_b/3.0);
			stddev = (M_b-c_b/3.0);
			dj = (M_b-c_b)/50.0;
			for(int j = 51; j < 101; ++j){
				double J = (double)(j-50);
				double sample_j = c_b+J*dj;
				
				bearingPredictor.get(i).set(j, right=estimator(sample_j,c_b,stddev));
				bPredAreas.get(i).set(j-1,trapezoidalIntegrator(left,right,dj));
				
				left = right;
			}
			
			//SPEED TIME!
			
			double c_s = getSpeedDelta(adv.getPos(iprev),adv.getPos(i),adv.getPos(inext),adv.hoursInSeg(iprev),adv.hoursInSeg(i));
			double sL  = getSpeedDelta(adv.getLeft(iprev),adv.getLeft(i),adv.getLeft(inext),adv.hoursInSeg(iprev),adv.hoursInSeg(i));
			double sR  = getSpeedDelta(adv.getRight(iprev),adv.getRight(i),adv.getRight(inext),adv.hoursInSeg(iprev),adv.hoursInSeg(i));
			
			double m_s = c_s + 3.0*(Math.min(sL,sR) - c_s);
			double M_s = c_s + 3.0*(Math.max(sL,sR) - c_s);
			
			//stddev = M_s < c_s ? (c_s-m_s/3.0) : (M_s-c_s/3.0);
			stddev = (c_s-m_s/3.0);
			dj = (c_s-m_s)/50.0;
			speedPredictor.get(i).set(0,left=estimator(m_s,c_s,stddev));
			for(int j=1; j < 50; ++j){
				double J = (double)j;
				double sample_j = m_s+J*dj;
				
				speedPredictor.get(i).set(j,right=estimator(sample_j,c_s,stddev));
				sPredAreas.get(i).set(j-1, trapezoidalIntegrator(left,right,dj));
				
				left = right;
			}
			
			speedPredictor.get(i).set(50,right=estimator(c_s,c_s,stddev));
			sPredAreas.get(i).set(49, trapezoidalIntegrator(left,right,dj));
			
			left = right;
			
			//stddev = M_s < c_s ? (c_s-m_s/3.0) : (M_s-c_s/3.0);
			stddev = (M_s-c_s/3.0);
			dj = (M_s-c_s)/50.0;
			for(int j = 51; j < 101; ++j){
				double J = (double)j;
				double sample_j = c_s+J*dj;
				
				speedPredictor.get(i).set(j, right=estimator(sample_j,c_s,stddev));
				sPredAreas.get(i).set(j-1, trapezoidalIntegrator(left,right,dj));
				
				left = right;
			}
		}
		
		// FILL IN
		
		for(int i=0; i < N; ++i){			
			double ssum = 0.0;
			double bsum = 0.0;
			for(int j=0; j < 100; ++j){
				ssum += sPredAreas.get(i).get(j);
				bsum += bPredAreas.get(i).get(j);
			}
			double invsnorm = 1.0/ssum;
			double invbnorm = 1.0/bsum;
			double temp;
			for(int j=0; j < 100; ++j){
				temp = sPredAreas.get(i).get(j);
				sPredAreas.get(i).set(j,temp*invsnorm);
				temp = bPredAreas.get(i).get(j);
				bPredAreas.get(i).set(j,temp*invbnorm);
			}
		}

	}
	
	private Random rand = new Random((new Date()).getTime());
	private double nextDelta(Vector<Double> areas,Vector<Double> values){
		double r = rand.nextDouble();
		double t=0,tn=areas.get(0);
		int i=0;
		while(tn<r){
			t = tn;
			tn += areas.get(++i);
		}
		t = (r-t)/(tn-t);
		
		return (1.0-t)*values.get(i)+t*values.get(i+1);
	}
	
	@Override
	public vec genDeltas(vec x, int day) {
	   vec ret = vec.vec2(nextDelta(bPredAreas.get(day),bearingPredictor.get(day))
			             ,nextDelta(sPredAreas.get(day),speedPredictor.get(day)));
	   System.err.println(ret);
	   return ret;
	}

}
