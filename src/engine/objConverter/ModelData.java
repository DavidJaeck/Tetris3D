package engine.objConverter;

// data class holding the data of a Model/Object
public class ModelData {
    private final float[] vertices;
    private final float[] textureCoords;
    private final float[] normals;
    private final int[] indices;

    public ModelData(float[] vertices, float[] textureCoords, float[] normals, int[] indices) {
        this.vertices = vertices;
        this.textureCoords = textureCoords;
        this.normals = normals;
        this.indices = indices;
    }

    public float[] getVertices() {
        return vertices;
    }

    public float[] getTextureCoords() {
        return textureCoords;
    }

    public float[] getNormals() {
        return normals;
    }

    public int[] getIndices() {
        return indices;
    }
}
