package de.munsel.spiderlamp;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import de.munsel.spiderlamp.bluetooth.BluetoothAcessor;
import de.munsel.spiderlamp.bluetooth.BtMessageHandler;

public class SpiderlampMain extends Game {
	private final String TAG = SpiderlampMain.class.getSimpleName();

	private Screen controlPanelScreen;

	BluetoothAcessor bluetoothAcessor;

	public SpiderlampMain(BluetoothAcessor acessor){
		bluetoothAcessor = acessor;
		}
	
	@Override
	public void create () {

		int i = 128;
		byte b = (byte)(i & 0xFF);
		Gdx.app.log(TAG, Byte.toString(b));

		Gdx.graphics.setDisplayMode(720, 1280, false);
		controlPanelScreen = new ControlPanelScreen(this, bluetoothAcessor);
		setScreen(controlPanelScreen);

	}
}
