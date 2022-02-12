package game;


import engine.entities.Block;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import java.util.List;

// data class representing the playing field
public class Field {
    private final int[][][] spaces;
    private Module currentModule; // the module the player can influence
    private Module nextModule; // the next module to drop from the ceiling of the cage
    private int currentX;
    private int currentY;
    private int currentZ;

    public static final int X_LENGTH = 12; // 10 + 1 + 1 for the field and walls
    public static final int Y_LENGTH = 25; // 20 + 1 + 4 for the field, the bottom and the top
    public static final int Z_LENGTH = 12; // 10 + 1 + 1 for the field and walls

    private boolean left = false;
    private boolean right = false;
    private boolean up = false;
    private boolean down = false;
    private boolean space = false;
    private int rotateX = 0;
    private int rotateY = 0;
    private int rotateZ = 0;
    private int cameraSide = 3; // -> default is view from 1 + 1/2 pi

    private int leftIndex = 0;
    private int rightIndex = 2;
    private int backwardsIndex = 3;
    private int forwardsIndex = 1;

    public Field() {
        spaces = new int[X_LENGTH][Y_LENGTH][Z_LENGTH];
        for (int x = 0; x < X_LENGTH; x++) {
            for (int y = 0; y < Y_LENGTH; y++) {
                for (int z = 0; z < Z_LENGTH; z++) {
                    if (x == 0 || y == 0 || z == 0 || x == X_LENGTH - 1 || z == Z_LENGTH - 1) {
                        spaces[x][y][z] = -1; // sets hit boxes for floor and walls
                    }
                }
            }
        }
        nextModule = new Module();
        setNextModule();
    }

    // lowers the current module by one plane level
    public void lowerModule(ScoreSystem scoreSystem) {
        List<int[]> sideParts = currentModule.getSideFacingParts(4);
        if (isColliding(sideParts, 4)) { // if the module is colliding the modules beneath
            placeDownModule(scoreSystem); // it will be placed down in its spot
        } else {
            currentY -= 1; // eleswise it is lowered by one plane level
        }
    }

    public int[][][] getSpaces() {
        return spaces;
    }

    // handles the placement of the current module on the ground
    public void placeDownModule(ScoreSystem scoreSystem) {
        putModuleInPlace();
        dissolveSpaces(scoreSystem);
        if (currentY > Y_LENGTH - 2 * Module.SIDE_LENGTH) // if the module was placed above the field the game is over
            RunGame.gameOver = true;
        setNextModule();
    }

    // positions the current module on the ground
    private void putModuleInPlace() {
        List<int[]> sideParts = currentModule.getSideFacingParts(4);
        while (!isColliding(sideParts, 4)) // moves the module all the way down
            currentY -= 1;
        addCurrentModuleToSpaces();
    }

    // for a given direction moves the current module if possible
    private void moveModuleWithCheck(int direction) {
        List<int[]> sideParts = currentModule.getSideFacingParts(direction);
        if (!isColliding(sideParts, direction)) {
            moveModule(direction);
        }
    }

    // for a given axis and rotation-direction rotates the current module if possible
    private void rotateModuleWithCheck(int axis, boolean reverse) {
        if (isRotationAllowed(axis, reverse))
            currentModule.rotate(axis, reverse);
    }

    private boolean isRotationAllowed(int axis, boolean reverse){
        if (currentModule.getType() == 3) // rotating the cube is never necessary and therefore never allowed
            return false;
        boolean result = true;
        currentModule.rotate(axis, reverse); // simulates the proposed rotation
        for (int x = 0; x < Module.SIDE_LENGTH; x++) { // for all blocks of the module:
            for (int y = 0; y < Module.SIDE_LENGTH; y++) {
                for (int z = 0; z < Module.SIDE_LENGTH; z++) {
                    if (currentModule.getConfiguration()[x][y][z] && spaces[currentX + x][currentY + y][currentZ + z] != 0)
                        result = false; //checks if there is a block in the module and on the field with the same position
                }
            }
        }
        currentModule.rotate(axis, !reverse); // cleans up the simulated rotation, does not matter if it was possible
        return result;
    }

    // given the directions 0 <-> x negative, 1 <-> z positive, 2 <-> x positive and 3 <-> z negative
    // moves the current module by one block
    private void moveModule(int direction) {
        if (direction == 2) {
            currentX += 1;
        } else if (direction == 0) {
            currentX -= 1;
        } else if (direction == 1) {
            currentZ += 1;
        } else if (direction == 3) {
            currentZ -= 1;
        }
    }

    private void dissolveSpaces(ScoreSystem scoreSystem) {
        int[] dissolvePlanes = new int[Module.SIDE_LENGTH];
        int plane_count = 0;
        for (int y = 1; y < Y_LENGTH - Module.SIDE_LENGTH; y++) { // y=0 is the floor, stop before buffer on top
            for (int x = 1; x < X_LENGTH - 1; x++) { // x=1 and x=X_LENGTH-1 are walls
                for (int z = 1; z < Z_LENGTH - 1; z++) { // y=1 and y=Y_LENGTH-1 are walls
                    if (0 == spaces[x][y][z]) { // if a single block is not filled
                        x = X_LENGTH; //skip this plane
                        break;
                    } else if (x == X_LENGTH - 2 && z == Z_LENGTH - 2) { // all spaces in the plane were filled
                        dissolvePlanes[plane_count] = y;
                        plane_count++;
                    }
                }
            }
            if (plane_count == Module.SIDE_LENGTH) // placing a module can't complete any more planes
                break;
        }
        int index = 0;
        if (plane_count > 0) {
            for (int y = dissolvePlanes[index]; y < Y_LENGTH - Module.SIDE_LENGTH; y++) { //starts at the first plane that is to be dissolved
                for (int x = 1; x < X_LENGTH - 1; x++) {
                    //copies the filled spaces downwards
                    System.arraycopy(spaces[x][y + 1 + index], 1, spaces[x][y], 1, Z_LENGTH - 1 - 1);
                }
                if (index < plane_count) // if there are more planes that are to be dissolved
                    if (y == dissolvePlanes[index + 1]) // if one of them has been reached
                        index++; // increase the margin for copying by 1
            }
            scoreSystem.score(plane_count, isFieldEmpty()); // set points
        }
    }

    private boolean isFieldEmpty(){
        for (int x = 1; x < X_LENGTH-1; x++)
            for (int z = 1; z < Z_LENGTH-1; z++)
                if (spaces[x][1][z] != 0)
                    return false;
        return true;
    }


    private boolean isColliding(List<int[]> sideParts, int direction) {
        for (int[] part : sideParts) { // loop over all blocks facing the relevant side
            if (direction == 4) { // y negative - bottom
                if (!(0 == spaces[currentX + part[0]][currentY + part[1] - 1][currentZ + part[2]]))
                    return true;
            } else if (direction == 0) { // x negative left
                if (!(0 == spaces[currentX + part[0] - 1][currentY + part[1]][currentZ + part[2]]))
                    return true;
            } else if (direction == 2) { // x positive right
                if (!(0 == spaces[currentX + part[0] + 1][currentY + part[1]][currentZ + part[2]]))
                    return true;
            } else if (direction == 3) { // z negative behind
                if (!(0 == spaces[currentX + part[0]][currentY + part[1]][currentZ + part[2] - 1]))
                    return true;
            } else if (direction == 1) { // z positive front
                if (!(0 == spaces[currentX + part[0]][currentY + part[1]][currentZ + part[2] + 1]))
                    return true;
            }
        }
        return false; // no neighbouring blocks found
    }

    private void resetCurrentPos() {
        currentX = X_LENGTH / 2 - Module.SIDE_LENGTH / 2;
        currentY = Y_LENGTH - Module.SIDE_LENGTH;
        currentZ = X_LENGTH / 2 - Module.SIDE_LENGTH / 2;
    }

    private void setNextModule() {
        resetCurrentPos();
        currentModule = nextModule;
        nextModule = new Module();
    }

    public Module getNextModule() {
        return nextModule;
    }

    public void addCurrentModuleToSpaces() {
        for (int x = 0; x < Module.SIDE_LENGTH; x++) {
            for (int y = 0; y < Module.SIDE_LENGTH; y++) {
                for (int z = 0; z < Module.SIDE_LENGTH; z++) {
                    if (currentModule.getConfiguration()[x][y][z])
                        spaces[currentX + x][currentY + y][currentZ + z] = currentModule.getType();
                }
            }
        }
    }

    public void removeCurrentModuleFromSpaces() {
        for (int x = 0; x < Module.SIDE_LENGTH; x++) {
            for (int y = 0; y < Module.SIDE_LENGTH; y++) {
                for (int z = 0; z < Module.SIDE_LENGTH; z++) {
                    if (currentModule.getConfiguration()[x][y][z])
                        spaces[currentX + x][currentY + y][currentZ + z] = 0;
                }
            }
        }
    }

    public void update() {
        addCurrentModuleToSpaces();
        for (int x = 0; x < Field.X_LENGTH; x++) {
            for (int y = 0; y < Field.Y_LENGTH - Module.SIDE_LENGTH; y++) {
                for (int z = 0; z < Field.Z_LENGTH; z++) {
                    if (RunGame.fieldCubes[x][y][z] != null) { // check if there is currently a Block
                        if (spaces[x][y][z] == 0) { // if there should not be a Block
                            RunGame.fieldCubes[x][y][z] = null; // delete Block
                        } else if (RunGame.fieldCubes[x][y][z].getColor() != spaces[x][y][z]) { // if there should be a block, check if the color is wrong
                            RunGame.fieldCubes[x][y][z] = new Block(RunGame.fieldModels[spaces[x][y][z]-1], // -1 because spaces[X][Y][Z]=0 means a free space
                                    new Vector3f(x - 6, y+1, z - 6), 0, 0, 0, 1, spaces[x][y][z]); // set right color
                        }
                    } else { // if there is no block
                        if (spaces[x][y][z] > 0) { // if there should be a Block
                            RunGame.fieldCubes[x][y][z] = new Block(RunGame.fieldModels[spaces[x][y][z]-1], // -1 because spaces[X][Y][Z]=0 means a free space
                                    new Vector3f(x - 6, y+1, z - 6), 0, 0, 0, 1, spaces[x][y][z]); // set Block
                        }
                    }
                }
            }
        }
        removeCurrentModuleFromSpaces();
    }

    public void handleModuleManeuver(double cameraPosition, ScoreSystem scoreSystem) {
        handleResetSideInput(cameraPosition);
        handleMovementInput(scoreSystem);
        handleRotationInput();
        resetControlCoolDown();
    }

    private void handleResetSideInput(double cameraPosition) {
        if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
            int newCameraSide = (int) (4 + Math.floor(4 * (cameraPosition + 0.125))) % 4;
            leftIndex = (1 + cameraSide) % 4;
            rightIndex = (3 + cameraSide) % 4;
            backwardsIndex = (4 + cameraSide) % 4;
            forwardsIndex = (2 + cameraSide) % 4;
            cameraSide = newCameraSide;
        }
    }

    private void handleMovementInput(ScoreSystem scoreSystem) {
        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT) && !Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            if (!(left || right))
                moveModuleWithCheck(leftIndex);
            left = true;
        }
        if (!Keyboard.isKeyDown(Keyboard.KEY_LEFT) && Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            if (!(left || right))
                moveModuleWithCheck(rightIndex);
            right = true;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_UP) && !Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            if (!(up || down))
                moveModuleWithCheck(backwardsIndex);
            up = true;
        }
        if (!Keyboard.isKeyDown(Keyboard.KEY_UP) && Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            if (!(up || down))
                moveModuleWithCheck(forwardsIndex);
            down = true;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            if (!space)
                placeDownModule(scoreSystem);
            space = true;
        }

    }

    private void handleRotationInput() { // fix adjusted rotations
        boolean reverse = (cameraSide == 0 || cameraSide == 1);
        if (Keyboard.isKeyDown(Keyboard.KEY_Q) && !Keyboard.isKeyDown(Keyboard.KEY_A)) {
            if (rotateX == 0)
                rotateModuleWithCheck((cameraSide + 1) % 2, (cameraSide == 0 || cameraSide == 1));
            rotateX = 1;
        }
        if (!Keyboard.isKeyDown(Keyboard.KEY_Q) && Keyboard.isKeyDown(Keyboard.KEY_A)) {
            if (rotateX == 0)
                rotateModuleWithCheck((cameraSide + 1) % 2, !(cameraSide == 0 || cameraSide == 1));
            rotateX = -1;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_W) && !Keyboard.isKeyDown(Keyboard.KEY_S)) {
            if (rotateY == 0)
                rotateModuleWithCheck(2, true);
            rotateY = 1;
        }
        if (!Keyboard.isKeyDown(Keyboard.KEY_W) && Keyboard.isKeyDown(Keyboard.KEY_S)) {
            if (rotateY == 0)
                rotateModuleWithCheck(2, false);
            rotateY = -1;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_E) && !Keyboard.isKeyDown(Keyboard.KEY_D)) {
            if (rotateZ == 0)
                rotateModuleWithCheck((cameraSide) % 2, (cameraSide == 1 || cameraSide == 2));
            rotateZ = 1;
        }
        if (!Keyboard.isKeyDown(Keyboard.KEY_E) && Keyboard.isKeyDown(Keyboard.KEY_D)) {
            if (rotateZ == 0)
                rotateModuleWithCheck((cameraSide) % 2, !(cameraSide == 1 || cameraSide == 2));
            rotateZ = -1;
        }
    }

    private void resetControlCoolDown() {
        if (!Keyboard.isKeyDown(Keyboard.KEY_LEFT))
            left = false;
        if (!Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
            right = false;
        if (!Keyboard.isKeyDown(Keyboard.KEY_UP))
            up = false;
        if (!Keyboard.isKeyDown(Keyboard.KEY_DOWN))
            down = false;
        if (!Keyboard.isKeyDown(Keyboard.KEY_SPACE))
            space = false;
        if (!Keyboard.isKeyDown(Keyboard.KEY_Q))
            if (rotateX == 1)
                rotateX = 0;
        if (!Keyboard.isKeyDown(Keyboard.KEY_A))
            if (rotateX == -1)
                rotateX = 0;
        if (!Keyboard.isKeyDown(Keyboard.KEY_W))
            if (rotateY == 1)
                rotateY = 0;
        if (!Keyboard.isKeyDown(Keyboard.KEY_S))
            if (rotateY == -1)
                rotateY = 0;
        if (!Keyboard.isKeyDown(Keyboard.KEY_E))
            if (rotateZ == 1)
                rotateZ = 0;
        if (!Keyboard.isKeyDown(Keyboard.KEY_D))
            if (rotateZ == -1)
                rotateZ = 0;
    }

}
