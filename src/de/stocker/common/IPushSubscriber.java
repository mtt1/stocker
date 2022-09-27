package de.stocker.common;

/**
 * The Interface IPushSubscriber implemented by the class handling the incoming
 * push messages from the network.
 * 
 * @author Matthias Rudolph
 */
public interface IPushSubscriber {
    
    /**
     * Called by the network controller when new push data is received.
     *
     * @param data the push data
     */
    void newPushData(String data);

}
