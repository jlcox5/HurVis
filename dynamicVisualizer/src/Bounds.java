import java.awt.Dimension;
import java.awt.Rectangle;


public class Bounds {
   public double x,y,width,height;
   public Bounds(double nx, double ny, double nw, double nh){
	   x = nx;
	   y = ny;
	   width  = nw;
	   height = nh;
   }
   public boolean intersect(double px, double py){
	   return px >= x
		&&    py >= y
		&&    px <= (x+width)
		&&    py <= (y+height);
   }
   public Rectangle project(Dimension p){
	   double fpw = p.width;
	   double fph = p.height;
	   int nx = (int)(x*fpw);
	   int ny = (int)(y*fph);
	   int nw = (int)(width*fpw);
	   int nh = (int)(height*fph);
	   return new Rectangle(nx,ny,nw,nh);
   }
   public vec project(vec p){
	   return vec.vec2((p.get(0)-x)/width, (p.get(1)-y)/height);
   }
}
