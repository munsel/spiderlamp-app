package de.munsel.spiderlamp.bluetooth;



/**
 * Created by munsel on 27.01.16.
 */
public class Message {

    /**
     * since JAVA is not using ASCII 8bit-characters,
     * the message is in single byte packets
     * and ascii numbers are in hex as constants
     */

    private static final byte R_ASCII = 0x72;
    private static final byte G_ASCII = 0x67;
    private static final byte B_ASCII = 0x62;
    private static final byte E_ASCII = 0x65;
    private static final byte X_ASCII = 0x78;
    private static final byte Y_ASCII = 0x79;
    private static final byte Z_ASCII = 0x7A;
    private static final byte I_ASCII = 0x69;
    private static final byte J_ASCII = 0x6A;
    private static final byte K_ASCII = 0x6B;
    private static final byte U_ASCII = 0x75;
    private static final byte S_ASCII = 0x73;
    private static final byte O_ASCII = 0x6F;
    public static final byte V_ASCII = 0x76;
    private static final byte LF_ASCII = 0x0A;
    private static final byte ACK_ASCII = 0x06;


    private byte[] message;

    public Message(byte id, byte data1, byte data2)
    {
        message = new byte[4];
        message[0] = id;
        message[1] = data1;
        message[2] = data2;
        message[3] = LF_ASCII;
    }


    public static Message getRedMessage(byte data)
    {
        return new Message(R_ASCII, data, (byte)1);
    }

    public static Message getGreenMessage(byte data)
    {
        return new Message(G_ASCII, data, (byte)1);
    }

    public static Message getBlueMessage(byte data)
    {
        return new Message(B_ASCII, data, (byte)1);
    }

    public static Message getXMessage(int x)
    {
        return getSixteenBitDataMessage(X_ASCII, x);
    }

    public static Message getYMessage(int y)
    {
        return getSixteenBitDataMessage(Y_ASCII, y);
    }

    public static Message getZMessage(int z)
    {
        return getSixteenBitDataMessage(Z_ASCII, z);
    }

    public static Message getXDiffMessage(byte diff)
    {
        return new Message(I_ASCII, diff, (byte)1);
    }

    public static Message getYDiffMessage(byte diff)
    {
        return new Message(J_ASCII, diff, (byte)1);
    }

    public static Message getZDiffMessage(byte diff)
    {
        return new Message(K_ASCII, diff, (byte)1);
    }


    public static Message getSpotlightMessage(byte intensity)
    {
        return new Message(S_ASCII, intensity, (byte)1);
    }

    public static Message getUpdateMessage()
    {
        return new Message(U_ASCII, (byte)1, (byte)1);
    }

    public static Message getBatteryMessage()
    {
        return new Message(V_ASCII, (byte)1, (byte)1);
    }

    public static Message getOffMessage()
    {
        return new Message(O_ASCII, (byte)1, (byte)1);
    }

    public static Message getErrorMessage(){return new Message(E_ASCII, (byte)1, (byte)1);}

    public String toString()
    {
        return message.toString();
    }

    public byte[] getBytes()
    {
        return message;
    }
    public byte getId(){return message[0];}


    private static  Message getSixteenBitDataMessage(byte id, int data)
    {
        return new Message(id, (byte)(data&0xff),(byte)((data>>8)&0xff));
    }
}
