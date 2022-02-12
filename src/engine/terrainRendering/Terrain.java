package engine.terrainRendering;

import engine.models.RawModel;
import engine.renderEngine.Loader;

// data holder class for terrains
public class Terrain {
    private static final float SIZE = 400;
    private static final int VERTEX_COUNT = 128;

    private final float x;
    private final float z;
    private final RawModel model;
    private final TerrainTexturePack texturePack;
    private final TerrainTexture blendMap;

    public Terrain(float gridX, float gridZ, Loader loader, TerrainTexturePack texturePack, TerrainTexture blendMap) {
        this.texturePack = texturePack;
        this.blendMap = blendMap;
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;
        this.model = generateTerrain(loader);
    }

    public float getX() {
        return x;
    }

    public float getZ() {
        return z;
    }

    public RawModel getModel() {
        return model;
    }

    public TerrainTexturePack getTexturePack() {
        return texturePack;
    }

    public TerrainTexture getBlendMap() {
        return blendMap;
    }

    // generates vertices, textureCoords, normals, indices constructing the terrain
    private RawModel generateTerrain(Loader loader) {
        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];
        int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];
        int index = 0;
        for (int i = 0; i < VERTEX_COUNT; i++) {
            for (int j = 0; j < VERTEX_COUNT; j++) {
                // generates vertices
                vertices[index * 3] = (float) j / ((float) VERTEX_COUNT - 1) * SIZE;
                vertices[index * 3 + 1] = 0;
                vertices[index * 3 + 2] = (float) i / ((float) VERTEX_COUNT - 1) * SIZE;
                // generates textureCoords
                textureCoords[index * 2] = (float) j / ((float) VERTEX_COUNT - 1);
                textureCoords[index * 2 + 1] = (float) i / ((float) VERTEX_COUNT - 1);
                // generates normals
                normals[index * 3] = 0;
                normals[index * 3 + 1] = 1;
                normals[index * 3 + 2] = 0;
                index += 1;
            }
        }
        index = 0;// generates indices
        for (int z = 0; z < VERTEX_COUNT - 1; z++) {
            for (int x = 0; x < VERTEX_COUNT - 1; x++) {
                int topLeft = (z * VERTEX_COUNT) + x;
                int topRight = topLeft + 1;
                int bottomLeft = ((z + 1) * VERTEX_COUNT) + x;
                int bottomRight = bottomLeft + 1;
                indices[index] = topLeft;
                indices[index + 1] = bottomLeft;
                indices[index + 2] = topRight;
                indices[index + 3] = topRight;
                indices[index + 4] = bottomLeft;
                indices[index + 5] = bottomRight;
                index += 6;
            }
        }
        return loader.loadToVAO(vertices, textureCoords, normals, indices);
    }
}
