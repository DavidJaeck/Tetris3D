package engine.skyboxRendering;

import engine.entities.Camera;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import engine.renderEngine.DisplayManager;
import engine.shaderEngine.ShaderProgram;
import engine.util.Maths;

// shader classes have the two purposes:
// compiling the vertex and fragment shader code
// binding and unbinding the necessary variables for the compiled shader programs on the gpu
public class SkyboxShader extends ShaderProgram {
    private static final String VERTEX_FILE = "src/engine/skyboxRendering/vertexShader.txt";
    private static final String FRAGMENT_FILE = "src/engine/skyboxRendering/fragmentShader.txt";
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private static final float ROTATION_SPEED = 4f;
    private float current_rotation = 0;

    public SkyboxShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    public void loadProjectionMatrix(Matrix4f matrix) {
        super.loadMatrix(location_projectionMatrix, matrix);
    }

    public void loadViewMatrix(Camera camera) {
        Matrix4f matrix = Maths.createViewMatrix(camera);
        matrix.m30 = 0; // apply not translation
        matrix.m31 = 0;
        matrix.m32 = 0;
        current_rotation += ROTATION_SPEED * DisplayManager.getFrameTimeSeconds(); //calculates Rotation for Skybox
        Matrix4f.rotate((float) Math.toRadians(current_rotation), new Vector3f(0, 1, 0), matrix, matrix); //applies Rotation to viewMatrix of Skybox
        super.loadMatrix(location_viewMatrix, matrix);
    }

    @Override
    protected void getALlUniformLocations() {
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
    }

    @Override
    protected void bindAllAttributes() {
        super.bindAttribute(0, "position");
    }
}
