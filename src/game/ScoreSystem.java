package game;

import engine.fontMeshCreator.FontType;
import engine.fontMeshCreator.Text;
import engine.util.Sounds;
import org.lwjgl.util.vector.Vector2f;

public class ScoreSystem {

    private final int[] pointsPerPlane = {40, 100, 300, 1200};

    private final boolean centerScore = false;
    private int score = 0;
    private final int scoreSize = 2;
    private final float maxScoreLength = 0.5f;
    private final Vector2f scorePosition = new Vector2f(0.15f, 0.04f);
    private final FontType scoreFont;

    private final boolean centerLevel = false;
    private int level = 1;
    private final int levelSize = 2;
    private final float maxLevelLength = 0.5f;
    private final Vector2f levelPosition = new Vector2f(0.15f, 0.12f);
    private final FontType levelFont;

    private int levelProgress = 0;


    public ScoreSystem(FontType font) {
        this.scoreFont = font;
        RunGame.scoreValueText =
                new Text(Integer.toString(score), scoreSize, font, scorePosition, maxScoreLength, centerScore);
        RunGame.scoreValueText.setColor(1, 1, 0);
        this.levelFont = font;
        RunGame.levelValueText =
                new Text(Integer.toString(level), levelSize, levelFont, levelPosition, maxLevelLength, centerLevel);
        RunGame.levelValueText.setColor(1, 1, 0);
        Text scoreSting = new Text("Score", 2, font, new Vector2f(0.02f, 0.04f), 1f, false);
        Text levelSting = new Text("Level", 2, font, new Vector2f(0.02f, 0.12f), 1f, false);
        scoreSting.setColor(1, 1, 0);
        levelSting.setColor(1, 1, 0);
        Sounds.loopThemeSong(0);
    }

    public void score(int planeCount, boolean perfect) {
        if (perfect)
            score += pointsPerPlane[planeCount - 1] * level * 2;
        else
            score += pointsPerPlane[planeCount - 1] * level;
        levelProgress += planeCount;
        if (levelProgress >= 10){
            levelProgress = 0;
            level++;
            Sounds.loopThemeSong(level-1);
        }
    }

    public void update() {
        updateScore();
        updateLevel();
    }

    private void updateScore() {
        RunGame.scoreValueText.remove();
        RunGame.scoreValueText =
                new Text(Integer.toString(score), scoreSize, scoreFont, scorePosition, maxScoreLength, centerScore);
        RunGame.scoreValueText.setColor(1, 1, 0);
    }

    private void updateLevel() {
        RunGame.levelValueText.remove();
        RunGame.levelValueText =
                new Text(Integer.toString(level), levelSize, levelFont, levelPosition, maxLevelLength, centerLevel);
        RunGame.levelValueText.setColor(1, 1, 0);
    }

    public int getLevel() {
        return level;
    }
}
