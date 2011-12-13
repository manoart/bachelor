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
    private static final int STEPS = 5;
    /** Array which contains the faces' vetices */
    private int[] faces;

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
//        for(int i = 0; i < voxels.length; i++)
//        {
//            if(voxels[i]!= null)
//            System.out.println(voxels[i].toString());
//        }
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
            int[] currentFaces = activeFaces(z);
            
            for (float x = calculateMinX(vertices); x <= calculateMaxX(vertices) + (0.5f / STEPS); x += 1.0f / STEPS)
            {
                for (float y = calculateMinY(vertices); y <=  calculateMaxY(vertices) + (0.5f / STEPS); y += 1.0f / STEPS)
                {
//                    if(!contains(x, y, z, currentFaces))
                    {
                        setVoxel((float) x , (float) y , (float) z);
                    }
                }
            }
        }
    }

    /**
     * Sets a voxel by adding it to the Voxel[] voxels.
     */
    private void setVoxel(float x, float y, float z)
    {
        // add the new Voxel to the end of the array
        int i = 0;
        while (voxels[i] != null)
        {
            i++;
        }

        voxels[i] = new Voxel(x, y, z);
    }
    
    /**
     * Checks if the current Voxel (represented through x,y,z) is inside an
     * object or not using the Point in Polygon-Algorithm.
     * 
     * @param x x-coordinate of the Voxel.
     * @param y y-coordinate of the Voxel.
     * @param z z-coordinate of the Voxel.
     * @param currentFaces Faces which intersect with the current plane.
     * @return true if the coordinates are inside an object.
     */
    private boolean contains(float x, float y, float z, int[] currentFaces)
    {
        int intersections = 0;
        
        for(int i = 0; i < currentFaces.length; i++)
        {
            //TODO Schnittkante berechnen und Punkt in Polygon - Algo implementieren
        }
        
        if(intersections % 2 == 0)
        {
            return true;
        } else
        {
            return false;
        }
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
        for(int k = 0; k < activeFaces.length; k++)
        {
            activeFaces[k] = -1;
        }
        for(int i = 0; i < this.faces.length; i += 3)
        {
            //TODO test if the index is right, look at Renderer.java
            if(!((this.vertices[this.faces[i] * 3 - 1] < z) && 
                    (this.vertices[this.faces[i + 1] * 3 - 1] < z) && 
                    (this.vertices[this.faces[i + 2] * 3 - 1] < z)) &&
                    !((this.vertices[this.faces[i] * 3 - 1] > z) && 
                    (this.vertices[this.faces[i + 1] * 3 - 1] > z) && 
                    (this.vertices[this.faces[i + 2] * 3 - 1] > z)))
            {
                while(activeFaces[j] != -1) 
                {
                    j += 3;
                }
                activeFaces[j] = this.faces[i];
                activeFaces[j + 1] = this.faces[i + 1];
                activeFaces[j + 2] = this.faces[i + 2];
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
     * Method to carve the object out of the voxel grid.
     */
    private void carveOut()
    {
        for(int i = 0; i <= voxels.length; i++)
        {
            if(voxels[i] != null && voxels[i].isInObject())
            {
                voxels[i] = null;
            }
        }
            
    }
    
    /**
     * Deletes a certain Voxel from the Voxel[] voxels.
     * @param a Voxel to be deleted.
     */
    private void deleteVoxel(Voxel a)
    {
        int i = 0;
        while(!a.equals(voxels[i]))
        {
            i++;
        }
        voxels[i] = null;
    }
    
    /**
     * Getter for the Voxel-Array.
     */
    public Voxel[] getVoxels()
    {
        return this.voxels;
    }

    /**
     * Calculates the highest y-value.
     * @param vertices Array with the scene.
     * @return max y-value
     */
    private float calculateMaxY(float[] vertices)
    {
        float tmp = 0.0f;
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
    private float calculateMinY(float[] vertices)
    {
        float tmp = 0.0f;
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
    private float calculateMaxX(float[] vertices)
    {
        float tmp = 0.0f;
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
    private float calculateMinX(float[] vertices)
    {
        float tmp = 0.0f;
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
    private float calculateMaxZ(float[] vertices)
    {
        float tmp = 0.0f;
        float max = 0.0f;
        for (int i = 1; i <= (vertices.length / 3); i++)
        {
            tmp = vertices[(i * 3) - 1];
            if (tmp > max)
            {
                max = tmp;
            }
        }
        return max;
    }

    /**
     * Calculates the lowest z-value.
     * @param vertices Array which contains the scene.
     * @return min z-value
     */
    private float calculateMinZ(float[] vertices)
    {
        float tmp = 0.0f;
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
