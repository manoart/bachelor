package model;

/**
 *
 * @author manschwa
 */
public class Snowflakes
{
    private Vertex[] snowflakes;
    private float height;
    
    public Snowflakes(int density, Generator generator)
    {
        int width = generator.getWidth() * 2;
        int height = generator.getHeight();
        int depth = generator.getDepth() * 2;
        
        this.snowflakes = new Vertex[width * height * depth * density * density * density];
        this.height = height;
        
        
        for(int i = 0; i < this.snowflakes.length/*width * density * depth * density*/; i++)
        {
            this.snowflakes[i] = new Vertex((float)(Math.random() * width) - 0.5f * width, 
                    (float)(Math.random() * depth) - 0.5f * depth,
                    (float)Math.random() * this.height);
//            this.snowflakes[i].setY((float)(Math.random() * depth));
        }
    }
    
    public void topSnowfall()
    {
        for(int i = 0; i < this.snowflakes.length; i++)
        {
            if(this.snowflakes[i] != null)
            {   
                if(this.snowflakes[i].getZ() <= 0.0f)
                {
                    this.snowflakes[i].setZ(this.height);
                }else
                {
                    this.snowflakes[i].setX(this.snowflakes[i].getX() + random() * 0.01f);
                    this.snowflakes[i].setY(this.snowflakes[i].getY() + random() * 0.01f);
                    this.snowflakes[i].setZ(this.snowflakes[i].getZ() - (float)Math.random() * 0.005f);
                }
            }
        }
    }
    
    public void rightSnowfall()
    {
        for(int i = 0; i < this.snowflakes.length; i++)
        {
            if(this.snowflakes[i] != null)
            {   
                if(this.snowflakes[i].getZ() <= 0.0f)
                {
                    this.snowflakes[i].setZ(this.height);
                }else
                {
                    this.snowflakes[i].setX(this.snowflakes[i].getX() + random() * 0.01f);
                    this.snowflakes[i].setY(this.snowflakes[i].getY() + random() * 0.01f);
                    this.snowflakes[i].setZ(this.snowflakes[i].getZ() - (float)Math.random() * 0.005f);
                }
            }
        }
    }
    
    private float random()
    {
        float random = (float)Math.random();
        if(random < 0.5f)
        {
            return -random;
        }else
        {
            return random - 0.5f;
        }
    }
    
    public Vertex[] getSnowflakes()
    {
        return this.snowflakes;
    }
}
