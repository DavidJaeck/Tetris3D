package engine.fontMeshCreator;

import java.io.File;

// data class holding the information about a font
public class FontType {
    private final int textureAtlas;
    private final TextMeshCreator loader;

    public FontType(int textureAtlas, File fontFile) {
        this.textureAtlas = textureAtlas; // sets the texture atlas
        this.loader = new TextMeshCreator(fontFile); // sets the metaData about the texture atlas from the font file
    }

    public int getTextureAtlas() {
        return textureAtlas;
    }

    // for a text calculates the vertices for all the letter textures
    public TextMeshData loadText(Text text) {
        return loader.createTextMesh(text);
    }

}
