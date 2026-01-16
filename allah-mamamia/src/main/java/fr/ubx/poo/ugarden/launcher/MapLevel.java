package fr.ubx.poo.ugarden.launcher;

import fr.ubx.poo.ugarden.game.Game;
import fr.ubx.poo.ugarden.go.personage.Bee;
import fr.ubx.poo.ugarden.game.Position;

import java.util.*;

import static fr.ubx.poo.ugarden.launcher.MapEntity.Grass;
import static fr.ubx.poo.ugarden.launcher.MapEntity.Player;
import static fr.ubx.poo.ugarden.launcher.MapEntity.Bee;
import static fr.ubx.poo.ugarden.launcher.MapEntity.*;

public class MapLevel {

    private final int width;
    private final int height;
    private final MapEntity[][] grid;
    private Collection<Position> beeCollection;
    private Position playerPosition = null;

    public MapLevel(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new MapEntity[height][width];
    }

    public int width() {
        return width;    }

    public int height() {
        return height;
    }

    public MapEntity get(int i, int j) {
        return grid[j][i];
    }

    public void set(int i, int j, MapEntity mapEntity) {
        grid[j][i] = mapEntity;
    }

    public Position getPlayerPosition() {
        for (int i = 0; i < width; i++){
            for (int j = 0; j < height; j++){
                if (grid[j][i] == Player) {
                    if (playerPosition != null)
                        throw new RuntimeException("Multiple definition of player");
                    set(i, j, Grass);
                    // Player can be only on level 1
                    playerPosition = new Position(1, i, j);
                }
                if(grid[j][i] == DoorPrevOpened){
                    set(i, j, DoorPrevOpened);
                    playerPosition = new Position(2, i, j);
                }
            }
        }
        return playerPosition;
    }

    public Collection<Position> updateBeePositionCollection(){
        if (beeCollection != null)
            beeCollection.clear();
        getBeePositionCollection();
        return beeCollection;
    }

    public Collection<Position> getBeePositionCollection() {
        int cpt = 0;
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                if (grid[j][i] == Bee) {
                    set(i, j, Grass);
                    if (beeCollection == null){
                    beeCollection = new ArrayList<Position>();
                    }
                    beeCollection.add(new Position(1,i,j));
                }
        return beeCollection;
    }
}
