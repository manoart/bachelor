private int height(Voxel v)
{
    int height = 0;
    Voxel tmp = v;
    while(tmp != null && !tmp.getSnow() && !tmp.isInside())
    {
        height++;
        tmp = tmp.getBottomNeighbor();
    }
    return height;
}
