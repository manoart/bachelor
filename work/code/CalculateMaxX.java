private float calculateMaxX(float[] vertices)
{
    float tmp;
    float max = Float.MIN_VALUE;
    for (int i = 1; i <= (vertices.length / 3); i++)
    {
        tmp = vertices[(i * 3) - 3];
        if (tmp > max)
        {
            max = tmp;
        }
    }
    return max;
}
