package agolf.game;

import agolf.Seed;
import agolf.SynchronizedBool;
import java.util.List;

public class GameState {

    // Constants
    private static final double MAGIC_OFFSET = Math.sqrt(2.0) / 2.0;
    private static final int DIAG_OFFSET = (int) (6.0 * MAGIC_OFFSET + 0.5);
    private static final int[] FRAME_TIME_HISTORY = new int[] {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};

    // Game state variables
    public int playerCount;
    public int onShoreSetting;
    public int collisionMode;
    public int currentPlayerId;
    public int isValidPlayerId;
    public int gameState;

    public double startPositionX;
    public double startPositionY;
    public double bounciness;
    public double somethingSpeedThing;

    public double[] resetPositionX;
    public double[] resetPositionY;

    public List<double[]>[] teleportStarts;
    public List<double[]>[] teleportExits;

    public short[][][] magnetMap;

    public double[] playerX;
    public double[] playerY;
    public double[] speedX;
    public double[] speedY;

    public boolean[] simulatePlayer;
    public SynchronizedBool[] onHoleSync;
    public boolean isLocalPlayer;
    public boolean[] playerActive;

    public Seed seed;
    public int maxPhysicsIterations;
    public boolean strokeInterrupted;
}
