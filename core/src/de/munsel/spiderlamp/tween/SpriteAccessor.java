package de.munsel.spiderlamp.tween;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by munsel on 08.11.14.
 */
public class SpriteAccessor implements TweenAccessor<Sprite> {
    public static final int ALPHA = 0;
    public static final int Y_TRANSLATE = 1;
    public static final int X_TRANSLATE = 2;
    public static final int XY_TRANSLATE = 3;


    @Override
    public int getValues(Sprite target, int tweenType, float[] returnValues) {

        switch(tweenType){
            case ALPHA:
                returnValues[0] = target.getColor().a;
                return 1;
            case Y_TRANSLATE:
                returnValues[0] = target.getY();
                return 1;
            case X_TRANSLATE:
                returnValues[0] = target.getX();
                return 1;
            case XY_TRANSLATE:
                returnValues[0] = target.getX();
                returnValues[1] = target.getY();
                return 2;
            default:
                assert false;
                return -1;
        }
    }

    @Override
    public void setValues(Sprite target, int tweenType, float[] newValues) {

        switch (tweenType){
            case ALPHA:
                target.setColor(target.getColor().r,
                        target.getColor().g,
                        target.getColor().b,
                        newValues[0]);
                break;
            case X_TRANSLATE:
                target.setX(newValues[0]);
                break;
            case Y_TRANSLATE:
                target.setY(newValues[0]);
                break;
            case XY_TRANSLATE:
                target.setPosition(newValues[0], newValues[1]);
                break;
            default:
                assert false;
                break;
        }

    }
}


