package Main;

import java.awt.Color;
import java.awt.Graphics2D;
import entity.Player;

public class UI {
    GamePanel gp;

    public UI(GamePanel gp) {
        this.gp = gp;
    }

    public void drawInventory(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(50, 50, gp.screenWidth - 100, gp.screenHeight - 100);

        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(24f));
        g2.drawString("Inventory", 60, 80);
        g2.drawString("Stone: " + Player.stone, 60, 100);
        g2.drawString("Wood: " + Player.wood, 60, 120);
        g2.drawString("Iron: " + Player.iron, 60, 140);
        g2.drawString("Gold: ", 60, 160);


    }

        public void drawCrafting(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(50, 50, gp.screenWidth - 100, gp.screenHeight - 100);

        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(24f));
        
        //Crafting UI
        g2.drawString("Crafting coming soon!", 60, 80);



    }
}



