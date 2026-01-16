/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ugarden.go;


import fr.ubx.poo.ugarden.go.personage.*;

public interface Takeable {
    default void takenBy(Player player) {}

    default void takenBy(Bee bee) {}
}
