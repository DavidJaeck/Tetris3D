package game;

import engine.entities.Block;
import engine.entities.Camera;
import engine.entities.Entity;
import engine.entities.Light;
import engine.models.TexturedModel;
import engine.fontMeshCreator.FontType;
import engine.fontMeshCreator.Text;
import engine.fontRendering.TextMaster;
import engine.guiRendering.GuiRenderer;
import engine.guiRendering.GuiTexture;
import engine.objConverter.ModelData;
import engine.objConverter.OBJFileLoader;
import engine.util.Sounds;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import engine.particleRendering.ComplexParticleSystem;
import engine.particleRendering.ParticleMaster;
import engine.particleRendering.ParticleTexture;
import engine.renderEngine.*;
import engine.models.RawModel;
import engine.terrainRendering.Terrain;
import engine.models.ModelTexture;
import engine.terrainRendering.TerrainTexture;
import engine.terrainRendering.TerrainTexturePack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

// is called by the Launcher and runs the whole game
public class RunGame {
    public static boolean gameOver = false;
    public Text gameOverText = null;
    public Text restartText = null;
    public static TexturedModel[] fieldModels;
    public FontType font;
    private static final float MIPMAP_BIAS = 0; // deprecated

    // tick writes to these variables, render reads from them
    public static Text scoreValueText;
    public static Text levelValueText;
    public static Block[][][] fieldCubes;

    // variables for rendering
    private Loader loader;
    private Light light;
    private Camera camera;
    private MasterRenderer masterRenderer;
    private GuiRenderer guiRenderer;

    // variables displayed of the game
    private ComplexParticleSystem complexParticleSystem;
    private Terrain terrain;
    private Field field;
    private Entity cage;
    private Block[][] cageBottom;
    private List<GuiTexture> guis;
    private ScoreSystem scoreSystem;

    // variables for the game loop
    private int frameCount = 0;
    private int tickCount = 0;
    private int fps = 0;
    private final int tickRate = 60;
    private double unprocessedSeconds = 0;
    private long previousTime = System.nanoTime();
    private boolean wasCloseRequested = false;
    private boolean pause = false;

    public static void main(String[] args) {
        new RunGame();
    }

    public RunGame() {
        Main.getLauncherInstance().stopMenu(); // stops launcher thread
        init();
        loadWorld();
        gameLoop();
        cleanUp();
    }

    // initializes required variables
    private void init() {
        DisplayManager.createDisplay();
        loader = new Loader();
        masterRenderer = new MasterRenderer(loader);
        ParticleMaster.init(loader, masterRenderer.getProjectionMatrix());
        guiRenderer = new GuiRenderer(loader);
        TextMaster.init(loader);
        camera = new Camera();
        field = new Field();
        light = new Light(new Vector3f(3000, 2000, 2000), new Vector3f(1, 1, 1));
        fieldCubes = new Block[Field.X_LENGTH][Field.Y_LENGTH][Field.Z_LENGTH];
        font = new FontType(loader.loadTexture(Main.RES_FONTS_DIR + "ocr_a_extended", 0f), new File(Main.RES_FONTS_DIR + "ocr_a_extended.fnt"));
        scoreSystem = new ScoreSystem(font);
        gameOverText = new Text("", 1, font, new Vector2f(0.25f, 0.4f), 0.5f, true);
        restartText = new Text("", 1, font, new Vector2f(0.25f, 0.4f), 0.5f, true);
    }

    private void loadWorld() {
        loadFieldModels();
        loadCage();
        loadTerrain();
        loadGUI();
        loadParticleSystem();
    }

    // loads, parses and stores the wavefront-cube-objects and their textures for later use on the field
    private void loadFieldModels() {
        fieldModels = new TexturedModel[8];
        ModelData modelData = OBJFileLoader.loadOBJ("cube"); // parses the object file
        RawModel rawModel = loader.loadToVAO(modelData.getVertices(), modelData.getTextureCoords(),
                modelData.getNormals(), modelData.getIndices()); // loads the modelData to VAO
        ModelTexture modelTexture = new ModelTexture(loader.loadTexture(Main.RES_OBJECTS_DIR + "cubeBlue", MIPMAP_BIAS)); // loads one of the corresponding texture maps
        fieldModels[0] = new TexturedModel(rawModel, modelTexture); // stores a modelTexture for later use
        modelTexture = new ModelTexture(loader.loadTexture(Main.RES_OBJECTS_DIR + "cubeRed", MIPMAP_BIAS));
        fieldModels[1] = new TexturedModel(rawModel, modelTexture);
        modelTexture = new ModelTexture(loader.loadTexture(Main.RES_OBJECTS_DIR + "cubeYellow", MIPMAP_BIAS));
        fieldModels[2] = new TexturedModel(rawModel, modelTexture);
        modelTexture = new ModelTexture(loader.loadTexture(Main.RES_OBJECTS_DIR + "cubeGreen", MIPMAP_BIAS));
        fieldModels[3] = new TexturedModel(rawModel, modelTexture);
        modelTexture = new ModelTexture(loader.loadTexture(Main.RES_OBJECTS_DIR + "cubeOrange", MIPMAP_BIAS));
        fieldModels[4] = new TexturedModel(rawModel, modelTexture);
        modelTexture = new ModelTexture(loader.loadTexture(Main.RES_OBJECTS_DIR + "cubePurple", MIPMAP_BIAS));
        fieldModels[5] = new TexturedModel(rawModel, modelTexture);
        modelTexture = new ModelTexture(loader.loadTexture(Main.RES_OBJECTS_DIR + "cubeCyan", MIPMAP_BIAS));
        fieldModels[6] = new TexturedModel(rawModel, modelTexture);
        modelTexture = new ModelTexture(loader.loadTexture(Main.RES_OBJECTS_DIR + "cubeGrey", MIPMAP_BIAS));
        fieldModels[7] = new TexturedModel(rawModel, modelTexture);
        // creates the bocks forming the bottom of the field
        cageBottom = new Block[10][10]; // plane of blocks
        for (int i = 0; i < Field.X_LENGTH - 2; i++) {
            for (int j = 0; j < Field.Z_LENGTH - 2; j++) {
                cageBottom[i][j] = new Block(fieldModels[7], new Vector3f(i - 5, 1.1f, j - 5), 0, 0, 0, 1, 7);
            }
        }
    }

    // loads the cage containing the playing field
    private void loadCage() {
        ModelData modelData = OBJFileLoader.loadOBJ("cage"); // parses the object file
        RawModel rawModel = loader.loadToVAO(modelData.getVertices(), modelData.getTextureCoords(),
                modelData.getNormals(), modelData.getIndices()); // loads the modelData to VAO
        ModelTexture modelTexture = new ModelTexture(loader.loadTexture(Main.RES_OBJECTS_DIR + "cage", MIPMAP_BIAS)); // loads the corresponding texture map
        TexturedModel texturedModel = new TexturedModel(rawModel, modelTexture); // construct the finished model
        cage = new Entity(texturedModel, new Vector3f(0, 0, 0), 0, 0, 0, 1); // constructs entity from model that can be rendered
    }

    private void loadTerrain() {
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture(Main.RES_TERRAIN_DIR + "blueTile", MIPMAP_BIAS));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture(Main.RES_TERRAIN_DIR + "redTile", MIPMAP_BIAS));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture(Main.RES_TERRAIN_DIR + "greenTile", MIPMAP_BIAS));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture(Main.RES_TERRAIN_DIR + "yellowTile", MIPMAP_BIAS));
        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture(Main.RES_TERRAIN_DIR + "blendMap", MIPMAP_BIAS));
        terrain = new Terrain(-0.5f, -0.5f, loader, texturePack, blendMap);
    }

    private void loadGUI() {
        guis = new ArrayList<>();
        GuiTexture logo = new GuiTexture(loader.loadTexture(Main.RES_DIR + "logo", MIPMAP_BIAS), new Vector2f(0.95f, -0.95f), new Vector2f(0.1f, 0.1725f));
        guis.add(logo);
    }

    private void loadParticleSystem() {
        ParticleTexture particleTexture = new ParticleTexture(loader.loadTexture(Main.RES_DIR + "star", 0f));
        complexParticleSystem = new ComplexParticleSystem(particleTexture, 40, 10, 0.1f, 1, 1.6f);
        //complexParticleSystem.generateParticles(new Vector3f(0, 0, 0));
    }

    // ticks and renders the game
    private void gameLoop() {
        while (!wasCloseRequested) {
            wasCloseRequested = Display.isCloseRequested(); // will remember that close was requested
            long currentTime = System.nanoTime();
            unprocessedSeconds += (currentTime - previousTime) / 1000000000.0;
            previousTime = currentTime;
            while (unprocessedSeconds > 1.0 / tickRate) {
                if (Keyboard.isKeyDown(Keyboard.KEY_RETURN)) // resume game
                    pause = false;
                if (!pause) // tick if not paused
                    tick();
                unprocessedSeconds -= 1.0 / tickRate;
                tickCount++;
                render(); // render always
                frameCount++;
                if (tickCount % 60 == 0) { // one second has passed
                    fps = frameCount;
                    frameCount = 0;
                    previousTime += 1000;
                }
            }
        }
    }

    // every tick player inputs and the determined processes of the game have to be handled
    private void tick() {
        camera.handleCameraMovement(); // the player can adjust the camera
        ParticleMaster.update();
        //complexParticleSystem.generateParticles(new Vector3f(0, 0, 0)); // could be used to show particles

        if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) // pause the game
            pause = true;
        if (!gameOver && tickCount % (40 + tickRate / scoreSystem.getLevel()) == 0) // every n ticks the current module is lowered by force
            field.lowerModule(scoreSystem); // can cause gameOver
        if (!gameOver) { // only if lowering the module did not cause gameOver the player is allowed to play on
            field.handleModuleManeuver(camera.getCirclePos(), scoreSystem); // can cause gameOver
            field.update(); // resolves fully filled planes
            scoreSystem.update(); // scores
        } else {
            gameOverLoop(); // enter gameOver loop
        }
    }

    // calls the render methods for all the
    private void render() {
        masterRenderer.processField();
        masterRenderer.processCageBottom(cageBottom);
        masterRenderer.processNextModuleHint(field.getNextModule(), camera);
        masterRenderer.processEntity(cage);
        masterRenderer.processTerrain(terrain);
        masterRenderer.render(light, camera);

        ParticleMaster.render(camera);
        guiRenderer.render(guis);
        TextMaster.render();

        DisplayManager.updateDisplay(); // updates the displayed frame with the recently rendered
    }

    // blends in/ blends out game overText waiting for the player to restart
    private void gameOverLoop() {
        if ((tickCount % 60) > 30) { // blend in game over text
            gameOverText.remove();
            gameOverText = new Text("Game Over", 3, font, new Vector2f(0.25f, 0.4f), 0.5f, true);
            gameOverText.setColor(1, 1, 0);
            restartText.remove();
            restartText = new Text("Press Enter To Restart", 3, font, new Vector2f(0.25f, 0.5f), 0.5f, true);
            restartText.setColor(1, 1, 0);
        } else { // blend out text
            if (gameOverText != null) {
                gameOverText.remove();
            }
            if (restartText != null) {
                restartText.remove();
            }
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_RETURN)) { // start new game
            restartGame();
        }
    }

    // when the game over state is reached, sets up new game
    private void restartGame() {
        gameOver = false;
        gameOverText.remove();
        restartText.remove();
        Sounds.stopThemeSong(); // creating a will start the song again
        scoreSystem = new ScoreSystem(font);
        field = new Field();
    }

    // free memory
    private void cleanUp() {
        TextMaster.cleanUp();
        guiRenderer.cleanUp();
        ParticleMaster.cleanUp();
        masterRenderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}
