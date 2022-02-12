package engine.particleRendering;

// data class holding the texture of a particle
public class ParticleTexture {
    private final int textureID;

    public ParticleTexture(int textureID) {
        this.textureID = textureID;
    }

    public int getTextureID() {
        return textureID;
    }
}
