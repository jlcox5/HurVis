import java.awt.Dimension;
import java.awt.Rectangle;


public class Bounds {
   public double x,y,width,height;
   boolean rightHanded=false;
   public Bounds(double nx, double ny, double nw, double nh){
	   x = nx;
	   y = ny;
	   width  = nw;
	   height = nh;
   }
   public Bounds(double nx, double ny, double nw, double nh, boolean rh){
	   x = nx;
	   y = ny;
	   width  = nw;
	   height = nh;
	   rightHanded=rh;
   }
   
   private boolean iy0l(double yy){
	   return yy >= y;
   }
   private boolean iy0r(double yy){
	   return yy <= y;
   }
   private boolean iy1l(double yy){
	   return yy <= (y+height);
   }
   private boolean iy1r(double yy){
	   return yy >= (yy-height);
   }
   private boolean iy0(double yy){
	   return rightHanded?iy0r(yy):iy0l(yy);
   }
   private boolean iy1(double yy){
	   return rightHanded?iy1r(yy):iy1l(yy);
   }
   public boolean intersect(double px, double py){	   
	   return px >= x
		&&    iy0(py)
		&&    px <= (x+width)
		&&    iy1(py);
   }
   public Rectangle project(Dimension p){
	   double fpw = p.width;
	   double fph = p.height;
	   int nx = (int)(x*fpw);
	   int ny = (int)((rightHanded?y-height:y)*fph);
	   int nw = (int)(width*fpw);
	   int nh = (int)(height*fph);
	   return new Rectangle(nx,ny,nw,nh);
   }
   public vec project(vec p){
	   return rightHanded?vec.vec2((p.get(0)-x)/width, 1.0-(p.get(1)-y)/height)
			             :vec.vec2((p.get(0)-x)/width, (p.get(1)-y)/height);
   }
}
