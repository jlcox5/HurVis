
public class vizUtils {
	
	public static double addBearing(double current, double delta){
		double sum = current+delta;
		if(sum > 360.0) return sum % 360.0;
		if(sum < 0.0)   return sum + 360.0;
		return sum;
	}
	public static double addSpeed(double current, double delta){
		return current+delta;
	}
	
	public static double earthRad(){
		   return 6371.0;
	}
	public static double haversine(vec p0, vec p1){
		   return haversine(p0.get(0),p1.get(0),p0.get(1),p1.get(1));
	}
	public static double PI = Math.PI;
	public static double PI180 = Math.PI/180.0;
	public static double haversine(double lon1, double lon2, double lat1, double lat2){
		   
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
	
	//                                      (lat,lon)      (in km)   (deg - not rad)
	public static vec spherical_translation(vec curPos, double dist, double bearing){
		
		vec X = new vec(curPos);
		
		bearing=(bearing==0.0?360.0:bearing);
		double earthRad=6371.0;
		
		double orgLon = X.get(0)*PI180;
		double orgLat = X.get(1)*PI180;
		dist /= earthRad;
		bearing *= PI180;
		X.set(1,Math.asin( Math.sin(orgLat) * Math.cos(dist) + Math.cos(orgLat) * Math.sin(dist) * Math.cos(bearing)));
		X.set(0,orgLon + Math.atan2(Math.sin(bearing) * Math.sin(dist) * Math.cos(orgLat)
				                   ,Math.cos(dist) - Math.sin(orgLat) * Math.sin(X.get(1))));
		X = X.scale(180.0/Math.PI);
		return X;
	}
	public static double findBearing_2(vec p1, vec p2){
		double dLon = p2.get(0)-p1.get(0);
		double y = Math.sin(dLon*(PI180))*Math.cos(p2.get(1)*PI180);
		double x = Math.cos(p1.get(1)*PI180)*Math.sin(p2.get(1)*PI180) - Math.sin(p1.get(1)*PI180)*Math.cos(p2.get(1)*PI180)*Math.cos(dLon*PI180);
		
		double ret = Math.atan2(y,x)*180.0/Math.PI;
		
		return ret<0.0?360.0+ret:ret;
	}
}
