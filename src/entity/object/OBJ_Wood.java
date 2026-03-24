package entity.object;

import entity.Item;

import java.awt.Rectangle;

import javax.imageio.ImageIO;

public class OBJ_Wood extends Item {

    public OBJ_Wood() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/res/objects/wood.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        //collision box
        solidArea = new Rectangle(0, 0, 18, 18);
        // Bounce properties
        yOffset = 0;
        vy = -6;            // initial upward bounce
        gravity = 0.6;
        bounceDamping = 0.5;
        onGround = false;

        pickupDelay = 0;

    }
}
