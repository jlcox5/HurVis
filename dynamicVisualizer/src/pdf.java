import java.util.Date;
import java.util.Random;
import java.util.Vector;


public class pdf {
	public double min,max,center,dx,dX,area;
	
	public double[] probabilities;
	public double[] areas;
	
	private static double C0 = 1/(Math.sqrt(2*Math.PI));
	public double estimator(double x, double c, double s){
		double invS = 1.0/s; 
		//invS=badNum(invS)?0.0:invS;
		//double invS2 = invS*invS;
		double expPowNum = -1*(x-c)*(x-c);
		double expPow = expPowNum/(2*s*s);
		return expPow==0.0?0:invS*C0*Math.exp(expPow);
	}
	double trapezoidal(double a, double b, double dt){
		//a = Math.abs(a); b = Math.abs(b); dt = Math.abs(dt);
		//dt = Math.abs(dt);
		double area = 0.5*dt*(b+a);
		return area;
	}
	
	public double value(int i){
		return i<=50?min+((double)i/50)*dx:center+((double)(100-i)/50)*dX;
	}
	
	public double interp(int i, double t){
		//System.err.println("    i: " + i + "    min val: " + value(i) +  "   max val: " + value(i+1));
		return (1.0-t)*value(i)+t*value(i+1);
	}
	
	private void mkProbs(){
		
		//System.err.println(":::"+min+":::"+max+":::");
		
		double stddev_lte = Math.abs(center-min)/3.0;
		double stddev_gt = Math.abs(max-center)/3.0;
		//System.err.println("dx,dX = " + dx+","+dX);
		
		for(int i=0; i < 50; ++i){
			double dubi = (double)i/50;
			double val  = min+dubi*dx;
			//double val = vizUtils.addBearing(min, dubi*dx);
			double stddev = stddev_lte;
			
			//System.err.println("dubi: " + dubi + "   val: " + val + "   stddev: " + stddev + "   center " + center);
			probabilities[i]=estimator(val,center,stddev);
		}
		//probabilities[50]=estimator(center,center,stddev_lte);
		for(int i=50; i < 101; ++i){
			double dubi = 1 - (double)(100-i-1)/50;
			double val  = center+dubi*dX;
			
			double stddev = stddev_gt;
			
			//System.err.println("dubi: " + dubi + "   val: " + val + "   stddev: " + stddev + "   center " + center);
			probabilities[i]=estimator(val,center,stddev);
		}
		
		//for(int i=0; i < probabilities.length; i++){ 
		//	System.err.println("i: " + i + "   val: " + probabilities[i]);
		//}
	}
	private void mkAreas(){
		double left,right = probabilities[0];
		/*double h = dx;
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
		}*/
		
		double sum;
		
		sum = probabilities[0];
		for(int i=1; i <probabilities.length-1; i++){
			sum += 2*probabilities[i];
		}
		sum += probabilities[probabilities.length-1];
		
		area = ((max-min)/(2*probabilities.length))*sum;
		
		//System.err.println("Pre area: " + area);
		
		// Normalize probabilities
		for(int i=0; i<probabilities.length; i++){
			probabilities[i] = probabilities[i]*(1.0/area);
		}
		
		// Compute and save areas for individual trapezoids
		double aV, bV, fa, fb, a;
		double k=1;
		for(int i=1; i<probabilities.length; i++){
			aV = min + ((k-1)/probabilities.length)*(max - min);
			bV = min + (k/probabilities.length)*(max - min);
			fa = probabilities[i];
			fb = probabilities[i-1];
			a = (bV-aV)*((fa+fb)/2);
			areas[i-1] = a;
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
		area = -1.0;
		
		probabilities = new double[101];
		areas         = new double[101];
		
		dx = (center-min);///50.0;
		dX = (max-center);///50.0;
		
		//System.err.println("dx: " + dx + "      dX: " + dX);
		
		mkProbs();
		mkAreas();
		//mkNormal();
		
	}
	
	static int count = 5;
	public double generate(Random rand){
		if(count-- > 0) System.err.println(area);
		//System.err.println(count++);
		double r = rand.nextDouble();
		double t=0,tn=areas[0];
		int i=0;
		while(tn<r && i < 99){
			t = tn;
			tn += areas[i++];
		}

		t = (r-t)/(tn-t);
		
		//System.err.println(t+" "+tn+" "+r);
		
		//System.err.println("t: " + t);
		
		double dB = interp(i,t);
		//System.err.println("Bearing change: " + dB);
		return dB;
		//return interp(i,t);
	}
}
