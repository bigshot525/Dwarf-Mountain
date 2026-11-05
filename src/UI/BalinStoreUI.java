package UI;

import Main.GamePanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

/** Owns the rendering and input state for Balin's shop screen. */
public class BalinStoreUI {
    private final GamePanel gp;
    private final List<ShopItem> items = new ArrayList<>();
    private BufferedImage background;
    private BufferedImage selectionLeft;
    private BufferedImage selectionRight;
    private Rectangle leftArrow;
    private Rectangle rightArrow;
    private int page;
    private int itemCount;
    private Boolean currentSide;
    public String itemCountMessage;

    private static class ShopItem {
        final String name;
        final int quantity;
        final int price;
        final BufferedImage icon;

        ShopItem(String name, int quantity, int price, BufferedImage icon) {
            this.name = name;
            this.quantity = quantity;
            this.price = price;
            this.icon = icon;
        }
    }

    public BalinStoreUI(GamePanel gp) {
        this.gp = gp;
        loadImages();
        loadItems();
    }

    private void loadImages() {
        background = loadImage("/res/UI/Balin_Store.png");
        selectionLeft = loadImage("/res/UI/Balin_Store_SelectionL.png");
        selectionRight = loadImage("/res/UI/Balin_Store_SelectionR.png");
    }

    private BufferedImage loadImage(String path) {
        try (InputStream stream = getClass().getResourceAsStream(path)) {
            return stream == null ? null : ImageIO.read(stream);
        } catch (Exception e) {
            return null;
        }
    }

    private void loadItems() {
        try (InputStream stream = getClass().getResourceAsStream("/res/Shop_Inventories/Balin_Shop.json")) {
            if (stream == null) return;
            String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            Pattern pattern = Pattern.compile("\\\"([^\\\"]+)\\\"\\s*:\\s*\\{\\s*\\\"quantity\\\"\\s*:\\s*(\\d+)\\s*,\\s*\\\"price\\\"\\s*:\\s*(\\d+)\\s*\\}");
            Matcher matcher = pattern.matcher(json);
            while (matcher.find()) {
                String name = matcher.group(1);
                BufferedImage icon = loadImage("/res/objects/" + name + ".png");
                items.add(new ShopItem(name, Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(3)), icon));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
        updateArrowHitboxes();
        int mouseX = gp.mouseScreenX;
        int mouseY = gp.mouseScreenY;
        BufferedImage currentBackground = background;
        if (leftArrow.contains(mouseX, mouseY) && selectionLeft != null) {
            currentBackground = selectionLeft;
            currentSide = true;
        } else if (rightArrow.contains(mouseX, mouseY) && selectionRight != null) {
            currentBackground = selectionRight;
            currentSide = false;
        } else {
            currentSide = null;
        }

        if (currentBackground != null) {
            g2.drawImage(currentBackground, 25, 25, gp.screenWidth - 50, gp.screenHeight - 50, null);
        }
        drawItemCards(g2);
        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 40f));
        g2.drawString(String.valueOf(itemCount), gp.screenWidth - 165, 380);
    }

    private void drawItemCards(Graphics2D g2) {
        int cardWidth = Math.min(320, gp.screenWidth - 250);
        int cardHeight = 100;
        int gap = 16;
        int x = 120;
        int startY = 60;
        for (int i = 0; i < items.size(); i++) {
            ShopItem item = items.get(i);
            int y = startY + i * (cardHeight + gap);
            g2.setColor(new Color(45, 35, 25, 220));
            g2.fillRoundRect(x, y, cardWidth, cardHeight, 16, 16);
            g2.setColor(new Color(220, 190, 130));
            g2.drawRoundRect(x, y, cardWidth, cardHeight, 16, 16);
            g2.setColor(Color.WHITE);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 18f));
            g2.drawString(item.name, x + 20, y + 30);
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 14f));
            g2.drawString("In stock: " + item.quantity, x + 20, y + 56);
            g2.drawString("Price: " + item.price, x + 20, y + 80);
            if (item.icon != null) g2.drawImage(item.icon, x + cardWidth - 76, y + 12, 64, 64, null);
        }
    }

    public void handleClick(int mouseX, int mouseY) {
        updateArrowHitboxes();
        if (leftArrow.contains(mouseX, mouseY)) page = Math.max(0, page - 1);
        else if (rightArrow.contains(mouseX, mouseY)) page++;
    }

    private void updateArrowHitboxes() {
        leftArrow = new Rectangle(gp.screenWidth - 270, 330, 50, 80);
        rightArrow = new Rectangle(gp.screenWidth - 100, 330, 50, 80);
    }

    public void updateCount() {
        if (currentSide == null) return;
        if (currentSide) {
            if (itemCount == 0) return;
            itemCount--;
        } else {
            itemCount++;
        }
        itemCountMessage = String.valueOf(itemCount);
    }
}
