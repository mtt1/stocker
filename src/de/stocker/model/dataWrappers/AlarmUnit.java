package de.stocker.model.dataWrappers;

import java.awt.Color;

import de.stocker.common.EAlarmPos;

/**
 * The Class AlarmUnit contains all information about a chart alarm packaged
 * into one object: the stock information, the threshold, the alarm position and
 * its drawing color.
 * 
 * @author Matthias Rudolph
 */
public class AlarmUnit extends PaintableChartComponent {
    
    private String stockId;
    private double threshold;
    private EAlarmPos position;
    
    /**
     * Instantiates a new alarm unit without stock information or color.
     * Primarily used for testing purposes.
     *
     * @param threshold the alarm threshold
     * @param position the position set depending on whether the threshold is
     * below or above the current price
     */
    public AlarmUnit(double threshold, EAlarmPos position) {
        this.threshold = threshold;
        this.position = position;
    }

    /**
     * Instantiates a new alarm unit with stock information and the drawing
     * color.
     *
     * @param stockId the stock id
     * @param threshold the alarm threshold
     * @param position the position set depending on whether the threshold is
     * below or above the current price
     * @param color the color used to draw the alarm line on the chart
     */
    public AlarmUnit(String stockId, double threshold, EAlarmPos position, Color color) {
        this(threshold, position);
        this.stockId = stockId;
        this.color = color;
    }

    /**
     * Gets the alarm threshold price
     *
     * @return the threshold
     */
    public double getThreshold() {
        return threshold;
    }

    /**
     * Gets the alarm position: below or above the current price
     *
     * @return the position
     */
    public EAlarmPos getPosition() {
        return position;
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
     * Overriding the toString() method. This is used to conveniently print the
     * alarm threshold, e. g. in the list of alarms added in a chart frame.
     *
     * @return the threshold as string
     */
    @Override
    public String toString() {
        return String.valueOf(threshold);
    }

}
