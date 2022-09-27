package de.stocker.model.dataWrappers;

import java.time.Instant;

/**
 * The Class ChartCandle is a wrapper class containing all data for a candle
 * object. The data format returned by the data provider is translated into
 * candle objects by the model for easier manipulation and display.
 * 
 * @author Matthias Rudolph
 */
public class ChartCandle {
    
    // time in epoch milliseconds to be compatible with the millisecond
    // time stamps of the push data
    private long timeOpen;
    private Instant timeInstant;
    private double priceLow;
    private double priceHigh;
    private double priceOpen;
    private double priceClose;
    private double volume;
    
    /**
     * Instantiates a new chart candle object.
     *
     * @param timeOpen the opening time of the current candle interval
     * @param timeInstant the opening time instant as a Java Instant object
     * @param priceLow the low price of the current candle
     * @param priceHigh the high price of the current candle
     * @param priceOpen the opening price of the current candle
     * @param priceClose the close price of the current candle
     * @param volume the trade volume of the current candle
     */
    public ChartCandle(long timeOpen, Instant timeInstant, double priceLow, double priceHigh, double priceOpen,
            double priceClose, double volume) {
        this.timeOpen = timeOpen;
        this.timeInstant = timeInstant;
        this.priceLow = priceLow;
        this.priceHigh = priceHigh;
        this.priceOpen = priceOpen;
        this.priceClose = priceClose;
        this.volume = volume;
    }
    
    /**
     * Gets the opening time in epoch milliseconds format.
     *
     * @return the opening time
     */
    public long getTimeOpen() {
        return timeOpen;
    }
    
    /**
     * Gets the opening time as a Java instant object used for conveniently
     * displaying the time stamp
     *
     * @return the opening time Instant
     */
    public Instant getTimeInstant() {
        return timeInstant;
    }
    
    /**
     * Gets the low price of the current candle.
     *
     * @return the low price
     */
    public double getPriceLow() {
        return priceLow;
    }
    
    /**
     * Gets the high price of the current candle.
     *
     * @return the high price
     */
    public double getPriceHigh() {
        return priceHigh;
    }
    
    /**
     * Gets the open price of the current candle.
     *
     * @return the open price 
     */
    public double getPriceOpen() {
        return priceOpen;
    }
    
    /**
     * Gets the close price of the current candle.
     *
     * @return the close price
     */
    public double getPriceClose() {
        return priceClose;
    }
    
    /**
     * Gets the trade volume of the current candle.
     *
     * @return the trade volume
     */
    public double getVolume() {
        return volume;
    }
    
}
