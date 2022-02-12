package engine.objConverter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import game.Main;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

// reads and passed Objects from the obj file format to ModelData
public class OBJFileLoader {
    public static ModelData loadOBJ(String objFileName) {
        List<Vertex> vertices = new ArrayList<>(); // these lists store the object data for processing
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        processObjectFile(objFileName, vertices, textures, normals, indices); // reads, parses, processes and writes obj-file
        removeUnusedVertices(vertices); // no need for not indexed vertices
        return buildModel(vertices, textures, normals, indices); // cast lists and construct the ModelData object
    }

    //------------start of processObjectFile methods
    private static void processObjectFile(String objFileName, List<Vertex> vertices,
                                          List<Vector2f> textures, List<Vector3f> normals, List<Integer> indices) {
        BufferedReader reader = new BufferedReader(getObjectFileReader(objFileName)); // error handling for absent file
        try {
            String line = reader.readLine();
            line = readRawData(line, vertices, textures, normals, reader);
            readAndProcessMetaData(line, vertices, indices, reader);
            reader.close();
        } catch (IOException e) { // error handling for corrupted file
            System.err.println("Error reading file: " + objFileName);
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static FileReader getObjectFileReader(String objFileName) {
        File objFile = new File(Main.RES_OBJECTS_DIR + objFileName + Main.OBJ_FILE);
        FileReader objFileReader = null;
        try {
            objFileReader = new FileReader(objFile);
        } catch (FileNotFoundException e) {
            System.err.println("Could not find file: " + objFileName);
            e.printStackTrace();
            System.exit(-1);
        }
        return objFileReader;
    }

    private static String readRawData(String line, List<Vertex> vertices, List<Vector2f> textures,
                                    List<Vector3f> normals, BufferedReader reader) throws IOException {
        String[] splitLine;
        while (!line.startsWith("f ")) {// f stands for a line with a face / all raw data has been read
            if (line.startsWith("v ")) { // v stands for a line with a vertex position
                splitLine = line.split(" ");
                Vector3f vertex = new Vector3f(Float.parseFloat(splitLine[1]),
                        Float.parseFloat(splitLine[2]), Float.parseFloat(splitLine[3]));
                Vertex newVertex = new Vertex(vertices.size(), vertex);
                vertices.add(newVertex);
            } else if (line.startsWith("vt ")) { // vt stands for a line with UV-texture coordinates
                splitLine = line.split(" ");
                Vector2f texture = new Vector2f(Float.parseFloat(splitLine[1]),
                        Float.parseFloat(splitLine[2]));
                textures.add(texture);
            } else if (line.startsWith("vn ")) { // vt stands for a line with normal vectors
                splitLine = line.split(" ");
                Vector3f normal = new Vector3f(Float.parseFloat(splitLine[1]),
                        Float.parseFloat(splitLine[2]), Float.parseFloat(splitLine[3]));
                normals.add(normal);
            }
            line = reader.readLine();
        }
        return line;
    }

    private static void readAndProcessMetaData(String line, List<Vertex> vertices, List<Integer> indices,
                                               BufferedReader reader) throws IOException {
        String[] splitLine;
        while (line != null && line.startsWith("f ")) { // a face consists of three vertices
            splitLine = line.split(" "); // indexing position, UV-texture and normal vector
            processVertex(splitLine[1].split("/"), vertices, indices); // matches pos, texture and normals
            processVertex(splitLine[2].split("/"), vertices, indices);
            processVertex(splitLine[3].split("/"), vertices, indices);
            line = reader.readLine();
        }
    }

    private static void processVertex(String[] vertex, List<Vertex> vertices, List<Integer> indices) {
        int vertexIndex = Integer.parseInt(vertex[0]) - 1; // in the object file vertices are counted from 1 to n
        int textureIndex = Integer.parseInt(vertex[1]) - 1;
        int normalIndex = Integer.parseInt(vertex[2]) - 1;
        Vertex currentVertex = vertices.get(vertexIndex); // get the current vertex
        if (currentVertex.hasNotBeenSet()) { // if UV-texture and normal vector are not set yet
            currentVertex.setTextureIndex(textureIndex); // set corresponding data:
            currentVertex.setNormalIndex(normalIndex);
            indices.add(vertexIndex);
        } else { // vertex is about to be processed again
            dealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices,
                    vertices);
        }
    }

    private static void dealWithAlreadyProcessedVertex(Vertex currentVertex, int newTextureIndex,
                                                       int newNormalIndex, List<Integer> indices, List<Vertex> vertices) {
        if (currentVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
            indices.add(currentVertex.getIndex());
        } else { // if the current Vertex has actually two different assignments from the scene in the editing software
            Vertex oneDuplicateVertex = currentVertex.getDuplicateVertex();
            if (Objects.nonNull(oneDuplicateVertex)) { // iterate through linked vertices until the last duplicate
                dealWithAlreadyProcessedVertex(oneDuplicateVertex, newTextureIndex, newNormalIndex,
                        indices, vertices);
            } else { // last duplicate has been reached
                Vertex duplicateVertex = new Vertex(vertices.size(), currentVertex.getPosition());
                duplicateVertex.setTextureIndex(newTextureIndex); // set corresponding data:
                duplicateVertex.setNormalIndex(newNormalIndex);
                currentVertex.setDuplicateVertex(duplicateVertex); // link as duplicate
                vertices.add(duplicateVertex); // add new vertex to lists:
                indices.add(duplicateVertex.getIndex());
            }
        }
    }
    //------------end of processObjectFile methods

    private static void removeUnusedVertices(List<Vertex> vertices) {
        for (Vertex vertex : vertices) {
            if (vertex.hasNotBeenSet()) {
                vertex.setTextureIndex(0);
                vertex.setNormalIndex(0);
            }
        }
    }

    //------------start of buildModel methods
    private static ModelData buildModel(List<Vertex> vertices, List<Vector2f> textures,
                                        List<Vector3f> normals, List<Integer> indices){
        float[] verticesArray = new float[vertices.size() * 3];
        float[] texturesArray = new float[vertices.size() * 2];
        float[] normalsArray = new float[vertices.size() * 3];
        convertDataToArrays(vertices, textures, normals, verticesArray,
                texturesArray, normalsArray);
        int[] indicesArray = convertIndicesListToArray(indices);
        return new ModelData(verticesArray, texturesArray, normalsArray, indicesArray);
    }

    private static void convertDataToArrays(List<Vertex> vertices, List<Vector2f> textures,
                                             List<Vector3f> normals, float[] verticesArray, float[] texturesArray,
                                             float[] normalsArray) {
        for (int i = 0; i < vertices.size(); i++) { // for every vertex
            Vertex currentVertex = vertices.get(i); // get the data:
            Vector3f position = currentVertex.getPosition();
            Vector2f textureCoordinate = textures.get(currentVertex.getTextureIndex());
            Vector3f normalVector = normals.get(currentVertex.getNormalIndex());
            verticesArray[i * 3] = position.x; // and cast the data into required format of array:
            verticesArray[i * 3 + 1] = position.y;
            verticesArray[i * 3 + 2] = position.z;
            texturesArray[i * 2] = textureCoordinate.x;
            texturesArray[i * 2 + 1] = 1 - textureCoordinate.y;
            normalsArray[i * 3] = normalVector.x;
            normalsArray[i * 3 + 1] = normalVector.y;
            normalsArray[i * 3 + 2] = normalVector.z;
        }
    }

    private static int[] convertIndicesListToArray(List<Integer> indices) {
        int[] indicesArray = new int[indices.size()];
        for (int i = 0; i < indicesArray.length; i++) {
            indicesArray[i] = indices.get(i);
        }
        return indicesArray;
    }
}