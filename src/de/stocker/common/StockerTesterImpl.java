package de.stocker.common;

import java.util.Set;

import de.stocker.model.StockCalcHelper;
import de.stocker.model.StockerModel;
import stocker.IStockerTester;

public class StockerTesterImpl implements IStockerTester {
    
    private IStockerModel stockerModel;
    
    public StockerTesterImpl() {
        stockerModel = new StockerModel(null, null);
    }
    
    @Override
    public String getMatrNr() {
        return "3266494";
    }

    @Override
    public String getName() {
        return "Matthias Rudolph";
    }

    @Override
    public String getEmail() {
        return "mttrud@gmail.com";
    }

    @Override
    public void clearWatchlist() {
        stockerModel.clearWatchlist();
    }

    @Override
    public void addWatchlistEntry(String stockId) {
        stockerModel.addWatchlistEntry(stockId);
    }

    @Override
    public void removeWatchlistEntry(String stockId) {
        stockerModel.removeWatchlistEntry(stockId);
    }

    @Override
    public String[] getWatchlistStockIds() {
        return stockerModel.getWatchlistStockIds();
    }

    @Override
    public void clearAlarms(String stockId) {
        stockerModel.clearAlarms(stockId);
    }

    @Override
    public void clearAllAlarms() {
        stockerModel.clearAllAlarms();;
    }

    @Override
    public void addAlarm(String stockId, double threshold) {
        stockerModel.addAlarm(stockId, threshold);
    }

    @Override
    public void removeAlarm(String stockId, double threshold) {
        stockerModel.removeAlarm(stockId, threshold);
    }

    @Override
    public double[] getAlarms(String stockId) {
        return stockerModel.getAlarms(stockId);
    }

    @Override
    public double[] getMovingAverage(int n, double[] stockData) {
        return StockCalcHelper.getMovingAverage(n, stockData);
    }

    @Override
    public double[] getUpperBollingerBand(double f, int n, double[] stockData) {
        return StockCalcHelper.getUpperBollingerBand(f, n, stockData);
    }

    @Override
    public double[] getLowerBollingerBand(double f, int n, double[] stockData) {
        return StockCalcHelper.getLowerBollingerBand(f, n, stockData);
    }

    @Override
    public Set<String> getAlarmStockIds() {
        return stockerModel.getAlarmStockIds();
    }
    
}