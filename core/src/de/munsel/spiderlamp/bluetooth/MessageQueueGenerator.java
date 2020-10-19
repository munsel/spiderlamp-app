package de.munsel.spiderlamp.bluetooth;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import de.munsel.spiderlamp.LampDataManagement.LampData;
import de.munsel.spiderlamp.LampDataManagement.LampDataManager;

/**
 * Created by munsel on 31.07.16.
 */
public class MessageQueueGenerator {

    private static final Color outlineHelix = Color.valueOf("0a09aa");
    private static final Color adenine = Color.valueOf("0a09aa");
    private static final Color thymine = Color.valueOf("0a09aa");
    private static final Color guanine = Color.valueOf("0a09aa");
    private static final Color cytosine = Color.valueOf("0a09aa");

    private static float stepSize = 1;
    private static float nucleoidPairsPerPeriod = 10;

    private static float periodLengthOverRadius = 2.8f;

    //gets values in cm and sputs out the DNA helix into the queue
    public static void addDNATrail(MessageQueue queue, float height,
                                   int startX, int startY, int startZ, int radius){
        int n = (int)(height/stepSize);
        float periodLength = radius * periodLengthOverRadius;
        LampData lampData = new LampData();
        lampData.setLampCoordiantes(startX, startY, startZ);
        enqueueRGBMessage(queue, Color.BLACK);//turns LED off
        enqueueRopeMessages(queue, lampData);
        queue.enqueue(Message.getUpdateMessage());
        //first outline
        for( int i = 0; i<=n; i++){
            float zOverPeriodLength = i*stepSize/periodLength;
            lampData.setLampCoordiantes(
                    startX+(int)(MathUtils.cos(zOverPeriodLength)*radius),
                    startY+(int)(MathUtils.sin(zOverPeriodLength)*radius),
                    startZ+(int)(i*stepSize));
            enqueueRopeMessages(queue, lampData);
            enqueueRGBMessage(queue, outlineHelix);
            queue.enqueue(Message.getUpdateMessage());
        }
        enqueueRGBMessage(queue, Color.BLACK);//turns LED off
        lampData.setLampCoordiantes(
                startX-(int)(MathUtils.cos(height/periodLength)*radius),
                startY-(int)(MathUtils.sin(height/periodLength)*radius),
                startZ+(int)(height));
        queue.enqueue(Message.getUpdateMessage());
        //second outline
        for( int i = n; i>=0; i--){
            float zOverPeriodLength = i*stepSize/periodLength;
            lampData.setLampCoordiantes(
                    startX-(int)(MathUtils.cos(zOverPeriodLength)*radius),
                    startY-(int)(MathUtils.sin(zOverPeriodLength)*radius),
                    startZ+(int)(i*stepSize));
            enqueueRopeMessages(queue, lampData);
            enqueueRGBMessage(queue, outlineHelix);
            queue.enqueue(Message.getUpdateMessage());
        }
        enqueueRGBMessage(queue, Color.BLACK);//turns LED off
        queue.enqueue(Message.getUpdateMessage());
    }

    public static void enqueueRGBMessage(MessageQueue queue, Color color)
    {
        queue.enqueue(Message.getRedMessage(
                (byte)((int)(color.r*255)&0xFF)));
        queue.enqueue(Message.getGreenMessage(
                (byte) ((int) (color.g * 255) & 0xFF)));
        queue.enqueue(Message.getBlueMessage(
                (byte) ((int) (color.b * 255) & 0xFF)));
    }


    public static void enqueueRopeMessages(MessageQueue queue, LampData lampData){
        queue.enqueue(Message.getSetValueM1AMessage(lampData.getRopeL1()));
        queue.enqueue(Message.getSetValueM1BMessage(lampData.getRopeL2()));
        queue.enqueue(Message.getSetValueM2AMessage(lampData.getRopeL3()));
        queue.enqueue(Message.getSetValueM2BMessage(lampData.getRopeL4()));
    }
}