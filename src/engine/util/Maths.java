package engine.util;

import engine.entities.Camera;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

// creates matrices for rendering computations
public class Maths {
    public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
        Matrix4f transformationMatrix = getIdentMat();
        Matrix4f.translate(translation, transformationMatrix, transformationMatrix); // first translation
        Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1, 0, 0), transformationMatrix, transformationMatrix); // second rotation
        Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0, 1, 0), transformationMatrix, transformationMatrix);
        Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0, 0, 1), transformationMatrix, transformationMatrix);
        Matrix4f.scale(new Vector3f(scale, scale, scale), transformationMatrix, transformationMatrix); // third scaling
        return transformationMatrix;
    }

    // if no rotation is needed:
    public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
        Matrix4f transformationMatrix = getIdentMat();
        Matrix4f.translate(translation, transformationMatrix, transformationMatrix); // first translation
        Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), transformationMatrix, transformationMatrix); // second scaling
        return transformationMatrix;
    }

    // opposite workflow compared to the transformation matrices:
    public static Matrix4f createViewMatrix(Camera camera) {
        Matrix4f viewMatrix = getIdentMat();
        Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), // first rotation
                new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
        Matrix4f.rotate((float) Math.toRadians(camera.getYaw()),
                new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
        // no roll is needed
        Vector3f cameraPos = camera.getPosition();
        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix); // second (negative) translation
        return viewMatrix;
    }

    private static Matrix4f getIdentMat() {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.setIdentity();
        return matrix4f;
    }
}
