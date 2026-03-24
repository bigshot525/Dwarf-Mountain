package Main;

import javax.swing.JPanel;

import entity.Item;
import entity.Player;
import entity.object.superObject;
import tile.Tile;
import tile.TileManager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseListener;



/*
    break tiles/ spawn items

*/

public class GamePanel extends JPanel implements Runnable {

    public UI ui = new UI(this);

    final int originalTileSize = 16; //16x16 tile
    final int scale = 3;
    public final int tileSize = originalTileSize * scale; //48x48 tile
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; //768 pixels
    public final int screenHeight = tileSize * maxScreenRow; //576 pixels

    //World Settings
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    public final int worldWidth = tileSize * maxWorldCol;
    public final int worldHeight = tileSize * maxWorldRow;


    public boolean showInventory = false;
    public boolean showCrafting = false;


    public boolean canMove = true;

    //FPS
    int FPS = 60;

    public TileManager tileM = new TileManager(this);
    KeyHandler keyH = new KeyHandler();
    
    public CollisionChecker cChecker = new CollisionChecker(this);

    public Item[] items = new Item[100];


    
    
    Thread gameThread;
    public Player player = new Player(this, keyH);

    
    public assetSetter aSetter = new assetSetter(this);
    
    public superObject obj[]= new superObject[10];
 

    public GamePanel(){

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);


        this.addKeyListener(keyH);
        this.addMouseListener((MouseListener) keyH);
        this.setFocusable(true);

    }

    public void startGameThread(){
        gameThread = new Thread(this);
        gameThread.start();
    }


    @Override
    public void run() {

        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;

        long timer = 0;
        int drawCount = 0;


        while (gameThread != null) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            timer += now - lastTime;
            lastTime = now;



            if (delta >= 1) {
                //1 Update: update information such as character positions
                update();
                //2 Draw: draw the screen with the updated information
                repaint();
                delta--;
                drawCount++;

            }
            if (timer >= 1000000000) {
                System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
            }
        }
    }





    public void update(){
        //update character positions
        player.update();
        for (Item item : items){
            if (item != null){
                item.update();
            }
        }
    }

    @Override
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);

        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;

        tileM.draw(g2);

    // DRAW DROPPED ITEMS 
    for (Item item : items) {
        if (item != null) {
            item.draw(g2, this);
        }
    }

        player.draw(g2);

        // DRAW INVENTORY ON TOP
        if (showInventory) {
            ui.drawInventory(g2);
        }
        
        if(showCrafting){
            ui.drawCrafting(g2);
        }

        g2.dispose();
    }

    
    
public void breakTileFacing(String direction) {
    System.out.println("breaking facing " + direction);
    int col = (player.worldX + tileSize / 2) / tileSize;
    int row = (player.worldY + tileSize / 2) / tileSize;

    switch (direction) {
        case "up":    row--; break;
        case "down":  row++; break;
        case "left":  col--; break;
        case "right": col++; break;
    }

    if (col < 0 || col >= maxWorldCol || row < 0 || row >= maxWorldRow) return;

int tileNum = tileM.mapTileNum[col][row];
Tile tile = tileM.tile[tileNum];

if (!tile.breakable) return;


    tileM.tileHP[col][row] -= player.toolLevel;
    System.out.println("Tile at (" + col + ", " + row + ") has " +  tileM.tileHP[col][row] + " HP.");



    if (tileM.tileHP[col][row] <= 0) {
        tileM.tileHP[col][row] = tileM.tile[0].maxHP;
        if (tile == tileM.tile[1]) {
            tileM.mapTileNum[col][row] = 5;
            Player.stone++;
            System.out.println("added 1 stone to inventory");
            spawnItem(new entity.object.OBJ_Stone(), col * tileSize, row * tileSize);

        }
        if (tile == tileM.tile[4]) {
            //make a tree trunk
            tileM.mapTileNum[col][row] = 0;
            spawnItem(new entity.object.OBJ_Wood(), col * tileSize, row * tileSize);    
        }

    }
}
    

    public void spawnItem(Item item, int worldX, int worldY) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                item.worldX = worldX;
                item.worldY = worldY;
                items[i] = item;

            System.out.println("Spawned item at " + worldX + ", " + worldY);

                break;
            }
        }
}




    



}


