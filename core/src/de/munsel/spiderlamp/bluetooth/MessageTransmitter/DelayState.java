package de.munsel.spiderlamp.bluetooth.MessageTransmitter;

import com.badlogic.gdx.Gdx;
import de.munsel.spiderlamp.bluetooth.BluetoothAcessor;
import de.munsel.spiderlamp.bluetooth.Message;

/**
 * Created by munsel on 03.08.16.
 */
public class DelayState implements TransmitState {
    private final String TAG = DelayState.class.getSimpleName();

    private MessageTransmitter transmitter;
    private BluetoothAcessor acessor;
    private float delayTime;


    public DelayState(MessageTransmitter transmitter, BluetoothAcessor acessor, float delayTime){
        this.transmitter = transmitter;
        this.delayTime = delayTime;
        this.acessor = acessor;
    }

    @Override
    public void update(float delta) {
        delayTime += delta;
        if (delayTime > delayTime){
            transmitter.setState(new IdleState(transmitter,acessor));
            transmitter.done(Message.getWaitMessage());  
        }
    }

    @Override
    public void receiveAnswer(byte[] answer) {
        Gdx.app.log(TAG, "i should not receive!");
    }

    @Override
    public void transmit(Message message) {
        Gdx.app.log(TAG, "neither should i transmit!");
    }
}
