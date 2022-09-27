package de.stocker.model;

import java.awt.Color;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonObject;

import de.stocker.common.*;
import de.stocker.json.*;
import de.stocker.model.dataWrappers.*;
import de.stocker.preferences.PreferencesModel;

/**
 * The Class StockerModel is the central data base model for the Stocker
 * application. It handles the data coming from the data provider through the
 * network, organizes the fetching of data and the different stock items making
 * up the data base.
 * 
 * @author Matthias Rudolph
 */
public class StockerModel implements IStockerModel, IPushSubscriber {
    
    private INetworkController networkController;
    private PreferencesModel preferencesModel;
    private IAlarmListener alarmListener;

    private List<IStockItem> activeStocks = new ArrayList<IStockItem>();
    
    // Watchlist
    private List<IStockItem> watchlist = new ArrayList<IStockItem>();
    private WatchlistTableModel watchlistTableModel = new WatchlistTableModel(watchlist);
    
    // Search
    private List<SearchResult> searchResults = new ArrayList<SearchResult>();
    private SearchResultTableModel searchResultTableModel = new SearchResultTableModel(searchResults);
    
    private final int DEFAULT_CANDLE_DRAW_AMOUNT = 30;
    // FOR INDICATORS
    private final int DEFAULT_CANDLE_BACKLOG_AMOUNT = 200;
    
    /**
     * Instantiates a new stocker model with references to the network and the
     * preferences.
     *
     * @param networkController the network controller
     * @param preferencesModel the preferences model
     */
    public StockerModel(INetworkController networkController, PreferencesModel preferencesModel) {
        this.networkController = networkController;
        if (networkController != null) {
            networkController.registerPushSubscriber(this);
        }
        this.preferencesModel = preferencesModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void newPushData(String data) {
        JsonTradeObject jsonTrade = JsonFactory.jsonToObject(data, JsonTradeObject.class);
        if (jsonTrade.getType().equals("trade")) {
            TradeDataUnit[] tradeData = jsonTrade.getDataArray();
            for (int i = 0; i < tradeData.length; i++) {
                updateStockFromPushData(tradeData[i].getStockId(), tradeData[i].getPrice(), Instant.ofEpochMilli(tradeData[i].getTime()), tradeData[i]);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addStock(String stockId) {
        if (!isDuplicate(activeStocks, stockId)) {
            generateStockItem(stockId);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addWatchlistEntry(String stockId) {
        IStockItem stockItem = getStock(stockId);
        
        if (!isDuplicate(watchlist, stockId)) {
            watchlist.add(stockItem);
        }
        
        watchlistTableModel.fireTableDataChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeWatchlistEntry(String stockId) {
        watchlist.removeIf(wle -> wle.getStockId().equals(stockId));
        watchlistTableModel.fireTableDataChanged();
    }

    /**
     * Checks if the stock is a duplicate in a specific list.
     *
     * @param list the list to check
     * @param stockId the stock id to check
     * @return true, if stock is duplicate
     */
    private boolean isDuplicate(List<IStockItem> list, String stockId) {
        if (!list.isEmpty()) {
            for (IStockItem stock : list) {
                if (stock.getStockId().equals(stockId)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IStockItem getStock(String stockId) {
        if (!isDuplicate(activeStocks, stockId)) {
            generateStockItem(stockId);
        }

        for (IStockItem stockItem : activeStocks) {
            if (stockItem.getStockId().equals(stockId)) {
                return stockItem;
            }
        }

        System.err.println("Error: No stock data available.");
        return null;
    }

    /**
     * Generates and triggers the data for a stock item.
     *
     * @param stockId the stock id
     */
    private void generateStockItem(String stockId) {
        IStockItem stockItem = fetchStockItem(stockId);

        addToActiveStocks(stockItem);

        // Fetch data for default resolution
        if (preferencesModel != null) {
            triggerDataGeneration(stockId, preferencesModel.getChartResolution());
        }
    }

    /**
     * Fetches a stock item from the network, storing the basic information alongside it.
     *
     * @param stockId the stock id
     * @return the stock item
     */
    private IStockItem fetchStockItem(String stockId) {
        if (networkController != null) {
        String requestResult = networkController.getSearch(stockId);
        JsonSearchObject searchObject = JsonFactory.jsonToObject(requestResult, JsonSearchObject.class);
        JsonSearchResult searchResult = searchObject.getMatchingResult(stockId);

        if (searchResult != null) {
            String description = searchResult.getDescription();
            String displaySymbol = searchResult.getDisplaySymbol();

            String quoteString = networkController.getQuote(stockId);
            if (quoteString != null) {
                JsonQuoteObject quoteObject = JsonFactory.jsonToObject(quoteString, JsonQuoteObject.class);

                double curPrice = quoteObject.getCurrent();
                Instant curPriceInstant = Instant.ofEpochSecond(quoteObject.getTime());
                double openPrice = quoteObject.getOpen();

                return new StockItem(stockId, true, displaySymbol, description, curPrice, curPriceInstant, openPrice);
            } else {
        // catching the situations where fetching the stock item data fails and
        // generates an not-available dummy object then
                return new StockItem(stockId, false);
            }
        } else {
            return new StockItem(stockId, false);
        }
        } else {
            return new StockItem(stockId, false);
        }
    }

    /**
     * Adds a stock to the active stocks and subscribes it to the push updates
     * if it is available.
     *
     * @param stockItem the stock item
     */
    private void addToActiveStocks(IStockItem stockItem) {
        activeStocks.add(stockItem);
        if (stockItem.isAvailable()) {
            subscribeStockToPushUpdates(stockItem.getStockId());
        }
    }

    /**
     * Updates a stock item from push data.
     *
     * @param stockId the stock id
     * @param price the new price
     * @param instant the new instant
     * @param pushData the trade data point
     */
    private void updateStockFromPushData(String stockId, double price, Instant instant, TradeDataUnit pushData) {
        IStockItem stockItem = getStock(stockId);
        stockItem.updateStockFromPushData(price, instant, pushData);

        if (watchlist.contains(stockItem)) {
            int index = watchlistTableModel.getWatchlistEntryIndex(stockItem);
            watchlistTableModel.fireTableRowsUpdated(index, index);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void triggerSearch(String searchString) {
        clearSearchResults();
        
        String requestResult = networkController.getSearch(searchString);
        JsonSearchObject searchObject = JsonFactory.jsonToObject(requestResult, JsonSearchObject.class);
        JsonSearchResult[] jsonResults = searchObject.getResult();
        
        for (JsonSearchResult jsonResult : jsonResults) {
            searchResults.add(new SearchResult(jsonResult.getSymbol(), jsonResult.getDescription(), jsonResult.getDisplaySymbol()));
        }
        
        searchResultTableModel.fireTableDataChanged();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void clearSearchResults() {
        searchResults.clear();
        searchResultTableModel.fireTableDataChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResultTableModel getSearchResultTableModel() {
        return searchResultTableModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WatchlistTableModel getWatchlistTableModel() {
        return watchlistTableModel;
    }

    /**
     * Fetch candle data from the network. The method works by generating an
     * "earlier" time stamp to request data from that instant to now, checking if
     * it gets enough data in return or else repeating the request with an even
     * earlier time stamp.
     *
     * @param stockId the stock id
     * @param chartResolution the chart resolution
     */
    private void fetchCandleData(String stockId, EChartResolution chartResolution) {
        IStockItem stockItem = getStock(stockId);
        stockItem.setLoading(true);
        
        Instant now = Instant.now();
        String nowString = String.valueOf(now.getEpochSecond());
        
        JsonHistCandle histCandleArray = null;
        int numberOfEntries = 0;
        
        String earlierString = "";
        
        long timeDiff = 0;
        switch (chartResolution) {
        case ONE:
            timeDiff = TimeUnit.MINUTES.toSeconds(1);
            break;
        case FIVE:
            timeDiff = TimeUnit.MINUTES.toSeconds(5);
            break;
        case FIFTEEN:
            timeDiff = TimeUnit.MINUTES.toSeconds(15);
            break;
        case THIRTY:
            timeDiff = TimeUnit.MINUTES.toSeconds(30);
            break;
        case SIXTY:
            timeDiff = TimeUnit.HOURS.toSeconds(1);
            break;
        case DAY:
            timeDiff = TimeUnit.DAYS.toSeconds(1);
            break;
        case WEEK:
            timeDiff = TimeUnit.DAYS.toSeconds(7);
            break;
        case MONTH:
            timeDiff = TimeUnit.DAYS.toSeconds(31);
            break;
        default:
            break;
        }
        
        // arbitrary number of cycles to make sure there is enough data in the database to calculate all indicators
        // doesn't work for months and weeks on the free version of finnhub as data is limited to one year
        for (int i = 1; i <= 10; i++) {
            long defaultTimeDiffIteration = (DEFAULT_CANDLE_BACKLOG_AMOUNT + 5 * i * DEFAULT_CANDLE_DRAW_AMOUNT) * timeDiff;

            switch (chartResolution) {
            case ONE:
            case FIVE:
            case FIFTEEN:
            case THIRTY:
            case SIXTY:
            case DAY:
                earlierString = String.valueOf(Long.valueOf(nowString) - defaultTimeDiffIteration);
                break;
            case WEEK:
                earlierString = String.valueOf(Long.valueOf(nowString) - (i * DEFAULT_CANDLE_DRAW_AMOUNT * timeDiff));
                break;
            case MONTH:
                earlierString = String.valueOf(Long.valueOf(nowString) - (i * 12 * timeDiff));
                break;
            default:
                break;
            }

            String jsonString = networkController.getCandles(stockId, chartResolution.getUrlString(), earlierString,
                    nowString);
            histCandleArray = JsonFactory.jsonToObject(jsonString, JsonHistCandle.class);

            numberOfEntries = histCandleArray.getNumberOfEntries();

            // break when enough data collected
            // account for the behavior of finnhub free version, delivering only one year of data
            if (chartResolution == EChartResolution.WEEK && numberOfEntries >= 50) {
                break;
            } else if (chartResolution == EChartResolution.MONTH && numberOfEntries >= 12) {
                break;
            } else if (numberOfEntries > DEFAULT_CANDLE_BACKLOG_AMOUNT + DEFAULT_CANDLE_DRAW_AMOUNT) {
                break;
            }

        }
        
        List<ChartCandle> candles = new ArrayList<ChartCandle>();
        
        // translates the provider format to the data wrapper class
        for (int i = 0; i < numberOfEntries; i++) {
            // adjust to milliseconds to be able to merge candles from push data later
            ChartCandle candle = new ChartCandle(histCandleArray.getTime()[i] * 1000,
                    Instant.ofEpochSecond(histCandleArray.getTime()[i]),
                    histCandleArray.getLow()[i], histCandleArray.getHigh()[i],
                    histCandleArray.getOpen()[i], histCandleArray.getClose()[i], histCandleArray.getVolume()[i]);
            candles.add(candle);
        }
        
        // puts the collected data in the stock item
        stockItem.putCandleData(chartResolution, candles);
        stockItem.setLoading(false);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void triggerDataGeneration(String stockId, EChartResolution chartResolution) {
        IStockItem stockItem = getStock(stockId);
        if (stockItem.isAvailable()) {
            if (!stockItem.hasCandles(chartResolution, DEFAULT_CANDLE_BACKLOG_AMOUNT + DEFAULT_CANDLE_DRAW_AMOUNT)
                    && !stockItem.isLoading()) {
                fetchCandleData(stockId, chartResolution);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ChartCandle> getCandles(String stockId, EChartResolution candleResolution) {
        return getCandles(stockId, candleResolution, DEFAULT_CANDLE_DRAW_AMOUNT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ChartCandle> getCandles(String stockId, EChartResolution candleResolution, int amount) {
        IStockItem stockItem = getStock(stockId);
        if (!stockItem.hasCandles(candleResolution, amount)) {
            fetchCandleData(stockId, candleResolution);
        }
        return stockItem.getCandles(candleResolution, amount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void subscribeStockToPushUpdates(String stockId) {
        JsonObject sub = new JsonObject();
        sub.addProperty("type", "subscribe");
        sub.addProperty("symbol", stockId);
        
        String message = JsonFactory.objectToJson(sub);
        networkController.sendWebSocketMessage(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unsubscribeStockFromPushUpdates(String stockId) {
        JsonObject unsub = new JsonObject();
        unsub.addProperty("type", "unsubscribe");
        unsub.addProperty("symbol", stockId);
        
        String message = JsonFactory.objectToJson(unsub);
        networkController.sendWebSocketMessage(message);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getActiveStockIds() {
        List<String> stockIds = new ArrayList<String>();
        for (IStockItem s : activeStocks) {
            stockIds.add(s.getStockId());
        }
        return stockIds.toArray(new String[0]);
    }
        
    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getWatchlistStockIds() {
        List<String> stockIds = new ArrayList<String>();
        for (IStockItem s : watchlist) {
            stockIds.add(s.getStockId());
        }
        return stockIds.toArray(new String[0]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetModel() {
        watchlist.clear();
        activeStocks.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearWatchlist() {
        watchlist.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addAlarm(String stockId, double threshold) {
        IStockItem stockItem = getStock(stockId);
        stockItem.addAlarm(threshold);
        if (alarmListener != null) {
            alarmListener.alarmAdded(stockItem);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addAlarm(String stockId, double threshold, Color color) {
        IStockItem stockItem = getStock(stockId);
        stockItem.addAlarm(threshold, color);
        if (alarmListener != null) {
            alarmListener.alarmAdded(stockItem);
        }
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double[] getAlarms(String stockId) {
        IStockItem stockItem = getStock(stockId);
        return stockItem.getAlarms();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAlarm(String stockId, double threshold) {
        IStockItem stockItem = getStock(stockId);
        stockItem.removeAlarm(threshold);
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearAlarms(String stockId) {
        IStockItem stockItem = getStock(stockId);
        stockItem.clearAlarms();
        if (alarmListener != null) {
//            alarmListener.alarmRemoved(stockItem);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearAllAlarms() {
        for (String stockId : getActiveStockIds()) {
            clearAlarms(stockId);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAlarmListener(IAlarmListener alarmListener) {
        this.alarmListener = alarmListener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getAlarmStockIds() {
        Set<String> alarmStockIds = new HashSet<String>();
        for (IStockItem stockItem : activeStocks) {
            double[] alarms = stockItem.getAlarms();
            if (alarms != null && alarms.length > 0) {
                alarmStockIds.add(stockItem.getStockId());
            }
        }
        return alarmStockIds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<AlarmUnit> getAllAlarmUnits() {
        Set<AlarmUnit> allAlarmUnits = new HashSet<AlarmUnit>();
        
        for (IStockItem stockItem : activeStocks) {
            allAlarmUnits.addAll(stockItem.getAlarmUnits());
        }
        
        return allAlarmUnits;
    }
    
}
