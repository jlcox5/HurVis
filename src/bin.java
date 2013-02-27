
import java.util.Vector;
import java.math.*;

public class bin {
	
   public static double[] errorRadius;
   public static double   userSig;
   public static int      preOnly;
   
   //-------------------------------
	
   private double wB, wS
                , hB, hS;
   private double maxB, minB
                , maxS, minS;
   private int     samples;
   private boolean hasPrePath;
   
   private double areaB, areaS;
   
   vec midPoint;
   
   public Vector<gridPoint> b;
   public Vector<Double> predStartTime
                       , predFinishTime
                       , bearF
                       , bearPos
                       , bearDistFunc
                       , speedF
                       , speedPos
                       , speedDistFunc;
   
   public Vector< Vector<Double> > preSegDist;
   
   public bin(int minLat, int minLon){
	   float flat = minLat;
	   float flon = minLon;
	   hasPrePath = false;
	   midPoint   = vec.vec2(flon - 0.5f, flat + 0.5f);
   }
   
   public void resolve(){
   }
   
   public double findKB(double x){
	   if( hB == 0.0 ) return 0.0;
	   
	   return Math.exp( -1.0 * (x*x) / (2.0*hB*hB) ) 
			/ (hB*Math.sqrt(2.0*Math.PI));
   }

   public double findKS(double x){
	   if( hS == 0.0 ) return 0.0;
	   
	   return Math.exp( -1.0 * (x*x) / (2.0*hS*hS) ) 
			/ (hS*Math.sqrt(2.0*Math.PI));
   }
   
   public void genBearDelta(){
   }
   
   public void genSpeedDelta(){
   }
   
   public void computeAreaB(){
   }
   
   public void computeAreaS(){
	   double aV, bV
	        , n, sum;
	   
	   n = speedF.size();
	   
	   int _d;
	   aV = minS;
	   sum = speedF.get(0);
	   
	   for(_d = 1; _d < speedF.size(); ++_d){
		   sum += 2.0*speedF.get(_d);
	   }
	   
	   bV = maxS;
	   
	   areaS = n==0.0 ? 0.0 : ((bV-aV)/(2.0*n))*sum;
	   
   }
   
   public double getProbBear(double p, int posChanged, double min, boolean findPre){
	   int _d;
	   double newP = p*areaB;
	   double aV, bV, k, n, testD
	        , sum, prevSum
	        , rangeL, rangeT
	        , areaDif, areaRatio
	        , mod
	        , chosen, preBear;
	   
	   int r,q;
	   
	   k = 0;
	   n = bearF.size();
	   aV = bearF.get(0);
	   bV = bearF.get(bearF.size()-1);
	   
	   sum = prevSum = 0.0;
	   
	   for(_d=0; _d < bearDistFunc.size() ; ++_d){
		   prevSum = sum;
		   
		   sum += (testD=(n==0.0?0.0:bearDistFunc.get(_d)));
		   
		   if(sum > p) break;
		   
		   ++k;
	   }
	   
	   areaDif = sum - prevSum;
	   areaRatio = sum - newP;
	   
	   if(areaDif != 0.0){
		   areaRatio /= areaDif;
	   }else{
		   areaRatio = 0.0;
	   }
	   
	   rangeL = bearPos.get(((int)k));
	   rangeT = bearPos.get(((int)k)+1);
	   
	   rangeT -= rangeL;
	   mod = (rangeT < 0.0) ? -1.0 : 1.0;
	   
	   rangeL += rangeT*areaRatio;
	   
	   if(findPre){
		   ERROR_FINISH_ME();
	   }
	   
	   return rangeL;
   }
   
   public double getProbSpeed(double p, int posChanged, double min, boolean findPre){
	   int _d;
	   double newP = p*areaS;
	   double aV, bV, k, n, testD
	        , sum, prevSum
	        , rangeL, rangeT
	        , areaDif, areaRatio
	        , mod
	        , chosen, preSpeed;
	   
	   int r,q;
	   
	   k = 0;
	   n = speedF.size();
	   aV = speedF.get(0);
	   bV = speedF.get(speedF.size()-1);
	   
	   sum = prevSum = 0.0;
	   
	   for(_d=0; _d < speedDistFunc.size() ; ++_d){
		   prevSum = sum;
		   
		   sum += (testD=(n==0.0?0.0:speedDistFunc.get(_d)));
		   
		   if(sum > p) break;
		   
		   ++k;
	   }
	   
	   areaDif = sum - prevSum;
	   areaRatio = sum - newP;
	   
	   if(areaDif != 0.0){
		   areaRatio /= areaDif;
	   }else{
		   areaRatio = 0.0;
	   }
	   
	   rangeL = speedPos.get(((int)k));
	   rangeT = speedPos.get(((int)k)+1);
	   
	   rangeT -= rangeL;
	   mod = (rangeT < 0.0) ? -1.0 : 1.0;
	   
	   rangeL += rangeT*areaRatio;
	   
	   if(findPre){
		   ERROR_FINISH_ME();
	   }
	   
	   return rangeL;
   }
   
   public void buildSegDist(int curAdv){
	   ERROR_FINISH_ME();
   }
   
   public double findOmega(double min, int curAdvisory){
	   
	   if( preOnly > 0 ){
		   return 1.0;
	   }
	   
	   int bin = (int)Math.floor(min/((69.0/(preSegDist.get(curAdvisory).size()-1.0)*60)));
	   
	   double dx, rho, toRet;
	   
	   dx  = preSegDist.get(curAdvisory).get(bin);
	   rho = findRho(min);
	   
	   if( dx < 100.0 ){
		   dx = 1.0;
	   }
	   
	   toRet = Math.exp((-1.0*dx*dx)/(2.0*(rho*rho*userSig*userSig)));
	   
	   int i = 0;
	   
	   return toRet;
   }
   
   public double findRho(double min){
	   double t = 3.0 + min/60.0;
	   int i;
	   double subt, setNHS, f;
	   double intRad;
	   double rho;
	   
	   if(       t <  9  ){
		   i      = 0;
		   subt   = 0;
		   setNHS = 9;
	   }else if( t <= 21 ){
		   i      = 1;
		   subt   = 9;
		   setNHS = 12;
	   }else if( t <= 33 ){
		   i      = 2;
		   subt   = 21;
		   setNHS = 12;
	   }else if( t <= 45 ){
		   i      = 3;
		   subt   = 33;
		   setNHS = 12;
	   }else              {
		   i      = 4;
		   subt   = 45;
		   setNHS = 24;
	   }
	   
	   f = (t-subt)/setNHS;
	   
	   if( t != 0 ){
		   intRad = f*errorRadius[i+1] + (1.0-f)*errorRadius[i];
	   }else{
		   intRad = errorRadius[i];
	   }
	   
	   rho = intRad/errorRadius[5];
	   
	   if(rho == 0.0){
		   rho = 0.0001;
	   }
	   
	   return rho;
   }
}
