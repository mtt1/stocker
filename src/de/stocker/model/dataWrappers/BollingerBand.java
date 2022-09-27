package de.stocker.model.dataWrappers;

import java.util.Arrays;

/**
 * The Class BollingerBand contains all information of a Bollinger Band
 * indicator object: the values of the Bollinger Bands, the Simple Moving
 * Average and the parameters used to calculate them.
 * 
 * @author Matthias Rudolph
 */
public class BollingerBand extends PaintableChartComponent {
    
    private double f;
    private int n;
    private double[] movingAvg;
    private double[] upperBollingerBand;
    private double[] lowerBollingerBand;
    
    /**
     * Instantiates a new Bollinger Band object with the specified parameters
     * for storing all indicator components.
     *
     * @param f the standard deviation factor f
     * @param n n determines the period of the moving average used to calculate
     * the Bollinger Bands
     * @param movingAvg the array of Simple Moving Average values
     * @param upperBollingerBand the array containing the values of the upper
     * Bollinger Band
     * @param lowerBollingerBand the array containing the values of the lower
     * Bollinger Band
     */
    public BollingerBand(double f, int n, double[] movingAvg, double[] upperBollingerBand, double[] lowerBollingerBand) {
        this.n = n;
        this.f = f;
        this.movingAvg = movingAvg;
        this.upperBollingerBand = upperBollingerBand;
        this.lowerBollingerBand = lowerBollingerBand;
        
        name = "Bollinger Band";
    }

    /**
     * Gets an array of upper Bollinger Band values. The array is as long as the
     * default candle draw amount, thus containing a Bollinger value for every
     * painted data point.
     *
     * @return the array of upper Bollinger Band values
     */
    public double[] getUpperBollingerBand() {
        if (upperBollingerBand.length >= n) {
            return Arrays.copyOfRange(upperBollingerBand, upperBollingerBand.length - DEFAULT_CANDLE_DRAW_AMOUNT, upperBollingerBand.length - 1);
        } else {
            System.err.println("Error: No enough data to draw Bollinger Band.");
            return null;
        }

    }
    
    /**
     * Gets an array of lower Bollinger Band values. The array is as long as the
     * default candle draw amount, thus containing a Bollinger value for every
     * painted data point.
     *
     * @return the array of lower Bollinger Band values
     */
    public double[] getLowerBollingerBand() {
        if (lowerBollingerBand.length >= n) {
            return Arrays.copyOfRange(lowerBollingerBand, lowerBollingerBand.length - DEFAULT_CANDLE_DRAW_AMOUNT, lowerBollingerBand.length - 1);
        } else {
            System.err.println("Error: No enough data to draw Bollinger Band.");
            return null;
        }
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
            return Arrays.copyOfRange(movingAvg, movingAvg.length - DEFAULT_CANDLE_DRAW_AMOUNT, movingAvg.length - 1);
        } else {
            System.err.println("Error: No enough data to draw Bollinger Band.");
            return null;
        }
    }

    /**
     * Gets the minimum price of this indicator object, used to fix the scale of
     * the chart frame.
     *
     * @return the minimum price of this indicator object
     */
    public double getMinPrice() {
        double min = 0;
        if (lowerBollingerBand != null) {
            min = lowerBollingerBand[lowerBollingerBand.length - DEFAULT_CANDLE_DRAW_AMOUNT];
            for (int i = 0; i < DEFAULT_CANDLE_DRAW_AMOUNT; i++) {
                double d = lowerBollingerBand[lowerBollingerBand.length - DEFAULT_CANDLE_DRAW_AMOUNT + i];
                if (d < min) {
                    min = d;
                }
            }
        }
        return min;
    }
    
    /**
     * Gets the maximum price of this indicator object, used to fix the scale of
     * the chart frame.
     *
     * @return the maximum price of this indicator object
     */
    public double getMaxPrice() {
        double max = 0;
        if (upperBollingerBand != null) {
            max = upperBollingerBand[upperBollingerBand.length - DEFAULT_CANDLE_DRAW_AMOUNT];
            for (int i = 0; i < DEFAULT_CANDLE_DRAW_AMOUNT; i++) {
                double d = upperBollingerBand[upperBollingerBand.length - DEFAULT_CANDLE_DRAW_AMOUNT + i];
                if (d > max) {
                    max = d;
                }
            }
        }
        return max;
    }
    
    /**
     * Gets the standard deviation factor f used to calculate the Bollinger Band
     * values.
     *
     * @return the standard deviation factor f
     */
    public double getF() {
        return f;
    }

    /**
     * Gets the integer n stating the period over which the Simple Moving
     * Average is calculated, used to calculate the Bollinger Band values.
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
        String label = name + ": " + getF() + ", " + getN();
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
