package de.munsel.spiderlamp.bluetooth;

/**
 * Acessor to handle the communication from libgdx core project
 * to android API
 *
 * Created by munsel on 28.09.15.
 */
public interface BluetoothAcessor {
    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device


    void dispose();
    void init();
    void selectDevice(String adress);
    void doDiscovery();
    boolean isConnected();
    void writeMessage(byte[] msg);

    /**
     * to register a receiver to the acessor
     * @param handler
     */
    void setBtMessageHandler(BtMessageHandler handler);


}
