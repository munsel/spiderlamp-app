package de.munsel.spiderlamp.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by munsel on 08.03.16.
 */
public class JoyStickY extends Actor {
    private static final String TAG = JoyStickY.class.getSimpleName();

    private TextureRegion background;
    private TextureRegion knob;
    private Actor knobActor;
    private float knobPos;
    private float center;

    public JoyStickY(Skin skin){
        background = skin.getRegion("joystick-y-background");
        knob = skin.getRegion("joystick-y-knob");
        knobActor = new Image(knob);
        setSize(background.getRegionWidth(), background.getRegionHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(background, getX(), getY());
        //batch.draw(knob, getX(), knobPos);
        knobActor.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        knobActor.act(delta);
    }

    public void setKnobPos(float y){
        knobPos= getY()+y-knob.getRegionHeight()/2;
        if(y>getHeight()) knobPos = getY()+getHeight()-knobActor.getHeight()/2;
        if(y<0) knobPos = getY()-knobActor.getHeight()/2;
        knobActor.setPosition(getX(), knobPos);
    }

    public void setKnobToCenter(){
        knobPos = center-knob.getRegionHeight()/2;
        knobActor.addAction(Actions.moveTo(getX(), knobPos, 0.25f, Interpolation.bounce));
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        center = y+background.getRegionHeight()/2;
        knobActor.setPosition(x, center);
    }
}
