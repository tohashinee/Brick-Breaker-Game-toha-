/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.brickbreakergame;

/**
 *
 * @author User
 */
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
public class MapGenerator {
    public int map[][];
    public int brickWidth;
    public int brickHeight;
    
    // The constructor sets up rows and columns
    public MapGenerator(int row, int col) {
        map = new int[row][col];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = 1; // 1 means the brick is active and visible
            }
        }
        
        // Calculate dimensions based on the available upper screen space
        brickWidth = 540 / col;
        brickHeight = 150 / row;
    }
    
    // Draws the multi-colored brick matrix onto the screen canvas
    public void draw(Graphics2D g) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] > 0) {
                    
                    // Assign colors dynamically based on rows for a vibrant gradient look
                    if (i == 0) g.setColor(Color.red);
                    else if (i == 1) g.setColor(Color.orange);
                    else if (i == 2) g.setColor(Color.yellow);
                    else g.setColor(Color.cyan);

                    // Draw the solid brick shape
                    g.fillRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);

                    // Draw a black outline border around each brick to isolate them visually
                    g.setStroke(new BasicStroke(3));
                    g.setColor(Color.black);
                    g.drawRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
                }
            }
        }
    }

    // Changes the value to 0 when hit by the ball
    public void setBrickValue(int value, int row, int col) {
        map[row][col] = value;
    }

    
}
