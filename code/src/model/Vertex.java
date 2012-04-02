package model;

/**
 *
 * @author Manuel
 */
public class Vertex
{
    private float x;
    private float y;
    private float z;
    
    public Vertex()
    {
        this(0.0f, 0.0f, 0.0f);
    }
    
    public Vertex(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public boolean isEmpty()
    {
        return this.equals(new Vertex(0.0f, 0.0f, 0.0f));
    }
    
    public boolean equals(Vertex v)
    {
        if(this.x == v.getX() && this.y == v.getY() && this.z == v.getZ())
        {
            return true;
        }else
        {
            return false;
        }
    }
    
    public float getX()
    {
        return this.x;
    }
    
    public float getY()
    {
        return this.y;
    }
    
    public float getZ()
    {
        return this.z;
    }
    
    public void setX(float x)
    {
        this.x = x;
    }
    
    public void setY(float y)
    {
        this.y = y;
    }
    
    public void setZ(float z)
    {
        this.z = z;
    }
}
