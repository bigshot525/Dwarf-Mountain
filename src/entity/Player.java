package entity;

import Main.GamePanel;
import Main.KeyHandler;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Player extends Entity {

    GamePanel gp;
    KeyHandler keyH;

    public Rectangle attackArea = new Rectangle(0, 0, 32, 32);

    public final int screenX;
    public final int screenY;

    public int toolLevel = 1;

    public boolean canMove = true;

    //current hotbar slot (0-8)
    public int currentHotbarSlot = 0;

    //player stats
    public int maxHealth = 100;
    public int health = maxHealth;
    public int maxEnergy = 1000;
    public int energy = maxEnergy;
    
    //player money
    public int coins = 200;
    //items
    public static int stone = 0;
    public static int wood = 0;
    public static int iron = 0;
    public static int ironOre = 10;
    public static int gold = 0;
    public static int goldOre = 0;
    public static int coal = 100;

    //placeable items
    public static int furnace = 1;
    public static int stoneBed = 1;

    //tools
    public static boolean ironPickaxe = false;
    public static boolean goldPickaxe = false;

    //interactions with npcs
    public static boolean hasTalkedToBalin = false;

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;

        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        solidArea = new Rectangle(8, 16, 32, 32);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        setDefaultValues();
        getPlayerImage();
        getPlayerAttackImage();
    }

    public void setDefaultValues() {
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;
        speed = 4;
        direction = "down";
    }

    public void getPlayerImage() {
        try {
            up1 = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/player/boy_up_1.png"));
            up2 = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/player/boy_up_2.png"));
            down1 = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/player/boy_down_1.png"));
            down2 = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/player/boy_down_2.png"));
            left1 = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/player/boy_left_1.png"));
            left2 = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/player/boy_left_2.png"));
            right1 = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/player/boy_right_1.png"));
            right2 = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/player/boy_right_2.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getPlayerAttackImage() {
        try {
            attackUp1 = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/player/boy_attack_up_1.png"));
            attackUp2 = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/player/boy_attack_up_2.png"));
            attackDown1 = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/player/boy_attack_down_1.png"));
            attackDown2 = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/player/boy_attack_down_2.png"));
            attackLeft1 = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/player/boy_attack_left_1.png"));
            attackLeft2 = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/player/boy_attack_left_2.png"));
            attackRight1 = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/player/boy_attack_right_1.png"));
            attackRight2 = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/player/boy_attack_right_2.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {

        // -------------------------
        // INVENTORY LOCK
        // -------------------------
        if (gp.currentTab == 1) {
            if (keyH.invPressed) {
                System.out.println("Player paused");
                gp.currentTab = 0;
                keyH.invPressed = false;
            }
            return;
        }

        // -------------------------
        // CRAFTING UPDATE
        //--------------------------
        if (gp.currentTab == 2) {
            if (keyH.craftPressed) {
                System.out.println("Player paused");
                gp.currentTab = 0;
                keyH.craftPressed = false;
            }
            return;
        }
        if(gp.currentTab == 4){
            return; // prevent player movement when furnace UI is open
        }
        if(gp.currentTab == 6){
            return; // prevent player movement when passout screen is open
        }




        // -------------------------
        // ATTACK UPDATE
        // -------------------------
        if (attacking) {
            attackCounter++;

            if (attackCounter > 5 && attackCounter < 15) {
                spriteNum = 2;
            }

            if (attackCounter >= attackDuration) {
                attacking = false;
                attackCounter = 0;
                spriteNum = 1;
            }
            return;
        }

        // -------------------------
        // break tile
        // -------------------------
        if (keyH.attackPressed) {
            attacking = true;
            spriteCounter = 0;
            spriteNum = 1;
            performBreak();
            keyH.attackPressed = false;
            return;
        }
        // -------------------------
        // MOVEMENT
        // -------------------------
        boolean moving = false;

        if (keyH.upPressed) {
            direction = "up";
            moving = true;
        } else if (keyH.downPressed) {
            direction = "down";
            moving = true;
        } else if (keyH.leftPressed) {
            direction = "left";
            moving = true;
        } else if (keyH.rightPressed) {
            direction = "right";
            moving = true;
        }

        if (moving) {
            collisionOn = false;
            gp.cChecker.checkTile(this);
            gp.cChecker.checkObject(this, true);

            if (!collisionOn) {
                switch (direction) {
                    case "up": worldY -= speed; break;
                    case "down": worldY += speed; break;
                    case "left": worldX -= speed; break;
                    case "right": worldX += speed; break;
                }
            }

            spriteCounter++;
            if (spriteCounter > 15) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        } else {
            spriteNum = 1;
            spriteCounter = 0;
        }

        // -------------------------
        // INVENTORY TOGGLE
        // -------------------------
        if (keyH.invPressed) {
            gp.currentTab = 1;
            keyH.invPressed = false;
        }
        //Crafting toggle
        if (keyH.craftPressed) {
            gp.currentTab = 2;
            keyH.craftPressed = false;
        }



        pickUpItems();
    }

    public void draw(Graphics2D g2) {

        BufferedImage image = null;
        int drawX = screenX;
        int drawY = screenY;
        int drawWidth = gp.tileSize;
        int drawHeight = gp.tileSize;

        if (attacking) {
            switch (direction) {
                case "up":
                    image = (spriteNum == 1) ? attackUp1 : attackUp2;
                    drawY -= gp.tileSize;
                    drawHeight = gp.tileSize * 2;
                    break;
                case "down":
                    image = (spriteNum == 1) ? attackDown1 : attackDown2;
                    drawHeight = gp.tileSize * 2;
                    break;
                case "left":
                    image = (spriteNum == 1) ? attackLeft1 : attackLeft2;
                    drawX -= gp.tileSize;
                    drawWidth = gp.tileSize * 2;
                    break;
                case "right":
                    image = (spriteNum == 1) ? attackRight1 : attackRight2;
                    drawWidth = gp.tileSize * 2;
                    break;
            }
        } else {
            switch (direction) {
                case "up": image = (spriteNum == 1) ? up1 : up2; break;
                case "down": image = (spriteNum == 1) ? down1 : down2; break;
                case "left": image = (spriteNum == 1) ? left1 : left2; break;
                case "right": image = (spriteNum == 1) ? right1 : right2; break;
            }
        }

        g2.drawImage(image, drawX, drawY, drawWidth, drawHeight, null);
    }

    public void performBreak() {

        int attackX = worldX;
        int attackY = worldY;

        switch (direction) {
            case "up": attackY -= attackArea.height; break;
            case "down": attackY += attackArea.height; break;
            case "left": attackX -= attackArea.width; break;
            case "right": attackX += attackArea.width; break;
        }

        gp.breakTileFacing(direction);

        Rectangle attackBox = new Rectangle(
                attackX + solidArea.x,
                attackY + solidArea.y,
                attackArea.width,
                attackArea.height
        );
    }


    public void pickUpItems() {

        Rectangle playerArea = new Rectangle(
            worldX + solidArea.x,
            worldY + solidArea.y,
            solidArea.width,
            solidArea.height
        );

        for (int i = 0; i < gp.items.length; i++) {
            Item item = gp.items[i];
            if (item == null) continue;

            // Allow pickup regardless of bounce
            if (item.pickupDelay > 0) continue;

            // Include yOffset in the item's rectangle
            Rectangle itemArea = new Rectangle(
                item.worldX,
                item.worldY + (int)item.yOffset, // <-- add bounce offset here
                item.solidArea.width,
                item.solidArea.height
            );

            if (playerArea.intersects(itemArea)) {
                gp.items[i] = null;
                break;
            }
        }
    }
}
