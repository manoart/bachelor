private int stabilityTest(Voxel v)
{
    int avalanche = 0;
    // test all 8 neighbors and calculate their height difference
    if(v.hasRightNeighbor())
    {
        if(height(v.getRightNeighbor()) >= STEEPNESS)
        {
            avalanche(v.getRightNeighbor()).raiseDensity(SNOWFLAKE / 6.0f);
            stabilityTest(avalanche(v.getRightNeighbor()));
            avalanche++;
        }
    }
    ...
}
