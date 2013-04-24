import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.Vector;

import javax.imageio.ImageIO;

public class oldMethod implements Visualizer {
	private float width, height, ol, or, ot, ob;
	
	private Vector<Path> paths = new Vector<Path>(100,50);
	
	private class iwobs implements ImageObserver{
	      	 public boolean gotwidth = false;
	    	 
	      	 public int width;
	      	 
	      	 public boolean imageUpdate(Image img, int infoflags, int x, int y, int nwidth, int nheight){
	      		 if((infoflags & java.awt.image.ImageObserver.WIDTH) == java.awt.image.ImageObserver.WIDTH){
	      			 width = nwidth;
	      			 gotwidth = true;
	      		 }
	      		 
	      		 return !gotwidth;
	      	 }
	}
	private class ihobs implements ImageObserver{
	      	 public boolean gotheight = false;
	    	 
	      	 public int height;
	      	 
	      	 public boolean imageUpdate(Image img, int infoflags, int x, int y, int nwidth, int nheight){
	      		 if((infoflags & java.awt.image.ImageObserver.HEIGHT) == java.awt.image.ImageObserver.HEIGHT){
	      			 height = nheight;
	      			 gotheight = true;
	      		 }
	      		 return !gotheight;
	      	 }
	}
	
	//Image width,height in pixels
	private int imW, imH;
	private BufferedImage image;
	private float clamp01(float x){
		if( x <= 0.0f ) return 0.0f;
		if( x >= 1.0f ) return 1.0f;
		return x;
	}
	
//Per map-------------------------
	float lon0 = -100f;
	float lon1 = -75f;
	float lat0 = 17f;
	float lat1 = 33f;
	Image hmap = null;
//--------------------------------
	
	public void resizeImage(Dimension d){
		image = new BufferedImage(d.width,d.height,BufferedImage.TYPE_4BYTE_ABGR);
	}
	
	pathStrategy predicted=null, historical=null;
	Advisory adv = null;
	
	Random rand = new Random(1337);
	public oldMethod(){
		adv = new Advisory("rita3Hr.txt");
		//System.err.println("FOO: "+adv.getPos(0));
		predicted = new predictedPaths(adv);
		historical = new historicalPaths(adv,"resHistCur.txt");
	   try {
		hmap = ImageIO.read(this.getClass().getResource("hurricane_map_1.png"));
	} catch (IOException e) {
		e.printStackTrace();
	}
		
       //image = new BufferedImage(512,512,BufferedImage.TYPE_4BYTE_ABGR);
       //Graphics g = image.createGraphics();
       //g.setColor(Color.white);
       //g.clearRect(0, 0, 512, 512);
	}
	
	private static final int pathTTL = 150;
	
	private static boolean extinction = true;
	private static boolean once = false;
	private static boolean oneshot = false;
	
	@Override
	public void Update() {
		
		//Graphics g = image.createGraphics();
		
		if(extinction){
		   int dcursor=0;
		   while(dcursor<paths.size()){
		       if(paths.get(dcursor).dec())
				  paths.removeElementAt(dcursor);
			   else ++dcursor;
		   }
		}

		vec    x1;
		vec    x0 = adv.getPos(0);
		double s = adv.getInitialSpeed();
		//double b = vizUtils.sanitizeBearing(adv.getInitialBearing());
		double b = vizUtils.sanitizeBearing(adv.getInitialBearing());
		
		Path p = new Path(pathTTL);
		     p.nodes.add(adv.project(x0));
		     
		pathStrategy strat = historical;
		//pathStrategy strat = predicted;
		//pathStrategy strat = predicted;
		boolean report = false;
		     
		if(!once || oneshot){
			//for(int i=0; i < predicted.getDays(); ++i){
				   //vec delta = predicted.genDeltas(x0, b, i);
		   for(int i=0; i < strat.getDays(); ++i){
			   vec delta = strat.genDeltas(x0, b, i);
			   
			   if( report ){
			     System.err.println("-----------------");
			     System.err.println("Bearing, Delta: " + b + ", " + delta.get(0));
			     System.err.println("Speed, Delta: " + s + ", " + delta.get(1));
			     System.err.println("Lat/Lon (0): " + x0.get(0) + "/" + x0.get(1));
			   }
			   
			   s = vizUtils.addSpeed(s, delta.get(1));
			   //HACKHACKHACKHACKHACKHACKHACKHACKHACKHACKHACKHACKHACK
			   s = adv.getInitialSpeed();
			   //HACKHACKHACKHACKHACKHACKHACKHACKHACKHACKHACKHACKHACK
			   if( report ) System.err.println("     Old B: " + b + "   Delta: " + delta.get(0));
			   b = vizUtils.addBearing(b,delta.get(0));
			   if( report ) System.err.println("     New B: " + b);
			   //if(i==0)System.err.println("delta - Bearing: " + delta.get(0) + " - " + b);
			   double dist = s*adv.hoursInSeg();
			   //System.err.println(""+b+" "+s);
			   x1 = vizUtils.spherical_translation(x0, dist, b);
			   if( report ) System.err.println("Lat/Lon (1): " + x1.get(0) + "/" + x1.get(1));
			
			   p.nodes.add(adv.project(x1));
			
			   //System.err.println(""+i+" "+delta);
			   x0 = x1;
		   }
		   paths.add(p);
		   oneshot = false;
		}
		//System.err.println(p);
		
		/*
		float x = 256;
		float y = 256;
		float b = rand.nextFloat()*360;
		
		//g.setColor(Color.blue);
		
		Path p = new Path(150);
		     p.nodes.add(vec.vec3(x, y, b));
		
		for(int i=0; i < 5; ++i){
			float b1 = b + (rand.nextFloat()-0.5f)*90.0f;
			boolean neg = b1<0.0;
			b1 = Math.abs(b1)%360.0f;
			b1 = neg?360.0f-b1:b1;
			
			float x1 = (float) (x + 50.0f*Math.cos(Math.toRadians(b1)));
			float y1 = (float) (y + 50.0f*Math.sin(Math.toRadians(b1)));
			
			//g.drawLine((int)x,(int)y,(int)x1,(int)y1);
			p.nodes.add(vec.vec3(x1, y1, b1));
			
			x = x1;
			y = y1;
			b = b1;
		}
		paths.add(p);
		*/
	}
	
	private void drawPath(Graphics g, vec v0, vec v1, Dimension d){
		//System.err.println(""+v0.get(0)+" "+v0.get(1));
		//System.err.println("("+v0.get(0)+","+v0.get(1)+") ("+v1.get(0)+","+v1.get(1)+")");
		g.drawLine((int)(v0.get(0)*d.width),(int)(v0.get(1)*d.height),(int)(v1.get(0)*d.width),(int)(v1.get(1)*d.height));
	}
	
	private void drawTruePath(Graphics g, Dimension d){
		float[] color1 = {0f,1f,0f};
		float[] color2 = {1f,0f,1f};
		boolean parity = true;
		//g.setColor(new Color(color[0],color[1],color[2],1f));
		//for(int i=1; i < Math.min(7,adv.pathdata.size());++i){
		for(int i=1; i < adv.pathdata.size();++i){
			float[] color = parity?color1:color2;
			parity ^= true;
			g.setColor(new Color(color[0],color[1],color[2],1f));
			vec p0 =  adv.project(adv.getPos(i-1));
			vec p1 = adv.project(adv.getPos(i));
			drawPath(g,p0,p1,d);
		}
	}
	float invsqr2f = (float) (1.0/Math.sqrt(2.0));
	private void drawErrorBars(Graphics g, Dimension d){
		float[] color1 = {invsqr2f,invsqr2f,0f};
		float[] color2 = {0f,1f,0f};
		boolean c = true;
		
		//for(int i=1;i<Math.min(7,adv.leftBounds.length);++i){
		for(int i=1;i<adv.leftBounds.length;++i){
			float[] color = (c^=true)?color1:color2; 
			g.setColor(new Color(color[0],color[1],color[2],1f));
			drawPath(g,adv.project(adv.leftBounds[i-1]),adv.project(adv.leftBounds[i]),d);
			drawPath(g,adv.project(adv.rightBounds[i-1]),adv.project(adv.rightBounds[i]),d);
		}
	}
	
	private void drawPaths(Graphics g,Dimension d){
		//Color proto = Color.blue;
		float[] proto = {0f,0.2f,1f};
		
		for(int i=0; i < paths.size(); ++i){
			Path p_i = paths.get(i);
			float alpha = ((float)p_i.ttl)/255.0f;
			g.setColor(new Color(proto[0],proto[1],proto[2],alpha));
			
			vec v_ij0 = p_i.nodes.get(0);
			boolean colorChange = true;
			for(int j=1; j < p_i.nodes.size(); ++j){
				//System.err.println(i);
			//for(int j=1; j < 2; ++j){
				vec v_ij1 = p_i.nodes.get(j);
				//g.drawLine((int)v_ij0.get(0),(int)v_ij0.get(1)
				//		  ,(int)v_ij1.get(0),(int)v_ij1.get(1));
				if(colorChange){
				  g.setColor(new Color(255, 0 ,0));
				}
				else{
			      g.setColor(new Color(0, 0, 255));
				}
				colorChange = !colorChange;
				drawPath(g,v_ij0,v_ij1,d);
				v_ij0 = v_ij1;
			}
		}
	}
	private void drawPaths(Graphics g,Dimension d,int MAX){
		//Color proto = Color.blue;
		float[] proto = {0f,0.2f,1f};
		
		for(int i=0; i < paths.size(); ++i){
			Path p_i = paths.get(i);
			float alpha = ((float)p_i.ttl)/255.0f;
			g.setColor(new Color(proto[0],proto[1],proto[2],alpha));
			
			vec v_ij0 = p_i.nodes.get(0);
			boolean colorChange = true;
			for(int j=1; j < Math.min(MAX+1,p_i.nodes.size()); ++j){
				//System.err.println(i);
			//for(int j=1; j < 2; ++j){
				vec v_ij1 = p_i.nodes.get(j);
				//g.drawLine((int)v_ij0.get(0),(int)v_ij0.get(1)
				//		  ,(int)v_ij1.get(0),(int)v_ij1.get(1));
				if(colorChange){
				  g.setColor(new Color(255, 0 ,0));
				}
				else{
			      g.setColor(new Color(0, 0, 255));
				}
				colorChange = !colorChange;
				drawPath(g,v_ij0,v_ij1,d);
				v_ij0 = v_ij1;
			}
		}
	}

	@Override
	public Image Draw(Dimension d) {
		resizeImage(d);
		Graphics g = image.createGraphics();
		g.setColor(Color.black);
		//g.clearRect(0, 0, 512, 512);
		g.drawImage(hmap,0,0,d.width,d.height,null);
		drawErrorBars(g,d);
		drawTruePath(g,d);
		drawPaths(g,d);
		//drawPaths(g,d,1);
		
		//return image.getScaledInstance(d.width,d.height,Image.SCALE_SMOOTH);
		return image;
	}

	@Override
	public Image getRender(int reqw, int reqh) {
		// TODO Auto-generated method stub
		return null;
	}

}
