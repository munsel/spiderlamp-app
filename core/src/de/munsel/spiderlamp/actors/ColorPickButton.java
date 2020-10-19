package de.munsel.spiderlamp.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by munsel on 04.11.15.
 */
public class ColorPickButton extends Actor {

    private Color color;
    private TextureRegion background, foreground;

    public ColorPickButton(Skin skin){
        this.setTouchable(Touchable.enabled);
        background = skin.getRegion("colorpick-bg");
        foreground = skin.getRegion("colorpick-fg");
        setSize(background.getRegionWidth(), background.getRegionHeight());
        color = new Color();

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        color.set(color.r, color.g, color.b, parentAlpha);
        batch.setColor(color);
        batch.draw(background, getX(), getY());
        batch.setColor(1,1,1,1);
        batch.draw(foreground, getX(), getY());

    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }


}
