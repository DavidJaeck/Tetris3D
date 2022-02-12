package game;

public class Main {

    public static final String GAME_NAME = "Tetris 3D";
    public static final String MAIN_THEME_NAME = "korobeiniki";
    public static final String RES_DIR = "res/";
    public static final String RES_FONTS_DIR = RES_DIR + "fonts/";
    public static final String RES_LAUNCHER_DIR = RES_DIR + "launcher/";
    public static final String RES_OBJECTS_DIR = RES_DIR + "objects/";
    public static final String RES_SKYBOX_DIR = RES_DIR + "skybox/";
    public static final String RES_SOUNDS_DIR = RES_DIR + "sounds/";
    public static final String RES_TERRAIN_DIR = RES_DIR + "terrain/";
    public static final String PNG_FILE = ".png";
    public static final String OBJ_FILE = ".obj";
    public static final String WAV_FILE = ".wav";
    public static Launcher launcher;

    public static Launcher getLauncherInstance() {
        if (launcher == null)
            launcher = new Launcher();// only needs to execute the launcher
        return launcher;
    }

    public static void main(String[] args) {
        getLauncherInstance();
    }

}
