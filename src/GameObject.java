import java.awt.Color;

/**
 * stores the shared position and size for every object on the board.
 * the paddle, ball, and blocks inherit these properties.
 * positions use grid columns for x and grid rows for y.
 */
public abstract class GameObject {

    // these constants define the size of the game board.
    public static final int COLS = 12;
    public static final int ROWS = 14;
    private static int objectCount = 0;

    // position and size are measured in grid cells.
    private int x;
    private int y;
    private int width;
    private int height;

    // shorter constructors use the full constructor to share setup.
    public GameObject() {
        this(0, 0);
    }

    public GameObject(int x, int y) {
        this(x, y, 1, 1);
    }

    public GameObject(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        objectCount++;
    }

    // each child supplies its own appearance and description.
    public abstract Color getColor();

    public abstract String describe();

    /** checks whether this object covers the requested cell. */
    public boolean contains(int col, int row) {
        return col >= x && col < x + width && row >= y && row < y + height;
    }

    /** checks whether this object overlaps another object. */
    public boolean intersects(GameObject other) {
        return x < other.x + other.width && x + width > other.x
                && y < other.y + other.height && y + height > other.y;
    }

    /** moves the object by the requested number of cells. */
    public void moveBy(int dx, int dy) {
        x = x + dx;
        y = y + dy;
    }

    /** places the object at an exact grid position. */
    public void moveTo(int newX, int newY) {
        x = newX;
        y = newY;
    }

    // these methods provide controlled access to the position and size.
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public static int getObjectCount() { return objectCount; }

    // these methods support readable output and value comparison.
    @Override
    public String toString() {
        return "at (" + x + ", " + y + ") size " + width + "x" + height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameObject other = (GameObject) o;
        return x == other.x && y == other.y && width == other.width && height == other.height;
    }
}
