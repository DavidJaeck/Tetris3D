package engine.fontRendering;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import engine.shaderEngine.ShaderProgram;

// shader classes have the two purposes:
// compiling the vertex and fragment shader code
// binding and unbinding the necessary variables for the compiled shader programs on the gpu
public class FontShader extends ShaderProgram {
    private static final String VERTEX_FILE = "src/engine/fontRendering/vertexShader.txt";
    private static final String FRAGMENT_FILE = "src/engine/fontRendering/fragmentShader.txt";
    private int location_color;
    private int location_translation;

    public FontShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getALlUniformLocations() {
        location_color = super.getUniformLocation("color");
        location_translation = super.getUniformLocation("translation");

    }

    @Override
    protected void bindAllAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
    }

    protected void loadColor(Vector3f color) {
        super.loadVector(location_color, color);
    }

    protected void loadTranslation(Vector2f translation) {
        super.load2DVector(location_translation, translation);
    }
}
