package game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serial;

//this class is responsible for displaying the launcher
//the player can start the game, display the controls and exit the launcher
public class Launcher extends Canvas implements Runnable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final JFrame frame;
    private int frame_id = 0;

    private final int height = 400;
    private final int width = (int) (height * 1.2 * 16 / 9);
    private boolean running = false;
    private double unprocessedSeconds = 0;
    private long previousTime = System.nanoTime();
    private boolean display_controls = false;
    private int controlsToggleCoolDown = 0;

    private BufferedImage[] background;
    private BufferedImage play;
    private BufferedImage playGrey;
    private BufferedImage help;
    private BufferedImage helpGrey;
    private BufferedImage options;
    private BufferedImage optionsGrey;
    private BufferedImage quit;
    private BufferedImage quitGrey;
    private BufferedImage controls;


    public Launcher() {
        try { // changes java buttons into user specific buttons
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadImages();
        frame = new JFrame();
        JPanel window = new JPanel();
        frame.setUndecorated(true);
        frame.setTitle("Tetris Launcher");
        frame.setSize(new Dimension(width, height));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.pack();
        window.setLayout(null);
        InputHandler input = new InputHandler();
        addKeyListener(input);
        addFocusListener(input);
        addMouseListener(input);
        addMouseMotionListener(input);
        startMenu();
        frame.repaint(); // force Buttons to show up
    }

    // loads all images and the gif for the UI
    private void loadImages() {
        background = loadGif("gif/");
        play = loadImg("play");
        playGrey = loadImg("play_grey");
        help = loadImg("info");
        helpGrey = loadImg("info_grey");
        options = loadImg("options");
        optionsGrey = loadImg("options_grey");
        quit = loadImg("exit");
        quitGrey = loadImg("exit_grey");
        controls = loadImg("controls");
    }

    // given the directory loads the images of the gif
    private BufferedImage[] loadGif(String directory) {
        BufferedImage[] gif = new BufferedImage[61];
        for (int i = 1; i < 62; i++) {
            try {
                File file = new File(Main.RES_LAUNCHER_DIR + directory + "ezgif-frame-0" + i + Main.PNG_FILE);
                gif[i-1] = ImageIO.read(file);
            } catch (IOException e) {
                System.err.println("Error loading image of gif:" + directory + " frame: " + i);
                e.printStackTrace();
            }
        }
        return gif;
    }

    // loads a single image by name from the launcher res folder
    private BufferedImage loadImg(String file) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(Main.RES_LAUNCHER_DIR + file + Main.PNG_FILE));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public void startMenu() {
        if (running)
            return;
        running = true;
        Thread thread = new Thread(this, "menu");
        thread.start();
    }

    public void stopMenu() {
        if (!running)
            return;
        running = false;
    }

    // launcher loop
    @Override
    public void run() {
        requestFocus();
        while (running) {
            long currentTime = System.nanoTime();
            unprocessedSeconds += (currentTime - previousTime) / 1000000000.0;
            previousTime = currentTime;
            int gifSpeed = 30;
            while (unprocessedSeconds > 1.0 / gifSpeed) {
                unprocessedSeconds -= 1.0 / gifSpeed;
                try {
                    renderMenu();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //displays launcher content and handles player inputs
    public void renderMenu() throws IllegalStateException {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }
        // draws the gif
        Graphics g = bs.getDrawGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
        g.drawImage(background[frame_id++ % 61], 0, 0, height * 16 / 9, height, null);

        //draws the 4 icons and reacts to the player clicking on them
        //play
        if (isHovered(760, 50, 40, 40)) {
            g.drawImage(play, 760, 50, 40, 40, null);
            if (InputHandler.mouseButton == 1) {
                frame.dispose();
                new RunGame();
            }
        } else {
            g.drawImage(playGrey, 760, 50, 40, 40, null);
        }
        //options
        if (isHovered(760, 110, 40, 40)) {
            g.drawImage(options, 760, 110, 40, 40, null);
            if (InputHandler.mouseButton == 1)
                System.out.println("no options implemented");
            // options could be implemented here
        } else {
            g.drawImage(optionsGrey, 760, 110, 40, 40, null);
        }
        //help
        if (isHovered(760, 170, 40, 40)) {
            g.drawImage(help, 760, 170, 40, 40, null);
            if (InputHandler.mouseButton == 1) {
                if (controlsToggleCoolDown == 0) {
                    controlsToggleCoolDown = 10;
                    display_controls = !display_controls;
                }
            }
        } else {
            g.drawImage(helpGrey, 760, 170, 40, 40, null);
        }
        // quit
        if (isHovered(760, 230, 40, 40)) {
            g.drawImage(quit, 760, 230, 40, 40, null);
            if (InputHandler.mouseButton == 1)
                System.exit(0);
        } else {
            g.drawImage(quitGrey, 760, 230, 40, 40, null);
        }
        if (display_controls)
            g.drawImage(controls, 100, (height-controls.getHeight()/3)/2,
                    controls.getWidth()/3, controls.getHeight()/3, null);

        g.dispose();
        if (running)
            bs.show();
        if (controlsToggleCoolDown > 0) { //prevent multiple accidental toggles of help
            controlsToggleCoolDown--;
        }
    }

    // given an area checks if the player hovers it with the mouse
    private boolean isHovered(int x, int y, int xLen, int yLen) {
        return (InputHandler.mouseX > x && InputHandler.mouseX < x + xLen && InputHandler.mouseY > y && InputHandler.mouseY < y + yLen);
    }
}
