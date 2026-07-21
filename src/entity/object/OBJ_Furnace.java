package entity.object;

import entity.Item;
import java.awt.Rectangle;
import javax.imageio.ImageIO;

public class OBJ_Furnace extends Item {
    
    public OBJ_Furnace() {
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/res/objects/furnace.png"));
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        boolean placeable = true;
        // collision box
        solidArea = new Rectangle(0, 0, 48, 48);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        // Don't bounce - furnaces are stationary
        yOffset = 0;
        vy = 0;
        onGround = true;
        isInteractible = true;
        pickupDelay = Integer.MAX_VALUE;  // Can't be picked up
    }
}
