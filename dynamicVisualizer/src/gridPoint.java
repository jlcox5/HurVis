
public class gridPoint {
  public double sdel,bdel,weight,lat0,lon0,lat1,lon1;
  
  public gridPoint(double nb, double ns, double nw, double nlat0, double nlon0, double nlat1, double nlon1){
    sdel   = ns   ;
    bdel   = nb   ;
    weight = nw   ;
    lat0   = nlat0;
    lat1   = nlat1;
    lon0   = nlon0;
    lon1   = nlon1;
  }
}
