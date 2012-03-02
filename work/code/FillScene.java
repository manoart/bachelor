private void fillScene(float[] vertices)
{
    float maxX = calculateMaxX(vertices);
    float minX = calculateMinX(vertices);
    float maxY = calculateMaxY(vertices);
    float minY = calculateMinY(vertices);
    float maxZ = calculateMaxZ(vertices);
    float minZ = calculateMinZ(vertices);

    for (float z = minZ; z <= maxZ; z += 1.0f / STEPS)
    {
        for (float x = minX; x <= maxX; x += 1.0f / STEPS)
        {
            for (float y = minY; y <= maxY; y += 1.0f / STEPS)
            {
                setVoxel(x, y, z);
            }
        }
    }
}
