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
    /** left neighbor of the voxel */
    private Voxel leftNeighbor;
    /** right neighbor of the voxel */
    private Voxel rightNeighbor;
    /** top neighbor of the voxel */
    private Voxel topNeighbor;
    /** bottom neighbor of the voxel */
    private Voxel bottomNeighbor;
    /** front neighbor of the voxel */
    private Voxel frontNeighbor;
    /** back neighbor of the voxel */
    private Voxel backNeighbor;

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
    public boolean isNeighbor(Voxel v, float distance)
    {
        /*
         * To make sure that we only test in the six directions of the axis
         * (and not the diagonals), we have to check three cases.
         */
        return (((Math.abs(this.x - v.getX()) == distance ^ // first
                Math.abs(this.y - v.getY()) == distance) &&
                Math.abs(this.z - v.getZ()) == 0) ||
                ((Math.abs(this.x - v.getX()) == distance ^ // second
                Math.abs(this.z - v.getZ()) == distance) &&
                Math.abs(this.y - v.getY()) == 0) ||
                ((Math.abs(this.y - v.getY()) == distance ^ // third
                Math.abs(this.z - v.getZ()) == distance) &&
                Math.abs(this.x - v.getX()) == 0));
    }

    /**
     * Counts the neighbors of this Voxel.
     * 
     * @return number of neighbors
     */
    public int countNeighbors()
    {
        int count = 0;
        
        if(hasLeftNeighbor())   count++;
        if(hasRightNeighbor())  count++;
        if(hasTopNeighbor())    count++;
        if(hasBottomNeighbor()) count++;
        if(hasBackNeighbor())   count++;
        if(hasFrontNeighbor())  count++;
        
        return count;
    }
    
    /**
     * Tests if this Voxel has any neighbors.
     *
     * @return true, if this Voxel has any neighbors
     */
    public boolean hasNeighbor()
    {
        return countNeighbors() > 0 ? true : false;
    }
    
    public boolean hasNeighborInEveryDirection(Voxel[] voxels, float distance)
    {
        return (countNeighbors() == 6) ? true : false;
    }
    
    public void setNeighbors(Voxel[] voxels, float distance)
    {
        setLeftNeighbor(voxels, distance);
        setRightNeighbor(voxels, distance);
        setFrontNeighbor(voxels, distance);
        setBackNeighbor(voxels, distance);
        setTopNeighbor(voxels, distance);
        setBottomNeighbor(voxels, distance);
    }
    
    public boolean hasLeftNeighbor()
    {
        return this.leftNeighbor != null ? true : false;
    }
    
    public boolean hasRightNeighbor()
    {
        return this.rightNeighbor != null ? true : false;
    }
    
    public boolean hasTopNeighbor()
    {
        return this.topNeighbor != null ? true : false;
    }
    
    public boolean hasBottomNeighbor()
    {
        return this.bottomNeighbor != null ? true : false;
    }
    
    public boolean hasFrontNeighbor()
    {
        return this.frontNeighbor != null ? true : false;
    }
    
    public boolean hasBackNeighbor()
    {
        return this.backNeighbor != null ? true : false;
    }
    
    public void setLeftNeighbor(Voxel[] voxels, float distance)
    {
        for(int i = 0; i < voxels.length; i++)
        {
            if(voxels[i] != null)
            {
                float tmp = this.x - voxels[i].getX();
                if(tmp <= distance && tmp > 0.0f && this.y == voxels[i].getY()&& 
                        this.z == voxels[i].getZ())
                {
                    this.leftNeighbor = voxels[i];
                    return;
                }
            }
        }
    }
    
    public void setRightNeighbor(Voxel[] voxels, float distance)
    {
        for(int i = 0; i < voxels.length; i++)
        {
            if(voxels[i] != null)
            {
                float tmp = voxels[i].getX() - this.x;
                if(tmp <= distance && tmp > 0.0f && this.y == voxels[i].getY() && 
                        this.z == voxels[i].getZ())
                {
                    this.rightNeighbor = voxels[i];
                    return;
                }
            }
        }
    }
    
    public void setTopNeighbor(Voxel[] voxels, float distance)
    {
        for(int i = 0; i < voxels.length; i++)
        {
            if(voxels[i] != null)
            {
                float tmp = voxels[i].getZ() - this.z;
                if(tmp <= distance && tmp > 0.0f && this.y == voxels[i].getY() && 
                        this.x == voxels[i].getX())
                {
                    this.topNeighbor = voxels[i];
                    return;
                }
            }
        }
    }
    
    public void setBottomNeighbor(Voxel[] voxels, float distance)
    {
        for(int i = 0; i < voxels.length; i++)
        {
            if(voxels[i] != null)
            {
                float tmp = this.z - voxels[i].getZ();
                if(tmp <= distance && tmp > 0.0f && this.y == voxels[i].getY() && 
                        this.x == voxels[i].getX())
                {
                    this.bottomNeighbor = voxels[i];
                    return;
                }
            }
        }
    }
    
    public void setFrontNeighbor(Voxel[] voxels, float distance)
    {
        for(int i = 0; i < voxels.length; i++)
        {
            if(voxels[i] != null)
            {
                float tmp = voxels[i].getY() - this.y;
                if(tmp <= distance && tmp > 0.0f && this.x == voxels[i].getX() && 
                        this.z == voxels[i].getZ())
                {
                    this.frontNeighbor = voxels[i];
                    return;
                }
            }
        }
    }
    
    public void setBackNeighbor(Voxel[] voxels, float distance)
    {
        for(int i = 0; i < voxels.length; i++)
        {
            if(voxels[i] != null)
            {
                float tmp = this.y - voxels[i].getY();
                if(tmp <= distance && tmp > 0.0f && this.x == voxels[i].getX() && 
                        this.z == voxels[i].getZ())
                {
                    this.backNeighbor = voxels[i];
                    return;
                }
            }
        }
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

    public void setSnow()
    {
        this.snow = true;
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
    
    public void raiseDensity(double snowflake)
    {
        this.density += snowflake;
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
