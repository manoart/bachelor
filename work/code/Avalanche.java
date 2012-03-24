private Voxel avalanche(Voxel v)
{
    Voxel tmp = v;
    while(tmp.getBottomNeighbor() != null &&
         !(tmp.getBottomNeighbor().getDensity() >= 1.0f) &&
         !tmp.getBottomNeighbor().isInside())
    {
        tmp = tmp.getBottomNeighbor();
    }
    return tmp;
}
