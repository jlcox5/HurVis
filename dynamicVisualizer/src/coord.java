
public class coord {
  int i,j;
  coord(int ni, int nj) { i=ni; j=nj; }
  
  public boolean equals(Object o){
	  if( !( o instanceof coord )  ) return false;
	  coord c = (coord)o;
	  
	  return c.i==i && c.j==j;
  }
  
  private static final int maskleft = 0xFFFF << 16;
  
  public int hashCode(){
    return (i << 16)&(j&maskleft);
  }
  
}
