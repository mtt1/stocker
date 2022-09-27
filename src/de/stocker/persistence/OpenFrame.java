package de.stocker.persistence;

import java.awt.Color;
import java.util.List;

import de.stocker.common.*;
import de.stocker.model.dataWrappers.BollingerBand;
import de.stocker.model.dataWrappers.SimpleMovingAverage;

/**
 * The Class OpenFrame is used for collecting information about all open
 * internal frames displayed in the application main frame. This information is
 * used to store the state of the application on a file on disk and to restore
 * it after a restart.
 * 
 * @author Matthias Rudolph
 */
class OpenFrame {
    private EFrameType frameType;
    private int xPosition;
    private int yPosition;
    private int width;
    private int height;

    private String stockId;
    private EChartResolution resolution;
    private EChartType chartType;
    
    private Color alarmColor;
    private Color movingAvgColor;
    private Color bollingerColor;

    private List<SimpleMovingAverage> movingAvgs;
    private List<BollingerBand> bollingers;
    
    /**
     * Instantiates a new open frame with information gathered from a specific
     * internal frame displayed on the main frame.
     *
     * @param frameType the frame type, search, watchlist, or chart
     * @param xPosition the x position
     * @param yPosition the y position
     * @param width the width
     * @param height the height
     */
    public OpenFrame(EFrameType frameType, int xPosition, int yPosition, int width, int height) {
        this.frameType = frameType;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.width = width;
        this.height = height;
    }

    /**
     * Instantiates a new open frame with all information gathered from a
     * internal chart frame displayed on the main frame.
     *
     * @param frameType the frame type
     * @param xPosition the x position
     * @param yPosition the y position
     * @param width the width
     * @param height the height
     * @param stockId the stock id
     * @param resolution the chart resolution
     * @param chartType the chart type
     * @param alarmColor the alarm color
     * @param movingAvgColor the Simple Moving Average color
     * @param bollingerColor the Bollinger Band color
     * @param movingAvgs the list of displayed Simple Moving Average indicator objects
     * @param bollingers the list of displayed Bollinger Band indicator objects
     */
    public OpenFrame(EFrameType frameType, int xPosition, int yPosition, int width, int height, String stockId,
            EChartResolution resolution, EChartType chartType, Color alarmColor, Color movingAvgColor,
            Color bollingerColor, List<SimpleMovingAverage> movingAvgs, List<BollingerBand> bollingers) {
        this(frameType, xPosition, yPosition, width, height);

        this.stockId = stockId;
        this.resolution = resolution;
        this.chartType = chartType;
        this.alarmColor = alarmColor;
        this.movingAvgColor = movingAvgColor;
        this.bollingerColor = bollingerColor;
        this.movingAvgs = movingAvgs;
        this.bollingers = bollingers;
    }

    /**
     * Gets the frame type.
     *
     * @return the frame type
     */
    public EFrameType getFrameType() {
        return frameType;
    }

    /**
     * Gets the x position.
     *
     * @return the x position
     */
    public int getXPosition() {
        return xPosition;
    }

    /**
     * Gets the y position.
     *
     * @return the y position
     */
    public int getYPosition() {
        return yPosition;
    }

    /**
     * Gets the width.
     *
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height.
     *
     * @return the height
     */
    public int getHeight() {
        return height;
    }
    
    /**
     * Gets the stock id.
     *
     * @return the stock id
     */
    public String getStockId() {
        return stockId;
    }

    /**
     * Gets the chart resolution.
     *
     * @return the chart resolution
     */
    public EChartResolution getResolution() {
        return resolution;
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
     * Gets the alarm color.
     *
     * @return the alarm color
     */
    public Color getAlarmColor() {
        return alarmColor;
    }

    /**
     * Gets the Simple Moving Average color.
     *
     * @return the Simple Moving Average color
     */
    public Color getMovingAvgColor() {
        return movingAvgColor;
    }

    /**
     * Gets the Bollinger Band color.
     *
     * @return the Bollinger Band color
     */
    public Color getBollingerColor() {
        return bollingerColor;
    }

    /**
     * Gets the list Simple Moving Average indicator objects displayed in the frame.
     *
     * @return the list of Simple Moving Average indicator objects
     */
    public List<SimpleMovingAverage> getMovingAvgs() {
        return movingAvgs;
    }

    /**
     * Gets the list of Bollinger Band indicator objects displayed in the frame.
     *
     * @return the list of Bollinger Band indicator objects
     */
    public List<BollingerBand> getBollingers() {
        return bollingers;
    }
    
    
}