int k = 0;
this.activeVoxels = new Voxel[countActiveVoxels(voxels)];
// snow from above
for(int i = 0; i < voxels.length; i++)
{
    if((voxels[i].getSnow() &&                   // voxels on the surface
       !voxels[i].getTopNeighbor().getSnow()) ||
       (!voxels[i].hasBottomNeighbor() &&        // floor-voxels beside objects
       !voxels[i].getTopNeighbor().getSnow() && !voxels[i].getSnow()) ||
       (voxels[i].isInside() &&                  // voxels on an object
       !voxels[i].getTopNeighbor().isInside()))
    {
        activeVoxels[k++] = voxels[i];
    }
}
