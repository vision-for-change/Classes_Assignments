import java.awt.Color;

/** represents the ball and its direction of travel. */
public class Ball extends GameObject implements Comparable<Ball> {

    // these constants define the ball's starting position.
    public static final int START_X = 5;
    public static final int START_Y = 10;
    private static int ballCount = 0;

    // direction values are one cell per turn, and power is block damage.
    private int dx;
    private int dy;
    private int power;

    // shorter constructors supply default direction and power values.
    public Ball() {
        this(START_X, START_Y);
    }

    public Ball(int x, int y) {
        this(x, y, 1, -1);
    }

    public Ball(int x, int y, int dx, int dy) {
        this(x, y, dx, dy, 1);
    }

    public Ball(int x, int y, int dx, int dy, int power) {
        super(x, y, 1, 1);
        this.dx = dx;
        this.dy = dy;
        this.power = power;
        ballCount++;
    }

    /** moves one cell and bounces off the side and top walls. */
    public void move() {
        // reverse direction before moving if the next cell is beyond a wall.
        if (getX() + dx < 0 || getX() + dx >= COLS) {
            bounceX();
        }
        if (getY() + dy < 0) {
            bounceY();
        }
        moveBy(dx, dy);
    }

    public void bounceX() {
        dx = -dx;
    }

    public void bounceY() {
        dy = -dy;
    }

    /** restores the starting position, direction, and power. */
    public void reset() {
        moveTo(START_X, START_Y);
        dx = 1;
        dy = -1;
        power = 1;
    }

    /** finds the next column, accounting for a side wall bounce. */
    public int getNextX() {
        if (getX() + dx < 0 || getX() + dx >= COLS) {
            return getX() - dx;
        }
        return getX() + dx;
    }

    /** finds the next row, accounting for a top wall bounce. */
    public int getNextY() {
        if (getY() + dy < 0) {
            return getY() - dy;
        }
        return getY() + dy;
    }

    public boolean isOutOfBounds() {
        return getY() >= ROWS;
    }

    @Override
    public Color getColor() {
        return Color.WHITE;
    }

    @Override
    public String describe() {
        return "Ball moving (" + dx + ", " + dy + ") with power " + power;
    }

    // these methods expose the ball's direction and damage power.
    public int getDx() { return dx; }
    public void setDx(int dx) { this.dx = dx; }

    public int getDy() { return dy; }
    public void setDy(int dy) { this.dy = dy; }

    public int getPower() { return power; }
    public void setPower(int power) { this.power = power; }

    public static int getBallCount() { return ballCount; }

    // comparison uses damage power.
    @Override
    public String toString() {
        return "Ball[dx=" + dx + ", dy=" + dy + ", power=" + power + "] " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        Ball other = (Ball) o;
        return dx == other.dx && dy == other.dy && power == other.power;
    }

    /** compares balls by damage power. */
    @Override
    public int compareTo(Ball other) {
        return power - other.power;
    }
}
