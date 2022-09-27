package de.stocker.model;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.stocker.common.EChartResolution;
import de.stocker.model.dataWrappers.ChartCandle;
import de.stocker.model.dataWrappers.TradeDataUnit;

/**
 * The Class StockCalcHelper is a calculator class with static methods to
 * perform operations on inputs of values. This is used to calculate the values
 * of chart indicator objects and to update candle data with new push data.
 * 
 * @author Matthias Rudolph
 */
public final class StockCalcHelper {

    /**
     * Calculates the Simple Moving Average for the specified period and the input
     * array of values according to the formula in the course specification. Method
     * returns an array of results.
     *
     * @param n         the period n
     * @param stockData input array of values
     * @return an array of Moving Average values for every data point or an empty
     *         array if there was not enough input data to calculate the moving
     *         average
     */
    public static double[] getMovingAverage(int n, double[] stockData) {
        if (stockData.length < n) {
            System.err.println("Not enough data to calculate moving average.");
            return new double[0];
        }

        int indicatorLength = stockData.length - n + 1;
        double[] movAvg = new double[indicatorLength];
        for (int i = 0; i < indicatorLength; i++) {
            double sum = 0;

            for (int j = 0; j < n; j++) {
                sum += stockData[i + j];
            }

            movAvg[i] = sum / n;
        }

        return movAvg;
    }

    /**
     * Calculates the upper Bollinger Band for the specified parameters and input
     * values according to the formula in the course specification. Method returns
     * an array of results.
     *
     * @param f         the standard deviation factor f
     * @param n         the period n
     * @param stockData input array of values
     * @return an array containing the the upper Bollinger Band values or an empty
     *         array if there was not enough input data to calculate the Bollinger
     *         Band
     */
    public static double[] getUpperBollingerBand(double f, int n, double[] stockData) {
        if (stockData.length < n) {
            System.err.println("Not enough data to calculate upper Bollinger band.");
            return new double[0];
        }

        int indicatorLength = stockData.length - n + 1;
        double[] upperBol = new double[indicatorLength];
        double[] movAvg = getMovingAverage(n, stockData);
        for (int i = 0; i < indicatorLength; i++) {
            upperBol[i] = movAvg[i] + f * stdDev(i, n, stockData, movAvg[i]);
        }

        return upperBol;
    }

    /**
     * Calculates the lower Bollinger Band for the specified parameters and input
     * values according to the formula in the course specification. Method returns
     * an array of results.
     *
     * @param f         the standard deviation factor f
     * @param n         the period n
     * @param stockData input array of values
     * @return an array containing the the lower Bollinger Band values or an empty
     *         array if there was not enough input data to calculate the Bollinger
     *         Band
     */
    public static double[] getLowerBollingerBand(double f, int n, double[] stockData) {
        if (stockData.length < n) {
            System.err.println("Not enough data to calculate lower Bollinger band.");
            return new double[0];
        }

        int indicatorLength = stockData.length - n + 1;
        double[] lowerBol = new double[indicatorLength];
        double[] movAvg = getMovingAverage(n, stockData);
        for (int i = 0; i < indicatorLength; i++) {
            lowerBol[i] = movAvg[i] - f * stdDev(i, n, stockData, movAvg[i]);
        }

        return lowerBol;
    }

    private static double stdDev(int i, int n, double[] stockData, double movAvgPrice) {
        double sum = 0;
        for (int j = 0; j < n; j++) {
            sum += Math.pow(stockData[i + j] - movAvgPrice, 2);
        }
        double stdDev = Math.sqrt(sum / n);
        return stdDev;
    }

    /**
     * Updates a list of candles from a new times-sales/trade data point pushed by
     * the data provider.
     *
     * @param candles         the list of candles as presently stored by the stock
     *                        item
     * @param tradeData       the times-sales/trade data point, containing new
     *                        price, time and trade volume
     * @param chartResolution the chart resolution of the input candles
     * @return the list of candles after updating it with the new data point
     */
    public static List<ChartCandle> updateCandlesFromPushData(List<ChartCandle> candles, TradeDataUnit tradeData,
            EChartResolution chartResolution) {
        List<ChartCandle> candlesToOperateOn = candles;

        // calculate the time length of a chart interval depending on the resolution
        long timeDiff = 0;
        switch (chartResolution) {
        case ONE:
            timeDiff = TimeUnit.MINUTES.toMillis(1);
            break;
        case FIVE:
            timeDiff = TimeUnit.MINUTES.toMillis(5);
            break;
        case FIFTEEN:
            timeDiff = TimeUnit.MINUTES.toMillis(15);
            break;
        case THIRTY:
            timeDiff = TimeUnit.MINUTES.toMillis(30);
            break;
        case SIXTY:
            timeDiff = TimeUnit.HOURS.toMillis(1);
            break;
        case DAY:
            timeDiff = TimeUnit.DAYS.toMillis(1);
            break;
        case WEEK:
            timeDiff = TimeUnit.DAYS.toMillis(7);
            break;
        case MONTH:
            timeDiff = TimeUnit.DAYS.toMillis(31);
            break;
        default:
            break;
        }

        /*
         * Algorithm has to decide whether to update the last candle if its interval is
         * not yet "full" or else whether to append a new candle to the list. For this
         * first the last candle pulled from the list.
         */
        ChartCandle lastCandle = candlesToOperateOn.get(candlesToOperateOn.size() - 1);
        long timeOpen = lastCandle.getTimeOpen();
        double priceOpen = lastCandle.getPriceOpen();
        double priceLow = lastCandle.getPriceLow();
        double priceHigh = lastCandle.getPriceHigh();
        double priceClose = lastCandle.getPriceClose();
        double volume = lastCandle.getVolume();

        /*
         * Introduce a boolean to check whether the algorithm is still working on the
         * last candle of the list or has generated a new one.
         */
        boolean workingOnLastCandle = true;

        if (tradeData.getTime() > timeOpen + timeDiff) {
            // If interval is "full" generate a new candle from the current values
            ChartCandle candle = new ChartCandle(timeOpen, Instant.ofEpochMilli(timeOpen), priceLow, priceHigh,
                    priceOpen, priceClose, volume);

            // Resets the last candle or adds a new one depending on check
            if (workingOnLastCandle) {
                candlesToOperateOn.set(candlesToOperateOn.size() - 1, candle);
                workingOnLastCandle = false;
            } else {
                candlesToOperateOn.add(candle);
            }

            timeOpen = tradeData.getTime();
            priceOpen = tradeData.getPrice();
            priceLow = tradeData.getPrice();
            priceHigh = tradeData.getPrice();
            priceClose = tradeData.getPrice();
            volume = tradeData.getVolume();
        }

        priceLow = Math.min(priceLow, tradeData.getPrice());
        priceHigh = Math.max(priceHigh, tradeData.getPrice());
        priceClose = tradeData.getPrice();
        volume += tradeData.getVolume();

        ChartCandle candle = new ChartCandle(timeOpen, Instant.ofEpochMilli(timeOpen), priceLow, priceHigh, priceOpen,
                priceClose, volume);

        // Resets the last candle or adds a new one depending on check
        if (workingOnLastCandle) {
            candlesToOperateOn.set(candlesToOperateOn.size() - 1, candle);
            workingOnLastCandle = false;
        } else {
            candlesToOperateOn.add(candle);
        }

        return candlesToOperateOn;
    }

    /**
     * Updates a list of candles from a an array of times-sales/trade data points
     * pushed by the data provider.
     *
     * @param candles         the list of candles as presently stored by the stock
     *                        item
     * @param tradeData       the array of times-sales/trade data points, containing
     *                        new prices, times and trade volumes
     * @param chartResolution the chart resolution of the input candles
     * @return the list of candles after updating it with the new data points
     */
    public static List<ChartCandle> updateCandlesFromPushData(List<ChartCandle> candles, TradeDataUnit[] tradeData,
            EChartResolution chartResolution) {
        List<ChartCandle> candlesToOperateOn = candles;

        for (TradeDataUnit tradeDataUnit : tradeData) {
            candlesToOperateOn = updateCandlesFromPushData(candlesToOperateOn, tradeDataUnit, chartResolution);
        }

        return candlesToOperateOn;
    }
}
