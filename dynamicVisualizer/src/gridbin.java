import java.util.ArrayList;
import java.util.List;
import java.math.*;


public class gridbin extends bin {
   ArrayList<gridPoint> pointlist = new ArrayList<gridPoint>();
   
   public double[] initArray(){
	   double[] narr = new double[samples];
	   for(int i=0; i < samples; ++i) narr[i] = 0.0;
	   return narr;
   }
   
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
   double priorMinB, priorMaxB;
   double priorMinS, priorMaxS;
   int samples=11;
   boolean hasPrePath;
   double areaB, areaS;
   
   int pLen=0;
   
   boolean DBUG = false;
   int lat=-666,lon=-666;
   
   public gridbin(){}
   public gridbin(int nlon, int nlat){
	   lat=nlat; lon=nlon;
   }
   
   public void add(gridPoint gp){
	   pointlist.add(gp);
	   ++pLen;
   }
   
   public double genBearingDelta(double r){
	   if(pLen==0) return 0.0;
	   if(pLen==1) return bearPos[0]*3.0;
	   
	   int k=0;
	   
	   double rangeL,rangeT,newR = r*areaB;
	   double prevsum = 0.0;
	   double sum = 0.0;
	   
	   int N = bearDistFunc==null?0:samples;
	   
	   while( sum < r && k<samples-1){
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
	   
	   return rangeL;
   }
   
   public double genSpeedDelta(double r){
	   //if(N==0 || areaS == 0.0) return 0.0;
	   //if(pLen==0) return 0.0;
	   //if(pLen==1) return speedPos[0]*3.0;	   
	   
	   int k=0;
	   
	   double rangeL,rangeT,newR = r*areaS;
	   double prevsum = 0.0;
	   double sum = 0.0;
	   
	   //while( sum < r && k < samples-1){
	   while( sum < r && k < samples-1){
		   prevsum = sum;
		   sum += speedDistFunc[k++];
	   }
	   k=Math.max(k-1,0);
	   
	   double areadif = sum - prevsum;
	   double arearatio = sum - newR;
	   
	   arearatio = (areadif != 0.0 && !Double.isNaN(areadif)) ? arearatio/areadif : 0.0;
	   
	   rangeL = speedPos[k  ];
	   rangeT = speedPos[k+1] - rangeL;
	   
	   rangeL = (rangeL+rangeT*arearatio)%360.0;
	   
	   //if(areaS == 0.0) System.err.println("BOGUS: "+3.0*rangeL);
	   return rangeL;
   }
   
   public void resolve(){
	   double meanB,meanS,tSum,uSum;
	   double k = 1.0;
	   
	   //if(pointlist.size() == 28 && lon==-77) System.err.println(lon + " " + lat);
	   
	   int size = pointlist.size();
	   //predStartTime  = new double[samples];
	   //predFinishTime = new double[samples];
	   //bearDistFunc   = new double[samples];
	   //speedDistFunc  = new double[samples];
	   //bearF          = new double[samples];
	   //speedF         = new double[samples];
	   //bearPos        = new double[samples];
	   //speedPos       = new double[samples];
	   
	   predStartTime   = initArray();
	   predFinishTime  = initArray();
	   bearDistFunc    = initArray();
	   speedDistFunc   = initArray();
	   bearF           = initArray();
	   speedF          = initArray();
	   bearPos         = initArray();
	   speedPos        = initArray();
	   
	   if(_debug) System.err.println("Pointlist size: " + size);
	   if(pointlist.size() == 0) return;
	   
	   //meanB=pointlist.get(0).bdel;
	   //meanS=pointlist.get(0).sdel;
	   meanB=meanS=0.0;
	   
	   //maxB = minB = pointlist.get(0).bdel;
	   //maxS = minS = pointlist.get(0).sdel;
	   
	   meanB = maxB = minB = pointlist.get(0).bdel;
	   meanS = maxS = minS = pointlist.get(0).sdel;
	   
	   if(_debug)System.err.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	   for(int i=1; i < pointlist.size(); ++i){
		   maxB = Math.max(maxB, pointlist.get(i).bdel);
		   // Changed by Jon
		   //minB = Math.min(maxB, pointlist.get(i).bdel);
		   minB = Math.min(minB, pointlist.get(i).bdel);
		   maxS = Math.max(maxS, pointlist.get(i).sdel);
		   // Changed by Jon
		   //minS = Math.min(maxS, pointlist.get(i).sdel);
		   minS = Math.min(minS, pointlist.get(i).sdel);
		   
		   if(_debug)System.err.println("meanB: "+meanB+" ; meanS: "+meanS);
		   if(_debug)System.err.println("bdel_i: "+pointlist.get(i).bdel
				                       +"sdel_i: "+pointlist.get(i).sdel);
		   
		   meanB += pointlist.get(i).bdel;
		   meanS += pointlist.get(i).sdel;
	   }
	   //if(_debug)System.err.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	   //if(_debug)System.err.println("meanB: "+meanB+" ; meanS: "+meanS);
	   meanB /= (double)pointlist.size();
	   meanS /= (double)pointlist.size();
	   //if(_debug)System.err.println("meanB: "+meanB+" ; meanS: "+meanS);
	   
	   tSum=0.0;
	   uSum=0.0;
	   
	   for(int i=1; i < pointlist.size(); ++i){
		   tSum += Math.pow(pointlist.get(i).bdel-meanB,2.0);
		   uSum += Math.pow(pointlist.get(i).sdel-meanS,2.0);
	   }
	   
	   tSum *= 1.0/((double)pointlist.size()-1.0);
	   uSum *= 1.0/((double)pointlist.size()-1.0);
	   
	   if(_debug)System.err.println("tSum: "+tSum+" ; uSum: "+uSum);
	   
	   wB = maxB-minB;
	   wS = maxS-minS;
	   
	   if(wB > 0.0) hB = k*Math.sqrt(tSum);
	   else hB = 0.0;
	   
	   if(wS > 0.0) hS = k*Math.sqrt(uSum);
	   else hS =0.0;
	   
	   priorMinB = minB;
	   priorMaxB = maxB;
	   priorMinS = minS;
	   priorMaxS = maxS;
	   
	   minB -= 3.0*hB;
	   maxB += 3.0*hB;
	   minS -= 3.0*hS;
	   maxS += 3.0*hS;
       
	   genBearDif();
	   computeAreaB();
	   genSpeedDif();
	   computeAreaS();
	   
	   //if( areaS != 0.0 && Math.abs(areaS-1.0) > 0.01 ) System.err.println("Bad Speed Area:   " + areaS);
	   //if( areaB != 0.0 && Math.abs(areaB-1.0) > 0.01 ) System.err.println("Bad Bearing Area: " + areaB);
	   
	   //if( areaS == 0.0 ) System.err.println("ZERO! Pointlist size: " + pointlist.size());
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
			   //if(_debug)System.err.println("wSum: "+wSum+" ; kSum: "+kSum);
			   
			   //if(_debug)System.err.println("Weight_"+j+": "+pointlist.get(j).weight+" ; SD: "+pointlist.get(j).sdel);
		   }
		   if(_debug)System.err.println("--------------------------------");
		   if(!Double.isNaN(kSum)){
			  if(wSum <= 0.0) System.err.println("Bad wSum in speed: " + wSum);
			  if(_debug)System.err.println("wSum: "+wSum+" ; kSum: "+kSum);
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
	   
	   if(_debug){
		 System.err.println("-----------------------------------");
		 System.err.println("speedF: ");
	     for(int i=0; i < samples; ++i) System.err.println(""+speedF[i]);
	     System.err.println("-----------------------------------");
	   }
	   if(_debug){
			 System.err.println("-----------------------------------");
			 System.err.println("speedPos: ");
		     for(int i=0; i < samples; ++i) System.err.println(""+speedPos[i]);
		     System.err.println("-----------------------------------");
	   }
   }
   
   public void computeAreaB(){
	   double aV, bV;
	   
	   //double N = samples;
	   double N = samples;
	   
	   double sum = bearF[0];
	   for(int i=1; i < samples-1; ++i) sum += 2.0*bearF[i];
	   sum += bearF[samples-1];
	   
	   aV = minB;
	   bV = maxB;
	   
	   areaB = (N!=0.0)?((bV-aV)/(2.0*N))*sum:0.0;
	   
	   double invAreaB = 1.0/areaB;
	   
	   if(areaB != 0.0){

		   for(int i=0; i < samples; ++i) bearF[i] *= invAreaB;
		   
		   //bearF[0]              *= invAreaB;
		   //bearF[samples-1] *= invAreaB;
		   //sum += bearF[0] + bearF[samples-1];
		   //for(int i=1; i < samples-1; ++i){
			//   bearF[i] *= invAreaB;
			 //  sum += 2.0*bearF[i];
		   //}
		   
		   sum = bearF[0];
		   for(int i=1; i < samples; ++i) sum += 2.0*bearF[i];
		   sum += bearF[samples-1];
		   		   
		   //if( N != 0.0) areaB = ((bV-aV)/(2.0*N))*sum;
		   areaB = (N!=0.0)?((bV-aV)/(2.0*N))*sum:0.0;
	   }
	   
	   double k=1.0;
	   double fa,fb, a;
	   sum = 0.0;
	   if(areaB != 0.0){
		   int bDFndx=0;
		   for(int i=1; i < samples; ++i){
			   aV = minB + ((k-1.0)/N)*(maxB-minB);
			   bV = minB + (k/N)*(maxB-minB);
			   fa = bearF[ i ];
			   fb = bearF[i-1];
			   a = (bV-aV)*((fa+fb)/2.0);
			   sum += a;
			   bearDistFunc[bDFndx++] = a;
			   ++k;
		   }
	   }
   }
   public double originalArea;
   public double revisedAreaS;
   @Override
   public double getOriginalArea(){ return originalArea; }
   @Override
   public double getRevisedArea(){ return revisedAreaS; }
   public void computeAreaS(){
	   double aV, bV;
	   
	   double N = samples;
	   
	   double sum = speedF[0];
	   for(int i=1; i < samples-1; ++i) sum += 2.0*speedF[i];
	   sum += speedF[samples-1];
	   
	   aV = minS;
	   bV = maxS;
	   
	   areaS = (N!=0.0)?((bV-aV)/(2.0*N))*sum:0.0;
	   
	   if(_debug)System.err.println("AREA_S Original: " + areaS);
	   
	   //if(areaS == 0.0){
	//	   System.err.println("--------------BOGUS---------------");
	//	   for(int i=0; i < samples; ++i) System.err.println(speedF[i]);
	//	   System.err.println("----------------------------------");
	 //  }
	   originalArea=areaS;
	   double invAreaS = 1.0/areaS;
	   
	   if(areaS != 0.0){

		   for(int i=0; i < samples; ++i) speedF[i] *= invAreaS;
		   
		   //bearF[0]              *= invAreaB;
		   //bearF[samples-1] *= invAreaB;
		   //sum += bearF[0] + bearF[samples-1];
		   //for(int i=1; i < samples-1; ++i){
			//   bearF[i] *= invAreaB;
			 //  sum += 2.0*bearF[i];
		   //}
		   
		   sum = speedF[0];
		   for(int i=1; i < samples-1; ++i) sum += 2.0*speedF[i];
		   sum += speedF[samples-1];
		   		   
		   //if( N != 0.0) areaS = ((bV-aV)/(2.0*N))*sum;
		   areaS = (N!=0.0)?((bV-aV)/(2.0*N))*sum:0.0;
	   }
	   revisedAreaS = areaS;
	   
	   if(_debug)System.err.println("AREA_S Revised: " + areaS);
	   double k=1.0;
	   double fa,fb, a;
	   sum = 0.0;
	   if(areaS != 0.0){
		   int sDFndx=0;
		   for(int i=1; i < samples; ++i){
			   aV = minS + ((k-1.0)/N)*(maxS-minS);
			   bV = minS + (k/N)*(maxS-minS);
			   fa = speedF[ i ];
			   fb = speedF[i-1];
			   a = (bV-aV)*((fa+fb)/2.0);
			   sum += a;
			   speedDistFunc[sDFndx++] = a;
			   ++k;
		   }
	   }
	   if(_debug){
		 System.err.println("-----------------------------------");
		 System.err.println("speedDistFunc: ");
	     for(int i=0; i < samples; ++i) System.err.println(""+speedDistFunc[i]);
	     System.err.println("-----------------------------------");
	   }
	   /*
	   double aV, bV;
	   double sum = 0.0;
	   
	   double N = samples;
	   
	   sum += speedF[0] + speedF[samples-1];
	   for(int i=1; i < samples-1; ++i) sum += 2*speedF[i];
	   
	   aV = minS;
	   bV = maxS;
	   
	   areaS = (N!=0.0)?((bV-aV)/(2.0*N))*sum:0.0;
	   
	   double invAreaS = 1.0/areaS;
	   
	   if(areaS != 0.0){
		   sum = 0.0;
		   speedF[0]              *= invAreaS;
		   speedF[samples-1] *= invAreaS;
		   sum += speedF[0] + speedF[samples-1];
		   for(int i=1; i < samples-1; ++i){
			   speedF[i] *= invAreaS;
			   sum += 2.0*speedF[i];
		   }
		   
		   if( N != 0.0) areaS = ((bV-aV)/(2.0*N))*sum;
	   }
	   
	   double k=1.0;
	   double fa,fb, a;
	   sum = 0.0;
	   if(areaS != 0.0){
		   for(int i=1; i < samples; ++i){
			   aV = minS + ((k-1.0)/((double)samples))*(maxS-minS);
			   bV = minS + (k/((double)samples))*(maxS-minS);
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
  for(int i=0; i < samples; ++i) System.err.print(bearDistFunc[i]+", ");
}

@Override
public void printBP() {
	for(int i=0; i < samples; ++i) System.err.print(bearPos[i]+", ");
}

public void printPoints(){
    for(int i=0; i < pointlist.size(); ++i){
      System.err.println("  sDeL: " + pointlist.get(i).sdel + "     bDel: " + pointlist.get(i).bdel);	
    }
}

@Override
public void printpoints2(){
	for(int i=0; i < pointlist.size(); ++i){
		System.err.println("SIZE: "+pLen);
		pointlist.get(i).print();
	}
}

@Override
public void printBF() {
	/*
	System.err.println("bin size: " + pointlist.size());
	System.err.println("priorMinB: " + priorMinB + "     priorMaxB: " + priorMaxB);
	System.err.println("priorMinS: " + priorMinS + "     priorMaxS: " + priorMaxS);
	System.err.println("hB: " + hB + "     hS: " + hS);
	System.err.println("minB: " + minB + "   maxB: " + maxB);
	System.err.println("minS: " + minS + "   maxS: " + maxS);
	*/
	//printPoints();
	for(int i=0; i < samples; ++i) System.err.print(bearF[i]+", ");
}

@Override
public void printSDF() {
	for(int i=0; i < samples; ++i) System.err.print(speedDistFunc[i]+", ");
}

@Override
public void printSP() {
	for(int i=0; i < samples; ++i) System.err.print(speedPos[i]+", ");
}

@Override
public void printSF() {
	for(int i=0; i < samples; ++i) System.err.print(speedF[i]+", ");
}
}
