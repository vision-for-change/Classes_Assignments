import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/** starts the game and asks for the player's name. */
public class BlockBreakerTest {

    public static void main(String[] args) {
        // create the window on swing's event thread.
        SwingUtilities.invokeLater(BlockBreakerTest::startGame);
    }

    public static void startGame() {
        // canceling this first prompt exits without opening a game window.
        String name = JOptionPane.showInputDialog(
                null, "What is your name?", "Block Breaker", JOptionPane.QUESTION_MESSAGE);
        if (name == null) {
            return;
        }

        name = name.trim();
        if (name.isEmpty()) {
            name = "Player";
        }
        new GameWindow(name);
    }
}
