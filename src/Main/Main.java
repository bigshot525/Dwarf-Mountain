package Main;


import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame();
        GamePanel gamePanel = new GamePanel();

        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // handle it manually
        window.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (gamePanel.gameState == GameState.PLAYING) {
                    gamePanel.saveManager.save(); // save before closing
                }
                System.exit(0);
            }
        });

        window.add(gamePanel);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        gamePanel.startGameThread();
    }


}
