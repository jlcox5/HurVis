
import java.lang.Object;
import java.util.*;

public class vec {
   public int N;
   public Vector<Double> coords;
   public vec(int n){
	   N = n;
	   coords = new Vector<Double>(N);
   }
   public double dot(vec rhs){
	   if( rhs.N != N ) return 0.0;
	   
	   double inner = 0.0;
	   for(int i=0; i < N; ++i) inner += coords.get(i)*rhs.coords.get(i);
	   return inner;
   }
   public vec add(vec rhs){
	   vec ret = new vec(N);
	   for( int i=0; i < N; ++i){
		   ret.set(i,get(i)+rhs.get(i));
	   }
	   return ret;
   }
   public vec scale(double rhs){
	   vec ret = new vec(N);
	   for(int i=0; i < N; ++i){
		   ret.set(i, get(i)*rhs);
	   }
	   return ret;
   }
   public static vec vec2(double x, double y){
	   vec ret = new vec(2);
	   ret.coords.set(0,x);
	   ret.coords.set(0,y);
	   return ret;
   }
   public static vec vec3(double x, double y, double z){
	   vec ret = new vec(3);
	   ret.coords.set(0,x);
	   ret.coords.set(0,y);
	   ret.coords.set(0,z);
	   return ret;
   }
   public static vec vec4(double x, double y, double z, double w){
	   vec ret = new vec(4);
	   ret.coords.set(0,x);
	   ret.coords.set(0,y);
	   ret.coords.set(0,z);
	   ret.coords.set(0,w);
	   return ret;
   }
   public double get(int ndx){
	   return coords.get(ndx);
   }
   public void set(int ndx, double val){
	   coords.set(ndx, val);
   }
}
