/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Manuel
 */
public class VoxelTest
{

    public static void main(String[] args)
    {
        Voxel v1 = new Voxel(0, 0, 0, true, 1.0);
        Voxel v2 = new Voxel(v1);
        Voxel v3 = new Voxel(1, 0, 1, true, 0.5);
        System.out.println("Voxel 1: " + "\n" + v1);
        System.out.println("Voxel 2: " + "\n" + v2);
        System.out.println("Voxel 1 equals Voxel 3?: " + v1.equals(v3));
        System.out.println("Voxel 1 isNeighbor to Voxel 3?: " + v1.isNeighbor(v3));

    }
}
