package model;

import view.parser.ObjParser;

/**
 * Class to fill up the scene with Voxels. Sets Voxels in the whole scene
 * (complete xyz-range) and cuts out the Voxels where objects are.
 * In addition to that there are Voxels set on each surface of the objects.
 *
 * @verison 25.10.2011
 * @author manschwa
 */
public class Generator
{
    /** Array to store all Voxels */
    private Voxel[] voxels;
    /** Array which contains the scene */
    private float[] vertices;
    /** constant number of steps per unit (granularity of the voxel-density) */
    private static final int STEPS = 10;
    /** Array which contains the faces' vertices */
    private int[] faces;

    public Generator(float[] vertices, int[] faces)
    {
        this.vertices = vertices;
        this.faces = faces;
        this.voxels = new Voxel[calculateNumberOfVoxels(this.vertices)];
        scan(this.vertices);
    }
    
    /**
     * Constructor which parses a scene, calculates the Voxel[] voxels size
     * and scans the scene to fill it up with Voxels.
     */
    public Generator(String path)
    {
        // Parser reads out scene (*.obj)
        ObjParser op = new ObjParser(path);
        this.vertices = op.getVertices();
        this.faces = op.getFaces();
        System.out.println("Size of vertices-array: " + vertices.length);

        // calculate size of Voxel[] voxels (every 0.1 "m")
        voxels = new Voxel[calculateNumberOfVoxels(vertices)];
        System.out.println("Size of voxel-array: " + voxels.length);
        scan(vertices);
    }

    /**
     * Calculates the size of the array which should holds the Voxels.
     * @param vertices float[] which represents the scene.
     * @return Needed size of the Voxel[].
     */
    private int calculateNumberOfVoxels(float[] vertices)
    {
        // granularity of the voxel-density (every 1/steps units)

        // "+ 1" because you need 11 points to get 10 parts
        float heigth = ((calculateMaxY(vertices) - calculateMinY(vertices)) * STEPS) + 1;
        float width = ((calculateMaxX(vertices) - calculateMinX(vertices)) * STEPS) + 1;
        float depth = ((calculateMaxZ(vertices) - calculateMinZ(vertices)) * STEPS) + 1;
        
        // make the array a little bigger, so that we do not run out of bounds
        return (int) ((heigth + 0.5f) * (width + 0.5f) * (depth + 0.5f));
    }

    /**
     * This method calculates where the voxels have to be set and therefore 
     * calls the setVoxel()-method.
     * @param vertices Vertices of the given scene.
     */
    private void scan(float[] vertices)
    {
        for (float z = calculateMinZ(vertices); z <= calculateMaxZ(vertices) + (0.5f / STEPS); z += 1.0f / STEPS)
        {
            // calculate the edges just once for every z-coordinate
            float[] edges = edges(z);

            for (float x = calculateMinX(vertices); x <= calculateMaxX(vertices) + (0.5f / STEPS); x += 1.0f / STEPS)
            {
                for (float y = calculateMinY(vertices); y <=  calculateMaxY(vertices) + (0.5f / STEPS); y += 1.0f / STEPS)
                {
//                    if((pointInPolygonX(x, y, z, edges) || pointInPolygonY(x, y, z, edges)))
                    {
                        setVoxel((float) x , (float) y , (float) z, edges);
                    }
                }
            }
        }      
    }

    /**
     * Sets a voxel by adding it to the Voxel[] voxels.
     */
    private void setVoxel(float x, float y, float z, float[] edges)
    {
        // add the new Voxel to the end of the array
        int i = 0;
        while (voxels[i] != null)
        {
            // make sure every Voxel just exists once, no doubles
            if(voxels[i].equals(new Voxel(x, y, z)))
            {
               return;
            }
            i++;
        }

        voxels[i] = new Voxel(x, y, z);
        
        if(pointInPolygonX(x, y, z, edges) || pointInPolygonY(x, y, z, edges))
        {
            voxels[i].setSnow();
        }
    }
    
    /**
     * Checks if the current Voxel (represented through x,y,z) is inside an
     * object or not using the Point in Polygon-Algorithm.
     * Sets also a Voxel directly on an edge.
     * 
     * @param x x-coordinate of the Voxel.
     * @param y y-coordinate of the Voxel.
     * @param z z-coordinate of the Voxel to set a Voxel.
     * @param edges which are intersections of faces with the current plane.
     * @return true if the coordinates are inside an object.
     */
    private boolean pointInPolygonX(float x, float y, float z, float[] edges)
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
                float sx = ((float) (y * (x2 - x1) - y1 * x2 + y2 * x1) / (float) (y2 - y1));
                
                if(sx >= x)
                {
                    inside = !inside;
//                    setVoxel(sx, y, z);
                }
            }
        }
        
        return inside;
    }
    
        /**
     * Checks if the current Voxel (represented through x,y,z) is inside an
     * object or not using the Point in Polygon-Algorithm.
     * Sets also a Voxel directly on an edge.
     * 
     * @param x x-coordinate of the Voxel.
     * @param y y-coordinate of the Voxel.
     * @param z z-coordinate of the Voxel to set a Voxel.
     * @param edges which are intersections of faces with the current plane.
     * @return true if the coordinates are inside an object.
     */
    private boolean pointInPolygonY(float x, float y, float z, float[] edges)
    {
        boolean inside = false;
        for(int i = 0; i < edges.length; i += 4)
        {
            float x1 = edges[i];
            float y1 = edges[i + 1];
            float x2 = edges[i + 2];
            float y2 = edges[i + 3];
            
            boolean startOver = x1 >= x;
            boolean endOver = x2 >= x;
            
            if(startOver != endOver)
            {
                float sy = ((float) (x * (y2 - y1) - x1 * y2 + x2 * y1) / (float) (x2 - x1));
                
                if(sy >= y)
                {
                    inside = !inside;
//                    setVoxel(x, sy, z);
                }
            }
        }
        
        return inside;
    }
    
    
    /**
     * Calculates all edges at heigth z createt by any object in the y-x-plane.
     * This Method is used to perform the Point in Polygon algorithm afterwards.
     * @param z The heigth (z-coordinate) of the y-x-plane.
     * @return Array of indices that represent the edges of all intersecting
     *         faces in the scene (in the following form: 
     *         [x1e1,y1e1,x2e1,y2e1,x1e2,y1e2,x2e2,y2e2, ... ,x2en,y2en]).
     *         Every edge has two points and therefore 4 vertices.
     */
    public float[] edges(float z)
    {
        int[] activeFaces = activeFaces(z);

        //TODO do not save double elements in edges
        //at the moment every edge has two points
        float[] edges = new float[activeFaces.length / 3 * 4];
        
        // calculate the intersecting edge for every face
        // 
        //
        for(int i = 0; i < activeFaces.length; i += 3)
        {
            if((      (this.vertices[activeFaces[i] * 3 - 1] > z) &&
                      (this.vertices[activeFaces[i + 1] * 3 - 1] < z) &&
                      (this.vertices[activeFaces[i + 2] * 3 - 1] > z)) ||
                      ((this.vertices[activeFaces[i] * 3 - 1] < z) &&
                      (this.vertices[activeFaces[i + 1] * 3 - 1] > z) &&
                      (this.vertices[activeFaces[i + 2] * 3 - 1] < z)))
            {
                //calculate edges and add them to the array
                //edge i x1-value
                edges[(i / 3 * 4)] = 
                        (((z - this.vertices[activeFaces[i + 1] * 3 - 1]) / 
                        (this.vertices[activeFaces[i] * 3 - 1] - 
                        this.vertices[activeFaces[i + 1] * 3 - 1])) * 
                        (this.vertices[activeFaces[i] * 3 - 3] - 
                        this.vertices[activeFaces[i + 1] * 3 - 3]) + 
                        this.vertices[activeFaces[i + 1] * 3 - 3]);
                //edge i y1-value
                edges[(i / 3 * 4) + 1] = 
                        (((z - this.vertices[activeFaces[i + 1] * 3 - 1]) / 
                        (this.vertices[activeFaces[i] * 3 - 1] - 
                        this.vertices[activeFaces[i + 1] * 3 - 1])) * 
                        (this.vertices[activeFaces[i] * 3 - 2] - 
                        this.vertices[activeFaces[i + 1] * 3 - 2]) + 
                        this.vertices[activeFaces[i + 1] * 3 - 2]);
                //edge i x2-value
                edges[(i / 3 * 4) + 2] = 
                        (((z - this.vertices[activeFaces[i + 1] * 3 - 1]) / 
                        (this.vertices[activeFaces[i + 2] * 3 - 1] - 
                        this.vertices[activeFaces[i + 1] * 3 - 1])) * 
                        (this.vertices[activeFaces[i + 2] * 3 - 3] - 
                        this.vertices[activeFaces[i + 1] * 3 - 3]) + 
                        this.vertices[activeFaces[i + 1] * 3 - 3]);
                //edge i y2-value
                edges[(i / 3 * 4) + 3] = 
                        (((z - this.vertices[activeFaces[i + 1] * 3 - 1]) / 
                        (this.vertices[activeFaces[i + 2] * 3 - 1] - 
                        this.vertices[activeFaces[i + 1] * 3 - 1])) * 
                        (this.vertices[activeFaces[i + 2] * 3 - 2] - 
                        this.vertices[activeFaces[i + 1] * 3 - 2]) + 
                        this.vertices[activeFaces[i + 1] * 3 - 2]);
                
            }else if(((this.vertices[activeFaces[i] * 3 - 1] < z) &&
                      (this.vertices[activeFaces[i + 1] * 3 - 1] < z) &&
                      (this.vertices[activeFaces[i + 2] * 3 - 1] > z)) ||
                      ((this.vertices[activeFaces[i] * 3 - 1] > z) &&
                      (this.vertices[activeFaces[i + 1] * 3 - 1] > z) &&
                      (this.vertices[activeFaces[i + 2] * 3 - 1] < z)))
            {
                //calculate edges and add them to the array
                //edge i x1-value
                edges[(i / 3 * 4)] = 
                        (((z - this.vertices[activeFaces[i + 2] * 3 - 1]) / 
                        (this.vertices[activeFaces[i] * 3 - 1] - 
                        this.vertices[activeFaces[i + 2] * 3 - 1])) * 
                        (this.vertices[activeFaces[i] * 3 - 3] - 
                        this.vertices[activeFaces[i + 2] * 3 - 3]) + 
                        this.vertices[activeFaces[i + 2] * 3 - 3]);
                //edge i y1-value
                edges[(i / 3 * 4) + 1] = 
                        (((z - this.vertices[activeFaces[i + 2] * 3 - 1]) / 
                        (this.vertices[activeFaces[i] * 3 - 1] - 
                        this.vertices[activeFaces[i + 2] * 3 - 1])) * 
                        (this.vertices[activeFaces[i] * 3 - 2] - 
                        this.vertices[activeFaces[i + 2] * 3 - 2]) + 
                        this.vertices[activeFaces[i + 2] * 3 - 2]);
                //edge i x2-value
                edges[(i / 3 * 4) + 2] = 
                        (((z - this.vertices[activeFaces[i + 2] * 3 - 1]) / 
                        (this.vertices[activeFaces[i + 1] * 3 - 1] - 
                        this.vertices[activeFaces[i + 2] * 3 - 1])) * 
                        (this.vertices[activeFaces[i + 1] * 3 - 3] - 
                        this.vertices[activeFaces[i + 2] * 3 - 3]) + 
                        this.vertices[activeFaces[i + 2] * 3 - 3]);
                //edge i y2-value
                edges[(i / 3 * 4) + 3] = 
                        (((z - this.vertices[activeFaces[i + 2] * 3 - 1]) / 
                        (this.vertices[activeFaces[i + 1] * 3 - 1] - 
                        this.vertices[activeFaces[i + 2] * 3 - 1])) * 
                        (this.vertices[activeFaces[i + 1] * 3 - 2] - 
                        this.vertices[activeFaces[i + 2] * 3 - 2]) + 
                        this.vertices[activeFaces[i + 2] * 3 - 2]);
                
            }else if(((this.vertices[activeFaces[i] * 3 - 1] < z) &&
                      (this.vertices[activeFaces[i + 1] * 3 - 1] > z) &&
                      (this.vertices[activeFaces[i + 2] * 3 - 1] > z)) ||
                      ((this.vertices[activeFaces[i] * 3 - 1] > z) &&
                      (this.vertices[activeFaces[i + 1] * 3 - 1] < z) &&
                      (this.vertices[activeFaces[i + 2] * 3 - 1] < z)))
            {
                //calculate edges and add them to the array
                //edge i x1-value
                edges[(i / 3 * 4)] = 
                        (((z - this.vertices[activeFaces[i] * 3 - 1]) / 
                        (this.vertices[activeFaces[i + 2] * 3 - 1] - 
                        this.vertices[activeFaces[i] * 3 - 1])) * 
                        (this.vertices[activeFaces[i + 2] * 3 - 3] - 
                        this.vertices[activeFaces[i] * 3 - 3]) + 
                        this.vertices[activeFaces[i] * 3 - 3]);
                //edge i y1-value
                edges[(i / 3 * 4) + 1] = 
                        (((z - this.vertices[activeFaces[i] * 3 - 1]) / 
                        (this.vertices[activeFaces[i + 2] * 3 - 1] - 
                        this.vertices[activeFaces[i] * 3 - 1])) * 
                        (this.vertices[activeFaces[i + 2] * 3 - 2] - 
                        this.vertices[activeFaces[i] * 3 - 2]) + 
                        this.vertices[activeFaces[i] * 3 - 2]);
                //edge i x2-value
                edges[(i / 3 * 4) + 2] = 
                        (((z - this.vertices[activeFaces[i] * 3 - 1]) / 
                        (this.vertices[activeFaces[i + 1] * 3 - 1] - 
                        this.vertices[activeFaces[i] * 3 - 1])) * 
                        (this.vertices[activeFaces[i + 1] * 3 - 3] - 
                        this.vertices[activeFaces[i] * 3 - 3]) + 
                        this.vertices[activeFaces[i] * 3 - 3]);
                //edge i y2-value
                edges[(i / 3 * 4) + 3] = 
                        (((z - this.vertices[activeFaces[i] * 3 - 1]) / 
                        (this.vertices[activeFaces[i + 1] * 3 - 1] - 
                        this.vertices[activeFaces[i] * 3 - 1])) * 
                        (this.vertices[activeFaces[i + 1] * 3 - 2] - 
                        this.vertices[activeFaces[i] * 3 - 2]) + 
                        this.vertices[activeFaces[i] * 3 - 2]);
            }
        }
        
        return edges;
    }
    
    /**
     * Calculates all the faces that intersect with the current x-y-plane
     * using the z-coordinate.
     * 
     * @param z The z-coordinate of the current x-y-plane.
     * @return An Array with all faces which intersect with the current plane.
     */
    public int[] activeFaces(float z)
    {
        int j = 0;
        int[] activeFaces = new int[this.faces.length];

        for(int i = 0; i < this.faces.length; i += 3)
        {
            //TODO test if the index is right, look at Renderer.java
            if(     !((this.vertices[this.faces[i] * 3 - 1] < z) && 
                    (this.vertices[this.faces[i + 1] * 3 - 1] < z) && 
                    (this.vertices[this.faces[i + 2] * 3 - 1] < z)) &&
                    !((this.vertices[this.faces[i] * 3 - 1] > z) && 
                    (this.vertices[this.faces[i + 1] * 3 - 1] > z) && 
                    (this.vertices[this.faces[i + 2] * 3 - 1] > z)))
            {
                activeFaces[j++] = this.faces[i];
                activeFaces[j++] = this.faces[i + 1];
                activeFaces[j++] = this.faces[i + 2];
            }
        }
        
        return trim(activeFaces, j);
    }

    /**
     * Trims a overgiven Array to the given length.
     * 
     * @param a Array which should be trimmed.
     * @param length New length of the Array a.
     * @return The shortened Array. 
     */
    private int[] trim(int[] a, int length)
    {
        int[] trimmedArray = new int[length];
        for(int i = 0; i < length; i++)
        {
            trimmedArray[i] = a[i];
        }
        return trimmedArray;
    }
    
    /**
     * Getter for the Voxel-Array.
     */
    public Voxel[] getVoxels()
    {
        return this.voxels;
    }
    /**
     * Getter for the STEPS.
     */
    public int getSteps()
    {
        return Generator.STEPS;
    }
    /**
     * Getter for the Faces-Array.
     */
    public int[] getFaces()
    {
        return this.faces;
    }
    /**
     * Getter for the Vertices-Array.
     */
    public float[] getVertices()
    {
        return this.vertices;
    }
    
    /**
     * Calculates the highest y-value.
     * @param vertices Array with the scene.
     * @return max y-value
     */
    public float calculateMaxY(float[] vertices)
    {
        float tmp;
        float max = 0.0f;
        for (int i = 1; i <= (vertices.length / 3); i++)
        {
            tmp = vertices[(i * 3) - 2];
            if (tmp > max)
            {
                max = tmp;
            }
        }
        return max;
    }

    /**
     * Calculates the lowest y-value.
     * @param vertices Array which contains the scene.
     * @return min y-value
     */
    public float calculateMinY(float[] vertices)
    {
        float tmp;
        float min = 0.0f;
        for (int i = 1; i <= (vertices.length / 3); i++)
        {
            tmp = vertices[(i * 3) - 2];
            if (tmp < min)
            {
                min = tmp;
            }
        }
        return min;
    }

    /**
     * Calculates the highest x-value.
     * @param vertices Array with the scene.
     * @return max x-value
     */
    public float calculateMaxX(float[] vertices)
    {
        float tmp;
        float max = 0.0f;
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

    /**
     * Calculates the lowest x-value.
     * @param vertices Array which contains the scene.
     * @return min x-value
     */
    public float calculateMinX(float[] vertices)
    {
        float tmp;
        float min = 0.0f;
        for (int i = 1; i <= (vertices.length / 3); i++)
        {
            tmp = vertices[(i * 3) - 3];
            if (tmp < min)
            {
                min = tmp;
            }
        }
        return min;
    }

    /**
     * Calculates the highest z-value.
     * @param vertices Array with the scene.
     * @return max z-value
     */
    public float calculateMaxZ(float[] vertices)
    {
        float tmp;
        float max = 0.0f;
        for (int i = 1; i <= (vertices.length / 3); i++)
        {
            tmp = vertices[(i * 3) - 1];
            if (tmp > max)
            {
                max = tmp;
            }
        }
        return max + 1.0f;
    }

    /**
     * Calculates the lowest z-value.
     * @param vertices Array which contains the scene.
     * @return min z-value
     */
    public float calculateMinZ(float[] vertices)
    {
        float tmp;
        float min = 0.0f;
        for (int i = 1; i <= (vertices.length / 3); i++)
        {
            tmp = vertices[(i * 3) - 1];
            if (tmp < min)
            {
                min = tmp;
            }
        }
        return min;
    }
}
