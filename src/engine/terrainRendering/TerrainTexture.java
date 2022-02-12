package engine.terrainRendering;

// data class holding the texture of a terrain
public class TerrainTexture {
    private final int textureID;

    public TerrainTexture(int textureID) {
        this.textureID = textureID;
    }

    public int getTextureID() {
        return textureID;
    }
}
