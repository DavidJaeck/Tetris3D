package engine.fontRendering;

import engine.fontMeshCreator.FontType;
import engine.fontMeshCreator.Text;
import engine.fontMeshCreator.TextMeshData;
import engine.renderEngine.Loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// handles the rendering of all texts
public class TextMaster {
    private static Loader loader;
    private static final Map<FontType, List<Text>> texts = new HashMap<>();
    private static FontRenderer renderer;

    public static void init(Loader l) {
        renderer = new FontRenderer();
        loader = l;
    }

    public static void render() {
        renderer.render(texts);
    }

    // puts a text into the list of texts that are to be rendered
    public static void loadText(Text text) {
        FontType font = text.getFont();
        TextMeshData data = font.loadText(text);
        int vao = loader.loadToVAO(data.getVertexPositions(), data.getTextureCoords());
        text.setMeshInfo(vao, data.getVertexCount());
        List<Text> textBatch = texts.get(font);
        if (textBatch == null) {
            textBatch = new ArrayList<>();
            texts.put(font, textBatch);
        }
        textBatch.add(text);
    }

    // removes a text into the list of texts that are to be rendered
    public static void removeText(Text text) {
        List<Text> textBatch = texts.get(text.getFont());
        textBatch.remove(text);
        if (textBatch.isEmpty())
            texts.remove(text.getFont());

    }

    public static void cleanUp() {
        renderer.cleanUp();
    }
}
