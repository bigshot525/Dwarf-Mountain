package entity;

import Main.GamePanel;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import Main.Time;

public class Balin extends Entity {

    GamePanel gp;

    BufferedImage image = null;

    boolean visible = true;

    boolean goingOutside = false;
    boolean goingToRegister = false;

    // register = standing at shop
    // inside = walking inside house
    // outside = outside mountain
    String currentPosition = "register";


    public Balin(GamePanel gp) {
        this.gp = gp;

        setDefaultValues();
        getBalinImage();

        solidArea = new Rectangle(0, 0, 48, 48);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }


    public void setDefaultValues() {

        worldX = gp.tileSize * 3;
        worldY = gp.tileSize * 18;

        speed = 2;
        direction = "down";
    }


    public void getBalinImage() {

        try {
            up1 = javax.imageio.ImageIO.read(
                    getClass().getResourceAsStream("/res/player/boy_up_1.png"));

            up2 = javax.imageio.ImageIO.read(
                    getClass().getResourceAsStream("/res/player/boy_up_2.png"));

            down1 = javax.imageio.ImageIO.read(
                    getClass().getResourceAsStream("/res/player/boy_down_1.png"));

            down2 = javax.imageio.ImageIO.read(
                    getClass().getResourceAsStream("/res/player/boy_down_2.png"));

            left1 = javax.imageio.ImageIO.read(
                    getClass().getResourceAsStream("/res/player/boy_left_1.png"));

            left2 = javax.imageio.ImageIO.read(
                    getClass().getResourceAsStream("/res/player/boy_left_2.png"));

            right1 = javax.imageio.ImageIO.read(
                    getClass().getResourceAsStream("/res/player/boy_right_1.png"));

            right2 = javax.imageio.ImageIO.read(
                    getClass().getResourceAsStream("/res/player/boy_right_2.png"));
        } catch(Exception e){
            e.printStackTrace();
        }
    }


    public void update(){

        Time t = gp.t;
        // Morning - return to register
        if(t.hour >= 5 && t.hour < 6){
            if(!currentPosition.equals("register")){
                moveToRegister();
            }
        }
        // Work outside
        else if(t.hour >= 6 && t.hour < 20){
            if(!currentPosition.equals("outside")){
                moveToOutside();
            }
        }
        // Night - return inside
        else{
            if(!currentPosition.equals("register")){
                moveToRegister();
            }
        }
    }

public void moveToOutside() {

    // Phase 1: Walk to the house door
    if (!goingOutside) {

        int targetX = gp.tileSize * 7;
        int targetY = gp.tileSize * 29;

        // Move X first
        if (worldX != targetX) {

            if (worldX < targetX) {
                worldX += speed;
                if (worldX > targetX) worldX = targetX;
                direction = "right";
            } else {
                worldX -= speed;
                if (worldX < targetX) worldX = targetX;
                direction = "left";
            }

        }
        // Then move Y
        else if (worldY != targetY) {

            if (worldY < targetY) {
                worldY += speed;
                if (worldY > targetY) worldY = targetY;
                direction = "down";
            } else {
                worldY -= speed;
                if (worldY < targetY) worldY = targetY;
                direction = "up";
            }

        }

        // Reached the house door
        if (worldX == targetX && worldY == targetY) {

            goingOutside = true;

            // Teleport to mountain entrance
            worldX = gp.tileSize * 210;
            worldY = gp.tileSize * 73;
            return; // Wait until the next update before walking outside
        }
    }

    // Phase 2: Walk 3 tiles to the right
    else {

        int targetX = gp.tileSize * 213;
        int targetY = gp.tileSize * 73;

        if (worldX != targetX) {

            if (worldX < targetX) {
                worldX += speed;
                if (worldX > targetX) worldX = targetX;
                direction = "right";
            } else {
                worldX -= speed;
                if (worldX < targetX) worldX = targetX;
                direction = "left";
            }

        }
        else if (worldY != targetY) {

            if (worldY < targetY) {
                worldY += speed;
                if (worldY > targetY) worldY = targetY;
                direction = "down";
            } else {
                worldY -= speed;
                if (worldY < targetY) worldY = targetY;
                direction = "up";
            }

        }
        else {

            currentPosition = "outside";
            direction = "down";
            System.out.println("Balin is now outside.");
        }
    }
}



    public void moveToRegister(){
        //move to door and tp him to inside coords
        int targetX = gp.tileSize * 210;
        int targetY = gp.tileSize * 73;

        //MOVE TO door and tp to inside coords
        if(!goingToRegister){
            //move x first
            if(worldX != targetX){

                if(worldX < targetX){
                    worldX += speed;
                    direction = "right";
                }
                else{
                    worldX -= speed;
                    direction = "left";
                }

            }
            //teleport him inside when he reaches the door
            if(worldX == targetX && worldY == targetY){
                currentPosition = "inside";
                goingToRegister = true;
                goingOutside = false;
                direction = " up";
                worldX = gp.tileSize * 7;
                worldY = gp.tileSize * 29;
            }
        }
        //move from door to register
        else{
            targetX = gp.tileSize * 3;
            targetY = gp.tileSize * 18;

            if(worldY != targetY){
                if(worldY < targetY){
                    worldY += speed;
                    direction = "down";
                }
                else{
                    worldY -= speed;
                    direction = "up";
                }
            }
            else if(worldX != targetX){

                if(worldX < targetX){
                    worldX += speed;
                    direction = "right";
                }
                else{
                    worldX -= speed;
                    direction = "left";
                }
            }
            if(worldX == targetX && worldY == targetY){
                goingToRegister = false;
                direction = "down";
                currentPosition = "register";
                System.out.println("Balin is now at the register.");
            }
        }
    }

    public void draw(Graphics2D g2){
        if(!visible) return;

        // House
        if(gp.mapManager.currentMap.equals("/res/maps/house1.txt")
            && (currentPosition.equals("register")
            || currentPosition.equals("inside"))){

            drawImage(g2);
        }

        // Mountain
        if (gp.mapManager.currentMap.equals("/res/maps/mountain01.txt")
                && goingOutside) {

            drawImage(g2);
        }
    }

    private void drawImage(Graphics2D g2){

        switch(direction){

            case "up":
                image = up1;
                break;

            case "down":
                image = down1;
                break;

            case "left":
                image = left1;
                break;

            case "right":
                image = right1;
                break;
        }
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;
        g2.drawImage(
                image,
                screenX,
                screenY,
                gp.tileSize,
                gp.tileSize,
                null
        );
    }

        
}