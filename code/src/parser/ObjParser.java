package parser;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Ein Parser zum Einlesen von *.obj Dateien. Dateien dieses Formats
 * sind wavefront Dateien, die von Blender erstellt wurden und beinhalten
 * Vertex-, Normalen- und Indexinformationen der in Blender erstellen Models.
 * Diese Daten werden hier eingelesen und in Arrays abgespeichert.
 * @author cgp10
 *
 */
public class ObjParser
{
	/* String Konstanten, die jeweils das Praefix fuer Vertices, Normals und Indices
	 * innerhalb der *.obj Datei repraesentieren */
	private static final String VERTEX_PREFIX = new String("v ");
	private static final String NORMAL_PREFIX = new String("vn");
	private static final String FACE_PREFIX = new String("f ");
	
	/* Die Arrays mit den Daten */
	private float[] outputVertices;
	private int[] outputFaces;
	private float[] outputNormals;
	private int[] outputNormalIndices;
	
	/* Indizes fuer die aktuelle Stelle im Array */
	private int vertexArrayIndex;
	private int faceArrayIndex;
	private int normalArrayIndex;
	private int normalIndicesArrayIndex;
	
	/**
	 * Erstellt einen neuen Parser fuer die Datei am uebergebenen Pfad
	 * @param filePath Pfad der wavefront Datei
	 */
	public ObjParser(String filePath)
	{
		File file = new File(filePath);
		
		BufferedReader br;
		FileReader reader;
		
		this.vertexArrayIndex = 0;
		this.faceArrayIndex = 0;
		this.normalArrayIndex = 0;
		this.normalIndicesArrayIndex = 0;
		
		this.outputVertices = new float[countOccurrences(file, VERTEX_PREFIX)*3];
		this.outputFaces = new int[countOccurrences(file, FACE_PREFIX)*3];
		this.outputNormals = new float[countOccurrences(file, NORMAL_PREFIX)*3];
		this.outputNormalIndices = new int[countOccurrences(file, FACE_PREFIX)*3];
		
		try
		{
			reader = new FileReader(file);
			br = new BufferedReader(reader);
			
			String line;
			
			while((line = br.readLine()) != null)
			{
				if(getPrefix(line).equals(VERTEX_PREFIX))
				{
					addVertices(line);
				}
				else if(getPrefix(line).equals(FACE_PREFIX))
				{
					addFaces(line);
				}
				else if(getPrefix(line).equals(NORMAL_PREFIX))
				{
					addNormals(line);
				}
			}
			
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Geht die uebergebene Datei durch und zaehlt die Vorkommen des
	 * uebergebenen substrings. Wird benoetigt um zu bestimmen wie viele
	 * Eintrage die Arrays haben muessen.
	 * @param file Zu lesende Datei
	 * @param substring Das Praefix, das gezaehlt werden soll
	 * @return
	 */
	private int countOccurrences(File file, String substring)
	{
		BufferedReader br = null;
		FileReader reader = null;
		
		int count = 0;
		
		try
		{
			reader = new FileReader(file);
			br = new BufferedReader(reader);
			
			String line;
			while((line = br.readLine()) != null)
			{
				if(line.substring(0, 2).equals(substring))
					count++;
			}
			
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				reader.close();
				br.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		return count;
	}
	
	/**
	 * Liest das Praefix, in diesem Fall die ersten beiden Zeichen,
	 * eines uebergebenen Strings aus und gibt diese als String zurueck
	 * @param line Auszulesender String
	 * @return Die ersten beiden Zeichen als substring, falls das Wort weniger als
	 * 			Zeichen besitzt, so wird ein leerer String zurueckgegeben
	 */
	private String getPrefix(String line)
	{
		if(line.length() >= 2)
			return line.substring(0, 2);
		
		return new String("");
	}
	
	/**
	 * Liest aus einer Zeile (ein uebergebener String), von der bereits bekannt ist,
	 * dass es sich um eine Zeile mit Vertex-Koordinaten handelt, die entsprechenden
	 * Koordinaten aus und fuegt sie dem Array fuer die Vertex-Koordinaten hinzu.
	 * @param line Die Zeile mit den Vertex-Koordinaten
	 */
	private void addVertices(String line)
	{
		StringTokenizer tokens = new StringTokenizer(line);
		try
		{
			tokens.nextToken();
			this.outputVertices[this.vertexArrayIndex++] = Float.parseFloat(tokens.nextToken());
			this.outputVertices[this.vertexArrayIndex++] = Float.parseFloat(tokens.nextToken());
			this.outputVertices[this.vertexArrayIndex++] = (-1)*Float.parseFloat(tokens.nextToken());
		}
		catch(NumberFormatException nfe)
		{
			System.out.println("Wrong numbers in obj. file (vertices)");;
			System.exit(0);
		}
	}
	
	
	/**
	 * Liest aus einer Zeile (ein uebergebener String), von der bereits bekannt ist,
	 * dass es sich um eine Zeile mit Index-Koordinaten handelt, die entsprechenden
	 * Indizes aus und fuegt sie dem Array fuer die Face-Indizes und dem Array fuer
	 * die Normal-Indizes hinzu. BEMERKUNG: Die Zeile kann in verschiedenen Formaten
	 * auftreten, es wird dazu zunaechst das Format ueberprueft und entsprechend ausgelesen.
	 * Dabei kann es vorkommen, dass es keine Normalen-Indizes gibt und das Array leer bleibt.
	 * @param line Die Zeile mit den Indizes
	 */
	private void addFaces(String line)
	{
		StringTokenizer tokens = new StringTokenizer(line, "/ ", false);
		byte numberOfJumps = 0;
		if(tokens.countTokens() == 4)
			throw new RuntimeException("Face indices syntax error...");
		else if(tokens.countTokens() == 7)
			numberOfJumps = 0;
		else if(tokens.countTokens() == 10)
			numberOfJumps = 1;
		else
		{
			System.out.println(tokens.countTokens());
			throw new RuntimeException("weird number of tokens");	
		}
		
		try
		{	
			//f v1//n1 v2//n2 v3//n3
			//oder
			//f v1/tex1/n1 v2/tex2/n2 v3/tex3/n3
			
			//f ueberspringen
			tokens.nextToken();
			
			//VERTEX1 -----------
			//v1 hinzufuegen
			this.outputFaces[this.faceArrayIndex++] = Integer.parseInt(tokens.nextToken());
			
			//eventuell tex1 ueberspringen
			for(byte t = 0; t < numberOfJumps; t++)
				tokens.nextToken();
			
			//n1 hinzufuegen
			this.outputNormalIndices[this.normalIndicesArrayIndex++] = Integer.parseInt(tokens.nextToken());
			
			//VERTEX2 -----------
			//v2 hinzufuegen
			this.outputFaces[this.faceArrayIndex++] = Integer.parseInt(tokens.nextToken());
			
			//eventuell tex2 ueberspringen
			for(byte t = 0; t < numberOfJumps; t++)
				tokens.nextToken();
			
			//n2 hinzufuegen
			this.outputNormalIndices[this.normalIndicesArrayIndex++] = Integer.parseInt(tokens.nextToken());
			
			//VERTEX3 -----------
			//v2 hinzufuegen
			this.outputFaces[this.faceArrayIndex++] = Integer.parseInt(tokens.nextToken());
			
			//eventuell tex3 ueberspringen
			for(byte t = 0; t < numberOfJumps; t++)
				tokens.nextToken();
			
			//n2 hinzufuegen
			this.outputNormalIndices[this.normalIndicesArrayIndex++] = Integer.parseInt(tokens.nextToken());
		}
		catch(NumberFormatException nfe)
		{
			System.out.println("Wrong numbers in obj. file (faces)");;
			System.exit(0);
		}
		catch(java.util.NoSuchElementException nsee)
		{
			System.out.println(tokens.countTokens());
			nsee.printStackTrace();
			System.exit(0);
		}
	}
	
	/**
	 * Liest aus einer Zeile (ein uebergebener String), von der bereits bekannt ist,
	 * dass es sich um eine Zeile mit Normalen-Vektoren handelt, die entsprechenden
	 * Koordinaten aus und fuegt sie dem Array fuer die Normalen-Koordinaten hinzu.
	 * @param line Die Zeile mit den Normalen-Koordinaten
	 */
	private void addNormals(String line)
	{
		StringTokenizer tokens = new StringTokenizer(line);
		try
		{
			tokens.nextToken();
			this.outputNormals[this.normalArrayIndex++] = Float.parseFloat(tokens.nextToken());
			this.outputNormals[this.normalArrayIndex++] = Float.parseFloat(tokens.nextToken());
			this.outputNormals[this.normalArrayIndex++] = (-1)*Float.parseFloat(tokens.nextToken());
		}
		catch(NumberFormatException nfe)
		{
			System.out.println("Wrong numbers in obj. file (vertices)");;
			System.exit(0);
		}
	}
	
	
	/**
	 * Gibt die Vertex-Koordinaten als Array zurueck. Dabei besteht
	 * ein Vertex immer aus drei Koordinaten, die in dem Array alle
	 * hintereinander fortgeschrieben sind.
	 * @return Die Vertex-Koordinaten
	 */
	public float[] getVertices()
	{
		return this.outputVertices;
	}
	
	/**
	 * Gibt die Index-Koordinaten fuer die Faces als Array zurueck. 
	 * Dabei besteht ein Face immer aus drei Vertices, deren Indizes 
	 * alle hintereinander fortgeschrieben sind.
	 * @return Die Face-Indizes
	 */
	public int[] getFaces()
	{
		return this.outputFaces;
	}
	
	/**
	 * Gibt die Normalen-Koordinaten als Array zurueck. Dabei besteht
	 * eine Normale immer aus drei Koordinaten, die in dem Array alle
	 * hintereinander fortgeschrieben sind.
	 * @return Die Normalen-Koordinaten
	 */
	public float[] getNormals()
	{
		return this.outputNormals;
	}
	
	/**
	 * Gibt die Index-Koordinaten fuer die Normalen als Array zurueck. 
	 * Dabei gehoert jeder Index immer zu dem entsprechenden Vertex-Index
	 * (die im Face-Array abgespeichert sind) an der gleichen Stelle.
	 * @return Die Normalen-Indizes
	 */
	public int[] getNormalIndices()
	{
		return this.outputNormalIndices;
	}
}
