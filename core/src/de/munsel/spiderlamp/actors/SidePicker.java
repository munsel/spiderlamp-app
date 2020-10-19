package de.munsel.spiderlamp.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import de.munsel.spiderlamp.ControlPanelScreen;

/**
 * select side to change the ropelength on that side
 * also show ropelength info
 */
public class SidePicker extends Actor{
    private static final String TAG = SidePicker.class.getSimpleName();
    /**
     * CONSTANTS
     */
    public static final float LTFX=70;
    public static final float LTFY=280;

    public static final float LDFX=70;
    public static final float LDFY=80;

    public static final float RTFX=280;
    public static final float RTFY=280;

    public static final float RDFX=280;
    public static final float RDFY=80;

    private Sprite backgroundSprite;

    public SidePicker(Skin skin){
        this.setPosition(ControlPanelScreen.V_WIDTH*.05f, ControlPanelScreen.V_HEIGHT*.42f);

        TextureRegion region = skin.getRegion("sidepicker");
        backgroundSprite = new Sprite(region);
        backgroundSprite.setPosition(getX(), getY());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        backgroundSprite.draw(batch, parentAlpha);
        }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
    }
}
