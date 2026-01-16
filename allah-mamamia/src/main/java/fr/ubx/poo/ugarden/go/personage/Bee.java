/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ugarden.go.personage;

import fr.ubx.poo.ugarden.engine.Timer;
import fr.ubx.poo.ugarden.game.Direction;
import fr.ubx.poo.ugarden.game.Game;
import fr.ubx.poo.ugarden.game.Position;
import fr.ubx.poo.ugarden.go.GameObject;
import fr.ubx.poo.ugarden.go.Movable;
import fr.ubx.poo.ugarden.go.TakeVisitor;
import fr.ubx.poo.ugarden.go.WalkVisitor;
import fr.ubx.poo.ugarden.go.decor.Decor;
import fr.ubx.poo.ugarden.go.bonus.*;

import static fr.ubx.poo.ugarden.game.Direction.DOWN;
import static fr.ubx.poo.ugarden.game.Direction.random;

public class Bee extends GameObject implements Movable, WalkVisitor {

    private Direction direction;

    private boolean moveRequested = false;

    public Direction getDirection() {
        return direction;
    }

    public void requestMove(Timer t) {
        if (t.isRunning()){
            moveRequested = false;
        }
        else {
            direction = direction.random();
            setModified(true);
            moveRequested = true;
            t.start();
        }

    }

    @Override
    public final boolean canMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        Decor next = game.world().getGrid().get(nextPos);
        if(next != null){
            if (next.walkableBy(this)) {return true;}}
        return false;
    }

    public void update(long now) {
        if (moveRequested) {
            if (canMove(direction)) {
                doMove(direction);
            }
        }
        moveRequested = false;
    }

    public Bee(Game game,Position position) {
        super(game,position);
        this.direction = Direction.DOWN;
    }

    @Override
    public void doMove(Direction direction) {
        if(direction != null) {
            // This method is called only if the move is possible, do not check again
            Position nextPos = direction.nextPosition(getPosition());
            Decor next = game.world().getGrid().get(nextPos);
            setPosition(nextPos);
            if (next != null)
                next.takenBy(this);//a quoi ca sert ca ?
        }
    }

    @Override
    public String toString() {
        return "Bee";
    }


}
