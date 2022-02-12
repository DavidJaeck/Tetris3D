package engine.fontMeshCreator;

import java.util.ArrayList;
import java.util.List;

// data class holding the information for words of a text
public class Word {
    private final List<Character> characters = new ArrayList<>();
    private final double fontSize;
    private double width = 0;

    protected Word(double fontSize) {
        this.fontSize = fontSize;
    }

    // if a character is added the words' width grows by the characters width
    protected void addCharacter(Character character) {
        characters.add(character);
        width += character.getXAdvance() * fontSize;
    }

    protected List<Character> getCharacters() {
        return characters;
    }

    protected double getWordWidth() {
        return width;
    }

}
