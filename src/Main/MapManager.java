package Main;

public class MapManager {
    GamePanel gp;

    public String currentMap = "/res/maps/mountain01.txt";
    public int returnX, returnY;

    public MapManager(GamePanel gp) {
        this.gp = gp;
    }

    public void changeMap(String newMap, int spawnCol, int spawnRow) {
        // Save the outgoing map's items so they aren't lost, then load the incoming map's own items
        gp.saveCurrentMapItems(currentMap);

        gp.tileM.loadMap(newMap);
        gp.tileM.initTileHP(newMap);

        gp.loadMapItems(newMap);

        gp.player.worldX = spawnCol * gp.tileSize;
        gp.player.worldY = spawnRow * gp.tileSize;
        currentMap = newMap;
    }
}