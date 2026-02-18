/*
 * Author: Matěj Šťastný
 * Date created: 5/13/2024
 * Github link: repository not public
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package logic;

import java.time.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import game.ElevatorController;
import game.Game;

/**
 * <h2>My Elevator Controller class</h2>
 * Class designed to controll the game elevator. Contains onEvent methods, that
 * are called on events in the game accordingly.
 * </p>
 * <h4>Algorithm system:</h4>
 * </p>
 * TODO: Algorithm definiton
 *
 */
public class MyElevatorController implements ElevatorController {

    /////////////////
    // Constants
    ////////////////

    private final String PLAYER_NAME = "Matěj Šťastný";
    private final int PLAYER_PERIOD = 6;
    private final int GAME_LENGHT = 120;
    private final double ELEVATOR_SPEED = 0.5; // How many seconds per floor
    private final double ZOMBIE_BOARD_TIME = 2.85; // How many seconds from the eleator arrival to zombie in
    private final double ZOMBIE_UNBOARD_TIME = 1.5;
    private final String COLOR_RESET = "\u001B[0m";
    private final String COLOR_ELEVATOR_MOVE = "\u001B[35m";
    private final String COLOR_CALL_ELEVATOR = "\u001B[33m";
    private final String COLOR_QUE_FLOOR = "\u001B[32m";
    private final String COLOR_IMPORTANT = "\u001B[41m";
    private final String COLOR_IMPORTANT_2 = "\u001B[44m";

    /////////////////
    // Class variables
    ////////////////

    private Game game;
    private boolean toDestination;
    private boolean isFirstReq;
    private boolean areEmpty;
    private ArrayList<ElevatorRequest> waitingForElevatorRequests;
    private ArrayList<ElevatorRequest> inElevatorRequests;
    private ScheduledExecutorService scheduler;
    private int elevatorTargetFloor;

    /////////////////
    // Accesor methods
    ////////////////

    /**
     * Returns the player name.
     *
     * @return {@code String} of the player name.
     */
    public String getStudentName() {
        return this.PLAYER_NAME;
    }

    /**
     * Returns the player period number.
     *
     * @return {@code int} of the player period number.
     */
    public int getStudentPeriod() {
        return this.PLAYER_PERIOD;
    }

    /////////////////
    // OnEvent methods
    ////////////////

    /**
     * Method executed in the start of the game.
     *
     * @param game - {@code Game} object of the game.
     */
    public void onGameStarted(Game game) {

        // Initialize fields
        this.game = game;
        this.areEmpty = false;
        this.isFirstReq = true;
        this.waitingForElevatorRequests = new ArrayList<ElevatorRequest>();
        this.inElevatorRequests = new ArrayList<ElevatorRequest>();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();

        // Log the start of the game, and schedule the log for the end of the game.
        System.out.println(this.COLOR_IMPORTANT + timeStamp() + "GAME START" + this.COLOR_RESET);
        scheduler.schedule(() -> {
            System.out.println(this.COLOR_IMPORTANT_2 + timeStamp() + "GAME END" + this.COLOR_RESET);
        }, this.GAME_LENGHT, TimeUnit.SECONDS);
    }

    /**
     * On event method called automaticly by the game, when specified event appeares
     * </p>
     * <h4>Event:</h4>
     * "outside-the-elevator" request, meaning zombie requesting an elevator from
     * it's floor.
     * </p>
     * The event will be triggered with the request is created/enabled and also when
     * it is
     * cleared (reqEnable indicates which).
     *
     * @param floorIdx  - index of the floor, where the zombie is calling the
     *                  elevator from.
     * @param dir       - direction that the elevator must go from it's position, to
     *                  reach the zombies floor.
     * @param reqEnable - if the request was created, or cleared.
     */
    public void onElevatorRequestChanged(int floorIdx, Direction dir, boolean reqEnable) {
        // Add the request to the zombie wait list, if it's a new request
        if (reqEnable) {
            System.out.println(this.COLOR_CALL_ELEVATOR + timeStamp() + "newElevatorCall(" + floorIdx + ")" + " Sum: "
                    + this.waitingForElevatorRequests.size() + this.COLOR_RESET);
            this.waitingForElevatorRequests.add(new ElevatorRequest(floorIdx, LocalTime.now()));
        }

        // If it's the first request, start the elevator move sequence
        if (this.isFirstReq) {
            this.isFirstReq = false;
            this.toDestination = false;
            startElevatorMoveSequence();
        }
    }

    /**
     * On event method called automaticly by the game, when specified event appeares
     * </p>
     * <h4>Event:</h4>
     * "inside-the-elevator" request, meaning a zombie in that entered the elevator
     * is requesting a floor it want's to go.
     * </p>
     * The event will be triggered with the request is created/enabled and also when
     * it is
     * cleared (reqEnable indicates which).
     *
     * @param elevatorIdx - index of the elevator, normally 0.
     * @param floorIdx    - index of the floor, that the zombie wan't to go to.
     * @param reqEnable   - if the request was created, or cleared.
     */
    public void onFloorRequestChanged(int elevatorIdx, int floorIdx, boolean reqEnable) {
        System.out.println(
                this.COLOR_QUE_FLOOR + timeStamp() + "floorRequestQued(" + elevatorIdx + ", " + floorIdx + ", "
                        + reqEnable + ")" + " Sum: " + this.inElevatorRequests.size() + this.COLOR_RESET);

        // Add the request to the que list, if new request
        if (reqEnable) {
            this.inElevatorRequests.add(new ElevatorRequest(floorIdx, LocalTime.now()));
        }
    }

    /**
     * On event method called automaticly by the game, when specified event appeares
     * </p>
     * <h4>Event:</h4>
     * The elevator arrived at a floor and opened it's door.
     * </p>
     * !The medhod doesn't work properly!
     *
     * @param elevatorIdx     - index of the elevator, normally 0.
     * @param floorIdx        - index of the floor, that the elevator arrived at.
     * @param traverDirection - I have no clue what this is :(
     */
    public void onElevatorArrivedAtFloor(int elevatorIdx, int floorIdx, Direction travelDirection) {
        // ! Method doesn't work!
    }

    /**
     * On event method called automaticly by the game, when specified event appeares
     * (tick method).
     * </p>
     * <h4>Event:</h4>
     * Tick method.
     *
     * @param deltaTime - delta time.
     */
    public void onUpdate(double deltaTime) {
        if (game == null) {
            return;
        }

        // The lists are empty, and haven't been emty last tick.
        if (this.inElevatorRequests.size() + this.waitingForElevatorRequests.size() <= 0 && !this.isFirstReq) {
            if (!this.areEmpty) {
                System.out.println(this.COLOR_IMPORTANT + timeStamp() + "EMPTY ARRAYS" + this.COLOR_RESET);
                areEmpty = true;
            }
        } 
        // The lists not empty for the first time since empty
        else if (areEmpty) {
            System.out.println(this.COLOR_IMPORTANT + timeStamp() + "MoveSequence Restored" + this.COLOR_RESET);
            areEmpty = false;
            resumeElevatorMoveSequence(this.elevatorTargetFloor);
        }
    }

    /////////////////
    // Private methods
    ////////////////

    /**
     * Algorythm to determine if the elevator should go for a zombie waiting for an
     * elevator, or deliver a zombie in the elevator.
     *
     * @return {@code boolean} value of "delivering zombie", meaning {@code true} if
     *         the next move should be delivering a zombie to it's destination, or
     *         {@code false} if the next move should be picking up a zombie waiting
     *         for an elevator.
     */
    private boolean getRequestMode() {

        // Assume, that the at least one of the arrays is not empty
        assert (this.inElevatorRequests.size() > 0 || this.waitingForElevatorRequests.size() > 0) : "Arraylists empty";
        boolean isDelivering;

        if (this.toDestination) {
            // Previously going to destination, and wait list isn't empty
            if (waitingForElevatorRequests.size() > 0) {
                isDelivering = false;
            }

            // Previously going to destination, but waitlist empty
            else {
                isDelivering = true;
            }
        }

        else {
            // Previously going for new zombie on waitlist, and the gotoList isn't empty
            if (this.inElevatorRequests.size() > 0) {
                isDelivering = true;
            }

            // Previously goig for new zombie, but the gotoList empty
            else {
                isDelivering = false;
            }
        }

        return isDelivering;
    }

    /**
     * Method, that will find the most optimal request to fufill next. Picks the
     * oldest {@code ElevatorRequest} object in both arrays. Returns an array of
     * {@code int}s
     * </p>
     * First element is determined like this:
     * </p>
     * 0 -> {@code inElevatorRequests}
     * 1 -> {@code waitingForElevatorRequests}
     * </p>
     * The second element just indicated the index of the {@ElevatorRequest} chosen
     * from that array.
     * 
     * @return index of the {@code ElevatorRequest} chosen.
     */
    private int[] getBestReqIndex() {
        // If the lists are empty, return null
        if (this.waitingForElevatorRequests.size() < 1 && this.inElevatorRequests.size() < 1) {
            return null;
        }

        LocalTime currentBestTime;
        int[] currentBestIndex = new int[2];
        // If waitlist is empty take first from goto
        if (this.waitingForElevatorRequests.size() == 0) {
            currentBestIndex[0] = 0;
            currentBestIndex[1] = 0;
            currentBestTime = inElevatorRequests.get(0).getTimeCreated();
        } else {
            currentBestIndex[0] = 1;
            currentBestIndex[1] = 0;
            currentBestTime = waitingForElevatorRequests.get(0).getTimeCreated();
        }

        for (int i = 1; i < this.waitingForElevatorRequests.size(); i++) {
            if (currentBestTime.compareTo(waitingForElevatorRequests.get(i).getTimeCreated()) >= 0) {
                currentBestTime = waitingForElevatorRequests.get(i).getTimeCreated();
                currentBestIndex[0] = 1;
                currentBestIndex[1] = i;
            }
        }

        for (int i = 0; i < this.inElevatorRequests.size(); i++) {
            if (currentBestTime.compareTo(inElevatorRequests.get(i).getTimeCreated()) >= 0) {
                currentBestTime = inElevatorRequests.get(i).getTimeCreated();
                currentBestIndex[0] = 0;
                currentBestIndex[1] = i;
            }
        }

        return currentBestIndex;
    }

    /**
     * Old method to find the index of the next floor the elevator will travel to.
     * 
     * @return - array of {@int}s, first element is the request list chosen (either
     *         1 for {@code inElevatorRequests} or 0 for
     *         {@code waitingForElevatorRequests}), and the second element for the
     *         index of the {@code ElevatorRequest} chosen in the array.
     */
    @SuppressWarnings("unused")
    private int[] getNextIndex() {
        if (this.waitingForElevatorRequests.size() < 1 && this.inElevatorRequests.size() < 1) {
            return null;
        }

        int[] next = new int[2];
        this.toDestination = getRequestMode();
        if (this.toDestination) {
            next[0] = 0;
            next[1] = 0;
        } else {
            next[0] = 1;
            next[1] = 0;
        }
        return next;
    }

    /**
     * Starts the recursive method for moving the elevator.
     *
     */
    private void startElevatorMoveSequence() {
        moveToNext(true, 0);
    }

    /**
     * Starts the recursive method, but gives it a floor.
     * 
     * @param floor - floor, that the elevator is on.
     */
    private void resumeElevatorMoveSequence(int floor) {
        moveToNext(true, floor);
    }

    /**
     * This is a recursive method, with a time delay of when it's called.
     * </p>
     * This method will first remove the data of the floor the elevator arrived in
     * (if there are zombies on the 1st floor, and the elevator is on 1st floor,
     * than all the {@code elevatorRequest} for this floor will be cleared), than
     * moves the eleator to it's next position using reursion.
     *
     * @param initialCall          - tells the method, if it should delete it's
     *                             preious
     *                             destination data.
     * @param currentElevatorFloor - current floor of the elevator.
     */
    private void moveToNext(boolean initialCall, int currentElevatorFloor) {

        // Remove previous destination data
        if (!initialCall) {
            for (int i = 0; i < this.inElevatorRequests.size(); i++) {
                ElevatorRequest e = this.inElevatorRequests.get(i);
                if (e.getTargetFloor() == currentElevatorFloor) {
                    inElevatorRequests.remove(i);
                    i--;
                }
            }
            for (int i = 0; i < this.waitingForElevatorRequests.size(); i++) {
                ElevatorRequest e = this.waitingForElevatorRequests.get(i);
                if (e.getTargetFloor() == currentElevatorFloor) {
                    waitingForElevatorRequests.remove(i);
                    i--;
                }
            }
        }

        int targetFloor;

        // Get next array request index
        int[] nextFloorIndex = getBestReqIndex();

        if (nextFloorIndex == null) {
            return;
        }
        if (nextFloorIndex[0] == 0) {
            targetFloor = inElevatorRequests.get(nextFloorIndex[1]).getTargetFloor();
            inElevatorRequests.get(nextFloorIndex[1]).runRequest();
        } else {
            targetFloor = waitingForElevatorRequests.get(nextFloorIndex[1]).getTargetFloor();
            waitingForElevatorRequests.get(nextFloorIndex[1]).runRequest();
        }

        // Print log
        System.out.println(this.COLOR_ELEVATOR_MOVE + timeStamp() + "moveElevator" + "(From: " + currentElevatorFloor
                + " To: " + targetFloor
                + ")" + this.COLOR_RESET);

        // Save the target floor
        this.elevatorTargetFloor = targetFloor;

        // Counts the time it will take the eleator to travel in miliseconds
        int elevDelay = (int) (getElevatorTravelTime(currentElevatorFloor, targetFloor) * 1000);
        // Creates a runnable, that should be excecuted after the elevator arrives
        Runnable onArrival = () -> {
            moveToNext(false, targetFloor);
        };
        // Schedules the onArrival code after the travel time of the elevator, and the
        // boarding time
        scheduler.schedule(onArrival, elevDelay + (int) (getWaitTime(targetFloor) * 1000),
                TimeUnit.MILLISECONDS);
    }

    /**
     * Returns the time stamp {@code String}. to append before an event log.
     *
     * @return {@code String} of a log time stamp. It's spaced and with a dividor.
     */
    private String timeStamp() {
        LocalTime time = LocalTime.now();
        return "> " + time.toString() + " | ";
    }

    /**
     * Determines the shortest time the elevator has to wait on a floor, in order to
     * all zombies to board, and unboard.
     *
     * @param onFloor - target floor.
     * @return {@code double} of the time in seconds.
     */
    private double getWaitTime(int onFloor) {
        boolean hasWaiting = false;
        for (ElevatorRequest e : this.waitingForElevatorRequests) {
            if (e.getTargetFloor() == onFloor) {
                hasWaiting = true;
            }
        }
        return hasWaiting ? this.ZOMBIE_BOARD_TIME : this.ZOMBIE_UNBOARD_TIME;
    }

    /**
     * Gets the time it will take the elevator to travel from a floor to another.
     *
     * @param currentFloor - current floor of the elevator.
     * @param targetFloor  - target floor of the elevator.
     * @return {@code double} of the time in seconds.
     */
    private double getElevatorTravelTime(int currentFloor, int targetFloor) {
        int floors = Math.abs(targetFloor - currentFloor);
        return (double) (floors * this.ELEVATOR_SPEED);
    }

    /////////////////
    // Nested classes
    ////////////////

    /**
     * Class containing the data of an elevator request. Has 2 fields, an
     * {@code int} field indicating the target elevator floor requested, and time,
     * that idicating the time the request was created.
     *
     */
    @SuppressWarnings("unused")
    private final class ElevatorRequest {

        /////////////////
        // Constants
        ////////////////

        private final int DEFAULT_ELEVATOR_INDEX = 0;

        /////////////////
        // Class variables
        ////////////////

        private int targetFloor;
        private LocalTime timeCreated;

        /////////////////
        // Contructors
        ////////////////

        /**
         * Creates a new {@code ElevatorRequest}.
         * 
         * @param targetFloor - number of the floor this {@code ElevatorRequest} was
         *                    requested to.
         * @param timeCreated - time of when was this request created.
         */
        public ElevatorRequest(int targetFloor, LocalTime timeCreated) {
            this.targetFloor = targetFloor;
            this.timeCreated = timeCreated;
        }

        /////////////////
        // Accesor methods
        ////////////////

        /**
         * Gets the floor number of the {@code ElevatorRequest}.
         * 
         * @return - {@code int} of the floor.
         */
        public int getTargetFloor() {
            return this.targetFloor;
        }

        /**
         * Gets the {@code LocalTime} of this {@code ElevatorObject}.
         * 
         * @return the time.
         */
        public LocalTime getTimeCreated() {
            return this.timeCreated;
        }

        /////////////////
        // Modifier methods
        ////////////////

        /**
         * Sets the floor number value of this {@code ElevatorRequest}.
         * 
         * @param newValue - new value.
         */
        public void setTargetFloor(int newValue) {
            this.targetFloor = newValue;
        }

        /**
         * Sets the time created field.
         * 
         * @param newValue - new value.
         */
        public void setTimeCreated(LocalTime newValue) {
            this.timeCreated = newValue;
        }

        /////////////////
        // Other public methods
        ////////////////

        /**
         * Moves the elevator to the floor this {@code ElevatorRequest} has.
         * 
         */
        public void runRequest() {
            boolean b = gotoFloor(this.DEFAULT_ELEVATOR_INDEX, this.targetFloor);
        }

        /////////////////
        // ToString
        ////////////////

        @Override
        public String toString() {
            return "Target floor: " + this.targetFloor + ", Time created: " + this.timeCreated.toString();
        }
    }
}
