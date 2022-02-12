package engine.renderEngine;

import game.Main;
import engine.models.RawModel;
import de.matthiasmann.twl.utils.PNGDecoder;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

// handles loading up data to VAOs and VBOs holding Object data
public class Loader {
    private final List<Integer> VAOs; // variables for keeping track of VAOs, VBOs and textures in memory
    private final List<Integer> VBOs;
    private final List<Integer> textures;

    public Loader() {
        VAOs = new ArrayList<>();
        VBOs = new ArrayList<>();
        textures = new ArrayList<>();
    }

    // methods for loading data to VAOs
    public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices); // avoids specifying vertices multiple times by indexing them in polygons
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        storeDataInAttributeList(2, 3, normals);
        GL30.glBindVertexArray(0); //unbinds the currently bound VAO
        return new RawModel(vaoID, indices.length);
    }

    public RawModel loadToVAO(float[] positions, int dimensions) {
        int vaoID = createVAO();
        storeDataInAttributeList(0, dimensions, positions);
        GL30.glBindVertexArray(0); //unbinds the currently bound VAO
        return new RawModel(vaoID, positions.length / dimensions);
    }

    public int loadToVAO(float[] positions, float[] textureCoords) {
        int vaoID = createVAO();
        storeDataInAttributeList(0, 2, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        GL30.glBindVertexArray(0); //unbinds the currently bound VAO
        return vaoID;
    }

    // reads in a png texture
    public int loadTexture(String fileName, float bias) {
        Texture texture = null;
        try {
            texture = TextureLoader.getTexture("PNG", new FileInputStream(fileName + Main.PNG_FILE));
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D); // Mipmap lowers Quality on engine.entities further away
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, bias); // adjusts impact of Mipmapping
            if (GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic) { //applies anisotropic filtering
                float amount = Math.min(4f,
                        GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
                GL11.glTexParameterf(GL11.GL_TEXTURE_2D,
                        EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
            } else {
                System.out.println("Anisotropic rendering is not supported!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not load texture: " + fileName);
            System.exit(-1);
        }
        int textureID = texture.getTextureID();
        textures.add(textureID);
        return textureID;
    }

    // reads in a png cubeMap for the skybox
    public int loadCubeMap(String[] textureFiles) {
        int texID = GL11.glGenTextures();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);

        for (int i = 0; i < textureFiles.length; i++) {
            TextureData data = decodeTextureFile(Main.RES_SKYBOX_DIR + textureFiles[i] + Main.PNG_FILE);
            GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA,
                    data.getWidth(), data.getHeight(), 0, GL11.GL_RGBA,
                    GL11.GL_UNSIGNED_BYTE, data.getBuffer());
        }
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        textures.add(texID);
        return texID;
    }

    // reads and passes a png texture to a TextureData Object
    private TextureData decodeTextureFile(String fileName) {
        int width = 0;
        int height = 0;
        ByteBuffer buffer = null;
        try {
            FileInputStream in = new FileInputStream(fileName);
            PNGDecoder decoder = new PNGDecoder(in);
            width = decoder.getWidth();
            height = decoder.getHeight();
            buffer = ByteBuffer.allocateDirect(4 * width * height);
            decoder.decode(buffer, width * 4, PNGDecoder.Format.RGBA);
            buffer.flip();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Tried to load texture " + fileName + ", didn't work");
            System.exit(-1);
        }
        return new TextureData(buffer, width, height);
    }

    private int createVAO() {
        int vaoID = GL30.glGenVertexArrays(); // creates VAO
        VAOs.add(vaoID);
        GL30.glBindVertexArray(vaoID); // binds VAO for writing
        return vaoID;
    }

    private void bindIndicesBuffer(int[] indices) {
        int vboID = GL15.glGenBuffers(); // creates VBO
        VBOs.add(vboID);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID); // binds VBO for writing
        IntBuffer buffer = storeDataInIntBuffer(indices); // the int array must be converted to a buffer
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW); // writes the buffer to the VBO
    }

    private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
        int vboID = GL15.glGenBuffers(); // creates VBO
        VBOs.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID); // binds VBO for writing
        FloatBuffer buffer = storeDataInFloatBuffer(data); // the float array must be converted to a buffer
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW); // writes the buffer to the VBO
        GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT,
                false, 0, 0); // writes the VBO into the VAO
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); //unbinds the currently bound VBO
    }

    // for writing data to VBOs is necessary to convert it to buffers:
    private IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip(); //announces writing is finished, enables reading
        return buffer;
    }

    private FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length); // creates the buffer
        buffer.put(data); // writes the float-array-data to the buffer
        buffer.flip(); // announces that writing is finished and enables reading
        return buffer;
    }

    // cleans up memory by deleting VAOs, VBOs and engine.textures
    public void cleanUp() {
        for (int vao : VAOs)
            GL30.glDeleteVertexArrays(vao);
        for (int vbo : VBOs)
            GL15.glDeleteBuffers(vbo);
        for (int texture : textures)
            GL11.glDeleteTextures(texture);
    }
}
