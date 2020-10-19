package de.munsel.spiderlamp.desktop;

import com.badlogic.gdx.Gdx;
import de.munsel.spiderlamp.bluetooth.BluetoothAcessor;
import de.munsel.spiderlamp.bluetooth.BtMessageHandler;

/**
 * Created by munsel on 28.09.15.
 */
public class DesktopBluetoothAcessor implements BluetoothAcessor {
    private final String TAG = DesktopBluetoothAcessor.class.getSimpleName();

    @Override
    public void dispose() {

    }

    @Override
    public void init() {

    }

    @Override
    public void selectDevice(String adress) {

    }

    @Override
    public void doDiscovery() {

    }

    public boolean isConnected() {
        Gdx.app.log(TAG, "nothing happens here!");
        return false;
    }

    @Override
    public void writeMessage(byte[] msg) {

    }

    @Override
    public void setBtMessageHandler(BtMessageHandler handler) {

    }


}
