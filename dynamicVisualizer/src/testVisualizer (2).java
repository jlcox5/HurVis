import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.net.URL;

import javax.imageio.ImageIO;


public class testVisualizer implements Visualizer {
	              // Unitless image width, height, offset {left, right, top, bottom}
	private float width, height, ol, or, ot, ob;
	
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
	private Image image;
	private float clamp01(float x){
		if( x <= 0.0f ) return 0.0f;
		if( x >= 1.0f ) return 1.0f;
		return x;
	}
	public testVisualizer(String im/*, float w, float h*/){
		//w = clamp01(w);
		//h = clamp01(h);
		try{
			URL res = this.getClass().getResource(im);
			image = ImageIO.read(res);
			
			iwobs widthObs  = new iwobs();
			ihobs heightObs = new ihobs();
			
			imW = image.getWidth(widthObs);
			imH = image.getHeight(heightObs);
			
			if(imW == -1){
				 while(!widthObs.gotwidth)Thread.yield();
	        	 
	        	 imW = widthObs.width;
			}
			if(imH == -1){
				while(!heightObs.gotheight)Thread.yield();
				
				imH = heightObs.height;
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
			//Cry uncontrollably because something went wrong
		}
	}
	
	private void nothingAtAll(){
		// Optimal version, complexity O(0)
	}

	@Override
	public void Update() {
		// TODO Auto-generated method stub
		
        //Do nothing at all
		nothingAtAll();
	}

	@Override
	public Image Draw(Dimension d) {
		// TODO Auto-generated method stub
        
		//Do nothing at all
		//nothingAtAll();
		return image.getScaledInstance(d.width, d.height, Image.SCALE_SMOOTH);
	}

	public Image getRender(int reqw, int reqh){
		return image.getScaledInstance(reqw, reqh, Image.SCALE_SMOOTH);
	}
}
