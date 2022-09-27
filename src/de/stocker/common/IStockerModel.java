package de.stocker.common;

import java.awt.Color;
import java.util.List;
import java.util.Set;

import de.stocker.model.*;
import de.stocker.model.dataWrappers.AlarmUnit;
import de.stocker.model.dataWrappers.ChartCandle;

/**
 * The Interface IStockerModel details all public data base methods.
 * 
 * @author Matthias Rudolph
 */
public interface IStockerModel {
    
    /**
     * Adds the stock with the corresponding id to the data base.
     *
     * @param stockId the stock id
     */
    void addStock(String stockId);
    
    /**
     * Gets the stock item corresponding to the stock id.
     *
     * @param stockId the stock id
     * @return the stock item
     */
    IStockItem getStock(String stockId);
    
    /**
     * Trigger data generation for a certain stock and resolution from the
     * outside (e. g. from a chart when resolution is changed by the user).
     *
     * @param stockId the stock id
     * @param chartResolution the chart resolution
     */
    void triggerDataGeneration(String stockId, EChartResolution chartResolution);

    /**
     * Resets the model to an empty state.
     */
    void resetModel();
    
    /**
     * Adds an alarm for a specified stock and threshold.
     *
     * @param stockId the stock id
     * @param threshold the threshold
     */
    void addAlarm(String stockId, double threshold);
    
    /**
     * Adds an alarm for a specified stock and threshold, also specifying the color.
     *
     * @param stockId the stock id
     * @param threshold the threshold
     * @param color the color
     */
    void addAlarm(String stockId, double threshold, Color color);
    
    /**
     * Gets all alarm thresholds for a specified stock.
     *
     * @param stockId the stock id
     * @return an array of all alarm thresholds for this stock
     */
    double[] getAlarms(String stockId);
    
    /**
     * Gets all alarm units, containing thresholds, colors etc., for all stocks.
     *
     * @return the alarm units
     */
    Set<AlarmUnit> getAllAlarmUnits();
    
    /**
     * Removes an alarm at the threshold.
     *
     * @param stockId the stock id
     * @param threshold the threshold
     */
    void removeAlarm(String stockId, double threshold);
    
    /**
     * Clears all alarms for a specified stock.
     *
     * @param stockId the stock id for which the alarms are cleared
     */
    void clearAlarms(String stockId);
    
    /**
     * Clears all alarms for all stocks.
     */
    void clearAllAlarms();
    
    /**
     * Sets the alarm listener which is notified when a new alarm is added.
     *
     * @param alarmListener the alarm listener
     */
    void setAlarmListener(IAlarmListener alarmListener);
    
    /**
     * Gets all stock ids for which alarms have been added.
     *
     * @return a set of all stock ids for which alarms have been added
     */
    Set<String> getAlarmStockIds();
    
    /**
     * Gets a list of candle data for a specified stock and resolution, default amount.
     *
     * @param stockId the stock id
     * @param chartResolution the chart resolution
     * @return the candles
     */
    List<ChartCandle> getCandles(String stockId, EChartResolution chartResolution);
    
    /**
     * Gets a list of candle data for a specified stock and resolution.
     * Requesting a specified amount of data.
     *
     * @param stockId the stock id
     * @param chartResolution the chart resolution
     * @param amount the amount
     * @return the candles
     */
    List<ChartCandle> getCandles(String stockId, EChartResolution chartResolution, int amount);
    
    /**
     * Triggers the model to request the search results for the specified string
     * from the data provider.
     *
     * @param searchString the search string
     */
    void triggerSearch(String searchString);
    
    /**
     * Clears the search results.
     */
    void clearSearchResults();
    
    /**
     * Gets the search result table model used to display the search results.
     *
     * @return the search result table model
     */
    SearchResultTableModel getSearchResultTableModel();
    
    /**
     * Adds a watchlist entry for the specified stock id.
     *
     * @param stockId the stock id
     */
    // Watchlist
    void addWatchlistEntry(String stockId);
    
    /**
     * Removes the watchlist entry for the specified stock id.
     *
     * @param stockId the stock id
     */
    void removeWatchlistEntry(String stockId);
    
    /**
     * Gets the watchlist table model used to display the watchlist entries.
     *
     * @return the watchlist table model
     */
    WatchlistTableModel getWatchlistTableModel();
    
    /**
     * Clears the watchlist.
     */
    void clearWatchlist();
    
    /**
     * Subscribes the stock with the specified id to push updates by the data provider.
     *
     * @param stockId the stock id
     */
    void subscribeStockToPushUpdates(String stockId);
    
    /**
     * Unsubscribes the stock with the specified id from push updates by the data provider.
     *
     * @param stockId the stock id
     */
    void unsubscribeStockFromPushUpdates(String stockId);
    
    /**
     * Gets the ids of all active stock.
     *
     * @return an array of all active stock ids
     */
    String[] getActiveStockIds();
    
    /**
     * Gets the ids of all stocks in the watchlist.
     *
     * @return the watchlist stock ids
     */
    String[] getWatchlistStockIds();
}
