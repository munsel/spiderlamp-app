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
    private final float TIMER_LIMIT = .8f;


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
        String ans = "";
        for (int i = 0; i< 4; i++){
            ans += message.getBytes()[i]+" ";
        }
        Gdx.app.log(TAG, "sended is: "+ans);
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
        //Gdx.app.log(TAG, "answer is: "+(char)answer[0]);
        //Gdx.app.log(TAG, "id  is: "+(char)message.getId());

        byte idindex = 0;

        if (message.getId() != answer[idindex])
        {
            transmitter.setState(new ErrorState(transmitter, message,acessor));
        } else // when message was correct
        {
            transmitter.resetCounter();
            transmitter.setState(new IdleState(transmitter, acessor));
            transmitter.done(new Message(answer[idindex], answer[idindex+1], answer[idindex+2]));
        }
    }

    @Override
    public void transmit(Message message) {
        Gdx.app.log(TAG, "nothing to do here");
    }


}
