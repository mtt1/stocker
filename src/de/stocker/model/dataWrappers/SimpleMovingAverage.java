package de.stocker.model.dataWrappers;

import java.util.Arrays;

/**
 * The Class SimpleMovingAverage contains all information of a Simple Moving
 * Average indicator object: the values of the Simple Moving Average and the
 * parameter used to calculate it.
 * 
 * @author Matthias Rudolph
 */
public class SimpleMovingAverage extends PaintableChartComponent {
    
    private double[] movingAvg;
    private int n;
    
    /**
     * Instantiates a new Simple Moving Average object with the specified
     * parameters for storing all indicator components.
     *
     * @param n n determines the period over which the Moving aVerage is
     * calculated
     * @param movingAvg the array containing the values of the Simple Moving
     * Average
     */
    public SimpleMovingAverage(int n, double[] movingAvg) {
        this.n = n;
        this.movingAvg = movingAvg;

        name = "SMA";
    }

    /**
     * Gets an array of Simple Moving Average values. The array is as long as the
     * default candle draw amount, thus containing a Moving Average value for
     * every painted data point.
     *
     * @return the array of Simple Moving Average values
     */
    public double[] getMovingAvg() {
        if (movingAvg.length >= n) {
            return Arrays.copyOfRange(movingAvg, movingAvg.length - DEFAULT_CANDLE_DRAW_AMOUNT - 1, movingAvg.length - 1);
        } else {
            System.err.println("Error: Not enough data to draw moving average.");
            return null;
        }
    }

    /**
     * Gets the integer n stating the period over which the Simple Moving
     * Average is calculated.
     *
     * @return the period n
     */
    public int getN() {
        return n;
    }
    
    /**
     * Gets the label text, detailing the name and parameters of the indicator
     * object.
     *
     * @return the label text
     */
    public String getLabelText() {
        String label = name + ": " + String.valueOf(getN());
        return label;
    }

    /**
     * Overriding the toString() method. This is used to conveniently print the
     * indicator name and details, e. g. in the list of active indicators in a
     * chart frame.
     *
     * @return the label text
     */
    @Override
    public String toString() {
        return getLabelText();
    }

}
