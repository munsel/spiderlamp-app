package de.munsel.spiderlamp;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import java.util.Observable;

/**
 * The LamData contains all the physical
 * status information about the device.
 * it also informs all its observers when data has changed.
 * To preserve this data, this class will be serialized.
 * When the Application starts, this data will be used
 * to initialize.
 *
 * lengths are in cm.
 *
 * Created by munsel on 03.11.15.
 */
public class LampData extends Observable {

    private static final int LAMP_WIDTH = 8;
    private static final int LAMP_HEIGHT = 10;

    private int x,y,z;
    private int l1, l2, l3, l4;
    private int h;
    private int a;
    private int intensity;
    private Color color;
    private boolean isSpotOn, isRgbOn;

    public LampData(){
        h = 4;
        a = 2;
    }


    public void calculateRopeLengthsFromCoordinates()
    {
        int deltaX1 = x - LAMP_WIDTH/2;
        deltaX1 *= deltaX1;
        int deltaX2 = a - x - LAMP_WIDTH/2;
        deltaX2 *= deltaX2;
        int deltaY1 = y - LAMP_WIDTH/2;
        deltaY1 *= deltaY1;
        int deltaY2 = a - y - LAMP_WIDTH/2;
        deltaY2 *= deltaY2;
        int deltaZ = h - z - LAMP_HEIGHT;
        deltaZ *= deltaZ;
        l1 = (int) Math.sqrt(deltaX1 + deltaY1 + deltaZ);
        l2 = (int) Math.sqrt(deltaX2 + deltaY1 + deltaZ);
        l3 = (int) Math.sqrt(deltaX2 + deltaY2 + deltaZ);
        l4 = (int) Math.sqrt(deltaX1 + deltaY2 + deltaZ);
    }

    public void setLampCoordiantes(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getL1() {
        return l1;
    }

    public void setL1(int l1) {
        this.l1 = l1;
    }

    public int getL2() {
        return l2;
    }

    public void setL2(int l2) {
        this.l2 = l2;
    }

    public int getL3() {
        return l3;
    }

    public void setL3(int l3) {
        this.l3 = l3;
    }

    public int getL4() {
        return l4;
    }

    public void setL4(int l4) {
        this.l4 = l4;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public float getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(int z) {
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

    public int getH() {
        return h;
    }

    public void setH(int maxHeight) {
        this.h = maxHeight;
    }

    public int getA() {
        return a;
    }

    public void setA(int mountWidth) {
        this.a = mountWidth;
    }
}
