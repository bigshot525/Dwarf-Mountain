package UI;

import java.awt.*;
import java.awt.event.KeyEvent;

import Main.GamePanel;

public class SaveScreen {

    public enum Mode { LOAD, SAVE }

    GamePanel gp;
    public Mode mode;

    private Rectangle confirmButton;


    private String[] saves;
    private int selectedIndex = 0;
    private String typingName = "";
    private boolean typing = false;
    private String message = "";
    private int messageTimer = 0;

    private final int SLOT_X = 100;
    private final int SLOT_Y = 120;
    private final int SLOT_W = 560;
    private final int SLOT_H = 55;
    private final int SLOT_GAP = 10;
    private final int MAX_VISIBLE = 5;

    public SaveScreen(GamePanel gp) {
        this.gp = gp;
    }

    public void open(Mode mode) {
        this.mode = mode;
        this.selectedIndex = 0;
        this.message = "";
        refreshSaves();

        // If opening save screen with no saves, jump straight to typing
        if (mode == Mode.SAVE && saves.length == 0) {
            typing = true;
            typingName = "";
        } else {
            typing = false;
            typingName = "";
        }
    }

    private void refreshSaves() {
        saves = gp.saveManager.getSaveNames();
    }

    public void draw(Graphics2D g2) {
        // Background
        g2.setColor(new Color(0, 0, 0, 210));
        g2.fillRect(50, 50, gp.screenWidth - 100, gp.screenHeight - 100);
        g2.setColor(Color.WHITE);
        g2.drawRect(50, 50, gp.screenWidth - 100, gp.screenHeight - 100);

        // Title
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 28f));
        String title = mode == Mode.SAVE ? "Save Game" : "Load Game";
        g2.drawString(title, 70, 95);

        // Save slots
        for (int i = 0; i < Math.min(saves.length, MAX_VISIBLE); i++) {
            int slotY = SLOT_Y + i * (SLOT_H + SLOT_GAP);

            // Highlight selected
            if (i == selectedIndex) {
                g2.setColor(new Color(80, 80, 160, 200));
            } else {
                g2.setColor(new Color(40, 40, 40, 200));
            }
            g2.fillRoundRect(SLOT_X, slotY, SLOT_W, SLOT_H, 10, 10);
            g2.setColor(Color.WHITE);
            g2.drawRoundRect(SLOT_X, slotY, SLOT_W, SLOT_H, 10, 10);

            // Save name
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 18f));
            g2.drawString(saves[i], SLOT_X + 15, slotY + 22);

            // Timestamp
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 13f));
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawString(gp.saveManager.getTimestamp(saves[i]), SLOT_X + 15, slotY + 42);
            g2.setColor(Color.WHITE);

            // Delete hint
            g2.setFont(g2.getFont().deriveFont(12f));
            g2.setColor(new Color(200, 80, 80));
            g2.drawString("[DEL] Delete", SLOT_X + SLOT_W - 100, slotY + 35);
            g2.setColor(Color.WHITE);
        }

        // New save button (only in SAVE mode)
        if (mode == Mode.SAVE) {
            int newY = SLOT_Y + Math.min(saves.length, MAX_VISIBLE) * (SLOT_H + SLOT_GAP);
            g2.setColor(new Color(50, 120, 50, 200));
            g2.fillRoundRect(SLOT_X, newY, SLOT_W, SLOT_H, 10, 10);
            g2.setColor(Color.WHITE);
            g2.drawRoundRect(SLOT_X, newY, SLOT_W, SLOT_H, 10, 10);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 18f));
            g2.drawString("+ New Save", SLOT_X + 15, newY + 33);
        }

        // Typing box
        if (typing) {
            g2.setColor(new Color(20, 20, 60, 230));
            g2.fillRoundRect(150, 350, 460, 80, 10, 10);
            g2.setColor(Color.WHITE);
            g2.drawRoundRect(150, 350, 460, 80, 10, 10);
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 14f));
            g2.drawString("Enter save name:", 165, 372);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20f));
            g2.drawString(typingName + "|", 165, 398);

            // Confirm button
            confirmButton = new Rectangle(430, 358, 160, 35);
            g2.setColor(new Color(50, 120, 50));
            g2.fillRoundRect(confirmButton.x, confirmButton.y, confirmButton.width, confirmButton.height, 8, 8);
            g2.setColor(Color.WHITE);
            g2.drawRoundRect(confirmButton.x, confirmButton.y, confirmButton.width, confirmButton.height, 8, 8);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 16f));
            FontMetrics fm = g2.getFontMetrics();
            String label = "Create Save";
            g2.drawString(label, confirmButton.x + confirmButton.width / 2 - fm.stringWidth(label) / 2, confirmButton.y + 23);
        }

        // Message (feedback)
        if (messageTimer > 0) {
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 16f));
            g2.setColor(Color.YELLOW);
            g2.drawString(message, 70, gp.screenHeight - 70);
            messageTimer--;
        }

        // Controls hint
        g2.setFont(g2.getFont().deriveFont(12f));
        g2.setColor(Color.GRAY);
        g2.drawString("ESC: Back   ENTER: Select", 70, gp.screenHeight - 55);
    }

    public void handleKey(int code, char keyChar) {
        if (typing) {
            if (code == KeyEvent.VK_ENTER && !typingName.trim().isEmpty()) {
                gp.saveManager.saveToSlot(typingName.trim());
                showMessage("Saved as: " + typingName.trim());
                typing = false;
                typingName = "";
                refreshSaves();
                // If this was the first save, close the screen and start playing
                if (saves.length == 1) {
                    gp.showSaveScreen = false;
                }
            } else if (code == KeyEvent.VK_ESCAPE) {
                // Only allow escaping the typing box if there are existing saves to fall back to
                if (saves.length > 0) {
                    typing = false;
                    typingName = "";
                }
                // else: force them to enter a name
            } else if (code == KeyEvent.VK_BACK_SPACE && !typingName.isEmpty()) {
                typingName = typingName.substring(0, typingName.length() - 1);
            } else if (Character.isLetterOrDigit(keyChar) || keyChar == '_' || keyChar == ' ') {
                if (typingName.length() < 20) typingName += keyChar;
            }
            return;
        }
        if (code == KeyEvent.VK_ENTER) {
            if (mode == Mode.LOAD && saves.length > 0) {
                gp.saveManager.loadFromSlot(saves[selectedIndex]);
                gp.gameState = GameState.PLAYING;
                gp.showSaveScreen = false;
                showMessage("Loaded: " + saves[selectedIndex]);
            } else if (mode == Mode.SAVE) {
                if (selectedIndex < saves.length) {
                    // Overwrite existing
                    gp.saveManager.saveToSlot(saves[selectedIndex]);
                    showMessage("Overwritten: " + saves[selectedIndex]);
                    refreshSaves();
                } else {
                    // New save
                    typing = true;
                }
            }
        }

        if (code == KeyEvent.VK_DELETE && saves.length > 0 && selectedIndex < saves.length) {
            String deleted = saves[selectedIndex];
            gp.saveManager.deleteSave(deleted);
            showMessage("Deleted: " + deleted);
            refreshSaves();
            if (selectedIndex >= saves.length) selectedIndex = Math.max(0, saves.length - 1);
        }

        if (code == KeyEvent.VK_UP && selectedIndex > 0) selectedIndex--;
        if (code == KeyEvent.VK_DOWN) {
            int max = mode == Mode.SAVE ? saves.length : saves.length - 1;
            if (selectedIndex < max) selectedIndex++;
        }
    }

    public void handleClick(int mouseX, int mouseY) {
        if (typing && confirmButton != null && confirmButton.contains(mouseX, mouseY)) {
            if (!typingName.trim().isEmpty()) {
                gp.saveManager.saveToSlot(typingName.trim());
                showMessage("Saved as: " + typingName.trim());
                typing = false;
                typingName = "";
                refreshSaves();
                gp.showSaveScreen = false; // always close after confirming
            }
            return;
        }

        // Slot clicks
        for (int i = 0; i < Math.min(saves.length, MAX_VISIBLE); i++) {
            int slotY = SLOT_Y + i * (SLOT_H + SLOT_GAP);
            Rectangle slot = new Rectangle(SLOT_X, slotY, SLOT_W, SLOT_H);
            if (slot.contains(mouseX, mouseY)) {
                if (mode == Mode.LOAD) {
                    gp.saveManager.loadFromSlot(saves[i]);
                    gp.gameState = GameState.PLAYING;
                    gp.showSaveScreen = false;
                } else {
                    selectedIndex = i;
                    gp.saveManager.saveToSlot(saves[i]);
                    showMessage("Overwritten: " + saves[i]);
                    refreshSaves();
                    gp.showSaveScreen = false; // close after overwriting too
                }
                return;
            }
        }

        // New save button
        if (mode == Mode.SAVE) {
            int newY = SLOT_Y + Math.min(saves.length, MAX_VISIBLE) * (SLOT_H + SLOT_GAP);
            Rectangle newSlot = new Rectangle(SLOT_X, newY, SLOT_W, SLOT_H);
            if (newSlot.contains(mouseX, mouseY)) {
                typing = true;
                typingName = "";
            }
        }
    }


    private void showMessage(String msg) {
        message = msg;
        messageTimer = 120;
    }
}