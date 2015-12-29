package de.munsel.spiderlamp;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import de.munsel.spiderlamp.bluetooth.BluetoothAcessor;

/**
 * Created by munsel on 20.12.15.
 */
public class JoypadScreen implements Screen {

    private SpiderlampMain parent;
    private BluetoothAcessor acessor;

    //stage with Ui elements
    private Stage stage;
    private Skin skin;

    public JoypadScreen(SpiderlampMain parent, BluetoothAcessor acessor){
        this.parent = parent;
        this.acessor = acessor;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

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

    }

    @Override
    public void dispose() {

    }
}
