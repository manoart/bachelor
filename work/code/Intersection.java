Vertex[] edgeVertex = new Vertex[12];
for(int iEdge = 0; iEdge < 12; iEdge++)
{
    //if there is an intersection on this edge
    if((iEdgeFlags & (1 << iEdge)) == (1 << iEdge))
    {
        edgeVertex[iEdge] =
            new Vertex((v.getX() + (VOXEL_OFFSET[EDGE_CONNECTION[iEdge][0] ][0]+
                           0.5f * EDGE_DIRECTION[iEdge][0]) * scale),
                       (v.getY() + (VOXEL_OFFSET[EDGE_CONNECTION[iEdge][0] ][1]+
                           0.5f * EDGE_DIRECTION[iEdge][1]) * scale),
                       (v.getZ() + (VOXEL_OFFSET[EDGE_CONNECTION[iEdge][0] ][2]+
                           v.getDensity() * EDGE_DIRECTION[iEdge][2]) * scale));
    }
}
