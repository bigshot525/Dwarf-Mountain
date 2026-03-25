package Main;

import javax.swing.JPanel;

import entity.Item;
import entity.Player;
import entity.object.superObject;
import tile.TileManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseListener;



/*
    break tiles/ spawn items

*/

public class GamePanel extends JPanel implements Runnable {

    public UI ui = new UI(this);
    public SaveManager saveManager = new SaveManager(this);
    public GameState gameState = GameState.MAIN_MENU;
    public MainMenu mainMenu = new MainMenu(this);
    public SaveScreen saveScreen = new SaveScreen(this);
    public boolean showSaveScreen = false;




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

    // 0 = closed, 1 = inventory, 2 = crafting, 3 = pause
    public int currentTab = 0; 


    public boolean canMove = true;

    //save game
    private long autoSaveTimer = 0;
    private final long AUTO_SAVE_INTERVAL = 600_000_000_000L; // 10 minutes in nanoseconds
    public long lastUpdateTime = System.nanoTime();


    //FPS
    int FPS = 60;

    public TileManager tileM = new TileManager(this);
    KeyHandler keyH = new KeyHandler(this);
    
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

    this.addMouseListener((MouseListener) keyH);

    }



    public void resetGame() {
    player.setDefaultValues();
    tileM.loadMap("/res/maps/mountain01.txt");
    items = new Item[100];
    currentTab = 0;
    }

    public void startGameThread(){
        gameThread = new Thread(this);
        gameThread.start();
    }


    @Override
    public void run() {

        long lastTime = System.nanoTime();
        lastUpdateTime = lastTime;
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;

        long timer = 0;
        int drawCount = 0;


        while (gameThread != null) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            timer += now - lastTime;
            lastUpdateTime = now;
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
    
    public void update() {
        if (gameState == GameState.MAIN_MENU) {
            mainMenu.update();
            return;
        }

        if (showSaveScreen) return; // pause everything while save screen is open

        autoSaveTimer += System.nanoTime() - lastUpdateTime;
        if (autoSaveTimer >= AUTO_SAVE_INTERVAL) {
            saveManager.save();
            autoSaveTimer = 0;
        }

        if (currentTab == 3) return;

        player.update();

        if (keyH.attackCooldown > 0) keyH.attackCooldown--;
        if (keyH.attackPressed && keyH.attackCooldown == 0) {
            breakTileFacing(player.direction);
            keyH.attackCooldown = 15;
        }

        for (Item item : items) {
            if (item != null) item.update();
        }
    }

    @Override
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;

        if (gameState == GameState.MAIN_MENU) {
            mainMenu.draw(g2);
            g2.dispose();
            return;
        }

        tileM.draw(g2);

        for (Item item : items) {
            if (item != null) item.draw(g2, this);
        }

        player.draw(g2);

        if (currentTab > 0) ui.draw(g2);

        if (showSaveScreen) saveScreen.draw(g2); // draw on top of everything

        g2.dispose();
    }

    public void breakTileFacing(String direction) {
        int col = (player.worldX + tileSize / 2) / tileSize;
        int row = (player.worldY + tileSize / 2) / tileSize;

        switch (direction) {
            case "up":    row--; break;
            case "down":  row++; break;
            case "left":  col--; break;
            case "right": col++; break;
        }

        // Bounds check first
        if (col < 0 || col >= maxWorldCol || row < 0 || row >= maxWorldRow) return;

        int tileNum = tileM.mapTileNum[col][row];

        // Exit early if not breakable
        if (!tileM.tile[tileNum].breakable) return;


        tileM.tileHP[col][row] -= player.toolLevel;


        if (tileM.tileHP[col][row] <= 0) {
                tileM.tileHP[col][row] = tileM.tile[0].maxHP;

                if (tileNum == 1) {
                    tileM.mapTileNum[col][row] = 5;
                    Player.stone++;
                    spawnItem(new entity.object.OBJ_Stone(), col * tileSize, row * tileSize);
                } else if (tileNum == 4) {
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
                break;
            }
        }
    }
}