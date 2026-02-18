package ai;
 
import java.util.ArrayList;
import java.util.Collections;
 
import game.GameObject;
import game.PowerUp;
import game.Tank;
import game.TankAIBase;
import game.Target;
import game.Vec2;
 
public class OtherAI extends TankAIBase {
    // public String getPlayerName() {
    //     return "DORE";
    // }
    // public int getPlayerPeriod() {
    //     return 1;              
    // }
       
 
    public boolean updateAI() {
        if (tank.getShotRange() < 7) {    
            shoot();                      
            goToPowerUps();
        } else {
            if (tank.getPos().y != 6.5) {  
                moveToMiddle();
                return true;
            }
            seekTargets();
            shoot();
        }
           
        return true;
    }
 
    public void shoot() {
        if (distanceFromTank(findClosestTarget()) <= getTankShotRange()) {
            Vec2 toTarget = new Vec2(findClosestTarget().x - getTankPos().x, findClosestTarget().y - getTankPos().y);
            queueCmd("shoot", toTarget);
        }
    }
 
    public Vec2 findClosestTarget() {
        Target[] allTargets = getTargets();
        Target closest = allTargets[0];
        for (Target element : allTargets) {
            if (distanceFromTank(element.getPos()) < distanceFromTank(closest.getPos())) {
                closest = element;
            }
        }
        return closest.getPos();
    }
 
    public void moveToMiddle() {
        double distanceFromMiddle = 6.5 - tank.getPos().y;
        Vec2 toMiddle = new Vec2(0, distanceFromMiddle);
        if (distanceFromMiddle == 0.0) {
            return;
        }
        queueCmd("move", toMiddle);
    }
 
    public void seekTargets() {
        double distanceFromTargetX = findClosestTarget().x - getTankPos().x;
        double distanceFromTargetY = findClosestTarget().y - getTankPos().y;
        double pythagX = Math.pow((Math.pow(tank.getShotRange(), 2) - Math.pow(distanceFromTargetY, 2)) , 0.5);
        double minDistance;
        if (distanceFromTargetX <= 0) {
            minDistance = distanceFromTargetX + pythagX;
        } else {
            minDistance = distanceFromTargetX - pythagX;
        }
       
        if (tank.getShotRange() > distanceFromTank(findClosestTarget())) {
            return;
        }
        Vec2 horizontal = new Vec2(minDistance, 0);
        if (Math.abs(horizontal.x) <= 0.1) {
            if (horizontal.x < 0) {
                Vec2 negativeSmallMove = new Vec2(-0.1, 0);
                queueCmd("move", negativeSmallMove);
            } else {
                Vec2 positiveSmallMove = new Vec2(0.1, 0);
                queueCmd("move", positiveSmallMove);
            }
        } else {
            queueCmd("move", horizontal);
        }
    }
 
    public void goToPowerUps() {
        Vec2 horizontal = new Vec2(findClosestPowerUp().x - getTankPos().x, 0);
        Vec2 vertical = new Vec2(0, findClosestPowerUp().y - getTankPos().y);
        if (horizontal.x != 0) {
            queueCmd("move", horizontal);
        }
        if (vertical.y != 0) {
            queueCmd("move", vertical);
        }
    }
 
    public Vec2 findClosestPowerUp() {
        PowerUp[] allPowerUps = getPowerUps();
        PowerUp currClosest = allPowerUps[0];
        for (PowerUp currElement : allPowerUps) {
            if (currElement.getType().equals("R") || currElement.getType().equals("S")) {
                if (distanceFromTank(currElement.getPos()) < distanceFromTank(currClosest.getPos())) {
                    currClosest = currElement;
                }
            }
        }
        return currClosest.getPos();
    }
 
    public double distanceFromTank(Vec2 position) {
        double tankX = getTankPos().x;
        double tankY = getTankPos().y;
        return Math.sqrt(Math.pow((position.x - tankX), 2) + Math.pow((position.y - tankY), 2));
    }
}