package engine.fontMeshCreator;

// data class holding the information of a character in the texture atlas
class Character {
    private final int id;
    private final double xTaStartPos; // top left corner x pos of the char in the texture atlas
    private final double yTaStartPos; // top left corner y pos of the char in the texture atlas
    private final double xTaEndPos;  // bottom right corner x pos of the char in the texture atlas
    private final double yTaEndPos; // bottom right corner y pos of the char in the texture atlas
    private final double xOff; // x off set of the character by the cursor
    private final double yOff;// y off set of the character by the cursor
    private final double xAdvance; // x advance that has to be performed by the cursor for this character
    private final double xScreenWidth; // width the character has on the screen with the given res
    private final double yScreenHeight; // height the character has on the screen with the given res

    protected Character(int id, double xTaPos, double yTaPos, double xTaWidth, double yTaHeight,
                        double xOff, double yOff, double xAdvance, double xScreenWidth, double yScreenHeight) {
        this.id = id;
        this.xTaStartPos = xTaPos;
        this.yTaStartPos = yTaPos;
        this.xTaEndPos = xTaPos + xTaWidth;
        this.yTaEndPos = yTaPos + yTaHeight;
        this.xOff = xOff;
        this.yOff = yOff;
        this.xAdvance = xAdvance;
        this.xScreenWidth = xScreenWidth;
        this.yScreenHeight = yScreenHeight;
    }

    protected int getId() {
        return id;
    }

    protected double getXTaStartPos() {
        return xTaStartPos;
    }

    protected double getYTaStartPos() {
        return yTaStartPos;
    }

    protected double getXTaEndPos() {
        return xTaEndPos;
    }

    protected double getYTaEndPos() {
        return yTaEndPos;
    }

    protected double getXOff() {
        return xOff;
    }

    protected double getYOff() {
        return yOff;
    }

    protected double getXAdvance() {
        return xAdvance;
    }

    protected double getXScreenWidth() {
        return xScreenWidth;
    }

    protected double getYScreenHeight() {
        return yScreenHeight;
    }


}
