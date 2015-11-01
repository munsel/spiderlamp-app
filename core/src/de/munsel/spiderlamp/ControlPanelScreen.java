package de.munsel.spiderlamp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import de.munsel.spiderlamp.bluetooth.BluetoothAcessor;
import de.munsel.spiderlamp.bluetooth.BtMessageHandler;


/**
 * Created by munsel on 16.10.15.
 */
public class ControlPanelScreen implements Screen {
    private final String TAG = ControlPanelScreen.class.getSimpleName();

    // CONSTANTS
    public static final int V_WIDTH = 720;
    public static final int V_HEIGHT = 1280;

    private final float POS_INCREMENT = 0.25f;

    private BluetoothAcessor btAccesor;
    private int state;
    private Array<String> messages;

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
    private Stage uiStage, btDeviceSelectStage, colorPickStage;
    private boolean isDeviceSelecting;
    private String selectedDevice;
    private TextButton headerButton;
    private Button connectDeviceButton;
    private Image colorPickImage;
    private Array<TextButton> devices;
    private TextButton scanForDeviceButton;
    private TextButton addButton, shutDownButton;
    private Button xMoreButton, xLessButton, yMoreButton, yLessButton, zMoreButton, zLessButton;
    private Button colorPickButton;
    private boolean isInColorPickingMode;
    private boolean isColorPicking;
    private Vector2 pickedPosition;
    private Color pickedColor;
    private Label colorPickCaptionLabel;
    private float xPos, yPos, zPos;
    private Label headlinePositionLabel;
    private Label xPosLabel, yPosLabel, zPosLabel;
    private Label connectedDeviceLabel, logLabel;
    private Label instructionListLabel;
    private Slider intensitySlider;

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
        camera.setToOrtho(false, V_WIDTH,V_HEIGHT);

        isDeviceSelecting = false;
        isInColorPickingMode = false;
        isColorPicking = false;
        pickedColor = new Color(UiPositioningConstants.COLORPICK_DEFAULT_COLOR);
        pickedPosition = new Vector2();
        devices = new Array<TextButton>();

        messages = new Array<String>();

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
        uiStage = new Stage(new StretchViewport(V_WIDTH,V_HEIGHT, camera));
        initializeLabels();
        initializeButtons();

        btDeviceSelectStage = new Stage(new StretchViewport(V_WIDTH,V_HEIGHT, camera));
        colorPickStage = new Stage(new StretchViewport(V_WIDTH,V_HEIGHT, camera));

        //Color Picker init
        Texture colorPickTexture = new Texture(Gdx.files.internal("colormap.png"));
        final Pixmap colorPickPixmap = new Pixmap(Gdx.files.internal("colormap.png"));
        colorPickImage = new Image(colorPickTexture);
        colorPickImage.addListener(new InputListener(){

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                pickedColor.set( colorPickPixmap.getPixel(
                        colorPickPixmap.getWidth()-(int)x,
                        colorPickPixmap.getHeight()-(int)y) );
                pickedPosition.set(x,y);
                isColorPicking = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.input.setInputProcessor(uiStage);
                isInColorPickingMode = false;
                isColorPicking = false;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                Gdx.app.log(TAG, "dragged, pos:x " + Float.toString(x)+" y "+Float.toString(y));
                pickedColor.set(colorPickPixmap.getPixel(
                        (int) x,
                        colorPickPixmap.getHeight() - (int) y));
                pickedPosition.set(x,y);
            }
        });
        colorPickStage.addActor(colorPickImage);

        //Slider init
        intensitySlider = new Slider(4,10,0.2f,true,skin);
        intensitySlider.setSize(35, 150);
        //intensitySlider.setDebug(true);
        intensitySlider.setPosition(350,50);













    }
    @Override
    public void show() {
        btAccesor.setBtMessageHandler(handler);
        btAccesor.init();
        Gdx.input.setInputProcessor(uiStage);
        uiStage.clear();
        addActors();


    }

    @Override
    public void render(float delta) {
        drawBackground();
        uiStage.act(delta);
        uiStage.draw();
        if (isDeviceSelecting){
            drawOverlayBackground();
            btDeviceSelectStage.act(delta);
            btDeviceSelectStage.draw();
        }

        if (isInColorPickingMode){
            drawOverlayBackground();
            colorPickStage.act(delta);
            colorPickStage.draw();
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
        parameter.color = Color.BLACK;
        uiFont72 = generator.generateFont(parameter);
        parameter.size = 36;
        uiFont36 =generator.generateFont(parameter);
            generator.dispose();
    }

    private void initializeLabels(){
        Label.LabelStyle logLabelStyle = new Label.LabelStyle();
        logLabelStyle.font = uiFont24;
        logLabel = new Label(LOG_LABEL_DEFAULT_TEXT,logLabelStyle);
        logLabel.setPosition(20,951-logLabel.getHeight());
        connectedDeviceLabel = new Label(CONNECTED_DEVICE_DEFAULT_TEXT, skin);
        connectedDeviceLabel.setPosition(
                UiPositioningConstants.CONNECTED_LABEL_OFFSET_X,
                UiPositioningConstants.CONNECTED_LABEL_OFFSET_Y);

        headlinePositionLabel = new Label("position",skin);
        headlinePositionLabel.setPosition(UiPositioningConstants.POSITION_HEADLINE_LABEL_X,
                UiPositioningConstants.POSITION_HEADLINE_LABEL_Y);

        xPosLabel = new Label("x", skin, "big");
        xPosLabel.setPosition(UiPositioningConstants.POS_OFFSET_X+UiPositioningConstants.POSITION_LABEL_OFFSET_X,
                UiPositioningConstants.X_POS_OFFSET_Y+UiPositioningConstants.POSITION_LABEL_OFFSET_Y);
        yPosLabel = new Label("y", skin, "big");
        yPosLabel.setPosition(UiPositioningConstants.POS_OFFSET_X+UiPositioningConstants.POSITION_LABEL_OFFSET_X,
                UiPositioningConstants.Y_POS_OFFSET_Y+UiPositioningConstants.POSITION_LABEL_OFFSET_Y);
        zPosLabel = new Label("z", skin, "big");
        zPosLabel.setPosition(UiPositioningConstants.POS_OFFSET_X+UiPositioningConstants.POSITION_LABEL_OFFSET_X,
                UiPositioningConstants.Z_POS_OFFSET_Y+UiPositioningConstants.POSITION_LABEL_OFFSET_Y);

        instructionListLabel = new Label("",skin,"instruction-list");
        instructionListLabel.setAlignment(Align.topLeft);
        //instructionListLabel.setHeight(UiPositioningConstants.InstructionListLabel.HEIGHT);
        //instructionListLabel.setWidth(UiPositioningConstants.InstructionListLabel.WIDTH);
        instructionListLabel.setPosition(UiPositioningConstants.InstructionListLabel.X,
                UiPositioningConstants.InstructionListLabel.Y);

        colorPickCaptionLabel = new Label("pick color", skin, "colorpick-caption");
        colorPickCaptionLabel.setPosition(
                UiPositioningConstants.COLORPICK_CAPTION_X,
                UiPositioningConstants.COLORPICK_CAPTION_Y
        );

    }


    private void addActors(){
        uiStage.addActor(headerButton);
        uiStage.addActor(connectedDeviceLabel);
        uiStage.addActor(logLabel);
        uiStage.addActor(connectDeviceButton);
        uiStage.addActor(headlinePositionLabel);
        uiStage.addActor(xPosLabel);
        uiStage.addActor(yPosLabel);
        uiStage.addActor(zPosLabel);
        uiStage.addActor(xLessButton);
        uiStage.addActor(xMoreButton);
        uiStage.addActor(yLessButton);
        uiStage.addActor(yMoreButton);
        uiStage.addActor(zLessButton);
        uiStage.addActor(zMoreButton);
        uiStage.addActor(colorPickButton);
        uiStage.addActor(addButton);
        uiStage.addActor(instructionListLabel);
        uiStage.addActor(intensitySlider);


        btDeviceSelectStage.addActor(scanForDeviceButton);

        colorPickStage.addActor(colorPickCaptionLabel);



    }

    private void initializeButtons(){
        TextButton.TextButtonStyle headerStyle = new TextButton.TextButtonStyle();
        headerStyle.font = uiFont72;
        headerButton = new TextButton("//SPINNE", headerStyle);
        headerButton.setPosition(50,1150);
        connectDeviceButton = new Button(skin, "bt-search");
        connectDeviceButton.setPosition(UiPositioningConstants.BLUETOOTH_SEARCBUTTON_OFFSET_X,
                UiPositioningConstants.BLUETOOTH_SEARCBUTTON_OFFSET_Y);
        connectDeviceButton.addListener(new InputListener(){

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                isDeviceSelecting = true;
                Gdx.input.setInputProcessor(btDeviceSelectStage);
                return true;
            }
        });

        //position modifier buttons

        xLessButton = new Button(skin, "pos-less");
        xLessButton.setPosition(UiPositioningConstants.POS_OFFSET_X+UiPositioningConstants.LESS_OFFSET_X,
                UiPositioningConstants.X_POS_OFFSET_Y+UiPositioningConstants.LESS_OFFSET_Y);
        xLessButton.addListener(new InputListener(){

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                xPos -= POS_INCREMENT;
                xPosLabel.setText(Float.toString(xPos));
                return true;
            }
        });
        xMoreButton = new Button(skin, "pos-more");
        xMoreButton.setPosition(UiPositioningConstants.POS_OFFSET_X + UiPositioningConstants.MORE_OFFSET_X,
                UiPositioningConstants.X_POS_OFFSET_Y + UiPositioningConstants.MORE_OFFSET_Y);
        xMoreButton.addListener(new InputListener(){

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                xPos += POS_INCREMENT;
                xPosLabel.setText(Float.toString(xPos));
                return true;
            }
        });

        yLessButton = new Button(skin, "pos-less");
        yLessButton.setPosition(UiPositioningConstants.POS_OFFSET_X+UiPositioningConstants.LESS_OFFSET_X,
                UiPositioningConstants.Y_POS_OFFSET_Y+UiPositioningConstants.LESS_OFFSET_Y);
        yLessButton.addListener(new InputListener(){

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                yPos -= POS_INCREMENT;
                yPosLabel.setText(Float.toString(yPos));
                return true;
            }
        });

        yMoreButton = new Button(skin, "pos-more");
        yMoreButton.setPosition(UiPositioningConstants.POS_OFFSET_X + UiPositioningConstants.MORE_OFFSET_X,
                UiPositioningConstants.Y_POS_OFFSET_Y + UiPositioningConstants.MORE_OFFSET_Y);
        yMoreButton.addListener(new InputListener(){

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                yPos += POS_INCREMENT;
                yPosLabel.setText(Float.toString(yPos));
                return true;
            }
        });

        zLessButton = new Button(skin, "pos-less");
        zLessButton.setPosition(UiPositioningConstants.POS_OFFSET_X+UiPositioningConstants.LESS_OFFSET_X,
                UiPositioningConstants.Z_POS_OFFSET_Y+UiPositioningConstants.LESS_OFFSET_Y);
        zLessButton.addListener(new InputListener(){

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                zPos -= POS_INCREMENT;
                zPosLabel.setText(Float.toString(zPos));
                return true;
            }
        });

        zMoreButton = new Button(skin, "pos-more");
        zMoreButton.setPosition(UiPositioningConstants.POS_OFFSET_X + UiPositioningConstants.MORE_OFFSET_X,
                UiPositioningConstants.Z_POS_OFFSET_Y + UiPositioningConstants.MORE_OFFSET_Y);
        zMoreButton.addListener(new InputListener(){

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                zPos += POS_INCREMENT;
                zPosLabel.setText(Float.toString(zPos));
                return true;
            }
        });

        scanForDeviceButton = new TextButton("scan for devices", skin, "device-scan");
        scanForDeviceButton.setPosition(UiPositioningConstants.SCAN_BUTTON_X,
                UiPositioningConstants.SCAN_BUTTON_Y);
        scanForDeviceButton.addListener(new InputListener(){

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                btAccesor.doDiscovery();
                scanForDeviceButton.setText("is scanning...");
                return true;
            }
        });

        addButton = new TextButton("add", skin, "device-scan");
        addButton.setPosition(UiPositioningConstants.ADD_BUTTON_X,
                UiPositioningConstants.ADD_BUTTON_Y);
        addButton.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    String message = "x: " + xPosLabel.getText().toString()
                            + ", y: " + yPosLabel.getText().toString()
                            + ", z: " + zPosLabel.getText().toString();
                    //btAccesor.writeMessage(message.getBytes());
                    String newLog = instructionListLabel.getText().toString()+"\n"+message;
                    instructionListLabel.setText(newLog);
                instructionListLabel.setPosition(UiPositioningConstants.InstructionListLabel.X,
                        UiPositioningConstants.InstructionListLabel.Y
                        +UiPositioningConstants.InstructionListLabel.HEIGHT
                        - 2*instructionListLabel.getHeight());
                return true;
            }
        });

        colorPickButton = new Button(skin,"color-pick");
        colorPickButton.setPosition(UiPositioningConstants.COLORPICK_X,
                UiPositioningConstants.COLORPICK_Y);
        colorPickButton.setColor(UiPositioningConstants.COLORPICK_DEFAULT_COLOR);
        colorPickButton.setSize(UiPositioningConstants.COLORPICK_WIDTH,
                UiPositioningConstants.COLORPICK_HEIGHT);
        colorPickButton.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                isInColorPickingMode = true;
                Gdx.input.setInputProcessor(colorPickStage);
                return true;
            }
        });

    }

    private void drawBackground(){
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.05f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.valueOf("216778"));
        shapeRenderer.rect(0,961,720,319);
        shapeRenderer.setColor(Color.valueOf("6f8a91"));
        shapeRenderer.rect(445,0,275,1280);
        shapeRenderer.setColor(Color.valueOf("6f918a"));
        shapeRenderer.rect(0,0,720,445);
        shapeRenderer.setColor(UiPositioningConstants.BOUNDARY_BOX_COLOR);
        shapeRenderer.rect(
                UiPositioningConstants.POS_OFFSET_X,
                UiPositioningConstants.X_POS_OFFSET_Y,
                UiPositioningConstants.BOUNDARY_BOX_WIDTH,
                UiPositioningConstants.BOUNDARY_BOX_HEIGHT
        );
        shapeRenderer.rect(
                UiPositioningConstants.POS_OFFSET_X,
                UiPositioningConstants.Y_POS_OFFSET_Y,
                UiPositioningConstants.BOUNDARY_BOX_WIDTH,
                UiPositioningConstants.BOUNDARY_BOX_HEIGHT
        );

        shapeRenderer.rect(
                UiPositioningConstants.POS_OFFSET_X,
                UiPositioningConstants.Z_POS_OFFSET_Y,
                UiPositioningConstants.BOUNDARY_BOX_WIDTH,
                UiPositioningConstants.BOUNDARY_BOX_HEIGHT
        );
        shapeRenderer.setColor(pickedColor);
        shapeRenderer.rect(
                UiPositioningConstants.COLORPICK_X,
                UiPositioningConstants.COLORPICK_Y,
                UiPositioningConstants.COLORPICK_WIDTH,
                UiPositioningConstants.COLORPICK_HEIGHT
        );

        shapeRenderer.end();



    }

    private void drawOverlayBackground(){
        Gdx.gl20.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(UiPositioningConstants.OVERLAY_BACKGROUND_COLOR);
        shapeRenderer.rect(0, 0, V_WIDTH, V_HEIGHT);
        shapeRenderer.end();
    }

    private void addMessageToLogLabel(String sender, String msg){
        String newLog = logLabel.getText().toString()+"\n"+sender+": "+msg;
        logLabel.setText(newLog);
        logLabel.setPosition(20,951-logLabel.getHeight());


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
            addMessageToLogLabel(selectedDevice, msg);

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
            newDeviceButton.setPosition(UiPositioningConstants.DEVICE_BUTTON_X,
                    UiPositioningConstants.DEVICE_BUTTON_Y+ devices.size* UiPositioningConstants.DEVICE_BUTTON_OFFSET_Y);
            newDeviceButton.addListener(new InputListener(){

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    selectedDevice = name;
                    btAccesor.selectedDevice(address);
                    Gdx.input.setInputProcessor(uiStage);
                    isDeviceSelecting = false;
                    return true;
                }
            });
            devices.add(newDeviceButton);
            btDeviceSelectStage.addActor(newDeviceButton);


        }

        @Override
        public void connect(String deviceName) {
            connectedDeviceLabel.setText("connected to "+deviceName);

        }

        @Override
        public void failedToConnect() {

        }

        @Override
        public void lostConnection() {

        }

        @Override
        public void write(byte[] buffer) {

        }
    };
}
