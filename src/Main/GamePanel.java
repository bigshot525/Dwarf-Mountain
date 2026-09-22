package Main;

import UI.GameState;
import UI.InteractionUIs.BalinInteractionUI;
import UI.MainMenu;
import UI.SaveScreen;
import UI.UI;
import entity.Balin;
import entity.Item;
import entity.Player;
import entity.WorldObject;
import entity.object.superObject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.MouseListener;
import java.util.Random;
import javax.swing.JPanel;
import tile.TileManager;



/*
    break tiles/ spawn items

*/

public class GamePanel extends JPanel implements Runnable {
    Random rand = new Random();

    public UI ui = new UI(this);
    public Time t = new Time(this);

    //init npcs
    Balin balin = new Balin(this);
    public BalinInteractionUI balinInteractionUI;

    public SaveManager saveManager = new SaveManager(this);
    public GameState gameState = GameState.MAIN_MENU;
    public MainMenu mainMenu = new MainMenu(this);
    public SaveScreen saveScreen = new SaveScreen(this);
    public java.util.List<WorldObject> worldObjects = new java.util.ArrayList<>();

    public boolean showSaveScreen = false;

    public MapManager mapManager = new MapManager(this);
    public java.util.List<entity.Door> doors = new java.util.ArrayList<>();


    public float fadeAlpha = 0f;
    public boolean showDoorPrompt = false;
    public boolean showFurnacePrompt = false; 
    public boolean showSleepPrompt = false;
    public boolean showBalinsShopPrompt = false;
    public boolean showHud = true;
    public boolean showBalinInteraction = false;

    final int originalTileSize = 16; //16x16 tile
    final int scale = 3;
    public final int tileSize = originalTileSize * scale; //48x48 tile
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; //768 pixels
    public final int screenHeight = tileSize * maxScreenRow; //576 pixels

    //World Settings
    public final int maxWorldCol = 500;
    public final int maxWorldRow = 500;
    public final int worldWidth = tileSize * maxWorldCol;
    public final int worldHeight = tileSize * maxWorldRow;

    // 0 = closed,
    //  1 = inventory,
    //  2 = crafting,
    //  3 = pause,
    //  4 = furnace,
    //  5 = sleep,
    //  6 = passout,
    //  7 = Balin's Shop,
    //  8 = Balin Interaction,

    public int currentTab = 0;


    //world seed
    public long worldSeed = 0;

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

    // Dropped items for the CURRENTLY LOADED map only. Each map keeps its own array in
    // mapItemsStorage below, and this reference gets swapped whenever changeMap() runs.
    public Item[] items = new Item[100];

    // Per-map storage for dropped items, keyed by map path (e.g. "/res/maps/mountain01.txt").
    // Prevents items dropped on one map from appearing on another map that reuses the same
    // col/row space (this was causing mountain stone/iron to render inside house1).
    public java.util.Map<String, Item[]> mapItemsStorage = new java.util.HashMap<>();

    // Placement mode for furnaces and objects
    public boolean placementMode = false;
    public String itemToPlace = null;  // "furnace" etc
    public int mouseScreenX = 0, mouseScreenY = 0;
    
    Thread gameThread;
    public Player player = new Player(this, keyH);

    
    public assetSetter aSetter = new assetSetter(this);
    
    public superObject obj[]= new superObject[10];
 

    public GamePanel(){
        balinInteractionUI = new BalinInteractionUI(this, ui);

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        this.addMouseListener((MouseListener) keyH);
        this.addMouseMotionListener((java.awt.event.MouseMotionListener) keyH);
    }



    public void resetGame() {
        player.setDefaultValues();
        worldSeed = new java.util.Random().nextLong(); // random for each new game
        showHud = true;
        t.reset();
        ui.resetInventoryLayout();

        player.worldX = 250 * tileSize;
        player.worldY = 64 * tileSize;
        tileM.loadMap("/res/maps/mountain01.txt");
        tileM.initTileHP("/res/maps/mountain01.txt");
        mapManager.currentMap = "/res/maps/mountain01.txt";

        // Clear all per-map item storage and start the new game with a fresh mountain01 array
        mapItemsStorage.clear();
        items = new Item[100];
        mapItemsStorage.put("/res/maps/mountain01.txt", items);

        currentTab = 0;
        setupDoors();
    }

    // Stores the array currently referenced by `items` under the given map name, so it can
    // be restored later. Called right before switching maps.
    public void saveCurrentMapItems(String mapName) {
        mapItemsStorage.put(mapName, items);
    }

    // Points `items` at the array belonging to the given map, creating a fresh empty one the
    // first time that map is visited. Called right after switching maps.
    public void loadMapItems(String mapName) {
        items = mapItemsStorage.computeIfAbsent(mapName, k -> new Item[100]);
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
        long clock = 0;
        int drawCount = 0;


        while (gameThread != null) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            timer += now - lastTime;
            clock += now - lastTime;
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

            if(currentTab == 0){
                //update in-game time every 20 seconds
                if(clock >= 20000000000L){
                    t.updateTime();
                    System.out.println("Time: " + t.minute);
                    clock = 0;
                }
            }

            //print FPS every second
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

        //update npcs
        balin.update();

        //update player
        player.update();
        ui.update();

        // Check if player is on a door tile to change map
        int playerCol = (player.worldX + tileSize / 2) / tileSize;
        int playerRow = (player.worldY + tileSize / 2) / tileSize;
        for (entity.Door door : doors) {
            int doorCol = door.worldX / tileSize;
            int doorRow = door.worldY / tileSize;
            if (playerCol == doorCol && playerRow == doorRow) {
                mapManager.changeMap(door.targetMap, door.targetCol, door.targetRow);
                setupDoors();
                break;
            }
        }

        if (keyH.attackCooldown > 0) keyH.attackCooldown--;
        if (keyH.attackPressed && keyH.attackCooldown == 0) {
            breakTileFacing(player.direction);
            keyH.attackCooldown = 15;
        }

        for (Item item : items) {
            if (item != null) item.update();
        }
        checkDoors();
        checkFurnaceInteraction();
        checkBedInteraction();

        //check if interacting with npcs
        checkBalinInteraction();

        if(mapManager.currentMap.equals("/res/maps/house1.txt")){
            // check if player is looking at a register tile, if true show prompt to open register UI
            checkRegisterInteraction();
        }
    }

    private void checkDoors() {
        // Find the tile the player is facing
        int col = (player.worldX + tileSize / 2) / tileSize;
        int row = (player.worldY + tileSize / 2) / tileSize;

        switch (player.direction) {
            case "up":    row--; break;
            case "down":  row++; break;
            case "left":  col--; break;
            case "right": col++; break;
        }

        int facingTile = tileM.mapTileNum[col][row];

        // Show prompt if facing a door tile
        if (facingTile == 11) {
            showDoorPrompt = true;
            if (keyH.interactPressed) {
                for (entity.Door door : doors) {
                    int doorCol = door.worldX / tileSize;
                    int doorRow = door.worldY / tileSize;
                    if (doorCol == col && doorRow == row) {
                        keyH.interactPressed = false;
                        mapManager.changeMap(door.targetMap, door.targetCol, door.targetRow);
                        setupDoors();
                        return;
                    }
                }
            }
        } else {
            showDoorPrompt = false;
        }
    }

    private void checkFurnaceInteraction(){
        // Find the tile the player is facing
        int col = (player.worldX + tileSize / 2) / tileSize;
        int row = (player.worldY + tileSize / 2) / tileSize;

        switch (player.direction) {
            case "up":    row--; break;
            case "down":  row++; break;
            case "left":  col--; break;
            case "right": col++; break;
        }

        if (col < 0 || col >= maxWorldCol || row < 0 || row >= maxWorldRow) {
            showFurnacePrompt = false;
            return;
        }

        boolean facingFurnace = false;
        for(Item item : items){
            if(item instanceof entity.object.OBJ_Furnace){
                int furnaceCol = item.worldX / tileSize;
                int furnaceRow = item.worldY / tileSize;
                if(furnaceCol == col && furnaceRow == row){
                    facingFurnace = true;
                    break;
                }
            }
        }

        if(facingFurnace){
            showFurnacePrompt = true;
            if(keyH.interactPressed){
                keyH.interactPressed = false;
                currentTab = 4;
            }
        }else{
            showFurnacePrompt = false;
        }
        
        if (col < 0 || col >= maxWorldCol || row < 0 || row >= maxWorldRow) {
            showFurnacePrompt = false;
            return;
        }
    }


    private void checkBedInteraction(){
        // has to be top tile of the bed to interact
        // show prompt to sleep through the night (skip the day)
        int col = (player.worldX + tileSize / 2) / tileSize;
        int row = (player.worldY + tileSize / 2) / tileSize;
        switch (player.direction) {
            case "up":    row--; break;
            case "down":  row++; break;
            case "left":  col--; break;
            case "right": col++; break;
        }

        if (col < 0 || col >= maxWorldCol || row < 0 || row >= maxWorldRow) {
            showSleepPrompt = false;
            return;
        }
        boolean facingBed = false;
        for(Item item : items){
            //TODO: add other bed types 
            if(item instanceof entity.object.OBJ_StoneBed){
                int bedCol = item.worldX / tileSize;
                int bedRow = item.worldY / tileSize;
                if(bedCol == col && bedRow == row){
                    facingBed = true;
                    break;
                }
            }
        }
        if(facingBed){
            //check if it's nighttime
            // if(t.hour >= )
            showSleepPrompt = true;
            if(keyH.interactPressed){
                keyH.interactPressed = false;
                //
                currentTab = 5; // sleep screen
            }
        }else{
            showSleepPrompt = false;
        }
        if (col < 0 || col >= maxWorldCol || row < 0 || row >= maxWorldRow) {
            showSleepPrompt = false;
            return;
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
            if (item != null) {

                if (item.isInteractible) {
                    item.drawStatic(g2, this);
                } else {
                    item.draw(g2, this);
                }
            }
        }

        balin.draw(g2);
        player.draw(g2);

        // Draw prompt AFTER player so it appears on top
        if (showDoorPrompt) {
            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRoundRect(screenWidth / 2 - 80, screenHeight - 80, 160, 35, 10, 10);
            g2.setColor(Color.WHITE);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 16f));
            FontMetrics fm = g2.getFontMetrics();
            String prompt = "Enter";
            g2.drawString(prompt, screenWidth / 2 - fm.stringWidth(prompt) / 2, screenHeight - 57);
        }else if(showFurnacePrompt){
            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRoundRect(screenWidth / 2 - 80, screenHeight - 80, 160, 35, 10, 10);
            g2.setColor(Color.WHITE);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 16f));
            FontMetrics fm = g2.getFontMetrics();
            String prompt = "Right Click to Interact";
            g2.drawString(prompt, screenWidth / 2 - fm.stringWidth(prompt) / 2, screenHeight - 57);
        }else if(showSleepPrompt){
            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRoundRect(screenWidth / 2 - 150, screenHeight - 80, 300, 35, 10, 10);
            g2.setColor(Color.WHITE);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 16f));
            FontMetrics fm = g2.getFontMetrics();
            String prompt = "Right Click to Sleep for the Night";
            g2.drawString(prompt, screenWidth / 2 - fm.stringWidth(prompt) / 2, screenHeight - 57);
        }else if(showBalinsShopPrompt){
            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRoundRect(screenWidth / 2 - 150, screenHeight - 80, 300, 35, 10, 10);
            g2.setColor(Color.WHITE);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 16f));
            FontMetrics fm = g2.getFontMetrics();
            String prompt = "Right Click to Shop";
            g2.drawString(prompt, screenWidth / 2 - fm.stringWidth(prompt) / 2, screenHeight - 57);
        }

        if(currentTab == 4){
            ui.drawFurnaceUI(g2);
        }
        if(currentTab == 5){
            ui.drawSleepScreen(g2);
        }
        if(currentTab == 6){
            ui.drawPassoutScreen(g2);
        }
        if(currentTab == 7){
            ui.openBalinsShop(g2);
        }
        if (currentTab > 0 && currentTab < 4) ui.draw(g2);

        if(currentTab == 8){
            balinInteractionUI.balinInteraction(g2);
        }

        if (showSaveScreen) saveScreen.draw(g2);

        drawHud(g2);
        ui.drawEnergyBar(g2);

        if (fadeAlpha > 0) {
            g2.setComposite(java.awt.AlphaComposite.getInstance(
                java.awt.AlphaComposite.SRC_OVER, Math.min(fadeAlpha, 1f)));
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, screenWidth, screenHeight);
            g2.setComposite(java.awt.AlphaComposite.getInstance(
                java.awt.AlphaComposite.SRC_OVER, 1f));
        }

        if (placementMode) {
            drawPlacementGrid(g2);
        }
        g2.dispose();
    }

    private void drawHud(java.awt.Graphics2D g2) {
        if (!showHud) {
            return;
        }
        //time overlay
        String dateText = t.getDateString();
        String timeText = t.getTimeString();
        g2.setFont(g2.getFont().deriveFont(java.awt.Font.BOLD, 18f));
        java.awt.FontMetrics fm = g2.getFontMetrics();
        int padding = 8;
        int lineSpacing = 4;
        int boxWidth = Math.max(fm.stringWidth(dateText), fm.stringWidth(timeText)) + padding * 2;
        int boxHeight = fm.getHeight() * 3 + lineSpacing * 2 + padding * 2;
        int x = screenWidth - boxWidth - 10;
        int y = 10;
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(x, y, boxWidth, boxHeight, 12, 12);
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(x, y, boxWidth, boxHeight, 12, 12);

        g2.drawString(dateText, x + padding, y + padding + fm.getAscent() - 2);
        //time in the center of the box
        g2.drawString(timeText, x + padding + (boxWidth / 2) - (fm.stringWidth(timeText) / 2), y + padding + fm.getHeight() + lineSpacing + fm.getAscent() - 2);
        //draw player money under time
        String moneyText = "Coins: " + player.coins;
        g2.drawString(moneyText, x + padding + (boxWidth / 2) - (fm.stringWidth(moneyText) / 2), y + padding + fm.getHeight() * 2 + lineSpacing * 2 + fm.getAscent() - 2);
    
        //Hotbar overlay (9 slots, 1-9 keys, show item icon, make current slot thicker)
        //get current slot from player.currentHotbarSlot (0-8)
        //get specific hotbar png based on the current slot
        int currentSlot = player.currentHotbarSlot;
        java.awt.image.BufferedImage hotbarImage = null;

        try {
            switch (currentSlot) {
                case 0: hotbarImage = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/UI/HotbarItem1.png")); break;
                case 1: hotbarImage = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/UI/HotbarItem2.png")); break;
                case 2: hotbarImage = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/UI/HotbarItem1.png")); break;
                case 3: hotbarImage = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/UI/HotbarItem1.png")); break;
                case 4: hotbarImage = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/UI/HotbarItem1.png")); break;
                case 5: hotbarImage = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/UI/HotbarItem1.png")); break;
                case 6: hotbarImage = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/UI/HotbarItem1.png")); break;
                case 7: hotbarImage = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/UI/HotbarItem1.png")); break;
                case 8: hotbarImage = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/UI/HotbarItem1.png")); break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (hotbarImage != null) {
            // Keeps your exact original coordinates (200, 500)
            g2.drawImage(hotbarImage, 150, 500, 450, 60, null);
        }

        //draw png of the hotbar in the bottom center of the screen
    }

    private void drawPlacementGrid(java.awt.Graphics2D g2) {
        //check if item is placeable
        boolean placeable = false;
        if(itemToPlace.equals("furnace") && Player.furnace > 0){
            placeable = true;
        }else if(itemToPlace.equals("stoneBed") && Player.stoneBed > 0){
            placeable = true;
        }

        if(placeable){
            // Get the grid cell containing the player's center
            int gridCol = (player.worldX + tileSize / 2) / tileSize;
            int gridRow = (player.worldY + tileSize / 2) / tileSize;

            // Draw 3x3 grid centered under the player
            for (int dy = 1; dy <= 3; dy++) {
                for (int dx = -1; dx <= 1; dx++) {
                    int col = gridCol + dx;
                    int row = gridRow + dy - 2;
                    
                    // Convert world coords to screen coords
                    int screenX = col * tileSize - player.worldX + player.screenX;
                    int screenY = row * tileSize - player.worldY + player.screenY;
                    
                    // Only draw if on screen
                    if (screenX + tileSize > 0 && screenX < screenWidth &&
                        screenY + tileSize > 0 && screenY < screenHeight) {
                        
                        boolean isValid = isValidPlacementTile(col, row);
                        
                        // Highlight center cell and show valid/invalid status
                        if (dx == 0 && dy == 2) {
                            // Center cell - bright if valid, red if invalid
                            if (isValid) {
                                g2.setColor(new Color(0, 255, 0, 150));
                                g2.fillRect(screenX, screenY, tileSize, tileSize);
                                g2.setColor(new Color(0, 255, 0, 255));
                            } else {
                                g2.setColor(new Color(255, 0, 0, 150));
                                g2.fillRect(screenX, screenY, tileSize, tileSize);
                                g2.setColor(new Color(255, 0, 0, 255));
                            }
                        } else {
                            // Surrounding cells - dim if valid, red-dim if invalid
                            if (isValid) {
                                g2.setColor(new Color(0, 255, 0, 80));
                            } else {
                                g2.setColor(new Color(255, 0, 0, 80));
                            }
                            g2.fillRect(screenX, screenY, tileSize, tileSize);
                        }
                        
                        g2.drawRect(screenX, screenY, tileSize, tileSize);
                    }
                }
            }
            // Draw instruction text
            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRoundRect(screenWidth / 2 - 120, screenHeight - 60, 240, 45, 10, 10);
            g2.setColor(Color.WHITE);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 16f));
            FontMetrics fm = g2.getFontMetrics();
            String instruction = "Click to place | ESC to cancel";
            g2.drawString(instruction, screenWidth / 2 - fm.stringWidth(instruction) / 2, screenHeight - 30);
        }else{
            return;
        }
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

        player.energy -= 1; // reduce energy on each swing attempt
        if(player.energy == 0){
            //when players energy drops to 0, force player to pass out and skip to next morning and spawn at entrance of mountain
            //tell user they passed out from exhaustion

            player.canMove = false;
            //spawn player at entrance of mountain and skip to next morning
            player.worldX = 250 * tileSize;
            player.worldY = 64 * tileSize;
            currentTab = 6; // passout screen

            t.hour = 6;
            t.day++;

            //reset energy to full
            player.energy = player.maxEnergy;
        }

        if (tileM.tileHP[col][row] <= 0) {
                tileM.tileHP[col][row] = tileM.tile[0].maxHP;
                if (tileNum == 1) {
                    tileM.mapTileNum[col][row] = 5;
                    // Check for coal spawn
                    if(rand.nextInt(20) == 1){
                        Player.coal++;
                        // System.out.println("You found a peice of coal!");
                        spawnItem(new entity.object.OBJ_Coal(), col * tileSize, row * tileSize);
                    }
                    Player.stone++;
                    spawnItem(new entity.object.OBJ_Stone(), col * tileSize, row * tileSize);
                } else if (tileNum == 4) { //Wood
                    tileM.mapTileNum[col][row] = 0;
                    Player.wood++;
                    spawnItem(new entity.object.OBJ_Wood(), col * tileSize, row * tileSize);
                }else if(tileNum == 14){ //Iron ore
                    // System.out.println("You found an iron ore!");
                    tileM.mapTileNum[col][row] = 5;
                    Player.ironOre++;
                    spawnItem(new entity.object.OBJ_Iron(), col * tileSize, row * tileSize);
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

    public void setupDoors() {
        doors.clear();

        if (mapManager.currentMap.equals("/res/maps/mountain01.txt")) {
            doors.add(new entity.Door(
                210 * tileSize, 72 * tileSize,
                "/res/maps/house1.txt",
                7, 28,
                "/res/maps/mountain01.txt",
                210, 73,
                false
            ));
        } else if (mapManager.currentMap.equals("/res/maps/house1.txt")) {
            doors.add(new entity.Door(
                7 * tileSize, 29 * tileSize,     // exit door inside house
                "/res/maps/mountain01.txt",
                210, 73,
                "/res/maps/house1.txt",
                7, 29,
                true
            ));
        }
    }

    // Placement mode methods
    public void startPlacement(String itemType) {
        placementMode = true;
        itemToPlace = itemType;
    }

    public void cancelPlacement() {
        placementMode = false;
        itemToPlace = null;
    }

    private boolean isValidPlacementTile(int col, int row) {
        // Check bounds
        if (col < 0 || col >= maxWorldCol || row < 0 || row >= maxWorldRow) return false;
        
        //check if inside 3x3 grid centered on player
         int playerCol = (player.worldX + tileSize / 2) / tileSize;
        int playerRow = (player.worldY + tileSize / 2) / tileSize;
        if (Math.abs(col - playerCol) > 1 || Math.abs(row - playerRow) > 1) return false;

        int tileNum = tileM.mapTileNum[col][row];
        // Valid tiles for placement: grass (0), earth/path (3), sand (5), or plank/floor (9)
        //dont allow placement on same tile as player is standing on
        if (col == playerCol && row == playerRow) return false;
        return tileNum == 0 || tileNum == 3 || tileNum == 5 || tileNum == 9;
    }

    public void updateMousePosition(int screenX, int screenY) {
        mouseScreenX = screenX;
        mouseScreenY = screenY;
    }

    public void handlePlacementClick(int screenX, int screenY) {
        if (!placementMode) return;

        //debug
        System.out.println("PLACEMENT CLICK: " + screenX + ", " + screenY);


        // Convert screen coordinates to world coordinates
        int worldX = screenX + player.worldX - player.screenX;
        int worldY = screenY + player.worldY - player.screenY;

        // Snap to grid
        int col = worldX / tileSize;
        int row = worldY / tileSize;

        //debug
        System.out.println("Tile: " + col + ", " + row + " Valid: " + isValidPlacementTile(col, row));

        // Check if placement location is valid
        if (!isValidPlacementTile(col, row)) {
            return; // Can't place on invalid tiles
        }

        // Place items
        if (itemToPlace.equals("furnace") && Player.furnace > 0) {
            spawnPlacedObject(new entity.object.OBJ_Furnace(), col * tileSize, row * tileSize);
            Player.furnace--;
            cancelPlacement();
        }
        else if(itemToPlace.equals("stoneBed") && Player.stoneBed > 0){
            spawnPlacedObject(new entity.object.OBJ_StoneBed(), col * tileSize, row * tileSize);
            Player.stoneBed--;
            cancelPlacement();
        }
    }

    public void spawnPlacedObject(Item item, int worldX, int worldY) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                item.worldX = worldX;
                item.worldY = worldY;
                items[i] = item;
                break;
            }
        }
    }

    public void checkRegisterInteraction() {
        // Find the tile the player is facing
        int col = (player.worldX + tileSize / 2) / tileSize;
        int row = (player.worldY + tileSize / 2) / tileSize;

        switch (player.direction) {
            case "up":    row--; break;
            case "down":  row++; break;
            case "left":  col--; break;
            case "right": col++; break;
        }
        // Show prompt if facing a register and is on right coords
        // System.out.println("Facing: " + col + ", " + row + " dir=" + player.direction);

        if (player.direction.equals("up") && row == 19 && col == 3) {
            // System.out.println("FOUND REGISTER");
            showBalinsShopPrompt = true;
            if (keyH.interactPressed) {
                 keyH.interactPressed = false; currentTab = 7; 
            }
        } else {
            showBalinsShopPrompt = false;
        }

    }
    public void checkBalinInteraction() {
        // Find the tile the player is facing
        int col = (player.worldX + tileSize / 2) / tileSize;
        int row = (player.worldY + tileSize / 2) / tileSize;

        switch (player.direction) {
            case "up":    row--; break;
            case "down":  row++; break;
            case "left":  col--; break;
            case "right": col++; break;
        }

        // show ui if player is withing 1 tile of balin and clicks on him,  do not promt player to interact with him
        int balinCol = balin.worldX / tileSize;
        int balinRow = balin.worldY / tileSize;
        if (Math.abs(col - balinCol) <= 1 && Math.abs(row - balinRow) <= 1) {
            if (keyH.interactPressed) {
                keyH.interactPressed = false;
                currentTab = 8; // Balin Interaction
            }
        }

    }
}