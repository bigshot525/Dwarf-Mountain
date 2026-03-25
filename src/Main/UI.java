package Main;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import entity.Player;
import java.awt.Rectangle;

public class UI {
    GamePanel gp;
    private Rectangle[] tabRects = new Rectangle[3];
    private Rectangle exitButton;

    public UI(GamePanel gp) {
        this.gp = gp;
    }

    // Single entry point called from GamePanel
    public void draw(Graphics2D g2) {
        switch (gp.currentTab) {
            case 1 -> drawInventory(g2);
            case 2 -> drawCrafting(g2);
            case 3 -> drawPauseMenu(g2);
        }
    }

    private void drawBackground(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(50, 50, gp.screenWidth - 100, gp.screenHeight - 100);
    }

    private void drawTabs(Graphics2D g2) {
        String[] tabs = {"Inventory", "Crafting", "Exit"};
        for (int i = 0; i < tabs.length; i++) {
            tabRects[i] = new Rectangle(60 + i * 90, 55, 80, 30);

            if (gp.currentTab == i + 1) {
                g2.setColor(new Color(80, 80, 80, 220));
            } else {
                g2.setColor(new Color(40, 40, 40, 220));
            }
            g2.fillRect(tabRects[i].x, tabRects[i].y, tabRects[i].width, tabRects[i].height);
            g2.setColor(Color.WHITE);
            g2.drawRect(tabRects[i].x, tabRects[i].y, tabRects[i].width, tabRects[i].height);

            // Center the label inside each tab
            g2.setFont(g2.getFont().deriveFont(14f));
            FontMetrics fm = g2.getFontMetrics();
            int labelX = tabRects[i].x + tabRects[i].width / 2 - fm.stringWidth(tabs[i]) / 2;
            g2.drawString(tabs[i], labelX, 75);
        }
    }

    private void drawInventory(Graphics2D g2) {
        drawBackground(g2);
        drawTabs(g2);

        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(24f));
        g2.drawString("Stone: " + Player.stone, 60, 130);
        g2.drawString("Wood: "  + Player.wood,  60, 160);
        g2.drawString("Iron: "  + Player.iron,  60, 190);
        g2.drawString("Gold: ",  60, 220);
    }

    private void drawCrafting(Graphics2D g2) {
        drawBackground(g2);
        drawTabs(g2);

        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(24f));
        g2.drawString("Crafting coming soon!", 60, 130);
    }

    private void drawPauseMenu(Graphics2D g2) {
        drawBackground(g2);
        drawTabs(g2);

        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 32f));
        g2.drawString("Game Paused", 60, 150);
        g2.setFont(g2.getFont().deriveFont(18f));
        g2.drawString("Press ESC to continue", 60, 185);

        // Exit to main menu button
        exitButton = new Rectangle(gp.screenWidth / 2 - 100, 280, 200, 50);
        g2.setColor(new Color(140, 40, 40));
        g2.fillRoundRect(exitButton.x, exitButton.y, exitButton.width, exitButton.height, 12, 12);
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(exitButton.x, exitButton.y, exitButton.width, exitButton.height, 12, 12);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 20f));
        FontMetrics fm = g2.getFontMetrics();
        String label = "Exit to Main Menu";
        g2.drawString(label, exitButton.x + exitButton.width / 2 - fm.stringWidth(label) / 2, exitButton.y + 33);
    }

    public void handleClick(int mouseX, int mouseY) {
        // Check exit button inside pause menu
        if (gp.currentTab == 3 && exitButton != null && exitButton.contains(mouseX, mouseY)) {
            gp.saveManager.save(); // save before leaving
            gp.gameState = GameState.MAIN_MENU;
            gp.currentTab = 0;
            return;
        }

        // Check tabs
        if (tabRects == null) return;
        for (int i = 0; i < tabRects.length; i++) {
            if (tabRects[i] != null && tabRects[i].contains(mouseX, mouseY)) {
                gp.currentTab = i + 1; // 1=inventory, 2=crafting, 3=pause/exit screen
            }
        }
    }
}