package UI;

import Main.GamePanel;
import entity.Player;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class UI {
    //inventory fields
    private static final int INV_COLS = 9;
    private static final int INV_ROWS = 4;
    private static final int INV_SLOT_COUNT = INV_COLS * INV_ROWS; // 36

    private int[] slotItems;      // slotItems[slot] = index into names[]/itemIcons[], or -1 if empty
    private boolean slotsInit = false;

    private int heldItemIndex = -1; // item currently being dragged (index into names[]/itemIcons[])
    private int heldFromSlot = -1;  // which slot it was picked up from
    private int mouseX, mouseY;     // live cursor pos, updated by mouseDragged



    GamePanel gp;
    private Rectangle[] tabRects = new Rectangle[3];
    private Rectangle exitButton;
    private Rectangle passoutOkButton;
    private BufferedImage[] itemIcons;
    private BufferedImage[] craftIcons;
    private BufferedImage[] smeltIcons;

    private int selectedCraftIndex = -1;
    private Rectangle[] craftSlotRects;
    private Rectangle craftButton;

    private Rectangle[] inventorySlotRects = new Rectangle[INV_SLOT_COUNT];
    private String[] inventoryItemNames = {"Stone", "Wood","Coal", "Iron Ore", "Iron","Gold Ore", "Gold", "Furnace", "Stone Bed"};

    // Each recipe: { {itemIconIndex, count}, ... }
    // itemIconIndex: 0=stone, 1=wood, 2=iron
    private int[][][] recipes = {
        {{0, 5}, {1, 3}},        // Furnace: 5 stone, 3 wood
        {{0, 10}, {1, 10}}       // Stone Bed: 10 stone, 10 wood
    };

    private String[][] recipeLabels = {
        {"Stone x5", "Wood x3"},
        {"Stone x10", "Wood x10"}
    };



    //furnace recipesprivate int selectedSmeltIndex = -1;
    private Rectangle[] smeltSlotRects;
    private Rectangle smeltButton;
    private int selectedSmeltIndex = -1;

    private boolean smeltingInProgress = false;
    private int smeltingRecipeIndex = -1;
    private long smeltStartTime;
    private static final long IRON_SMELT_DURATION_NS = 5_000_000_000L; // 5 seconds

    // Smelt recipes: { {itemIconIndex, count}, ... }
    private int[][][] smeltRecipes = {
        {{3, 5}, {2, 1}},   // Iron Bar:  5 iron ore + 1 coal
        {{5, 5}, {2, 1}},   // Gold Bar:  5 gold ore + 1 coal
    };

    private String[][] smeltRecipeLabels = {
        {"Iron Ore x5", "Coal x1"},
        {"Gold Ore x5", "Coal x1"},
    };

    private void initInventorySlots() {
        slotItems = new int[INV_SLOT_COUNT];
        java.util.Arrays.fill(slotItems, -1);
        for (int i = 0; i < inventoryItemNames.length; i++) {
            slotItems[i] = i; // start each item type in its own default slot, in order
        }
        slotsInit = true;
    }



    public static int[] updatedHave(){
        return new int[]{
            Player.stone,
            Player.wood,
            Player.coal,
            Player.ironOre,
             Player.iron,
             Player.goldOre,
             Player.gold,
             Player.furnace,
             Player.stoneBed
        };
    }

    public void update() {
        if (!smeltingInProgress) return;

        long elapsed = System.nanoTime() - smeltStartTime;
        if (elapsed >= getSmeltDuration(smeltingRecipeIndex)) {
            completeSmelt();
        }
    }

    private long getSmeltDuration(int index) {
        if (index == 0) {
            return IRON_SMELT_DURATION_NS;
        }
        return IRON_SMELT_DURATION_NS;
    }

    private void completeSmelt() {
        smeltingInProgress = false;
        if (smeltingRecipeIndex == 0) {
            Player.iron++;
        } else if (smeltingRecipeIndex == 1) {
            Player.gold++;
        }
        smeltingRecipeIndex = -1;
    }

    private float getSmeltProgress() {
        if (!smeltingInProgress) return 0f;
        long elapsed = System.nanoTime() - smeltStartTime;
        return Math.min(1f, (float) elapsed / getSmeltDuration(smeltingRecipeIndex));
    }

    public UI(GamePanel gp) {
        this.gp = gp;

        loadItemIcons();
    }
    //inventoryItemNames = {"Stone", "Wood","Coal", "Iron Ore", "Iron","Gold Ore", "Gold", "Furnace"};

    private void loadItemIcons() {
        String[] paths = {
            "/res/objects/stone_pile.png",
            "/res/objects/wood.png",
            "/res/objects/coal.png",
            "/res/objects/ironOre.png",
            "/res/objects/iron.png",
            "/res/objects/goldOre.png",
            "/res/objects/gold.png",
            "/res/objects/furnace.png",
            "/res/objects/stoneBed.png"
        };
        itemIcons = new BufferedImage[paths.length];
        for (int i = 0; i < paths.length; i++) {
            try {
                itemIcons[i] = javax.imageio.ImageIO.read(getClass().getResourceAsStream(paths[i]));
            } catch (Exception e) {
                itemIcons[i] = null;
            }
        }


        // Crafting icons
        String[] craftPaths = {
            "/res/objects/furnace.png",
            "/res/objects/stoneBed.png"
        };
        craftIcons = new BufferedImage[craftPaths.length];
        for (int i = 0; i < craftPaths.length; i++) {
            try {
                craftIcons[i] = javax.imageio.ImageIO.read(getClass().getResourceAsStream(craftPaths[i]));
            } catch (Exception e) {
                craftIcons[i] = null;
            }
        }
    }

    public void draw(Graphics2D g2) {
        switch (gp.currentTab) {
            case 1:
                drawInventory(g2);
                break;
            case 2:
                drawCrafting(g2);
                break;
            case 3:
                drawPauseMenu(g2);
                break;
        }
    }

    private void drawBackground(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(50, 50, gp.screenWidth - 100, gp.screenHeight - 100);

    }

    private void drawTabs(Graphics2D g2) {
        String[] tabs = {"Inventory", "Crafting", "Exit"};
        for (int i = 0; i < tabs.length; i++) {
            tabRects[i] = new Rectangle(60 + i * 90, 55, 80, 30);

            if (gp.currentTab == i + 1) {
                g2.setColor(new Color(80, 80, 80, 220));
            } else {
                g2.setColor(new Color(40, 40, 40, 220));
            }
            g2.fillRect(tabRects[i].x, tabRects[i].y, tabRects[i].width, tabRects[i].height);
            g2.setColor(Color.WHITE);
            g2.drawRect(tabRects[i].x, tabRects[i].y, tabRects[i].width, tabRects[i].height);

            g2.setFont(g2.getFont().deriveFont(14f));
            FontMetrics fm = g2.getFontMetrics();
            int labelX = tabRects[i].x + tabRects[i].width / 2 - fm.stringWidth(tabs[i]) / 2;
            g2.drawString(tabs[i], labelX, 75);
        }
    }

    private void drawInventory(Graphics2D g2) {
        //hide date time and money
        // gp.showTimeOverlay = false;

        //put inventoryBackground.png in the background of the inventory screen
        try {
            BufferedImage inventoryBackground = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/ui/inventoryBackground.png"));
            if (inventoryBackground != null) {
                g2.drawImage(inventoryBackground, 50, 50, gp.screenWidth - 100, gp.screenHeight - 220, null);
            }
        } catch (Exception e) {
            drawBackground(g2);
        }

        drawTabs(g2);

        if (!slotsInit) initInventorySlots();

        int startX = 70;
        int startY = 107;
        int cellSize = 64;
        int gap = 6;

        int[] counts = updatedHave();

        for (int slot = 0; slot < INV_SLOT_COUNT; slot++) {
            int col = slot % INV_COLS;
            int row = slot / INV_COLS;
            int x = startX + col * (cellSize + gap);
            int y = startY + row * (cellSize + gap);

            // Store rectangle for click/drag detection
            inventorySlotRects[slot] = new Rectangle(x, y, cellSize, cellSize);

            int itemIndex = slotItems[slot];
            boolean isBeingDragged = (slot == heldFromSlot && heldItemIndex != -1);

            if (itemIndex != -1 && !isBeingDragged && counts[itemIndex] > 0) {
                // Cell background
                // g2.setColor(new Color(60, 60, 60, 200));
                // g2.fillRoundRect(x, y, cellSize, cellSize, 8, 8);
                // g2.setColor(new Color(180, 180, 180, 200));
                // g2.drawRoundRect(x, y, cellSize, cellSize, 8, 8);

                BufferedImage iconToDraw = itemIcons[itemIndex];
                if (iconToDraw != null) {
                    g2.drawImage(iconToDraw, x + 8, y + 8, cellSize - 16, cellSize - 16, null);
                    g2.setColor(Color.GRAY);
                }

                // Name below cell
                g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 11f));
                FontMetrics fm = g2.getFontMetrics();
                // g2.setColor(Color.LIGHT_GRAY);
                // g2.drawString(inventoryItemNames[itemIndex], x + cellSize / 2 - fm.stringWidth(inventoryItemNames[itemIndex]) / 2, y + cellSize + 14);

                // Count badge
                String countStr = String.valueOf(counts[itemIndex]);
                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14f));
                fm = g2.getFontMetrics();
                int badgeX = x + cellSize - fm.stringWidth(countStr) - 4;
                int badgeY = y + cellSize - 4;
                g2.setColor(Color.BLACK);
                g2.drawString(countStr, badgeX + 1, badgeY + 1);
                g2.setColor(Color.WHITE);
                g2.drawString(countStr, badgeX, badgeY);
            }
        }

        drawHeldItem(g2);
    }

    private void drawHeldItem(Graphics2D g2) {
        if (heldItemIndex == -1) return;
        BufferedImage icon = itemIcons[heldItemIndex];
        if (icon != null) {
            g2.drawImage(icon, mouseX - 24, mouseY - 24, 48, 48, null);
        }
    }

    private void drawCrafting(Graphics2D g2) {
        drawBackground(g2);
        drawTabs(g2);

        int startX = 70;
        int startY = 110;
        int cellSize = 64;
        int gap = 10;
        int cols = 8;

        String[] names = {"Furnace", "Stone Bed"};
        craftSlotRects = new Rectangle[names.length];

        for (int i = 0; i < names.length; i++) {
            int col = i % cols;
            int row = i / cols;
            int x = startX + col * (cellSize + gap);
            int y = startY + row * (cellSize + gap);
            craftSlotRects[i] = new Rectangle(x, y, cellSize, cellSize);

            // Highlight selected
            if (i == selectedCraftIndex) {
                g2.setColor(new Color(100, 100, 200, 220));
            } else {
                g2.setColor(new Color(60, 60, 60, 200));
            }
            g2.fillRoundRect(x, y, cellSize, cellSize, 8, 8);
            g2.setColor(i == selectedCraftIndex ? Color.CYAN : new Color(180, 180, 180, 200));
            g2.drawRoundRect(x, y, cellSize, cellSize, 8, 8);

            if (craftIcons[i] != null) {
                g2.drawImage(craftIcons[i], x + 8, y + 8, cellSize - 16, cellSize - 16, null);
            } else {
                g2.setColor(Color.GRAY);
                g2.fillRect(x + 8, y + 8, cellSize - 16, cellSize - 16);
            }

            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 11f));
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawString(names[i], x + cellSize / 2 - fm.stringWidth(names[i]) / 2, y + cellSize + 14);
        }

        // Recipe panel
        if (selectedCraftIndex >= 0) {
            drawRecipe(g2, selectedCraftIndex);
        }
    }

    private boolean canAfford(int recipeIndex){
        int[][] recipe = recipes[recipeIndex];
        int [] have = updatedHave();
        for (int[] req : recipe) {
            if (have[req[0]] < req[1]) return false;
        }
        return true;
    }

    private void drawRecipe(Graphics2D g2, int index) {
        int panelX = 70;
        int panelY = 280;
        int panelW = gp.screenWidth - 170;
        int panelH = 160;

        // Panel background
        g2.setColor(new Color(30, 30, 30, 220));
        g2.fillRoundRect(panelX, panelY, panelW, panelH, 10, 10);
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(panelX, panelY, panelW, panelH, 10, 10);

        // Title
        String[] names = {"Furnace", "Iron Pickaxe", "Gold Pickaxe"};
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 18f));
        g2.setColor(Color.WHITE);
        g2.drawString("Recipe: " + names[index], panelX + 12, panelY + 28);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 13f));
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawString("Required:", panelX + 12, panelY + 52);

        // Ingredient icons + counts
        int[][] recipe = recipes[index];
        int[] have = updatedHave();
        int iconSize = 40;
        int iconGap = 8;

        for (int i = 0; i < recipe.length; i++) {
            int iconIndex = recipe[i][0];
            int required  = recipe[i][1];
            int ix = panelX + 12 + i * (iconSize + iconGap + 60);
            int iy = panelY + 60;

            // Icon cell
            g2.setColor(new Color(50, 50, 50, 200));
            g2.fillRoundRect(ix, iy, iconSize, iconSize, 6, 6);
            g2.setColor(Color.GRAY);
            g2.drawRoundRect(ix, iy, iconSize, iconSize, 6, 6);

            if (itemIcons[iconIndex] != null) {
                g2.drawImage(itemIcons[iconIndex], ix + 4, iy + 4, iconSize - 8, iconSize - 8, null);
            }

            // Count — red if not enough, green if enough
            boolean enough = have[iconIndex] >= required;
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14f));
            g2.setColor(enough ? new Color(80, 220, 80) : new Color(220, 80, 80));
            g2.drawString(have[iconIndex] + "/" + required, ix + iconSize + 6, iy + 26);
        }

        // Craft button
        boolean affordable = canAfford(index);
        craftButton = new Rectangle(panelX + panelW - 160, panelY + panelH - 55, 140, 40);
        g2.setColor(affordable ? new Color(50, 140, 50) : new Color(80, 80, 80));
        g2.fillRoundRect(craftButton.x, craftButton.y, craftButton.width, craftButton.height, 8, 8);
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(craftButton.x, craftButton.y, craftButton.width, craftButton.height, 8, 8);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 16f));
        FontMetrics fm = g2.getFontMetrics();
        String btnLabel = affordable ? "Craft" : "Need more";
        g2.drawString(btnLabel, craftButton.x + craftButton.width / 2 - fm.stringWidth(btnLabel) / 2, craftButton.y + 26);
    }

    private void drawPauseMenu(Graphics2D g2) {
        drawBackground(g2);
        drawTabs(g2);

        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 32f));
        g2.drawString("Game Paused", 60, 150);
        g2.setFont(g2.getFont().deriveFont(18f));
        g2.drawString("Press ESC to continue", 60, 185);

        exitButton = new Rectangle(gp.screenWidth / 2 - 100, 280, 200, 50);
        g2.setColor(new Color(140, 40, 40));
        g2.fillRoundRect(exitButton.x, exitButton.y, exitButton.width, exitButton.height, 12, 12);
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(exitButton.x, exitButton.y, exitButton.width, exitButton.height, 12, 12);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 20f));
        FontMetrics fm = g2.getFontMetrics();
        String label = "Exit to Main Menu";
        g2.drawString(label, exitButton.x + exitButton.width / 2 - fm.stringWidth(label) / 2, exitButton.y + 33);
    }

    public void handleClick(int mouseX, int mouseY) {

        if (gp.currentTab == 6 && passoutOkButton != null && passoutOkButton.contains(mouseX, mouseY)) {
            gp.currentTab = 0;
            return;
        }

        if (gp.currentTab == 3 && exitButton != null && exitButton.contains(mouseX, mouseY)) {
            gp.saveManager.save();
            gp.gameState = GameState.MAIN_MENU;
            gp.currentTab = 0;
            return;
        }

        // Furnace smelt slot selection
        if (gp.currentTab == 4 && smeltSlotRects != null) {
            for (int i = 0; i < smeltSlotRects.length; i++) {
                if (smeltSlotRects[i].contains(mouseX, mouseY)) {
                    selectedSmeltIndex = (selectedSmeltIndex == i) ? -1 : i;
                    return;
                }
            }
            if (smeltButton != null && smeltButton.contains(mouseX, mouseY) && selectedSmeltIndex >= 0 && !smeltingInProgress) {
                if (canAffordSmelt(selectedSmeltIndex)) {
                    int[][] recipe = smeltRecipes[selectedSmeltIndex];
                    for (int[] req : recipe) {
                        switch (req[0]) {
                            case 0: Player.stone   -= req[1]; break;
                            case 1: Player.wood    -= req[1]; break;
                            case 2: Player.coal    -= req[1]; break;
                            case 3: Player.ironOre -= req[1]; break;
                            case 4: Player.iron    -= req[1]; break;
                            case 5: Player.goldOre -= req[1]; break;
                            case 6: Player.gold    -= req[1]; break;
                            case 7: Player.furnace -= req[1]; break;
                            case 8: Player.stoneBed -= req[1]; break;
                        }
                    }
                    smeltingInProgress = true;
                    smeltingRecipeIndex = selectedSmeltIndex;
                    smeltStartTime = System.nanoTime();
                }
                return;
            }
        }

        if(gp.currentTab == 5){
            // Sleep screen buttons
            Rectangle yesButton = new Rectangle(70, 180, 100, 40);
            Rectangle noButton = new Rectangle(180, 180, 100, 40);
            if (yesButton.contains(mouseX, mouseY)) {
                // Player.health = Player.maxHealth;
                gp.t.setToMorning();
                gp.currentTab = 0;
                return;
            } else if (noButton.contains(mouseX, mouseY)) {
                gp.currentTab = 0;
                return;
            }
        }



        // NOTE: inventory clicks (pickup/placement) are now handled by
        // handleInventoryPress/Drag/Release below, wired to mouse press/drag/release
        // events rather than the plain click handler here.

        // Crafting slot selection
        if (gp.currentTab == 2 && craftSlotRects != null) {
            for (int i = 0; i < craftSlotRects.length; i++) {
                if (craftSlotRects[i].contains(mouseX, mouseY)) {
                    selectedCraftIndex = (selectedCraftIndex == i) ? -1 : i; // toggle
                    return;
                }
            }
            // Craft button
            if (craftButton != null && craftButton.contains(mouseX, mouseY) && selectedCraftIndex >= 0) {
                if (canAfford(selectedCraftIndex)) {
                    int[][] recipe = recipes[selectedCraftIndex];
                    // Deduct resources
                    for (int[] req : recipe) {
                        if (req[0] == 0) Player.stone -= req[1];
                        if (req[0] == 1) Player.wood  -= req[1];
                        if (req[0] == 2) Player.iron  -= req[1];
                    }
                    System.out.println("Crafted: " + selectedCraftIndex);

                    //add selected item to player inventory
                    // 0 = Furnace

                    if(selectedCraftIndex == 0){
                        Player.furnace++;
                    }else if(selectedCraftIndex == 1){
                        Player.stoneBed++;
                    }
                }
                return;
            }
        }

        if (tabRects == null) return;
        for (int i = 0; i < tabRects.length; i++) {
            if (tabRects[i] != null && tabRects[i].contains(mouseX, mouseY)) {
                gp.currentTab = i + 1;
                selectedCraftIndex = -1; // reset selection on tab switch
            }
        }
    }

    // --- Inventory drag-and-drop handlers ---
    // Wire these to mousePressed/mouseDragged/mouseReleased on gp, guarded by
    // gp.currentTab == 1, so they only run while the inventory screen is open.

    public void handleInventoryPress(int mx, int my) {
        if (gp.currentTab != 1 || inventorySlotRects == null) return;
        if (!slotsInit) initInventorySlots();

        for (int slot = 0; slot < inventorySlotRects.length; slot++) {
            if (inventorySlotRects[slot] != null && inventorySlotRects[slot].contains(mx, my)) {
                int itemIndex = slotItems[slot];
                if (itemIndex != -1) {
                    heldItemIndex = itemIndex;
                    heldFromSlot = slot;
                    mouseX = mx;
                    mouseY = my;
                }
                return;
            }
        }
    }

    public void handleInventoryDrag(int mx, int my) {
        if (heldItemIndex != -1) {
            mouseX = mx;
            mouseY = my;
        }
    }

    public void handleInventoryRelease(int mx, int my) {
        if (heldItemIndex == -1) return;

        int targetSlot = -1;
        for (int slot = 0; slot < inventorySlotRects.length; slot++) {
            if (inventorySlotRects[slot] != null && inventorySlotRects[slot].contains(mx, my)) {
                targetSlot = slot;
                break;
            }
        }

        if (targetSlot == -1) {
            // Dropped outside the grid — snap back to where it came from
            slotItems[heldFromSlot] = heldItemIndex;
        } else if (targetSlot == heldFromSlot) {
            // Released on the same slot — treat as a plain click (placement trigger)
            slotItems[targetSlot] = heldItemIndex;

            String itemName = inventoryItemNames[heldItemIndex];
            if (itemName.equals("Furnace")) {
                gp.startPlacement("furnace");
                gp.currentTab = 0; // Close inventory
            } else if (itemName.equals("Stone Bed")) {
                gp.startPlacement("stoneBed");
                gp.currentTab = 0; // Close inventory
            }
        } else {
            // Swap whatever is in the target slot (if anything) with the held item
            int existing = slotItems[targetSlot];
            slotItems[targetSlot] = heldItemIndex;
            slotItems[heldFromSlot] = existing; // existing is -1 if target was empty
        }

        heldItemIndex = -1;
        heldFromSlot = -1;
    }
    public int[] getSlotItems() {
        if (!slotsInit) initInventorySlots();
        return slotItems;
    }
    public void setSlotItems(int[] slots) {
        slotItems = slots.clone();
        slotsInit = true;
    }

    public void resetInventoryLayout() {
        slotsInit = false;
        initInventorySlots();
    }

    public void drawFurnaceUI(Graphics2D g2) {
        drawBackground(g2);

        int startX = 70;
        int startY = 110;
        int cellSize = 64;
        int gap = 10;
        int cols = 8;

        String[] names = {"Iron Bar", "Gold Bar"};
        smeltSlotRects = new Rectangle[names.length];
        // Load smelt icons inline
        // itemIcons[2] = iron, itemIcons[4] = gold
        BufferedImage[] icons = { itemIcons[4], itemIcons[6] };

        for (int i = 0; i < names.length; i++) {
            int col = i % cols;
            int row = i / cols;
            int x = startX + col * (cellSize + gap);
            int y = startY + row * (cellSize + gap);
            smeltSlotRects[i] = new Rectangle(x, y, cellSize, cellSize);

            // Highlight selected — same style as crafting
            if (i == selectedSmeltIndex) {
                g2.setColor(new Color(100, 100, 200, 220));
            } else {
                g2.setColor(new Color(60, 60, 60, 200));
            }
            g2.fillRoundRect(x, y, cellSize, cellSize, 8, 8);
            g2.setColor(i == selectedSmeltIndex ? Color.CYAN : new Color(180, 180, 180, 200));
            g2.drawRoundRect(x, y, cellSize, cellSize, 8, 8);

            if (icons[i] != null) {
                g2.drawImage(icons[i], x + 8, y + 8, cellSize - 16, cellSize - 16, null);
            } else {
                g2.setColor(Color.GRAY);
                g2.fillRect(x + 8, y + 8, cellSize - 16, cellSize - 16);
            }

            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 11f));
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawString(names[i], x + cellSize / 2 - fm.stringWidth(names[i]) / 2, y + cellSize + 14);
        }

        // --- Recipe panel ---
        if (selectedSmeltIndex >= 0) {
            drawSmeltRecipe(g2, selectedSmeltIndex);
        }
    }

    public void drawSleepScreen(Graphics2D g2) {
        drawBackground(g2);
        //ask user if they want to sleep, if they click yes, then set player health to max and time to morning
        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 24f));
        String msg = "Do you want to sleep to the morning?";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(msg, 70, 150);
        // Yes button
        Rectangle yesButton = new Rectangle(70, 180, 100, 40);
        g2.setColor(new Color(50, 140, 50));
        g2.fillRoundRect(yesButton.x, yesButton.y, yesButton.width, yesButton.height, 8, 8);
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(yesButton.x, yesButton.y, yesButton.width, yesButton.height, 8, 8);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 16f));
        g2.drawString("Yes", yesButton.x + yesButton.width / 2 - fm.stringWidth("Yes") / 2, yesButton.y + 26);
        // No button
        Rectangle noButton = new Rectangle(180, 180, 100, 40);
        g2.setColor(new Color(140, 40, 40));
        g2.fillRoundRect(noButton.x, noButton.y, noButton.width, noButton.height, 8, 8);
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(noButton.x, noButton.y, noButton.width, noButton.height, 8, 8);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 16f));
        g2.drawString("No", noButton.x + noButton.width / 2 - fm.stringWidth("No") / 2, noButton.y + 26);
    }




    public java.awt.Rectangle furnaceSmeltButton;
    private void drawSmeltRecipe(Graphics2D g2, int index) {
        int panelX = 70;
        int panelY = 280;
        int panelW = gp.screenWidth - 170;
        int panelH = 160;

        // Panel background
        g2.setColor(new Color(30, 30, 30, 220));
        g2.fillRoundRect(panelX, panelY, panelW, panelH, 10, 10);
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(panelX, panelY, panelW, panelH, 10, 10);

        // Title
        String[] names = {"Iron Bar", "Gold Bar"};
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 18f));
        g2.setColor(Color.WHITE);
        g2.drawString("Smelt: " + names[index], panelX + 12, panelY + 28);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 13f));
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawString("Required:", panelX + 12, panelY + 52);

        // Ingredient icons + counts
        int[][] recipe = smeltRecipes[index];
        int [] have = updatedHave();
        int iconSize = 40;
        int iconGap = 8;

        for (int i = 0; i < recipe.length; i++) {
            int iconIndex = recipe[i][0];
            int required  = recipe[i][1];
            int ix = panelX + 12 + i * (iconSize + iconGap + 60);
            int iy = panelY + 60;

            g2.setColor(new Color(50, 50, 50, 200));
            g2.fillRoundRect(ix, iy, iconSize, iconSize, 6, 6);
            g2.setColor(Color.GRAY);
            g2.drawRoundRect(ix, iy, iconSize, iconSize, 6, 6);

            if (itemIcons[iconIndex] != null) {
                g2.drawImage(itemIcons[iconIndex], ix + 4, iy + 4, iconSize - 8, iconSize - 8, null);
            }

            boolean enough = have[iconIndex] >= required;
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14f));
            g2.setColor(enough ? new Color(80, 220, 80) : new Color(220, 80, 80));
            g2.drawString(have[iconIndex] + "/" + required, ix + iconSize + 6, iy + 26);
        }

        // Smelt button
        //when user clicks smelt button check if they can afford the recipe, if yes deduct items and show the wait bar while item smelts for 5 seconds, then add the smelted item to player inventory
        //smelt button located at the bottom middle of the recipe panel
        boolean affordable = canAffordSmelt(index);
        //x = panelX + (panelW - buttonWidth) / 2
        smeltButton = new Rectangle(panelX + (panelW - 140) / 2, panelY + panelH - 55, 140, 40);

        g2.setColor(affordable ? new Color(50, 140, 50) : new Color(80, 80, 80));
        g2.fillRoundRect(smeltButton.x, smeltButton.y, smeltButton.width, smeltButton.height, 8, 8);
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(smeltButton.x, smeltButton.y, smeltButton.width, smeltButton.height, 8, 8);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 16f));
        FontMetrics fm = g2.getFontMetrics();
        String btnLabel = smeltingInProgress && smeltingRecipeIndex == index ? "Smelting..." : (affordable ? "Smelt" : "Need more");
        g2.drawString(btnLabel, smeltButton.x + smeltButton.width / 2 - fm.stringWidth(btnLabel) / 2, smeltButton.y + 26);

        if (smeltingInProgress && smeltingRecipeIndex == index) {
            //hide recipe rectangle while smelting bar is active
            g2.clearRect(panelX + 12, panelY + 60, panelW - 24, panelH - 115);

            float progress = getSmeltProgress();
            int barWidth = 220;
            int barHeight = 20;

            int barX = panelX + (panelW - 220) / 2;
            int barY = panelY + panelH - 90;

            g2.setColor(new Color(40, 40, 40, 220));
            g2.fillRoundRect(barX, barY, barWidth, barHeight, 8, 8);
            g2.setColor(new Color(80, 180, 250));
            g2.fillRoundRect(barX, barY, (int) (barWidth * progress), barHeight, 8, 8);
            g2.setColor(Color.WHITE);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14f));
            String progressLabel = "Smelting " + (int) (progress * 100) + "%";
            g2.drawString(progressLabel, barX + barWidth / 2 - fm.stringWidth(progressLabel) / 2, barY + barHeight - 2);
        }
    }

    private boolean canAffordSmelt(int index) {
        int[][] recipe = smeltRecipes[index];
        int[] have = updatedHave();
        for (int[] req : recipe) {
            if (have[req[0]] < req[1]) return false;
        }
        return true;
    }

    public void drawEnergyBar(java.awt.Graphics2D g2) {
        // Draws an energy bar in the top left corner of the screen using the current paint graphics.
        int barWidth = 100;
        int barHeight = 20;
        int x = 10;
        int y = 10;
        double energyPercent = (double) gp.player.energy / gp.player.maxEnergy;
        Color barColor;
        if (energyPercent > 0.66) {
            barColor = new Color(80, 220, 80);
        } else if (energyPercent > 0.33) {
            barColor = new Color(220, 220, 80);
        } else {
            barColor = new Color(220, 80, 80);
        }
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(x - 2, y - 2, barWidth + 4, barHeight + 4);
        g2.setColor(barColor);
        g2.fillRect(x, y, (int) (barWidth * energyPercent), barHeight);
    }

    public void drawPassoutScreen(Graphics2D g2) {
        drawBackground(g2);
        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 24f));
        String msg = "You passed out from exhaustion!";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(msg, 200, 150);
        //add okay button to return to game screen
        //center text and button

        Rectangle okButton = new Rectangle(330, 180, 100, 40);
        g2.setColor(new Color(50, 140, 50));
        g2.fillRoundRect(okButton.x, okButton.y, okButton.width, okButton.height, 8, 8);
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(okButton.x, okButton.y, okButton.width, okButton.height, 8, 8);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 16f));
        fm = g2.getFontMetrics();
        String btnLabel = "Okay";
        g2.drawString(btnLabel, okButton.x + okButton.width / 2 - fm.stringWidth(btnLabel) / 2, okButton.y + 26);
        passoutOkButton = okButton;
        //once user clicks okay set canMove to true and return to game screen

    }

    public void openBalinsShop(Graphics2D g2) {
        drawBackground(g2);
        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 24f));
        String msg = "Balins Shop -w Soon!";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(msg, 200, 150);
    }

    public void balinInteraction(Graphics2D g2) {
        //draw background
        try {
            BufferedImage inventoryBackground = javax.imageio.ImageIO.read(getClass().getResourceAsStream("/res/ui/balinInteraction.png"));
            if (inventoryBackground != null) {
                g2.drawImage(inventoryBackground, 120, 300, gp.screenWidth - 230, gp.screenHeight - 350, null);
            }
        } catch (Exception e) {
            drawBackground(g2);
        }
        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 18f));

        //button to skip dialogue, located at bottom right of dialogue box
        Rectangle skipButton = new Rectangle(gp.screenWidth - 375, gp.screenHeight - 100, 80, 30);

        g2.setColor(Color.WHITE);
        g2.fillRoundRect(skipButton.x, skipButton.y, skipButton.width, skipButton.height, 8, 8);
        g2.setColor(Color.BLACK);
        g2.drawRoundRect(skipButton.x, skipButton.y, skipButton.width, skipButton.height, 8, 8);
        g2.setColor(Color.BLACK);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14f));
        g2.drawString("Skip", skipButton.x + 10, skipButton.y + 22);
    


        
        //random message, 1/3 chance of each message, unless its players first time interacting with balin
        if(!gp.player.hasTalkedToBalin){
            String msg = "msg";
            try {
                InputStream is = getClass().getResourceAsStream("/res/dialogue/balin.json");
                if (is != null) {
                    // System.out.println("balin.json loaded successfully");
                    String jsonText = new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                        // lightweight JSON parsing to avoid external org.json dependency
                        // look for firstMeeting array and extract first string entry
                        String key = "\"firstMeeting\"";
                        int ki = jsonText.indexOf(key);
                        if (ki != -1) {
                            int arrStart = jsonText.indexOf('[', ki);
                            if (arrStart != -1) {
                                int strStart = jsonText.indexOf('"', arrStart + 1);
                                if (strStart != -1) {
                                    int strEnd = jsonText.indexOf('"', strStart + 1);
                                    if (strEnd != -1) {
                                        msg = jsonText.substring(strStart + 1, strEnd);
                                    }
                                }
                            }
                        }
                        //random message, 1/3 chance of each message
                        // else{
                        // }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            g2.drawString(msg, 150, 350);
            return;
        }
        int rand = (int) (Math.random() * 3);

    }

}