package de.munsel.spiderlamp.bluetooth.MessageTransmitter;

import com.badlogic.gdx.Gdx;
import de.munsel.spiderlamp.bluetooth.BluetoothAcessor;
import de.munsel.spiderlamp.bluetooth.Message;

/**
 * Created by munsel on 27.01.16.
 */
public class SendedState implements TransmitState {
    private final String TAG = SendedState.class.getSimpleName();

    private float timer;
    private final float TIMER_LIMIT = 1f;


    private MessageTransmitter transmitter;
    private BluetoothAcessor acessor;
    private Message message;

    private byte sendedId;

    public SendedState(MessageTransmitter transmitter, Message message, BluetoothAcessor acessor)
    {
        this.transmitter = transmitter;
        this.message = message;
        this.acessor = acessor;
        timer = 0;
        acessor.writeMessage(message.getBytes());
        Gdx.app.log(TAG, "sended stuff");
    }

    @Override
    public void update(float delta) {
        timer += delta;
        if (timer > TIMER_LIMIT)
        {
            Gdx.app.log(TAG, "timed out");
            transmitter.setState(new ErrorState(transmitter,message, acessor));
        }
    }


    @Override
    public void receiveAnswer(byte[] answer) {
        Gdx.app.log(TAG, "answer is: "+(char)answer[0]);
        Gdx.app.log(TAG, "id  is: "+(char)message.getId());
        if (message.getId() != answer[0])
        {
            transmitter.setState(new ErrorState(transmitter, message,acessor));
        } else // when message was correct
        {
            transmitter.resetCounter();
            transmitter.setState(new IdleState(transmitter, acessor));
            transmitter.done(new Message(answer[0], answer[1], answer[2]));
        }
    }

    @Override
    public void transmit(Message message) {
        Gdx.app.log(TAG, "nothing to do here");
    }


}
