package fr.ubx.poo.ugarden.game;

import fr.ubx.poo.ugarden.engine.GameEngine;
import fr.ubx.poo.ugarden.go.personage.Player;
import fr.ubx.poo.ugarden.go.personage.Bee;

import java.util.ArrayList;
import java.util.Collection;


public class Game {

    private final Configuration configuration;
    public Configuration configuration() {
        return configuration;
    }
    private final World world;

    private Collection<Position> beePosition;
    private Collection<Bee> bee;
    private final Player player;
    private boolean switchLevelRequested = false;
    private int switchLevel;

    public Game(World world, Configuration configuration, Position playerPosition, Collection<Position> beePositionCollection) {
        this.configuration = configuration;
        this.world = world;
        player = new Player(this, playerPosition);
        this.beePosition = beePositionCollection;
        for (var beePosition : beePositionCollection) {
            if (bee == null) {
                bee = new ArrayList<Bee>();
            }
            bee.add(new Bee(this, beePosition));
        }
    }


    public void setBeePosition(Collection<Position> beePosition) {
        this.beePosition = beePosition;
    }

    public void updateBee (){
        if (bee != null) bee.clear();
        for (var Pos : beePosition) {
            if (bee == null) {
                bee = new ArrayList<Bee>();
            }
            bee.add(new Bee(this, Pos));
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    public Collection<Bee> getBee() {
        return this.bee;
    }

    public World world() {
        return world;
    }

    public boolean isSwitchLevelRequested() {
        return switchLevelRequested;
    }

    public int getSwitchLevel() {
        return switchLevel;
    }

    public void requestSwitchLevel(int level) {
        this.switchLevel = level;
        switchLevelRequested = true;
    }

    public void clearSwitchLevel() {
        switchLevelRequested = false;
    }

    public void levelupdate(){
        GameEngine.currentlevel = GameEngine.currentlevel +1;
    }

}
