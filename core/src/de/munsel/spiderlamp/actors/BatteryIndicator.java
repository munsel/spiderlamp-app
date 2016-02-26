package de.munsel.spiderlamp.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by munsel on 09.02.16.
 */
public class BatteryIndicator extends Actor {

    private Label label;

    public BatteryIndicator(Skin skin)
    {
        label = new Label("...",skin,  "instruction-list");
    }



    /**
     * gets the measuered ADC value from the lamp
     * and sets it
     */
    public void setBatteryValue(int adcValue)
    {
        label.setText(Integer.toString(adcValue));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        label.draw(batch,parentAlpha);
        super.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        label.act(delta);
    }

    @Override
    public void setPosition(float x, float y) {
        label.setPosition(x,y);
        super.setPosition(x, y);
    }
}
