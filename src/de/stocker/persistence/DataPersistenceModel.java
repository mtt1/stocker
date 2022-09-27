package de.stocker.persistence;

import java.util.*;

import de.stocker.common.IStockerModel;
import de.stocker.model.dataWrappers.AlarmUnit;

/**
 * The Class DataPersistenceModel is a wrapper class for all data needed to save
 * the current state of the data base model to disk and restore it from there.
 * 
 * @author Matthias Rudolph
 */
public class DataPersistenceModel {
    
    private String[] activeStocks;
    private String[] watchlistStocks;
    private Set<AlarmUnit> alarmUnits;
    
    /**
     * Instantiates a new data persistence model with a reference to the data
     * base model to pull information from.
     *
     * @param stockerModel the stocker model
     */
    public DataPersistenceModel(IStockerModel stockerModel) {
        activeStocks = stockerModel.getActiveStockIds();
        watchlistStocks = stockerModel.getWatchlistStockIds();
        alarmUnits = stockerModel.getAllAlarmUnits();
    }
    
    /**
     * Gets the IDs of all currently active stocks.
     *
     * @return the active stock ids
     */
    public String[] getActiveStockIds() {
        return activeStocks;
    }
    
    /**
     * Gets the IDs of all stocks in the watchlist.
     *
     * @return the watchlist stock ids
     */
    public String[] getWatchlistStockIds() {
        return watchlistStocks;
    }

    /**
     * Gets a set of the alarm units saved in the model.
     *
     * @return the set of alarm units
     */
    public Set<AlarmUnit> getAlarmUnits() {
        return alarmUnits;
    }

}