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

        props.setProperty("playerX",   String.valueOf(gp.player.worldX));
        props.setProperty("playerY",   String.valueOf(gp.player.worldY));
        props.setProperty("direction", gp.player.direction);
        props.setProperty("stone",     String.valueOf(Player.stone));
        props.setProperty("wood",      String.valueOf(Player.wood));
        props.setProperty("iron",      String.valueOf(Player.iron));
        props.setProperty("saveName",  name);
        props.setProperty("timestamp", new java.util.Date().toString());

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

            gp.player.worldX    = Integer.parseInt(props.getProperty("playerX"));
            gp.player.worldY    = Integer.parseInt(props.getProperty("playerY"));
            gp.player.direction = props.getProperty("direction");
            Player.stone        = Integer.parseInt(props.getProperty("stone"));
            Player.wood         = Integer.parseInt(props.getProperty("wood"));
            Player.iron         = Integer.parseInt(props.getProperty("iron"));
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