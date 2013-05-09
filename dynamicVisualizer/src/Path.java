import java.util.Vector;


public class Path {
	public Vector<vec> nodes = new Vector<vec>(7,5);
	public boolean predictable=false;
	public boolean isPredictable(){ return predictable; }
	public boolean testErrorCone(Advisory adv){
		predictable=true;
		for(int i=1; i < nodes.size(); ++i){
		  if(!adv.inCone(nodes.get(i))){
			predictable=false;
			break;
		  }
		}
		return predictable;
	}
	public int ttl = 0;
	public int ttl0 = 1;
	public Path(int nttl){
		ttl = nttl;
		ttl0 = Math.max(ttl, 1);
	}
	public float life(){ return vizUtils.scaledPulseLife(.4f,((float)ttl)/((float)ttl0)); }
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
	public void age(long dt) {
       ttl-=dt;
	}
}
