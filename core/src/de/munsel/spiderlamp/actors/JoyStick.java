package de.munsel.spiderlamp.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by munsel on 28.02.16.
 */
public class JoyStick extends Actor {
    private static final String TAG = JoyStick.class.getSimpleName();

    private TextureRegion background, knob;
    private Actor knobActor;
    private Vector2 knobPos;
    private Vector2 center;

    public float joystickRadius;

    public JoyStick(Skin skin)
    {
        this.background = skin.getRegion("joystick-background");
        this.knob = skin.getRegion("xy-cursor");
        this.knobActor = new Image(knob);
        this.joystickRadius = this.background.getRegionHeight()/2-knob.getRegionWidth()/2;
        setSize( background.getRegionWidth(), background.getRegionHeight());

        center = new Vector2(getX()+background.getRegionWidth()/2,
                getY()+background.getRegionHeight()/2);

        knobPos = new Vector2(center);
    }
    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(background, getX(),getY());
        //batch.draw(knob, knobPos.x, knobPos.y);
        super.draw(batch, parentAlpha);
        knobActor.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        knobActor.act(delta);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        center.set(getX() + background.getRegionWidth() / 2,
                getY() + background.getRegionHeight() / 2);
        knobActor.setPosition(center.x-knobActor.getWidth()/2,
                center.y-knobActor.getHeight()/2);
    }

    public void setKnobPos(float x, float y){
        Vector2 relativeCenter = new Vector2(center.x-getX(),center.y-getY());
        float phi = MathUtils.atan2( y - relativeCenter.y,
                x- relativeCenter.x);
        float yDiff = MathUtils.sin(phi)*joystickRadius;
        float xDiff = MathUtils.cos(phi)*joystickRadius;
        Vector2 circlePoint = new Vector2(relativeCenter.x+xDiff,
                relativeCenter.y+yDiff);
        if (relativeCenter.dst(circlePoint)> relativeCenter.dst(x,y))
            knobPos.set(getX()+x-knob.getRegionWidth()/2,
                getY()+y-knob.getRegionHeight()/2);
        else knobPos.set(circlePoint.x+getX()-knob.getRegionWidth()/2,
                circlePoint.y+getY()-knob.getRegionHeight()/2);
        knobActor.setPosition(knobPos.x, knobPos.y);
    }


    public void setKnobToCenter(){
        knobActor.addAction(Actions.moveTo(center.x - knob.getRegionWidth() / 2,
                center.y - knob.getRegionHeight() / 2, 0.25f, Interpolation.bounce));
        knobPos.set(center.x-knob.getRegionWidth()/2,
                center.y-knob.getRegionHeight()/2);
    }


}
