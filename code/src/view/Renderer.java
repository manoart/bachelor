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
import model.Surface;
import model.Voxel;
import org.lwjgl.BufferUtils;
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

        FloatBuffer lightAmbient = BufferUtils.createFloatBuffer(4).put(new float[] {0.0f,0.0f,0.0f,1.0f});
        lightAmbient.flip();
        
//        float lightAmbient[] = {0.0f, 0.0f, 0.0f, 1.0f};
        float lightDiffuse[] = {1.0f, 1.0f, 1.0f, 1.0f};
        float lightSpecular[] = {1.0f, 1.0f, 1.0f, 1.0f};
        float lightPosition[] = {-1.0f, 0.0f, 2.0f, 1.0f};
        
//        float spotPosition[] = {0.0f, 0.0f, 3.5f, 1.0f};
//        float spotDirection [] = {0.0f, 0.0f, -1.0f,1.0f};
        
        float globalAmbient[] = {0.2f, 0.2f, 0.2f, 1.0f};

        ByteBuffer temp = ByteBuffer.allocateDirect(16);
        temp.order(ByteOrder.nativeOrder());
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, lightAmbient);
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, (FloatBuffer) temp.asFloatBuffer().put(lightDiffuse).flip());
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, (FloatBuffer) temp.asFloatBuffer().put(lightSpecular).flip());
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, (FloatBuffer) temp.asFloatBuffer().put(lightPosition).flip());
        // try to make a spotlight
//        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_SPOT_DIRECTION, (FloatBuffer) temp.asFloatBuffer().put(spotDirection).flip());
//        GL11.glLightf(GL11.GL_LIGHT1, GL11.GL_SPOT_EXPONENT, 60.0f);
//        GL11.glLightf(GL11.GL_LIGHT1, GL11.GL_SPOT_CUTOFF, 30.0f);
//        GL11.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION, (FloatBuffer) temp.asFloatBuffer().put(spotPosition).flip());
        //global ambient lighting
        GL11.glLightModel(GL11.GL_AMBIENT, (FloatBuffer) temp.asFloatBuffer().put(globalAmbient).flip());
        
        GL11.glEnable(GL11.GL_LIGHTING);
//        GL11.glColor3f(1.0f, 0.0f, 0.0f);

        GL11.glEnable(GL11.GL_LIGHT0);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glColorMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE);

        //TODO general file-path
        String path = "/Users/Manuel/NetBeansProjects/Snow/src/obj/pawn.obj";
        ObjParser op = new ObjParser(path);
//        System.out.println("faces.length: " + op.getFaces().length);
//        System.out.println("normalIndices.length: " + op.getNormalIndices().length);
//        Generator generator = new Generator(path);
        Surface surface = new Surface(path);
        
        // angle to rotate around the z-axis
        float orbitAngle = 0.0f;
        // zoom-factor
        float zoom = 1.0f;
        float cameraX = 0.0f;
        float cameraY = 4.0f;
        // move the 'camera' up and down (glLookAt()-z-coordinate)
        float cameraZ = 2.0f;
        
        

        // init OpenGL
//        private void initOpenGL()
        {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GLU.gluPerspective(70, (float) 800 / 600, 1, 1000);
//	GL11.glOrtho(0, 800, 600, 0, 1, -1);
        
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GLU.gluLookAt(cameraX, cameraY, cameraZ,       // camera-position
                      0.0f, 0.0f, 1.0f,     // point of view
                      0.0f, 0.0f, 1.0f);    // up-vector
        }
        
        GL11.glPointSize(3.0f);

//        GL11.glPushMatrix();
        while (!Display.isCloseRequested())
        {
            // Clear the screen and depth buffer
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            GL11.glPushMatrix();

//            GL11.glTranslatef(0.0f, -1.3f, -3.0f);
//            GL11.glRotatef(270, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(orbitAngle, 0.0f, 0.0f, 1.0f);
//            GL11.glTranslatef(0.0f, y, 0.0f);

            //TODO fix rotating CAMERA
            if(Keyboard.isKeyDown(Keyboard.KEY_LEFT))
            {
                orbitAngle -= 0.05f;
            }

            if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
            {
                orbitAngle += 0.05f;
            }
            
            if(Keyboard.isKeyDown(Keyboard.KEY_UP))
            {
                cameraZ += 0.01f;
                if(cameraZ > 10.0f) cameraZ = 10.0f;
                GL11.glPopMatrix();
                GL11.glMatrixMode(GL11.GL_PROJECTION);
                GL11.glLoadIdentity();
                GLU.gluPerspective(70 * zoom, (float) 800 / 600, 1, 1000);
//             	GL11.glOrtho(0, 800, 600, 0, 1, -1);

                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glLoadIdentity();
                GLU.gluLookAt(cameraX, cameraY, cameraZ, 
                              0.0f, 0.0f, 1.0f, 
                              0.0f, 0.0f, 1.0f);
//                GL11.glRotatef(a, 0.0f, 0.0f, 1.0f);
                GL11.glPushMatrix();
                GL11.glRotatef(orbitAngle, 0.0f, 0.0f, 1.0f);
            }
            
            if(Keyboard.isKeyDown(Keyboard.KEY_DOWN))
            {
                cameraZ -= 0.01f;
                if(cameraZ < 0.0f) cameraZ = 0.0f;
                GL11.glPopMatrix();
                GL11.glMatrixMode(GL11.GL_PROJECTION);
                GL11.glLoadIdentity();
                GLU.gluPerspective(70 * zoom, (float) 800 / 600, 1, 1000);
//             	GL11.glOrtho(0, 800, 600, 0, 1, -1);

                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glLoadIdentity();
                GLU.gluLookAt(cameraX, cameraY, cameraZ, 
                              0.0f, 0.0f, 1.0f, 
                              0.0f, 0.0f, 1.0f);
//                GL11.glRotatef(a, 0.0f, 0.0f, 1.0f);
                GL11.glPushMatrix();
                GL11.glRotatef(orbitAngle, 0.0f, 0.0f, 1.0f);
            }
            
            if(Keyboard.isKeyDown(Keyboard.KEY_I))
            {
                zoom /= 1.001f;
                if(zoom < 0.5f) zoom = 0.5f;
                GL11.glPopMatrix();
                GL11.glMatrixMode(GL11.GL_PROJECTION);
                GL11.glLoadIdentity();
                GLU.gluPerspective(70 * zoom, (float) 800 / 600, 1, 1000);
//             	GL11.glOrtho(0, 800, 600, 0, 1, -1);

                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glLoadIdentity();
                GLU.gluLookAt(cameraX, cameraY, cameraZ, 
                              0.0f, 0.0f, 1.0f, 
                              0.0f, 0.0f, 1.0f);
//                GL11.glRotatef(a, 0.0f, 0.0f, 1.0f);
                GL11.glPushMatrix();
                GL11.glRotatef(orbitAngle, 0.0f, 0.0f, 1.0f);
            }
            
            if(Keyboard.isKeyDown(Keyboard.KEY_O))
            {
                zoom *= 1.001f;
                if(zoom > 2.0f) zoom = 2.0f;
                GL11.glPopMatrix();
                GL11.glMatrixMode(GL11.GL_PROJECTION);
                GL11.glLoadIdentity();
                GLU.gluPerspective(70 * zoom, (float) 800 / 600, 1, 1000);
//             	GL11.glOrtho(0, 800, 600, 0, 1, -1);

                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glLoadIdentity();
                GLU.gluLookAt(cameraX, cameraY, cameraZ, 
                              0.0f, 0.0f, 1.0f, 
                              0.0f, 0.0f, 1.0f);
//                GL11.glRotatef(a, 0.0f, 0.0f, 1.0f);
                GL11.glPushMatrix();
                GL11.glRotatef(orbitAngle, 0.0f, 0.0f, 1.0f);
            }
            
            if(Keyboard.isKeyDown(Keyboard.KEY_F))
            {
                GL11.glShadeModel(GL11.GL_FLAT);
            }
            
            if(Keyboard.isKeyDown(Keyboard.KEY_S))
            {
                // switch from Flatshading to SmoothShading (Phong?)
                GL11.glShadeModel(GL11.GL_SMOOTH);
            }
            
            if(Keyboard.isKeyDown(Keyboard.KEY_W))
            {
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK,GL11.GL_LINE);
            }
            
            if(Keyboard.isKeyDown(Keyboard.KEY_T))
            {
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK,GL11.GL_FILL);
            }


            // draw triangles or points or lines
//            GL11.glBegin(GL11.GL_POINTS);
////            surface.randomSnow();
//            Voxel[] voxels = surface.getActiveVoxels();
//
//            for (int i = 0; i < voxels.length; i++)
//            {
//                if (voxels[i] != null && voxels[i].getSnow())
//                {
//                    GL11.glVertex3f(voxels[i].getX(), voxels[i].getY(), voxels[i].getZ());
//                }
//            }
//            GL11.glEnd();

            GL11.glColor3f(1.0f, 1.0f, 1.0f);
            GL11.glBegin(GL11.GL_TRIANGLES);
            if(Keyboard.isKeyDown(Keyboard.KEY_P))
            {
                surface.randomSnow();
                surface.setCnt(0);
                surface.marchingCubes();
            }
            float[] marchingCubesFaces = surface.getFaces();

            for (int i = 0; i < marchingCubesFaces.length; i += 3)
            {
                    GL11.glVertex3f(marchingCubesFaces[i], 
                                    marchingCubesFaces[i+1], 
                                    marchingCubesFaces[i+2]);
            }
            GL11.glEnd();
            
            
            // set the color of the quad (R,G,B)
            GL11.glColor3f(1.0f, 0.0f, 0.0f);
            
            GL11.glBegin(GL11.GL_TRIANGLES);
            // show all faces which intersect with the x-y-plane
//            int[] faces = generator.activeFaces(0.75f);
            int[] faces = op.getFaces();
            float[] vertices = op.getVertices();
            float[] normals = op.getNormals();
            int[] normalIndices = op.getNormalIndices();
            float[][] vertexNormals = op.getVertexNormals();

            for (int i = 0; i < faces.length; i++)
            {
                //subtract 1 to get the right index
                int j = (faces[i] - 1) * 3;
                GL11.glVertex3f(vertices[j], vertices[j + 1], vertices[j + 2]);
//                GL11.glNormal3f(normals[normalIndices[j]], normals[normalIndices[j+1]], normals[normalIndices[j+2]]);
//                GL11.glNormal3f(vertexNormals[j][0], vertexNormals[j][1] , vertexNormals[j][2]);
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
        Renderer renderer = new Renderer();
        renderer.start();
    }
}