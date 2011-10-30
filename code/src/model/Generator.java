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
public class Generator {
    /** Array to store all Voxels */
    private Voxel[] voxels;
    private float[] vertices;

    /**
     * Constructor which parses a scene, calculates the Voxel[] voxels size
     * and scans the scene to fill it up with Voxels.
     */
    public Generator(String path)
    {
        //TODO Parser reads out scene (*.obj)
        ObjParser op = new ObjParser(path);
        this.vertices = op.getVertices();
        scan(vertices);
        //TODO calculate size of Voxel[] voxels (every 0.1 "m")
    }

    /**
     * This method calculates where Voxels h
     *
     * @param vertices Vertices of the given scene.
     */
    private void scan(float[] vertices)
    {
        //TODO go through scene (float[]) and get Voxel-coordinates
    }

    /**
     * Sets the Voxels and add them to the Voxel[] voxels.
     */
    private void setVoxel()
    {

    }

    /**
     * Getter for the Voxel-Array.
     */
    public Voxel[] getVoxels()
    {
        return this.voxels;
    }
}
