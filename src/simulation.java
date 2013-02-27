import java.util.Vector;
import java.math.*;

public class simulation {
	   private static simulation _handle = new simulation();
	
	   private simulation(){
	   }
	   
	   public static simulation summon(){
		   return _handle;
	   }
	   	   
	   //SINGLETON

	   public Vector<advisory> advList;
	   public Vector<path>     dataPathList;
	   public Vector<path>     dataPathAugList;
	   
	   public Vector<Vector<vec>> bearBin;
	   
	   public double getMaxLon(){
		   //STUB
		   return 0.0;
	   }
	   public double getMaxLat(){
		   //STUB
		   return 0.0;
	   }
	   public double getTotLon(){
		   //STUB
		   return 0.0;
	   }
	   public double getTotLat(){
		   //STUB
		   return 0.0;
	   }
	   public int getScreenWidth(){
		   //STUB
		   return 0;
	   }
	   public int getScreenHeight(){
		   //STUB
		   return 0;
	   }
	   public static double earthRad(){
		   return 6371.0;
	   }
	   
	   //Geo functions
	   public vec translateToScreen(double curLon, double curLat){
		   double w = getScreenWidth();
		   double h = getScreenHeight();
		   
		   vec toRet = vec.vec2( w * (getMaxLon()-curLon) / getTotLon() 
				               , h * (getMaxLat()-curLat) / getTotLat() );
		   
		   return toRet;	   
	   }
	   double haversine(vec p0, vec p1){
		   return haversine(p0.get(0),p1.get(0),p0.get(1),p1.get(1));
	   }
	   double haversine(double lon1, double lon2, double lat1, double lat2){
		   
		   double radLat1 = lat1*(Math.PI/180.0);
		   double radLon1 = lon1*(Math.PI/180.0);
		   double radLat2 = lat2*(Math.PI/180.0);
		   double radLon2 = lon2*(Math.PI/180.0);
		   
		   double dLat = (lat2-lat1)*(Math.PI/180.0);
		   double dLon = (lon2-lon1)*(Math.PI/180.0);
		   
		   double a_0 = Math.sin(dLat/2.0);
		   double a_1 = Math.sin(dLon/2.0);
		   double a   = a_0*a_0 + Math.cos(radLat1)*Math.cos(radLat2)*a_1*a_1;
		   
		   double b   = 2.0*Math.atan2(Math.sqrt(a),Math.sqrt(1.0-a));
		   
		   return earthRad() * b;
	   }
	   
	   vec advancePos(double curLon, double curLat, double velX, double velY){	   
		   double lon1 = haversine(getMaxLon(),getMaxLon()-getTotLon(),getMaxLat(),getMaxLat());
		   double lon2 = haversine(getMaxLon(),getMaxLon()-getTotLon(),getMaxLat()-getTotLat(),getMaxLat()-getTotLat());
		   
		   double disLon = (lon1 + lon2)/2.0;
		   double disLat = haversine(getMaxLon(),getMaxLon(),getMaxLat(),getMaxLat()-getTotLat());
		   
		   double w = getScreenWidth();
		   double h = getScreenHeight();
		   return translateToScreen(curLon,curLat).add(vec.vec2((velX/disLon)*w,(velY/disLat)*h));
	   }
	   
	   vec locateDestination(double curLon, double curLat, double velX, double velY, double curDeg){
		   if(curDeg == 0.0){
			   curDeg = 360.0;
		   }
		   
		   double dist = Math.sqrt(velX) + Math.sqrt(velY);
		   
		   double orgLon = (curLon*Math.PI)*180.0;
		   double orgLat = (curLat*Math.PI)*180.0;
		   
		   dist /= earthRad();
		   
		   curDeg = (curDeg*Math.PI)/180.0;
		   curLat = Math.asin( Math.sin(orgLat)*Math.cos(dist)+Math.cos(orgLat)*Math.sin(dist)*Math.cos(curDeg) );
		   curLon = orgLon + Math.atan2( Math.sin(curDeg)*Math.sin(dist)*Math.cos(orgLat)
				                       , Math.cos(dist)-Math.sin(orgLat)*Math.sin(curLat) );
		   
		   return vec.vec2(180.0*curLon/Math.PI, 180.0*curLat/Math.PI);
	   }

	   vec locateDestination2(double curLon, double curLat, double dist, double curDeg){
		   if(curDeg == 0.0){
			   curDeg = 360.0;
		   }
		   
		   double orgLon = (curLon*Math.PI)*180.0;
		   double orgLat = (curLat*Math.PI)*180.0;
		   
		   dist /= earthRad();
		   
		   curDeg = (curDeg*Math.PI)/180.0;
		   curLat = Math.asin( Math.sin(orgLat)*Math.cos(dist)+Math.cos(orgLat)*Math.sin(dist)*Math.cos(curDeg) );
		   curLon = orgLon + Math.atan2( Math.sin(curDeg)*Math.sin(dist)*Math.cos(orgLat)
				                       , Math.cos(dist)-Math.sin(orgLat)*Math.sin(curLat) );
		   
		   return vec.vec2(180.0*curLon/Math.PI, 180.0*curLat/Math.PI);
	   }
	   
	   double findBearing(vec pos1, vec pos2, vec pos3, vec curPoint){	   
		   if(pos1.get(0) != pos2.get(0) && pos1.get(1) != pos2.get(1))
			   return findBearing_2(pos1,pos2);
		   else
			   return findBearing_2(pos3,pos2);
	   }
	   double findBearing_2(vec pos1, vec pos2){
		   double dLon,x,y,result;
		   double pi_180 = Math.PI/180.0;
		   
		   dLon = pos2.get(0) - pos1.get(0);
		   y = Math.sin(dLon*pi_180)*Math.cos(pos2.get(1)*pi_180);
		   x = Math.cos(pos1.get(1)*pi_180)*Math.sin(pos2.get(1)*pi_180) - Math.sin(pos1.get(1)*pi_180)*Math.cos(pos2.get(1)*pi_180)*Math.cos(dLon*pi_180);
			   
		   result = Math.atan2(y,x)*pi_180;
		   
		   if(result < 0.0) result += 360.0;	   
		   return result;
	   }
	   double crossTrack(vec projPath1, vec projPath2, vec curPoint){
		   double b1,b2,l1;
		   
		   b1 = findBearing_2(projPath1,projPath2);
		   b2 = findBearing_2(projPath1,curPoint);
		   
		   l1 = haversine(projPath1, curPoint);
		   
		   return Math.asin(Math.sin(l1/earthRad())*Math.sin(b2-b1)*earthRad());
	   }
}
