private void setRightNeighbor(Voxel[] voxels, float distance)
{
    for(int i = 0; i < voxels.length; i++)
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
