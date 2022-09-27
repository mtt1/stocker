package de.stocker.preferences;

import java.awt.Color;
import java.util.*;

import de.stocker.common.EChartResolution;
import de.stocker.common.EChartType;
import de.stocker.model.dataWrappers.DataProvider;

/**
 * The Class PreferencesModel contains all currently active preference settings
 * and their corresponding defaults.
 * 
 * @author Matthias Rudolph
 */
public class PreferencesModel {

    /*
     * DEFAULTS
     * 
     * Use transient keyword to avoid serialization of the default values
     * when saved to disk as a json file.
     */

    // Watchlist
    private final transient int DEFAULT_MIN_WIDTH_WATCHLIST_FRAME = 300;
    private final transient int DEFAULT_MIN_HEIGHT_WATCHLIST_FRAME = 500;
    
    // Search
    private final transient int DEFAULT_MIN_WIDTH_SEARCH_FRAME = 500;
    private final transient int DEFAULT_MIN_HEIGHT_SEARCH_FRAME = 200;
    
    // Charts
    private final transient int DEFAULT_MIN_WIDTH_CHART_FRAME = 600;
    private final transient int DEFAULT_MIN_HEIGHT_CHART_FRAME = 400;
    private final transient EChartResolution DEFAULT_CANDLE_RESOLUTION = EChartResolution.DAY;
    private final transient EChartType DEFAULT_CHART_TYPE = EChartType.CANDLE;
    private final transient Color DEFAULT_COLOR_ALARM = Color.RED;
    private final transient Color DEFAULT_COLOR_MOVING_AVG = Color.BLUE;
    private final transient Color DEFAULT_COLOR_BOLLINGER = Color.CYAN;
    
    /*
     * ACTIVE
     */
    // Frames
    private int minWidthWatchlistFrame;
    private int minHeightWatchlistFrame;
    private int minWidthSearchFrame;
    private int minHeightSearchFrame;
    private int minWidthChartFrame;
    private int minHeightChartFrame;

    // Charts
    private EChartResolution chartResolution;
    private EChartType chartType;
    private Color colorAlarm;
    private Color colorMovingAvg;
    private Color colorBollinger;
    
    // Data
    private List<DataProvider> dataProviders;
    private Integer activeDataProvider;
    private transient DataProvTableModel dataTableModel;
    
    /**
     * Instantiates a new preferences model.
     */
    public PreferencesModel() {
        
    }
    
    /**
     * Sets the defaults.
     */
    public void setDefaults() {
        // Frames
        minWidthWatchlistFrame = DEFAULT_MIN_WIDTH_WATCHLIST_FRAME;
        minHeightWatchlistFrame = DEFAULT_MIN_HEIGHT_WATCHLIST_FRAME;
        minWidthSearchFrame = DEFAULT_MIN_WIDTH_SEARCH_FRAME;
        minHeightSearchFrame = DEFAULT_MIN_HEIGHT_SEARCH_FRAME;
        minWidthChartFrame = DEFAULT_MIN_WIDTH_CHART_FRAME;
        minHeightChartFrame = DEFAULT_MIN_HEIGHT_CHART_FRAME;
        
        // Charts
        chartResolution = DEFAULT_CANDLE_RESOLUTION;
        chartType = DEFAULT_CHART_TYPE;
        colorAlarm = DEFAULT_COLOR_ALARM;
        colorBollinger = DEFAULT_COLOR_BOLLINGER;
        colorMovingAvg = DEFAULT_COLOR_MOVING_AVG;
        
        // Data
        dataProviders = new ArrayList<DataProvider>() {{
            add(new DataProvider("Kursdatengenerator", "", "http://localhost:8080", "ws://localhost:8090"));
            add(new DataProvider("Finnhub", "", "https://finnhub.io/api/v1", "wss://ws.finnhub.io"));
        }};
        activeDataProvider = 1;
        dataTableModel = new DataProvTableModel(dataProviders, activeDataProvider);
        
    }

    /**
     * Gets the minimum width of the watchlist frame.
     *
     * @return the minimum width of the watchlist frame
     */
    public int getMinWidthWatchlistFrame() {
        return minWidthWatchlistFrame;
    }

    /**
     * Sets the minimum width of the watchlist frame.
     *
     * @param minWidthWatchlistFrame the new minimum width of the watchlist frame
     */
    public void setMinWidthWatchlistFrame(int minWidthWatchlistFrame) {
        this.minWidthWatchlistFrame = minWidthWatchlistFrame;
    }

    /**
     * Gets the minimum height of the watchlist frame.
     *
     * @return the minimum height of the watchlist frame
     */
    public int getMinHeightWatchlistFrame() {
        return minHeightWatchlistFrame;
    }

    /**
     * Sets the minimum height of the watchlist frame.
     *
     * @param minHeightWatchlistFrame the new minimum height of the watchlist frame
     */
    public void setMinHeightWatchlistFrame(int minHeightWatchlistFrame) {
        this.minHeightWatchlistFrame = minHeightWatchlistFrame;
    }

    /**
     * Gets the minimum width of the search frame.
     *
     * @return the minimum width of the search frame
     */
    public int getMinWidthSearchFrame() {
        return minWidthSearchFrame;
    }

    /**
     * Sets the minimum width of the search frame.
     *
     * @param minWidthSearchFrame the new minimum width of the search frame
     */
    public void setMinWidthSearchFrame(int minWidthSearchFrame) {
        this.minWidthSearchFrame = minWidthSearchFrame;
    }

    /**
     * Gets the minimum height of the search frame.
     *
     * @return the minimum height of the search frame
     */
    public int getMinHeightSearchFrame() {
        return minHeightSearchFrame;
    }

    /**
     * Sets the minimum height of the search frame.
     *
     * @param minHeightSearchFrame the new minimum height of the search frame
     */
    public void setMinHeightSearchFrame(int minHeightSearchFrame) {
        this.minHeightSearchFrame = minHeightSearchFrame;
    }

    /**
     * Gets the minimum width of the chart frames.
     *
     * @return the minimum width of the chart frames
     */
    public int getMinWidthChartFrame() {
        return minWidthChartFrame;
    }

    /**
     * Sets the minimum width of the chart frames.
     *
     * @param minWidthChartFrame the new minimum width of the chart frames
     */
    public void setMinWidthChartFrame(int minWidthChartFrame) {
        this.minWidthChartFrame = minWidthChartFrame;
    }

    /**
     * Gets the minimum height of the chart frames.
     *
     * @return the minimum height of the chart frames
     */
    public int getMinHeightChartFrame() {
        return minHeightChartFrame;
    }

    /**
     * Sets the minimum height of the chart frames.
     *
     * @param minHeightChartFrame the new minimum height of the chart frames
     */
    public void setMinHeightChartFrame(int minHeightChartFrame) {
        this.minHeightChartFrame = minHeightChartFrame;
    }

    /**
     * Gets the chart resolution.
     *
     * @return the chart resolution
     */
    public EChartResolution getChartResolution() {
        return chartResolution;
    }

    /**
     * Sets the chart resolution.
     *
     * @param chartResolution the new chart resolution
     */
    public void setChartResolution(EChartResolution chartResolution) {
        this.chartResolution = chartResolution;
    }

    /**
     * Gets the chart type.
     *
     * @return the chart type
     */
    public EChartType getChartType() {
        return chartType;
    }

    /**
     * Sets the chart type.
     *
     * @param chartType the new chart type
     */
    public void setChartType(EChartType chartType) {
        this.chartType = chartType;
    }

    /**
     * Gets the alarm color.
     *
     * @return the alarm color
     */
    public Color getColorAlarm() {
        return colorAlarm;
    }

    /**
     * Sets the alarm color.
     *
     * @param colorAlarm the new alarm color
     */
    public void setColorAlarm(Color colorAlarm) {
        this.colorAlarm = colorAlarm;
    }

    /**
     * Gets the Bollinger Band color.
     *
     * @return the Bollinger Band color
     */
    public Color getColorBollinger() {
        return colorBollinger;
    }

    /**
     * Sets the Bollinger Band color.
     *
     * @param colorBollinger the new Bollinger Band color
     */
    public void setColorBollinger(Color colorBollinger) {
        this.colorBollinger = colorBollinger;
    }

    /**
     * Gets the Moving Average color.
     *
     * @return the Moving Average color
     */
    public Color getColorMovingAvg() {
        return colorMovingAvg;
    }

    /**
     * Sets the Moving Average color.
     *
     * @param colorMovingAvg the new Moving Average color
     */
    public void setColorMovingAvg(Color colorMovingAvg) {
        this.colorMovingAvg = colorMovingAvg;
    }

    /**
     * Gets the list of data providers.
     *
     * @return the list of data providers
     */
    public List<DataProvider> getDataProviders() {
        return dataProviders;
    }

    /**
     * Sets the data providers.
     *
     * @param dataProviders the new data providers
     */
    public void setDataProviders(List<DataProvider> dataProviders) {
        this.dataProviders = dataProviders;
    }
    
    /**
     * Adds a data provider.
     *
     * @param p the data provider
     */
    public void addDataProvider(DataProvider p) {
        this.dataProviders.add(p);
    }

    /**
     * Gets the index of the active data provider.
     *
     * @return the index of the active data provider
     */
    public int getActiveDataProvider() {
        return activeDataProvider;
    }

    /**
     * Sets the active data provider.
     *
     * @param activeDataProvider the index of the new active data provider
     */
    public void setActiveDataProvider(int activeDataProvider) {
        this.activeDataProvider = activeDataProvider;
        dataTableModel.setActiveDataProvider(activeDataProvider);
        dataTableModel.fireTableDataChanged();
    }

    /**
     * Gets the data provider table model.
     *
     * @return the data provider table model
     */
    public DataProvTableModel getDataTableModel() {
        if (dataTableModel == null) {
            generateTableModel();
        }
        return dataTableModel;
    }
    
    private void generateTableModel() {
        this.dataTableModel = new DataProvTableModel(dataProviders, activeDataProvider);
    }
    
}
