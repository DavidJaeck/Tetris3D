package engine.util;

import engine.shaderEngine.ShaderProgram;
import org.lwjgl.opengl.GL11;

public class Rendering {
    // enable rendering specific settings
    public static void prepareGUIs(ShaderProgram shader) {
        GL11.glEnable(GL11.GL_BLEND); // enable Alpha-blend -> transparency
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST); // no Depth-testing needed Text is always in front
        shader.start();
    }

    //disable rendering specific settings
    public static void endRenderingGUIs(ShaderProgram shader) {
        shader.stop();
        GL11.glDisable(GL11.GL_BLEND); // disable Alpha-blend again
        GL11.glEnable(GL11.GL_DEPTH_TEST); // enable Depth-test again
    }
}
