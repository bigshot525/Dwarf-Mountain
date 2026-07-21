package entity.object;

import java.awt.Rectangle;
import javax.imageio.ImageIO;

import entity.Item;

public class OBJ_Coal extends Item {

    public OBJ_Coal() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/res/objects/coal.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        //collision box
        solidArea = new Rectangle(0, 0, 18, 18);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        // Bounce properties
        yOffset = 0;
        vy = -6;      
        gravity = 0.6;
        bounceDamping = 0.5;
        onGround = false;

        pickupDelay = 0;

    }
}