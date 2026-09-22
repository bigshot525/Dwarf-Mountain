package UI.InteractionUIs;
import Main.GamePanel;
import UI.UI;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.InputStream;
public class BalinInteractionUI{

    private GamePanel gp;
    private UI ui;

    public BalinInteractionUI(GamePanel gp, UI ui) {
        this.gp = gp;
        this.ui = ui;
    }



    public void balinInteraction(Graphics2D g2) {
        //check if player has already talked to balin twice today, if so, dont let player interact with balin again until tomorrow
        if(ui.dailyBalinDialogueIndex >= 2){
            return;
        }

        // Draw background
        try {
            BufferedImage inventoryBackground = javax.imageio.ImageIO.read(
                getClass().getResourceAsStream("/res/ui/balinInteraction.png")
            );

            if (inventoryBackground != null) {
                g2.drawImage(inventoryBackground, 120, 300, gp.screenWidth - 230, gp.screenHeight - 350, null);
            }
        } catch (Exception e) {
            ui.drawBackground(g2);
        }

        String msg = "msg";

        try {
            InputStream is = getClass().getResourceAsStream("/res/dialogue/balin.json");

            if (is != null) {
                String jsonText = new String(
                    is.readAllBytes(),
                    java.nio.charset.StandardCharsets.UTF_8
                );

                if (!gp.player.hasTalkedToBalin) {
                    // Find the firstMeeting array
                    String key = "\"firstMeeting\"";
                    int keyIndex = jsonText.indexOf(key);

                    if (keyIndex != -1) {
                        int arrayStart = jsonText.indexOf('[', keyIndex);
                        int arrayEnd = jsonText.indexOf(']', arrayStart);

                        if (arrayStart != -1 && arrayEnd != -1) {
                            String dialogueArray = jsonText.substring(arrayStart + 1, arrayEnd);

                            // Extract every quoted string from the array
                            java.util.ArrayList<String> lines = new java.util.ArrayList<>();

                            boolean insideString = false;
                            StringBuilder currentLine = new StringBuilder();

                            for (int i = 0; i < dialogueArray.length(); i++) {
                                char c = dialogueArray.charAt(i);

                                if (c == '"' && (i == 0 || dialogueArray.charAt(i - 1) != '\\')) {
                                    if (insideString) {
                                        lines.add(currentLine.toString());
                                        currentLine.setLength(0);
                                        insideString = false;
                                    } else {
                                        insideString = true;
                                    }
                                } else if (insideString) {
                                    currentLine.append(c);
                                }
                            }

                            // Make sure the index is valid
                            if (!lines.isEmpty()) {
                                if (ui.balinDialogueIndex >= lines.size()) {
                                    ui.balinDialogueIndex = lines.size() - 1;
                                }

                                msg = lines.get(ui.balinDialogueIndex);
                            }
                        }
                    }

                    // Draw first-meeting dialogue with text wrapping
                    g2.setColor(Color.WHITE);
                    g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 18f));

                    int maxWidth = 305;
                    int textX = 150;
                    int textY = 350;
                    int lineHeight = 25;

                    java.util.List<String> wrappedLines = ui.wrapText(msg, maxWidth, g2);

                    for (String line : wrappedLines) {
                        g2.drawString(line, textX, textY);
                        textY += lineHeight;
}

                }else {
                    // Regular dialogue
                    if (ui.balinRegularDialogue.isEmpty()) {
                        String key = "\"regularDialogue\"";
                        int keyIndex = jsonText.indexOf(key);

                        if (keyIndex != -1) {
                            int arrayStart = jsonText.indexOf('[', keyIndex);

                            if (arrayStart != -1) {
                                int depth = 0;
                                int arrayEnd = -1;

                                for (int i = arrayStart; i < jsonText.length(); i++) {
                                    char c = jsonText.charAt(i);

                                    if (c == '[') {
                                        depth++;
                                    } else if (c == ']') {
                                        depth--;

                                        if (depth == 0) {
                                            arrayEnd = i;
                                            break;
                                        }
                                    }
                                }

                                if (arrayEnd != -1) {
                                    String dialogueArray = jsonText.substring(arrayStart + 1, arrayEnd);

                                    java.util.ArrayList<String> lines = new java.util.ArrayList<>();

                                    boolean insideString = false;
                                    StringBuilder currentLine = new StringBuilder();

                                    for (int i = 0; i < dialogueArray.length(); i++) {
                                        char c = dialogueArray.charAt(i);

                                        if (c == '"' && (i == 0 || dialogueArray.charAt(i - 1) != '\\')) {
                                            if (insideString) {
                                                lines.add(currentLine.toString());
                                                currentLine.setLength(0);
                                                insideString = false;
                                            } else {
                                                insideString = true;
                                            }
                                        } else if (insideString) {
                                            currentLine.append(c);
                                        }
                                    }

                                    if (!lines.isEmpty()) {
                                        int rand = (int) (Math.random() * lines.size());
                                        ui.balinRegularDialogue = lines.get(rand);
                                    }
                                }
                            }
                        }
                    }

                    msg = ui.balinRegularDialogue;

                    g2.setColor(Color.WHITE);
                    g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 18f));

                    int maxWidth = 305;
                    java.util.List<String> wrappedLines = ui.wrapText(msg, maxWidth, g2);

                    int textX = 150;
                    int textY = 350;
                    int lineHeight = 25;

                    for (String line : wrappedLines) {
                        g2.drawString(line, textX, textY);
                        textY += lineHeight;
                    }
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Next / Close button
        ui.skipButton = new Rectangle(
            gp.screenWidth - 375,
            gp.screenHeight - 100,
            80,
            30
        );

        g2.setColor(Color.WHITE);
        g2.fillRoundRect(
            ui.skipButton.x,
            ui.skipButton.y,
            ui.skipButton.width,
            ui.skipButton.height,
            8,
            8
        );

        g2.setColor(Color.BLACK);
        g2.drawRoundRect(
            ui.skipButton.x,
            ui.skipButton.y,
            ui.skipButton.width,
            ui.skipButton.height,
            8,
            8
        );

        g2.setColor(Color.BLACK);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14f));

        if (!gp.player.hasTalkedToBalin && ui.balinDialogueIndex >= 5) {
            g2.drawString("Close", ui.skipButton.x + 10, ui.skipButton.y + 22);
        } else {
            g2.drawString("Next", ui.skipButton.x + 10, ui.skipButton.y + 22);
        }
    }


}

