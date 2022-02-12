package engine.fontMeshCreator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.Display;

// data class holding the information of the font file
public class FontMetaData {
    private static final int PAD_TOP = 0;
    private static final int PAD_LEFT = 1;
    private static final int PAD_BOTTOM = 2;
    private static final int PAD_RIGHT = 3;
    private static final int DESIRED_PADDING = 8;

    private static final String SPLITTER = " ";
    private static final String NUMBER_SEPARATOR = ",";

    private final double aspectRatio;
    private double xPixelScale;
    private double yPixelScale;
    private double spaceWidth; // the width of a space character
    private int[] padding;
    private int paddingWidth;
    private int paddingHeight;
    private BufferedReader reader;
    private final Map<Integer, Character> metaData = new HashMap<>();
    private final Map<String, String> lineKeyValuePairs = new HashMap<>();

    // reads and parses and saves the information from the font file
    protected FontMetaData(File file) {
        aspectRatio = (double) Display.getWidth() / (double) Display.getHeight();
        openFile(file);
        readPadding();
        calculatePixelScales();
        processCharacters(getValueOfVariable("scaleW"));
        closeFile();
    }

    private boolean getNextLineKeyValues() {
        lineKeyValuePairs.clear(); // clear data of last line
        String line = null;
        try { // read line
            line = reader.readLine();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        if (line == null || line.startsWith("kerning")) {
            return false;
        } // get and save the corresponding key value pairs
        for (String part : line.split(SPLITTER)) {
            String[] valuePairs = part.split("=");
            if (valuePairs.length == 2) {
                lineKeyValuePairs.put(valuePairs[0], valuePairs[1]);
            }
        }
        return true;
    }

    // for the current line gets the value for a given variable as int array
    private int getValueOfVariable(String variable) {
        return Integer.parseInt(lineKeyValuePairs.get(variable));
    }

    // for the current line gets the values for a given variable as int array
    private int[] getValuesOfVariable(String variable) {
        String[] stringValues = lineKeyValuePairs.get(variable).split(NUMBER_SEPARATOR); //
        int[] values = new int[stringValues.length];// cast the array from string to in
        for (int i = 0; i < values.length; i++) {
            values[i] = Integer.parseInt(stringValues[i]);
        }
        return values;
    }

    private void openFile(File file) {
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Couldn't read font meta file!");
        }
    }

    private void closeFile() {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // read how much padding is around the character in the texture atlas
    private void readPadding() {
        getNextLineKeyValues();
        this.padding = getValuesOfVariable("padding");
        this.paddingWidth = padding[PAD_LEFT] + padding[PAD_RIGHT];
        this.paddingHeight = padding[PAD_TOP] + padding[PAD_BOTTOM];
    }

    // calculates the conversion factor the pixel width and height of a line in the texture atlas
    // have to be multiplied by to get to the pixel width and height on the screen
    private void calculatePixelScales() {
        getNextLineKeyValues();
        int lineHeightPixels = getValueOfVariable("lineHeight") - paddingHeight;
        xPixelScale = TextMeshCreator.LINE_HEIGHT / (double) lineHeightPixels;
        yPixelScale = xPixelScale / aspectRatio;
    }

    private void processCharacters(int imageWidth) {
        getNextLineKeyValues();
        getNextLineKeyValues();
        while (getNextLineKeyValues()) {
            Character c = processCharacter(imageWidth);
            if (c != null) {
                metaData.put(c.getId(), c);
            }
        }
    }

    private Character processCharacter(int TASize) {
        int id = getValueOfVariable("id");
        if (id == TextMeshCreator.SPACE_ASCII) {
            this.spaceWidth = (getValueOfVariable("xadvance") - paddingWidth) * yPixelScale;
            return null;
        }
        int width = getValueOfVariable("width") - (paddingWidth - (2 * DESIRED_PADDING)); // remove padding from size
        int height = getValueOfVariable("height") - ((paddingHeight) - (2 * DESIRED_PADDING)); // remove padding from size
        // get x any y pos of the character in the texture atlas
        double xTaPos = ((double) getValueOfVariable("x") + (padding[PAD_LEFT] - DESIRED_PADDING)) / TASize;
        double yTaPos = ((double) getValueOfVariable("y") + (padding[PAD_TOP] - DESIRED_PADDING)) / TASize;
        // get x any y width and height of the character in the texture atlas
        double xTaWidth = (double) width / TASize;
        double yTaHeight = (double) height / TASize;
        // get x any y offset the character has by the cursor
        double xOff = (getValueOfVariable("xoffset") + padding[PAD_LEFT] - DESIRED_PADDING) * yPixelScale;
        double yOff = (getValueOfVariable("yoffset") + (padding[PAD_TOP] - DESIRED_PADDING)) * xPixelScale;
        // get x any y width and height the character has on the screen
        double xScreenWidth = width * yPixelScale;
        double yScreenHeight = height * xPixelScale;
        // get the x length the cursor has to be advanced by
        double xAdvance = (getValueOfVariable("xadvance") - paddingWidth) * yPixelScale;
        return new Character(id, xTaPos, yTaPos, xTaWidth, yTaHeight, xOff, yOff, xAdvance, xScreenWidth, yScreenHeight);
    }

    protected double getSpaceWidth() {
        return spaceWidth;
    }

    // get character by ascii
    protected Character getCharacter(int ascii) {
        return metaData.get(ascii);
    }
}
