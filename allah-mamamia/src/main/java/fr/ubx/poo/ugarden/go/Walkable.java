package fr.ubx.poo.ugarden.go;


import fr.ubx.poo.ugarden.go.personage.*;

public interface Walkable {
    default boolean walkableBy(Player player) { return false; }

    default boolean walkableBy(Bee bee) { return false; }

    default int energyConsumptionWalk() { return 0; }
}
