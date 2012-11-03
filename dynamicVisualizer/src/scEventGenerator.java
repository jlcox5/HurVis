import java.util.ArrayList;

public class scEventGenerator {
   private ArrayList<scEventListener> listeners;
   public scEventGenerator(){
	   listeners = new ArrayList<scEventListener>();
   }
   public void fireEvent(){
	   scEvent e = new scEvent(this);
	   for(int i=0; i < listeners.size(); ++i){
		   
	   }
   }
}
