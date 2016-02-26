package de.munsel.spiderlamp.bluetooth.MessageTransmitter;

import de.munsel.spiderlamp.bluetooth.Message;

/**
 * Created by munsel on 27.01.16.
 */
public interface TransmitState {


    void update(float delta);
    void receiveAnswer(byte[] answer);
    void transmit(Message message);


}
