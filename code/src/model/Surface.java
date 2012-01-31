package model;

/**
 *
 * @author manschwa
 */
public class Surface
{
    private Voxel[] voxels;
    private Voxel[] activeVoxels;
    private Generator generator;
    
    public Surface(String path)
    {
        this.generator = new Generator(path);
        this.voxels = generator.getVoxels();
        setNeighbors();
        randomSnow();
    }
    
    private void setNeighbors()
    {
        for(int i = 0; i < voxels.length; i++)
        {
            if(voxels[i] != null)
            {
                voxels[i].setNeighbors(voxels, (1.0f / generator.getSteps()) + 0.01f);
            }
        }
    }
    
    private void randomSnow()
    {
//        this.activeVoxels = new Voxel[voxels.length/generator.getSteps()];
        for(int i = 0; i < voxels.length; i++)
        {
            if(voxels[i] != null && voxels[i].hasTopNeighbor() && !voxels[i].hasBottomNeighbor())
            {
//                this.activeVoxels[i] = voxels[i];
                this.voxels[i].setSnow();
            }
        }
        
//        // let it snow
//        for(int j = 0; j < 10000; j++)
//        {
//            this.voxels[(int)(Math.random() * activeVoxels.length)].raiseDensity(0.1);
//        }
    }
    
    public Voxel[] getVoxels()
    {
        return this.voxels;
    }
    
    public Voxel[] getActiveVoxels()
    {
        return this.activeVoxels;
    }
    
    public Generator getGenerator()
    {
        return this.generator;
    }
}
