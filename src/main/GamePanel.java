package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable, KeyListener {

    private int score = 0;
    Thread thread;

    Random random = new Random();

    private boolean gotInput = false;

    private enum Direction {
        UP, DOWN, RIGHT, LEFT
    }
    Direction direction = Direction.RIGHT;

    private List<List<Integer>> tiles = new ArrayList<>(4);

    public GamePanel() {
        setPreferredSize(new Dimension(380, 480));
        addKeyListener(this);
        setFocusable(true);
        thread = new Thread(this);
        thread.start();
    }

    private void newGame() {
        for(int i = 0; i < 4; i++) {
            tiles.add(i, new ArrayList<>());
            for(int j = 0; j < 4; j++) {
                tiles.get(i).add(1);
            }
        }
        CreateTileWithCheckGameOver();
        gotInput = false;
    }

    private void restartGame() {
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                tiles.get(i).set(j, 1);
            }
        }
        score = 0;
        gotInput = false;
        CreateTileWithCheckGameOver();
    }

    @Override
    public void run() {
        newGame();
        while(thread != null) {
            if(gotInput) {
                logic();
                repaint();
                gotInput = false;
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void logic() {
        move();
        combine();
        CreateTileWithCheckGameOver();
    }

    private void setScore() {
        int highest = 1;
        for(List<Integer> row : tiles) {
            for(int i = 0; i < 4; i ++) {
                if(row.get(i) > highest) {
                    highest = row.get(i);
                }
            }
        }
        score = highest;
    }

    private void move() {
        if(direction == Direction.LEFT) {
            for (List<Integer> row : tiles) {
                for (int i = 1; i < 4; i++) {
                    if (row.get(i) != 1) {
                        for (int j = i - 1; j >= 0; j--) {
                            if (row.get(j) == 1) {
                                row.set(j, row.get(j + 1));
                                row.set(j + 1, 1);
                            }
                        }
                    }
                }
            }
        } else if(direction == Direction.RIGHT) {
            for (List<Integer> row : tiles) {

                horizontalFlip(row);

                for (int i = 1; i < 4; i++) {
                    if (row.get(i) != 1) {
                        for (int j = i - 1; j >= 0; j--) {
                            if (row.get(j) == 1) {
                                row.set(j, row.get(j + 1));
                                row.set(j + 1, 1);
                            }
                        }
                    }
                }

                horizontalFlip(row);
            }
        } else if (direction == Direction.UP) {
            for (int i = 0; i < 4; i++) {
                for (int j = 1; j < 4; j++) {
                    if (tiles.get(j).get(i) != 1) {
                        for (int k = j - 1; k >= 0; k--) {
                            if (tiles.get(k).get(i) == 1) {
                                tiles.get(k).set(i, tiles.get(k + 1).get(i));
                                tiles.get(k+1).set(i, 1);
                            }
                        }
                    }
                }

            }
        } else if (direction == Direction.DOWN) {
            for (int i = 0; i < 4; i++) {
                verticalFlip(i);
                for (int j = 1; j < 4; j++) {
                    if (tiles.get(j).get(i) != 1) {
                        for (int k = j - 1; k >= 0; k--) {
                            if (tiles.get(k).get(i) == 1) {
                                tiles.get(k).set(i, tiles.get(k + 1).get(i));
                                tiles.get(k+1).set(i, 1);
                            }
                        }
                    }
                }
                verticalFlip(i);
            }
        }
    }

    private void combine() {
        if(direction == Direction.LEFT) {
            for(List<Integer> row : tiles) {
                for(int i = 0; i < 3; i++) {
                    if(Objects.equals(row.get(i), row.get(i + 1)) && row.get(i) != 1) {
                        row.set(i, row.get(i) * 2);
                        row.set(i + 1, 1);
                    }
                }
            }
        } else if(direction == Direction.RIGHT) {
            for(List<Integer> row : tiles) {
                horizontalFlip(row);

                for(int i = 0; i < 3; i++) {
                    if(Objects.equals(row.get(i), row.get(i + 1)) && row.get(i) != 1) {
                        row.set(i, row.get(i) * 2);
                        row.set(i + 1, 1);
                    }
                }

                horizontalFlip(row);
            }
        } else if (direction == Direction.UP) {
            for(int i = 0; i < 4; i ++) {
                for(int j = 0; j < 3; j++) {
                    if(Objects.equals(tiles.get(j).get(i), tiles.get(j + 1).get(i)) && tiles.get(j).get(i) != 1) {
                        tiles.get(j).set(i, tiles.get(j).get(i) *2);
                        tiles.get(j+1).set(i, 1);
                    }
                }
            }
        } else if (direction == Direction.DOWN) {

            for(int i = 0; i < 4; i ++) {
                verticalFlip(i);
                for(int j = 0; j < 3; j++) {
                    if(Objects.equals(tiles.get(j).get(i), tiles.get(j + 1).get(i)) && tiles.get(j).get(i) != 1) {
                        tiles.get(j).set(i, tiles.get(j).get(i) *2);
                        tiles.get(j+1).set(i, 1);
                    }
                }
                verticalFlip(i);
            }
        }
        move();
    }

    private void verticalFlip(int i) {
        int t;
        t = tiles.get(0).get(i);
        tiles.get(0).set(i, tiles.get(3).get(i));
        tiles.get(3).set(i, t);
        t = tiles.get(1).get(i);
        tiles.get(1).set(i, tiles.get(2).get(i));
        tiles.get(2).set(i, t);
    }

    private void horizontalFlip(List<Integer> row) {
        int t;
        t = row.get(0);
        row.set(0, row.get(3));
        row.set(3, t);
        t = row.get(1);
        row.set(1, row.get(2));
        row.set(2, t);
    }

    private void CreateTileWithCheckGameOver() {

        for(List<Integer> row : tiles) {
            for(Integer num : row) {
                if(num == 1) {
                    newTile();
                    setScore();
                    return;
                }
            }
        }
        restartGame();
    }

    private void newTile() {
        int newTileX = random.nextInt(4);
        int newTileY = random.nextInt(4);
        if(tiles.get(newTileY).get(newTileX) == 1) {
            tiles.get(newTileY).set(newTileX, random.nextBoolean() ? 2 : 4);
        }
        else {
            newTile();
        }
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawLine(0, 100, 380, 100);
        g.drawLine(0, 195, 380, 195);
        g.drawLine(0, 290, 380, 290);
        g.drawLine(0, 385, 380, 385);

        g.drawLine(95, 100, 95, 480);
        g.drawLine(190, 100, 190, 480);
        g.drawLine(285, 100, 285, 480);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Consolas", Font.PLAIN, 50));
        g.drawString("Score: " + (score > 1 ? score : 0), 10, 60);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (tiles.get(i).get(j) != 1)
                    drawCenteredString(g , String.valueOf(tiles.get(i).get(j)), new Rectangle(95 * j, 106 + 95 * i, 95, 95), new Font("Consolas", Font.PLAIN, 50));
            }
        }
    }

    public void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        g.setFont(font);
        g.drawString(text, x, y);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(!gotInput) {
            switch(e.getKeyCode()) {
                case KeyEvent.VK_UP -> {
                    direction = Direction.UP;
                    gotInput = true;
                }
                case KeyEvent.VK_DOWN -> {
                    direction = Direction.DOWN;
                    gotInput = true;
                }
                case KeyEvent.VK_RIGHT -> {
                    direction = Direction.RIGHT;
                    gotInput = true;
                }
                case KeyEvent.VK_LEFT -> {
                    direction = Direction.LEFT;
                    gotInput = true;
                }
                default -> {

                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
    @Override
    public void keyTyped(KeyEvent e) {

    }
}