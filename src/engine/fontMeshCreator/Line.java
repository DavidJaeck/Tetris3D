package engine.fontMeshCreator;

import java.util.ArrayList;
import java.util.List;

// data class holding the information about a line of a text
public class Line {
    private final double maxLength; // max length of the line on the screen
    private final double spaceSize; // length of a space character in the text
    private final List<Word> words = new ArrayList<>();
    private double currentLineLength = 0;

    // empty line
    protected Line(double spaceWidth, double fontSize, double maxLength) {
        this.spaceSize = spaceWidth * fontSize;
        this.maxLength = maxLength;
    }

    // appends a word to the line if the max line length is not going to be exceeded
    protected boolean tryToAddWord(Word word) {
        double additionalLength = word.getWordWidth();
        additionalLength += !words.isEmpty() ? spaceSize : 0;
        if (currentLineLength + additionalLength <= maxLength) {
            words.add(word);
            currentLineLength += additionalLength;
            return true;
        } else {
            return false;
        }
    }

    protected double getMaxLength() {
        return maxLength;
    }

    protected double getLineLength() {
        return currentLineLength;
    }

    protected List<Word> getWords() {
        return words;
    }

}
