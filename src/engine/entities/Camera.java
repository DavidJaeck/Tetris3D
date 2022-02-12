package engine.entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

// handles the position, rotation and movement of the camera also impacting the tip for the next module
public class Camera {
    // the camera is moved on a circle around the cage focusing on the center
    private static final Vector3f center = new Vector3f(0, 0, 0);
    private static final float radius = 30.0f;
    private static final float rotationSpeed = 0.003f;
    private double circlePos = 0.75; // initial camera pos equals 1 1/2 pi

    private final float pitch = 10; // rotation up and down
    private float yaw = (float) ((circlePos - 0.75) * 360); // rotation left and right
    private float roll; // rotation around axis of itself and focus-point/center

    private final Vector3f position = new Vector3f(0, 15, 0); // 3 dimensional positions of camera
    private final Vector3f nextModulePosition = new Vector3f(0, 20, 0); // 3 dimensional positions of  tip for next module

    public void handleCameraMovement() {
        // move camera right on the circle
        if (Keyboard.isKeyDown(Keyboard.KEY_X)) {
            circlePos = (circlePos + rotationSpeed) % 1.0;
            yaw -= 360 * rotationSpeed;
        }// move camera left on the circle
        if (Keyboard.isKeyDown(Keyboard.KEY_Y)) {
            circlePos = (circlePos - rotationSpeed) % 1.0;
            yaw += 360 * rotationSpeed;
        }
        // recalculate 3 dimensional positions of camera and tip for next module
        calculateCameraPosition();
        calculateNextModulePosition();
    }

    private void calculateCameraPosition() {
        float unitCircleX = (float) Math.cos(Math.PI * 2 * circlePos);
        float unitCircleY = (float) Math.sin(Math.PI * 2 * circlePos);
        position.x = center.x + unitCircleX * radius;
        position.z = center.z - unitCircleY * radius;
    }

    private void calculateNextModulePosition() {
        float unitCircleX = (float) Math.cos(Math.PI * 2 * (circlePos + 0.35));
        float unitCircleY = (float) Math.sin(Math.PI * 2 * (circlePos + 0.35));
        nextModulePosition.x = center.x + unitCircleX * (radius);
        nextModulePosition.z = center.z - unitCircleY * (radius);
    }

    public Vector3f getNextModulePosition() {
        return nextModulePosition;
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public double getCirclePos() {
        return circlePos;
    }

    public float getRoll() {
        return roll;
    }
}
