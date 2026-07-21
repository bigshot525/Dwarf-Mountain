package entity.object;
import entity.Item;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class OBJ_StoneBed extends Item {
    
    public OBJ_StoneBed() {
        try {
            BufferedImage original = ImageIO.read(getClass().getResourceAsStream("/res/objects/stoneBed.png"));
            
            // Scale image to match collision box (1 tile wide, 2 tiles tall)
            image = new BufferedImage(48, 96, BufferedImage.TYPE_INT_ARGB);
            java.awt.Graphics2D g2 = image.createGraphics();
            g2.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(original, 0, 0, 48, 96, null);
            g2.dispose();
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        // collision box (1,2)
        solidArea = new Rectangle(0, 0, 48, 96);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        //no bounce
        yOffset = 0;
        vy = 0;
        onGround = true;
        isInteractible = true;
        pickupDelay = Integer.MAX_VALUE;  // Can't be picked up
    }
}
