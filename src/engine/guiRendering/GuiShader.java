package engine.guiRendering;

import org.lwjgl.util.vector.Matrix4f;
import engine.shaderEngine.ShaderProgram;

// shader classes have the two purposes:
// compiling the vertex and fragment shader code
// binding/uploading and unbinding the necessary variables for the compiled shader programs on the gpu
public class GuiShader extends ShaderProgram {
    private static final String VERTEX_FILE = "src/engine/guiRendering/guiVertexShader.txt";
    private static final String FRAGMENT_FILE = "src/engine/guiRendering/guiFragmentShader.txt";
    private int location_transformationMatrix;

    public GuiShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    public void loadTransformation(Matrix4f matrix) {
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    @Override
    protected void getALlUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
    }

    @Override
    protected void bindAllAttributes() {
        super.bindAttribute(0, "position");
    }
}
