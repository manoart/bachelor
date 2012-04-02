package model;

/**
 *
 * @author manschwa
 */
public class Surface
{
    private Voxel[] voxels;
    private Voxel[] activeRightVoxels;
    private Voxel[] activeVoxels;
    private Generator generator;
    private Snowflakes snowflakes;

    public Snowflakes getSnowflakes()
    {
        return snowflakes;
    }
    /** Array which contains the faces' vertices. */
    private float[] faces;
    /** Array which contains the normals vertices. */
    private float[] normals;
    /** static counter for the faces and normals arrays */
    private static int cnt = 0;
    private static boolean cold = true;
    private static float SNOWFLAKE = 0.1f;
    private static int STEEPNESS = 3;
    /** VERTEX_OFFSET lists the positions, relative to vertex0, of each of the 8 vertices of a cube */
    private static final float[][] VERTEX_OFFSET =
    {
        {0.0f, 0.0f, 0.0f},{1.0f, 0.0f, 0.0f},{1.0f, 1.0f, 0.0f},{0.0f, 1.0f, 0.0f},
        {0.0f, 0.0f, 1.0f},{1.0f, 0.0f, 1.0f},{1.0f, 1.0f, 1.0f},{0.0f, 1.0f, 1.0f}
    };
    
    // EdgeConnection lists the index of the endpoint vertices for each of the 12 edges of the cube
    private static final int[][] EDGE_CONNECTION = 
    {
        {0,1}, {1,2}, {2,3}, {3,0},
        {4,5}, {5,6}, {6,7}, {7,4},
        {0,4}, {1,5}, {2,6}, {3,7}
    };
    
    //a2fEdgeDirection lists the direction vector (vertex1-vertex0) for each edge in the cube
    private static final float[][] EDGE_DIRECTION =
    {
        {1.0f, 0.0f, 0.0f},{0.0f, 1.0f, 0.0f},{-1.0f, 0.0f, 0.0f},{0.0f, -1.0f, 0.0f},
        {1.0f, 0.0f, 0.0f},{0.0f, 1.0f, 0.0f},{-1.0f, 0.0f, 0.0f},{0.0f, -1.0f, 0.0f},
        {0.0f, 0.0f, 1.0f},{0.0f, 0.0f, 1.0f},{ 0.0f, 0.0f, 1.0f},{0.0f,  0.0f, 1.0f}
    };


    
    public Surface(String path)
    {
        this.generator = new Generator(path);
        this.snowflakes = new Snowflakes(5, this.generator);
        this.voxels = generator.getVoxels();
        this.faces = new float[voxels.length * 6];
        this.normals = new float[faces.length];
        
        setNeighbors();
        setInitialDensity();
        
//        randomSnow();
//        marchingCubes();
//        marchingCubesActive();
        
        //TODO AVZ-Modell (Plus)
        //TODO Textur auf AVZ-Modell
    }
    
    private void marchingCube(Voxel v, float scale)
    {      
        
        Vertex[] edgeVertex = new Vertex[12];
        Vertex[] edgeNormal = new Vertex[12];
        
        float density = (float)v.getDensity();
        
        // generate a new cube
        // if one voxel of the cube is null, then it is not a complete cube
        // and one can break up the marching cubes algorithm
        Voxel[] cube = new Voxel[8];
        cube[0] = v;
        cube[1] = v.getRightNeighbor();
        if(cube[1] == null) return;
        cube[2] = v.getRightNeighbor().getFrontNeighbor();
        if(cube[2] == null) return;
        cube[3] = v.getFrontNeighbor();
        if(cube[3] == null) return;
        cube[4] = v.getTopNeighbor();
        if(cube[4] == null) return;
        cube[5] = v.getTopNeighbor().getRightNeighbor();
        if(cube[5] == null) return;
        cube[6] = v.getTopNeighbor().getRightNeighbor().getFrontNeighbor();
        if(cube[6] == null) return;
        cube[7] = v.getTopNeighbor().getFrontNeighbor();
        if(cube[7] == null) return;
        
        // find which vertices are inside of the surface and which are outside
        int iFlagIndex = 0;
        for(int iVoxel = 0; iVoxel < 8; iVoxel++)
        {
            if(cube[iVoxel].isInside() || cube[iVoxel].getSnow())
            {
                iFlagIndex |= 1 << iVoxel;
            }
        }

        //Find which edges are intersected by the surface
        int iEdgeFlags = CubeEdgeFlags[iFlagIndex];

        //If the cube is entirely inside or outside of the surface, then there will be no intersections
        if(iEdgeFlags == 0) 
        {
            return;
        }
        
        //Find the point of intersection of the surface with each edge
        //Then find the normal to the surface at those points
        for(int iEdge = 0; iEdge < 12; iEdge++)
        {
            //if there is an intersection on this edge
            if((iEdgeFlags & (1 << iEdge)) == (1 << iEdge))
            {
                // no exact calculation of the intersection points
                // just connect the middle of the edges with each other (0.5f)
                /*
                if(iEdge == 0)
                {
                    edgeVertex[iEdge] = new Vertex((v.getX() + (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][0]  +  interpolate(v.getDensity(), v.getRightNeighbor().getDensity()) * EDGE_DIRECTION[iEdge][0]) * scale), 
                                                   (v.getY()),// + (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][1]  +  0.5f * EDGE_DIRECTION[iEdge][1]) * scale), 
                                                   (v.getZ()));// + (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][2]  +  0.5f * EDGE_DIRECTION[iEdge][2]) * scale));
                }else if(iEdge == 1)
                {
                    edgeVertex[iEdge] = new Vertex((v.getRightNeighbor().getX()),// + (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][0]  +  0.5f * EDGE_DIRECTION[iEdge][0]) * scale), 
                                                   (v.getRightNeighbor().getY() + (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][1]  +  interpolate(v.getRightNeighbor().getDensity(), v.getRightNeighbor().getFrontNeighbor().getDensity()) * EDGE_DIRECTION[iEdge][1]) * scale), 
                                                   (v.getRightNeighbor().getZ())); //+ (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][2]  +  0.5f * EDGE_DIRECTION[iEdge][2]) * scale));
                }else if(iEdge == 2)
                {
                    edgeVertex[iEdge] = new Vertex((v.getRightNeighbor().getFrontNeighbor().getX() - (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][0]  +  interpolate(v.getRightNeighbor().getFrontNeighbor().getDensity(), v.getFrontNeighbor().getDensity()) * EDGE_DIRECTION[iEdge][0]) * scale), 
                                                   (v.getRightNeighbor().getFrontNeighbor().getY()),// + (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][1]  +  0.5f * EDGE_DIRECTION[iEdge][1]) * scale), 
                                                   (v.getRightNeighbor().getFrontNeighbor().getZ())); //+ (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][2]  +  0.5f * EDGE_DIRECTION[iEdge][2]) * scale));
                }else if(iEdge == 3)
                {
                    edgeVertex[iEdge] = new Vertex((v.getFrontNeighbor().getX()),// + (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][0]  +  0.5f * EDGE_DIRECTION[iEdge][0]) * scale), 
                                                   (v.getFrontNeighbor().getY() - (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][1]  +  interpolate(v.getFrontNeighbor().getDensity(), v.getDensity()) * EDGE_DIRECTION[iEdge][1]) * scale), 
                                                   (v.getFrontNeighbor().getZ()));// + (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][2]  +  0.5f * EDGE_DIRECTION[iEdge][2]) * scale));
                }else if(iEdge == 4)
                {
                    edgeVertex[iEdge] = new Vertex((v.getTopNeighbor().getX() + (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][0]  +  interpolate(v.getTopNeighbor().getDensity(), v.getTopNeighbor().getRightNeighbor().getDensity()) * EDGE_DIRECTION[iEdge][0]) * scale), 
                                                   (v.getTopNeighbor().getY()),// + (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][1]  +  0.5f * EDGE_DIRECTION[iEdge][1]) * scale), 
                                                   (v.getTopNeighbor().getZ()));// + (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][2]  +  0.5f * EDGE_DIRECTION[iEdge][2]) * scale));
                }else if(iEdge == 5)
                {
                    edgeVertex[iEdge] = new Vertex((v.getTopNeighbor().getRightNeighbor().getX()),// + (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][0]  +  0.5f * EDGE_DIRECTION[iEdge][0]) * scale), 
                                                   (v.getTopNeighbor().getRightNeighbor().getY() + (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][1]  +  interpolate(v.getTopNeighbor().getRightNeighbor().getDensity(), v.getTopNeighbor().getRightNeighbor().getFrontNeighbor().getDensity()) * EDGE_DIRECTION[iEdge][1]) * scale), 
                                                   (v.getTopNeighbor().getRightNeighbor().getZ()));// + (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][2]  +  0.5f * EDGE_DIRECTION[iEdge][2]) * scale));
                }else if(iEdge == 6)
                {
                    edgeVertex[iEdge] = new Vertex((v.getTopNeighbor().getRightNeighbor().getFrontNeighbor().getX() - (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][0]  +  interpolate(v.getTopNeighbor().getRightNeighbor().getFrontNeighbor().getDensity(), v.getTopNeighbor().getFrontNeighbor().getDensity()) * EDGE_DIRECTION[iEdge][0]) * scale), 
                                                   (v.getTopNeighbor().getRightNeighbor().getFrontNeighbor().getY()),// + (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][1]  +  0.5f * EDGE_DIRECTION[iEdge][1]) * scale), 
                                                   (v.getTopNeighbor().getRightNeighbor().getFrontNeighbor().getZ()));// + (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][2]  +  0.5f * EDGE_DIRECTION[iEdge][2]) * scale));
                }else if(iEdge == 7)
                {
                    edgeVertex[iEdge] = new Vertex((v.getTopNeighbor().getFrontNeighbor().getX()),// + (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][0]  +  0.5f * EDGE_DIRECTION[iEdge][0]) * scale), 
                                                   (v.getTopNeighbor().getFrontNeighbor().getY() - (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][1]  +  interpolate(v.getTopNeighbor().getFrontNeighbor().getDensity(), v.getTopNeighbor().getDensity()) * EDGE_DIRECTION[iEdge][1]) * scale), 
                                                   (v.getTopNeighbor().getFrontNeighbor().getZ()));// + (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][2]  +  0.5f * EDGE_DIRECTION[iEdge][2]) * scale));
                }else*/ if(iEdge == 8)
                {
                   edgeVertex[iEdge] = new Vertex((v.getX() + (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][0]  +  0.5f * EDGE_DIRECTION[iEdge][0]) * scale), 
                                                  (v.getY() + (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][1]  +  0.5f * EDGE_DIRECTION[iEdge][1]) * scale), 
                                                  (v.getZ() + (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][2]  +  v.getDensity() * EDGE_DIRECTION[iEdge][2]) * scale)); 
                }else if(iEdge == 9)
                {
                    edgeVertex[iEdge] = new Vertex((v.getX() + (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][0]  +  0.5f * EDGE_DIRECTION[iEdge][0]) * scale), 
                                                   (v.getY() + (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][1]  +  0.5f * EDGE_DIRECTION[iEdge][1]) * scale), 
                                                   (v.getZ() + (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][2]  +  v.getRightNeighbor().getDensity() * EDGE_DIRECTION[iEdge][2]) * scale));
                }else if(iEdge == 10)
                {
                    edgeVertex[iEdge] = new Vertex((v.getX() + (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][0]  +  0.5f * EDGE_DIRECTION[iEdge][0]) * scale), 
                                                   (v.getY() + (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][1]  +  0.5f * EDGE_DIRECTION[iEdge][1]) * scale), 
                                                   (v.getZ() + (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][2]  +  v.getRightNeighbor().getFrontNeighbor().getDensity() * EDGE_DIRECTION[iEdge][2]) * scale));
                }else if (iEdge == 11)
                {
                    edgeVertex[iEdge] = new Vertex((v.getX() + (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][0]  +  0.5f * EDGE_DIRECTION[iEdge][0]) * scale), 
                                                   (v.getY() + (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][1]  +  0.5f * EDGE_DIRECTION[iEdge][1]) * scale), 
                                                   (v.getZ() + (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][2]  +  v.getFrontNeighbor().getDensity() * EDGE_DIRECTION[iEdge][2]) * scale));
                }else
                {
                    edgeVertex[iEdge] = new Vertex((v.getX() + (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][0]  +  0.5f * EDGE_DIRECTION[iEdge][0]) * scale), 
                                                   (v.getY() + (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][1]  +  0.5f * EDGE_DIRECTION[iEdge][1]) * scale), 
                                                   (v.getZ() + (VERTEX_OFFSET[EDGE_CONNECTION[iEdge][0] ][2]  +  0.5f * EDGE_DIRECTION[iEdge][2]) * scale));
                }
            
                edgeNormal[iEdge] = getNormal(edgeVertex[iEdge].getX(), edgeVertex[iEdge].getY(), edgeVertex[iEdge].getZ());
            }
        }


        //Draw the triangles that were found.  There can be up to five per cube
        for(int iTriangle = 0; iTriangle < 5; iTriangle++)
        {
                if(TriangleLookupTable[iFlagIndex][3*iTriangle] < 0)
                        break;

                for(int iCorner = 0; iCorner < 3; iCorner++)
                {
                        int iVertex = TriangleLookupTable[iFlagIndex][3*iTriangle+iCorner];
                        this.normals[cnt] = edgeNormal[iVertex].getX();
                        this.faces[cnt++] = edgeVertex[iVertex].getX();
                        this.normals[cnt] = edgeNormal[iVertex].getY();
                        this.faces[cnt++] = edgeVertex[iVertex].getY();
                        this.normals[cnt] = edgeVertex[iVertex].getZ();
                        this.faces[cnt++] = edgeVertex[iVertex].getZ();
                }
        }      
    }
    
    private Vertex getNormal(float x, float y, float z)
    {
        Vertex v = new Vertex();
        v.setX(fSample(x - 0.01f, y, z) - fSample(x + 0.01f, y, z));
        v.setY(fSample(x, y - 0.01f, z) - fSample(x, y + 0.01f, z));
        v.setZ((fSample(x, y, z - 0.01f) - fSample(x, y, z + 0.01f)) * (1));
        return normalizeVector(v);
    }    
    
    private Vertex normalizeVector(Vertex vIn)
    {
        Vertex vOut = new Vertex();
        float fOldLength;
        float fScale;

        fOldLength = (float)Math.sqrt( (vIn.getX() * vIn.getX()) +
                            (vIn.getY() * vIn.getY()) +
                            (vIn.getZ() * vIn.getZ()) );

        if(fOldLength != 0.0f)
        {
            fScale = 1.0f / fOldLength;
            vOut.setX(vIn.getX() * fScale);
            vOut.setY(vIn.getY() * fScale);
            vOut.setZ(vIn.getZ() * fScale);
        }else
        {
            vOut = vIn;
        }
        
        return vOut;
    }
    
    private float interpolate(float a, float b)
    {
        if(a > b && b == 0.0f)
        {
            return a;
        }else if(b > a && a == 0.0f)
        {
            return b;
        }else
        {
            return 0.5f;
        }
    }

        
    
    //fSample1 finds the distance of (fX, fY, fZ) from three moving points
    private float fSample(float fX, float fY, float fZ)
    {
        float fResult = 0.0f;
        float fDx, fDy, fDz;
        fDx = fX - 0.5f;
        fDy = fY - 0.5f;
        fDz = fZ - 0.5f;
        fResult += 0.5f/(fDx*fDx + fDy*fDy + fDz*fDz);

        fDx = fX - 0.5f;
        fDy = fY - 0.5f;
        fDz = fZ - 0.5f;
        fResult += 1.0/(fDx*fDx + fDy*fDy + fDz*fDz);

        fDx = fX - 0.5f;
        fDy = fY - 0.5f;
        fDz = fZ - 0.5f;
        fResult += 1.5f/(fDx*fDx + fDy*fDy + fDz*fDz);

        return fResult;
    }
    
    public void marchingCubes()
    {
        for(int i = 0; i < this.faces.length; i++)
        {
            this.faces[i] = 0.0f;
        }
        for(int i = 0; i < voxels.length; i++)
        {
            if(voxels[i] != null)
            {
                marchingCube(voxels[i], 1.0f / generator.getSteps());
            }
        }
    }
    
    public void marchingCubesActive()
    {
        for(int i = 0; i < this.activeRightVoxels.length; i++)
        {
            if(this.activeRightVoxels[i] != null)
            {
                marchingCube(this.activeRightVoxels[i], 1.0f / generator.getSteps());
            }
        }
    }
    
    private void setNeighbors()
    {
        for(int i = 0; i < voxels.length; i++)
        {
            if(voxels[i] != null)
            {
                voxels[i].setNeighbors(voxels, (1.0f / generator.getSteps()) + 0.01f);
            }
        }
    }
    
    private void setInitialDensity()
    {
        for(int i = 0; i < voxels.length; i++)
        {
            if(voxels[i] != null && voxels[i].isInside())
            {
                voxels[i].setDensity(0.5f);
            }
        }
    }
    
    public void rightSnow()
    {
        int k = 0;

        this.activeRightVoxels = new Voxel[countActiveRightVoxels(voxels)];
        // snow from above
        // mark Voxels as active if they have a topNeighbor 
        for(int i = 0; i < voxels.length; i++)
        {
            if((voxels[i] != null && (voxels[i].getSnow() || voxels[i].isInside())) && voxels[i].hasRightNeighbor() && !voxels[i].getRightNeighbor().getSnow())

            {
                activeRightVoxels[k++] = voxels[i];
            }
        }
        
        // let it snow
        if(Surface.cold)
        {
            int snowflakes = 50;
            for(int j = 0; j < snowflakes; j++)
            {
                int index = (int)(Math.random() * activeRightVoxels.length);
                this.activeRightVoxels[index].raiseDensity(SNOWFLAKE);
                if(this.activeRightVoxels[index].getDensity() > 1.0f && this.activeRightVoxels[index].getRightNeighbor() != null)
                {
                    this.activeRightVoxels[index].getRightNeighbor().setSnow();
                    this.activeRightVoxels[index] = this.activeRightVoxels[index].getRightNeighbor();
                }   
            }
        }else
        {
        
            // melting snow
            for(int j = 0; j < 10; j++)
            {
                int index = (int)(Math.random() * activeRightVoxels.length);
                Voxel v = this.activeRightVoxels[index];
                v.raiseDensity(-0.1f);
                
                Voxel right = v.getRightNeighbor();
                Voxel left = v.getLeftNeighbor();
                Voxel front = v.getFrontNeighbor();
                Voxel back = v.getBackNeighbor();
                Voxel bottom = v.getBottomNeighbor();
                
                if((v.getDensity() < 0.0f && left != null) )//||

//                   (v.getSnow() && right != null && !right.getSnow() && left != null && 
//                    !left.getSnow() && front != null && !front.getSnow() &&
//                    back != null && !back.getSnow() && bottom != null))

                {
                    v.removeSnow();
                    this.activeRightVoxels[index] = v.getRightNeighbor();
                }
            }
        }
    }
    
    private int countActiveRightVoxels(Voxel[] voxels)
    {
        int count = 0;
        for(int i = 0; i < voxels.length; i++)
        {
            if((voxels[i] != null && (voxels[i].getSnow() || voxels[i].isInside()) && voxels[i].hasRightNeighbor() && !voxels[i].getRightNeighbor().getSnow()))// ||
//               (voxels[i] != null && !voxels[i].getSnow() && !voxels[i].hasBottomNeighbor() && !voxels[i].getTopNeighbor().getSnow()) ||
//               (voxels[i] != null && voxels[i].getInside() && !voxels[i].getTopNeighbor().getInside()))
            {
                count++;
            }
        }
        return count;
    }
    
    // without snow stability
    public void plainTopSnow()
    {
        int k = 0;

        this.activeVoxels = new Voxel[countActiveVoxels(voxels)];

        // snow from above
        // mark Voxels as active if they have a topNeighbor 
        for(int i = 0; i < voxels.length; i++)
        {
            if((voxels[i] != null && voxels[i].getSnow() && !voxels[i].getTopNeighbor().getSnow()) ||
               (voxels[i] != null && !voxels[i].hasBottomNeighbor() && !voxels[i].getTopNeighbor().getSnow() && !voxels[i].getSnow()) ||
               (voxels[i] != null && voxels[i].isInside() && !voxels[i].getTopNeighbor().isInside()))
            {
                activeVoxels[k++] = voxels[i];
            }
        }
        
        // let it snow
        
        int snowflakes = 20;
        for(int j = 0; j < snowflakes; j++)
        {
            int index = (int)(Math.random() * activeVoxels.length);


//                this.activeVoxels[index] = singleTopSnow(this.activeVoxels[index]);
            this.activeVoxels[index].raiseDensity(SNOWFLAKE);
            if(this.activeVoxels[index].getDensity() > 1.0f && this.activeVoxels[index].getTopNeighbor() != null)
            {
                this.activeVoxels[index].getTopNeighbor().setSnow();
                this.activeVoxels[index] = this.activeVoxels[index].getTopNeighbor();
            }

            // make a visible border of the scene
            if(!this.activeVoxels[index].hasSameLevelNeighbors())
            {
                this.activeVoxels[index].removeSnow();
            }
        }
    }
    
    // with snow stability
    public void topSnow()
    {
        int k = 0;

        this.activeVoxels = new Voxel[countActiveVoxels(voxels)];

        // snow from above
        // mark Voxels as active if they have a topNeighbor 
        for(int i = 0; i < voxels.length; i++)
        {
            if((voxels[i] != null && voxels[i].getSnow() && !voxels[i].getTopNeighbor().getSnow()) ||
               (voxels[i] != null && !voxels[i].hasBottomNeighbor() && !voxels[i].getTopNeighbor().getSnow() && !voxels[i].getSnow()) ||
               (voxels[i] != null && voxels[i].isInside() && !voxels[i].getTopNeighbor().isInside()))
            {
                activeVoxels[k++] = voxels[i];
            }
        }
        
        // let it snow
        if(Surface.cold)
        {
            int snowflakes = 20;
            for(int j = 0; j < snowflakes; j++)
            {
                int index = (int)(Math.random() * activeVoxels.length);
                this.activeVoxels[index] = singleTopSnow(this.activeVoxels[index]);
                // make a visible border of the scene
                if(!this.activeVoxels[index].hasSameLevelNeighbors())
                {
                    this.activeVoxels[index].removeSnow();
                }
            }
        }else
        {
        
            // melting snow
            for(int j = 0; j < 20; j++)
            {
                int index = (int)(Math.random() * activeVoxels.length);
                Voxel v = this.activeVoxels[index];
                v.raiseDensity(-0.1f);
                
                Voxel right = v.getRightNeighbor();
                Voxel left = v.getLeftNeighbor();
                Voxel front = v.getFrontNeighbor();
                Voxel back = v.getBackNeighbor();
                Voxel bottom = v.getBottomNeighbor();
                
                if((v.getDensity() < 0.0f && bottom != null) ||
//                   (!this.activeVoxels[index].getRightNeighbor().getSnow() && !this.activeVoxels[index].getLeftNeighbor().getSnow() && 
//                    !this.activeVoxels[index].getFrontNeighbor().getSnow() && !this.activeVoxels[index].getBackNeighbor().getSnow() && 
                   (v.getSnow() && right != null && !right.getSnow() && left != null && 
                    !left.getSnow() && front != null && !front.getSnow() &&
                    back != null && !back.getSnow() && bottom != null)) //||
//                     (v.countNeighborsWithSnow() <= 4 && bottom != null))
                {
                    v.removeSnow();
                    this.activeVoxels[index] = v.getBottomNeighbor();
                }
            }
        }
    }
    
    private int countActiveVoxels(Voxel[] voxels)
    {
        int count = 0;
        for(int i = 0; i < voxels.length; i++)
        {
            if((voxels[i] != null && voxels[i].getSnow() && !voxels[i].getTopNeighbor().getSnow()) ||
               (voxels[i] != null && !voxels[i].getSnow() && !voxels[i].hasBottomNeighbor() && !voxels[i].getTopNeighbor().getSnow()) ||
               (voxels[i] != null && voxels[i].isInside() && !voxels[i].getTopNeighbor().isInside()))
            {
                count++;
            }
        }
        return count;
    }
    
    private Voxel singleTopSnow(Voxel v)
    { 
        // snow just from a point-source
        v.raiseDensity(SNOWFLAKE);
        if(v.getDensity() > 1.0f && v.getTopNeighbor() != null)
        {
            v = v.getTopNeighbor();
            // magic number
            if(stabilityTest(v) >= 4)
            {
                v.removeSnow();
                return v.getBottomNeighbor();
            }

        }
        return v;
    }
    
    public void singleTopSnowIndex()
    {
            
        int index = 14627;
//            int index = 14750;
//            int index = 275;
//            
        this.voxels[index].raiseDensity(SNOWFLAKE);
        if(this.voxels[index].getDensity() > 1.0f && this.voxels[index].getTopNeighbor() != null)
        {
//                if(stabilityTest(this.voxels[index]) < 5)
            this.voxels[index] = this.voxels[index].getTopNeighbor();

            if(stabilityTest(this.voxels[index]) >= 5)
            {
                this.voxels[index].removeSnow();
                this.voxels[index] = this.voxels[index].getBottomNeighbor();
            }
        }
            
            
//            if(this.voxels[index].getDensity() <= 1.0)
//            {
//                this.voxels[index].raiseDensity(SNOWFLAKE);
//            }else
//            {
//
//
//                if(stabilityTest(this.voxels[index]) < 5)
//                {
//                    this.voxels[index] = this.voxels[index].getTopNeighbor();
//                }
//
//            }
        
        
//        for(int j = 0; j < 1; j++)
//        {
//            int index = 19050;
//            Voxel source = avalanche(this.voxels[index]);
//            source.raiseDensity(SNOWFLAKE);
//            if(source.getDensity() > 1.0f && source.getTopNeighbor() != null)
//            {
//
//                if(stabilityTest(source) >= 1) //default = 5
//                {
//                    source.setDensity(0.0f);
//                }
//            }
//        }
    }
    
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
        
        if(v.hasLeftNeighbor())
        {
            if(height(v.getLeftNeighbor()) >= STEEPNESS)
            {
                avalanche(v.getLeftNeighbor()).raiseDensity(SNOWFLAKE / 6.0f);
                stabilityTest(avalanche(v.getLeftNeighbor()));
                avalanche++;
            }
        }
        
        if(v.hasFrontNeighbor())
        {
            if(height(v.getFrontNeighbor()) >= STEEPNESS)
            {
                avalanche(v.getFrontNeighbor()).raiseDensity(SNOWFLAKE / 6.0f);
                stabilityTest(avalanche(v.getFrontNeighbor()));
                avalanche++;
            }
        }
        
        if(v.hasBackNeighbor())
        {
            if(height(v.getBackNeighbor()) >= STEEPNESS)
            {
                avalanche(v.getBackNeighbor()).raiseDensity(SNOWFLAKE / 6.0f);
                stabilityTest(avalanche(v.getBackNeighbor()));
                avalanche++;
            }
        }
        
        if(v.hasRightNeighbor() && v.getRightNeighbor().hasFrontNeighbor())
        {
            if(height(v.getRightNeighbor().getFrontNeighbor()) >= STEEPNESS)
            {
                avalanche(v.getRightNeighbor().getFrontNeighbor()).raiseDensity(SNOWFLAKE / 12.0f);
                stabilityTest(avalanche(v.getRightNeighbor().getFrontNeighbor()));
                avalanche++;
            }
        }
        
        if(v.hasLeftNeighbor() && v.getLeftNeighbor().hasFrontNeighbor())
        {
            if(height(v.getLeftNeighbor().getFrontNeighbor()) >= STEEPNESS)
            {
                avalanche(v.getLeftNeighbor().getFrontNeighbor()).raiseDensity(SNOWFLAKE / 12.0f);
                stabilityTest(avalanche(v.getLeftNeighbor().getFrontNeighbor()));
                avalanche++;
            }
        }
        
        if(v.hasRightNeighbor() && v.getRightNeighbor().hasBackNeighbor())
        {
            if(height(v.getRightNeighbor().getBackNeighbor()) >= STEEPNESS)
            {
                avalanche(v.getRightNeighbor().getBackNeighbor()).raiseDensity(SNOWFLAKE / 12.0f);
                stabilityTest(avalanche(v.getRightNeighbor().getBackNeighbor()));
                avalanche++;
            }
        }
        
        if(v.hasLeftNeighbor() && v.getLeftNeighbor().hasBackNeighbor())
        {
            if(height(v.getLeftNeighbor().getBackNeighbor()) >= STEEPNESS)
            {
                avalanche(v.getLeftNeighbor().getBackNeighbor()).raiseDensity(SNOWFLAKE / 12.0f);
                stabilityTest(avalanche(v.getLeftNeighbor().getBackNeighbor()));
                avalanche++;
            }
        }
        
        if(!v.hasSameLevelNeighbors())
        {
            v.removeSnow();
        }
        
        
        
        return avalanche;
    }
    
    
    private Voxel avalanche(Voxel v)
    {
        Voxel tmp = v;
        while(tmp.getBottomNeighbor() != null && !(tmp.getBottomNeighbor().getDensity() >= 1.0f) && 
             !tmp.getBottomNeighbor().isInside())
        {
            tmp = tmp.getBottomNeighbor();
        }
        return tmp;
    }
    
    
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
    
    
    public void setCnt(int cnt)
    {
        Surface.cnt = cnt;
    }
    
    public void setCold(boolean bool)
    {
        Surface.cold = bool;
    }
    
    public Voxel[] getVoxels()
    {
        return this.voxels;
    }
    
    public float[] getFaces()
    {
        return this.faces;
    }
    
    public float[] getNormals()
    {
        return this.normals;
    }
    
    public Voxel[] getActiveVoxels()
    {
        return this.activeRightVoxels;
    }
    
    public Generator getGenerator()
    {
        return this.generator;
    }
    
    // For any edge, if one vertex is inside of the surface and the other is outside of the surface
    //  then the edge intersects the surface
    // For each of the 8 vertices of the cube can be two possible states : either inside or outside of the surface
    // For any cube the are 2^8=256 possible sets of vertex states
    // This table lists the edges intersected by the surface for all 256 possible vertex states
    // There are 12 edges.  For each entry in the table, if edge #n is intersected, then bit #n is set to 1
    // size = [256]
    private int[] CubeEdgeFlags =
    {
        0x000, 0x109, 0x203, 0x30a, 0x406, 0x50f, 0x605, 0x70c, 0x80c, 0x905, 0xa0f, 0xb06, 0xc0a, 0xd03, 0xe09, 0xf00, 
        0x190, 0x099, 0x393, 0x29a, 0x596, 0x49f, 0x795, 0x69c, 0x99c, 0x895, 0xb9f, 0xa96, 0xd9a, 0xc93, 0xf99, 0xe90, 
        0x230, 0x339, 0x033, 0x13a, 0x636, 0x73f, 0x435, 0x53c, 0xa3c, 0xb35, 0x83f, 0x936, 0xe3a, 0xf33, 0xc39, 0xd30, 
        0x3a0, 0x2a9, 0x1a3, 0x0aa, 0x7a6, 0x6af, 0x5a5, 0x4ac, 0xbac, 0xaa5, 0x9af, 0x8a6, 0xfaa, 0xea3, 0xda9, 0xca0, 
        0x460, 0x569, 0x663, 0x76a, 0x066, 0x16f, 0x265, 0x36c, 0xc6c, 0xd65, 0xe6f, 0xf66, 0x86a, 0x963, 0xa69, 0xb60, 
        0x5f0, 0x4f9, 0x7f3, 0x6fa, 0x1f6, 0x0ff, 0x3f5, 0x2fc, 0xdfc, 0xcf5, 0xfff, 0xef6, 0x9fa, 0x8f3, 0xbf9, 0xaf0, 
        0x650, 0x759, 0x453, 0x55a, 0x256, 0x35f, 0x055, 0x15c, 0xe5c, 0xf55, 0xc5f, 0xd56, 0xa5a, 0xb53, 0x859, 0x950, 
        0x7c0, 0x6c9, 0x5c3, 0x4ca, 0x3c6, 0x2cf, 0x1c5, 0x0cc, 0xfcc, 0xec5, 0xdcf, 0xcc6, 0xbca, 0xac3, 0x9c9, 0x8c0, 
        0x8c0, 0x9c9, 0xac3, 0xbca, 0xcc6, 0xdcf, 0xec5, 0xfcc, 0x0cc, 0x1c5, 0x2cf, 0x3c6, 0x4ca, 0x5c3, 0x6c9, 0x7c0, 
        0x950, 0x859, 0xb53, 0xa5a, 0xd56, 0xc5f, 0xf55, 0xe5c, 0x15c, 0x055, 0x35f, 0x256, 0x55a, 0x453, 0x759, 0x650, 
        0xaf0, 0xbf9, 0x8f3, 0x9fa, 0xef6, 0xfff, 0xcf5, 0xdfc, 0x2fc, 0x3f5, 0x0ff, 0x1f6, 0x6fa, 0x7f3, 0x4f9, 0x5f0, 
        0xb60, 0xa69, 0x963, 0x86a, 0xf66, 0xe6f, 0xd65, 0xc6c, 0x36c, 0x265, 0x16f, 0x066, 0x76a, 0x663, 0x569, 0x460, 
        0xca0, 0xda9, 0xea3, 0xfaa, 0x8a6, 0x9af, 0xaa5, 0xbac, 0x4ac, 0x5a5, 0x6af, 0x7a6, 0x0aa, 0x1a3, 0x2a9, 0x3a0, 
        0xd30, 0xc39, 0xf33, 0xe3a, 0x936, 0x83f, 0xb35, 0xa3c, 0x53c, 0x435, 0x73f, 0x636, 0x13a, 0x033, 0x339, 0x230, 
        0xe90, 0xf99, 0xc93, 0xd9a, 0xa96, 0xb9f, 0x895, 0x99c, 0x69c, 0x795, 0x49f, 0x596, 0x29a, 0x393, 0x099, 0x190, 
        0xf00, 0xe09, 0xd03, 0xc0a, 0xb06, 0xa0f, 0x905, 0x80c, 0x70c, 0x605, 0x50f, 0x406, 0x30a, 0x203, 0x109, 0x000
    };

    //  For each of the possible vertex states listed in aiCubeEdgeFlags there is a specific triangulation
    //  of the edge intersection points.  a2iTriangleConnectionTable lists all of them in the form of
    //  0-5 edge triples with the list terminated by the invalid value -1.
    //  For example: TriangleLookupTable[3] list the 2 triangles formed when corner[0] 
    //  and corner[1] are inside of the surface, but the rest of the cube is not.
    //
    //  I found this table in an example program someone wrote long ago.  It was probably generated by hand
    //  size = [256][16]
    private int[][] TriangleLookupTable =  
    {
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {0, 8, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {0, 1, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {1, 8, 3, 9, 8, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {1, 2, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {0, 8, 3, 1, 2, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {9, 2, 10, 0, 2, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {2, 8, 3, 2, 10, 8, 10, 9, 8, -1, -1, -1, -1, -1, -1, -1},
        {3, 11, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {0, 11, 2, 8, 11, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {1, 9, 0, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {1, 11, 2, 1, 9, 11, 9, 8, 11, -1, -1, -1, -1, -1, -1, -1},
        {3, 10, 1, 11, 10, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {0, 10, 1, 0, 8, 10, 8, 11, 10, -1, -1, -1, -1, -1, -1, -1},
        {3, 9, 0, 3, 11, 9, 11, 10, 9, -1, -1, -1, -1, -1, -1, -1},
        {9, 8, 10, 10, 8, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {4, 7, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {4, 3, 0, 7, 3, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {0, 1, 9, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {4, 1, 9, 4, 7, 1, 7, 3, 1, -1, -1, -1, -1, -1, -1, -1},
        {1, 2, 10, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {3, 4, 7, 3, 0, 4, 1, 2, 10, -1, -1, -1, -1, -1, -1, -1},
        {9, 2, 10, 9, 0, 2, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1},
        {2, 10, 9, 2, 9, 7, 2, 7, 3, 7, 9, 4, -1, -1, -1, -1},
        {8, 4, 7, 3, 11, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {11, 4, 7, 11, 2, 4, 2, 0, 4, -1, -1, -1, -1, -1, -1, -1},
        {9, 0, 1, 8, 4, 7, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1},
        {4, 7, 11, 9, 4, 11, 9, 11, 2, 9, 2, 1, -1, -1, -1, -1},
        {3, 10, 1, 3, 11, 10, 7, 8, 4, -1, -1, -1, -1, -1, -1, -1},
        {1, 11, 10, 1, 4, 11, 1, 0, 4, 7, 11, 4, -1, -1, -1, -1},
        {4, 7, 8, 9, 0, 11, 9, 11, 10, 11, 0, 3, -1, -1, -1, -1},
        {4, 7, 11, 4, 11, 9, 9, 11, 10, -1, -1, -1, -1, -1, -1, -1},
        {9, 5, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {9, 5, 4, 0, 8, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {0, 5, 4, 1, 5, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {8, 5, 4, 8, 3, 5, 3, 1, 5, -1, -1, -1, -1, -1, -1, -1},
        {1, 2, 10, 9, 5, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {3, 0, 8, 1, 2, 10, 4, 9, 5, -1, -1, -1, -1, -1, -1, -1},
        {5, 2, 10, 5, 4, 2, 4, 0, 2, -1, -1, -1, -1, -1, -1, -1},
        {2, 10, 5, 3, 2, 5, 3, 5, 4, 3, 4, 8, -1, -1, -1, -1},
        {9, 5, 4, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {0, 11, 2, 0, 8, 11, 4, 9, 5, -1, -1, -1, -1, -1, -1, -1},
        {0, 5, 4, 0, 1, 5, 2, 3, 11, -1, -1, -1, -1, -1, -1, -1},
        {2, 1, 5, 2, 5, 8, 2, 8, 11, 4, 8, 5, -1, -1, -1, -1},
        {10, 3, 11, 10, 1, 3, 9, 5, 4, -1, -1, -1, -1, -1, -1, -1},
        {4, 9, 5, 0, 8, 1, 8, 10, 1, 8, 11, 10, -1, -1, -1, -1},
        {5, 4, 0, 5, 0, 11, 5, 11, 10, 11, 0, 3, -1, -1, -1, -1},
        {5, 4, 8, 5, 8, 10, 10, 8, 11, -1, -1, -1, -1, -1, -1, -1},
        {9, 7, 8, 5, 7, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {9, 3, 0, 9, 5, 3, 5, 7, 3, -1, -1, -1, -1, -1, -1, -1},
        {0, 7, 8, 0, 1, 7, 1, 5, 7, -1, -1, -1, -1, -1, -1, -1},
        {1, 5, 3, 3, 5, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {9, 7, 8, 9, 5, 7, 10, 1, 2, -1, -1, -1, -1, -1, -1, -1},
        {10, 1, 2, 9, 5, 0, 5, 3, 0, 5, 7, 3, -1, -1, -1, -1},
        {8, 0, 2, 8, 2, 5, 8, 5, 7, 10, 5, 2, -1, -1, -1, -1},
        {2, 10, 5, 2, 5, 3, 3, 5, 7, -1, -1, -1, -1, -1, -1, -1},
        {7, 9, 5, 7, 8, 9, 3, 11, 2, -1, -1, -1, -1, -1, -1, -1},
        {9, 5, 7, 9, 7, 2, 9, 2, 0, 2, 7, 11, -1, -1, -1, -1},
        {2, 3, 11, 0, 1, 8, 1, 7, 8, 1, 5, 7, -1, -1, -1, -1},
        {11, 2, 1, 11, 1, 7, 7, 1, 5, -1, -1, -1, -1, -1, -1, -1},
        {9, 5, 8, 8, 5, 7, 10, 1, 3, 10, 3, 11, -1, -1, -1, -1},
        {5, 7, 0, 5, 0, 9, 7, 11, 0, 1, 0, 10, 11, 10, 0, -1},
        {11, 10, 0, 11, 0, 3, 10, 5, 0, 8, 0, 7, 5, 7, 0, -1},
        {11, 10, 5, 7, 11, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {10, 6, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {0, 8, 3, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {9, 0, 1, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {1, 8, 3, 1, 9, 8, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1},
        {1, 6, 5, 2, 6, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {1, 6, 5, 1, 2, 6, 3, 0, 8, -1, -1, -1, -1, -1, -1, -1},
        {9, 6, 5, 9, 0, 6, 0, 2, 6, -1, -1, -1, -1, -1, -1, -1},
        {5, 9, 8, 5, 8, 2, 5, 2, 6, 3, 2, 8, -1, -1, -1, -1},
        {2, 3, 11, 10, 6, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {11, 0, 8, 11, 2, 0, 10, 6, 5, -1, -1, -1, -1, -1, -1, -1},
        {0, 1, 9, 2, 3, 11, 5, 10, 6, -1, -1, -1, -1, -1, -1, -1},
        {5, 10, 6, 1, 9, 2, 9, 11, 2, 9, 8, 11, -1, -1, -1, -1},
        {6, 3, 11, 6, 5, 3, 5, 1, 3, -1, -1, -1, -1, -1, -1, -1},
        {0, 8, 11, 0, 11, 5, 0, 5, 1, 5, 11, 6, -1, -1, -1, -1},
        {3, 11, 6, 0, 3, 6, 0, 6, 5, 0, 5, 9, -1, -1, -1, -1},
        {6, 5, 9, 6, 9, 11, 11, 9, 8, -1, -1, -1, -1, -1, -1, -1},
        {5, 10, 6, 4, 7, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {4, 3, 0, 4, 7, 3, 6, 5, 10, -1, -1, -1, -1, -1, -1, -1},
        {1, 9, 0, 5, 10, 6, 8, 4, 7, -1, -1, -1, -1, -1, -1, -1},
        {10, 6, 5, 1, 9, 7, 1, 7, 3, 7, 9, 4, -1, -1, -1, -1},
        {6, 1, 2, 6, 5, 1, 4, 7, 8, -1, -1, -1, -1, -1, -1, -1},
        {1, 2, 5, 5, 2, 6, 3, 0, 4, 3, 4, 7, -1, -1, -1, -1},
        {8, 4, 7, 9, 0, 5, 0, 6, 5, 0, 2, 6, -1, -1, -1, -1},
        {7, 3, 9, 7, 9, 4, 3, 2, 9, 5, 9, 6, 2, 6, 9, -1},
        {3, 11, 2, 7, 8, 4, 10, 6, 5, -1, -1, -1, -1, -1, -1, -1},
        {5, 10, 6, 4, 7, 2, 4, 2, 0, 2, 7, 11, -1, -1, -1, -1},
        {0, 1, 9, 4, 7, 8, 2, 3, 11, 5, 10, 6, -1, -1, -1, -1},
        {9, 2, 1, 9, 11, 2, 9, 4, 11, 7, 11, 4, 5, 10, 6, -1},
        {8, 4, 7, 3, 11, 5, 3, 5, 1, 5, 11, 6, -1, -1, -1, -1},
        {5, 1, 11, 5, 11, 6, 1, 0, 11, 7, 11, 4, 0, 4, 11, -1},
        {0, 5, 9, 0, 6, 5, 0, 3, 6, 11, 6, 3, 8, 4, 7, -1},
        {6, 5, 9, 6, 9, 11, 4, 7, 9, 7, 11, 9, -1, -1, -1, -1},
        {10, 4, 9, 6, 4, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {4, 10, 6, 4, 9, 10, 0, 8, 3, -1, -1, -1, -1, -1, -1, -1},
        {10, 0, 1, 10, 6, 0, 6, 4, 0, -1, -1, -1, -1, -1, -1, -1},
        {8, 3, 1, 8, 1, 6, 8, 6, 4, 6, 1, 10, -1, -1, -1, -1},
        {1, 4, 9, 1, 2, 4, 2, 6, 4, -1, -1, -1, -1, -1, -1, -1},
        {3, 0, 8, 1, 2, 9, 2, 4, 9, 2, 6, 4, -1, -1, -1, -1},
        {0, 2, 4, 4, 2, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {8, 3, 2, 8, 2, 4, 4, 2, 6, -1, -1, -1, -1, -1, -1, -1},
        {10, 4, 9, 10, 6, 4, 11, 2, 3, -1, -1, -1, -1, -1, -1, -1},
        {0, 8, 2, 2, 8, 11, 4, 9, 10, 4, 10, 6, -1, -1, -1, -1},
        {3, 11, 2, 0, 1, 6, 0, 6, 4, 6, 1, 10, -1, -1, -1, -1},
        {6, 4, 1, 6, 1, 10, 4, 8, 1, 2, 1, 11, 8, 11, 1, -1},
        {9, 6, 4, 9, 3, 6, 9, 1, 3, 11, 6, 3, -1, -1, -1, -1},
        {8, 11, 1, 8, 1, 0, 11, 6, 1, 9, 1, 4, 6, 4, 1, -1},
        {3, 11, 6, 3, 6, 0, 0, 6, 4, -1, -1, -1, -1, -1, -1, -1},
        {6, 4, 8, 11, 6, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {7, 10, 6, 7, 8, 10, 8, 9, 10, -1, -1, -1, -1, -1, -1, -1},
        {0, 7, 3, 0, 10, 7, 0, 9, 10, 6, 7, 10, -1, -1, -1, -1},
        {10, 6, 7, 1, 10, 7, 1, 7, 8, 1, 8, 0, -1, -1, -1, -1},
        {10, 6, 7, 10, 7, 1, 1, 7, 3, -1, -1, -1, -1, -1, -1, -1},
        {1, 2, 6, 1, 6, 8, 1, 8, 9, 8, 6, 7, -1, -1, -1, -1},
        {2, 6, 9, 2, 9, 1, 6, 7, 9, 0, 9, 3, 7, 3, 9, -1},
        {7, 8, 0, 7, 0, 6, 6, 0, 2, -1, -1, -1, -1, -1, -1, -1},
        {7, 3, 2, 6, 7, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {2, 3, 11, 10, 6, 8, 10, 8, 9, 8, 6, 7, -1, -1, -1, -1},
        {2, 0, 7, 2, 7, 11, 0, 9, 7, 6, 7, 10, 9, 10, 7, -1},
        {1, 8, 0, 1, 7, 8, 1, 10, 7, 6, 7, 10, 2, 3, 11, -1},
        {11, 2, 1, 11, 1, 7, 10, 6, 1, 6, 7, 1, -1, -1, -1, -1},
        {8, 9, 6, 8, 6, 7, 9, 1, 6, 11, 6, 3, 1, 3, 6, -1},
        {0, 9, 1, 11, 6, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {7, 8, 0, 7, 0, 6, 3, 11, 0, 11, 6, 0, -1, -1, -1, -1},
        {7, 11, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {7, 6, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {3, 0, 8, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {0, 1, 9, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {8, 1, 9, 8, 3, 1, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1},
        {10, 1, 2, 6, 11, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {1, 2, 10, 3, 0, 8, 6, 11, 7, -1, -1, -1, -1, -1, -1, -1},
        {2, 9, 0, 2, 10, 9, 6, 11, 7, -1, -1, -1, -1, -1, -1, -1},
        {6, 11, 7, 2, 10, 3, 10, 8, 3, 10, 9, 8, -1, -1, -1, -1},
        {7, 2, 3, 6, 2, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {7, 0, 8, 7, 6, 0, 6, 2, 0, -1, -1, -1, -1, -1, -1, -1},
        {2, 7, 6, 2, 3, 7, 0, 1, 9, -1, -1, -1, -1, -1, -1, -1},
        {1, 6, 2, 1, 8, 6, 1, 9, 8, 8, 7, 6, -1, -1, -1, -1},
        {10, 7, 6, 10, 1, 7, 1, 3, 7, -1, -1, -1, -1, -1, -1, -1},
        {10, 7, 6, 1, 7, 10, 1, 8, 7, 1, 0, 8, -1, -1, -1, -1},
        {0, 3, 7, 0, 7, 10, 0, 10, 9, 6, 10, 7, -1, -1, -1, -1},
        {7, 6, 10, 7, 10, 8, 8, 10, 9, -1, -1, -1, -1, -1, -1, -1},
        {6, 8, 4, 11, 8, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {3, 6, 11, 3, 0, 6, 0, 4, 6, -1, -1, -1, -1, -1, -1, -1},
        {8, 6, 11, 8, 4, 6, 9, 0, 1, -1, -1, -1, -1, -1, -1, -1},
        {9, 4, 6, 9, 6, 3, 9, 3, 1, 11, 3, 6, -1, -1, -1, -1},
        {6, 8, 4, 6, 11, 8, 2, 10, 1, -1, -1, -1, -1, -1, -1, -1},
        {1, 2, 10, 3, 0, 11, 0, 6, 11, 0, 4, 6, -1, -1, -1, -1},
        {4, 11, 8, 4, 6, 11, 0, 2, 9, 2, 10, 9, -1, -1, -1, -1},
        {10, 9, 3, 10, 3, 2, 9, 4, 3, 11, 3, 6, 4, 6, 3, -1},
        {8, 2, 3, 8, 4, 2, 4, 6, 2, -1, -1, -1, -1, -1, -1, -1},
        {0, 4, 2, 4, 6, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {1, 9, 0, 2, 3, 4, 2, 4, 6, 4, 3, 8, -1, -1, -1, -1},
        {1, 9, 4, 1, 4, 2, 2, 4, 6, -1, -1, -1, -1, -1, -1, -1},
        {8, 1, 3, 8, 6, 1, 8, 4, 6, 6, 10, 1, -1, -1, -1, -1},
        {10, 1, 0, 10, 0, 6, 6, 0, 4, -1, -1, -1, -1, -1, -1, -1},
        {4, 6, 3, 4, 3, 8, 6, 10, 3, 0, 3, 9, 10, 9, 3, -1},
        {10, 9, 4, 6, 10, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {4, 9, 5, 7, 6, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {0, 8, 3, 4, 9, 5, 11, 7, 6, -1, -1, -1, -1, -1, -1, -1},
        {5, 0, 1, 5, 4, 0, 7, 6, 11, -1, -1, -1, -1, -1, -1, -1},
        {11, 7, 6, 8, 3, 4, 3, 5, 4, 3, 1, 5, -1, -1, -1, -1},
        {9, 5, 4, 10, 1, 2, 7, 6, 11, -1, -1, -1, -1, -1, -1, -1},
        {6, 11, 7, 1, 2, 10, 0, 8, 3, 4, 9, 5, -1, -1, -1, -1},
        {7, 6, 11, 5, 4, 10, 4, 2, 10, 4, 0, 2, -1, -1, -1, -1},
        {3, 4, 8, 3, 5, 4, 3, 2, 5, 10, 5, 2, 11, 7, 6, -1},
        {7, 2, 3, 7, 6, 2, 5, 4, 9, -1, -1, -1, -1, -1, -1, -1},
        {9, 5, 4, 0, 8, 6, 0, 6, 2, 6, 8, 7, -1, -1, -1, -1},
        {3, 6, 2, 3, 7, 6, 1, 5, 0, 5, 4, 0, -1, -1, -1, -1},
        {6, 2, 8, 6, 8, 7, 2, 1, 8, 4, 8, 5, 1, 5, 8, -1},
        {9, 5, 4, 10, 1, 6, 1, 7, 6, 1, 3, 7, -1, -1, -1, -1},
        {1, 6, 10, 1, 7, 6, 1, 0, 7, 8, 7, 0, 9, 5, 4, -1},
        {4, 0, 10, 4, 10, 5, 0, 3, 10, 6, 10, 7, 3, 7, 10, -1},
        {7, 6, 10, 7, 10, 8, 5, 4, 10, 4, 8, 10, -1, -1, -1, -1},
        {6, 9, 5, 6, 11, 9, 11, 8, 9, -1, -1, -1, -1, -1, -1, -1},
        {3, 6, 11, 0, 6, 3, 0, 5, 6, 0, 9, 5, -1, -1, -1, -1},
        {0, 11, 8, 0, 5, 11, 0, 1, 5, 5, 6, 11, -1, -1, -1, -1},
        {6, 11, 3, 6, 3, 5, 5, 3, 1, -1, -1, -1, -1, -1, -1, -1},
        {1, 2, 10, 9, 5, 11, 9, 11, 8, 11, 5, 6, -1, -1, -1, -1},
        {0, 11, 3, 0, 6, 11, 0, 9, 6, 5, 6, 9, 1, 2, 10, -1},
        {11, 8, 5, 11, 5, 6, 8, 0, 5, 10, 5, 2, 0, 2, 5, -1},
        {6, 11, 3, 6, 3, 5, 2, 10, 3, 10, 5, 3, -1, -1, -1, -1},
        {5, 8, 9, 5, 2, 8, 5, 6, 2, 3, 8, 2, -1, -1, -1, -1},
        {9, 5, 6, 9, 6, 0, 0, 6, 2, -1, -1, -1, -1, -1, -1, -1},
        {1, 5, 8, 1, 8, 0, 5, 6, 8, 3, 8, 2, 6, 2, 8, -1},
        {1, 5, 6, 2, 1, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {1, 3, 6, 1, 6, 10, 3, 8, 6, 5, 6, 9, 8, 9, 6, -1},
        {10, 1, 0, 10, 0, 6, 9, 5, 0, 5, 6, 0, -1, -1, -1, -1},
        {0, 3, 8, 5, 6, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {10, 5, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {11, 5, 10, 7, 5, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {11, 5, 10, 11, 7, 5, 8, 3, 0, -1, -1, -1, -1, -1, -1, -1},
        {5, 11, 7, 5, 10, 11, 1, 9, 0, -1, -1, -1, -1, -1, -1, -1},
        {10, 7, 5, 10, 11, 7, 9, 8, 1, 8, 3, 1, -1, -1, -1, -1},
        {11, 1, 2, 11, 7, 1, 7, 5, 1, -1, -1, -1, -1, -1, -1, -1},
        {0, 8, 3, 1, 2, 7, 1, 7, 5, 7, 2, 11, -1, -1, -1, -1},
        {9, 7, 5, 9, 2, 7, 9, 0, 2, 2, 11, 7, -1, -1, -1, -1},
        {7, 5, 2, 7, 2, 11, 5, 9, 2, 3, 2, 8, 9, 8, 2, -1},
        {2, 5, 10, 2, 3, 5, 3, 7, 5, -1, -1, -1, -1, -1, -1, -1},
        {8, 2, 0, 8, 5, 2, 8, 7, 5, 10, 2, 5, -1, -1, -1, -1},
        {9, 0, 1, 5, 10, 3, 5, 3, 7, 3, 10, 2, -1, -1, -1, -1},
        {9, 8, 2, 9, 2, 1, 8, 7, 2, 10, 2, 5, 7, 5, 2, -1},
        {1, 3, 5, 3, 7, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {0, 8, 7, 0, 7, 1, 1, 7, 5, -1, -1, -1, -1, -1, -1, -1},
        {9, 0, 3, 9, 3, 5, 5, 3, 7, -1, -1, -1, -1, -1, -1, -1},
        {9, 8, 7, 5, 9, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {5, 8, 4, 5, 10, 8, 10, 11, 8, -1, -1, -1, -1, -1, -1, -1},
        {5, 0, 4, 5, 11, 0, 5, 10, 11, 11, 3, 0, -1, -1, -1, -1},
        {0, 1, 9, 8, 4, 10, 8, 10, 11, 10, 4, 5, -1, -1, -1, -1},
        {10, 11, 4, 10, 4, 5, 11, 3, 4, 9, 4, 1, 3, 1, 4, -1},
        {2, 5, 1, 2, 8, 5, 2, 11, 8, 4, 5, 8, -1, -1, -1, -1},
        {0, 4, 11, 0, 11, 3, 4, 5, 11, 2, 11, 1, 5, 1, 11, -1},
        {0, 2, 5, 0, 5, 9, 2, 11, 5, 4, 5, 8, 11, 8, 5, -1},
        {9, 4, 5, 2, 11, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {2, 5, 10, 3, 5, 2, 3, 4, 5, 3, 8, 4, -1, -1, -1, -1},
        {5, 10, 2, 5, 2, 4, 4, 2, 0, -1, -1, -1, -1, -1, -1, -1},
        {3, 10, 2, 3, 5, 10, 3, 8, 5, 4, 5, 8, 0, 1, 9, -1},
        {5, 10, 2, 5, 2, 4, 1, 9, 2, 9, 4, 2, -1, -1, -1, -1},
        {8, 4, 5, 8, 5, 3, 3, 5, 1, -1, -1, -1, -1, -1, -1, -1},
        {0, 4, 5, 1, 0, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {8, 4, 5, 8, 5, 3, 9, 0, 5, 0, 3, 5, -1, -1, -1, -1},
        {9, 4, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {4, 11, 7, 4, 9, 11, 9, 10, 11, -1, -1, -1, -1, -1, -1, -1},
        {0, 8, 3, 4, 9, 7, 9, 11, 7, 9, 10, 11, -1, -1, -1, -1},
        {1, 10, 11, 1, 11, 4, 1, 4, 0, 7, 4, 11, -1, -1, -1, -1},
        {3, 1, 4, 3, 4, 8, 1, 10, 4, 7, 4, 11, 10, 11, 4, -1},
        {4, 11, 7, 9, 11, 4, 9, 2, 11, 9, 1, 2, -1, -1, -1, -1},
        {9, 7, 4, 9, 11, 7, 9, 1, 11, 2, 11, 1, 0, 8, 3, -1},
        {11, 7, 4, 11, 4, 2, 2, 4, 0, -1, -1, -1, -1, -1, -1, -1},
        {11, 7, 4, 11, 4, 2, 8, 3, 4, 3, 2, 4, -1, -1, -1, -1},
        {2, 9, 10, 2, 7, 9, 2, 3, 7, 7, 4, 9, -1, -1, -1, -1},
        {9, 10, 7, 9, 7, 4, 10, 2, 7, 8, 7, 0, 2, 0, 7, -1},
        {3, 7, 10, 3, 10, 2, 7, 4, 10, 1, 10, 0, 4, 0, 10, -1},
        {1, 10, 2, 8, 7, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {4, 9, 1, 4, 1, 7, 7, 1, 3, -1, -1, -1, -1, -1, -1, -1},
        {4, 9, 1, 4, 1, 7, 0, 8, 1, 8, 7, 1, -1, -1, -1, -1},
        {4, 0, 3, 7, 4, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {4, 8, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {9, 10, 8, 10, 11, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {3, 0, 9, 3, 9, 11, 11, 9, 10, -1, -1, -1, -1, -1, -1, -1},
        {0, 1, 10, 0, 10, 8, 8, 10, 11, -1, -1, -1, -1, -1, -1, -1},
        {3, 1, 10, 11, 3, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {1, 2, 11, 1, 11, 9, 9, 11, 8, -1, -1, -1, -1, -1, -1, -1},
        {3, 0, 9, 3, 9, 11, 1, 2, 9, 2, 11, 9, -1, -1, -1, -1},
        {0, 2, 11, 8, 0, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {3, 2, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {2, 3, 8, 2, 8, 10, 10, 8, 9, -1, -1, -1, -1, -1, -1, -1},
        {9, 10, 2, 0, 9, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {2, 3, 8, 2, 8, 10, 0, 1, 8, 1, 10, 8, -1, -1, -1, -1},
        {1, 10, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {1, 3, 8, 9, 1, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {0, 9, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {0, 3, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}
    };
}
