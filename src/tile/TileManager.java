package tile;

import Main.GamePanel;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;

public class TileManager {



    GamePanel gp;
    public Tile[] tile;
    public int mapTileNum[][];
    public int[][] tileHP;

    // Tile ID used for "empty"/unused space (the black tile defined in getTileImage()).
    // Used to clear the grid before loading a map, and as the fallback for any cell a
    // map file doesn't explicitly define, so leftover tiles from a previous map never
    // show through around the edges of a smaller map.
    public static final int VOID_TILE = 12;



    public TileManager(GamePanel gp) {
        this.gp = gp;
        tile = new Tile[20];
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
        tileHP = new int[gp.maxWorldCol][gp.maxWorldRow];
        getTileImage();
        loadMap("/res/maps/mountain01.txt");
        initTileHP("/res/maps/mountain01.txt");
    }

    public void getTileImage(){

        try{

            tile[0] = new Tile();
            tile[0].image = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/tile/grass.png"));
            tile[0].layer = 0;

            tile[1] = new Tile();
            tile[1].image = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/tile/stone.png"));
            tile[1].collision = true;
            tile[1].breakable = true;
            tile[1].layer = 0;


            tile[2] = new Tile();
            tile[2].image = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/tile/water.png"));
            tile[2].collision = true;
            tile[2].layer = 0;

            tile[3] = new Tile();
            tile[3].image = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/tile/earth.png"));
            tile[3].layer = 0;

            tile[4] = new Tile();
            tile[4].image = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/tile/tree.png"));
            tile[4].collision = true;
            tile[4].breakable = true;
            tile[4].layer = 1;

            tile[5] = new Tile();
            tile[5].image = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/tile/sand.png"));
            tile[5].layer = 0;

            tile[6] = new Tile();
            tile[6].image = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/tile/trunk.png"));
            tile[6].collision = true;
            tile[6].layer = 1;

            tile[7] = new Tile();
            tile[7].image = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/objects/wood.png"));
            tile[7].collision = false;
            tile[7].layer = 5;


            tile[8] = new Tile();
            tile[8].image = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/tile/stone.png"));
            tile[8].collision = true;
            tile[8].breakable = false;
            tile[8].layer = 0;


            tile[9] = new Tile();
            tile[9].image = ImageIO.read(getClass().getResourceAsStream("/res/tile/plank.png"));
            tile[9].layer = 0;

            tile[10] = new Tile();
            tile[10].image = ImageIO.read(getClass().getResourceAsStream("/res/tile/wall.png"));
            tile[10].collision = true;
            tile[10].layer = 0;

            tile[11] = new Tile();
            tile[11].image = ImageIO.read(getClass().getResourceAsStream("/res/tile/door.png"));
            tile[11].layer = 0;
            // Allow player to walk through doors to enter/exit
            tile[11].collision = true;

            tile[12] = new Tile();
            java.awt.image.BufferedImage blackImg = new java.awt.image.BufferedImage(gp.tileSize, gp.tileSize, java.awt.image.BufferedImage.TYPE_INT_ARGB);
            java.awt.Graphics g = blackImg.getGraphics();
            g.setColor(java.awt.Color.BLACK);
            g.fillRect(0, 0, gp.tileSize, gp.tileSize);
            g.dispose();
            tile[12].image = blackImg;
            tile[12].collision = true;
            tile[12].layer = 0;

            try {
            tile[13] = new Tile();
            tile[13].image = ImageIO.read(getClass().getResourceAsStream("/res/maps/store1.png"));

            tile[13].layer = 0;
            tile[13].width = 11;
            tile[13].height = 9;
            } catch (Exception e) {
                e.printStackTrace();
            }

            tile[14] = new Tile();
            tile[14].image = ImageIO.read(getClass().getResourceAsStream("/res/tile/ironOre.png"));
            tile[14].collision = true;
            tile[14].breakable = true;
            tile[14].maxHP = 5;
            tile[14].layer = 0;

            try {
                tile[15] = new Tile();
                tile[15].image = ImageIO.read(getClass().getResourceAsStream("/res/maps/store1Interior.png"));
                tile[15].layer = 0;
                tile[15].width = 30;
                tile[15].height = 30;
            } catch (Exception e) {
                e.printStackTrace();

            }   


        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void loadMap(String filePath){
        try{
            // Clear the whole grid to the void tile first so no leftover tiles from the
            // previous map (e.g. mountain terrain) remain outside the new map's bounds.
            for (int col = 0; col < gp.maxWorldCol; col++) {
                java.util.Arrays.fill(mapTileNum[col], VOID_TILE);
            }

            java.io.InputStream is = getClass().getResourceAsStream(filePath);
            if (is == null) {
                System.err.println("[TileManager] Map file not found: " + filePath);
                return;
            }
            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(is));

            int col = 0;
            int row = 0;

            while (row < gp.maxWorldRow) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }

                // split on whitespace to tolerate multiple spaces or tabs
                String numbers[] = line.trim().split("\\s+");

                for (col = 0; col < gp.maxWorldCol; col++) {
                    if (col < numbers.length) {
                        try {
                            int num = Integer.parseInt(numbers[col]);
                            mapTileNum[col][row] = num;
                        } catch (NumberFormatException e) {
                            mapTileNum[col][row] = VOID_TILE;
                        }
                    } else {
                        mapTileNum[col][row] = VOID_TILE;
                    }
                }
                row++;
            }
            br.close();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public void draw(Graphics2D g2) {
        
        // ================================
        // PASS 1: NORMAL TILES
        // ================================
        for (int worldRow = 0; worldRow < gp.maxWorldRow; worldRow++) {
            for (int worldCol = 0; worldCol < gp.maxWorldCol; worldCol++) {

                int tileNum = mapTileNum[worldCol][worldRow];
                Tile t = tile[tileNum];

                int worldX = worldCol * gp.tileSize;
                int worldY = worldRow * gp.tileSize;

                int screenX = worldX - gp.player.worldX + gp.player.screenX;
                int screenY = worldY - gp.player.worldY + gp.player.screenY;

                boolean visible =
                    worldX > gp.player.worldX - gp.player.screenX - gp.tileSize &&
                    worldX < gp.player.worldX + gp.player.screenX + gp.tileSize &&
                    worldY > gp.player.worldY - gp.player.screenY - gp.tileSize &&
                    worldY < gp.player.worldY + gp.player.screenY + gp.tileSize;

                if (!visible) continue;

                boolean isBigObject = t.width > 1 || t.height > 1;

                // skip big objects here (handled in pass 2)
                if (!isBigObject) {
                    g2.drawImage(t.image, screenX, screenY, gp.tileSize, gp.tileSize, null);
                }
            }
        }

        // ================================
        // PASS 2: BIG OBJECTS (STORE, etc.)
        // ================================
        for (int worldRow = 0; worldRow < gp.maxWorldRow; worldRow++) {
            for (int worldCol = 0; worldCol < gp.maxWorldCol; worldCol++) {

                int tileNum = mapTileNum[worldCol][worldRow];
                Tile t = tile[tileNum];

                if (t.width <= 1 && t.height <= 1) continue;

                int worldX = worldCol * gp.tileSize;
                int worldY = worldRow * gp.tileSize;

                int drawW = gp.tileSize * t.width;
                int drawH = gp.tileSize * t.height;

                int screenX = worldX - gp.player.worldX + gp.player.screenX;
                int screenY = worldY - gp.player.worldY + gp.player.screenY;

                int camLeft = gp.player.worldX - gp.player.screenX;
                int camRight = gp.player.worldX + gp.player.screenX;
                int camTop = gp.player.worldY - gp.player.screenY;
                int camBottom = gp.player.worldY + gp.player.screenY;

                boolean visible =
                    worldX + drawW > camLeft &&
                    worldX < camRight &&
                    worldY + drawH > camTop &&
                    worldY < camBottom;

                if (!visible) continue;

                // ================================
                // DRAW REAL TILE BACKGROUND (NOT SCALED)
                // ================================
                for (int r = 0; r < t.height; r++) {
                    for (int c = 0; c < t.width; c++) {

                        int bgCol = worldCol + c;
                        int bgRow = worldRow + r;

                        if (bgCol < 0 || bgRow < 0 ||
                            bgCol >= gp.maxWorldCol || bgRow >= gp.maxWorldRow) continue;

                        int bgTileNum = mapTileNum[bgCol][bgRow];

                        int bgWorldX = bgCol * gp.tileSize;
                        int bgWorldY = bgRow * gp.tileSize;

                        int bgScreenX = bgWorldX - gp.player.worldX + gp.player.screenX;
                        int bgScreenY = bgWorldY - gp.player.worldY + gp.player.screenY;

                        g2.drawImage(
                            tile[0].image,
                            bgScreenX,
                            bgScreenY,
                            gp.tileSize,
                            gp.tileSize,
                            null
                        );
                    }
                }

                // ================================
                // DRAW BIG OBJECT ON TOP
                // ================================
                g2.drawImage(
                    t.image,
                    screenX,
                    screenY,
                    drawW,
                    drawH,
                    null
                );
            }
        }
    }

    public boolean isCoveredByBigObject(int col, int row) {
        int tileNum = mapTileNum[col][row];
        Tile t = tile[tileNum];

        if (t.width <= 1 && t.height <= 1) return false;

        // anchor top-left only
        int baseCol = col;
        int baseRow = row;

        // check if this tile is inside any big object area
        for (int r = 0; r < t.height; r++) {
            for (int c = 0; c < t.width; c++) {

                if (col == baseCol + c && row == baseRow + r) {
                    return !(c == 0 && r == 0); // only allow anchor
                }
            }
        }
        return false;
    }



    public void initTileHP(String mapPath) {
        // System.out.println("[initTileHP] called with: " + mapPath);aaa
        for (int col = 0; col < gp.maxWorldCol; col++) {
            for (int row = 0; row < gp.maxWorldRow; row++) {
                int tileNum = mapTileNum[col][row];
                tileHP[col][row] = tile[tileNum].maxHP;
            }
        }
        // Spawn ore veins based on map path
        spawnVeins(14, 500, 3, 7, mapPath);
    }

    public void spawnVeins(int tileID, int veinCount, int minSize, int maxSize, String mapPath) {
        //make veins different even on same map path
        long seed = gp.worldSeed + mapPath.hashCode();
        java.util.Random rand = new java.util.Random(seed);
    
        for (int v = 0; v < veinCount; v++) {
            int centerCol = 0, centerRow = 0, attempts = 0;
            do {
                centerCol = rand.nextInt(gp.maxWorldCol);
                centerRow = rand.nextInt(gp.maxWorldRow);
                attempts++;
            } while (mapTileNum[centerCol][centerRow] != 1 && attempts < 50);
            if (attempts >= 50) continue;
    
            int size = minSize + rand.nextInt(maxSize - minSize + 1);
            for (int i = 0; i < size; i++) {
                int col = centerCol + rand.nextInt(3) - 1;
                int row = centerRow + rand.nextInt(3) - 1;
                if (col >= 0 && col < gp.maxWorldCol
                        && row >= 0 && row < gp.maxWorldRow
                        && mapTileNum[col][row] == 1) {
                    mapTileNum[col][row] = tileID;
                    tileHP[col][row] = tile[tileID].maxHP;
                }
            }
        }
    }


}