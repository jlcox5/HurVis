import java.util.Date;
import java.util.Random;
import java.util.Vector;


public class pdf {
	public double min,max,center,dx,dX;
	
	public double[] probabilities;
	public double[] areas;
	
	private static double C0 = 1/(Math.sqrt(2*Math.PI));
	public double estimator(double x, double c, double s){
		double invS = 1.0/s; 
		//invS=badNum(invS)?0.0:invS;
		double invS2 = invS*invS;
		double expPow = -0.5*(x-c)*(x-c)*invS2;
		//return expPow==0.0?invS*C0:invS*C0*Math.exp(expPow);
		return invS*C0*Math.exp(expPow);
	}
	double trapezoidal(double a, double b, double dt){
		//a = Math.abs(a); b = Math.abs(b); dt = Math.abs(dt);
		//dt = Math.abs(dt);
		double area = 0.5*dt*(b+a);
		return area;
	}
	
	public double value(int i){
		return i<=50?min+((double)i)*dx:center+((double)i)*dX;
	}
	
	public double interp(int i, double t){
		return (1.0-t)*value(i)+t*value(i+1);
	}
	
	private void mkProbs(){
		
		double stddev_lte = Math.abs(center-min)/3.0;
		double stddev_gt = Math.abs(max-center)/3.0;
		//System.err.println("dx,dX = " + dx+","+dX);
		
		for(int i=0; i < 50; ++i){
			double dubi = (double)i;
			double val  = min+dubi*dx;
			//double val = vizUtils.addBearing(min, dubi*dx);
			
		    probabilities[i]=estimator(val,center,stddev_lte);
		}
		probabilities[50]=estimator(center,center,stddev_lte);
		for(int i=51; i < 101; ++i){
			double dubi = (double)(i-51);
			double val  = center+dubi*dX;
			//double val = vizUtils.addBearing(center, dubi*dX);
				
			probabilities[i]=estimator(val,center,stddev_gt);
		}
		
		//for(int i=0; i < probabilities.length; ++i) if( probabilities[i] < 0.0 ) System.err.println("BAD");
	}
	private void mkAreas(){
		double left,right = probabilities[0];
		double h = Math.abs(dx);
		for(int i=0; i < 50; ++i){
			left = right;
			right=probabilities[i+1];
			areas[i] = trapezoidal(left,right,h);
		}
		h = Math.abs(dX);
		for(int i=50; i < 100; ++i){
			left = right;
			right=probabilities[i+1];
			areas[i] = trapezoidal(left,right,h);
		}
	}
	private void mkNormal(){
		double sum=0.0;
		for(int i=0; i < 100; ++i) sum += areas[i];
		double inv = 1.0/sum;
		
		for(int i=0; i < 100; ++i){
			probabilities[i] *= inv;
			areas[i] *= inv;
		}
		
		//sum=0.0;
		//for(int i=0; i < 100; ++i) sum += areas[i];
	}
	
	public void Display(){
		System.err.println("AREAS:");
		System.err.println("_____________________________");
		for(int i=0; i < 100; ++i) System.err.println((i==50)?"[+]"+areas[i]:areas[i]);
		System.err.println("_____________________________");
		System.err.println("PROBS:");
		System.err.println("_____________________________");
		for(int i=0; i < 100; ++i) System.err.println((i==50)?"[+]"+probabilities[i]:probabilities[i]);
		System.err.println("_____________________________");
	}
	
	public pdf(double nmin, double nmax, double ncenter){
		min=nmin;
		max=nmax;
		
		if(max<min){
			double temp=max;
			max=min;
			min=temp;
		}
		center=ncenter;
		
		probabilities = new double[101];
		areas         = new double[101];
		
		dx = (center-min)/50.0;
		dX = (max-center)/50.0;
		
		mkProbs();
		mkAreas();
		mkNormal();
	}
	
	public double generate(Random rand){
		double r = rand.nextDouble();
		double t=0,tn=areas[0];
		int i=0;
		while(tn<r && i < 100){
			t = tn;
			tn += areas[++i];
		}
		
		t = (r-t)/(tn-t);
		
		return interp(i,t);
	}
}
