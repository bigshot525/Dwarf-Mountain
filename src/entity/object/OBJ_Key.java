package entity.object;

public class OBJ_Key extends superObject{

	public OBJ_Key() {
		
		name = "stone pile";
		
		try {
            image = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/object/stone_pile.png"));

		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}
}
