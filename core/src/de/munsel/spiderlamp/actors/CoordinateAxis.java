package de.munsel.spiderlamp.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by munsel on 27.03.16.
 */
public class CoordinateAxis extends Actor {

    float x,y,z;

    float[] xf, yf, zf; //front
    float[] xu, yu, zu; //up
    float[] xr, yr, zr; //right

    final float SIZE = 150;
    final float SIZE_Y = (float)Math.sqrt(SIZE);
    final float edge = 20;

    ShapeRenderer shapeRenderer;

    PolygonSprite frontx,fronty, frontz;
    PolygonSpriteBatch polygonSpriteBatch;

    public CoordinateAxis(){
        shapeRenderer = new ShapeRenderer();
       /* polygonSpriteBatch = new PolygonSpriteBatch();
        PolygonRegion  pRegion = new PolygonRegion();

        Texture textureSolid;

// Creating the color filling (but textures would work the same way)
        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(0xDEADBEFF); // DE is red, AD is green and BE is blue.
        pix.fill();
        textureSolid = new Texture(pix);
        PolygonRegion polyReg = new PolygonRegion(new TextureRegion(textureSolid),
                new float[] {      // Four vertices
                        0, 0,            // Vertex 0         3--2
                        100, 0,          // Vertex 1         | /|
                        100, 100,        // Vertex 2         |/ |
                        0, 100           // Vertex 3         0--1
                }, new short[] {
                0, 1, 2,         // Two triangles using vertex indices.
                0, 2, 3          // Take care of the counter-clockwise direction.
        });*/



        xf = new float[8];
        yf = new float[8];
        zf = new float[8];
        xu = new float[8];
        yu = new float[8];
        zu = new float[8];
        xr = new float[8];
        yr = new float[8];
        zr = new float[8];
    }

    public void setCoordinatesXY(float x, float y){
        this.x = x;
        this.y = y;
        setFrontPolygons();
        setRightPolygons();
        setUpPolygons();
    }
    public void setCoordinatesZ(float z){
        this.z = z;
        setFrontPolygons();
        setRightPolygons();
        setUpPolygons();
    }

    public void setFrontPolygons(){
        float x = getX();
        float y = getY();

        zf[0] = x;
        zf[1] = y+edge;
        zf[2] = x+edge;
        zf[3] = y+edge;
        zf[4] = x+edge;
        zf[5] = y+edge/2+ z*SIZE;
        zf[6] = x;
        zf[7] = y+edge/2+ z*SIZE;

        if (this.y >0) {
            yf[0] = x ;
            yf[1] = y + edge ;
            yf[2] = x ;
            yf[3] = y ;
            yf[4] = x + edge;
            yf[5] = y ;
            yf[6] = x + edge;
            yf[7] = y + edge;
        }else{
            yf[0] = x + this.y * MathUtils.cos(MathUtils.PI / 4) * SIZE;
            yf[1] = y + edge + this.y * MathUtils.sin(MathUtils.PI / 4) * SIZE;
            yf[2] = x + this.y * MathUtils.cos(MathUtils.PI / 4) * SIZE;
            yf[3] = y + this.y * MathUtils.sin(MathUtils.PI / 4) * SIZE;
            yf[4] = x + edge + this.y * MathUtils.cos(MathUtils.PI / 4) * SIZE;
            yf[5] = y + this.y * MathUtils.sin(MathUtils.PI / 4) * SIZE;
            yf[6] = x + edge + this.y * MathUtils.cos(MathUtils.PI / 4) * SIZE;
            yf[7] = y + edge + this.y * MathUtils.sin(MathUtils.PI / 4) * SIZE;
        }

        xf[0] = x;
        xf[1] = y;
        xf[2] = x;
        xf[3] = y+edge;
        xf[4] = x+this.x*SIZE;
        xf[5] = y+edge;
        xf[6] = x+this.x*SIZE;
        xf[7] = y;
    }
    public void setUpPolygons(){
        float x = getX();
        float y = getY();

        xu[0] = x;
        xu[1] = y+edge;
        xu[2] = x+this.x*SIZE;
        xu[3] = y+edge;
        xu[4] = x+this.x*SIZE+MathUtils.cos(MathUtils.PI/4)*edge/1.4f;
        xu[5] = y+edge+MathUtils.sin(MathUtils.PI/4)*edge/1.4f;
        xu[6] = x+MathUtils.cos(MathUtils.PI/4)*edge/1.4f;
        xu[7] = y+edge+MathUtils.sin(MathUtils.PI/4)*edge/1.4f;

        if (z>0){
            zu[0] = x;
            zu[1] = y + edge+this.z*SIZE-edge/2;
            zu[2] = x + edge;
            zu[3] = y + edge+this.z*SIZE-edge/2;
            zu[4] = x + edge + MathUtils.cos(MathUtils.PI / 4) * edge / 1.4f;
            zu[5] = y + edge +this.z*SIZE+ MathUtils.sin(MathUtils.PI / 4) * edge / 1.4f-edge/2;
            zu[6] = x + MathUtils.cos(MathUtils.PI / 4) * edge / 1.4f;
            zu[7] = y + edge +this.z*SIZE+ MathUtils.sin(MathUtils.PI / 4) * edge / 1.4f-edge/2;
        }else {
            zu[0] = x;
            zu[1] = y + edge;
            zu[2] = x + edge;
            zu[3] = y + edge;
            zu[4] = x + edge + MathUtils.cos(MathUtils.PI / 4) * edge / 1.4f;
            zu[5] = y + edge + MathUtils.sin(MathUtils.PI / 4) * edge / 1.4f;
            zu[6] = x + MathUtils.cos(MathUtils.PI / 4) * edge / 1.4f;
            zu[7] = y + edge + MathUtils.sin(MathUtils.PI / 4) * edge / 1.4f;

        }
        yu[0] = x;
        yu[1] = y+edge;
        yu[2] = x+edge;
        yu[3] = y+edge;
        yu[4] = x+edge+this.y*MathUtils.cos(MathUtils.PI/4)*SIZE;
        yu[5] = y+edge+this.y*MathUtils.sin(MathUtils.PI/4)*SIZE;
        yu[6] = x+this.y*MathUtils.cos(MathUtils.PI/4)*SIZE;
        yu[7] = y+edge+this.y*MathUtils.sin(MathUtils.PI/4)*SIZE;




    }
    public void setRightPolygons(){
        float x = getX();
        float y = getY();

        if (this.x<0){
            xr[0] = x +edge;
            xr[1] = y;
            xr[2] = x  + MathUtils.cos(MathUtils.PI / 4) * edge / 1.4f +edge;
            xr[3] = y + MathUtils.sin(MathUtils.PI / 4) * edge / 1.4f;
            xr[4] = x  + MathUtils.cos(MathUtils.PI / 4) * edge / 1.4f +edge;
            xr[5] = y + edge + MathUtils.sin(MathUtils.PI / 4) * edge / 1.4f;
            xr[6] = x  +edge;
            xr[7] = y + edge;
        }else {
            xr[0] = x + this.x * SIZE;
            xr[1] = y;
            xr[2] = x + this.x * SIZE + MathUtils.cos(MathUtils.PI / 4) * edge / 1.4f;
            xr[3] = y + MathUtils.sin(MathUtils.PI / 4) * edge / 1.4f;
            xr[4] = x + this.x * SIZE + MathUtils.cos(MathUtils.PI / 4) * edge / 1.4f;
            xr[5] = y + edge + MathUtils.sin(MathUtils.PI / 4) * edge / 1.4f;
            xr[6] = x + this.x * SIZE;
            xr[7] = y + edge;
        }


        yr[0] = x+edge;
        yr[1] = y;
        yr[2] = x;
        yr[3] = y;
        yr[4] = x+edge+this.y*MathUtils.cos(MathUtils.PI/4)*SIZE;
        yr[5] = y+edge+this.y*MathUtils.sin(MathUtils.PI/4)*SIZE;
        yr[6] = x+edge+this.y*MathUtils.cos(MathUtils.PI/4)*SIZE;
        yr[7] = y+this.y*MathUtils.sin(MathUtils.PI/4)*SIZE;

        zr[0] = x+edge;
        zr[1] = y+edge/2+ z*SIZE;
        zr[2] = x+edge+MathUtils.cos(MathUtils.PI / 4)*edge/1.4f;
        zr[3] = y+edge/2+z*SIZE+ MathUtils.sin(MathUtils.PI/3)*edge/1.4f;
        zr[4] = x+edge+MathUtils.cos(MathUtils.PI/4)*edge/1.4f;
        zr[5] = y+edge/2 + MathUtils.sin(MathUtils.PI/4)*edge/1.4f;
        zr[6] = x+edge;
        zr[7] = y+edge;


    }



    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.end();

        boolean yFirst = y>0;
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setColor(1,1,1,parentAlpha);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        //shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.polygon(zf);
        shapeRenderer.polygon(yf);
        shapeRenderer.polygon(xf);
        shapeRenderer.polygon(xu);
        shapeRenderer.polygon(yu);
        shapeRenderer.polygon(zu);
        shapeRenderer.polygon(xr);
        shapeRenderer.polygon(yr);
        shapeRenderer.polygon(zr);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        batch.begin();
        super.draw(batch, parentAlpha);
    }
}
