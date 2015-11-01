package de.munsel.spiderlamp;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by munsel on 21.10.15.
 */
public abstract class UiPositioningConstants {


    /**
     * define Positions of the boundarybox of
     * position control Elements
     */
    static final Color BOUNDARY_BOX_COLOR = Color.valueOf("856767");
    static final float BOUNDARY_BOX_WIDTH =190;
    static final float BOUNDARY_BOX_HEIGHT =110;
    static final float POS_OFFSET_X = 485;
    static final float X_POS_OFFSET_Y = 1078;
    static final float Y_POS_OFFSET_Y = 928;
    static final float Z_POS_OFFSET_Y = 770;

    /**
     * relative position of labels and up/down button
     * elements inside the bounday box
     */
    static final float POSITION_HEADLINE_LABEL_X = 470;
    static final float POSITION_HEADLINE_LABEL_Y = 1210;
    static final float POSITION_LABEL_OFFSET_X = 25;
    static final float POSITION_LABEL_OFFSET_Y = 35;
    static final float LESS_OFFSET_X = 132;
    static final float LESS_OFFSET_Y = 14;
    static final float MORE_OFFSET_X = 132;
    static final float MORE_OFFSET_Y = 62;

    /**
     * define positions of the connected labexl and
     * the bluetooth search button
     */
    static final float CONNECTED_LABEL_OFFSET_X = 32;
    static final float CONNECTED_LABEL_OFFSET_Y = 1062;
    static final float BLUETOOTH_SEARCBUTTON_OFFSET_X = 350;
    static final float BLUETOOTH_SEARCBUTTON_OFFSET_Y = 1050;


    /**
     * define position elements of the device select overlay menu
     */
static final Color OVERLAY_BACKGROUND_COLOR = new Color(.2f,.2f,.2f,.9f);
    static final float SCAN_BUTTON_X = 100;
    static final float SCAN_BUTTON_Y = 50;
    static final float DEVICE_BUTTON_X = 100;
    static final float DEVICE_BUTTON_Y = 200;
    static final float DEVICE_BUTTON_OFFSET_Y = 100;

    static final float ADD_BUTTON_X = 530;
    static final float ADD_BUTTON_Y = 500;

    static class InstructionListLabel{
        public static  final float X =50;
        public static final float Y =130;
        public static final float WIDTH  =580;
        public static final float HEIGHT =240;
    }

    static final float COLORPICK_X = 500;
    static final float COLORPICK_Y = 600;
    static final float COLORPICK_WIDTH = 150;
    static final float COLORPICK_HEIGHT = 80;
    static final Color COLORPICK_DEFAULT_COLOR = Color.CORAL;


    static final float COLORPICK_CAPTION_X = 135;
    static final float COLORPICK_CAPTION_Y = 1150;

}
