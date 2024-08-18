import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

// snakegame class extends jpanel to include game logic and visuals
public class SnakeGame extends JPanel {

    // constants for board dimensions, dot size, and game settings
    private static final int BOARD_WIDTH = 300;
    private static final int BOARD_HEIGHT = 300;
    private static final int DOT_SIZE = 10;
    private static final int ALL_DOTS = 900;
    private static final int RAND_POS = 29;
    private static final int DELAY = 140;

    // arrays to store x and y coordinates of the snake's segments
    private final int[] x = new int[ALL_DOTS];
    private final int[] y = new int[ALL_DOTS];

    // track snake length and apple's position
    private int dots;
    private int apple_x;
    private int apple_y;

    // monitor snake direction and game state
    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean inGame = true;

    // timer for game loop control, and images for the snake and apple
    private Timer timer;
    private Image ball;
    private Image apple;
    private Image head;

    public SnakeGame() {
        initBoard();
    }

    private void initBoard() {
        addKeyListener(new TAdapter());  // attach a key listener for keyboard controls
        setBackground(Color.black);      // set the background color to black
        setFocusable(true);    // allow the panel to receive key events

        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT)); 
        loadImages();
        initGame();
    }

    // loads the images for the snake and apple
    private void loadImages() {
        ImageIcon iid = new ImageIcon("/resources/dot.png");
        ball = iid.getImage();

        ImageIcon iia = new ImageIcon("/resources/apple.png");
        apple = iia.getImage();

        ImageIcon iih = new ImageIcon("/resources/head.png");
        head = iih.getImage();
    }

    // sets the initial game state and starts the game loop
    private void initGame() {
        dots = 3;  

        // place the snake's starting position on the board
        for (int z = 0; z < dots; z++) {
            x[z] = 50 - z * 10;
            y[z] = 50;
        }

        locateApple();  

        // create a timer to handle the game loop
        timer = new Timer(DELAY, new GameCycle());
        timer.start();  
    }

    // randomly places the apple on the board
    private void locateApple() {
        int r = (int) (Math.random() * RAND_POS);
        apple_x = ((r * DOT_SIZE));

        r = (int) (Math.random() * RAND_POS);
        apple_y = ((r * DOT_SIZE));
    }

    // checks if the snake has eaten the apple
    private void checkApple() {
        if ((x[0] == apple_x) && (y[0] == apple_y)) {
            dots++;          
            locateApple(); 
        }
    }

    // moves the snake based on the current direction
    private void move() {
        // shift the position of each segment of the snake
        for (int z = dots; z > 0; z--) {
            x[z] = x[(z - 1)];
            y[z] = y[(z - 1)];
        }

        // move the head of the snake in the specified direction
        if (leftDirection) {
            x[0] -= DOT_SIZE;
        }

        if (rightDirection) {
            x[0] += DOT_SIZE;
        }

        if (upDirection) {
            y[0] -= DOT_SIZE;
        }

        if (downDirection) {
            y[0] += DOT_SIZE;
        }
    }

    // checks for collisions with the snake itself or the board edges
    private void checkCollision() {
        // check if the snake's head collides with its body
        for (int z = dots; z > 0; z--) {
            if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
                inGame = false;  // end the game if a collision occurs
            }
        }

        // check if the snake hits the edges of the board
        if (y[0] >= BOARD_HEIGHT) {
            inGame = false;
        }

        if (y[0] < 0) {
            inGame = false;
        }

        if (x[0] >= BOARD_WIDTH) {
            inGame = false;
        }

        if (x[0] < 0) {
            inGame = false;
        }

        // stop the game if the snake collides with anything
        if (!inGame) {
            timer.stop();
        }
    }

    // draws the game elements on the board
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // draw the snake and apple if the game is still going
        if (inGame) {
            g.drawImage(apple, apple_x, apple_y, this);

            for (int z = 0; z < dots; z++) {
                if (z == 0) {
                    g.drawImage(head, x[z], y[z], this);
                } else {
                    g.drawImage(ball, x[z], y[z], this);
                }
            }

            Toolkit.getDefaultToolkit().sync();  // synchronize for smoother  animation

        } else {
            gameOver(g);  // display game over message when the game ends
        }
    }

    // displays game over message on the screen
    private void gameOver(Graphics g) {
        String msg = "game over";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(msg, (BOARD_WIDTH - metr.stringWidth(msg)) / 2, BOARD_HEIGHT / 2);  // center the message
    }

    // handles the game loop updates
    private class GameCycle implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            if (inGame) {
                checkApple();
                checkCollision(); 
                move();
            }

            repaint();
        }
    }

    // handles keyboard inputs for controlling the snake
    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            // change the snake's direction based on the arrow key pressed
            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame ex = new JFrame();  
            ex.add(new SnakeGame());   
            ex.setResizable(false);  
            ex.pack();
            ex.setTitle("Snake");
            ex.setLocationRelativeTo(null);
            ex.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ex.setVisible(true);     
        });
    }
}
