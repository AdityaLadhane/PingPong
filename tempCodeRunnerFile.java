import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class PongGame extends JPanel implements KeyListener, Runnable {
    // Screen dimensions
    private final int WIDTH = 800, HEIGHT = 600;

    // Ball properties
    private int ballX = WIDTH / 2, ballY = HEIGHT / 2, ballDiameter = 20;
    private int ballSpeedX = 3, ballSpeedY = 3;

    // Paddle properties
    private final int PADDLE_WIDTH = 20, PADDLE_HEIGHT = 100;
    private int leftPaddleY = HEIGHT / 2 - PADDLE_HEIGHT / 2, rightPaddleY = HEIGHT / 2 - PADDLE_HEIGHT / 2;
    private final int PADDLE_SPEED = 10;

    // Scores
    private int leftScore = 0, rightScore = 0;

    public PongGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw paddles
        g.setColor(Color.WHITE);
        g.fillRect(10, leftPaddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
        g.fillRect(WIDTH - 30, rightPaddleY, PADDLE_WIDTH, PADDLE_HEIGHT);

        // Draw ball
        g.fillOval(ballX, ballY, ballDiameter, ballDiameter);

        // Draw center line
        g.drawLine(WIDTH / 2, 0, WIDTH / 2, HEIGHT);

        // Draw scores
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString(String.valueOf(leftScore), WIDTH / 4, 50);
        g.drawString(String.valueOf(rightScore), WIDTH * 3 / 4, 50);
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
    }

    public void resetBall() {
        ballX = WIDTH / 2;
        ballY = HEIGHT / 2;
        ballSpeedX = -ballSpeedX; // Change direction after scoring
    }

    public void movePaddles(int keyCode, boolean isPressed) {
        if (keyCode == KeyEvent.VK_W && isPressed && leftPaddleY > 0) {
            leftPaddleY -= PADDLE_SPEED;
        } else if (keyCode == KeyEvent.VK_S && isPressed && leftPaddleY < HEIGHT - PADDLE_HEIGHT) {
            leftPaddleY += PADDLE_SPEED;
        }
        if (keyCode == KeyEvent.VK_UP && isPressed && rightPaddleY > 0) {
            rightPaddleY -= PADDLE_SPEED;
        } else if (keyCode == KeyEvent.VK_DOWN && isPressed && rightPaddleY < HEIGHT - PADDLE_HEIGHT) {
            rightPaddleY += PADDLE_SPEED;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        movePaddles(e.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        movePaddles(e.getKeyCode(), false);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void run() {
        while (true) {
            moveBall();
            repaint();
            try {
                Thread.sleep(10); // Controls the game speed
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Pong Game");
        PongGame pong = new PongGame();
        frame.add(pong);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        Thread gameThread = new Thread(pong);
        gameThread.start();
    }
}
