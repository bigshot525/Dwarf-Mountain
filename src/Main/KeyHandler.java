package Main;

import UI.GameState;
import UI.SaveScreen;
import entity.Player;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class KeyHandler implements KeyListener, MouseListener, MouseMotionListener {

    public boolean attackPressed = false;
    public int attackCooldown = 0;
    public boolean upPressed, downPressed, leftPressed, rightPressed, invPressed
    , craftPressed;
    // reference to the game panel so we can toggle the central gamePaused flag
    GamePanel gp;

    public boolean interactPressed;


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
        if (code == KeyEvent.VK_E){
            if(gp.currentTab == 0){
                gp.currentTab = 1;
            }else if(gp.currentTab == 8){
                Player.hasTalkedToBalin = true;
                gp.currentTab = 0;
            }
            else{
                gp.currentTab = 0;
            }
        }
        if (code == KeyEvent.VK_F){
            craftPressed = true;
        }

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

        // ESC closes save screen if open, cancels placement mode, or toggles pause
        if (code == KeyEvent.VK_ESCAPE && gp != null) {
            if (gp.placementMode) {
                gp.cancelPlacement();
                return;
            }
            else if(gp.currentTab == 6){
                return;
            }
            
            else if (gp.currentTab == 4) { //check if inside furnace ui, if true returns to gameplay tab (0)
                gp.currentTab = 0; 
            }
            else if(gp.currentTab == 8){//check if inside balin ui, if yes set hasTalkedToBalin to true
                Player.hasTalkedToBalin = true;
                gp.currentTab = 0;
            }
            else if (gp.currentTab == 0) {
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

        if (gp.placementMode) {
            gp.handlePlacementClick(e.getX(), e.getY());
            return;
        }

        // Handle placement mode clicks
        if (gp.currentTab == 1) {
            gp.ui.handleClick(e.getX(), e.getY());       // Check if a tab was clicked
            if (gp.currentTab == 1) {                    // Only handle inventory if we didn't switch tabs
                gp.ui.handleInventoryPress(e.getX(), e.getY());
            }
            return;
        }

        if (gp.currentTab == 1) {
            gp.ui.handleInventoryPress(e.getX(), e.getY());
            return;
        }
        if (gp.currentTab > 0) {
            gp.ui.handleClick(e.getX(), e.getY());
            return;
        }

        if (e.getButton() == MouseEvent.BUTTON1) {
            attackPressed = true;
        }
        if(e.getButton() == MouseEvent.BUTTON3){
            interactPressed = true;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        gp.updateMousePosition(e.getX(), e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        if (gp.currentTab == 1) {
            gp.ui.handleInventoryRelease(e.getX(), e.getY());
            return;
        }

        if (e.getButton() == MouseEvent.BUTTON1) {
            attackPressed = false;
        }

        if (e.getButton() == MouseEvent.BUTTON3) {
            interactPressed = false;
        }
    }

    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {
        gp.updateMousePosition(e.getX(), e.getY());

        if (gp.currentTab == 1) {
            gp.ui.handleInventoryDrag(e.getX(), e.getY());
        }
    }
}
