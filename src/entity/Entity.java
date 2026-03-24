package entity;


import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Entity {

    public int worldX, worldY;
    public int speed;

    // ---- MOVEMENT IMAGES ----
    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;

    // ---- ATTACK IMAGES ----
    public BufferedImage attackUp1, attackUp2, attackDown1, attackDown2,
            attackLeft1, attackLeft2, attackRight1, attackRight2;

    public String direction;

    // ---- Animation ----
    public int spriteCounter = 0;
    public int spriteNum = 1;

    // ---- Collision ----
    public Rectangle solidArea;
    public boolean collisionOn = false;

    // ---- Attacking ----
    public boolean attacking = false;
    public Rectangle attackArea = new Rectangle(0,0,0,0);
    public int attackCounter = 0;
    public int attackDuration = 20;  // frames
    public int attackCooldown = 25;  // frames
}
