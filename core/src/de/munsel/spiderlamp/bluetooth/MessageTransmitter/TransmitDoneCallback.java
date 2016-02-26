package de.munsel.spiderlamp.bluetooth.MessageTransmitter;

import de.munsel.spiderlamp.bluetooth.Message;

/**
 * Created by munsel on 27.01.16.
 */
public interface TransmitDoneCallback {

    void done(Message message);
}
