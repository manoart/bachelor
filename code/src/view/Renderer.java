package view;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Manuel
 */
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import model.Generator;
import model.Voxel;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import view.parser.ObjParser;

public class Renderer
{

    public void start()
    {
        try
        {
            Display.setDisplayMode(new DisplayMode(800, 600));
            Display.create();
        } catch (LWJGLException e)
        {
            e.printStackTrace();
            System.exit(0);
        }

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
//        GL11.glFrontFace(GL11.GL_CW);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_FRONT);
        GL11.glShadeModel(GL11.GL_FLAT);

        float lightAmbient[] =
        {
            0.5f, 0.5f, 0.5f, 1.0f
        };
        float lightDiffuse[] =
        {
            1.0f, 1.0f, 1.0f, 1.0f
        };
        float lightPosition[] =
        {
            0.0f, 3.0f, 0.0f, 2.0f
        };

        ByteBuffer temp = ByteBuffer.allocateDirect(16);
        temp.order(ByteOrder.nativeOrder());
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_AMBIENT, (FloatBuffer) temp.asFloatBuffer().put(lightAmbient).flip());
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_DIFFUSE, (FloatBuffer) temp.asFloatBuffer().put(lightDiffuse).flip());
        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION, (FloatBuffer) temp.asFloatBuffer().put(lightPosition).flip());
        GL11.glEnable(GL11.GL_LIGHT1);
        GL11.glEnable(GL11.GL_LIGHTING);

        GL11.glEnable(GL11.GL_LIGHT1);

        //TODO general file-path
        ObjParser op = new ObjParser("/Users/Manuel/NetBeansProjects/"
                + "Snow/src/obj/pawn.obj");
        Generator generator = new Generator("/Users/Manuel/NetBeansProjects/"
                + "Snow/src/obj/pawn.obj");
        
        // angle to rotate around the z-axis
        float a = 0.0f;
        // part to move object back and forth (y-axis)
        float y = 0.0f;

        // init OpenGL
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GLU.gluPerspective(70, (float) 800 / 600, 1, 1000);
//	GL11.glOrtho(0, 800, 600, 0, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPointSize(3.0f);



        while (!Display.isCloseRequested())
        {
            // Clear the screen and depth buffer
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            GL11.glPushMatrix();

            GL11.glTranslatef(0.0f, -1.3f, -3.0f);
            GL11.glRotatef(270, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(a, 0.0f, 0.0f, 1.0f);
            GL11.glTranslatef(0.0f, y, 0.0f);

            //TODO fix rotating CAMERA
            if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
            {
                a -= 0.05f;
            }

            if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
            {
                a += 0.05f;
            }
            
            if (Keyboard.isKeyDown(Keyboard.KEY_UP))
            {
                y += 0.005f;
            }
            
            if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
            {
                y -= 0.005f;
            }

            // set the color of the quad (R,G,B)
            GL11.glColor3f(0.2f, 0.6f, 0.1f);

            // draw triangle
            GL11.glBegin(GL11.GL_POINTS);

            Voxel[] voxels = generator.getVoxels();

            for (int i = 0; i < voxels.length; i++)
            {
                if (voxels[i] != null)
                {
//                    System.out.println("voxels[i] != null");
                    GL11.glVertex3f(voxels[i].getX(), voxels[i].getY(), voxels[i].getZ());
                }
            }
            GL11.glEnd();

            GL11.glBegin(GL11.GL_TRIANGLES);

            // show all faces which intersect with the x-y-plane
            int[] faces = generator.activeFaces(0.75f);
//            int[] faces = op.getFaces();
            float[] vertices = op.getVertices();

            for (int i = 0; i < faces.length; i++)
            {
                //subtract 1 to get the right index
                int j = (faces[i] - 1) * 3;

                GL11.glVertex3f(vertices[j], vertices[j + 1], vertices[j + 2]);
            }
//		GL11.glVertex3f(5,5,-20);
            GL11.glEnd();
            GL11.glPopMatrix();

            Display.update();
        }

        Display.destroy();
    }

    public static void main(String[] argv)
    {
        Renderer quadExample = new Renderer();
        quadExample.start();
    }
}
