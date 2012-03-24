int snowflakes = 50;
for(int j = 0; j < snowflakes; j++)
{
    int index = (int)(Math.random() * activeVoxels.length);
    this.activeVoxels[index].raiseDensity(SNOWFLAKE);
    if(this.activeVoxels[index].getDensity() > 1.0f)
    {
        this.activeVoxels[index].getTopNeighbor().setSnow();
        this.activeVoxels[index] = this.activeVoxels[index].getTopNeighbor();
    }
}
