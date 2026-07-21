package tile;

import java.awt.image.BufferedImage;

public class Tile {
    public BufferedImage image;

    public boolean collision = false;
    public boolean breakable = false;
    public int layer = 0;
    public int width = 1;
    public int height = 1;



        public int maxHP = 3;   // total hits required to break
        public int currentHP;   // remaining hits

        public Tile() {
            currentHP = maxHP; // initialize
        }

        // Reset HP when tile is placed or loaded
        public void resetHP() {
            currentHP = maxHP;
        }

}
