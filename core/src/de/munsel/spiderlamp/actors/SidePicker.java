package de.munsel.spiderlamp.actors;

import aurelienribon.tweenengine.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import de.munsel.spiderlamp.ControlPanelScreen;
import de.munsel.spiderlamp.tween.SpriteAccessor;

/**
 * select side to change the ropelength on that side
 * also show ropelength info
 */
public class SidePicker extends Actor{
    private static final String TAG = SidePicker.class.getSimpleName();

    /**
     * CONSTANTS
     */
    public static final float LTFX=50;
    public static final float LTFY=180;

    public static final float LDFX=50;
    public static final float LDFY=50;

    public static final float RTFX=180;
    public static final float RTFY=180;

    public static final float RDFX=180;
    public static final float RDFY=50;


    private Sprite leftUpSprite;
    private Sprite rightUpSprite;
    private Sprite leftDownSprite;
    private Sprite rightDownSprite;

    private Sprite lULabelBg;
    private Sprite rULabelBg;
    private Sprite lDLabelBg;
    private Sprite rDLabelBg;

    private Actor pickActor;

    private TextField lULabelTextfield;

    public interface SideCallbacks{
        void leftDown(boolean enable);
        void rightDown(boolean enable);
        void leftUp(boolean enable);
        void rightUp(boolean enable);
    }

    private SideCallbacks sideCallbacks;



    private class AnimationData{
        public boolean isAnimating, isOpen;
        public final int steps=100;
        public TweenCallback callback;
        public Vector2 from, to;
        public AnimationData(){
            isAnimating=false;
            isOpen = false;
            callback = new TweenCallback() {
                @Override
                public void onEvent(int i, BaseTween<?> baseTween) {
                    isAnimating=false;
                }
            };
            from = new Vector2();
            to = new Vector2();
        }
    }
    private AnimationData lU, rU, lD, rD;
    private TweenManager tweenManager;



    public SidePicker(Skin skin){
        this.setWidth(ControlPanelScreen.V_WIDTH*.4f);
        this.setHeight(ControlPanelScreen.V_WIDTH*.4f);
        this.setPosition(ControlPanelScreen.V_WIDTH*.1f, ControlPanelScreen.V_HEIGHT*.42f);
        TextureRegion region = skin.getRegion("sidepicker-knob");
        TextureRegion labelRegion = skin.getRegion("sidepicker-label");
        rightUpSprite = new Sprite(region);
        region.flip(false,true);
        rightDownSprite = new Sprite(region);
        region.flip(true, true);
        leftUpSprite = new Sprite(region);
        region.flip(false, true);
        leftDownSprite = new Sprite(region);

        tweenManager = new TweenManager();
        Tween.registerAccessor(Sprite.class, new SpriteAccessor());

        float gapSize = 5;
        lU = new AnimationData();
        lU.from.set(getX()+(getWidth()*.5f-leftDownSprite.getWidth()),
                getY()+getHeight()*.5f+gapSize);
        lU.to.set(lU.from.x+leftUpSprite.getWidth()-labelRegion.getRegionWidth()-leftUpSprite.getWidth(),
                lU.from.y+labelRegion.getRegionHeight());
        //lU.to.set(getX(),getY()+getHeight()-leftDownSprite.getHeight());
        leftUpSprite.setPosition(lU.from.x, lU.from.y);

        lD = new AnimationData();
        lD.from.set(getX()+(getWidth()*.5f-leftDownSprite.getWidth()),
                getY()+getHeight()*.5f-leftDownSprite.getHeight());
        //lD.to.set(getX(),getY());
        lD.to.set(lD.from.x-labelRegion.getRegionWidth(),
                lD.from.y-labelRegion.getRegionHeight());
        leftDownSprite.setPosition(lD.from.x, lD.from.y);

        rU = new AnimationData();
        rU.from.set(getX()+getWidth()*.5f+gapSize,
                getY()+getHeight()*.5f+gapSize);
        rU.to.set(rU.from.x+labelRegion.getRegionWidth(), rU.from.y+labelRegion.getRegionHeight());
        //rU.to.set(getX()+getWidth()-leftDownSprite.getWidth(),
          //      getY()+getHeight()-leftDownSprite.getHeight() );
        rightUpSprite.setPosition(rU.from.x, rU.from.y);

        rD = new AnimationData();
        rD.from.set(getX()+getWidth()*.5f+gapSize,
                getY()+(getHeight()*.5f-leftDownSprite.getHeight()));
        //rD.to.set(getX()+getWidth()-leftDownSprite.getWidth(),getY());
        rD.to.set(rD.from.x+labelRegion.getRegionWidth(), rD.from.y-labelRegion.getRegionHeight());
        rightDownSprite.setPosition(rD.from.x, rD.from.y);


        rULabelBg = new Sprite(labelRegion);
        rULabelBg.setPosition(getX() + getWidth() * .5f, getY() + getHeight() * .5f);
        rULabelBg.setAlpha(0);

        labelRegion.flip(true,false);
        rDLabelBg = new Sprite(labelRegion);
        rDLabelBg.setPosition(getX() + getWidth() / 2 + gapSize, getY() + getHeight() / 2 - rDLabelBg.getHeight());
        rDLabelBg.setAlpha(0);

        labelRegion.flip(false, true);
        lDLabelBg = new Sprite(labelRegion);
        lDLabelBg.setPosition(getX()+getWidth()/2- lDLabelBg.getWidth(),
                getY()+getHeight()/2- lDLabelBg.getHeight() );
        lDLabelBg.setAlpha(0);

        labelRegion.flip(false, true);
        lULabelBg = new Sprite(labelRegion);
        lULabelBg.setPosition(getX()+getWidth()/2- lULabelBg.getWidth(),
                getY()+getHeight()/2+gapSize);
        lULabelBg.setAlpha(0);






        Gdx.app.log(TAG, "x pos is " + getX());
        Gdx.app.log(TAG, "width is "+getWidth());

        pickActor = new Actor();
        pickActor.setHeight(this.getHeight());
        pickActor.setWidth(this.getWidth());
        pickActor.setPosition(this.getX(), this.getY());

        InputListener pickListener = new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                quartileCheck(x,y);
                Gdx.app.log(TAG, "touch up");
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                super.touchDragged(event, x, y, pointer);
                Gdx.app.log(TAG, "touch down");
                quartileCheck(x, y);
            }
        };

        this.addListener(pickListener);
        //this.addActor(pickActor);







    }

    public void quartileCheck(float x, float y){
        Gdx.app.log(TAG, "checking quartiles");
        //do nothing, when invalid input
        if (x< 0 ||
                x> getWidth()||
                y< 0||
                y> getHeight()) {
            Gdx.app.log(TAG, "Nonsense input!!");
            Gdx.app.log(TAG, "Input was: x: "+x+"y: "+y);
            return;
        }


        if (x < getWidth()*.5f) {
            //animate left side
            if (y < getHeight()*.5f){
                Gdx.app.log(TAG, "LEFT DOWN BOOM!!");
                tween("ld");
                //leftDown
            }else{
                tween("lu");
                //leftUp
            }
        }else{
            //right side
            if (y < getHeight()*.5f){
                //rightDown
                tween("rd");
            }else{
                //rightUp
                tween("ru");

            }
        }
    }

    private void tween(String id){
        if (id =="ru") {
            if (!rU.isAnimating) {
                spriteTweenSetOpen(rightUpSprite, rULabelBg, rU);
                spriteTweenSetClose(leftDownSprite, lDLabelBg, lD);
                spriteTweenSetClose(leftUpSprite, lULabelBg,lU);
                spriteTweenSetClose(rightDownSprite, rDLabelBg, rD);

                sideCallbacks.leftDown(false);
                sideCallbacks.leftUp(false);
                sideCallbacks.rightDown(false);
                sideCallbacks.rightUp(true);
            }
        }

        if (id=="rd") {
                if (!rD.isAnimating) {
                    spriteTweenSetOpen(rightDownSprite, rDLabelBg, rD);
                    spriteTweenSetClose(rightUpSprite, rULabelBg, rU);
                    spriteTweenSetClose(leftDownSprite, lDLabelBg,lD);
                    spriteTweenSetClose(leftUpSprite, lULabelBg, lU);

                    sideCallbacks.leftDown(false);
                    sideCallbacks.leftUp(false);
                    sideCallbacks.rightDown(true);
                    sideCallbacks.rightUp(false);
                }
        }

        if (id=="lu") {
            if (!lU.isAnimating) {
                spriteTweenSetOpen(leftUpSprite, lULabelBg, lU);
                spriteTweenSetClose(rightDownSprite, rDLabelBg, rD);
                spriteTweenSetClose(rightUpSprite, rULabelBg, rU);
                spriteTweenSetClose(leftDownSprite, lDLabelBg, lD);

                sideCallbacks.leftDown(false);
                sideCallbacks.leftUp(true);
                sideCallbacks.rightDown(false);
                sideCallbacks.rightUp(false);
            }
        }

        if (id=="ld") {
            if (!lD.isAnimating) {
                spriteTweenSetOpen(leftDownSprite, lDLabelBg, lD);
                spriteTweenSetClose(leftUpSprite, lULabelBg, lU);
                spriteTweenSetClose(rightDownSprite, rDLabelBg, rD);
                spriteTweenSetClose(rightUpSprite, rULabelBg, rU);

                sideCallbacks.leftDown(true);
                sideCallbacks.leftUp(false);
                sideCallbacks.rightDown(false);
                sideCallbacks.rightUp(false);
            }
        }


    }
    private void spriteTweenSetClose(Sprite spriteKnob, Sprite label, AnimationData data){
        if (data.isOpen) {
            data.isOpen = false;
            data.isAnimating = true;
            spriteKnob.setColor(1, 1, 1, 1);
            Tween.set(spriteKnob, SpriteAccessor.XY_TRANSLATE).target(
                    spriteKnob.getX(), spriteKnob.getY()).start(tweenManager);
            Tween.to(spriteKnob, SpriteAccessor.XY_TRANSLATE, 1).target(data.from.x, data.from.y).setCallback(data.callback).start(tweenManager);

            Tween.set(label, SpriteAccessor.ALPHA).target(1).start(tweenManager);
            Tween.to(label, SpriteAccessor.ALPHA, .5f).target(0).start(tweenManager).ease(TweenEquations.easeOutQuint);
        }
    }
    private void spriteTweenSetOpen(Sprite spriteKnob, Sprite label, AnimationData data){
        if (!data.isOpen) {
            data.isAnimating = true;
            data.isOpen = true;
            spriteKnob.setColor(1,.9f,.9f,1);
            Tween.set(spriteKnob, SpriteAccessor.XY_TRANSLATE).target(
                    spriteKnob.getX(), spriteKnob.getY()).start(tweenManager);
            Tween.to(spriteKnob, SpriteAccessor.XY_TRANSLATE, 1).target(data.to.x, data.to.y).ease(TweenEquations.easeOutQuint)
                    .setCallback(data.callback).start(tweenManager);

            Tween.set(label, SpriteAccessor.ALPHA).target(0).start(tweenManager);
            Tween.to(label, SpriteAccessor.ALPHA, 1).target(1).start(tweenManager);
        }
    }

    public void registerCallbacks(SideCallbacks callbacks){
        sideCallbacks = callbacks;
    }



    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        leftDownSprite.draw(batch);
        leftUpSprite.draw(batch);
        rightUpSprite.draw(batch);
        rightDownSprite.draw(batch);

        rULabelBg.draw(batch);
        rDLabelBg.draw(batch);
        lULabelBg.draw(batch);
        lDLabelBg.draw(batch);


    }

    @Override
    public void act(float delta) {
        super.act(delta);
        tweenManager.update(delta);
        //pickActor.act(delta);


    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);

    }
}
