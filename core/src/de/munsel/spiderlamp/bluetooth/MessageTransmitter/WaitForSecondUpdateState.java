package de.munsel.spiderlamp.bluetooth.MessageTransmitter;

import de.munsel.spiderlamp.bluetooth.BluetoothAcessor;
import de.munsel.spiderlamp.bluetooth.Message;

/**
 * Created by munsel on 13.07.16.
 */
public class WaitForSecondUpdateState implements TransmitState {


    private MessageTransmitter transmitter;
    private BluetoothAcessor acessor;

    public WaitForSecondUpdateState(MessageTransmitter transmitter, BluetoothAcessor acessor){
        this.transmitter = transmitter;
        this.acessor = acessor;
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void receiveAnswer(byte[] answer) {
        if (answer[0]==Message.UPDATE_ID){
            transmitter.resetCounter();
            transmitter.setState(new IdleState(transmitter,acessor));
            transmitter.done(Message.getUpdateMessage());
        }

    }

    @Override
    public void transmit(Message message) {

    }
}
