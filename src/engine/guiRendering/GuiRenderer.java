package engine.guiRendering;

import engine.models.RawModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import engine.renderEngine.Loader;
import engine.util.Maths;

import java.util.List;

import static engine.util.Rendering.endRenderingGUIs;
import static engine.util.Rendering.prepareGUIs;

// all renderer classes are similar:
// the bind and unbind necessary variables and most importantly DRAW to the DISPLAY
public class GuiRenderer {
    private final RawModel quad;
    private final GuiShader shader;

    public GuiRenderer(Loader loader) {
        float[] positions = {-1, 1, -1, -1, 1, 1, 1, -1};
        quad = loader.loadToVAO(positions, 2);
        shader = new GuiShader();
    }

    // binds and unbinds Variables enables and disables rendering specific settings and DRAWs the gui
    public void render(List<GuiTexture> guis) {
        prepareGUIs(shader);
        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        for (GuiTexture gui : guis) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTexture()); // binds gui texture
            Matrix4f matrix = Maths.createTransformationMatrix(gui.getPosition(), gui.getScale());
            shader.loadTransformation(matrix); // transforms gui
            GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount()); // DRAWS gui
        }
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        endRenderingGUIs(shader);
    }

    public void cleanUp() {
        shader.cleanUp();
    }
}
