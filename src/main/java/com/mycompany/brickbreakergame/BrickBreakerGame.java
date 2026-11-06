/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.brickbreakergame;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import java.awt.Rectangle;
import java.awt.Graphics2D;

/**
 *
 * @author User
 */
class GamePlay extends JPanel implements KeyListener,ActionListener{
    private int playerX = 310; //Paddle Horizontal Tracking Position
    private boolean play = false; // The game shouldn't start moving until a user presses an arrow key
    private int score = 0;
    private int totalBricks = 21; // 3 rows * 7 columns

    private MapGenerator map;
    
    private int ballposX = 350; // Starting X coordinate of the ball
    private int ballposY = 350; // Starting Y coordinate of the ball
    private int ballXdir = -1;  // X-axis speed: -1 means moving left, +1 means moving right
    private int ballYdir = -2;  // Y-axis speed: -2 means moving up, +2 means moving down

    private Timer timer;
    private int delay = 8; // The speed of the timer loop clock tick (8 milliseconds)

    public GamePlay(){
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        // Create a 3x7 structural grid layout
        map = new MapGenerator(3, 7);
        timer = new Timer(delay,this);
        timer.start();
    }
    
    @Override
    public void paint(Graphics g) {
        // Draw the Solid Black Background Canvas
        g.setColor(Color.black);
        g.fillRect(1, 1, 692, 592);
        
        // Draw the Yellow Visual Outer Canvas Borders
        g.setColor(Color.yellow);
        g.fillRect(0, 0, 3, 592);   // Left border
        g.fillRect(0, 0, 692, 3);   // Top border
        g.fillRect(691, 0, 3, 592); // Right border
        
        // Draw the Player's Green Paddle Control Rectangular Bar
        g.setColor(Color.green);
        g.fillRect(playerX, 550, 100, 8); 
        
        // Draw the Circular Pink Game Ball
        g.setColor(Color.pink);
        g.fillOval(ballposX, ballposY, 20, 20); 
        
        map.draw((Graphics2D)g);
        
        // Draw Score Counter Panel
        g.setColor(Color.white);
        g.drawString("Score: " + score, 590, 30);
        
        if (ballposY > 570) {
            play = false; // Freeze automatic game loop ticks
            ballXdir = 0;
            ballYdir = 0;
            
            g.setColor(Color.red);
            g.setFont(new java.awt.Font("serif", java.awt.Font.BOLD, 30));
            g.drawString("Game Over, Score: " + score, 190, 300);
            
            g.setColor(Color.white);
            g.setFont(new java.awt.Font("serif", java.awt.Font.BOLD, 20));
            g.drawString("Press Enter to Restart Game", 230, 350);
        }
        
        // 2. CONDITION: VICTORY STATE (All bricks shattered)
        if (totalBricks <= 0) {
            play = false;
            ballXdir = 0;
            ballYdir = 0;
            
            g.setColor(Color.green);
            g.setFont(new java.awt.Font("serif", java.awt.Font.BOLD, 30));
            g.drawString("🏆 VICTORY! Final Score: " + score, 170, 300);
            
            g.setColor(Color.white);
            g.setFont(new java.awt.Font("serif", java.awt.Font.BOLD, 20));
            g.drawString("Press Enter to Play Again", 230, 350);
        }
        
        g.dispose();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // If the game has started active gameplay movement
        if (play) {
            // Add the direction speeds to the ball's current coordinates
            ballposX += ballXdir;
            ballposY += ballYdir;
            
            // --- NEW FEATURE: PADDLE COLLISION DETECTION ---
            // We draw invisible structural rectangles over our shapes to check for impacts
            Rectangle ballRect = new Rectangle(ballposX, ballposY, 20, 20);
            Rectangle paddleRect = new Rectangle(playerX, 550, 100, 8);
            
            // If the ball's hitbox collides with the paddle's hitbox
            if (ballRect.intersects(paddleRect)) {
                ballYdir = -ballYdir; // Invert vertical direction -> Bounce Upward!
            }
            
            // --- WALL BOUNCE LOGIC ---
            if (ballposX < 0) {
                ballXdir = -ballXdir; // Hit Left Wall -> Bounce Right
            }
            if (ballposY < 0) {
                ballYdir = -ballYdir; // Hit Top Wall -> Bounce Down
            }
            if (ballposX > 670) {
                ballXdir = -ballXdir; // Hit Right Wall -> Bounce Left
            }
            
            // 3. NEW FEATURE: BRICK INTERSECTION ALGORITHM LOOP
            // Labeling the loop allows us to break out instantly on an impact registration
            A: for (int i = 0; i < map.map.length; i++) {
                for (int j = 0; j < map.map[0].length; j++) {
                    if (map.map[i][j] > 0) {
                        // Calculate the target brick's positioning bounds
                        int brickX = j * map.brickWidth + 80;
                        int brickY = i * map.brickHeight + 50;
                        int width = map.brickWidth;
                        int height = map.brickHeight;
                        
                        Rectangle brickRect = new Rectangle(brickX, brickY, width, height);
                        
                        if (ballRect.intersects(brickRect)) {
                            map.setBrickValue(0, i, j); // Shatter the brick!
                            totalBricks--;
                            score += 5;
                            
                            // Bounce Physics: Check side vs top impacts
                            if (ballposX + 19 <= brickRect.x || ballposX + 1 >= brickRect.x + brickRect.width) {
                                ballXdir = -ballXdir; // Horizontal deflection bounce
                            } else {
                                ballYdir = -ballYdir; // Vertical deflection bounce
                            }
                            
                            break A; // Halt loop evaluation to prevent double processing frames
                        }
                    }
                }
            }
        }
        
        // Force the canvas to erase and redraw our updated ball coordinates instantly!
        repaint();
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        // Check if the RIGHT arrow key was pressed
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            play = true;
            // Prevent the paddle from sliding off the right side of the screen
            if (playerX >= 580) {
                playerX = 580;
            } else {
                moveRight();
            }
        }
        
        // Check if the LEFT arrow key was pressed
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            play = true;
            // Prevent the paddle from sliding off the left side of the screen
            if (playerX <= 10) {
                playerX = 10;
            } else {
                moveLeft();
            }
        }
        
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!play) { // Only allow restart if the game is over or won
                play = true;
                ballposX = 350;
                ballposY = 350;
                ballXdir = -1;
                ballYdir = -2;
                playerX = 310;
                score = 0;
                totalBricks = 21;
                map = new MapGenerator(3, 7); // Remap a fresh brick matrix grid
                
                repaint();
            }
        }
    }
    
    public void moveRight() {
        playerX += 20; // Move 20 pixels to the right
        repaint();     // Force Java to erase and call the paint() method again!
    }

    public void moveLeft() {
        playerX -= 20; // Move 20 pixels to the left
        repaint();     // Force Java to erase and call the paint() method again!
    }

    // These two methods are required by KeyListener, but we can leave them blank!
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
    
}

public class BrickBreakerGame {

    public static void main(String[] args) {
        JFrame obj = new JFrame();
        GamePlay gamePlay = new GamePlay();
        
        obj.setBounds(10, 10, 700, 600); 
        obj.setTitle("Brick Breaker - Portfolio Project");
        obj.setResizable(false);
        obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        
        obj.add(gamePlay);
        obj.setVisible(true);
    
    }
}
