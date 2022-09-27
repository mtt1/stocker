package de.stocker.json;

import com.google.gson.annotations.SerializedName;

/**
 * The Class JsonHistCandle contains the arrays of historical candle data
 * received from the data provider. Close, high, low and open prices, time
 * stamps and volume information all stored in their respective arrays. To be
 * translated into individual ChartCandle objects in the model.
 * 
 * @author Matthias Rudolph
 */
public class JsonHistCandle {
    
    @SerializedName("c") private double[] close;
    @SerializedName("h") private double[] high;
    @SerializedName("l") private double[] low;
    @SerializedName("o") private double[] open;
    @SerializedName("s") private String status;
    // time in epoch seconds
    @SerializedName("t") private long[] time;
    @SerializedName("v") private double[] volume;
    
    /**
     * Gets the array of close prices.
     *
     * @return the array of close prices
     */
    public double[] getClose() {
        return close;
    }

    /**
     * Gets the array of high prices.
     *
     * @return the array of high prices
     */
    public double[] getHigh() {
        return high;
    }

    /**
     * Gets the array of low prices.
     *
     * @return the array of low prices
     */
    public double[] getLow() {
        return low;
    }

    /**
     * Gets the array of open prices.
     *
     * @return the array of open prices
     */
    public double[] getOpen() {
        return open;
    }

    /**
     * Gets the status information of the API request.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }
    /**
     * Gets the array of time stamps in epoch seconds.
     *
     * @return the array of time stamps
     */
    public long[] getTime() {
        return time;
    }

    /**
     * Gets the array of trade volume information
     *
     * @return the array of trade volume information
     */
    public double[] getVolume() {
        return volume;
    }

    /**
     * Gets the number of entries in the candle data arrays.
     *
     * @return the number of entries
     */
    public int getNumberOfEntries() {
        return time.length;
    }

}
