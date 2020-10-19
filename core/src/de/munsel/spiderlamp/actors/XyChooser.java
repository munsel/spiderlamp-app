package de.munsel.spiderlamp.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by munsel on 03.11.15.
 */
public class XyChooser extends Actor implements Observer{

    private TextureRegion background, lamp;
    private float lampX, lampY;
    private Image bgImg, lampImg;
    private ShapeRenderer shapeRenderer;

    public XyChooser(Skin skin){
        this.setTouchable(Touchable.enabled);

        background = skin.getRegion("xy-picker");;
        lamp = skin.getRegion("xy-cursor");
        bgImg = new Image(background);
        lampImg = new Image(lamp);

        setSize(background.getRegionWidth(), background.getRegionHeight());
        shapeRenderer = new ShapeRenderer();
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        bgImg.draw(batch, parentAlpha);
        batch.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setColor(1,1,1,parentAlpha);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.line(getX()+35, getY()+35,lampX+5.5f, lampY+5.5f);
        shapeRenderer.line(getX()+getWidth()-35, getY()+35,lampX+lamp.getRegionWidth()-5.5f, lampY+5.5f);
        shapeRenderer.line(getX()+35, getY()+getHeight()-35,lampX+5.5f, lampY+lamp.getRegionHeight()-5.5f);
        shapeRenderer.line(getX()+getWidth()-35, getY()+getHeight()-35,
                lampX+lamp.getRegionWidth()-5.5f, lampY+lamp.getRegionHeight()-5.5f);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        batch.begin();
        lampImg.draw(batch, parentAlpha);
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
        lampImg.setPosition(lampX, lampY);
    }

    @Override
    public void setPosition(float x, float y){
        super.setPosition(x,y);
        lampX = getX()+ background.getRegionWidth()*.5f;
        lampY = getY()+ background.getRegionHeight()*.5f;
        lampImg.setPosition(lampX, lampY);
        bgImg.setPosition(x,y);
    }


    public float getRealX(float mountWidth){
        return ((lampX-getX())+lamp.getRegionWidth()*.5f)/background.getRegionWidth() * mountWidth;
    }

    public float getRealY(float mountWidth){
        return ((lampY-getY())+lamp.getRegionHeight()*.5f)/background.getRegionHeight() * mountWidth;
    }

    public float getLampX() {
        return lampX;
    }

    public float getLampY() {
        return lampY;
    }



    @Override
    public void update(Observable o, Object arg) {

    }
}
