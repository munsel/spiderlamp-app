package de.munsel.spiderlamp.bluetooth;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by munsel on 27.01.16.
 */
public class MessageQueue {

    private Queue<Message> messages;
    private BluetoothAcessor acessor;
    private boolean isRunning;


    public MessageQueue(BluetoothAcessor acessor)
    {
        this.acessor = acessor;
        messages = new LinkedList<Message>();
        isRunning = false;
    }

    public void enqueue(Message message)
    {
        messages.add(message);
    }

    public boolean isEmpty()
    {
        return messages.isEmpty();
    }

    public Message decueue()
    {
        if (!messages.isEmpty())
        {
            return messages.remove();
        }
        //stop();
        return null;
    }

    public boolean isRunning(){return isRunning;}

    public void start()
    {
        isRunning = true;
    }

    public void stop()
    {
        isRunning = false;
    }

    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        /*for (Message message : messages)
        {
            builder.append(message.toString());
        }*/
        return builder.toString();
    }
}
