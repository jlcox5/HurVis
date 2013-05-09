import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;


public class Experiment extends JPanel{
	/*
   private class backPane extends JComponent{
	   private Image im;
	   public backPane(Image nim){
		   im = nim;
	   }
	   @Override
	   protected void paintComponent(Graphics g){
		   g.drawImage(im,0,0,null);
	   }
   }
	*/
   public class testButton extends JButton{
	   public testButton(){
		   super("TEST");
		   setOpaque(false);
	   }
	   @Override
	   protected void paintComponent(Graphics g){
		   Graphics2D g2d = (Graphics2D) g.create();
		   g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f));
		   
		   super.paintComponent(g2d);
		   g2d.dispose();
	   }
   }
   
   private int appletWidth  = 1;
   private int appletHeight = 1;
   
   public void appletResize(int nw, int nh){
	   appletWidth  = nw;
	   appletHeight = nh;
	   setSize(appletWidth,appletHeight);
	   repaint();
   }
	
   private Visualizer viz;
   private UI         ui;
   
   private long freq=30l;
   private Timer sched = new Timer(true);
   private long updateTimestamp;
   public Experiment(){
	   //viz = new testVisualizer("testOtter.jpg");
	   viz = new oldMethod();
	   ui  = new testUI();
	   
	   updateTimestamp = (new Date()).getTime();
	   
	   //30 hertz, hardcoded for now
	   sched.scheduleAtFixedRate(new TimerTask(){

		@Override
		public void run() {
		   long t1 = (new Date()).getTime();
		   long dt = t1-updateTimestamp;
		   updateTimestamp = t1;
		   Update(dt);
		}
		
	   }, 0, 1000l/freq);
	   
	   
	  /* viz.Draw();
	   Image i = viz.getRender(getWidth(),getHeight());
	   setContentPane(new backPane(i));
	   */
	   
	   //add(new testButton());
   }
   
   public void Update(long dt){
	   viz.Update(dt);
	   ui.Update(dt);
	   repaint();
   }
   
   @Override
   public void paintComponent(Graphics g){	   
	   //viz.Update();
	   //ui.Update();
	   
	   int W = getWidth();
	   int H = getHeight();
	   //g.drawImage(viz.getRender(W, H), 0, 0, W, H,null);
	   
	   Image vizIm = viz.Draw(new Dimension(W,H));
	   BufferedImage uiIm  = ui.Draw(new Dimension(W,H));
	   
	   super.paintComponent(g);
	   g.drawImage(vizIm,0,0,null);
	   g.drawImage(uiIm,0,0,null);
   }
}
