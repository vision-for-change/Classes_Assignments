import java.awt.Color;

/**
 * represents the paddle controlled by the player.
 * its name avoids confusion with java's character type.
 */
public class GameCharacter extends GameObject implements Comparable<GameCharacter> {

    // these constants define the paddle dimensions and strength limit.
    public static final int PADDLE_WIDTH = 3;
    public static final int PADDLE_ROW = ROWS - 1;   // the bottom row
    public static final int MAX_STRENGTH = 3;
    private static int characterCount = 0;

    // strength controls damage, and position stores the paddle's left column.
    private int strength;
    private int position;
    private char name;
    private int lives;

    // shorter constructors supply defaults through the full constructor.
    public GameCharacter() {
        this('P');
    }

    public GameCharacter(char name) {
        this(name, 1);
    }

    public GameCharacter(char name, int strength) {
        this(name, strength, (COLS - PADDLE_WIDTH) / 2, 3);
    }

    public GameCharacter(char name, int strength, int position, int lives) {
        super(position, PADDLE_ROW, PADDLE_WIDTH, 1);
        this.name = name;
        this.strength = strength;
        this.position = position;
        this.lives = lives;
        characterCount++;
    }

    // these methods move the paddle and respond to game events.
    public void moveLeft() {
        if (position > 0) {
            setPosition(position - 1);
        }
    }

    public void moveRight() {
        if (position + PADDLE_WIDTH < COLS) {
            setPosition(position + 1);
        }
    }

    /** bounces a descending ball and gives it the paddle's strength. */
    public boolean hitBall(Ball ball) {
        if (ball.getDy() > 0 && contains(ball.getNextX(), ball.getNextY())) {
            ball.bounceY();
            ball.setPower(strength);

            // hitting either end also changes the ball's horizontal direction.
            int spot = ball.getNextX() - position;
            if (spot == 0) {
                ball.setDx(-1);
            } else if (spot == PADDLE_WIDTH - 1) {
                ball.setDx(1);
            }
            return true;
        }
        return false;
    }

    /** removes one life and reports whether the player is still alive. */
    public boolean loseLife() {
        if (lives > 0) {
            lives--;
        }
        return isAlive();
    }

    /** increases strength without passing its maximum. */
    public void powerUp() {
        if (strength < MAX_STRENGTH) {
            strength++;
        }
    }

    public boolean isAlive() {
        return lives > 0;
    }

    /** uses color to show the paddle's current strength. */
    @Override
    public Color getColor() {
        if (strength == 1) {
            return new Color(66, 133, 244);   // blue
        } else if (strength == 2) {
            return new Color(251, 188, 5);    // yellow
        } else {
            return new Color(234, 67, 53);    // red
        }
    }

    @Override
    public String describe() {
        return "Character '" + name + "' moves left/right and hits the ball";
    }

    // these methods expose the paddle state to the game window.
    public int getStrength() { return strength; }
    public void setStrength(int strength) {
        if (strength >= 1 && strength <= MAX_STRENGTH) {
            this.strength = strength;
        }
    }

    public int getPosition() { return position; }
    public void setPosition(int position) {
        this.position = position;
        setX(position); // keep the board position in sync with the paddle.
    }

    public char getName() { return name; }
    public void setName(char name) { this.name = name; }

    public int getLives() { return lives; }
    public void setLives(int lives) { this.lives = lives; }

    public static int getCharacterCount() { return characterCount; }

    // comparison uses strength first and lives second.
    @Override
    public String toString() {
        return "GameCharacter[name=" + name + ", strength=" + strength + ", position=" + position
                + ", lives=" + lives + "] " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameCharacter other = (GameCharacter) o;
        return name == other.name && strength == other.strength && lives == other.lives;
    }

    /** compares strength first and lives second. */
    @Override
    public int compareTo(GameCharacter other) {
        if (strength != other.strength) {
            return strength - other.strength;
        }
        return lives - other.lives;
    }
}
