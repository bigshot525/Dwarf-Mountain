package UI;

import java.awt.*;

import Main.GamePanel;
import UI.SaveScreen.Mode;

public class MainMenu {

    GamePanel gp;
    private Rectangle playButton;
    private Rectangle loadButton;
    private Rectangle quitButton;
    private boolean hasSave;

    public MainMenu(GamePanel gp) {
        this.gp = gp;

        int btnW = 200;
        int btnH = 50;
        int centerX = gp.screenWidth / 2 - btnW / 2;

        playButton = new Rectangle(centerX, 220, btnW, btnH);
        loadButton = new Rectangle(centerX, 290, btnW, btnH);
        quitButton = new Rectangle(centerX, 360, btnW, btnH);
    }

    public void update() {
        hasSave = gp.saveManager.saveExists();
    }

    public void draw(Graphics2D g2) {
        // Background
        g2.setColor(new Color(20, 20, 20));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // Title
        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 60f));
        FontMetrics fm = g2.getFontMetrics();
        String title = "Underhill";
        int titleX = gp.screenWidth / 2 - fm.stringWidth(title) / 2;
        g2.drawString(title, titleX, 150);

        // Subtitle
        g2.setFont(g2.getFont().deriveFont(16f));
        g2.setColor(Color.GRAY);
        String sub = "A Mining Adventure";
        fm = g2.getFontMetrics();
        g2.drawString(sub, gp.screenWidth / 2 - fm.stringWidth(sub) / 2, 180);

        // Buttons
        drawButton(g2, playButton, "New Game", new Color(50, 120, 50));
        drawButton(g2, loadButton, hasSave ? "Load Game" : "No Save Found",
                   hasSave ? new Color(50, 80, 150) : new Color(60, 60, 60));
        drawButton(g2, quitButton, "Quit", new Color(140, 40, 40));

        // Version
        g2.setColor(Color.DARK_GRAY);
        g2.setFont(g2.getFont().deriveFont(12f));
        g2.drawString("v0.1", gp.screenWidth - 40, gp.screenHeight - 10);
    }

    private void drawButton(Graphics2D g2, Rectangle rect, String label, Color color) {
        g2.setColor(color);
        g2.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 12, 12);
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 12, 12);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 22f));
        FontMetrics fm = g2.getFontMetrics();
        int textX = rect.x + rect.width / 2 - fm.stringWidth(label) / 2;
        int textY = rect.y + rect.height / 2 + fm.getAscent() / 2 - 2;
        g2.drawString(label, textX, textY);
    }

    public void handleClick(int mouseX, int mouseY) {
        if (playButton.contains(mouseX, mouseY)) {
            gp.resetGame();
            gp.gameState = GameState.PLAYING;
            gp.saveScreen.open(SaveScreen.Mode.SAVE);
            gp.showSaveScreen = true; // prompt for name immediately
        }
        if (loadButton.contains(mouseX, mouseY) && hasSave) {
            gp.saveScreen.open(SaveScreen.Mode.LOAD);
            gp.showSaveScreen = true;
            gp.gameState = GameState.PLAYING;
        }
        if (quitButton.contains(mouseX, mouseY)) {
            System.exit(0);
        }
    }
}