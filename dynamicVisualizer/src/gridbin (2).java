import java.util.ArrayList;
import java.util.List;
import java.math.*;


public class gridbin implements bin {
   ArrayList<gridPoint> pointlist = new ArrayList<gridPoint>();
   
   public double[] predStartTime;
   public double[] predFinishTime;
   public double[] bearF;
   public double[] speedF;
   public double[] bearPos;
   public double[] speedPos;
   public double[] bearDistFunc;
   public double[] speedDistFunc;   
   
   double wB, wS;
   double hB, hS;
   double maxB, minB;
   double maxS, minS;
   int samples=11;
   boolean hasPrePath;
   double areaB, areaS;
   
   int N=0;

   
   public gridbin(){
   }
   
   public void add(gridPoint gp){
	   pointlist.add(gp);
	   ++N;
   }
   
   public double genBearingDelta(double r){
	   if(N==0) return 0.0;
	   if(N==1) return bearPos[0]*3.0;
	   
	   int k=0;
	   
	   double rangeL,rangeT,newR = r*areaB;
	   double prevsum = 0.0;
	   double sum = 0.0;
	   
	   int N = bearDistFunc==null?0:bearDistFunc.length;
	   
	   while( sum < r && k<N-1){
		   prevsum = sum;
		   sum += bearDistFunc[k++];
	   }
	   k=Math.max(k-1,0);
	   
	   double areadif = sum - prevsum;
	   double arearatio = sum - newR;
	   
	   arearatio = (areadif != 0.0 && !Double.isNaN(areadif)) ? arearatio/areadif : 0.0;

	   rangeL = bearPos[ k ];
	   rangeT = bearPos[k+1] - rangeL;
	   
	   rangeL = (rangeL+rangeT*arearatio)%360.0;
	   
	   return 3.0*rangeL;
   }
   
   public double genSpeedDelta(double r){
	   if(N==0) return 0.0;
	   if(N==1) return speedPos[0]*3.0;
	   
	   int k=0;
	   
	   double rangeL,rangeT,newR = r*areaS;
	   double prevsum = 0.0;
	   double sum = 0.0;
	   
	   while( sum < r && k < speedDistFunc.length-1){
		   prevsum = sum;
		   sum += speedDistFunc[k++];
	   }
	   k=Math.min(k-1,0);
	   
	   double areadif = sum - prevsum;
	   double arearatio = sum - newR;
	   
	   arearatio = (areadif != 0.0 && !Double.isNaN(areadif)) ? arearatio/areadif : 0.0;
	   
	   rangeL = speedPos[k  ];
	   rangeT = speedPos[k+1] - rangeL;
	   
	   rangeL = (rangeL+rangeT*arearatio)%360.0;
	   
	   return 3.0*rangeL;
   }
   
   public void resolve(){
	   double meanB,meanS,tSum,uSum;
	   double k = 1.0;
	   
	   if(pointlist.size() == 0) return;
	   
	   //meanB=pointlist.get(0).bdel;
	   //meanS=pointlist.get(0).sdel;
	   meanB=meanS=0.0;
	   
	   maxB = minB = pointlist.get(0).bdel;
	   maxS = minS = pointlist.get(0).sdel;
	   
	   for(int i=1; i < pointlist.size(); ++i){
		   maxB = Math.max(maxB, pointlist.get(i).bdel);
		   minB = Math.min(maxB, pointlist.get(i).bdel);
		   maxS = Math.max(maxS, pointlist.get(i).sdel);
		   minS = Math.min(maxS, pointlist.get(i).sdel);
		   
		   meanB += pointlist.get(i).bdel;
		   meanS += pointlist.get(i).sdel;
	   }
	   meanB /= (double)pointlist.size();
	   meanS /= (double)pointlist.size();
	   
	   tSum=0.0;
	   uSum=0.0;
	   
	   for(int i=1; i < pointlist.size(); ++i){
		   tSum += Math.pow(pointlist.get(i).bdel-meanB,2.0);
		   uSum += Math.pow(pointlist.get(i).sdel-meanS,2.0);
	   }
	   
	   tSum *= 1.0/((double)pointlist.size()-1.0);
	   uSum *= 1.0/((double)pointlist.size()-1.0);
	   
	   wB = maxB-minB;
	   wS = maxS-minS;
	   
	   if(wB > 0.0) hB = k*Math.sqrt(tSum);
	   else hB = 0.0;
	   
	   if(wS > 0.0) hS = k*Math.sqrt(uSum);
	   else hS =0.0;
	   
	   minB -= 3.0*hB;
	   maxB += 3.0*hB;
	   minS -= 3.0*hS;
	   maxS += 3.0*hS;
	   
	   int size = pointlist.size();
	   predStartTime  = new double[samples];
	   predFinishTime = new double[samples];
	   bearDistFunc   = new double[samples];
	   speedDistFunc  = new double[samples];
	   bearF          = new double[samples];
	   speedF         = new double[samples];
	   bearPos        = new double[samples];
	   speedPos       = new double[samples];
       
	   genBearDif();
	   computeAreaB();
	   genSpeedDif();
	   computeAreaS();
   }

   public double findKB(double x){
	   double a = 1.0/(hB*Math.sqrt(2.0*Math.PI));
	   double b = hB != 0.0 ? Math.exp( (-1.0*(x*x))/(2.0*hB*hB) ):0.0;
	   
	   return a*b;
   }
   
   public double findKS(double x){
	   double a = 1.0/(hS*Math.sqrt(2.0*Math.PI));
	   double b = hS != 0.0 ? Math.exp( (-1.0*(x*x))/(2.0*hS*hS) ):0.0;
	   
	   return a*b;
   }
   
   public void genBearDif(){
	   double wSum;
	   double kSum;
	   double tWB = maxB-minB;
	   double sampleVal,push;
	   
	   for(int i=0; i < samples; ++i){
		   sampleVal = minB+((double)i/(double)samples)*tWB;
		   wSum = kSum = 0.0;
		   for(int j=0; j < pointlist.size(); ++j){
			   wSum += pointlist.get(j).weight;
			   kSum += pointlist.get(j).weight*findKB(sampleVal-pointlist.get(j).bdel);
		   }
		   if(!Double.isNaN(kSum)){
			  if(wSum <= 0.0) System.err.println("Bad wSum in bearing: " + wSum);
			  
		      push       = (1.0/wSum)*kSum;
		      bearF[i]   = push;
		      bearPos[i] = sampleVal;
		   }
		   else{
			   push       = 0.0;
			   bearF[i]   = 0.0;
			   bearPos[i] = sampleVal;
		   }
	   }
   }
   
   public void genSpeedDif(){
	   double wSum;
	   double kSum;
	   double tWS = maxS-minS;
	   double sampleVal,push;
	   
	   for(int i=0; i < samples; ++i){
		   sampleVal = minS+((double)i/(double)samples)*tWS;
		   wSum = kSum = 0.0;
		   for(int j=0; j < pointlist.size(); ++j){
			   wSum += pointlist.get(j).weight;
			   kSum += pointlist.get(j).weight*findKS(sampleVal-pointlist.get(j).sdel);
		   }
		   if(!Double.isNaN(kSum)){
			  if(wSum <= 0.0) System.err.println("Bad wSum in speed: " + wSum);

		      push       = (1.0/wSum)*kSum;
		      speedF[i]   = push;
		      speedPos[i] = sampleVal;
		   }
		   else{
			   push       = 0.0;
			   speedF[i]   = 0.0;
			   speedPos[i] = sampleVal;
		   }
	   }
   }
   
   public void computeAreaB(){
	   double aV, bV;
	   
	   double N = bearF.length;
	   
	   double sum = bearF[0];
	   for(int i=1; i < bearF.length-1; ++i) sum += 2*bearF[i];
	   sum += bearF[bearF.length-1];
	   
	   aV = minB;
	   bV = maxB;
	   
	   areaB = (N!=0.0)?((bV-aV)/(2.0*N))*sum:0.0;
	   
	   double invAreaB = 1.0/areaB;
	   
	   if(areaB != 0.0){

		   for(int i=0; i < bearF.length; ++i) bearF[i] *= invAreaB;
		   
		   //bearF[0]              *= invAreaB;
		   //bearF[bearF.length-1] *= invAreaB;
		   //sum += bearF[0] + bearF[bearF.length-1];
		   //for(int i=1; i < bearF.length-1; ++i){
			//   bearF[i] *= invAreaB;
			 //  sum += 2.0*bearF[i];
		   //}
		   
		   sum = bearF[0];
		   for(int i=1; i < bearF.length-1; ++i) sum += 2.0*bearF[i];
		   sum += bearF[bearF.length-1];
		   		   
		   if( N != 0.0) areaB = ((bV-aV)/(2.0*N))*sum;
	   }
	   
	   double k=1.0;
	   double fa,fb, a;
	   sum = 0.0;
	   if(areaB != 0.0){
		   int bDFndx=0;
		   for(int i=1; i < bearF.length; ++i){
			   aV = minB + ((k-1.0)/((double)bearF.length))*(maxB-minB);
			   bV = minB + (k/((double)bearF.length))*(maxB-minB);
			   fa = bearF[ i ];
			   fb = bearF[i-1];
			   a = (bV-aV)*((fa+fb)/2.0);
			   sum += a;
			   bearDistFunc[bDFndx++] = a;
			   ++k;
		   }
	   }
   }
   
   public void computeAreaS(){
	   double aV, bV;
	   
	   double N = speedF.length;
	   
	   double sum = speedF[0];
	   for(int i=1; i < speedF.length-1; ++i) sum += 2*speedF[i];
	   sum += speedF[speedF.length-1];
	   
	   aV = minS;
	   bV = maxS;
	   
	   areaS = (N!=0.0)?((bV-aV)/(2.0*N))*sum:0.0;
	   
	   double invAreaS = 1.0/areaS;
	   
	   if(areaS != 0.0){

		   for(int i=0; i < speedF.length; ++i) speedF[i] *= invAreaS;
		   
		   //bearF[0]              *= invAreaB;
		   //bearF[bearF.length-1] *= invAreaB;
		   //sum += bearF[0] + bearF[bearF.length-1];
		   //for(int i=1; i < bearF.length-1; ++i){
			//   bearF[i] *= invAreaB;
			 //  sum += 2.0*bearF[i];
		   //}
		   
		   sum = speedF[0];
		   for(int i=1; i < speedF.length-1; ++i) sum += 2.0*speedF[i];
		   sum += speedF[speedF.length-1];
		   		   
		   if( N != 0.0) areaS = ((bV-aV)/(2.0*N))*sum;
	   }
	   
	   double k=1.0;
	   double fa,fb, a;
	   sum = 0.0;
	   if(areaS != 0.0){
		   int sDFndx=0;
		   for(int i=1; i < speedF.length; ++i){
			   aV = minS + ((k-1.0)/((double)speedF.length))*(maxS-minS);
			   bV = minS + (k/((double)speedF.length))*(maxS-minS);
			   fa = speedF[ i ];
			   fb = speedF[i-1];
			   a = (bV-aV)*((fa+fb)/2.0);
			   sum += a;
			   speedDistFunc[sDFndx++] = a;
			   ++k;
		   }
	   }
	   /*
	   double aV, bV;
	   double sum = 0.0;
	   
	   double N = speedF.length;
	   
	   sum += speedF[0] + speedF[speedF.length-1];
	   for(int i=1; i < speedF.length-1; ++i) sum += 2*speedF[i];
	   
	   aV = minS;
	   bV = maxS;
	   
	   areaS = (N!=0.0)?((bV-aV)/(2.0*N))*sum:0.0;
	   
	   double invAreaS = 1.0/areaS;
	   
	   if(areaS != 0.0){
		   sum = 0.0;
		   speedF[0]              *= invAreaS;
		   speedF[speedF.length-1] *= invAreaS;
		   sum += speedF[0] + speedF[speedF.length-1];
		   for(int i=1; i < speedF.length-1; ++i){
			   speedF[i] *= invAreaS;
			   sum += 2.0*speedF[i];
		   }
		   
		   if( N != 0.0) areaS = ((bV-aV)/(2.0*N))*sum;
	   }
	   
	   double k=1.0;
	   double fa,fb, a;
	   sum = 0.0;
	   if(areaS != 0.0){
		   for(int i=1; i < speedF.length; ++i){
			   aV = minS + ((k-1.0)/((double)speedF.length))*(maxS-minS);
			   bV = minS + (k/((double)speedF.length))*(maxS-minS);
			   fa = speedF[ i ];
			   fb = speedF[i-1];
			   a = (bV-aV)*((fa+fb)/2.0);
			   sum += a;
			   speedDistFunc[i] = a;
			   ++k;
		   }
	   }
	   */
   }

@Override
public void printBDF() {
  for(int i=0; i < bearDistFunc.length; ++i) System.err.print(bearDistFunc[i]+", ");
}

@Override
public void printBP() {
	for(int i=0; i < bearPos.length; ++i) System.err.print(bearPos[i]+", ");
}

@Override
public void printBF() {
	for(int i=0; i < bearF.length; ++i) System.err.print(bearF[i]+", ");
}

@Override
public void printSDF() {
	for(int i=0; i < speedDistFunc.length; ++i) System.err.print(speedDistFunc[i]+", ");
}

@Override
public void printSP() {
	for(int i=0; i < speedPos.length; ++i) System.err.print(speedPos[i]+", ");
}

@Override
public void printSF() {
	for(int i=0; i < speedF.length; ++i) System.err.print(speedF[i]+", ");
}
}
