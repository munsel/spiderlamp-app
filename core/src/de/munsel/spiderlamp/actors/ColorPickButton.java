package de.munsel.spiderlamp.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;

/**
 * Created by munsel on 04.11.15.
 */
public class ColorPickButton extends Actor {

    private ShapeRenderer shapeRenderer;
    private Color color;

    public ColorPickButton(){
        this.setTouchable(Touchable.enabled);
        shapeRenderer = new ShapeRenderer();
        color = new Color();

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        shapeRenderer.rect(getX(),getY(), getWidth(), getHeight());
        shapeRenderer.end();
        batch.begin();
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }


}
