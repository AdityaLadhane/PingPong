import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class RetroPongGame extends JPanel implements KeyListener, Runnable {
    // Screen dimensions
    private final int WIDTH = 800, HEIGHT = 600;

    // Game states
    private enum GameState { START, COUNTDOWN, PLAYING, PAUSED, GAME_OVER }
    private GameState gameState = GameState.START;

    // Countdown
    private int countdown = 3;

    // Player names
    private String player1Name = "Player 1";
    private String player2Name = "Player 2";

    // Ball properties
    private int ballX = WIDTH / 2, ballY = HEIGHT / 2, ballDiameter = 20;
    private int ballSpeedX = 3, ballSpeedY = 3;

    // Paddle properties
    private final int PADDLE_WIDTH = 20, PADDLE_HEIGHT = 100;
    private int leftPaddleY = HEIGHT / 2 - PADDLE_HEIGHT / 2, rightPaddleY = HEIGHT / 2 - PADDLE_HEIGHT / 2;
    private final int PADDLE_SPEED = 5;

    // Key press tracking
    private boolean upPressed = false, downPressed = false, wPressed = false, sPressed = false;

    // Scores
    private int leftScore = 0, rightScore = 0;

    public RetroPongGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        // Prompt players to enter their names
        player1Name = JOptionPane.showInputDialog("Enter name for Player 1 (Left Paddle):");
        if (player1Name == null || player1Name.trim().isEmpty()) player1Name = "Player 1";

        player2Name = JOptionPane.showInputDialog("Enter name for Player 2 (Right Paddle):");
        if (player2Name == null || player2Name.trim().isEmpty()) player2Name = "Player 2";
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        switch (gameState) {
            case START:
                drawStartScreen(g);
                break;
            case COUNTDOWN:
                drawCountdown(g);
                break;
            case PLAYING:
                drawGameScreen(g);
                break;
            case PAUSED:
                drawPauseScreen(g);
                break;
            case GAME_OVER:
                drawGameOverScreen(g);
                break;
        }
    }

    private void drawStartScreen(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        g.drawString("RETRO PING PONG", WIDTH / 4, HEIGHT / 4);

        g.setFont(new Font("Arial", Font.PLAIN, 30));
        g.drawString("Press ENTER to Start", WIDTH / 3, HEIGHT / 2);
    }

    private void drawCountdown(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 100));
        g.drawString(String.valueOf(countdown), WIDTH / 2 - 30, HEIGHT / 2);
    }

    private void drawGameScreen(Graphics g) {
        // Draw paddles
        g.setColor(Color.WHITE);
        g.fillRect(10, leftPaddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
        g.fillRect(WIDTH - 30, rightPaddleY, PADDLE_WIDTH, PADDLE_HEIGHT);

        // Draw ball
        g.fillOval(ballX, ballY, ballDiameter, ballDiameter);

        // Draw center line
        g.drawLine(WIDTH / 2, 0, WIDTH / 2, HEIGHT);

        // Draw scores and player names
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString(player1Name + ": " + leftScore, WIDTH / 4 - 50, 50);
        g.drawString(player2Name + ": " + rightScore, WIDTH * 3 / 4 - 50, 50);
    }

    private void drawPauseScreen(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        g.drawString("PAUSED", WIDTH / 3, HEIGHT / 4);

        g.setFont(new Font("Arial", Font.PLAIN, 30));
        g.drawString("Press ENTER to Resume", WIDTH / 4, HEIGHT / 2);
    }

    private void drawGameOverScreen(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        g.drawString("GAME OVER", WIDTH / 3, HEIGHT / 4);

        String winner = leftScore > rightScore ? player1Name : player2Name;
        g.setFont(new Font("Arial", Font.PLAIN, 30));
        g.drawString("Winner: " + winner, WIDTH / 3, HEIGHT / 2);
        g.drawString("Press ENTER to Return to Main Menu", WIDTH / 6, HEIGHT / 2 + 50);
    }

    public void moveBall() {
        ballX += ballSpeedX;
        ballY += ballSpeedY;

        // Ball collision with top and bottom
        if (ballY <= 0 || ballY >= HEIGHT - ballDiameter) {
            ballSpeedY = -ballSpeedY;
        }

        // Ball collision with paddles
        if (new Rectangle(10, leftPaddleY, PADDLE_WIDTH, PADDLE_HEIGHT).intersects(ballX, ballY, ballDiameter, ballDiameter)) {
            ballSpeedX = -ballSpeedX;
        }
        if (new Rectangle(WIDTH - 30, rightPaddleY, PADDLE_WIDTH, PADDLE_HEIGHT).intersects(ballX, ballY, ballDiameter, ballDiameter)) {
            ballSpeedX = -ballSpeedX;
        }

        // Check for scoring
        if (ballX < 0) {
            rightScore++;
            resetBall();
        }
        if (ballX > WIDTH) {
            leftScore++;
            resetBall();
        }

        // Check if the game should end
        if (leftScore + rightScore >= 11) {
            gameState = GameState.GAME_OVER;
        }
    }

    public void resetBall() {
        ballX = WIDTH / 2;
        ballY = HEIGHT / 2;
        ballSpeedX = -ballSpeedX; // Change direction after scoring
    }

    public void movePaddles() {
        if (wPressed && leftPaddleY > 0) {
            leftPaddleY -= PADDLE_SPEED;
        }
        if (sPressed && leftPaddleY < HEIGHT - PADDLE_HEIGHT) {
            leftPaddleY += PADDLE_SPEED;
        }
        if (upPressed && rightPaddleY > 0) {
            rightPaddleY -= PADDLE_SPEED;
        }
        if (downPressed && rightPaddleY < HEIGHT - PADDLE_HEIGHT) {
            rightPaddleY += PADDLE_SPEED;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (gameState == GameState.START && key == KeyEvent.VK_ENTER) {
            gameState = GameState.COUNTDOWN;
            startCountdown();
        } else if (gameState == GameState.COUNTDOWN || gameState == GameState.PLAYING) {
            if (key == KeyEvent.VK_W) wPressed = true;
            if (key == KeyEvent.VK_S) sPressed = true;
            if (key == KeyEvent.VK_UP) upPressed = true;
            if (key == KeyEvent.VK_DOWN) downPressed = true;
        } else if (gameState == GameState.GAME_OVER && key == KeyEvent.VK_ENTER) {
            resetGame();
        }
    }

    private void startCountdown() {
        new Thread(() -> {
            while (countdown > 0) {
                repaint();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countdown--;
            }
            gameState = GameState.PLAYING;
        }).start();
    }

    private void resetGame() {
        leftScore = 0;
        rightScore = 0;
        countdown = 3;
        gameState = GameState.START;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W) wPressed = false;
        if (key == KeyEvent.VK_S) sPressed = false;
        if (key == KeyEvent.VK_UP) upPressed = false;
        if (key == KeyEvent.VK_DOWN) downPressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void run() {
        while (true) {
            if (gameState == GameState.PLAYING) {
                moveBall();
                movePaddles();
            }
            repaint();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Retro Ping Pong");
        RetroPongGame game = new RetroPongGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        new Thread(game).start();
    }
}
