package de.munsel.spiderlamp;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by munsel on 03.11.15.
 */
public class LampData {
    private float x,y,z;
    private float maxHeight;
    private float mountWidth;
    private float intensity;
    private Color color;
    private boolean isSpotOn, isRgbOn;

    public LampData(){
        maxHeight = 4;
        mountWidth = 2;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isSpotOn() {
        return isSpotOn;
    }

    public void setIsSpotOn(boolean isSpotOn) {
        this.isSpotOn = isSpotOn;
    }

    public boolean isRgbOn() {
        return isRgbOn;
    }

    public void setIsRgbOn(boolean isRgbOn) {
        this.isRgbOn = isRgbOn;
    }

    public float getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(float maxHeight) {
        this.maxHeight = maxHeight;
    }

    public float getMountWidth() {
        return mountWidth;
    }

    public void setMountWidth(float mountWidth) {
        this.mountWidth = mountWidth;
    }
}
