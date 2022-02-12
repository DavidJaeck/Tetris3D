package game;


import java.awt.event.*;
import java.util.Arrays;

// handles player inputs for the launcher
public class InputHandler implements KeyListener, FocusListener, MouseListener, MouseMotionListener {

    public boolean[] key = new boolean[68836];
    public static int mouseX;
    public static int mouseY;
    public static int mouseButton;

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseButton = e.getButton();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseButton = 0;
    }

    @Override
    public void focusGained(FocusEvent e) {

    }

    @Override
    public void focusLost(FocusEvent e) {
        Arrays.fill(key, false);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode > 0 && keyCode < key.length)
            key[keyCode] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode > 0 && keyCode < key.length)
            key[keyCode] = false;
    }
}
