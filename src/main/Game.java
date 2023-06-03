package main;

import javax.swing.*;

public class Game extends JFrame{

    Game() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("2048 crappy off-brand");
        setResizable(false);
        add(new GamePanel());
        pack();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Game();
    }

}