package engine.entities;

import engine.models.TexturedModel;
import org.lwjgl.util.vector.Vector3f;

// data class holding the information for a single block
public class Block extends Entity{
    private final int color;

    public Block(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, int color) {
        super(model, position, rotX, rotY, rotZ, scale);
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
