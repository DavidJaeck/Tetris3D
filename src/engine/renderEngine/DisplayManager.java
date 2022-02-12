package engine.renderEngine;

import game.Main;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.*;

// the display is the window showing the game
public class DisplayManager {
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final int FPS_CAP = 120;
    private static long lastFrameTime;
    private static float delta;

    public static void createDisplay() {
        ContextAttribs attribs = new ContextAttribs(3, 2) // open gl-Version
                .withForwardCompatible(true)
                .withProfileCore(true);
        try {
            Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            PixelFormat pixelFormat = new PixelFormat().withSamples(8); // Anti-Aliasing requires withSamples
            Display.create(pixelFormat, attribs);
            Display.setTitle(Main.GAME_NAME);
            GL11.glEnable(GL13.GL_MULTISAMPLE); // enables Anti-Aliasing
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
        GL11.glViewport(0, 0, WIDTH, HEIGHT); // sets where in the window the game is rendered to
        lastFrameTime = getCurrentTime();
    }

    public static void updateDisplay() {
        Display.sync(FPS_CAP);
        Display.update();
        long currentFrameTime = getCurrentTime();
        delta = (currentFrameTime - lastFrameTime) / 1000f;
        lastFrameTime = currentFrameTime;
    }

    public static void closeDisplay() {
        Display.destroy();
    }

    public static float getFrameTimeSeconds(){
        return delta;
    }

    private static long getCurrentTime(){
        return Sys.getTime()*1000/Sys.getTimerResolution();
    }
}
