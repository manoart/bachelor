private boolean pointInPolygon(float x, float y, float[] edges)
{
    boolean inside = false;
    for(int i = 0; i < edges.length; i += 4)
    {
        float x1 = edges[i];
        float y1 = edges[i + 1];
        float x2 = edges[i + 2];
        float y2 = edges[i + 3];

        boolean startOver = y1 >= y;
        boolean endOver = y2 >= y;

        if(startOver != endOver)
        {
            float sx = ((float) (y * (x2 - x1) - y1 * x2 + y2 * x1) /
                        (float) (y2 - y1));

            if(sx >= x)
            {
                inside = !inside;
            }
        }
    }
    return inside;
}
