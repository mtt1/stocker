package de.stocker.model;

import java.awt.Color;

import java.time.Instant;
import java.util.*;

import de.stocker.common.*;
import de.stocker.model.dataWrappers.*;

/**
 * The Class StockItem is part of the data model, storing all stock-related data
 * for a specific stock.
 * 
 * @author Matthias Rudolph
 */
public class StockItem implements IStockItem {
    
    private Set<IStockListener> stockListeners = new HashSet<IStockListener>();
    
    private final String stockId;
    private final String displaySymbol;
    private final String description;
    
    private double curPrice;
    private Instant curPriceInstant;
    private double curPriceOld;
    private Instant curPriceInstantOld;
    
    private double openPrice;
    
    private boolean loading;
    private boolean available;
    
    private final int DEFAULT_CANDLE_DRAW_AMOUNT = 30;
    
    // calculated in-object
    private double change;
    
    // Use Collections to make the map synchronized
    // Details: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/EnumMap.html
    private Map<EChartResolution, List<ChartCandle>> candleMap = Collections.synchronizedMap(new EnumMap<EChartResolution, List<ChartCandle>>(EChartResolution.class));
    
    // Alarms
    private Set<AlarmUnit> alarmUnits = new HashSet<AlarmUnit>();

    /**
     * Instantiates a new dummy stock item, used when no data is available at
     * the data provider.
     *
     * @param stockId the stock id
     * @param available the availability status
     */
    public StockItem(String stockId, boolean available) {
        this.stockId = stockId;
        this.displaySymbol = "";
        this.description = "";
        
        this.available = available;
    }
    
    /**
     * Instantiates a new stock item with the basic set of data.
     *
     * @param stockId the stock id
     * @param available the availability status
     * @param displaySymbol the display symbol
     * @param description the description
     * @param curPrice the current price
     * @param curPriceInstant the current price instant
     * @param openPrice the open price of the day
     */
    public StockItem(String stockId, boolean available, String displaySymbol, String description,
            double curPrice, Instant curPriceInstant, double openPrice) {
        this.stockId = stockId;

        this.displaySymbol = displaySymbol;
        this.description = description;
        this.curPrice = curPrice;
        this.curPriceInstant = curPriceInstant;
        this.openPrice = openPrice;
        
        this.available = available;

        calculateChange();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStockId() {
        return stockId;
    }
    
    /**
     * Updates the stock price with a new price.
     *
     * @param price the new price
     * @param instant the new instant
     */
    private void updateStockPrice(double price, Instant instant) {
        setCurPrice(price);
        setCurPriceInstant(instant);
        calculateChange();
        
        notifyStockListeners();
    }

    /**
     * Sets the current price and pushes the old price to the curPriceOld field.
     *
     * @param price the new current price
     */
    private void setCurPrice(double price) {
        curPriceOld = curPrice;
        curPrice = price;
    }

    /**
     * Sets the current price instant.
     *
     * @param instant the new current price instant
     */
    private void setCurPriceInstant(Instant instant) {
        curPriceInstantOld = curPriceInstant;
        curPriceInstant = instant;
        
    }

    /**
     * Calculates the change.
     */
    private void calculateChange() {
        change = roundChange(curPrice / openPrice - 1);
    }

    /**
     * Rounds the change.
     *
     * @param c the c
     * @return the double
     */
    private double roundChange(double c) {
        final double DECIMAL_PLACES = 1000.0; // 3
        c = c * DECIMAL_PLACES;
        c = c >= 0 ? c + 0.5 : c - 0.5;
        int intHelper = (int) c;
        c = intHelper / DECIMAL_PLACES;
        return c;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplaySymbol() {
        return displaySymbol;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getCurPrice() {
        return curPrice;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getChange() {
        return change;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public double getCurPriceOld() {
        return curPriceOld;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasCandles(EChartResolution candleResolution) {
        return hasCandles(candleResolution, DEFAULT_CANDLE_DRAW_AMOUNT);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasCandles(EChartResolution candleResolution, int amount) {
        
        // check for week and months because data providers provider not enough
        // data for these resolutions
        if (candleResolution == EChartResolution.WEEK || candleResolution == EChartResolution.MONTH) {
            return (candleMap.containsKey(candleResolution));
        } else {
            return (candleMap.containsKey(candleResolution) &&
                    candleMap.get(candleResolution).size() >= amount);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ChartCandle> getCandles(EChartResolution candleResolution) {
        return getCandles(candleResolution, DEFAULT_CANDLE_DRAW_AMOUNT);
    }
        
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<ChartCandle> getCandles(EChartResolution candleResolution, int amount) {
        if (!candleMap.containsKey(candleResolution)) {
            return null;
        }
        List<ChartCandle> candles = candleMap.get(candleResolution);
        if (candles.size() >= amount) {
            return candles.subList(candles.size() - amount, candles.size() - 1);
        } else {
            return candles;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void putCandleData(EChartResolution candleResolution, List<ChartCandle> candles) {
        candleMap.put(candleResolution, candles);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ChartCandle[] getCandleArray(EChartResolution chartResolution) {
        ChartCandle[] candleArray = new ChartCandle[0];
        
        List<ChartCandle> candles = candleMap.get(chartResolution);
        
        if (chartResolution == EChartResolution.WEEK && candles != null) {
            if (candles.size() >= DEFAULT_CANDLE_DRAW_AMOUNT) {
                List<ChartCandle> candleSublist = candles.subList(candles.size() - DEFAULT_CANDLE_DRAW_AMOUNT, candles.size());
                candleArray = candleSublist.toArray(new ChartCandle[0]);
            } else {
                candleArray = candles.toArray(new ChartCandle[0]);
            }
        } else if (chartResolution == EChartResolution.MONTH && candles != null) {
            candleArray = candles.toArray(new ChartCandle[0]);
        } else if (candles != null && candles.size() >= DEFAULT_CANDLE_DRAW_AMOUNT) {
            List<ChartCandle> candleSublist = candles.subList(candles.size() - DEFAULT_CANDLE_DRAW_AMOUNT, candles.size());
            candleArray = candleSublist.toArray(new ChartCandle[0]);
        } else {
            System.err.println("Error: Can't get candle values. Not enough candle data present.");
        }
        return candleArray;
    }
    
    /**
     * Gets an array of the last close prices. This is used to gather the values
     * for the indicator calculations.
     *
     * @param chartResolution the chart resolution
     * @param amount the amount of close prices
     * @return the array of close prices
     */
    private double[] getClosePrices(EChartResolution chartResolution, int amount) {
        double[] closePrices = new double[amount];
        List<ChartCandle> candles = candleMap.get(chartResolution);
        if (candles != null && candles.size() >= amount) {
            for (int i = 0; i < amount; i++) {
                closePrices[i] = candles.get(candles.size() - amount + i).getPriceClose();
            }
        } else if (isLoading()) {
            try {
                Thread.sleep(100);
                return getClosePrices(chartResolution, amount);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Error: Can't get close prices. Not enough candle data present.");
        }
        return closePrices;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public BollingerBand getBollingerBand(EChartResolution chartResolution, double f, int n) {
        double[] closePrices = getClosePrices(chartResolution, DEFAULT_CANDLE_DRAW_AMOUNT + n);
        double[] lower = StockCalcHelper.getLowerBollingerBand(f, n, closePrices);
        double[] upper = StockCalcHelper.getUpperBollingerBand(f, n, closePrices);
        double[] movingAvg = StockCalcHelper.getMovingAverage(n, closePrices);
        return new BollingerBand(f, n, movingAvg, upper, lower);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public SimpleMovingAverage getMovingAvg(EChartResolution chartResolution, int n) {
        double[] closePrices = getClosePrices(chartResolution, DEFAULT_CANDLE_DRAW_AMOUNT + n);
        double[] movingAvg = StockCalcHelper.getMovingAverage(n, closePrices);
        return new SimpleMovingAverage(n, movingAvg);
    }
    
    /**
     * Updates candles from an array of push data.
     *
     * @param tradeData the array of trade data
     */
    private void updateCandlesFromPushData(TradeDataUnit[] tradeData) {
        for (EChartResolution res : EChartResolution.values()) {
            if (candleMap.containsKey(res)) {
                List<ChartCandle> updatedCandles = StockCalcHelper.updateCandlesFromPushData(candleMap.get(res), tradeData, res);
                candleMap.put(res, updatedCandles);
            }
        }
        notifyStockListeners();
    }
    
    /**
     * Updates candles from a single new trade data point.
     *
     * @param tradeData the trade data point
     */
    private void updateCandlesFromPushData(TradeDataUnit tradeData) {
        for (EChartResolution res : EChartResolution.values()) {
            if (candleMap.containsKey(res)) {
                List<ChartCandle> updatedCandles = StockCalcHelper.updateCandlesFromPushData(candleMap.get(res), tradeData, res);
                candleMap.put(res, updatedCandles);
            }
        }
        notifyStockListeners();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void updateStockFromPushData(double price, Instant instant, TradeDataUnit[] pushData) {
        updateStockPrice(price, instant);
        updateCandlesFromPushData(pushData);
        notifyStockListeners();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateStockFromPushData(double price, Instant instant, TradeDataUnit pushData) {
        updateStockPrice(price, instant);
        updateCandlesFromPushData(pushData);
        notifyStockListeners();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addStockListener(IStockListener l) {
        stockListeners.add(l);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeStockListener(IStockListener l) {
        stockListeners.remove(l);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyStockListeners() {
        for (IStockListener l : stockListeners) {
            l.stockDataUpdated(this);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addAlarm(double threshold) {
        addAlarm(threshold, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addAlarm(double threshold, Color color) {
        for (Double d : getAlarms()) {
            if (d == threshold) {
                return;
            }
        }

        EAlarmPos position;
        if (curPrice < threshold) {
            position = EAlarmPos.ALARM_ADDED_WHEN_PRICE_BELOW_THRESHOLD;
        } else if (curPrice > threshold) {
            position = EAlarmPos.ALARM_ADDED_WHEN_PRICE_ABOVE_THRESHOLD;
        } else {
            return;
        }
        alarmUnits.add(new AlarmUnit(this.stockId, threshold, position, color));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public double[] getAlarms() {
        AlarmUnit[] objectArray = alarmUnits.toArray(new AlarmUnit[0]);
        double[] primitiveArray = new double[objectArray.length];
        for (int i = 0; i < objectArray.length; i++) {
            primitiveArray[i] = objectArray[i].getThreshold();
        }
        return primitiveArray;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Set<AlarmUnit> getAlarmUnits() {
        return alarmUnits;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAlarm(double threshold) {
        alarmUnits.removeIf(alarmUnit -> (alarmUnit.getThreshold() == threshold));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void clearAlarms() {
        alarmUnits.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLoading() {
        return loading;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAvailable() {
        return available;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAvailable(boolean available) {
        this.available = available;
    }

}
