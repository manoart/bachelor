package model;

/** 
 * Representation of a Voxel.
 *
 * @version 19.07.11
 *  
 * @author Manuel Schwarz
 */
public class Voxel
{
    // instance variables

    /** x-coordinate of the voxel */
    private float x;
    /** y-coordinate of the voxel */
    private float y;
    /** z-coordinate of the voxel */
    private float z;
    /** marks if there is snow or not */
    private boolean snow;
    /** density of the snow (perhaps needed in the future)*/
    private double density;

    /**
     * Default-Constructor
     */
    public Voxel()
    {
        this(0f, 0f, 0f, false, 0.0);
    }

    /**
     * Custom-Constructor with 3 parameters (the coordinates)
     *
     * @param x x-coordinate of the voxel
     * @param y y-coordinate of the voxel
     * @param z z-coordinate of the voxel
     */
    public Voxel(float x, float y, float z)
    {
        this(x, y, z, false, 0.0);
    }

    /**
     * Custom-Constructor with 4 parameters
     *
     * @param x x-coordinate of the voxel
     * @param y y-coordinate of the voxel
     * @param z z-coordinate of the voxel
     * @param snow
     */
    public Voxel(float x, float y, float z, boolean snow)
    {
        this(x, y, z, snow, 0.0);
    }

    /**
     * Custom-Constructor with 5 parameters (all there are)
     *
     * @param x x-coordinate of the voxel
     * @param y y-coordinate of the voxel
     * @param z z-coordinate of the voxel
     * @param snow
     * @param density
     */
    public Voxel(float x, float y, float z, boolean snow, double density)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.snow = snow;
        this.density = density;
    }

    /**
     * Copy-Constructor
     *
     * @param v Voxel that should be copied.
     */
    public Voxel(Voxel v)
    {
        this(v.getX(), v.getY(), v.getZ(), v.getSnow(), v.getDensity());
    }

    /**
     * Compares this Voxel to the Voxel v (parameter)
     *
     * @param v the compared Voxel
     * @return true, if this Voxel equals v, else false
     */
    public boolean equals(Voxel v)
    {
        if(this.x == v.getX() && this.y == v.getY() && this.z == v.getZ())
        {
            return true;
        }else
        {
            return false;
        }
    }

    /**
     * Tests if this Voxel is the neighbor of a specific other Voxel.
     * 
     * @param v the tested Voxel
     * @return true, if v is a neighbor of this Voxel, else false
     */
    public boolean isNeighbor(Voxel v)
    {
        /*
         * To make sure that we only test in the six directions of the axis
         * (and not the diagonals), we have to check three cases.
         */
        return (((Math.abs(this.x - v.getX()) == 1 ^ // first
                Math.abs(this.y - v.getY()) == 1) &&
                Math.abs(this.z - v.getZ()) == 0) ||
                ((Math.abs(this.x - v.getX()) == 1 ^ // second
                Math.abs(this.z - v.getZ()) == 1) &&
                Math.abs(this.y - v.getY()) == 0) ||
                ((Math.abs(this.y - v.getY()) == 1 ^ // third
                Math.abs(this.z - v.getZ()) == 1) &&
                Math.abs(this.x - v.getX()) == 0));
    }

    /**
     * Tests if this Voxel has any neighbors.
     *
     * @return true, if this Voxel has any neighbors
     */
    public boolean hasNeighbor()
    {
        return true;    //TODO more Code
    }
    
    public boolean hasNeighborInEveryDirection()
    {
        return true;
    }
    
    public boolean hasTopNeighbor()
    {
        return true;
    }
    
    /**
     * Tests if a voxel is in another object. (surrounded by faces?)
     * @return true if voxel is in an object, false else.
     */
    public boolean isInObject()
    {
        //TODO write "is in Object"-Test
        return true;
    }

    /**
     * Getter for the x-coordinate.
     *
     * @return x-coordinate of the Voxel
     */
    public float getX()
    {
        return this.x;
    }

    /**
     * Getter for the y-coordinate.
     *
     * @return y-coordinate of the Voxel
     */
    public float getY()
    {
        return this.y;
    }

    /**
     * Getter for the z-coordinate.
     *
     * @return z-coordinate of the Voxel
     */
    public float getZ()
    {
        return this.z;
    }

    /**
     * Getter for the snow-flag.
     *
     * @return if there is snow or not
     */
    public boolean getSnow()
    {
        return this.snow;
    }

    /**
     * Getter for the snow`s density.
     *
     * @return the density of the snow
     */
    public double getDensity()
    {
        return this.density;
    }
    
    /**
     * 
     * 
     * @param v 
     * @return interpolated Voxel
     */
    public Voxel interpolate(Voxel v) {
        return v;
    }

    /**
     * toString-method of the Voxel
     *
     * @return a String with all information about the Voxel
     */
    @Override
    public String toString()
    {
        String s;
        s = "x = " + this.getX() + "\n"
                + "y = " + this.getY() + "\n"
                + "z = " + this.getZ() + "\n"
                + "snow = " + this.getSnow() + "\n"
                + "density = " + this.getDensity() + "\n";
        return s;
    }
}
