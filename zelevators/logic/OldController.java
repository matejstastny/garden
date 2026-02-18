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
 
 import java.time.LocalTime;
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
 public class OldController implements ElevatorController {
  
     /////////////////
     // Constants
     ////////////////
  
     private final String PLAYER_NAME = "Matěj Šťastný";
     private final int PLAYER_PERIOD = 6;
     private final double ELEVATOR_SPEED = 0.5; // How many seconds per floor
     private final double ZOMBIE_BOARD_TIME = 5; // How many seconds from the eleator arrival to zombie in
     private final double ZOMBIE_UNBOARD_TIME = 5;
  
     /////////////////
     // Class variables
     ////////////////
  
     private Game game;
     private boolean toDestination;
     private boolean isFirstReq;
     private ArrayList<ElevatorRequest> waitList;
     private ArrayList<ElevatorRequest> gotoList;
     private ScheduledExecutorService scheduler;
  
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
         this.game = game;
         this.isFirstReq = true;
         this.waitList = new ArrayList<ElevatorRequest>();
         this.gotoList = new ArrayList<ElevatorRequest>();
         this.scheduler = Executors.newSingleThreadScheduledExecutor();
  
         // Runnable goToTarget = () -> {
         // gotoFloor(0, 3);
         // System.out.println("Going to 3");
         // };
  
         // System.out.println("Going to 5");
         // gotoFloor(0, 5);
  
         // scheduler.schedule(goToTarget, 5, TimeUnit.SECONDS);
  
     }
  
     /**
      * Event: "outside-the-elevator" request, requesting an elevator.
      * The event will be triggered with the request is created/enabled & when it is
      * cleared (reqEnable indicates which).
      *
      * @param floorIdx
      * @param dir
      * @param reqEnable
      */
     public void onElevatorRequestChanged(int floorIdx, Direction dir, boolean reqEnable) {
         // System.out.println("onElevatorRequestChanged(" + floorIdx + ", " + dir + ", "
         // + reqEnable + ")");
  
         // Add the request to the wait list, if new request
         if (reqEnable) {
             this.waitList.add(new ElevatorRequest(floorIdx, dir, reqEnable));
         }
  
         if (this.isFirstReq) {
             this.isFirstReq = false;
             this.toDestination = false;
             moveToNext(true, 0);
         }
     }
  
     /**
      * Event: "inside-the-elevator" request, requesting to go to a floor.
      * The event will be triggered with the request is created/enabled & when it is
      * cleared (reqEnable indicates which).
      *
      * @param elevatorIdx
      * @param floorIdx
      * @param reqEnable
      */
     public void onFloorRequestChanged(int elevatorIdx, int floorIdx, boolean reqEnable) {
         System.out.println(
                 timeStamp() + "onFloorRequesteChanged(" + elevatorIdx + ", " + floorIdx + ", " + reqEnable + ")");
  
         // Add the request to the que list, if new request
         if (reqEnable) {
             this.gotoList.add(new ElevatorRequest(floorIdx, null, reqEnable));
         }
     }
  
     /**
      * Event: Elevator has arrived at the floor & doors are open.
      *
      * @param elevatorIdx
      * @param floorIdx
      * @param traverDirection
      */
     public void onElevatorArrivedAtFloor(int elevatorIdx, int floorIdx, Direction travelDirection) {
         // System.out.println(timeStamp() + "onElevatorArrivedAtFloor(" + elevatorIdx +
         // ", " + floorIdx + ", " + travelDirection + ")");
  
         // double delay = getDelay();
  
         // Runnable afterDelay = () -> {
         // moveToNext(false, floorIdx);
         // };
  
         // scheduler.schedule(afterDelay, (int)delay*1000, TimeUnit.MILLISECONDS);
  
         // if (this.toDestination) {
         // this.gotoList.remove(0);
         // if (this.waitList.size() > 0) {
         // this.waitList.get(0).runRequest();
         // this.waitList.remove(0);
         // this.toDestination = false;
         // }
  
         // }
         // else {
         // this.waitList.remove(0);
         // if (this.gotoList.size() > 0) {
         // this.gotoList.get(0).runRequest();
         // this.gotoList.remove(0);
         // this.toDestination = true;
         // }
         // }
     }
  
     /**
      * Event: Called each frame of the simulation (i.e. called continuously)
      *
      * @param deltaTime
      */
     public void onUpdate(double deltaTime) {
         if (game == null) {
             return;
         }
     }
  
     /////////////////
     // Private methods
     ////////////////
  
     private boolean getGotoDestination() {
         assert (this.gotoList.size() > 0 || this.waitList.size() > 0) : "Arraylists empty";
  
         boolean bol;
  
         if (this.toDestination) {
             // Previously going to destination, and wait list isn't empty
             if (waitList.size() > 0) {
                 bol = false;
             }
  
             // Previously going to destination, but waitlist empty
             else {
                 bol = true;
             }
         }
  
         else {
             // Previously going for new zombie on waitlist, and the gotoList isn't empty
             if (this.gotoList.size() > 0) {
                 bol = true;
             }
  
             // Previously goig for new zombie, but the gotoList empty
             else {
                 bol = false;
             }
         }
  
         return bol;
     }
  
     private void moveToNext(boolean init, int currFoor) {
         System.out.println("GT: " + this.gotoList);
         System.out.println("WL: " + this.waitList);
         if (!init) {
             // if (this.toDestination) {
             // this.gotoList.remove(0);
             // }
             // else {
             // this.waitList.remove(0);
             // }
             for (int i = 0; i < this.gotoList.size(); i++) {
                 ElevatorRequest e = this.gotoList.get(i);
                 if (e.getFloorNum() == currFoor) {
                     gotoList.remove(i);
                     i--;
                 }
             }
             for (int i = 0; i < this.waitList.size(); i++) {
                 ElevatorRequest e = this.waitList.get(i);
                 if (e.getFloorNum() == currFoor) {
                     waitList.remove(i);
                     i--;
                 }
             }
         }
  
         this.toDestination = getGotoDestination();
         int targetFloor;
  
         if (this.toDestination) {
             this.gotoList.get(0).runRequest();
             targetFloor = this.gotoList.get(0).getFloorNum();
         } else {
             this.waitList.get(0).runRequest();
             targetFloor = this.waitList.get(0).getFloorNum();
         }
  
         int elevatorTravelTimeInMiliseconds = (int) (getElevatorTravelTime(currFoor, targetFloor) * 1000);
         Runnable afterElevator = () -> {
             moveToNext(false, targetFloor);
         };
         scheduler.schedule(afterElevator, elevatorTravelTimeInMiliseconds + (int) (this.ZOMBIE_UNBOARD_TIME * 1000),
                 TimeUnit.MILLISECONDS);
     }
  
     private String timeStamp() {
         LocalTime time = LocalTime.now();
         return "> " + time.toString() + " | ";
     }
  
     private double getDelay() {
  
         double delay;
  
         if (this.toDestination) {
             delay = this.ZOMBIE_UNBOARD_TIME;
         } else {
             delay = this.ZOMBIE_BOARD_TIME;
         }
  
         return delay;
     }
  
     private double getElevatorTravelTime(int currFloor, int targetFloor) {
         int floors = Math.abs(targetFloor - currFloor);
         return (double) (floors * this.ELEVATOR_SPEED);
     }
  
     /////////////////
     // Nested classes
     ////////////////
  
     @SuppressWarnings("unused")
     private final class ElevatorRequest {
  
         private final int ELEVATOR_INDEX = 0;
  
         private int floorNum;
         private Direction dir;
         private boolean reqEnable;
  
         public ElevatorRequest(int floorNum, Direction dir, boolean reqEnable) {
             System.out.println("New request: " + floorNum);
             this.floorNum = floorNum;
             this.dir = dir;
             this.reqEnable = reqEnable;
         }
  
         public void runRequest() {
             boolean b = gotoFloor(this.ELEVATOR_INDEX, this.floorNum);
             System.out.println(timeStamp() + "Going to(" + this.floorNum + ")");
         }
  
         public int getFloorNum() {
             return this.floorNum;
         }
  
         @Override
         public String toString() {
             return "FNum: " + this.floorNum;
         }
     }
  
 }