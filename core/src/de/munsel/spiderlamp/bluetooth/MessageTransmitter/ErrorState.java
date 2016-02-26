package de.munsel.spiderlamp.bluetooth.MessageTransmitter;

import com.badlogic.gdx.Gdx;
import de.munsel.spiderlamp.bluetooth.BluetoothAcessor;
import de.munsel.spiderlamp.bluetooth.Message;

/**
 * Created by munsel on 27.01.16.
 */
public class ErrorState implements TransmitState {
    private final String TAG = ErrorState.class.getSimpleName();

    private MessageTransmitter transmitter;
    private BluetoothAcessor acessor;
    private Message message;

    private final int MAX_ERROR_COUNT = 15;

    public ErrorState(MessageTransmitter transmitter,Message message, BluetoothAcessor acessor)
    {
        this.transmitter = transmitter;
        this.message = message;
        this.acessor = acessor;
    }

    @Override
    public void update(float delta) {
        transmitter.incrementCounter();
        if (transmitter.getAttemptCounter() < MAX_ERROR_COUNT)
        {
            Gdx.app.log(TAG, "attempt nr:"+transmitter.getAttemptCounter());
            transmitter.setState(new SendedState(transmitter, message, acessor));
        } else
        {
            transmitter.setState(new IdleState(transmitter, acessor));
            transmitter.done(Message.getErrorMessage());
        }
    }

    @Override
    public void receiveAnswer(byte[] answer) {
        Gdx.app.log(TAG, "i should not receive!");

    }

    @Override
    public void transmit(Message message) {
        Gdx.app.log(TAG, "i should not transmit!");

    }
}
