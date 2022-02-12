package engine.renderEngine;

import engine.entities.Block;
import engine.entities.Camera;
import engine.entities.Entity;
import engine.entities.Light;
import game.Field;
import game.Module;
import game.RunGame;
import engine.models.TexturedModel;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import engine.shaderEngine.StaticShader;
import engine.terrainRendering.TerrainRenderer;
import engine.terrainRendering.TerrainShader;
import engine.skyboxRendering.SkyboxRenderer;
import engine.terrainRendering.Terrain;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// higher level management for Rendering all Objects
public class MasterRenderer {
    //variables defining the projected field of view
    private static final float FOV = 70;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000;

    private final StaticShader shader;
    private final TerrainShader terrainShader;
    private final EntityRenderer entityRenderer;
    private final TerrainRenderer terrainRenderer;
    private final SkyboxRenderer skyboxRenderer;

    private final Map<TexturedModel, List<Entity>> entities; // batches of entities
    private final List<Terrain> terrains; // batches of terrains

    private Matrix4f projectionMatrix;

    public MasterRenderer(Loader loader) {
        createProjectionMatrix();
        enableCulling(); // does not render faces behind objects
        entities = new HashMap<>();
        terrains = new ArrayList<>();
        shader = new StaticShader();
        terrainShader = new TerrainShader();
        entityRenderer = new EntityRenderer(shader, projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
        skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
    }

    // constructs the projectionMatrix based on the field of view angle and the near and far plane distance
    private void createProjectionMatrix() {
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float yScale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float xScale = yScale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = xScale;
        projectionMatrix.m11 = yScale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projectionMatrix.m33 = 0;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public static void enableCulling(){
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    public static void disableCulling(){
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    // executes the various render methods
    public void render(Light sun, Camera camera) {
        prepare();

        shader.start();
        shader.loadLight(sun);
        shader.loadViewMatrix(camera);
        entityRenderer.render(entities);
        shader.stop();

        terrainShader.start();
        terrainShader.loadLight(sun);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains);
        terrainShader.stop();
        skyboxRenderer.render(camera);

        terrains.clear();
        entities.clear();
    }

    // add generic entity to list of to be rendered entities
    public void processEntity(Entity entity) {
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);
        if (batch != null) {
            batch.add(entity);
        } else {
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
        }
    }

    // adds the field entities to list of to be rendered entities
    public void processField() {
        for (int x = 0; x < Field.X_LENGTH; x++) {
            for (int y = 0; y < Field.Y_LENGTH; y++) {
                for (int z = 0; z < Field.Z_LENGTH; z++) {
                    if (RunGame.fieldCubes[x][y][z] != null)
                        processEntity(RunGame.fieldCubes[x][y][z]);
                }
            }
        }
    }

    // adds the entities forming the bottom of the field to the list of to be rendered entities
    public void processCageBottom(Block[][] cageBottom) {
        for (int i = 0; i < Field.X_LENGTH-2; i++) {
            for (int j = 0; j < Field.Z_LENGTH-2; j++) {
                processEntity(cageBottom[i][j]);
            }
        }
    }

    // adds the entities of the nextModuleHint to list of to be rendered entities
    public void processNextModuleHint(Module nextModule, Camera camera) {
        Vector3f position = camera.getNextModulePosition();
        for (int x = 0; x < Module.SIDE_LENGTH; x++) {
            for (int y = 0; y < Module.SIDE_LENGTH; y++) {
                for (int z = 0; z < Module.SIDE_LENGTH; z++) {
                    if (nextModule.getConfiguration()[x][y][z])
                        processEntity(new Entity(RunGame.fieldModels[nextModule.getType() - 1], // -1 because spaces[X][Y][Z]=0 means a free space
                                new Vector3f(position.x+x, position.y+y, position.z+ z), 0, 0, 0, 1));
                }
            }
        }
    }

    // adds a terrain entity to list of to be rendered terrains
    public void processTerrain(Terrain terrain){
        terrains.add(terrain);
    }

    public void prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST); // renders the triangles that are in the front and therefore should be seen
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // clears color and depths of previous frame
        GL11.glClearColor(1, 1, 1, 1);
    }

    public void cleanUp() {
        shader.cleanUp();
        terrainShader.cleanUp();
    }

}
