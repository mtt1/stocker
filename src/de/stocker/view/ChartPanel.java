package de.stocker.view;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

import javax.swing.JPanel;

import de.stocker.common.*;
import de.stocker.model.dataWrappers.*;

/**
 * The Class ChartPanel is responsible for drawing the stock information and
 * chart indicators. It pulls the data from the stock object and the settings
 * from the frame it belongs to.
 * 
 * @author Matthias Rudolph
 */
public class ChartPanel extends JPanel {
    
    private ChartFrame chartFrame;
    private IStockItem stockItem;
    private EChartType chartType;
    private EChartResolution chartResolution;
    
    // amount of candles to draw
    private final int DEFAULT_CANDLE_DRAW_AMOUNT = 30;

    // GUI constants
    private final int MARGIN = 10;
    private final int LEGEND = 40;
    private final int GAP = 2;
    private final int CANDLE_PADDING_TOP_BOT = 30;
    private final int STATUS_LINE_HEIGHT = 20;

    // GUI variables
    private double MIN_PRICE;
    private double MAX_PRICE;
    private double STEP;
    private double CANDLE_WIDTH;

    private double drawBottomBoundary;
    private double drawTopBoundary;
    
    // initiate with values outside the frame so that the cross hair is only
    // drawn when the cursor enters the frame
    private int mouseX = -10;
    private int mouseY = -10;
    
    // arrays and lists of values and indicators to be drawn
    private ChartCandle[] candleArray;
    private List<BollingerBand> bollingers;
    private List<SimpleMovingAverage> smas;
    private Set<AlarmUnit> alarmUnits;

    /**
     * Instantiates a new chart panel with the frame it belongs to and the stock
     * whose data it is drawing.
     *
     * @param chartFrame the chart frame
     * @param stockItem the stock item
     */
    public ChartPanel(ChartFrame chartFrame, IStockItem stockItem) {

        this.chartFrame = chartFrame;
        this.stockItem = stockItem;
        
        this.setBackground(Color.WHITE);
        
        this.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseMoved(MouseEvent e) {
                
                // only set mouse coordinates if cursor is in the chart area of the frame
                int newX = e.getX();
                int newY = e.getY();
                if (newX < ChartPanel.this.getWidth() - MARGIN - LEGEND) {
                    mouseX = newX;
                }
                if (newY < ChartPanel.this.getHeight() - MARGIN - LEGEND) {
                    mouseY = newY;
                }
                repaint();
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
            }
        });
        
    }

    /**
     * {@inheritDoc}
     * 
     * Calls all methods needed to collect and visualize the data to be drawn
     * whenever the component is (re-)painted.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // calculates the step width and the candle width used for drawing later
        STEP = (double) ((this.getWidth() - 2 * MARGIN - LEGEND) / DEFAULT_CANDLE_DRAW_AMOUNT);
        CANDLE_WIDTH = (double) STEP - 2 * GAP;
        
        if (stockItem.isAvailable()) {
            collectAllData();
            fixPerspective();
            visualizeData(g2);
            
            drawCrosshairMouseCursor(g2);
            drawStatusLine(g2);
        } else {
            // fail safe for the item state !isAvailable
            g2.drawString("No data available for stock " + stockItem.getStockId() + " at the data provider.", 30, 30);
        }
        
    }

    /**
     * Collects all data necessary to draw the chart from the data stores.
     */
    private void collectAllData() {
        // gets the chart settings for this frame objects
        chartType = chartFrame.getChartType();
        chartResolution = chartFrame.getChartResolution();

        // gets the candle array from the stock item
        if (stockItem.hasCandles(chartResolution)) {
            candleArray = stockItem.getCandleArray(chartResolution);
        }

        // gets the indicators added to this chart from the frame object
        bollingers = new ArrayList<BollingerBand>(chartFrame.getBollingerBands());
        smas = new ArrayList<SimpleMovingAverage>(chartFrame.getMovingAvgs());

        // gets the alarms for this stock
        alarmUnits = stockItem.getAlarmUnits();
    }
    
    /**
     * Fixes the perspective of the chart so that no displayed component is
     * outside the visible frame.
     */
    private void fixPerspective() {
        if (stockItem.hasCandles(chartResolution)) {
            MIN_PRICE = chartFrame.getMinCandlePrice();
            MAX_PRICE = chartFrame.getMaxCandlePrice();
        }
        
        if (bollingers != null) {
            for (BollingerBand bb : bollingers) {
                if (bb.getMinPrice() < MIN_PRICE) {
                    MIN_PRICE = bb.getMinPrice();
                }

                if (bb.getMaxPrice() > MAX_PRICE) {
                    MAX_PRICE = bb.getMaxPrice();
                }
            }
        }
        
        if ((alarmUnits != null) && (alarmUnits.size() > 0)) {
            for (AlarmUnit alarmUnit : alarmUnits) {
                double threshold = alarmUnit.getThreshold();
                
                if (threshold < MIN_PRICE) {
                    MIN_PRICE = threshold;
                } else if (threshold > MAX_PRICE) {
                    MAX_PRICE = threshold;
                }
            }
        }

        /*
         * Resets step and candle width for the actual amount of candles coming
         * from the model. This is necessary for month and week intervals where
         * a lot less candles are delivered from the data provider
         */
        if (candleArray != null && candleArray.length > 0) {
            STEP = (double) ((this.getWidth() - 2 * MARGIN - LEGEND) / candleArray.length);
            CANDLE_WIDTH = (double) STEP - 2 * GAP;
        }

        drawBottomBoundary = this.getHeight() - MARGIN - LEGEND - CANDLE_PADDING_TOP_BOT;
        drawTopBoundary = CANDLE_PADDING_TOP_BOT;
    }
    
    /**
     * Visualizes the data on the chart. Order is important to make sure which
     * component is on top.
     *
     * @param g2 the Graphics2D component to draw on
     */
    private void visualizeData(Graphics2D g2) {
        drawAxes(g2);
        
        if (stockItem.hasCandles(chartResolution)) {
            drawPrices(g2);
            drawIndicators(g2);

            // draw line or candle chart depending on chart type
            switch (chartType) {
            case LINE:
                drawLine(g2);
                break;
            case CANDLE:
            default:
                drawCandles(g2);
            }
            
            drawAlarms(g2);
        } else {
            g2.drawString("Loading data ...", 30, 30);
        }
    }

    /**
     * Draws the X (time) and Y (price) axes.
     *
     * @param g2 the Graphics2D component to draw on
     */
    private void drawAxes(Graphics2D g2) {
        g2.draw(new Line2D.Double(MARGIN, this.getHeight() - MARGIN - LEGEND, this.getWidth() - MARGIN - LEGEND,
                this.getHeight() - MARGIN - LEGEND));
        g2.draw(new Line2D.Double(this.getWidth() - MARGIN - LEGEND, this.getHeight() - MARGIN - LEGEND,
                this.getWidth() - MARGIN - LEGEND, MARGIN));
    }
    
    /**
     * Draws the chart candles.
     *
     * @param g2 the Graphics2D component to draw on
     */
    private void drawCandles(Graphics2D g2) {
        if (candleArray != null) {
            for (int i = 0; i < candleArray.length; i++) {
                drawCandle(g2, candleArray[i], i);
                drawTimestamp(g2, candleArray[i], i);
            }
        } else {
            System.err.println("Error: No candles to draw.");
        }
    }
    
    /**
     * Draws a single chart candle.
     *
     * @param g2 the Graphics2D component to draw on
     * @param candle the candle object to draw
     * @param i the index of the candle object
     */
    private void drawCandle(Graphics2D g2, ChartCandle candle, int i) {
        if (candle == null) {
            System.err.println("Error: No candle to draw.");
            return;
        }
        
        // calculate coordinates
        double xCoordWick = calcChartXCoordinate(i);
        double xCoordCandle = xCoordWick - (0.5 * STEP) + GAP;

        double upperCandleBoundary = Math.max(candle.getPriceOpen(), candle.getPriceClose());
        double lowerCandleBoundary = Math.min(candle.getPriceOpen(), candle.getPriceClose());

        Color candleColor = (candle.getPriceOpen() >= candle.getPriceClose()) ? Color.RED : Color.GREEN;

        // draw candle wick
        g2.setColor(Color.BLACK);
        g2.draw(new Line2D.Double(xCoordWick, calcChartYCoordinate(candle.getPriceLow()), xCoordWick,
                calcChartYCoordinate(candle.getPriceHigh())));

        // fill colored rectangle
        g2.setColor(candleColor);
        Rectangle2D rect = new Rectangle2D.Double(xCoordCandle, calcChartYCoordinate(upperCandleBoundary), CANDLE_WIDTH,
                calcChartYCoordinate(lowerCandleBoundary) - calcChartYCoordinate(upperCandleBoundary));
        g2.fill(rect);

        // draw darker rectangle outline
        g2.setColor(candleColor.darker().darker());
        g2.draw(rect);
    }
    
    /**
     * Draws line for the line chart.
     *
     * @param g2 the Graphics2D component to draw on
     */
    private void drawLine(Graphics2D g2) {
        Path2D path = new Path2D.Double();

        if (candleArray != null) {
            path.moveTo(0, calcChartYCoordinate(candleArray[0].getPriceClose()));
            for (int i = 0; i < candleArray.length; i++) {
                path.lineTo(calcChartXCoordinate(i), calcChartYCoordinate(candleArray[i].getPriceClose()));
                drawTimestamp(g2, candleArray[i], i);
            }
            path.lineTo(this.getWidth() - MARGIN - LEGEND, calcChartYCoordinate(candleArray[candleArray.length - 1].getPriceClose()));
        } else {
            System.err.println("Error: No line data to draw.");
        }

        g2.setColor(Color.BLACK);
        g2.draw(path);
    }

    /**
     * Draws the prices for the y axis labels.
     *
     * @param g2 the Graphics2D component to draw on
     */
    private void drawPrices(Graphics2D g2) {
        final int PRICE_TEXT_PADDING = 2;
        int Y_STEP_COUNT;
        if (this.getHeight() < 280) {
            Y_STEP_COUNT = 5;
        } else if (this.getHeight() < 400) {
            Y_STEP_COUNT = 10;
        } else {
            Y_STEP_COUNT = 15;
        }
        
        double priceDiff = MAX_PRICE - MIN_PRICE;
        int priceStepSizeInt = (int) (priceDiff / Y_STEP_COUNT);
        
        if (priceStepSizeInt > 0) {
            int priceToPaint;

            priceToPaint = (int) MIN_PRICE;
            g2.drawString(String.valueOf(priceToPaint),
                    (int) this.getWidth() - MARGIN - LEGEND + PRICE_TEXT_PADDING, (int) calcChartYCoordinate((double) priceToPaint));
            priceToPaint = (int) (MAX_PRICE + 0.5);
            g2.drawString(String.valueOf(priceToPaint),
                    (int) this.getWidth() - MARGIN - LEGEND + PRICE_TEXT_PADDING, (int) calcChartYCoordinate((double) priceToPaint));

            for (int i = 0; i < Y_STEP_COUNT; i++) {
                priceToPaint = (int) (MIN_PRICE + i * priceStepSizeInt);
                //System.out.println(priceToPaint);
                int yCoord = (int) calcChartYCoordinate((double) priceToPaint);
                g2.drawString(String.valueOf(priceToPaint),
                        (int) this.getWidth() - MARGIN - LEGEND + PRICE_TEXT_PADDING, yCoord);
            }
        } else {
            double priceStepSizeDouble = priceDiff / Y_STEP_COUNT;
            double priceToPaint;

            priceToPaint = MIN_PRICE;
            g2.drawString(String.valueOf(priceToPaint),
                    (int) this.getWidth() - MARGIN - LEGEND + PRICE_TEXT_PADDING, (int) calcChartYCoordinate(priceToPaint));
            priceToPaint = MAX_PRICE;
            g2.drawString(String.valueOf(priceToPaint),
                    (int) this.getWidth() - MARGIN - LEGEND + PRICE_TEXT_PADDING, (int) calcChartYCoordinate(priceToPaint));

            for (int i = 0; i < Y_STEP_COUNT; i++) {
                priceToPaint = (MIN_PRICE + i * priceStepSizeDouble);
                int yCoord = (int) calcChartYCoordinate(priceToPaint);
                g2.drawString(String.format("%.2f", priceToPaint),
                        (int) this.getWidth() - MARGIN - LEGEND + PRICE_TEXT_PADDING, yCoord);
            }
        }
    }
    
    /**
     * Draws timestamp of a specific candle.
     *
     * @param g2 the Graphics2D component to draw on
     * @param candle the candle
     * @param i the index of the candle
     */
    private void drawTimestamp(Graphics2D g2, ChartCandle candle, int i) {
        double xCoordWick = calcChartXCoordinate(i);

        Instant instant = candle.getTimeInstant();
        String timestampString = formatTimestamp(instant);

        int stringWidth = g2.getFontMetrics().stringWidth(timestampString);

        double xCoordStamp = xCoordWick - (stringWidth / 2);

        g2.setColor(Color.BLACK);
        
        // don't draw every timestamp but only a selection depending on frame width
        if ((chartResolution == EChartResolution.DAY || chartResolution == EChartResolution.WEEK ||
                chartResolution == EChartResolution.MONTH) && this.getWidth() < 450) {
            if ((i + 1) % 10 == 0) {
                g2.drawString(timestampString, (int) xCoordStamp, (int) this.getHeight() - MARGIN - STATUS_LINE_HEIGHT);
            }
        } else {
            if ((i + 1) % 5 == 0) {
                g2.drawString(timestampString, (int) xCoordStamp, (int) this.getHeight() - MARGIN - STATUS_LINE_HEIGHT);
            }
        }

    }

    /**
     * Formats the timestamp string depending on the chart resolution
     *
     * @param instant the instant object whose timestamp to print
     * @return the formatted timestamp as astring
     */
    private String formatTimestamp(Instant instant) {
        String format;

        switch(chartResolution) {
        case ONE:
        case FIVE:
        case FIFTEEN:
        case THIRTY:
        case SIXTY:
            format = "HH:mm";
            break;
        case DAY:
        case WEEK:
        case MONTH:
            format = "yyyy-MM-dd";
            break;
        default:
            format = "yyyy-MM-dd HH:mm:ss";
        }

        return DateTimeFormatter.ofPattern(format).withZone(ZoneId.systemDefault()).format(instant);
    }
    
    /**
     * Draws the line of a Simple Moving Average indicator.
     *
     * @param g2 the Graphics2D component to draw on
     * @param sma the simple moving average object
     */
    private void drawMovingAvg(Graphics2D g2, SimpleMovingAverage sma) {
        double[] movingAvg = sma.getMovingAvg();
        
        Path2D path = new Path2D.Double();
        
        // connect path to the left side of the chart, not just starting on the first candle
        path.moveTo(0, calcChartYCoordinate(movingAvg[0]));

        for (int j = 0; j < movingAvg.length; j++) {
            path.lineTo(calcChartXCoordinate(j), calcChartYCoordinate(movingAvg[j]));
        }

        // make sure the last path segment always touches the y-axes in the panel
        path.lineTo(getWidth() - MARGIN - LEGEND, calcChartYCoordinate(movingAvg[movingAvg.length - 1]));
        
        g2.draw(path);

    }
    
    /**
     * Draws the channel of a Bollinger Band indicator.
     *
     * @param g2 the Graphics2D component to draw on
     * @param bb the Bollinger Band object
     */
    private void drawBollingerBand(Graphics2D g2, BollingerBand bb) {
        double[] upperBollingerBand = bb.getUpperBollingerBand();
        double[] lowerBollingerBand = bb.getLowerBollingerBand();
        double[] movingAvg = bb.getMovingAvg();

        Path2D middle = new Path2D.Double();
        Path2D upper = new Path2D.Double();
        Path2D lower = new Path2D.Double();
        Path2D channel = new Path2D.Double();
        
        lower.moveTo(0, calcChartYCoordinate(lowerBollingerBand[0]));
        middle.moveTo(0, calcChartYCoordinate(movingAvg[0]));
        upper.moveTo(0, calcChartYCoordinate(upperBollingerBand[0]));

            for (int j = 0; j < upperBollingerBand.length; j++) {
                lower.lineTo(calcChartXCoordinate(j), calcChartYCoordinate(lowerBollingerBand[j]));
                middle.lineTo(calcChartXCoordinate(j), calcChartYCoordinate(movingAvg[j]));
                upper.lineTo(calcChartXCoordinate(j), calcChartYCoordinate(upperBollingerBand[j]));
            }

            // make sure the last path segment always touches the y-axes in the panel
            middle.lineTo(getWidth() - MARGIN - LEGEND, calcChartYCoordinate(movingAvg[movingAvg.length - 1]));
            upper.lineTo(getWidth() - MARGIN - LEGEND, calcChartYCoordinate(upperBollingerBand[upperBollingerBand.length - 1]));
            lower.lineTo(getWidth() - MARGIN - LEGEND, calcChartYCoordinate(lowerBollingerBand[lowerBollingerBand.length - 1]));
            
            // create the channel to fill in color
            channel.append(upper, false);
            channel.lineTo(lower.getCurrentPoint().getX(), lower.getCurrentPoint().getY());
            for (int j = lowerBollingerBand.length - 1; j >= 0; j--) {
                channel.lineTo(calcChartXCoordinate(j), calcChartYCoordinate(lowerBollingerBand[j]));
            }
            channel.lineTo(0, calcChartYCoordinate(lowerBollingerBand[0]));
            channel.closePath();
            
            Color color = bb.getColor();
            Color colorBrighter = color.brighter().brighter().brighter();
            int r = colorBrighter.getRed();
            int g = colorBrighter.getGreen();
            int b = colorBrighter.getBlue();
            Color channelColor = new Color(r, g, b, 150);

            g2.setColor(channelColor);
            g2.fill(channel);
        
            g2.setColor(color);
            g2.draw(middle);
            g2.draw(upper);
            g2.draw(lower);
    }
    
    /**
     * Wrapper method to draw all indicators which are registered with the frame
     * and to update their values for the current stock values.
     *
     * @param g2 the Graphics2D component to draw on
     */
    private void drawIndicators(Graphics2D g2) {
        if (bollingers != null) {
            for (BollingerBand b : bollingers) {
                BollingerBand updatedB = stockItem.getBollingerBand(chartResolution, b.getF(), b.getN());
                updatedB.setColor(b.getColor());
                drawBollingerBand(g2, updatedB);
            }
        }

        if (smas != null) {
            for (SimpleMovingAverage a : smas) {
                SimpleMovingAverage updatedSMA = stockItem.getMovingAvg(chartResolution, a.getN());
                updatedSMA.setColor(a.getColor());
                drawMovingAvg(g2, updatedSMA);
            }
        }
    }
    
    /**
     * Draws a line for each alarm registered for this stock item.
     *
     * @param g2 the Graphics2D component to draw on
     */
    private void drawAlarms(Graphics2D g2) {
        if ((alarmUnits != null) && (alarmUnits.size() > 0)) {
            for (AlarmUnit alarmUnit : alarmUnits) {
                double threshold = alarmUnit.getThreshold();
                
                g2.setColor(alarmUnit.getColor());
                g2.draw(new Line2D.Double(0, calcChartYCoordinate(threshold), this.getWidth() - MARGIN - LEGEND, calcChartYCoordinate(threshold)));
            }
        }
    }
    
    /**
     * Draws a crosshair on the chart at the position of the mouse cursor
     *
     * @param g2 the Graphics2D component to draw on
     */
    private void drawCrosshairMouseCursor(Graphics2D g2) {

        // set dashed stroke for crosshair lines
        Stroke dashed = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                0, new float[]{9}, 0); 
        g2.setStroke(dashed);
        
        // draw lines at the mouse cursor position
        g2.drawLine(mouseX, 0, mouseX, this.getHeight() - MARGIN - LEGEND);
        g2.drawLine(0, mouseY, this.getWidth() - MARGIN - LEGEND, mouseY);
    }
    
    /**
     * Draws a status line at the bottom of the chart displaying information
     * about the stock and the mouse cursor position.
     *
     * @param g2 the Graphics2D component to draw on
     */
    private void drawStatusLine(Graphics2D g2) {
        int stringYPosition = this.getHeight() - 2 * GAP;
        
        // fill background
        g2.setColor(Color.LIGHT_GRAY);
        g2.fill(new Rectangle2D.Double(0, this.getHeight() - STATUS_LINE_HEIGHT, this.getWidth(), STATUS_LINE_HEIGHT));

        g2.setColor(Color.BLACK);
        
        String stockId = "ID: " + stockItem.getStockId();
        
        String currentPrice = "Current price: " + String.format("%.2f", stockItem.getCurPrice());
        
        // calculate the price at the mouse cursor position
        String cursorPrice = String.format("%.2f", calcPriceFromChartYCoordinate(mouseY));

        String cursorTimestamp = "";
        try {
            // calculate backwards which candle is at the mouse x position to display its timestamp
            ChartCandle nearestCandle = candleArray[calcStepCountFromChartXCoordinate(mouseX)];
            Instant nearestInstant = nearestCandle.getTimeInstant();
            String format = "yyyy-MM-dd HH:mm:ss";
            String nearestInstantFormatted = DateTimeFormatter.ofPattern(format).withZone(ZoneId.systemDefault()).format(nearestInstant);
            cursorTimestamp = nearestInstantFormatted + " | ";
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            /*
             * do nothing, only catch exceptions to account for mouse pointer
             * behavior when leaving frame and going off the chart which can't
             * be translated to coordinates/timestamps
             */
        }
        
        String cursorLabel = "Cursor: " + cursorTimestamp + cursorPrice;
        
        g2.drawString(stockId + " | " + currentPrice, 10, stringYPosition);
        int stringWidth = g2.getFontMetrics().stringWidth(cursorLabel);
        g2.drawString(cursorLabel, this.getWidth() - stringWidth - 10, stringYPosition);
        
    }

    /**
     * Calculates the chart Y coordinate from an input price.
     *
     * @param price the input price
     * @return the Y coordinate in double format
     * 
     * @see ChartPanel#translate(double, double, double, double, double)
     */
    private double calcChartYCoordinate(double price) {
        double yCoord = translate(price, MIN_PRICE, MAX_PRICE, drawBottomBoundary, drawTopBoundary);
        return yCoord;
    }
    
    /**
     * Calculates a stock price from a given chart Y coordinate.
     *
     * @param yCoord the Y coordinate
     * @return the corresponding stock price
     * 
     * @see ChartPanel#translate(double, double, double, double, double)
     */
    private double calcPriceFromChartYCoordinate(double yCoord) {
        double price = translate(yCoord, drawBottomBoundary, drawTopBoundary, MIN_PRICE, MAX_PRICE);
        return price;
    }

    /**
     * Translates an input value from one interval to the corresponding value in
     * another interval. The method is used to translate stock prices to chart
     * coordinates and back.
     *
     * @param value the input value
     * @param inputIntervalLow the low input interval limit
     * @param inputIntervalHigh the high input interval limit
     * @param outputIntervalLow the low output interval limit
     * @param outputIntervalHigh the high output interval limit
     * @return the translated value
     */
    private double translate(double value, double inputIntervalLow, double inputIntervalHigh,
            double outputIntervalLow, double outputIntervalHigh) {
        return (outputIntervalLow + ((value - inputIntervalLow) * (outputIntervalHigh - outputIntervalLow))
                / (inputIntervalHigh - inputIntervalLow));
    }
    
    /**
     * Calculates a chart X coordinate from the index of a given data point. The
     * method uses the STEP value which partitions the panel according to the
     * amount of drawable data points.
     *
     * @param i the index
     * @return the X coordinate
     */
    private double calcChartXCoordinate(int i) {
        return MARGIN + i * STEP + 0.5 * STEP;
    }
    
    /**
     * Calculates the step count from a given chart X coordinate. This
     * calculates which data point a given X coordinate belongs to.
     *
     * @param xCoord the X coordinate
     * @return the index of the corresponding data point
     */
    private int calcStepCountFromChartXCoordinate(double xCoord) {
        return (int) (((xCoord - MARGIN - 0.5 * STEP) / STEP) + 0.5);
    }
    
}
