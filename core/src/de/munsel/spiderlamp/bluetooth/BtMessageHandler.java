package de.munsel.spiderlamp.bluetooth;

/**
 * Handler to send back messages from android API to
 * libgdx core
 *
 * Created by munsel on 16.10.15.
 */
public interface BtMessageHandler {

    void setState(int state);
    void receiveMessage(String msg);
    void finishedDiscovery();
    void getPairedDevice(String name, String address);
    void discoveredNewDevice(String name, String address);
    void connect(String deviceName);
    void failedToConnect();
    void lostConnection();
    void write(byte[] buffer);
}
