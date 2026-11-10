import java.awt.Color;

/** represents a breakable block with health and a score value. */
public class Block extends GameObject implements Comparable<Block> {

    // these fields track block size and counts shared by all blocks.
    public static final int BLOCK_WIDTH = 3;
    private static int blocksRemaining = 0;
    private static int totalBlocksCreated = 0;

    // hit points control durability, and points determine score value.
    private int hitPoints;
    private int maxHitPoints;
    private int points;

    // shorter constructors choose default health and score values.
    public Block() {
        this(0, 0);
    }

    public Block(int x, int y) {
        this(x, y, 1);
    }

    public Block(int x, int y, int hitPoints) {
        this(x, y, hitPoints, hitPoints * 10);
    }

    public Block(int x, int y, int hitPoints, int points) {
        super(x, y, BLOCK_WIDTH, 1);
        this.hitPoints = hitPoints;
        this.maxHitPoints = hitPoints;
        this.points = points;
        blocksRemaining++;
        totalBlocksCreated++;
    }

    /** applies damage and reports whether this hit broke the block. */
    public boolean takeHit(int damage) {
        if (isBroken()) {
            return false;
        }
        hitPoints = Math.max(0, hitPoints - damage);
        if (hitPoints == 0) {
            blocksRemaining--;
            return true;
        }
        return false;
    }

    /** bounces an approaching ball and applies its damage to this block. */
    public boolean hitBy(Ball ball) {
        if (!isBroken() && contains(ball.getNextX(), ball.getNextY())) {
            ball.bounceY();
            takeHit(ball.getPower());
            return true;
        }
        return false;
    }

    /** restores the block to its maximum health. */
    public void repair() {
        if (isBroken()) {
            blocksRemaining++;
        }
        hitPoints = maxHitPoints;
    }

    /** removes all remaining health from the block. */
    public void destroy() {
        takeHit(hitPoints);
    }

    public boolean isBroken() {
        return hitPoints <= 0;
    }

    /** uses color to show how many hits the block can still take. */
    @Override
    public Color getColor() {
        if (hitPoints == 1) {
            return new Color(52, 168, 83);    // green
        } else if (hitPoints == 2) {
            return new Color(66, 133, 244);   // blue
        } else {
            return new Color(234, 67, 53);    // red
        }
    }

    @Override
    public String describe() {
        return "Block with " + hitPoints + " HP worth " + points + " points";
    }

    // these methods expose block health and score information.
    public int getHitPoints() { return hitPoints; }
    public void setHitPoints(int hitPoints) { this.hitPoints = hitPoints; }

    public int getMaxHitPoints() { return maxHitPoints; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public static int getBlocksRemaining() { return blocksRemaining; }
    public static int getTotalBlocksCreated() { return totalBlocksCreated; }
    public static void resetBlocksRemaining() { blocksRemaining = 0; }

    // comparison uses the score value.
    @Override
    public String toString() {
        return "Block[hp=" + hitPoints + "/" + maxHitPoints + ", points=" + points + "] " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false; // compare class, position, and size first.
        Block other = (Block) o;
        return hitPoints == other.hitPoints && points == other.points;
    }

    /** compares blocks by score value. */
    @Override
    public int compareTo(Block other) {
        return points - other.points;
    }
}
