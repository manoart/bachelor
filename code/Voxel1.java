import java.util.*;

public class Voxel {

  /** 
   * Coordinate of the voxel, as integer
   */
  public int x = -1;
  public int y = -1;
  public int z = -1;
  public double intensity = -1.0;
  public int mark = -1;
  public double interpolateDen = 100.0;  //density for interpolate points

  public Voxel(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  
  public Voxel(int x, int y, int z, double intensity) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.intensity = intensity;
  }
  
  public Voxel() {}
  
  /**
   * Check whether the Voxel is empty (if x==-1 && y==-1 && z==-1)
   *
   * @return True = the voxel is empty
   */
   //public boolean isEmpty() {
   //  return (this.x == -1 && this.y == -1 && this.z == -1);
   //}
  
  
  /** 
   * Check whether another voxel a has the same coordinates with this voxel
   *
   * @return true = the input voxel has the same coordinate
   */
  public boolean equals(Voxel a) {
    return (this.x == a.x && this.y == a.y && this.z == a.z);
  }
  
  /** 
   * Check whether another voxel a has the same coordinates with this voxel
   * Not override Object.equals() to avoid ambigiuos
   * 
   * @return true = the input voxel has the same coordinate
   */
  public boolean equalsto(Voxel a) {
    return (this.x == a.x && this.y == a.y && this.z == a.z);
  }

  /** 
   * Check whether another voxel a has the same coordinates and Intensity with
   * this voxel
   *
   * @return true = the input voxel has the same coordinate and intensity
   */
  public boolean allEquals(Voxel a) {
    return (this.x == a.x && this.y == a.y && this.z == a.z && 
            this.intensity == a.intensity);
  }
  
  /**
   * check whether two voxels are neighbor, but not the same
   *
   * @return True = Yes, they are neighbors
   */
  public boolean isNeighbor (Voxel a) {
    return (Math.abs(this.x-a.x) <=1.00001 && Math.abs(this.y-a.y) <=1.000001 
            && Math.abs(this.z-a.z) <=1.000001 && (! (this.x==a.x 
            && this.y == a.y && this.z == a.z)));
  }
  
  /**
   * Return a Vector which includes the points to interpolate
   * Density are set to interpolateDen.
   */ 
  public Vector interpolate (Voxel a) {
    Vector b = new Vector();
    int nx,ny,nz, ix,iy,iz, sx=this.x-a.x, sy=this.y-a.y, sz=this.z-a.z;
    ix=(sx==Math.abs(sx)) ? 1: -1;  //sign
    iy=(sy==Math.abs(sy)) ? 1: -1;  //sign
    iz=(sz==Math.abs(sz)) ? 1: -1;  //sign
    int max=Math.abs(sx), flg =1;
    if (max < Math.abs(sy)) {max=Math.abs(sy); flg=2;}
    if (max < Math.abs(sz)) {max=Math.abs(sz); flg=3;}
    for (int i=1; i<max; i++) {
      if(flg==1) {
        nx=a.x+i*ix;
        float delt=(nx-a.x)/(float)sx;
        ny=Math.round(delt*sy+a.y);
        nz=Math.round(delt*sz+a.z);
      } else if (flg==2) {
        ny=a.y+i*iy;
        float delt=(ny-a.y)/(float)sy;
        nx=Math.round(delt*sx+a.x);
        nz=Math.round(delt*sz+a.z);
      } else {
        nz=a.z+i*iz;
        float delt=(nz-a.z)/(float)sz;
        ny=Math.round(delt*sy+a.y);
        nx=Math.round(delt*sx+a.x);
      }  
      b.add(new Voxel(nx,ny,nz,interpolateDen));
    }
    return b;  
  }
  
  /**
   * calculate distance
   *
   * @return distance (NOT considering the grid interval-resolution) 
   */
  public double distance(Voxel a) {
    double leng= Math.sqrt((double)((this.x-a.x)*(this.x-a.x)+(this.y-a.y)*(this.y-a.y)+(this.z-a.z)*(this.z-a.z))); 
    return leng;
  }

  public String toString() {
    return (this.toCoord()+" density = "+((long)(intensity*1000))/1000.0);
  }
  
  /** 
   * @return Coordinates of the voxel
   */
  public String toCoord() {
    return ("("+this.x+", "+this.y+", "+this.z+")");
  }

}
