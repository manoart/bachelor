if((voxels[i].getSnow() || voxels[i].isInside()) &&
    voxels[i].hasRightNeighbor() && !voxels[i].getRightNeighbor().getSnow())
{
    activeRightVoxels[k++] = voxels[i];
}
