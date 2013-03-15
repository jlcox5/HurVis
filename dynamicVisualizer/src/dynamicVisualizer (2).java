import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class dynamicVisualizer extends Applet{
   public dynamicVisualizer(){
	   Experiment ex = new Experiment();
	   resize(1024,790);
	   ex.appletResize(getWidth(),getHeight());
	   addComponentListener((new ComponentListener(){
		   private Applet     app = null;
		   private Experiment ex  = null;
		   public ComponentListener setStuff(Applet napp, Experiment nex){ 
			   app = napp;
			   ex  = nex;
			   return this;
		   }
		   @Override
		   public void componentHidden(ComponentEvent e){
		   }
		   @Override
		   public void componentMoved(ComponentEvent e){
		   }
		   @Override
		   public void componentResized(ComponentEvent e){
			   ex.appletResize(app.getWidth(), app.getHeight());
		   }
		   @Override
		   public void componentShown(ComponentEvent e){
		   }
		   
	   }).setStuff(this,ex));
	   setLayout(new BorderLayout());
	   add(ex);
   }
}
