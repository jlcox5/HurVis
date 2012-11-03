import java.util.Vector;


public class Path {
	public Vector<vec> nodes = new Vector<vec>(7,5);
	public int ttl = 0;
	public Path(int nttl){
		ttl = nttl;
	}
	public boolean dead(){ return ttl <= 0;      }
	public boolean dec() { --ttl; return dead(); }
	@Override
	public String toString(){
		String ret = "";
		for(int i=0;i<nodes.size()-1;++i)
			ret += nodes.get(i)+"-->";
		ret += nodes.get(nodes.size()-1);
		return ret;
	}
}
