package engine.fontMeshCreator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TextMeshCreator {
    protected static final double LINE_HEIGHT = 0.03f;
    protected static final int SPACE_ASCII = 32;
    private final FontMetaData fontMetaData;

    protected TextMeshCreator(File fontFile) {
        fontMetaData = new FontMetaData(fontFile);
    }

    protected TextMeshData createTextMesh(Text text) {
        List<Line> lines = convertToLines(text);
        return createQuadVertices(text, lines);
    }

    // constructs all the lines of the text by constructing all the words of the text
    private List<Line> convertToLines(Text text) {
        char[] chars = text.getTextString().toCharArray(); // get all characters
        List<Line> lines = new ArrayList<>();
        Line currentLine = new Line(fontMetaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineLength());
        Word currentWord = new Word(text.getFontSize());
        for (char c : chars) {
            if (wordCompleted(c)) {
                currentLine = addWordToCurLine(text, lines, currentLine, currentWord);
                currentWord = new Word(text.getFontSize()); // get a new word characters can be appended to
            } else {
                addCharToCurWord(currentWord, c);
            }
        }// add the last word to the last line and add the last line to lines
        currentLine = addWordToCurLine(text, lines, currentLine, currentWord);
        lines.add(currentLine);
        return lines;
    }

    private boolean wordCompleted(char c) {
        return (int) c == SPACE_ASCII;
    }

    private Line addWordToCurLine(Text text, List<Line> lines, Line cl, Word cw) {
        boolean added = cl.tryToAddWord(cw);
        if (!added) { // if the word could not be added
            lines.add(cl); // add the current line to the list of completed ones
            cl = new Line(fontMetaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineLength()); // make a new line
            cl.tryToAddWord(cw); // and add the word to the line
        }
        return cl; // return the potentially new line
    }

    private void addCharToCurWord(Word currentWord, char c) {
        Character character = fontMetaData.getCharacter(c);
        currentWord.addCharacter(character);
    }

    // create quadVertices for every letter in every word in all the lines
    private TextMeshData createQuadVertices(Text text, List<Line> lines) {
        text.setNumberOfLines(lines.size());
        double cursorY = 0f;
        List<Float> vertices = new ArrayList<>();
        List<Float> textureCoords = new ArrayList<>();
        for (Line line : lines) { // for all lines
            double cursorX = getCursorXStart(text, line);
            for (Word word : line.getWords()) { // for all words
                for (Character letter : word.getCharacters()) { // for all letters
                    addVerticesForCharacter(vertices, cursorX, cursorY, letter, text.getFontSize());
                    addToList(textureCoords, letter.getXTaStartPos(), letter.getYTaStartPos(),
                            letter.getXTaEndPos(), letter.getYTaEndPos());
                    cursorX += letter.getXAdvance() * text.getFontSize();
                }
                cursorX += fontMetaData.getSpaceWidth() * text.getFontSize();
            }
            cursorY += LINE_HEIGHT * text.getFontSize();
        }
        return new TextMeshData(listToArray(vertices), listToArray(textureCoords));
    }

    private double getCursorXStart(Text text, Line line) {
        if (text.isCentered()) { // start at half the size of the size the line would leave blank
            return (line.getMaxLength() - line.getLineLength()) / 2;
        }
        return 0;
    }

    private void addVerticesForCharacter(List<Float> vertices,
                                         double cursorX, double cursorY, Character character, double fontSize) {
        double x = cursorX + (character.getXOff() * fontSize);
        double y = cursorY + (character.getYOff() * fontSize);
        double maxX = x + (character.getXScreenWidth() * fontSize);
        double maxY = y + (character.getYScreenHeight() * fontSize);
        double properX = (2 * x) - 1;
        double properY = (-2 * y) + 1;
        double properMaxX = (2 * maxX) - 1;
        double properMaxY = (-2 * maxY) + 1;
        addToList(vertices, properX, properY, properMaxX, properMaxY);
    }

    // adds texture coordinates oder vertices to the corresponding list
    private static void addToList(List<Float> vertices, double x, double y, double maxX, double maxY) {
        vertices.add((float) x);
        vertices.add((float) y);
        vertices.add((float) x);
        vertices.add((float) maxY);
        vertices.add((float) maxX);
        vertices.add((float) maxY);
        vertices.add((float) maxX);
        vertices.add((float) maxY);
        vertices.add((float) maxX);
        vertices.add((float) y);
        vertices.add((float) x);
        vertices.add((float) y);
    }

    private static float[] listToArray(List<Float> listOfFloats) {
        float[] array = new float[listOfFloats.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = listOfFloats.get(i);
        }
        return array;
    }


}
