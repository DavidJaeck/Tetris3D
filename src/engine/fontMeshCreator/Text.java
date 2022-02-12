package engine.fontMeshCreator;

import engine.fontRendering.TextMaster;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

// data class holding the information about a text
public class Text {
    private final boolean isCentered;
    private int textMeshVao; // VAO-ID of the text
    private int vertexCount; // number of vertices in all quads of the text
    private int numberOfLines; // amount of lines the text spans over (is calculated)
    private final float maxLineLength; // how long one line of the text can be
    private final float fontSize;
    private final String textString;
    private final Vector2f position; // top left coordinate of the text, x=0;y=0 corresponds with the top left corner
    private final Vector3f color = new Vector3f(0f, 0f, 0f); // rgb-Color for the text, each between 0 and 1
    private final FontType font;

    public Text(String textString, float size, FontType font, Vector2f pos, float maxLineLength, boolean isCentered) {
        this.textString = textString;
        this.fontSize = size;
        this.font = font;
        this.position = pos;
        this.maxLineLength = maxLineLength;
        this.isCentered = isCentered;
        TextMaster.loadText(this); // load up text to TextMaster
    }

    public void remove() {
        TextMaster.removeText(this);
    } // remove this text from the screen

    public void setMeshInfo(int vao, int verticesCount) {
        this.textMeshVao = vao;
        this.vertexCount = verticesCount;
    }

    protected boolean isCentered() {
        return isCentered;
    }

    public int getMesh() {
        return textMeshVao;
    }

    public int getVertexCount() {
        return this.vertexCount;
    }

    public int getNumberOfLines() {
        return numberOfLines;
    }

    protected void setNumberOfLines(int number) {
        this.numberOfLines = number;
    }

    protected float getMaxLineLength() {
        return maxLineLength;
    }

    protected float getFontSize() {
        return fontSize;
    }

    protected String getTextString() {
        return textString;
    }

    public Vector2f getPosition() {
        return position;
    }

    public void setColor(float r, float g, float b) {
        color.set(r, g, b);
    }

    public Vector3f getColor() {
        return color;
    }

    public FontType getFont() {
        return font;
    }
}
