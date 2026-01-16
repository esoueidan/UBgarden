/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ugarden.view;

import fr.ubx.poo.ugarden.game.Direction;
import fr.ubx.poo.ugarden.go.personage.Player;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class SpritePlayer extends Sprite {
    private final ColorAdjust effect = new ColorAdjust();

    public SpritePlayer(Pane layer, Player player) {
        super(layer, null, player);
        updateImage();
    }

    @Override
    public void updateImage() {
        Player player = (Player) getGameObject();
        if(player.isInvincibility()){
            effect.setContrast(0.8);
            effect.setBrightness(0.8);
            effect.setSaturation(0.8);
            effect.setHue(0.8);
            player.setModified(true);
        }
        else{
            effect.setContrast(0);
            effect.setBrightness(0);
            effect.setSaturation(0);
            effect.setHue(0);
            player.setModified(true);
        }
        Image image = getImage(player.getDirection());
        setImage(image,effect);
    }

    private Image getImage(Direction direction) {
        return ImageResourceFactory.getInstance().getPlayer(direction);
    }
}
