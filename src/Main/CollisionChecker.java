package Main;

import entity.Entity;

public class CollisionChecker {

	GamePanel gp;

	public CollisionChecker(GamePanel gp) {
		this.gp = gp;
	}
	
	public void checkTile(Entity entity) {
	    
	    int entityLeftWorldX = entity.worldX + entity.solidArea.x;
	    int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
	    int entityTopWorldY = entity.worldY + entity.solidArea.y;
	    int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

	    int entityLeftCol = entityLeftWorldX / gp.tileSize;
	    int entityRightCol = entityRightWorldX / gp.tileSize;
	    int entityTopRow = entityTopWorldY / gp.tileSize;
	    int entityBottomRow = entityBottomWorldY / gp.tileSize;

	    int tileNum1, tileNum2;

	    switch(entity.direction) {

	    case "up":
	        entityTopRow = (entityTopWorldY - entity.speed) / gp.tileSize;

	        tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
	        tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];

	        if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
	            entity.collisionOn = true;
	        }
	        break;

	    case "down":
	        entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;

	        tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
	        tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];

	        if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
	            entity.collisionOn = true;
	        }
	        break;

	    case "left":
	        entityLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;

	        tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
	        tileNum2 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];

	        if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
	            entity.collisionOn = true;
	        }
	        break;

	    case "right":
	        entityRightCol = (entityRightWorldX + entity.speed) / gp.tileSize;

	        tileNum1 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
	        tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];

	        if (gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision) {
	            entity.collisionOn = true;
	        }
	        break;
	    }
	}
	
	public void checkObject(Entity entity, boolean player) {
		
		if (player == false) return;
		
		for (int i = 0; i < gp.items.length; i++) {
			if (gp.items[i] != null && gp.items[i].pickupDelay >= Integer.MAX_VALUE / 2) {
				// Only check collision for non-collectible objects (like furnaces)
				// Get entity's solid area position
				entity.solidArea.x = entity.worldX + entity.solidArea.x;
				entity.solidArea.y = entity.worldY + entity.solidArea.y;
				
				// Get the object's solid area position
				gp.items[i].solidArea.x = gp.items[i].worldX + gp.items[i].solidArea.x;
				gp.items[i].solidArea.y = gp.items[i].worldY + gp.items[i].solidArea.y;
				
				switch(entity.direction) {
				case "up":
					entity.solidArea.y -= entity.speed;
					if (entity.solidArea.intersects(gp.items[i].solidArea)) {
						entity.collisionOn = true;
					}
					break;
				case "down":
					entity.solidArea.y += entity.speed;
					if (entity.solidArea.intersects(gp.items[i].solidArea)) {
						entity.collisionOn = true;
					}
					break;
				case "left":
					entity.solidArea.x -= entity.speed;
					if (entity.solidArea.intersects(gp.items[i].solidArea)) {
						entity.collisionOn = true;
					}
					break;
				case "right":
					entity.solidArea.x += entity.speed;
					if (entity.solidArea.intersects(gp.items[i].solidArea)) {
						entity.collisionOn = true;
					}
					break;
				}
				
				entity.solidArea.x = entity.solidAreaDefaultX;
				entity.solidArea.y = entity.solidAreaDefaultY;
				gp.items[i].solidArea.x = gp.items[i].solidAreaDefaultX;
				gp.items[i].solidArea.y = gp.items[i].solidAreaDefaultY;
			}
		}
	}
}
