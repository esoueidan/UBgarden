/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ugarden.engine;

import fr.ubx.poo.ugarden.game.*;
import fr.ubx.poo.ugarden.game.Map;
import fr.ubx.poo.ugarden.go.bonus.*;
import fr.ubx.poo.ugarden.go.decor.*;
import fr.ubx.poo.ugarden.go.personage.Player;
import fr.ubx.poo.ugarden.go.personage.Bee;
import fr.ubx.poo.ugarden.launcher.GameLauncher;
import fr.ubx.poo.ugarden.view.*;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.*;


public final class GameEngine {

    public static AnimationTimer gameLoop;

    public static int  currentlevel = 1;

    private Timer[] timers;
    private final Game game;
    private final Player player;
    private Collection<Bee> bee;
    private final List<Sprite> sprites = new LinkedList<>();
    private final Set<Sprite> cleanUpSprites = new HashSet<>();
    private final Stage stage;
    public Timer invictus;
    private StatusBar statusBar;
    private Pane layer;
    private Input input;

    public GameEngine(Game game, final Stage stage) {
        this.stage = stage;
        this.game = game;
        this.player = game.getPlayer();
        this.bee = game.getBee();
        this.timers = new Timer[bee.size()];
        for (int i = 0; i<bee.size(); i++){
            timers[i] = new Timer (game.configuration().beeMoveFrequency());
        }
        this.invictus = new Timer(game.configuration().playerInvincibilityDuration());
        initialize();
        buildAndSetGameLoop();
    }

    private void initialize() {
        Group root = new Group();
        layer = new Pane();

        int height = game.world().getGrid().height();
        int width = game.world().getGrid().width();
        int sceneWidth = width * ImageResource.size;
        int sceneHeight = height * ImageResource.size;
        Scene scene = new Scene(root, sceneWidth, sceneHeight + StatusBar.height);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/application.css")).toExternalForm());

        stage.setScene(scene);
        stage.setResizable(false);
        stage.sizeToScene();
        stage.hide();
        stage.show();

        input = new Input(scene);
        root.getChildren().add(layer);
        statusBar = new StatusBar(root, sceneWidth, sceneHeight);

        // Create sprites
        int currentLevel = game.world().currentLevel();

        for (var decor : game.world().getGrid().values()) {
            if(decor instanceof Door) {
                sprites.add(new SpriteDoor(layer, (Door) decor));
            } else {
                sprites.add(SpriteFactory.create(layer, decor));
                decor.setModified(true);
                var bonus = decor.getBonus();
                if (bonus != null) {
                    sprites.add(SpriteFactory.create(layer, bonus));
                    bonus.setModified(true);
                }
            }
        }

        for (var bee_s : bee) {
            sprites.add(new SpriteBee(layer, bee_s));
        }

        sprites.add(new SpritePlayer(layer, player));
    }


    void buildAndSetGameLoop() {
        gameLoop = new AnimationTimer() {
            public void handle(long now) {
            checkLevel();

            // Check keyboard actions
            processInput(now);

            // Do actions
            update(now);
            checkCollision(now);

            // Graphic update
            for (int i = 0; i<bee.size(); i++){
                timers[i].update(now);
            }
            cleanupSprites();
            render();
            statusBar.update(game);
            }
        };
    }


    private void checkLevel() {
        if (game.isSwitchLevelRequested()) {
            // Find the new level to switch to
            // clear all sprites
            for (var decor : game.world().getGrid().values()) {
                decor.remove();
                    }
            game.clearSwitchLevel();
            // change the current level
            game.world().setCurrentLevel(game.getSwitchLevel());
            // Find the position of the door to reach
            // Set the position of the player
            int currentLevel = game.world().currentLevel();
            stage.close();

            for (var decor : game.world().getGrid().values()){
                if(decor instanceof Door_opened){
                     Position position = new Position(currentLevel,decor.getPosition().x(),decor.getPosition().y());
                     player.setPosition(position);
                     sprites.add(SpriteFactory.create(layer, decor));
                     decor.setModified(true);
                     sprites.add(new SpritePlayer(layer, player));
                     player.setModified(true);
                }
            }
            game.updateBee();
            for (var bee_s : bee) {
                bee_s.setPosition(new Position(currentLevel,bee_s.getPosition().x(),bee_s.getPosition().y()));
                sprites.add(new SpriteBee(layer, bee_s));
            }
            sprites.clear();
            initialize();
        }
    }

    private void checkCollision(long now) {

        // Check a collision between a bee and the player
        if(!player.invincibility(invictus)){
            for (var bee_s : bee) {
                if(bee_s.getPosition().equals(player.getPosition())){
                    player.addLives(-1);
                    System.out.println("I am taking the bee, AAAAAAAAAAAAAAAAAAAAAAAAAA");
                    bee_s.remove();
                    bee.remove(bee_s);
                    invictus.start();
                    break;
                }
            }
        }invictus.update(now);
        player.setModified(true);
    }

    private void processInput(long now) {
        if (input.isExit()) {
            gameLoop.stop();
            Platform.exit();
            System.exit(0);
        } else if (input.isMoveDown()) {
            player.requestMove(Direction.DOWN);
        } else if (input.isMoveLeft()) {
            player.requestMove(Direction.LEFT);
        } else if (input.isMoveRight()) {
            player.requestMove(Direction.RIGHT);
        } else if (input.isMoveUp()) {
            player.requestMove(Direction.UP);
        }
        input.clear();
        int cpt = 0;
        for (var bees : bee){
            bees.requestMove(timers[cpt]);
            cpt++;
        }
    }

    public void showMessage(String msg, Color color) {
        Text waitingForKey = new Text(msg);
        waitingForKey.setTextAlignment(TextAlignment.CENTER);
        waitingForKey.setFont(new Font(60));
        waitingForKey.setFill(color);
        StackPane root = new StackPane();
        root.getChildren().add(waitingForKey);
        Scene scene = new Scene(root, 400, 200, Color.WHITE);
        stage.setScene(scene);
        input = new Input(scene);
        stage.show();
        new AnimationTimer() {
            public void handle(long now) {
                processInput(now);
            }
        }.start();
    }


    private void update(long now) {
        player.update(now);
        for (var bees : bee){
            bees.update(now);
        }

        if (player.getLives() <= 0) {
            gameLoop.stop();
            showMessage("Perdu!", Color.RED);
        }
        if (player.v==1){
            gameLoop.stop();
            showMessage("Gagné!", Color.BLUE);
        }
    }

    public void cleanupSprites() {
        sprites.forEach(sprite -> {
            if (sprite.getGameObject().isDeleted()) {
                cleanUpSprites.add(sprite);
            }
        });
        cleanUpSprites.forEach(Sprite::remove);
        sprites.removeAll(cleanUpSprites);
        cleanUpSprites.clear();
    }

    private void render() {
        sprites.forEach(Sprite::render);
    }

    public void start() {
        gameLoop.start();
    }

}