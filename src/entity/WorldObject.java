package entity;

import tile.Tile;

public class WorldObject {
    public int worldX, worldY;
    public Tile tile;

    public WorldObject(Tile tile, int worldX, int worldY) {
        this.tile = tile;
        this.worldX = worldX;
        this.worldY = worldY;
    }
}