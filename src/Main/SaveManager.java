package Main;

import entity.Player;
import java.io.*;
import java.util.Properties;

public class SaveManager {

    private static final String SAVE_DIR = "saves/";
    GamePanel gp;
    public String currentSaveName = null;

    public SaveManager(GamePanel gp) {
        this.gp = gp;
        new File(SAVE_DIR).mkdirs(); // create saves folder if it doesn't exist
    }

    public void save() {
        if (currentSaveName == null) return;
        saveToSlot(currentSaveName);

    }

    public void saveToSlot(String name) {
        currentSaveName = name;
        Properties props = new Properties();

        //save world seed
        props.setProperty("worldSeed", String.valueOf(gp.worldSeed));

        //save time and date
        // props.setProperty("meridiem", gp.t.meridiem);
        props.setProperty("minute", String.valueOf(gp.t.minute));
        props.setProperty("hour",   String.valueOf(gp.t.hour));
        props.setProperty("day",    String.valueOf(gp.t.day));
        props.setProperty("month",  String.valueOf(gp.t.month));
        props.setProperty("year",   String.valueOf(gp.t.year));

        //save obj data
        // Save placed furnaces
        StringBuilder furnaceData = new StringBuilder();
        int furnaceCount = 0;
        for (entity.Item item : gp.items) {
            if (item instanceof entity.object.OBJ_Furnace) {
                if (furnaceCount > 0) furnaceData.append(";");
                furnaceData.append(item.worldX).append(",").append(item.worldY);
                furnaceCount++;
            }
        }
        props.setProperty("furnaces", furnaceData.toString());

        //save player data
        props.setProperty("playerX",   String.valueOf(gp.player.worldX));
        props.setProperty("playerY",   String.valueOf(gp.player.worldY));
        props.setProperty("direction", gp.player.direction);
        props.setProperty("stone",     String.valueOf(Player.stone));
        props.setProperty("wood",      String.valueOf(Player.wood));
        props.setProperty("iron",      String.valueOf(Player.iron));
        props.setProperty("ironOre",     String.valueOf(Player.ironOre));
        props.setProperty("gold",      String.valueOf(Player.gold));
        props.setProperty("goldOre",      String.valueOf(Player.goldOre));
        props.setProperty("coal", String.valueOf(Player.coal));
        
        props.setProperty("saveName",  name);
        props.setProperty("timestamp", new java.util.Date().toString());

        //set npc interaction flags
        props.setProperty("hasTalkedToBalin", String.valueOf(Player.hasTalkedToBalin));

        // Save inventory layout
        int[] slots = gp.ui.getSlotItems();
        StringBuilder slotData = new StringBuilder();

        for (int i = 0; i < slots.length; i++) {
            slotData.append(slots[i]);
            if (i < slots.length - 1) {
                slotData.append(",");
            }
        }

        props.setProperty("inventorySlots", slotData.toString());

        StringBuilder mapData = new StringBuilder();
        for (int col = 0; col < gp.maxWorldCol; col++) {
            for (int row = 0; row < gp.maxWorldRow; row++) {
                mapData.append(gp.tileM.mapTileNum[col][row]);
                if (row < gp.maxWorldRow - 1) mapData.append(",");
            }
            if (col < gp.maxWorldCol - 1) mapData.append(";");
        }
        props.setProperty("mapData", mapData.toString());

        try (FileOutputStream fos = new FileOutputStream(SAVE_DIR + name + ".properties")) {
            props.store(fos, "Dwarf Mountain Save");

            System.out.println("Saved to slot: " + name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromSlot(String name) {
        File file = new File(SAVE_DIR + name + ".properties");
        if (!file.exists()) return;

        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(file)) {
            props.load(fis);

            //get seed
            gp.worldSeed = Long.parseLong(props.getProperty("worldSeed", "0"));

            //set game time and date
            // gp.t.meridiem = props.getProperty("meridiem", "AM");
            gp.t.minute = Integer.parseInt(props.getProperty("minute", "0"));
            //Start at 5 am by default so player has time to prepare before night
            gp.t.hour = Integer.parseInt(props.getProperty("hour", "5"));
            gp.t.day = Integer.parseInt(props.getProperty("day", "0"));
            gp.t.month = Integer.parseInt(props.getProperty("month", "0"));
            gp.t.year = Integer.parseInt(props.getProperty("year", "0"));

            gp.player.worldX    = Integer.parseInt(props.getProperty("playerX"));
            gp.player.worldY    = Integer.parseInt(props.getProperty("playerY"));
            gp.player.direction = props.getProperty("direction");

            //player Inventory
            Player.stone        = Integer.parseInt(props.getProperty("stone"));
            Player.wood         = Integer.parseInt(props.getProperty("wood"));
            Player.iron         = Integer.parseInt(props.getProperty("iron"));
            Player.ironOre      = Integer.parseInt(props.getProperty("ironOre"));
            Player.gold         = Integer.parseInt(props.getProperty("gold"));
            Player.goldOre      = Integer.parseInt(props.getProperty("goldOre"));
            Player.coal         = Integer.parseInt(props.getProperty("coal"));

            //load npc interaction flags
            Player.hasTalkedToBalin = Boolean.parseBoolean(props.getProperty("hasTalkedToBalin", "false"));

            currentSaveName     = name;

            String mapData = props.getProperty("mapData");
            String[] cols = mapData.split(";");
            for (int col = 0; col < cols.length; col++) {
                String[] rows = cols[col].split(",");
                for (int row = 0; row < rows.length; row++) {
                    gp.tileM.mapTileNum[col][row] = Integer.parseInt(rows[row]);
                }
            }

            for (int col = 0; col < gp.maxWorldCol; col++) {
                for (int row = 0; row < gp.maxWorldRow; row++) {
                    int tileNum = gp.tileM.mapTileNum[col][row];
                    gp.tileM.tileHP[col][row] = gp.tileM.tile[tileNum].maxHP;
                }
            }

            // Clear existing placed furnaces
            for (int i = 0; i < gp.items.length; i++) {
                if (gp.items[i] instanceof entity.object.OBJ_Furnace) {
                    gp.items[i] = null;
                }
            }

            String slotData = props.getProperty("inventorySlots", "");

            if (!slotData.isEmpty()) {
                String[] values = slotData.split(",");
                int[] slots = new int[values.length];

                for (int i = 0; i < values.length; i++) {
                    slots[i] = Integer.parseInt(values[i]);
                }

                gp.ui.setSlotItems(slots);
            } else {
                gp.ui.resetInventoryLayout();
            }

            // Load placed furnaces
            String furnaceData = props.getProperty("furnaces", "");
            if (!furnaceData.isEmpty()) {
                for (String entry : furnaceData.split(";")) {
                    String[] parts = entry.split(",");
                    int fx = Integer.parseInt(parts[0]);
                    int fy = Integer.parseInt(parts[1]);
                    gp.spawnPlacedObject(new entity.object.OBJ_Furnace(), fx, fy);
                }
            }

            System.out.println("Loaded slot: " + name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    

    public String[] getSaveNames() {
        File dir = new File(SAVE_DIR);
        File[] files = dir.listFiles((d, n) -> n.endsWith(".properties"));
        if (files == null) return new String[0];
        String[] names = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            names[i] = files[i].getName().replace(".properties", "");
        }
        return names;
    }

    public String getTimestamp(String name) {
        File file = new File(SAVE_DIR + name + ".properties");
        if (!file.exists()) return "";
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(file)) {
            props.load(fis);
            return props.getProperty("timestamp", "");
        } catch (IOException e) {
            return "";
        }
    }

    public boolean saveExists() {
        return getSaveNames().length > 0;
    }

    public void deleteSave(String name) {
        new File(SAVE_DIR + name + ".properties").delete();
    }
}