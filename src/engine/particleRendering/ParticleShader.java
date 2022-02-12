package engine.particleRendering;

import org.lwjgl.util.vector.Matrix4f;
import engine.shaderEngine.ShaderProgram;

// shader classes have the two purposes:
// compiling the vertex and fragment shader code
// binding and unbinding the necessary variables for the compiled shader programs on the gpu
public class ParticleShader extends ShaderProgram {
    private static final String VERTEX_FILE = "src/engine/particleRendering/vertexShader.txt";
    private static final String FRAGMENT_FILE = "src/engine/particleRendering/fragmentShader.txt";
    private int location_modelViewMatrix;
    private int location_projectionMatrix;

    public ParticleShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getALlUniformLocations() {
        location_modelViewMatrix = super.getUniformLocation("modelViewMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
    }

    @Override
    protected void bindAllAttributes() {
        super.bindAttribute(0, "position");
    }

    protected void loadModelViewMatrix(Matrix4f modelView) {
        super.loadMatrix(location_modelViewMatrix, modelView);
    }

    protected void loadProjectionMatrix(Matrix4f projectionMatrix) {
        super.loadMatrix(location_projectionMatrix, projectionMatrix);
    }
}
