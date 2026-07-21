package entity;

import Main.GamePanel;
import java.awt.Rectangle;

public class Door {

    public int worldX, worldY;
    public String targetMap;
    public int targetCol, targetRow;
    public String returnMap;
    public int returnCol, returnRow;
    public Rectangle bounds;
    public boolean isExit; // true = exits building, false = enters

    public Door(int worldX, int worldY, String targetMap, int targetCol, int targetRow,
                String returnMap, int returnCol, int returnRow, boolean isExit) {
        this.worldX = worldX;
        this.worldY = worldY;
        this.targetMap = targetMap;
        this.targetCol = targetCol;
        this.targetRow = targetRow;
        this.returnMap = returnMap;
        this.returnCol = returnCol;
        this.returnRow = returnRow;
        this.isExit = isExit;
        this.bounds = new Rectangle(worldX, worldY, 48, 48);
    }
}