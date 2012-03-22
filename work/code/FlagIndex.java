int iFlagIndex = 0;
for(int iVoxel = 0; iVoxel < 8; iVoxel++)
{
    if(cube[iVoxel].isInside() || cube[iVoxel].getSnow())
    {
        iFlagIndex |= 1 << iVoxel;
    }
}
