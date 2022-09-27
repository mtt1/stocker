package de.stocker.common;

import java.awt.Color;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import de.stocker.model.dataWrappers.*;

/**
 * The Interface IStockItem is implemented by the stock item objects in the data
 * base model and offers the methods to access all relevant data of the stock.
 * 
 * @author Matthias Rudolph
 */
public interface IStockItem {
    
    /**
     * Gets the stock id of this stock item instance.
     *
     * @return the stock id
     */
    String getStockId();
    
    /**
     * Gets the display symbol of this stock item instance.
     *
     * @return the display symbol
     */
    String getDisplaySymbol();
    
    /**
     * Gets the description of this stock item instance.
     *
     * @return the description
     */
    String getDescription();
    
    /**
     * Gets the cur price of this stock item instance.
     *
     * @return the cur price
     */
    double getCurPrice();
    
    /**
     * Gets the percentage change vs. the opening price of the day.
     *
     * @return the percentage change
     */
    double getChange();
    
    /**
     * Gets the old current price (last price before the current price).
     *
     * @return the old current price
     */
    double getCurPriceOld();
    
    /**
     * Checks if stock item is loading data from the data provider.
     *
     * @return true, if stock item is loading data
     */
    boolean isLoading();
    
    /**
     * Sets the loading status.
     *
     * @param loading the new loading status
     */
    void setLoading(boolean loading);
    
    /**
     * Checks if stock item is available at the active data provider.
     *
     * @return true, if stock item is available
     */
    boolean isAvailable();
    
    /**
     * Sets the availability status.
     *
     * @param available the new availability status
     */
    void setAvailable(boolean available);
    
    /**
     * Adds a stock listener which gets notified when the stock data is updated
     *
     * @param l the stock listener to be added
     */
    void addStockListener(IStockListener l);
    
    /**
     * Removes a stock listener.
     *
     * @param l the stock listener to be removed
     */
    void removeStockListener(IStockListener l);
    
    /**
     * Notifies all stock listeners of an update in the stock data
     */
    void notifyStockListeners();

    /**
     * Updates the stock item from push data. Used to update the current price,
     * the time stamp and the candle data from an array of times-sales data
     *
     * @param price the new current price
     * @param instant the current instant
     * @param pushData the push data array containing times-sales data
     */
    void updateStockFromPushData(double price, Instant instant, TradeDataUnit[] pushData);
    
    /**
     * Updates the stock item from push data. Used to update the current price,
     * the time stamp and the candle data from a single times-sales data unit
     *
     * @param price the new current price
     * @param instant the current instant
     * @param pushData the push data containing a single times-sales data unit
     */
    void updateStockFromPushData(double price, Instant instant, TradeDataUnit pushData);

    /**
     * Checks if the stock item has the candle data for the specified
     * resolution, default amount.
     *
     * @param chartResolution the chart resolution
     * @return true, if stock item has the candle data
     */
    boolean hasCandles(EChartResolution chartResolution);
    
    /**
     * Checks if the stock item has the specified amount of candle data for the
     * specified resolution.
     *
     * @param chartResolution the candle resolution
     * @param amount the amount of candles
     * @return true, if stock item has the candle data
     */
    boolean hasCandles(EChartResolution chartResolution, int amount);
    
    /**
     * Stores a list of candle data for the specified resolution.
     *
     * @param chartResolution the chart resolution
     * @param candles the list of candles
     */
    void putCandleData(EChartResolution chartResolution, List<ChartCandle> candles);
    
    /**
     * Gets a list of chart candles containing the candle data for a specified
     * resolution, default amount.
     *
     * @param chartResolution the chart resolution
     * @return the list of candles
     */
    List<ChartCandle> getCandles(EChartResolution chartResolution);
    
    /**
     * Gets a list of a specified amount of chart candles containing the candle
     * data for a specified resolution.
     *
     * @param candleResolution the chart resolution
     * @param amount the amount of candles
     * @return the list of candles
     */
    List<ChartCandle> getCandles(EChartResolution candleResolution, int amount);
    
    /**
     * Gets an array of candle data.
     *
     * @param chartResolution the chart resolution
     * @return the candle array
     */
    ChartCandle[] getCandleArray(EChartResolution chartResolution);

    /**
     * Gets a Bollinger Band object for this stock item for the specified
     * parameters.
     *
     * @param chartResolution the chart resolution
     * @param f the standard deviation factor f
     * @param n n determines the period of the moving average used to calculate
     * the Bollinger Bands
     * @return the Bollinger Band object
     */
    BollingerBand getBollingerBand(EChartResolution chartResolution, double f, int n);
    
    /**
     * Gets a Simple Moving Average object for this stock item for the specified parameters.
     *
     * @param chartResolution the chart resolution
     * @param n determines the period for which to calculate the moving average
     * @return the Simple Moving Average object
     */
    SimpleMovingAverage getMovingAvg(EChartResolution chartResolution, int n);

    /**
     * Adds an alarm at the specified threshold.
     *
     * @param threshold the threshold
     */
    void addAlarm(double threshold);
    
    /**
     * Adds an alarm alarm at the specified threshold with the specified color
     * used to draw the alarm on the charts.
     *
     * @param threshold the threshold
     * @param color the color
     */
    void addAlarm(double threshold, Color color);
    
    /**
     * Gets an array of all alarm thresholds for this stock item.
     *
     * @return array of all alarm thresholds
     */
    double[] getAlarms();
    
    /**
     * Gets a set of all alarm unit objects for this stock item.
     *
     * @return the set of alarm units
     */
    Set<AlarmUnit> getAlarmUnits();
    
    /**
     * Removes the alarm at the specified threshold.
     *
     * @param threshold the threshold
     */
    void removeAlarm(double threshold);
    
    /**
     * Clears all alarms for this stock item.
     */
    void clearAlarms();

}
