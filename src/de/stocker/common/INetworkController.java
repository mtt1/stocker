package de.stocker.common;

import de.stocker.model.dataWrappers.DataProvider;

/**
 * The Interface INetworkController implemented by the network controller to
 * handle data providers and API requests.
 * 
 * @author Matthias Rudolph
 */
public interface INetworkController {

    /**
     * Gets the active data provider.
     *
     * @return the active data provider
     */
    DataProvider getActiveDataProvider();
    
    /**
     * Sets the active data provider.
     *
     * @param dataProvider the new active data provider
     */
    void setActiveDataProvider(DataProvider dataProvider);
    
    /**
     * Registers a subscriber for all incoming web socket push messages.
     *
     * @param pushSub the push subscriber
     */
    void registerPushSubscriber(IPushSubscriber pushSub);
    
    /**
     * Gets search results from the data provider through an API request.
     *
     * @param symbol the search string
     * @return the search results
     */
    String getSearch(String symbol);
    
    /**
     * Gets stock candle data from the data provider through an API request.
     *
     * @param symbol the stock symbol
     * @param resolution the chart resolution
     * @param from from time stamp
     * @param to to time stamp
     * @return the candle data
     */
    String getCandles(String symbol, String resolution, String from, String to);
    
    /**
     * Gets quote data from the data provider through an API request.
     *
     * @param symbol the stock symbol
     * @return the quote data
     */
    String getQuote(String symbol);
    
    /**
     * Sends a web socket message.
     *
     * @param message the message string
     */
    void sendWebSocketMessage(String message);
    
    /**
     * Called when new push data is received. Notifies all push subscribers.
     *
     * @param message the message
     */
    void newPushDataReceived(String message);
    
}
