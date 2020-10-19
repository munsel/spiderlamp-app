package de.munsel.spiderlamp.bluetooth.MessageTransmitter;

import com.badlogic.gdx.Gdx;
import de.munsel.spiderlamp.bluetooth.BluetoothAcessor;
import de.munsel.spiderlamp.bluetooth.Message;

/**
 * Created by munsel on 27.01.16.
 */
public class MessageTransmitter {
    private final String TAG = MessageTransmitter.class.getSimpleName();



    private TransmitState state;
    private int attemptCounter;

    private TransmitDoneCallback callback;

    private BluetoothAcessor acessor;

    public MessageTransmitter(TransmitDoneCallback callback, BluetoothAcessor acessor)
    {
        this.acessor = acessor;
        state = new IdleState(this, acessor);
        this.callback = callback;
        attemptCounter = 0;
    }


    void done(Message message){
        attemptCounter = 0;
        setState(new IdleState(this, acessor));
        callback.done(message);
    }


 public void transmit(Message message)
    {
        state.transmit(message);

    }

    public void receiveAnswer(byte[] answer)
    {
        String ans = "";
        for (int i = 0; i< 10; i++){
            ans += answer[i]+" ";
        }
        Gdx.app.log(TAG, "answer is: "+ans);
        state.receiveAnswer(answer);
    }


    void setState(TransmitState newState)
    {
        state = newState;
    }

    public void update(float delta)
    {
        state.update(delta);
    }


    int getAttemptCounter(){return attemptCounter;}
    void incrementCounter(){attemptCounter++;}
    void resetCounter(){attemptCounter = 0;}

    public boolean isSending(){
        return state.getClass().getSimpleName().equals( IdleState.class.getSimpleName() );

    }


}
