// Store the triangles that were found. There can be up to five per cube.
for(int iTriangle = 0; iTriangle < 5; iTriangle++)
{
        if(TriangleLookupTable[iFlagIndex][3 * iTriangle] < 0)
            break;
        for(int iCorner = 0; iCorner < 3; iCorner++)
        {
                int iVertex =
                    TriangleLookupTable[iFlagIndex][3 * iTriangle + iCorner];
                this.faces[cnt++] = edgeVertex[iVertex].getX();
                this.faces[cnt++] = edgeVertex[iVertex].getY();
                this.faces[cnt++] = edgeVertex[iVertex].getZ();
        }
}
