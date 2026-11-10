import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * builds the game window and keeps its display in sync with the game state.
 * each turn moves the paddle first and then advances the ball by one cell.
 */
public class GameWindow extends JFrame implements ActionListener, KeyListener {

    private static final Color BACKGROUND = new Color(32, 33, 36);

    private final String playerName;

    // these objects hold the current positions and state of the game.
    private GameCharacter player;
    private Ball ball;
    // the twelve blocks form three rows with four blocks in each row.
    private Block b1, b2, b3, b4;
    private Block b5, b6, b7, b8;
    private Block b9, b10, b11, b12;

    private int score;
    private int blocksBroken;
    private boolean gameOver;

    // these components make up the status area, board, and controls.
    private JLabel statusLabel;
    private JPanel board;
    private JButton leftButton;
    private JButton stayButton;
    private JButton rightButton;
    private JButton newGameButton;

    public GameWindow(String playerName) {
        super("Block Breaker - " + playerName);
        this.playerName = playerName;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 650);
        setLayout(new BorderLayout());

        // the top area shows the score and player status.
        statusLabel = new JLabel();
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        add(statusLabel, BorderLayout.NORTH);

        // the center area shows one colored cell for each grid position.
        board = new JPanel(new GridLayout(GameObject.ROWS, GameObject.COLS));
        board.setBackground(BACKGROUND);
        add(board, BorderLayout.CENTER);

        // the bottom area contains the mouse controls and restart button.
        JPanel controls = new JPanel(new GridLayout(1, 4, 5, 5));
        controls.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        leftButton = makeButton("< Left");
        stayButton = makeButton("Stay");
        rightButton = makeButton("Right >");
        newGameButton = makeButton("New Game");
        controls.add(leftButton);
        controls.add(stayButton);
        controls.add(rightButton);
        controls.add(newGameButton);
        add(controls, BorderLayout.SOUTH);

        newGame();
        setLocationRelativeTo(null);
        setFocusable(true);
        addKeyListener(this);
        setVisible(true);
        requestFocusInWindow();
    }

    private JButton makeButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusable(false);
        button.addActionListener(this);
        return button;
    }

    /** makes a block for a row and column, with tougher blocks near the top. */
    private Block makeBlock(int row, int col) {
        // each lower row needs one fewer hit to break.
        int hp = 3 - row;
        return new Block(col * Block.BLOCK_WIDTH, row + 1, hp);
    }

    private void newGame() {
        // use the first letter of the saved player name on the paddle.
        player = new GameCharacter(playerName.charAt(0), 1);
        ball = new Ball();

        // reset the shared block count before creating this game's blocks.
        Block.resetBlocksRemaining();
        b1 = makeBlock(0, 0);  b2 = makeBlock(0, 1);  b3 = makeBlock(0, 2);  b4 = makeBlock(0, 3);
        b5 = makeBlock(1, 0);  b6 = makeBlock(1, 1);  b7 = makeBlock(1, 2);  b8 = makeBlock(1, 3);
        b9 = makeBlock(2, 0);  b10 = makeBlock(2, 1); b11 = makeBlock(2, 2); b12 = makeBlock(2, 3);

        score = 0;
        blocksBroken = 0;
        gameOver = false;
        setPlayButtons(true);
        updateBoard("Move the paddle to keep the ball up!");
    }

    private void setPlayButtons(boolean on) {
        // disable movement after the game ends, but leave restart available.
        leftButton.setEnabled(on);
        stayButton.setEnabled(on);
        rightButton.setEnabled(on);
    }

    /** handles a click on one of the game controls. */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newGameButton) {
            restart();
            return;
        }
        if (gameOver) {
            return;
        }

        // first move the paddle if a direction button was chosen.
        if (e.getSource() == leftButton) {
            player.moveLeft();
        } else if (e.getSource() == rightButton) {
            player.moveRight();
        }

        // then advance the ball and check for collisions.
        takeTurn();
    }

    private void takeTurn() {
        // stop checking after the first hit so only one block takes damage per turn.
        boolean hitBlock = checkBlock(b1) || checkBlock(b2) || checkBlock(b3) || checkBlock(b4)
                || checkBlock(b5) || checkBlock(b6) || checkBlock(b7) || checkBlock(b8)
                || checkBlock(b9) || checkBlock(b10) || checkBlock(b11) || checkBlock(b12);

        if (!hitBlock) {
            player.hitBall(ball);
        }
        // the ball updates its direction at walls before changing its position.
        ball.move();

        String message = "";
        boolean won = Block.getBlocksRemaining() == 0;
        if (won) {
            gameOver = true;
            message = "YOU WIN!";
        } else if (ball.isOutOfBounds()) {
            // the bottom is open, so a missed ball costs a life and returns to its start.
            ball.reset();
            if (player.loseLife()) {
                message = "You missed! Lives left: " + player.getLives();
            } else {
                gameOver = true;
                message = "GAME OVER!";
            }
        }
        if (gameOver) {
            setPlayButtons(false);
        }
        updateBoard(message);
        if (gameOver) {
            showEndScreen(won);
        }
    }

    private void showEndScreen(boolean won) {
        // keep the player's name for the replay and show the result in a dialog.
        String message = won
                ? "You won, " + playerName + "! Would you like to play again?"
                : "You lost, " + playerName + ". Would you like to play again?";
        int answer = JOptionPane.showConfirmDialog(
                this, message, "Game Over", JOptionPane.YES_NO_OPTION,
                won ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
        if (answer == JOptionPane.YES_OPTION) {
            restart();
        } else {
            dispose();
        }
    }

    private void restart() {
        // start a fresh window without asking for the player's name again.
        dispose();
        new GameWindow(playerName);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // a and d move the paddle and count as a full game turn.
        if (gameOver) {
            return;
        }
        if (e.getKeyCode() == KeyEvent.VK_A) {
            player.moveLeft();
        } else if (e.getKeyCode() == KeyEvent.VK_D) {
            player.moveRight();
        } else {
            return;
        }
        takeTurn();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    /** checks whether the ball is about to hit this block. */
    private boolean checkBlock(Block b) {
        if (b.hitBy(ball)) {
            if (b.isBroken()) {
                score = score + b.getPoints();
                blocksBroken++;
                if (blocksBroken % 4 == 0) {
                    // reward every fourth broken block with more paddle strength.
                    player.powerUp();
                }
            }
            return true;
        }
        return false;
    }

    /** finds the unbroken block that covers a grid cell. */
    private Block blockAt(int col, int row) {
        if (!b1.isBroken() && b1.contains(col, row)) return b1;
        if (!b2.isBroken() && b2.contains(col, row)) return b2;
        if (!b3.isBroken() && b3.contains(col, row)) return b3;
        if (!b4.isBroken() && b4.contains(col, row)) return b4;
        if (!b5.isBroken() && b5.contains(col, row)) return b5;
        if (!b6.isBroken() && b6.contains(col, row)) return b6;
        if (!b7.isBroken() && b7.contains(col, row)) return b7;
        if (!b8.isBroken() && b8.contains(col, row)) return b8;
        if (!b9.isBroken() && b9.contains(col, row)) return b9;
        if (!b10.isBroken() && b10.contains(col, row)) return b10;
        if (!b11.isBroken() && b11.contains(col, row)) return b11;
        if (!b12.isBroken() && b12.contains(col, row)) return b12;
        return null;
    }

    /** finds the visible game object at a grid cell. */
    private GameObject objectAt(int col, int row) {
        if (ball.contains(col, row)) return ball;
        if (player.contains(col, row)) return player;
        return blockAt(col, row);
    }

    /** rebuilds the colored grid to show the current positions of all objects. */
    private void updateBoard(String message) {
        board.removeAll();
        for (int row = 0; row < GameObject.ROWS; row++) {
            for (int col = 0; col < GameObject.COLS; col++) {
                JButton cell = new JButton();
                cell.setFocusable(false);
                cell.setContentAreaFilled(false);
                cell.setOpaque(true);
                cell.setBorder(BorderFactory.createLineBorder(BACKGROUND, 1));

                GameObject obj = objectAt(col, row);
                if (obj == null) {
                    cell.setBackground(BACKGROUND);
                } else {
                    cell.setBackground(obj.getColor());
                }
                if (obj == player && col == player.getPosition() + 1) {
                    cell.setText("" + player.getName());
                    cell.setFont(new Font("SansSerif", Font.BOLD, 14));
                }
                board.add(cell);
            }
        }
        board.revalidate();
        board.repaint();

        statusLabel.setText("Score: " + score + "   Lives: " + player.getLives()
                + "   Strength: " + player.getStrength() + "   " + message);
    }
}
