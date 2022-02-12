package engine.terrainRendering;

import engine.entities.Camera;
import engine.entities.Light;
import org.lwjgl.util.vector.Matrix4f;
import engine.shaderEngine.ShaderProgram;
import engine.util.Maths;

// shader classes have the two purposes:
// compiling the vertex and fragment shader code
// binding and unbinding the necessary variables for the compiled shader programs on the gpu
public class TerrainShader extends ShaderProgram {
    private static final String VERTEX_FILE = "src/engine/terrainRendering/vertexShader";
    private static final String FRAGMENT_FILE = "src/engine/terrainRendering/fragmentShader";
    private int location_transformationMatrix; // enables scaling, moving and rotation of objects in the world
    private int location_projectionMatrix; // enables projecting the 3D world onto the 2D screen
    private int location_viewMatrix; // enables the movement of the camera
    private int location_lightPosition; //enables lighting
    private int location_lightColor;
    private int location_shineDamper; // enables specular lighting
    private int location_reflectivity;
    private int location_backgroundTexture;
    private int location_rTexture; // enables the utilization of the blendMap
    private int location_gTexture;
    private int location_bTexture;
    private int location_blendMap;

    public TerrainShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAllAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoordinates");
        super.bindAttribute(2, "normal");
    }

    @Override
    protected void getALlUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_lightPosition = super.getUniformLocation("lightPosition");
        location_lightColor = super.getUniformLocation("lightColor");
        location_shineDamper = super.getUniformLocation("shineDamper");
        location_reflectivity = super.getUniformLocation("reflectivity");
        location_backgroundTexture = super.getUniformLocation("backgroundTexture");
        location_rTexture = super.getUniformLocation("rTexture");
        location_gTexture = super.getUniformLocation("gTexture");
        location_bTexture = super.getUniformLocation("bTexture");
        location_blendMap = super.getUniformLocation("blendMap");
    }

    public void connectTextureUnits() {
        super.loadInt(location_backgroundTexture, 0);
        super.loadInt(location_rTexture, 1);
        super.loadInt(location_gTexture, 2);
        super.loadInt(location_bTexture, 3);
        super.loadInt(location_blendMap, 4);
    }

    public void loadShineVariables(float damper, float reflectivity) {
        super.loadFloat(location_shineDamper, damper);
        super.loadFloat(location_reflectivity, reflectivity);
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    public void loadLight(Light light) {
        super.loadVector(location_lightPosition, light.getPosition());
        super.loadVector(location_lightColor, light.getColor());
    }

    public void loadViewMatrix(Camera camera) {
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        super.loadMatrix(location_viewMatrix, viewMatrix);
    }

    public void loadProjectionMatrix(Matrix4f matrix) {
        super.loadMatrix(location_projectionMatrix, matrix);
    }
}
