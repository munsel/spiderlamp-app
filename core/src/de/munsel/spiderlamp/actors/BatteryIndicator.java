package de.munsel.spiderlamp.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by munsel on 09.02.16.
 */
public class BatteryIndicator extends Actor {

    private Label label;
    private Sprite batteryFilling;
    private Sprite batteryFrame;

    private final int MIN_ADC_VALUE = 534;
    private final int MAX_ADC_VALUE = 652;

    private float maxWidth;


    public BatteryIndicator(Skin skin)
    {
        label = new Label("...",skin,  "instruction-list");
        TextureRegion filling = skin.getRegion("battery-filling");


        this.batteryFilling = new Sprite(filling,0,0,
                filling.getRegionWidth(),
                filling.getRegionHeight());

        maxWidth = filling.getRegionWidth();

        TextureRegion frame = skin.getRegion("battery-frame");
        this.batteryFrame = new Sprite(frame, 0,0,
                frame.getRegionWidth(),
                frame.getRegionHeight());

        this.setSize(filling.getRegionWidth(),filling.getRegionHeight());

    }



    /**
     * gets the measuered ADC value from the lamp
     * and sets it
     */
    public void setBatteryValue(int adcValue)
    {
        float percentage =((float)(adcValue-MIN_ADC_VALUE)/(float)(MAX_ADC_VALUE- MIN_ADC_VALUE));
        float width = maxWidth*percentage;

        label.setText(Integer.toString((int)(percentage*100))+"%");
        batteryFilling.setSize(width, batteryFilling.getHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        batteryFilling.draw(batch);
        batteryFrame.draw(batch);
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
        label.setPosition(
                x + (batteryFrame.getWidth()-label.getWidth())/3,
                y + (batteryFrame.getHeight()-label.getHeight())/2);
        batteryFrame.setPosition(x, y);
        batteryFilling.setPosition(x,y);
        super.setPosition(x, y);
    }
}
