package de.munsel.spiderlamp.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by munsel on 03.11.15.
 */
public class XyChooser extends Actor {

    private TextureRegion background, lamp;
    private float lampX, lampY;
    private ShapeRenderer shapeRenderer;

    public XyChooser(Skin skin){
        this.setTouchable(Touchable.enabled);

        background = skin.getRegion("xy-picker");;
        lamp = skin.getRegion("xy-cursor");

        setSize(background.getRegionWidth(), background.getRegionHeight());
        shapeRenderer = new ShapeRenderer();


    }
    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(background, getX(), getY());
        batch.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.line(getX()+35, getY()+35,lampX+5.5f, lampY+5.5f);
        shapeRenderer.line(getX()+getWidth()-35, getY()+35,lampX+lamp.getRegionWidth()-5.5f, lampY+5.5f);
        shapeRenderer.line(getX()+35, getY()+getHeight()-35,lampX+5.5f, lampY+lamp.getRegionHeight()-5.5f);
        shapeRenderer.line(getX()+getWidth()-35, getY()+getHeight()-35,
                lampX+lamp.getRegionWidth()-5.5f, lampY+lamp.getRegionHeight()-5.5f);
        shapeRenderer.end();
        batch.begin();
        batch.draw(lamp, lampX, lampY);

    }



    public void setLamp(float x, float y){
        this.lampX = x+getX()-lamp.getRegionWidth()*.5f;
        this.lampY = y+getY()-lamp.getRegionHeight()*.5f;
        if (lampX > getX()+background.getRegionWidth()){
            lampX = getX()+background.getRegionWidth()-lamp.getRegionWidth();
        }else if (lampX < getX()) lampX = getX();
        if (lampY > getY()+background.getRegionHeight()){
            lampY = getY()+background.getRegionHeight()-lamp.getRegionHeight();
        }else if (lampY < getY()) lampY = getY();
    }

    @Override
    public void setPosition(float x, float y){
        super.setPosition(x,y);
        lampX = getX()+ background.getRegionWidth()*.5f;
        lampY = getY()+ background.getRegionHeight()*.5f;
    }

    public float getRealX(float mountWidth){
        return ((lampX-getX())+lamp.getRegionWidth()*.5f)/background.getRegionWidth() * mountWidth;
    }

    public float getRealY(float mountWidth){
        return ((lampY-getY())+lamp.getRegionHeight()*.5f)/background.getRegionHeight() * mountWidth;
    }


}
