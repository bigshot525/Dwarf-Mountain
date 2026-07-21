package entity;

import Main.GamePanel;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Item extends Entity {

    public int[] newFurnaceCoords = new int[2]; // x, y coordinates for furnace placement
    public int worldX, worldY;
    public BufferedImage image;
    public Rectangle solidArea = new Rectangle(0, 0, 18, 18); 
    public boolean pickedUp = false;
    public int pickupDelay = 20;   // frames before pickup allowed
    public boolean canBePickedUp() {
        return pickupDelay <= 0 && onGround;
    }

    public int drawWidth = 48;
    public int drawHeight = 48;

    //item bounce
    public double yOffset = 0;
    public double vy = -6;        // initial upward bounce
    public double gravity = 0.6;
    public double bounceDamping = 0.5;
    public boolean onGround = false;
    public boolean isInteractible;

    public void update() {
        if (pickupDelay > 0) {
            pickupDelay--;
        }

        if (!onGround) {
            vy += gravity;
            yOffset += vy;

            // Hit the ground
            if (yOffset >= 0) {
                yOffset = 0;
                vy = -vy * bounceDamping;

                // Stop bouncing when small
                if (Math.abs(vy) < 1) {
                    vy = 0;
                    onGround = true;
                }
            }
        }
    }

    public void draw(Graphics2D g2, GamePanel gp) {
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        g2.drawImage(
            image,
            screenX,
            screenY + (int) yOffset,
            gp.tileSize,
            gp.tileSize,
            null
        );
    }

    //draw items like furnaces, chests, beds
    public void drawStatic(Graphics2D g2, GamePanel gp) {
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if(image == null) return; // safety check
        //check if drawing a stone bed (which is 1x2 tiles) and adjust draw size accordingly
        if(image.getWidth() == 48 && image.getHeight() == 96) {
            g2.drawImage(
                image,
                screenX,
                screenY + (int) yOffset,
                gp.tileSize,
                gp.tileSize * 2,
                null
            );
        } else {
            g2.drawImage(
                image,
                screenX,
                screenY + (int) yOffset,
                gp.tileSize,
                gp.tileSize,
                null
            );
        }

    }
}
