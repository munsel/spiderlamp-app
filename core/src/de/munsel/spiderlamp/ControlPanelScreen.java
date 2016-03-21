package de.munsel.spiderlamp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.munsel.spiderlamp.actors.*;
import de.munsel.spiderlamp.bluetooth.BluetoothAcessor;
import de.munsel.spiderlamp.bluetooth.BtMessageHandler;
import de.munsel.spiderlamp.bluetooth.Message;
import de.munsel.spiderlamp.bluetooth.MessageQueue;
import de.munsel.spiderlamp.bluetooth.MessageTransmitter.MessageTransmitter;
import de.munsel.spiderlamp.bluetooth.MessageTransmitter.TransmitDoneCallback;


/**
 * Created by munsel on 16.10.15.
 */
public class ControlPanelScreen implements Screen {
    private final String TAG = ControlPanelScreen.class.getSimpleName();

    // CONSTANTS
    public static final int V_WIDTH = Gdx.graphics.getWidth();
    public static final int V_HEIGHT = Gdx.graphics.getHeight();

    private final float POS_INCREMENT = 0.25f;

    private BluetoothAcessor btAccesor;
    private MessageQueue messageQueue;
    private MessageTransmitter messageTransmitter;
    private int state;
    private Array<String> instructions;

    // UI and font settings
    private Skin skin;
    private BitmapFont uiFont24;
    private BitmapFont uiFont36;
    private BitmapFont uiFont72;

    public static final String FONT_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;:,{}\"´`'<>";

    //UI background
    private ShapeRenderer shapeRenderer;
    private boolean headerEnable = true;
    private boolean footerEnable = true;

    // UI elements
    private OrthographicCamera camera;
    private InputMultiplexer inputMultiplexer;
    private Array<Stage> stages;

    private Stage headerStage;
    private TextButton headerButton;
    private Button btSelectButtonTab;
    private Button adjustButtonTab;
    private Button settingsButtonTab;
    private Button instructionsButtonTab;
    private Button shutDownButton;
    private Button joypadButtonTab;
    private BatteryIndicator batteryIndicator;


    private Stage btDeviceSelectStage;
    private TextButton scanForDeviceButton;
    private Array<TextButton> devices;
    private boolean isDeviceSelecting;
    private String selectedDevice;


    private Stage adjustStage;
    private XyChooser xyChooser;
    private Button addButton, sendButton;
    private CheckBox spotlightCheckbox, rgbCheckbox;
    private ColorPickButton colorPickButton;
    private Slider intensitySlider, heightSlider;
    private Label xPosLabel, yPosLabel, zPosLabel;

    private Stage joypadStage;
    private JoyStick joystick;
    private JoyStickY joyStickY;
    private TextButton upButton;
    private TextButton downButton;



    private Stage colorPickStage;
    private Image colorPickImage;
    private boolean isInColorPickingMode;
    private boolean isColorPicking;
    private Vector2 pickedPosition;
    private Color pickedColor;
    private Label colorPickCaptionLabel;


    private Stage instructionListStage;
    private Label instructionListLabel;
    private TextButton sendInstructionsButton;
    private TextButton abortSendingInstructionsButton;

    private Stage settingsStage;
    private SidePicker sidePicker;
    private Image mountSizesImage;
    private TextField mountSizeTextField;
    private TextButton setZeroButton;


    private LampData lampData;



    // UI default values

    private final CharSequence CONNECTED_DEVICE_DEFAULT_TEXT = "not connected";
    private final CharSequence LOG_LABEL_DEFAULT_TEXT = "SPINNE: Hello, i am ready!\n" +
            "ME:x:128 y:200 z:100\n" +
            "SPINNE: ok\n" +
            "ME: r:182 g:255 b:0\n" +
            "SPINNE: ok\n" +
            "ME:shutdown\n" +
            "SPINNE: ok\n" +
            "SPINNE: goodbye";



    public ControlPanelScreen(SpiderlampMain parent, BluetoothAcessor btAccessor){
        this.btAccesor = btAccessor;

        messageQueue = new MessageQueue(btAccessor);
        messageTransmitter = new MessageTransmitter(new TransmitDoneCallback()
        {
            @Override
            public void done(Message message)
            {
                //check, if batterystatus is sent
                if (message.getId() == Message.V_ASCII)
                {
                    byte[] bytes = message.getBytes();
                    int adcValue =(int) ( (bytes[1]) | (bytes[2]<<8) );
                    batteryIndicator.setBatteryValue(adcValue);
                }

                if (messageQueue.isRunning())
                {
                    Message newMessage = messageQueue.decueue();
                    if (newMessage != null)
                    {
                        messageTransmitter.transmit(newMessage);
                    } else
                    {
                        messageQueue.stop();
                        Gdx.app.log(TAG, "stopped the queue");
                    }
                }
            }
        }, btAccessor);

        state = BluetoothAcessor.STATE_NONE;

        camera = new OrthographicCamera();
        //camera.setToOrtho(false, V_WIDTH,V_HEIGHT);

        isDeviceSelecting = false;
        isInColorPickingMode = false;
        isColorPicking = false;
        pickedColor = new Color(Constants.COLORPICK_DEFAULT_COLOR);
        pickedPosition = new Vector2();
        devices = new Array<TextButton>();

        instructions = new Array<String>();

        // Font initialization
        initializeFont();
        skin = new Skin();
        skin.addRegions(new TextureAtlas(Gdx.files.internal("dataUi/uiskin.atlas")));
        skin.addRegions(new TextureAtlas(Gdx.files.internal("dataUi/spinneUi.atlas")));
        skin.add("droid-mono-24", uiFont24);
        skin.add("droid-mono-72", uiFont72);
        skin.add("droid-mono-36", uiFont36);
        skin.load(Gdx.files.internal("dataUi/uiskin.json"));

        // UI initialization
        shapeRenderer = new ShapeRenderer();

        Viewport viewport = new ScreenViewport(camera);
        headerStage = new Stage(viewport);
        btDeviceSelectStage = new Stage(viewport);
        joypadStage = new Stage(viewport);
        colorPickStage = new Stage(viewport);
        adjustStage = new Stage(viewport);
        instructionListStage = new Stage(viewport);
        settingsStage = new Stage(viewport);

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(headerStage);
        inputMultiplexer.addProcessor(adjustStage);

        stages = new Array<Stage>();
        stages.add(headerStage);
        stages.add(adjustStage);

        lampData = new LampData();

        messageQueue.enqueue(Message.getBatteryMessage());


    }


    @Override
    public void show() {
        btAccesor.setBtMessageHandler(handler);
        btAccesor.init();
        Gdx.input.setInputProcessor(inputMultiplexer);
        headerStage.clear();

        populateAdjustStage();
        populateJoypadStage();
        populateColorPickStage();
        populateHeaderStage();
        populateDeviceSelectStage();
        populateInstructionListStage();
        populateSettingsStage();
    }

    @Override
    public void render(float delta) {
        drawBackground();
        for (Stage stage: stages){
            stage.act(delta);
            stage.draw();
        }

        if (isColorPicking){
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(pickedColor);
            shapeRenderer.rect(pickedPosition.x-50,pickedPosition.y+50, 100,100);
            shapeRenderer.end();
        }

        if(messageQueue.isRunning()){
            messageTransmitter.update(delta);
        }

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        btAccesor.dispose();

    }

    @Override
    public void dispose() {
        btAccesor.dispose();

    }

    /**
     * to generate some BitmapFonts on the fly
     */
    private void initializeFont() {
        FileHandle handle = Gdx.files.internal("dataUi/droidSansMono.ttf");
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(handle);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
                    new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 24;
        parameter.color = Color.valueOf("ececec");
        parameter.characters = FONT_CHARACTERS;
        uiFont24 = generator.generateFont(parameter);
        parameter.size = 72;
        parameter.color = Color.WHITE;
        uiFont72 = generator.generateFont(parameter);
        parameter.size = 36;
        uiFont36 =generator.generateFont(parameter);
            generator.dispose();
    }



    /**
     * populate all the stages with actors
     * initializes all UI elements and adds it
     * to the corresponding stage
     */
    private void populateHeaderStage(){
        headerButton = new TextButton("//SPINNE", skin, "header");
        headerButton.setPosition(Constants.HEADER_BUTTON_X*V_WIDTH,
                Constants.HEADER_BUTTON_Y*V_HEIGHT);

        adjustButtonTab = new Button(skin, "adjust");
        adjustButtonTab.setPosition(Constants.FIRST_TAB_X*V_WIDTH, Constants.TABS_Y*V_HEIGHT);
        adjustButtonTab.addListener(new ClickListener(){

            @Override
            public void clicked(InputEvent event, float x, float y) {
                setStage(adjustStage);
            }
        });

        joypadButtonTab = new Button(skin, "joystick");
        joypadButtonTab.setPosition((Constants.FIRST_TAB_X + Constants.OFFSET_TAB_X) * V_WIDTH,
                Constants.TABS_Y * V_HEIGHT);
        joypadButtonTab.addListener( new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setStage(joypadStage);
            }
        });

        instructionsButtonTab = new Button(skin,"instructions");
        instructionsButtonTab.setPosition((Constants.FIRST_TAB_X+2*Constants.OFFSET_TAB_X)*V_WIDTH,
                Constants.TABS_Y*V_HEIGHT);
        instructionsButtonTab.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                updateInstructionList();
                setStage(instructionListStage);
            }
        });

        settingsButtonTab = new Button(skin, "settings");
        settingsButtonTab.setPosition((Constants.FIRST_TAB_X+3*Constants.OFFSET_TAB_X)*V_WIDTH,
                Constants.TABS_Y*V_HEIGHT);
        settingsButtonTab.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setStage(settingsStage);
            }
        });



        btSelectButtonTab = new Button(skin, "bt-search");
        btSelectButtonTab.setPosition((Constants.FIRST_TAB_X + 3 * Constants.OFFSET_TAB_X)*V_WIDTH,
                Constants.HEADER_BUTTON_Y*V_HEIGHT);
        btSelectButtonTab.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setStage(btDeviceSelectStage);
            }
        });
        btSelectButtonTab.setColor(1,0,0,1);



        shutDownButton = new Button(skin, "off");
        shutDownButton.setPosition((Constants.FIRST_TAB_X+4*Constants.OFFSET_TAB_X)*V_WIDTH,
                Constants.TABS_Y*V_HEIGHT);
        shutDownButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });


        batteryIndicator = new BatteryIndicator(skin);
        batteryIndicator.setPosition(.82f*V_WIDTH,
                Constants.HEADER_BUTTON_Y*V_HEIGHT+
                        (btSelectButtonTab.getHeight()-batteryIndicator.getHeight())/2);
        batteryIndicator.setBatteryValue(640);
        batteryIndicator.setVisible(false);

        headerStage.addActor(headerButton);
        headerStage.addActor(adjustButtonTab);
        headerStage.addActor(settingsButtonTab);
        headerStage.addActor(joypadButtonTab);
        headerStage.addActor(btSelectButtonTab);
        headerStage.addActor(instructionsButtonTab);
        headerStage.addActor(shutDownButton);
        headerStage.addActor(batteryIndicator);
    }

    private void populateAdjustStage(){

        xPosLabel = new Label("x", skin, "pos");
        xPosLabel.setPosition(Constants.POS_X_LABEL_X*V_WIDTH, Constants.POS_LABEL_Y*V_HEIGHT);
        yPosLabel = new Label("y", skin, "pos");
        yPosLabel.setPosition(Constants.POS_Y_LABEL_X*V_WIDTH, Constants.POS_LABEL_Y*V_HEIGHT);
        zPosLabel = new Label("z", skin, "pos");
        zPosLabel.setPosition(Constants.POS_Z_LABEL_X*V_WIDTH, Constants.POS_LABEL_Y*V_HEIGHT);

        xyChooser = new XyChooser(skin);
        xyChooser.setPosition(V_WIDTH*Constants.XY_CHOOSER_X,
                V_HEIGHT*Constants.XY_CHOOSER_Y);
        xyChooser.addListener(new InputListener(){

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                xyChooser.setLamp(x, y);
                lampData.setX(xyChooser.getRealX(lampData.getMountWidth()));
                lampData.setY(xyChooser.getRealY(lampData.getMountWidth()));
                String newXText = Float.toString(lampData.getX());
                if( newXText.length()>4) newXText = newXText.substring(0,4);
                xPosLabel.setText(newXText);
                String newYText = Float.toString(lampData.getY());
                if( newYText.length()>4) newYText = newYText.substring(0,4);
                yPosLabel.setText( newYText);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                xyChooser.setLamp(x, y);
                lampData.setX(xyChooser.getRealX(lampData.getMountWidth()));
                lampData.setY(xyChooser.getRealY(lampData.getMountWidth()));
                String newXText = Float.toString(lampData.getX());
                if( newXText.length()>4) newXText = newXText.substring(0,4);
                xPosLabel.setText( newXText);
                String newYText = Float.toString(lampData.getY());
                if( newYText.length()>4) newYText = newYText.substring(0,4);
                yPosLabel.setText( newYText);
            }
        });

        spotlightCheckbox = new CheckBox("spot", skin, "adjust");
        spotlightCheckbox.setPosition(V_WIDTH*Constants.CHECKBOX_X, V_HEIGHT*Constants.CHECKBOX_SPOT_Y);
        spotlightCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (spotlightCheckbox.isChecked()){
                    intensitySlider.setVisible(true);
                }else intensitySlider.setVisible(false);

            }
        });

        rgbCheckbox = new CheckBox("rgb", skin, "adjust");
        rgbCheckbox.setPosition(V_WIDTH*Constants.CHECKBOX_X, V_HEIGHT*Constants.CHECKBOX_RGB_Y);
        rgbCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (rgbCheckbox.isChecked()){
                    colorPickButton.setVisible(true);
                }else colorPickButton.setVisible(false);
            }
        });


        addButton = new Button( skin, "add");
        addButton.setPosition(Constants.ADD_BUTTON_X*V_WIDTH,
                Constants.ADD_BUTTON_Y*V_HEIGHT);
        addButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                String message = "x: " + xPosLabel.getText().toString()
                        + ", y: " + yPosLabel.getText().toString()
                        + ", z: " + zPosLabel.getText().toString();
                //btAccesor.writeMessage(message.getBytes());
                instructions.add(message);
                return true;
            }
        });

        sendButton = new Button(skin, "send");
        sendButton.setPosition(V_WIDTH*Constants.SEND_BUTTON_X,V_HEIGHT*Constants.SEND_BUTTON_Y);
        sendButton.addListener(new InputListener(){

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                if (rgbCheckbox.isChecked())
                {
                    enqueueRGBMessage();

                }else
                {
                    messageQueue.enqueue(Message.getRedMessage((byte) 0));
                    messageQueue.enqueue(Message.getGreenMessage((byte)0));
                    messageQueue.enqueue(Message.getBlueMessage((byte)0));
                }
                if (spotlightCheckbox.isChecked())
                {
                    messageQueue.enqueue(Message.getSpotlightMessage(
                            (byte)((int) (intensitySlider.getValue()) & 0xFF)
                    ));
                }
                messageQueue.enqueue(Message.getBatteryMessage());

                messageQueue.enqueue(Message.getUpdateMessage());
                messageQueue.start();
                messageTransmitter.transmit(messageQueue.decueue());
                return true;
            }
        });
        sendButton.setVisible(false);

        colorPickButton = new ColorPickButton();
        colorPickButton.setPosition(Constants.COLORPICK_X*V_WIDTH,
                Constants.COLORPICK_Y*V_HEIGHT);
        colorPickButton.setColor(Constants.COLORPICK_DEFAULT_COLOR);
        colorPickButton.setSize(Constants.COLORPICK_WIDTH*V_WIDTH,
                Constants.COLORPICK_HEIGHT*V_HEIGHT);
        colorPickButton.setVisible(false);
        colorPickButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                isInColorPickingMode = true;
                stages.clear();
                stages.add(colorPickStage);
                Gdx.input.setInputProcessor(colorPickStage);
                return true;
            }
        });

        intensitySlider = new Slider(4,10,0.2f,false,skin, /*"intensity"*/"height");
        intensitySlider.setWidth(Constants.INTENSITY_SLIDER_WIDTH*V_WIDTH);
        intensitySlider.setPosition(Constants.INTENSITY_SLIDER_X*V_WIDTH,
                Constants.INTENSITY_SLIDER_Y*V_HEIGHT);
        intensitySlider.setVisible(false);

        heightSlider = new Slider(0,lampData.getMaxHeight(),.2f, true, skin, "height");
        heightSlider.setHeight(Constants.HEIGHT_SLIDER_HEIGHT*V_HEIGHT);
        heightSlider.setPosition(Constants.HEIGHT_SLIDER_X*V_WIDTH,
                Constants.HEIGHT_SLIDER_Y*V_HEIGHT);
        heightSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                lampData.setZ(heightSlider.getValue());
                String newZText = Float.toString(heightSlider.getValue());
                if( newZText.length()>4) newZText = newZText.substring(0,4);
                zPosLabel.setText( newZText);
            }
        });



        adjustStage.addActor(xPosLabel);
        adjustStage.addActor(yPosLabel);
        adjustStage.addActor(zPosLabel);
        adjustStage.addActor(xyChooser);
        adjustStage.addActor(spotlightCheckbox);
        adjustStage.addActor(rgbCheckbox);
        adjustStage.addActor(colorPickButton);
        adjustStage.addActor(addButton);
        adjustStage.addActor(sendButton);
        adjustStage.addActor(intensitySlider);
        adjustStage.addActor(heightSlider);


    }

    private void populateJoypadStage(){
        joystick = new JoyStick(skin);
        joystick.setPosition(V_WIDTH*.2f, V_HEIGHT*.25f);
        joystick.addListener(new InputListener(){

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                joystick.setKnobPos(x,y);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                joystick.setKnobToCenter();
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                super.touchDragged(event, x, y, pointer);
                joystick.setKnobPos(x,y);
            }
        });

        joyStickY = new JoyStickY(skin);
        joyStickY.setPosition(.7f*V_WIDTH,.25f*V_HEIGHT);
        joyStickY.addListener(new InputListener(){

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                joyStickY.setKnobPos(y);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                joyStickY.setKnobToCenter();
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                super.touchDragged(event, x, y, pointer);
                joyStickY.setKnobPos(y);
            }
        });

        upButton = new TextButton("up",skin, "device");
        upButton.setPosition(V_WIDTH*.6f,V_HEIGHT*.6f);
        upButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                messageQueue.enqueue(Message.getZMessage(2));
                messageQueue.start();
                messageTransmitter.transmit(messageQueue.decueue());
            }
        });

        downButton = new TextButton("down",skin, "device");
        downButton.setPosition(V_WIDTH*.6f,V_HEIGHT*.55f);
        downButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                messageQueue.enqueue(Message.getZMessage(1));
                messageQueue.start();
                messageTransmitter.transmit(messageQueue.decueue());
            }
        });

        joypadStage.addActor(joystick);
        joypadStage.addActor(joyStickY);
        joypadStage.addActor(upButton);
        joypadStage.addActor(downButton);
    }

    private void populateInstructionListStage(){
        instructionListLabel = new Label("",skin,"instruction-list");
        instructionListLabel.setAlignment(Align.topLeft);
       // instructionListLabel.layout();

        ScrollPane scrollPane = new ScrollPane(instructionListLabel);
        scrollPane.setPosition(V_WIDTH * Constants.INSTRUCTION_LABEL_X,
                V_HEIGHT * Constants.INSTRUCTION_LABEL_Y);
        scrollPane.setHeight(Constants.INSTRUCTION_LABEL_HEIGHT * V_HEIGHT);
        scrollPane.setWidth(Constants.INSTRUCTION_LABEL_WIDTH * V_WIDTH);

        sendInstructionsButton = new TextButton("start", skin, "header");
        sendInstructionsButton.setPosition(V_WIDTH*Constants.INSTRUCTION_BUTTON_X,
                V_HEIGHT*Constants.INSTRUCTION_BUTTON_Y);
        sendInstructionsButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sendInstructionsButton.setVisible(false);
                abortSendingInstructionsButton.setVisible(true);
            }
        });

        abortSendingInstructionsButton = new TextButton("abort", skin, "header");
        abortSendingInstructionsButton.setPosition(V_WIDTH*Constants.INSTRUCTION_BUTTON_X,
                V_HEIGHT*Constants.INSTRUCTION_BUTTON_Y);
        abortSendingInstructionsButton.setVisible(false);
        abortSendingInstructionsButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                abortSendingInstructionsButton.setVisible(false);
                sendInstructionsButton.setVisible(true);
            }
        });


        instructionListStage.addActor(scrollPane);
        instructionListStage.addActor(sendInstructionsButton);
        instructionListStage.addActor(abortSendingInstructionsButton);

    }

    private void populateSettingsStage(){
        Table table = new Table();
        float textFieldWidth = 70;

        

        Label calibraitonHeadline = new Label("calibration", skin, "pos");
        calibraitonHeadline.setPosition((V_WIDTH-calibraitonHeadline.getWidth())/2,V_HEIGHT*.7f);
        //settingsStage.addActor(calibraitonHeadline);

        Label calibrationInstructions = new Label("Set actual \nrope lengths,\n" +
                "a and h \nin cm.",skin);
        calibrationInstructions.setPosition(V_WIDTH*.58f, V_HEIGHT*.58f);
        settingsStage.addActor(calibrationInstructions);

        Image topViewImage = new Image(skin.getRegion("topview-dimen"));
        topViewImage.setPosition(V_WIDTH*.1f, V_HEIGHT*.22f);
        settingsStage.addActor(topViewImage);

        Image sideViewImage = new Image(skin.getRegion("sideview-dimen"));
        sideViewImage.setPosition(V_WIDTH*.55f,V_HEIGHT*.25f);
        settingsStage.addActor(sideViewImage);


        Label heightLabel = new Label("h=    cm", skin);
        heightLabel.setPosition(V_WIDTH*.63f, V_HEIGHT*.46f);
        settingsStage.addActor(heightLabel);
        final TextField heigthTextField = new CustomTextField("123", skin);
        heigthTextField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        heigthTextField.setPosition(V_WIDTH*.7f, V_HEIGHT*.46f);
        heigthTextField.setWidth(textFieldWidth);
        settingsStage.addActor(heigthTextField);

        Label distanceLabel = new Label("a=    cm", skin);
        distanceLabel.setPosition(V_WIDTH * .63f, V_HEIGHT * .51f);
        settingsStage.addActor(distanceLabel);
        final TextField distanceTextField = new CustomTextField("123", skin);
        distanceTextField.setPosition(V_WIDTH * .7f, V_HEIGHT * .51f);
        distanceTextField.setWidth(textFieldWidth);
        settingsStage.addActor(distanceTextField);



        sidePicker = new SidePicker(skin);

        settingsStage.addActor(sidePicker);
        /**
         * four textFields for numeric input of the actual
         * measured ropelengths
         */

        final TextField leftDownTextField = new CustomTextField("123",skin);
        leftDownTextField.setPosition(sidePicker.getX()+SidePicker.LDFX,
                sidePicker.getY()+SidePicker.LDFY);
        leftDownTextField.setWidth(textFieldWidth);
        leftDownTextField.setVisible(false);
        settingsStage.addActor(leftDownTextField);

        final TextField leftUpTextField = new CustomTextField("456",skin);
        leftUpTextField.setPosition(sidePicker.getX()+SidePicker.LTFX,
                sidePicker.getY()+SidePicker.LTFY);
        leftUpTextField.setWidth(textFieldWidth);
        leftUpTextField.setVisible(false);
        settingsStage.addActor(leftUpTextField);

        final TextField rightDownTextField = new CustomTextField("789",skin);
        rightDownTextField.setPosition(sidePicker.getX()+SidePicker.RDFX,
                sidePicker.getY()+SidePicker.RDFY);
        rightDownTextField.setWidth(textFieldWidth);
        rightDownTextField.setVisible(false);
        settingsStage.addActor(rightDownTextField);

        final TextField rightUpTextField = new CustomTextField("876",skin);
        rightUpTextField.setPosition(sidePicker.getX()+SidePicker.RTFX,
                sidePicker.getY()+SidePicker.RTFY);
        rightUpTextField.setWidth(textFieldWidth);
        rightUpTextField.setVisible(false);
        settingsStage.addActor(rightUpTextField);

        /**
         * some callback functions
         */
        SidePicker.SideCallbacks sideCallbacks = new SidePicker.SideCallbacks() {
            @Override
            public void leftDown(boolean enable) {
                leftDownTextField.setVisible(enable);
            }

            @Override
            public void rightDown(boolean enable) {
                rightDownTextField.setVisible(enable);
            }

            @Override
            public void leftUp(boolean enable) {
                leftUpTextField.setVisible(enable);
            }

            @Override
            public void rightUp(boolean enable) {
                rightUpTextField.setVisible(enable);
            }
        };

        sidePicker.registerCallbacks(sideCallbacks);

        TextButton submitButton = new TextButton("calibrate", skin, "header");
        submitButton.setPosition((V_WIDTH-submitButton.getWidth())/2, V_HEIGHT*.06f);
        submitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                messageQueue.enqueue(Message.getAMessage(Integer.valueOf(distanceTextField.getText())));
                messageQueue.enqueue(Message.getHMessage(Integer.valueOf(heigthTextField.getText())));
                messageQueue.enqueue(Message.getIMessage(Integer.valueOf(leftDownTextField.getText())));
                messageQueue.enqueue(Message.getJMessage(Integer.valueOf(rightDownTextField.getText())));
                messageQueue.enqueue(Message.getKMessage(Integer.valueOf(rightUpTextField.getText())));
                messageQueue.enqueue(Message.getLMessage(Integer.valueOf(leftUpTextField.getText())));
                messageQueue.enqueue(Message.getCMessage());
                messageQueue.start();


            }
        });

        settingsStage.addActor(submitButton);

    }

    private void populateColorPickStage(){
        Texture colorPickTexture = new Texture(Gdx.files.internal("colormap.png"));
        final Pixmap colorPickPixmap = new Pixmap(Gdx.files.internal("colormap.png"));
        colorPickImage = new Image(colorPickTexture);
        colorPickImage.addListener(new InputListener(){

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                pickedColor.set( colorPickPixmap.getPixel(
                        colorPickPixmap.getWidth()-(int)x,
                        colorPickPixmap.getHeight()-(int)y) );
                colorPickButton.setColor(pickedColor);
                pickedPosition.set(x,y);
                isColorPicking = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                setStage(adjustStage);
                isInColorPickingMode = false;
                isColorPicking = false;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                pickedColor.set(colorPickPixmap.getPixel(
                        (int) x,
                        colorPickPixmap.getHeight() - (int) y));
                colorPickButton.setColor(pickedColor);
                pickedPosition.set(x,y);

                enqueueRGBMessage();
                messageQueue.enqueue(Message.getUpdateMessage());
                messageQueue.start();
                if (messageTransmitter.isSending())
                {
                    messageTransmitter.transmit(messageQueue.decueue());
                }
            }
        });

        colorPickCaptionLabel = new Label("pick color", skin, "big");
        colorPickCaptionLabel.setPosition(
                Constants.COLORPICK_CAPTION_X*V_WIDTH,
                Constants.COLORPICK_CAPTION_Y*V_HEIGHT
        );

        colorPickStage.addActor(colorPickImage);
        colorPickStage.addActor(colorPickCaptionLabel);
    }

    private void populateDeviceSelectStage(){

        scanForDeviceButton = new TextButton("scan for devices", skin, "device-scan");
        scanForDeviceButton.setPosition(Constants.SCAN_BUTTON_X,
                Constants.SCAN_BUTTON_Y);
        scanForDeviceButton.addListener(new InputListener(){

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                btAccesor.doDiscovery();
                scanForDeviceButton.setText("is scanning...");
                return true;
            }
        });

        btDeviceSelectStage.addActor(scanForDeviceButton);

    }



    private void setStage(Stage stage){
        stages.clear();
        inputMultiplexer.clear();
        stages.add(headerStage);
        inputMultiplexer.addProcessor(headerStage);
        stages.add(stage);
        inputMultiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    private void updateInstructionList(){
        StringBuilder builder = new StringBuilder();
        for (int i = 0 ; i< instructions.size; i++){
            builder.append(Integer.toString(i+1)+": ");
            builder.append(instructions.get(i)+"\n");
        }
        String newText = builder.toString();
        instructionListLabel.setText(newText);
    }


    private void drawBackground(){
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawFooterBackground(shapeRenderer);
        drawHeaderBackground(shapeRenderer);
        shapeRenderer.end();
    }


    void drawFooterBackground(ShapeRenderer shapeRenderer){
        shapeRenderer.setColor(Constants.FOOTER_COLOR);
        shapeRenderer.rect(0,0,V_WIDTH,V_HEIGHT* Constants.FOOTER_HEIGHT);
    }
    void drawHeaderBackground(ShapeRenderer shapeRenderer){
        shapeRenderer.setColor(Constants.HEADER_COLOR);
        shapeRenderer.rect(0,V_HEIGHT*(1-Constants.HEADER_HEIGHT),
                V_WIDTH,V_HEIGHT*Constants.HEADER_HEIGHT);
       /* shapeRenderer.setColor(.9f,.8f,.8f,1);
        shapeRenderer.rect(0,V_HEIGHT*(1-Constants.HEADER_HEIGHT),
                V_WIDTH,V_HEIGHT*Constants.HEADER_HEIGHT/2);*/
    }

    private void drawOverlayBackground(){
        Gdx.gl20.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Constants.OVERLAY_BACKGROUND_COLOR);
        shapeRenderer.rect(0, 0, V_WIDTH, V_HEIGHT);
        shapeRenderer.end();
    }

    private void enqueueRGBMessage()
    {
        messageQueue.enqueue(Message.getRedMessage(
                (byte)((int)(pickedColor.r*255)&0xFF)
        ));
        messageQueue.enqueue(Message.getGreenMessage(
                (byte) ((int) (pickedColor.g * 255) & 0xFF)
        ));
        messageQueue.enqueue(Message.getBlueMessage(
                (byte) ((int) (pickedColor.b * 255) & 0xFF)
        ));
    }





    /**
     * a handler which is used by the bluetooth accessor to
     * talk back to this controlPanelScreen
     */
    private BtMessageHandler handler = new BtMessageHandler(){

        @Override
        public void setState(int state) {
            ControlPanelScreen.this.state = state;

        }

        @Override
        public void receiveMessage(byte[] msg) {
            messageTransmitter.receiveAnswer(msg);

        }

        @Override
        public void finishedDiscovery() {
            scanForDeviceButton.setText("scan for devices");

        }

        @Override
        public void getPairedDevice(String name, String address) {
           discoveredNewDevice(name,address);

        }

        @Override
        public void discoveredNewDevice(final String name, final String address) {
            TextButton newDeviceButton = new TextButton(name, skin, "device");
            newDeviceButton.setPosition(Constants.DEVICE_BUTTON_X*V_WIDTH,
                    (Constants.DEVICE_BUTTON_Y+ devices.size* Constants.DEVICE_BUTTON_OFFSET_Y)*V_HEIGHT);
            newDeviceButton.addListener(new InputListener(){

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    selectedDevice = name;
                    btAccesor.selectDevice(address);
                    setStage(adjustStage);
                    return true;
                }
            });
            devices.add(newDeviceButton);
            btDeviceSelectStage.addActor(newDeviceButton);


        }

        @Override
        public void connect(String deviceName) {
            btSelectButtonTab.setColor(0,1,0,1);
            batteryIndicator.setVisible(true);
            sendButton.setVisible(true);


        }

        @Override
        public void failedToConnect() {
            btSelectButtonTab.setColor(1,0,0,1);
            batteryIndicator.setVisible(false);
            sendButton.setVisible(false);

        }

        @Override
        public void lostConnection() {
            btSelectButtonTab.setColor(1,0,0,1);
            batteryIndicator.setVisible(false);
            sendButton.setVisible(false);

        }

    };
}
