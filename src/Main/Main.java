package Main;


import javax.swing.JFrame;


/*

    TODO
    dwarf mining/building 2d gam
    add animations for mining

*/
public class Main {
    public static void main(String[] args){
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        window.setSize(800, 600);
        window.setResizable(false);

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);

        window.pack();



        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gamePanel.startGameThread();
    }


}
