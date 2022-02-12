package engine.fontRendering;

import engine.fontMeshCreator.FontType;
import engine.fontMeshCreator.Text;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.List;
import java.util.Map;

import static engine.util.Rendering.endRenderingGUIs;
import static engine.util.Rendering.prepareGUIs;

// all renderer classes are similar:
// the bind and unbind necessary variables and most importantly DRAW to the DISPLAY
public class FontRenderer {
    private final FontShader shader;

    public FontRenderer() {
        shader = new FontShader();
    }

    // binds and und binds Variables enables and disables rendering specific settings and DRAWs Text
    public void render(Map<FontType, List<Text>> texts) {
        prepareGUIs(shader);
        for (FontType font : texts.keySet()) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0); // binding every Font that has text
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, font.getTextureAtlas());
            for (Text text : texts.get(font))
                renderText(text); // rendering every text for a font
        }
        endRenderingGUIs(shader);
    }

    // binds and unbinds Variables enables and disables rendering specific settings and DRAWs the Text
    private void renderText(Text text) {
        GL30.glBindVertexArray(text.getMesh());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        shader.loadColor(text.getColor());
        shader.loadTranslation(text.getPosition());
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, text.getVertexCount()); // DRAW Text as polygons
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
    }

    public void cleanUp() {
        shader.cleanUp();
    }
}
