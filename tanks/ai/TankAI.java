/**
 * Class author: Matěj Šťastný
 * Period: 1
 * Date: 4/4/2024
 */

package ai;

import java.util.ArrayList;
import java.util.Arrays;

import game.PowerUp;
import game.TankAIBase;
import game.Target;
import game.Vec2;

public class TankAI extends TankAIBase {
    private boolean firstExe = true; // boolean determining, if it is the first execution of the updateAI() method
    private boolean isSingleplayer = true; // boolean determining, if there is an enemy AI

    private double border_X = 1.5; // Deafult border width value
    private double border_Y = 4.5; // Deafult border height value

    private final double STEP_SIZE = 6; // Size of the steps in which the tank moves
    private final double HEIGHT = 12; // Height of the playing field
    private final double WIDTH = 23; // Width of the playing field

    private Direction direction = Direction.RIGHT; // Initial direction of the tank in squareAI() method

    public String getPlayerName() {
        return "Matej";
    }

    public int getPlayerPeriod() {
        return 1;

    }

    /**
     * Enum for the direction of the tank
     * 
     */
    private enum Direction {
        RIGHT,
        UP,
        LEFT,
        DOWN
    }

    /**
     * Helper class to handle complex return value of an Array of doubles, and an
     * Array of
     * vectors.
     * It's able to convert both Arrays to strings.
     * 
     */
    public class DbVec {
        private double[] doubleArr;
        private Vec2[] vecArr;

        public DbVec(double[] doubleArr, Vec2[] vecArr) {
            this.doubleArr = doubleArr;
            this.vecArr = vecArr;
        }

        public String getAngles() {
            return Arrays.toString(doubleArr);
        }

        public String getVectors() {
            return Arrays.toString(vecArr);
        }
    }

    /**
     * Main method called by the game object, executed every time the command queue
     * is empty.
     * 
     */
    public boolean updateAI() {
        if (firstExe) {
            System.out.println("FIRST UPDATE_AI EXECUTION");
            updateBorderWithLimits(border_X, border_Y);
            isSingleplayer = getOther() == null;
            if (isSingleplayer) {
                System.out.println("SINGLEPLAYER MODE");
            }
        }

        if (!isSingleplayer) {
            if (shootIfEnemyAhed(getTankPos(), getOther().getPos(), getTankAngle())) {
                System.out.println("ENEMY DETECTED");
                return true;
            }
        }

        combinedAI();

        firstExe = false;
        return true;
    }

    /**
     * Tank AI combining square mootion and neartest powerup AIs.
     * The AI are swiched when there is 30 seconds of game time remaining.
     * Before it executes square motion AI, it resets the border dimensions
     * according to the tank range.
     * 
     */
    private void combinedAI() {
        if (getLevelTimeRemaining() > 30) {
            nearestPowerUpAI();
        } else {
            updateBorderWithLimits(getTankShotRange() - 1.5, getTankShotRange() - 1.5);
            squareMotionAI();
        }
    }

    /**
     * Tank AI based on moving in steps determined by a local variable in a square
     * pattern.
     * The size of the square with the local border size variables.
     * 
     */
    private void squareMotionAI() {
        if (firstExe) {
            move(Math.round(border_Y) - 1, Direction.UP); // If the square AI is executed in the beginning, it will move
                                                          // the tank to the x starter position.
        }

        shootTargets(sortTargetsFromAngle(getInRangeTargets(getTankPos()), getTankAngle(), true));
        squareMotion(getTankPos(), this.direction); // Moves the tank in square motion
    }

    /**
     * Tank AI based on driving to the powerup with the highest priority in steps
     * determined by the local variable.
     * After every step, the tank will shoot any targets, that are in range.
     * 
     */
    private void nearestPowerUpAI() {
        Vec2 powerUpPos = getClosestPowerUp(getTankPos()).getPos();
        Vec2 tankPos = getTankPos();

        ArrayList<Vec2> targets = getInRangeTargets(getTankPos());
        if (!targets.isEmpty()) {
            shootTargets(targets);
        }

        Vec2 vector = powerUpPos.minus(tankPos);
        double stepSize = getTankShotRange() / 1.4;

        if (vector.x != 0) {
            // distanceX can be positive (moving to the right) or negative (moving to the
            // left)
            double distanceX = vector.x > 0 ? Math.min(vector.x, stepSize) : Math.max(vector.x, -stepSize);
            queueCmd("move", new Vec2(distanceX, 0));

        } else if (vector.y != 0) {
            double distanceY = vector.y > 0 ? Math.min(vector.y, stepSize) : Math.max(vector.y, -stepSize);
            queueCmd("move", new Vec2(0, distanceY));

        }
    }

    private String directiinToString(Direction direction) {
        switch (direction) {
            case RIGHT:
                return "Right";
            case UP:
                return "Up";
            case LEFT:
                return "Left";
            case DOWN:
                return "Down";
            default:
                assert (false) : "Invalid direction";
                return null;
        }
    }

    /**
     * Converts an angle value in degrees to an {@code Direction} enum
     * 
     * @param deg - Degree value of the angle
     * @return {@code Direction} enum value
     */
    private Direction degreesToDirection(double deg) {
        // System.out.println("Raw: " + deg);
        deg = Math.round(deg / 90.0) * 90.0;
        // System.out.println("Round: " + deg);
        deg = Math.abs(deg);
        // System.out.println("Abs: " + deg);
        deg = Math.min(360, deg);
        // System.out.println("Min: " + deg);

        if (deg == 0) {
            return Direction.UP;
        } else if (deg == 90) {
            return Direction.RIGHT;
        } else if (deg == 180) {
            return Direction.DOWN;
        } else if (deg == 270) {
            return Direction.LEFT;
        } else {
            return Direction.UP;
        }
    }

    /**
     * Cycles the class Direction enum variable to the next direction according to a
     * square.
     */
    private void cycleDirections() {
        switch (direction) {
            case RIGHT:
                direction = Direction.UP;
                break;
            case UP:
                direction = Direction.LEFT;
                break;
            case LEFT:
                direction = Direction.DOWN;
                break;
            case DOWN:
                direction = Direction.RIGHT;
                break;
            default:
                assert (false) : "Invalid direction";
        }
    }

    /**
     * Updates the class border variables with according limits:
     * Floor of both -0.5 with offset causing it to be 0;
     * Ceiling of {@code borderX} (width) of half the wifth of the playing field;
     * Ceiling of {@code borderY} (height) of half the height of the playing field.
     * 
     * @param borderX - new value of the {@code borderX} (width)
     * @param borderY - new value of the {@code borderY} (height)
     */
    private void updateBorderWithLimits(double borderX, double borderY) {
        border_X = Math.min(Math.max(borderX, -0.5), 10.5);
        border_Y = Math.min(Math.max(borderY, -0.5), 4.5);
    }

    /**
     * Moves the tank in a direction given by a {@code Direction} enum by a distance
     * given by {@code distance}.
     * 
     * @param distance  - Double of the distance
     * @param direction - Enum of the direction
     * 
     */
    private void move(double distance, Direction direction) {
        Vec2 vector;
        switch (direction) {
            case RIGHT:
                vector = new Vec2(distance, 0);
                break;
            case UP:
                vector = new Vec2(0, distance);
                break;
            case LEFT:
                vector = new Vec2(-distance, 0);
                break;
            case DOWN:
                vector = new Vec2(0, -distance);
                break;
            default:
                vector = new Vec2(0, 0);
                assert (false) : "Invalid direction";
        }

        queueCmd("move", vector);
    }

    /**
     * Calculates the distance from a position given by {@code position} to the
     * border in the direction given by the {@code Direction} enum.
     * 
     * @param position  - Vector that is the origin of the calculation
     * @param direction - Direction of the border
     * @return Non-negative double distance (either X or Y according to
     *         the direction)
     */
    private double getDistanceFromBorder(Vec2 position, Direction direction) {
        double distance = 0;
        double x = position.x;
        double y = position.y;
        switch (direction) {
            case RIGHT:
                return WIDTH - border_X - x - 1;
            case UP:
                return HEIGHT - border_Y - y - 1;
            case LEFT:
                return x - border_X - 1;
            case DOWN:
                return y - border_Y - 1;
            default:
                assert (false) : "Invalid direction";
        }
        return distance;
    }

    /**
     * Moves the tank from a position given by {@code position} vector by a next
     * step, either the maximum step size given by a class variable, or if the
     * distance to the border is smaller, by the distance remaining.
     * If moved by smaller distance than the step (meaning the tank is on the edge
     * of
     * the border after the method execution), the method cycles the class
     * {@code Direction} enum variable.
     * 
     * @param origin    - origin, from where the tank is being moved
     * @param direction - direction, in which the tank is being moved
     */
    private void squareMotion(Vec2 origin, Direction direction) {
        double distFromBorder = getDistanceFromBorder(origin, direction);
        System.out.println("Origin " + origin + " distance " + distFromBorder);
        if (distFromBorder <= STEP_SIZE) {
            move(distFromBorder, direction);
            cycleDirections();
        } else {
            move(STEP_SIZE, direction);
        }
    }

    /**
     * Gets all targets in range of a point given by the {@code origin} vector, and
     * counts their distance from the {@code origin} point.
     * 
     * @param origin - Point, from where the distance is being counted
     * @return ArrayList of the distances from the origin to the target
     */
    private ArrayList<Vec2> getInRangeTargets(Vec2 origin) {
        ArrayList<Vec2> targetsInRange = new ArrayList<Vec2>();

        Target[] targets = getTargets();
        for (Target target : targets) {
            if (targetInRange(target, origin)) {
                targetsInRange.add(target.getPos().subtract(origin));
            }
        }
        return targetsInRange;
    }

    /**
     * Sorts target distances by the angle from their origin point, depending on the
     * {@code origin angle}.
     * 
     * @param vectors   - ArrayList of distances of the targets from the origin
     *                  point
     * @param origAngle - Angle from where the targets are being sorted
     * @param toRight   - Determins, if the tarets are being sorted from the origin
     *                  point to right if {@code true} or to left if {@code false}
     * @return Sorted ArrayList of same size of the distances
     */
    private ArrayList<Vec2> sortTargetsFromAngle(ArrayList<Vec2> vectors, double origAngle, boolean toRight) {
        if (vectors.size() == 0) {
            return vectors;
        }
        ArrayList<Vec2> sortedVec = new ArrayList<Vec2>();
        ArrayList<Double> sortedAng = new ArrayList<Double>();
        sortedVec.add(vectors.get(0));
        sortedAng.add(calculateAngleDegrees(vectors.get(0).x, vectors.get(0).y));
        for (int i = 1; i < vectors.size(); i++) {
            boolean added = false;
            Vec2 currVec = vectors.get(i);
            double angle = calculateAngleDegrees(currVec.x, currVec.y);
            double ang = origAngle - angle;
            if (toRight) {
                ang = -ang;
                for (int y = 0; y < sortedVec.size(); y++) {
                    if (ang <= sortedAng.get(y)) {
                        sortedAng.add(y, ang);
                        sortedVec.add(y, currVec);
                        added = true;
                        break;
                    }
                }
                if (!added) {
                    sortedAng.add(ang);
                    sortedVec.add(currVec);
                }
            } else {
                for (int y = 0; y < sortedVec.size(); y++) {
                    if (ang > sortedAng.get(y)) {
                        sortedAng.add(y, ang);
                        sortedVec.add(y, currVec);
                        added = true;
                        break;
                    }
                }
                if (!added) {
                    sortedAng.add(ang);
                    sortedVec.add(currVec);
                }
            }
        }
        return sortedVec;
    }

    /**
     * Calculates the lenght hypotenuse of a right triangle from the lenghts of the
     * adjacent and opposite sides.
     * 
     * @param adjacent - Lenght of the adjacent side
     * @param opposite - Lenght of the opposite side
     * @return
     */
    private static double calculateHypotenuse(double adjacent, double opposite) {
        return Math.sqrt(adjacent * adjacent + opposite * opposite);
    }

    /**
     * Calculates the angle between the hypotenuse and opposite sides of a right
     * triangle from the lenghts of the opposite and adjacent side.
     * 
     * @param opposite - Lenght of the opposite side
     * @param adjacent - Lenght of the adjacent side
     * @return Angle in decrees rounded to 2 decimal places
     */
    private static double calculateAngleDegrees(double opposite, double adjacent) {
        double angle = 0.0;

        if (adjacent < 0 && opposite >= 0) {
            angle += 90.0;
        } else if (adjacent < 0 && opposite < 0) {
            angle += 180.0;
        } else if (adjacent >= 0 && opposite < 0) {
            angle += 270.0;
        }

        opposite = Math.abs(opposite);
        adjacent = Math.abs(adjacent);

        double hypotenuse = calculateHypotenuse(adjacent, opposite);
        double radians = Math.asin(opposite / hypotenuse);

        angle += Math.round(Math.toDegrees(radians) * 100.0) / 100.0;
        return angle;
    }

    /**
     * Checks, if a target is in the current shooting range of the tank.
     * 
     * @param target - Target being checked
     * @param origin - Origin point, from where the method is checking the distance
     * @return Is in range - {@code true}/Is not in range {@code false}
     */
    private boolean targetInRange(Target target, Vec2 origin) {
        return (origin.distance(target.getPos()) <= getTankShotRange());
    }

    /**
     * Queues shoot commands on all given distances from the tank position.
     * 
     * @param targetVectors - distances being shot at
     */
    private void shootTargets(ArrayList<Vec2> targetVectors) {
        for (Vec2 vector : targetVectors) {
            queueCmd("shoot", vector);
        }
    }

    /**
     * Calculates the collect weight of a power up depending on its type.
     * 
     * @param currentTankPos - current position of the tank
     * @param powerUp        - power up object being evaluated
     * @return Double of the priority coefitient, the smaller, the bigger priority
     */
    private double powerUpWeight(Vec2 currentTankPos, PowerUp powerUp) {
        boolean goodRange = false;
        double range = getTankShotRange();
        if (range > 8) {
            goodRange = true;
        }

        switch (powerUp.getType()) {
            case "S":
                return !goodRange ? 1.6 : 0.5;
            case "R":
                return !goodRange ? 0.5 : 1;
            case "P":
                return !goodRange ? 2 : 0.5;
            default:
                return 1;
        }
    }

    /**
     * Finds the closest power up to the {@code origin} position given.
     * Takes powerup weight in mind.
     * 
     * @return
     */
    private PowerUp getClosestPowerUp(Vec2 origin) {
        PowerUp[] powerUp = getPowerUps();
        double smallestDist = origin.distance(powerUp[0].getPos()) * powerUpWeight(origin, powerUp[0]);
        PowerUp smallestPU = powerUp[0];
        for (PowerUp currPU : powerUp) {
            double distance = origin.distance(currPU.getPos()) * powerUpWeight(origin, currPU);
            if (smallestDist > distance) {
                smallestDist = distance;
                smallestPU = currPU;
            }
        }
        return smallestPU;
    }

    /**
     * Shoots in the direction of the enemy tank, if it is in range, and directly
     * ahead in the path of the tank
     * 
     * @param tankPos   - postion of the tank
     * @param enemyPos  - position of the enemy tank
     * @param tankAngle - angle of the tank
     * @return Boolean, if the shot was fired, returns imideatly false, if the class
     *         boolean isSinglePlayer is {@code true}
     */
    private boolean shootIfEnemyAhed(Vec2 tankPos, Vec2 enemyPos, double tankAngle) {
        if (isSingleplayer) {
            return false;
        }
        double range = 8;
        Direction tankDir = degreesToDirection(tankAngle);
        boolean inRange;
        Vec2 shotDir;
        System.out.println("TankPos" + tankPos + " EnemyPos: " + enemyPos + " Direction: " + directiinToString(tankDir)
                + " (" + tankAngle + ")");
        switch (tankDir) {
            case UP:
                inRange = enemyPos.x == tankPos.x && enemyPos.y - range <= tankPos.y;
                shotDir = new Vec2(0, 1);
                break;
            case DOWN:
                inRange = enemyPos.x == tankPos.x && enemyPos.y + range <= tankPos.y;
                shotDir = new Vec2(0, -1);
                break;
            case RIGHT:
                inRange = enemyPos.y == tankPos.y && enemyPos.x - range <= tankPos.x;
                shotDir = new Vec2(1, 0);
                break;
            case LEFT:
                inRange = enemyPos.y == tankPos.y && enemyPos.x + range <= tankPos.x;
                shotDir = new Vec2(-1, 0);
                break;
            default:
                System.out.println("HUH");
                return false;
        }
        if (inRange) {
            queueCmd("shoot", shotDir);
            return true;
        }
        return false;
    }
}
