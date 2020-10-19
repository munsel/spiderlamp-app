package de.munsel.spiderlamp.bluetooth.MessageTransmitter;

import com.badlogic.gdx.Gdx;
import de.munsel.spiderlamp.bluetooth.BluetoothAcessor;
import de.munsel.spiderlamp.bluetooth.Message;


/**
 * Created by munsel on 27.01.16.
 */
public class IdleState implements TransmitState {
    private static final String TAG = IdleState.class.getSimpleName();



    private MessageTransmitter transmitter;
    private BluetoothAcessor acessor;

    public IdleState(MessageTransmitter transmitter, BluetoothAcessor acessor)
    {
        this.transmitter = transmitter;
        this.acessor = acessor;
    }

    @Override
    public void update(float delta) {

    }


    @Override
    public void receiveAnswer(byte[] answer) {
        Gdx.app.log(TAG, "i should not receive anything, but i do!");
    }

    @Override
    public void transmit(Message message) {
        transmitter.setState(new SendedState(transmitter, message, acessor));
    }
}
