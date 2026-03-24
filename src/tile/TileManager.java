package tile;

import Main.GamePanel;
import java.awt.Graphics2D;

public class TileManager {



    GamePanel gp;
    public Tile[] tile;
    public int mapTileNum[][];
    public int[][] tileHP;
    



    public TileManager(GamePanel gp){
        this.gp = gp;

         tile = new Tile[10];
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];

        getTileImage();
        loadMap("/res/maps/mountain01.txt");
        
        tileHP = new int[gp.maxWorldCol][gp.maxWorldRow];

        for(int col = 0; col < gp.maxWorldCol; col++){
            for(int row = 0; row < gp.maxWorldRow; row++){
                int tileNum = mapTileNum[col][row];
                tileHP[col][row] = tile[tileNum].maxHP;
            }
        }

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
            tile[7].image = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/tile/wood.png"));
            tile[7].collision = false;
            tile[7].layer = 5;

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void loadMap(String filePath){
        try{
            java.io.InputStream is = getClass().getResourceAsStream(filePath);
            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(is));

            int col = 0;
            int row = 0;

            while (col < gp.maxWorldCol && row < gp.maxWorldRow){

                String line = br.readLine();

                while (col < gp.maxWorldCol){
                    String numbers[] = line.split(" ");

                    int num = Integer.parseInt(numbers[col]);

                    mapTileNum[col][row] = num;
                    col++;
                }
                if (col == gp.maxWorldCol){
                    col = 0;
                    row++;
                }
            }
            br.close();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public void draw(Graphics2D g2){

        int worldCol = 0;
        int worldRow = 0;


        while(worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow){

            int tileNum = mapTileNum[worldCol][worldRow];

            int worldX = worldCol * gp.tileSize;
            int worldY = worldRow * gp.tileSize;
            int screenX = worldX - gp.player.worldX + gp.player.screenX;
            int screenY = worldY - gp.player.worldY + gp.player.screenY;


            if(worldX > gp.player.worldX - gp.player.screenX - gp.tileSize &&
               worldX < gp.player.worldX + gp.player.screenX + gp.tileSize &&
               worldY > gp.player.worldY - gp.player.screenY - gp.tileSize &&
               worldY < gp.player.worldY + gp.player.screenY + gp.tileSize){
               
                g2.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
            }
            worldCol++;

            if(worldCol == gp.maxWorldCol){
                worldCol = 0;
                worldRow++;
            }

        }



        


    }
}
