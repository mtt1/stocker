package de.stocker.json;

import com.google.gson.annotations.SerializedName;

/**
 * The Class JsonQuoteObject contains the information received from the data
 * provider as a response to a quote request. Used for building the initial
 * stock item object in the data base model.
 * 
 * @author Matthias Rudolph
 */
public class JsonQuoteObject {
    
    @SerializedName("c") private double current;
    @SerializedName("h") private double high;
    @SerializedName("l") private double low;
    @SerializedName("o") private double open;
    @SerializedName("pc") private double previousClose;
    // time in epoch seconds
    @SerializedName("t") private long time;

    /**
     * Gets the current price.
     *
     * @return the current price
     */
    public double getCurrent() {
        return current;
    }
    
    /**
     * Gets the high price of the day.
     *
     * @return the high price of the day
     */
    public double getHigh() {
        return high;
    }
    
    /**
     * Gets the low price of the day.
     *
     * @return the low price of the day
     */
    public double getLow() {
        return low;
    }
    
    /**
     * Gets the open price of the day.
     *
     * @return the open price of the day
     */
    public double getOpen() {
        return open;
    }
    
    /**
     * Gets the close price of the previous day
     *
     * @return the previous close price 
     */
    public double getPreviousClose() {
        return previousClose;
    }
    
    /**
     * Gets the time stamp of the request in epoch seconds
     *
     * @return the time stamp
     */
    public long getTime() {
        return time;
    }
    
}
