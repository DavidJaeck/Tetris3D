package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// a module represent the unit of blocks the player can manipulate
public class Module {
    private final int type; // -1 is an invisible hitbox, 0 is nothing, 1 to N are different colored blocks
    private boolean[][][] configuration; // x,y,z


    public static final int SIDE_LENGTH = 3;
    public static final int CONFIGURATIONS_COUNT = 7;
    public static final int[][] configs = {
            {4, 5, 13, 22}, // L-shape
            {13, 14, 22}, // little L-shape
            {0, 1, 3, 4, 9, 10, 12, 13}, // cube
            {9, 10, 11, 12, 13, 14, 15, 16, 17}, // plane
            {4, 13, 22}, // I-Shape
            {1, 4, 7, 13, 22}, // T-shape
            {10, 12, 13, 14}}; // little T-shape

    // the constructor creates a random module with a random rotation
    public Module() {
        type = new Random().nextInt(CONFIGURATIONS_COUNT) + 1;
        configuration = convertConfiguration(configs[type - 1]);
        rotateRandom();
    }

    public int getType() {
        return type;
    }

    public boolean[][][] getConfiguration() {
        return configuration;
    }


    public void rotate(int axis, boolean reverse) {
        boolean[][][] result = new boolean[SIDE_LENGTH][SIDE_LENGTH][SIDE_LENGTH];
        if (reverse) { // for reverse operation perform 3 normal rotations
            rotate(axis, false);
            rotate(axis, false);
            rotate(axis, false);
            return;
        }//for all block components of the module:
        for (int x = 0; x < SIDE_LENGTH; x++) {
            for (int y = 0; y < SIDE_LENGTH; y++) {
                for (int z = 0; z < SIDE_LENGTH; z++) {
                    if (axis == 0) { // assign new positions
                        result[x][z][SIDE_LENGTH - y - 1] = configuration[x][y][z];
                    } else if (axis == 2) {
                        result[z][y][SIDE_LENGTH - x - 1] = configuration[x][y][z];
                    } else if (axis == 1) {
                        result[y][SIDE_LENGTH - x - 1][z] = configuration[x][y][z];

                    }
                }
            }
        }
        configuration = result;
    }

    public void rotateRandom() {
        Random random = new Random();
        int roll;
        for (int i = 0; i < 3; i++) { // for the 3 axes
            roll = random.nextInt(3); // rolls: rotate forwards, backwards or nothing
            if (roll == 0) {
                rotate(i, false); // performs rotation forward around axis
            } else if (roll == 1) {
                rotate(i, true); // performs rotation backward around axis
            } // else dont rotate
        }
    }
    // helper method for collision detection for moving modules side to side
    // for a given side returns the first blocks a collision would occur with
    public List<int[]> getSideFacingParts(int side) {
        List<int[]> result = new ArrayList<>();
        for (int i = 0; i < SIDE_LENGTH; i++) {
            for (int j = 0; j < SIDE_LENGTH; j++) {
                for (int k = 0; k < SIDE_LENGTH; k++) { // dimension of interest
                    if (side == 4) { // y negative - bottom
                        if (configuration[i][k][j]) {
                            result.add(new int[]{i, k, j}); // k-th part is the first one facing this side
                            break; // when a block is found the other blocks behind it can be neglected
                        }
                    } else if (side == 0) { // x negative - left
                        if (configuration[k][i][j]) {
                            result.add(new int[]{k, i, j});
                            break;
                        }
                    } else if (side == 2) { // x positive - right
                        if (configuration[SIDE_LENGTH - 1 - k][i][j]) {
                            result.add(new int[]{SIDE_LENGTH - 1 - k, i, j});
                            break;
                        }
                    } else if (side == 3) { // z negative - behind
                        if (configuration[i][j][k]) {
                            result.add(new int[]{i, j, k});
                            break;
                        }
                    } else if (side == 1) { // z positive - front
                        if (configuration[i][j][SIDE_LENGTH - 1 - k]) {
                            result.add(new int[]{i, j, SIDE_LENGTH - 1 - k});
                            break;
                        }
                    } else if (side == 5) { // y positive - top
                        if (configuration[i][SIDE_LENGTH - 1 - k][j]) {
                            result.add(new int[]{i, SIDE_LENGTH - 1 - k, j});
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    // constructs 3 dimensional array from list of indices representing the module blocks
    private boolean[][][] convertConfiguration(int[] inConfig) {
        boolean[][][] outConfig = new boolean[SIDE_LENGTH][SIDE_LENGTH][SIDE_LENGTH];
        for (int j : inConfig) {
            int index = j;
            int y = 0;
            int z = 0;
            while (index >= SIDE_LENGTH * SIDE_LENGTH) { // calculates y ->
                index -= SIDE_LENGTH * SIDE_LENGTH; // by decreasing index by number of blocks in a plane ->
                y++; // and counting the planes
            }
            while (index >= SIDE_LENGTH) { // calculates z ->
                index -= SIDE_LENGTH;  // by decreasing index by number of blocks in a line ->
                z++; // and counting the lines
            }
            outConfig[index][y][z] = true; // setting the corresponding array entry for each index
        }
        return outConfig;
    }
}
