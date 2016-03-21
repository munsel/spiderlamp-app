package de.munsel.spiderlamp.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by munsel on 08.03.16.
 */
public class JoyStickY extends Actor {
    private static final String TAG = JoyStickY.class.getSimpleName();

    private TextureRegion background;
    private TextureRegion knob;
    private float knobPos;
    private float center;

    public JoyStickY(Skin skin){
        background = skin.getRegion("joystick-y-background");
        knob = skin.getRegion("joystick-y-knob");

        setSize(background.getRegionWidth(), background.getRegionHeight());


    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(background, getX(), getY());
        batch.draw(knob, getX(), knobPos);
    }


    public void setKnobPos(float y){
        if (y>getHeight() || y<0)return;
        knobPos= getY()+y-knob.getRegionHeight()/2;
    }

    public void setKnobToCenter(){
        knobPos = center-knob.getRegionHeight()/2;

    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        center = y+background.getRegionHeight()/2;
        setKnobToCenter();
    }
}
