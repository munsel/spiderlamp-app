package de.munsel.spiderlamp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import de.munsel.spiderlamp.actors.ColorPickButton;
import de.munsel.spiderlamp.actors.CustomTextField;
import de.munsel.spiderlamp.actors.SidePicker;
import de.munsel.spiderlamp.actors.XyChooser;
import de.munsel.spiderlamp.bluetooth.BluetoothAcessor;
import de.munsel.spiderlamp.bluetooth.BtMessageHandler;


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

    }


    @Override
    public void show() {
        btAccesor.setBtMessageHandler(handler);
        btAccesor.init();
        Gdx.input.setInputProcessor(inputMultiplexer);
        headerStage.clear();

        populateAdjustStage();
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
     * to generate a BitmapFont on the fly
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

        settingsButtonTab = new Button(skin, "settings");
        settingsButtonTab.setPosition((Constants.FIRST_TAB_X+Constants.OFFSET_TAB_X)*V_WIDTH,
                Constants.TABS_Y*V_HEIGHT);
        settingsButtonTab.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setStage(settingsStage);
            }
        });


        btSelectButtonTab = new Button(skin, "bt-search");
        btSelectButtonTab.setPosition((Constants.FIRST_TAB_X+2*Constants.OFFSET_TAB_X)*V_WIDTH,
                Constants.TABS_Y*V_HEIGHT);
        btSelectButtonTab.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setStage(btDeviceSelectStage);
            }
        });

        instructionsButtonTab = new Button(skin,"instructions");
        instructionsButtonTab.setPosition((Constants.FIRST_TAB_X+3*Constants.OFFSET_TAB_X)*V_WIDTH,
                Constants.TABS_Y*V_HEIGHT);
        instructionsButtonTab.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                updateInstructionList();
                setStage(instructionListStage);
            }
        });

        shutDownButton = new Button(skin, "off");
        shutDownButton.setPosition((Constants.FIRST_TAB_X+4*Constants.OFFSET_TAB_X)*V_WIDTH,
                Constants.TABS_Y*V_HEIGHT);
        shutDownButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });


        headerStage.addActor(headerButton);
        headerStage.addActor(adjustButtonTab);
        headerStage.addActor(settingsButtonTab);
        headerStage.addActor(btSelectButtonTab);
        headerStage.addActor(instructionsButtonTab);
        headerStage.addActor(shutDownButton);
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
                //String message = "r"+(int)(pickedColor.r*255);
                byte blue = (byte)((int)(pickedColor.b*255)&0xFF);
                byte green = (byte)((int)(pickedColor.g*255)&0xFF);
                byte red = (byte)((int)(pickedColor.r*255)&0xFF);
                byte[] bytesBlue = {'b', blue,0,'\n'};
                byte[] bytesGreen = {'g', green,0,'\n'};
                byte[] bytesRed = {'r', red,0,'\n'};
                Gdx.app.log(TAG, Integer.toString( blue) );
                btAccesor.writeMessage(bytesBlue);
                Gdx.app.log(TAG, Integer.toString( green) );
                btAccesor.writeMessage(bytesGreen);
                Gdx.app.log(TAG, Integer.toString( red) );
                btAccesor.writeMessage(bytesRed);

                byte[] bytesUpdate = {'u', 0,0,'\n'};
                btAccesor.writeMessage(bytesUpdate);
                return true;
            }
        });

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

        intensitySlider = new Slider(4,10,0.2f,false,skin, "intensity");
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
        TextField heigthTextField = new CustomTextField("123", skin);
        heigthTextField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        heigthTextField.setPosition(V_WIDTH*.7f, V_HEIGHT*.46f);
        heigthTextField.setWidth(textFieldWidth);
        settingsStage.addActor(heigthTextField);

        Label distanceLabel = new Label("a=    cm", skin);
        distanceLabel.setPosition(V_WIDTH * .63f, V_HEIGHT * .51f);
        settingsStage.addActor(distanceLabel);
        TextField distanceTextField = new CustomTextField("123", skin);
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
        submitButton.setPosition((V_WIDTH-submitButton.getWidth())/2, V_HEIGHT*.1f);
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
        shapeRenderer.setColor(Constants.HEADER_FOOTER_COLOR);
        shapeRenderer.rect(0,0,V_WIDTH,V_HEIGHT* Constants.FOOTER_HEIGHT);
        shapeRenderer.rect(0,V_HEIGHT*(1-Constants.HEADER_HEIGHT),
                V_WIDTH,V_HEIGHT*Constants.HEADER_HEIGHT);
        shapeRenderer.end();



    }

    private void drawOverlayBackground(){
        Gdx.gl20.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Constants.OVERLAY_BACKGROUND_COLOR);
        shapeRenderer.rect(0, 0, V_WIDTH, V_HEIGHT);
        shapeRenderer.end();
    }




    /**
     * a handler which is uses by the bluetooth accessor to
     * talk back to this controlPanelScreen
     */
    private BtMessageHandler handler = new BtMessageHandler(){

        @Override
        public void setState(int state) {
            ControlPanelScreen.this.state = state;

        }

        @Override
        public void receiveMessage(String msg) {


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


        }

        @Override
        public void failedToConnect() {

        }

        @Override
        public void lostConnection() {

        }

    };
}
