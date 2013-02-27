
public class gridPoint {
   private float bearingDelta;
   private float speedDelta;
   private float weight;
   
   private float startLat;
   private float startLon;
   private float finalLat;
   private float finalLon;
   
   public gridPoint(float b, float s, float w, float sla, float slo, float fla, float flo){
	   bearingDelta = b;
	   speedDelta   = s;
	   weight       = w;
	   
	   startLat = sla;
	   startLon = slo;
	   finalLat = fla;
	   finalLon = flo;
   }
   
   public float getBD(){
	   return bearingDelta;
   }
   public float getSD(){
	   return speedDelta;
   }
   public float getW(){
	   return weight;
   }
   
   public float getSLat(){
	   return startLat;
   }
   public float getSLon(){
	   return startLon;
   }
   public float getFLat(){
	   return finalLat;
   }
   public float getFLon(){
	   return finalLon;
   }
}
