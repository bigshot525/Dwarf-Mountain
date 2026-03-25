package Main;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class KeyHandler implements KeyListener, MouseListener {

    public boolean attackPressed = false;
    public int attackCooldown = 0;
    public boolean upPressed, downPressed, leftPressed, rightPressed, invPressed
    , craftPressed;
    // reference to the game panel so we can toggle the central gamePaused flag
    GamePanel gp;

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        char keyChar = e.getKeyChar();

        // Route all keys to save screen if it's open
        if (gp.showSaveScreen) {
            gp.saveScreen.handleKey(code, keyChar);
            return;
        }

        if (code == KeyEvent.VK_W) upPressed = true;
        if (code == KeyEvent.VK_S) downPressed = true;
        if (code == KeyEvent.VK_A) leftPressed = true;
        if (code == KeyEvent.VK_D) rightPressed = true;
        if (code == KeyEvent.VK_E) invPressed = true;
        if (code == KeyEvent.VK_F) craftPressed = true;

        // F5 = save screen, F9 = load screen
        if (code == KeyEvent.VK_F5 && gp.gameState == GameState.PLAYING) {
            gp.saveScreen.open(SaveScreen.Mode.SAVE);
            gp.showSaveScreen = true;
            return;
        }
        if (code == KeyEvent.VK_F9) {
            gp.saveScreen.open(SaveScreen.Mode.LOAD);
            gp.showSaveScreen = true;
            return;
        }

        // ESC closes save screen if open, otherwise toggles pause
        if (code == KeyEvent.VK_ESCAPE && gp != null) {
            if (gp.currentTab == 0) {
                gp.currentTab = 3;
            } else {
                gp.currentTab = 0;
            }
            gp.repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W) upPressed = false;
        if (code == KeyEvent.VK_S) downPressed = false;
        if (code == KeyEvent.VK_A) leftPressed = false;
        if (code == KeyEvent.VK_D) rightPressed = false;
        if (code == KeyEvent.VK_E) invPressed = false;
        if (code == KeyEvent.VK_F) craftPressed = false;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (gp.showSaveScreen) {
            gp.saveScreen.handleClick(e.getX(), e.getY()); // route instead of block
            return;
        }

        if (gp.gameState == GameState.MAIN_MENU) {
            gp.mainMenu.handleClick(e.getX(), e.getY());
            return;
        }
        if (gp.currentTab > 0) {
            gp.ui.handleClick(e.getX(), e.getY());
            return;
        }
        if (e.getButton() == MouseEvent.BUTTON1) {
            attackPressed = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            attackPressed = false;
        }
    }

    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
