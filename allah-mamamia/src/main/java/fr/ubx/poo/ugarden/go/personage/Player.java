/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ugarden.go.personage;

import fr.ubx.poo.ugarden.engine.GameEngine;
import fr.ubx.poo.ugarden.engine.Timer;
import fr.ubx.poo.ugarden.game.Direction;
import fr.ubx.poo.ugarden.game.Game;
import fr.ubx.poo.ugarden.game.Position;
import fr.ubx.poo.ugarden.go.GameObject;
import fr.ubx.poo.ugarden.go.Movable;
import fr.ubx.poo.ugarden.go.TakeVisitor;
import fr.ubx.poo.ugarden.go.WalkVisitor;
import fr.ubx.poo.ugarden.go.decor.*;
import fr.ubx.poo.ugarden.go.bonus.*;

public class Player extends GameObject implements Movable, TakeVisitor, WalkVisitor {

    private Direction direction;
    private boolean moveRequested = false;
    private int lives;
    private int energy;
    private int disease;
    public int v=0;
    private boolean key;

    public boolean isInvincibility() {
        return invincibility;
    }

    private boolean invincibility = false;
    private Timer recovery_timer;
    private Timer disease_duration;

    public int getKey(){
        if(key) return 1;
        return 0;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int n){energy=n;}

    public int getDisease(){return disease;}

    public void setDisease(int n){disease = n;}

    public int getLives() {
        return lives;
    }

    public void addLives(int nbr) {
        lives+=nbr;
    }

    public Direction getDirection() {
        return direction;
    }

    public void requestMove(Direction direction) {
        if (direction != this.direction) {
            this.direction = direction;
            setModified(true);
        }
        moveRequested = true;
    }

    @Override
    public final boolean canMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        Decor next = game.world().getGrid().get(nextPos);
        if(next != null){
        if (energy >= (game.world().getGrid().get(this.getPosition()).energyConsumptionWalk())*getDisease() && next.walkableBy(this)) {return true;}}
        return false;
   }

    public void update(long now) {
        if (moveRequested) {
            if (canMove(direction)) {
                doMove(direction);
            }
        }
        moveRequested = false;
        recovery_timer.update(now);
        if(!recovery_timer.isRunning()){
            if(energy < 100){
                setEnergy(energy+1);
                recovery_timer.start();
            }
        }
        disease_duration.update(now);
        if(!disease_duration.isRunning()){
            if(disease > 1){
                setDisease(disease-1);
                disease_duration.start();
            }
        }
    }

    public Player(Game game, Position position) {
        super(game, position);
        this.direction = Direction.DOWN;
        this.lives = game.configuration().playerLives();
        this.energy = game.configuration().playerEnergy();
        this.disease = 1;
        this.recovery_timer = new Timer(game.configuration().energyRecoverDuration());
        this.disease_duration = new Timer(game.configuration().diseaseDuration());
    }

    @Override
    public void take(Heart bonus) {
        addLives(1);
        System.out.println("I am taking the heart");
        bonus.remove();
    }

    public void take(PoisonedApple bonus) {
        setDisease(disease+1);
        System.out.println("I am taking the wrong apple,(vomi)");
        bonus.remove();
        disease_duration.start();
    }

    public void take(Apple bonus) {
        setDisease(1);
        if(energy + game.configuration().energyBoost() < 100){
            setEnergy(energy+game.configuration().energyBoost());
        }
        else{
            setEnergy(100);
        }
        System.out.println("I am taking the apple, that's a damn good apple and I know what I saying rn bc I often eat apples so dont try me !");
        bonus.remove();
    }

    public void take(Princess bonus) {
        System.out.println("I am taking the dirty little princess, humm ...");
        v=1;
    }

    public void take(Key bonus) {
        System.out.println("I FOUND THE KEY COME HERE PASSPARTOU!");
        key = true;
        bonus.remove();
    }

    public void take(Door door) {
        if (!door.isOpen() && key) {
            door.setDoorState(true);
            door.setModified(true);
            key = false;
        }else if (door.isOpen()){
            game.levelupdate();
            game.requestSwitchLevel(GameEngine.currentlevel);
        }
    }

    @Override
    public void doMove(Direction direction) {
        // This method is called only if the move is possible, do not check again
        Position nextPos = direction.nextPosition(getPosition());
        Decor next = game.world().getGrid().get(nextPos);
        setEnergy(energy-(game.world().getGrid().get(this.getPosition()).energyConsumptionWalk())*getDisease());

        recovery_timer = new Timer(game.configuration().energyRecoverDuration());
        recovery_timer.start();
        setPosition(nextPos);
        if (next != null)
            next.takenBy(this);
    }

    @Override
    public String toString() {
        return "Player";
    }

    public boolean invincibility(Timer invictus){
        if(invictus.isRunning()){
            return invincibility = true;
        }return invincibility = false;
    }


}
