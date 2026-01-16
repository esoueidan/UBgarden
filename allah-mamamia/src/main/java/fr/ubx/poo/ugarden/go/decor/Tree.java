/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ugarden.go.decor;
import fr.ubx.poo.ugarden.game.Position;
import fr.ubx.poo.ugarden.go.personage.*;

public class Tree extends Decor {
    public Tree(Position position) {
        super(position);
    }

    @Override
    public boolean walkableBy(Player player) {
        return false;
    }

    @Override
    public boolean walkableBy(Bee bee) {
        return false;
    }
}
