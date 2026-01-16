package fr.ubx.poo.ugarden.launcher;

import fr.ubx.poo.ugarden.engine.GameEngine;
import fr.ubx.poo.ugarden.game.*;
import fr.ubx.poo.ugarden.go.personage.Bee;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.Properties;
import static fr.ubx.poo.ugarden.launcher.MapEntity.fromCode;

public class GameLauncher {

    private static class LoadSingleton {
        static final GameLauncher INSTANCE = new GameLauncher();
    }
    private GameLauncher() {}

    public MapLevel[] mapLevel;

    public static GameLauncher getInstance() {
        return LoadSingleton.INSTANCE;
    }

    private int integerProperty(Properties properties, String name, int defaultValue) {
        return Integer.parseInt(properties.getProperty(name, Integer.toString(defaultValue)));
    }

    private boolean booleanProperty(Properties properties, String name, boolean defaultValue) {
        return Boolean.parseBoolean(properties.getProperty(name, Boolean.toString(defaultValue)));
    }

    private Configuration getConfiguration(Properties properties) {

        // Load parameters
        int playerLives = integerProperty(properties, "playerLives", 5);
        int playerInvincibilityDuration = integerProperty(properties, "playerInvincibilityDuration", 4);
        int beeMoveFrequency = integerProperty(properties, "beeMoveFrequency", 1);
        int playerEnergy = integerProperty(properties, "playerEnergy", 100);
        int energyBoost = integerProperty(properties, "energyBoost", 50);
        int energyRecoverDuration = integerProperty(properties, "energyRecoverDuration", 5);
        int diseaseDuration = integerProperty(properties, "diseaseDuration", 5);
        int levels = integerProperty(properties,"levels",1);
        boolean compression = booleanProperty(properties,"compression",true);
        return new Configuration(playerLives, playerEnergy, energyBoost, playerInvincibilityDuration, beeMoveFrequency, energyRecoverDuration, diseaseDuration, levels, compression);
    }

    public Game load() {
        Properties emptyConfig = new Properties();
        MapLevel mapLevel = new MapLevelDefault();
        Position playerPosition = mapLevel.getPlayerPosition();
        if (playerPosition == null)
            throw new RuntimeException("Player not found");
        Collection<Position> beeCollection = mapLevel.getBeePositionCollection();
        Configuration configuration = getConfiguration(emptyConfig);
        WorldLevels world = new WorldLevels(1);
        Game game = new Game(world, configuration, playerPosition,beeCollection);
        Map level = new Level(game, 1, mapLevel);
        world.put(1, level);
        return game;
    }

    public Game load(Reader reader) throws IOException {
        Properties properties = new Properties();
        properties.load(reader);
        Configuration configuration = getConfiguration(properties);
        mapLevel = new MapLevel[configuration.levels()];
        String [] Level = new String[configuration.levels()];
        for (int i =0; i< configuration.levels(); i++){
            Level[i] = properties.getProperty("level"+Integer.toString(i+1));
        }
        if(configuration.compression()) {
            for (int i =0; i< configuration.levels(); i++){
                mapLevel[i] = loadstrLRE(Level[i+GameEngine.currentlevel-1]);
            }
        }else{
            for (int i =0; i< configuration.levels(); i++){
                mapLevel[i] = loadstr(Level[i+GameEngine.currentlevel-1]);
            }
        }
        Position playerPosition = mapLevel[GameEngine.currentlevel-1].getPlayerPosition();
        if (playerPosition == null)
            throw new RuntimeException("Player not found");
        Collection<Position> beeCollection = mapLevel[GameEngine.currentlevel-1].getBeePositionCollection();
        WorldLevels world = new WorldLevels(configuration.levels());
        Game game = new Game(world, configuration, playerPosition,beeCollection);

        for (int i =0; i< configuration.levels(); i++){
            Map level = new Level(game, i+GameEngine.currentlevel, mapLevel[i+GameEngine.currentlevel-1]);
            world.put(i+GameEngine.currentlevel, level);
        }

        return game;
    }

    public MapLevel loadstrLRE(String s) {
        String[] string = s.split(Character.toString('x'));
        int length=0;
        int a = 0;
        while (a < string[0].length()) {
            char c = string[0].charAt(a);
            if (a+1 < string[0].length()){
                char next = string[0].charAt(a + 1);
                if (Character.isDigit(next)) {
                    length += Character.getNumericValue(next);
                    a += 2;
                } else {
                    length++;
                    a++;
                }
            } else {
                length++;
                a++;
            }
        }
        MapLevel mapLevel = new MapLevel(length, string.length);
        int j = 0;
        for (int i = 0; i < mapLevel.height(); ++i) {
            int prevIndex=0;
            int currIndex=1;
            while (j < mapLevel.width()) {
                char prevChar = string[i].charAt(prevIndex);
                if (currIndex >= string[i].length()){
                    mapLevel.set(j, i, MapEntity.fromCode(prevChar));
                    break;
                }
                char currChar = string[i].charAt(currIndex);
                if (Character.isDigit(currChar)) {
                    for (int k = 0; k < Character.getNumericValue(currChar); k++) {
                        mapLevel.set(j, i, MapEntity.fromCode(prevChar));
                        j++;
                    }
                    prevIndex+=2;
                    currIndex+=2;
                }
                else {
                    mapLevel.set(j, i, MapEntity.fromCode(prevChar));
                    j++;
                    prevIndex+=1;
                    currIndex+=1;
                    prevChar=currChar;
                }
            }
            j=0;
        }
        return mapLevel;
    }
    public MapLevel loadstr(String s) {
        int numRows = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == 'x') {
                numRows++;
            }
        }
        int numCols = s.length() / numRows ;
        MapLevel oui = new MapLevel(numCols-1, numRows);
        //System.out.println("cols " + numCols +" rows"+numRows +" lenght"+s.length());
        int z=0;
        for(int j=0; j<oui.height(); j++){
            for(int i=0; i<oui.width();i++){
                if (z < s.length()) {
                    char x = s.charAt(z);
                    z++;
                    if (x != 'x') {
                        oui.set(i, j, fromCode(x));
                        //System.out.println("Index out of bounds : i=" + i + ", j=" + j + ", x=" + x);
                    }else{i--;}
                }
            }
        }
        return oui;
    }
}
