package de.munsel.spiderlamp.bluetooth;



/**
 * Created by munsel on 27.01.16.
 */
public class Message {

    private static final byte LF_ASCII = 0x0A;
    public static final byte UPDATE_ID = 0x05;
    public static final byte BATTERY_ID = (byte)6;


    private byte[] message;

    public Message(byte id, byte data1, byte data2)
    {
        message = new byte[4];
        message[0] = id;
        message[1] = data1;
        message[2] = data2;
        message[3] = LF_ASCII;
    }




    public static Message getRedMessage(byte data) {return new Message((byte)1, data, (byte)1);}
    public static Message getGreenMessage(byte data){return new Message((byte)2, data, (byte)1);}
    public static Message getBlueMessage(byte data){return new Message((byte)3, data, (byte)1);}
    public static Message getSpotlightMessage(byte intensity){return new Message((byte)4, intensity, (byte)1);}
    public static Message getUpdateMessage()
    {
        return new Message((byte)5, (byte)1, (byte)1);
    }
    public static Message getBatteryMessage()
    {
        return new Message((byte)6, (byte)1, (byte)1);
    }
    public static Message getSetValueM1AMessage(int value){return getSixteenBitDataMessage((byte)7, value);}
    public static Message getSetValueM1BMessage(int value){return getSixteenBitDataMessage((byte)8, value);}
    public static Message getSetValueM2AMessage(int value){return getSixteenBitDataMessage((byte)9, value);}
    public static Message getSetValueM2BMessage(int value){return getSixteenBitDataMessage((byte)10, value);}
    public static Message getCurrentValueM1AMessage(int value){return getSixteenBitDataMessage((byte)11, value);}
    public static Message getCurrentValueM1BMessage(int value){return getSixteenBitDataMessage((byte)12, value);}
    public static Message getCurrentValueM2AMessage(int value){return getSixteenBitDataMessage((byte)13, value);}
    public static Message getCurrentValueM2BMessage(int value){return getSixteenBitDataMessage((byte)14, value);}
    public static Message getOffMessage() {return new Message((byte)15, (byte)1, (byte)1);}


    //not actually for communicatoin, but this will let the transmitter wait some time
    public static Message getWaitMessage() {return new Message((byte)16, (byte)1, (byte)1);}



    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append((char)message[0]);
        builder.append(" ");
        builder.append((int)message[1]);
        builder.append(" ");
        builder.append((int)message[2]);
        builder.append((char)message[3]);
        return builder.toString();
    }

    public byte[] getBytes()
    {
        return message;
    }
    public byte getId(){return message[0];}


    private static  Message getSixteenBitDataMessage(byte id, int data)
    {
        return new Message(id, (byte)((data>>8)&0xff),(byte)(data&0xff));
    }
}
