package de.stocker.model.dataWrappers;

import com.google.gson.annotations.SerializedName;

/**
 * The Class TradeDataUnit is a data wrapper class used to process the updated
 * stock information coming in through a push update from the data provider.
 */
public class TradeDataUnit {
    
    @SerializedName("c") private String[] condition;
    @SerializedName("p") private double price;
    @SerializedName("s") private String stockId;
    //timestamp in milliseconds
    @SerializedName("t") private long time;
    @SerializedName("v") private double volume;

    /**
     * Gets the id of the stock for which this trade data unit contains a data
     * update.
     *
     * @return the stock id
     */
    public String getStockId() {
        return stockId;
    }
    
    /**
     * Gets the updated price.
     *
     * @return the price
     */
    public double getPrice() {
        return price;
    }
    
    /**
     * Gets the time of the update, in epoch milliseconds.
     *
     * @return the time
     */
    public long getTime() {
        return time;
    }

    /**
     * Gets the trade volume since the last push update.
     *
     * @return the volume
     */
    public double getVolume() {
        return volume;
    }
}
