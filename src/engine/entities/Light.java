package engine.entities;

import org.lwjgl.util.vector.Vector3f;

// data class holding the information about lights
public class Light {
    private Vector3f position; // a light has a position and a color
    private Vector3f color;

    public Light(Vector3f position, Vector3f color) {
        this.position = position;
        this.color = color;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }
}
