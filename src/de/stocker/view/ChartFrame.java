package de.stocker.view;

import java.awt.Color;
import java.util.*;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.plaf.DimensionUIResource;

import de.stocker.common.*;
import de.stocker.model.dataWrappers.*;

/**
 * The Class ChartFrame provides the frame for all chart related drawings, while
 * the drawing itself takes place in the ChartPanel class. The ChartFrame
 * creates the menus and wrappers and fetches the data from the model.
 * 
 * @author Matthias Rudolph
 */
public class ChartFrame extends BaseInternalFrame implements IStockListener {
    
    private IStockerModel stockerModel;
    private String stockId;
    private IStockItem stockItem;

    private volatile List<BollingerBand> bollingerBands = new ArrayList<BollingerBand>();
    private volatile List<SimpleMovingAverage> movingAvgs = new ArrayList<SimpleMovingAverage>();

    private DefaultListModel<PaintableChartComponent> activeIndicatorListModel = new DefaultListModel<PaintableChartComponent>();
    private DefaultListModel<AlarmUnit> activeAlarmListModel = new DefaultListModel<AlarmUnit>();
    
    private Color alarmColor;
    private Color movingAvgColor;
    private Color bollingerColor;
    
    // Menu
    private JMenuBar menuBar;

    private JMenu mChartType;
    private JMenuItem mCandles;
    private JMenuItem mLine;

    private JMenu mChartInterval;
    
    private JMenu mTools;
    private JMenuItem mIndicators;
    private JMenuItem mAlarms;
    
    private ChartPanel chartPanel;
    
    private EChartResolution chartResolution;
    private EChartType chartType;
    
    /** The open frame count is used to stagger the opening locations of the frames. */
    private static int openFrameCount = 0;
    private static final int xOffset = 30;
    private static final int yOffset = 30;
    
    /**
     * Instantiates a new chart frame.
     *
     * @param stockerModel the stocker model
     * @param stockId the stock id
     * @param chartResolution the chart resolution
     * @param chartType the chart type
     * @param alarmColor the alarm color
     * @param movingAvgColor the Simple Moving Average color
     * @param bollingerColor the Bollinger Band color
     */
    public ChartFrame(IStockerModel stockerModel, String stockId,
            EChartResolution chartResolution, EChartType chartType,
            Color alarmColor, Color movingAvgColor, Color bollingerColor) {
        this.stockerModel = stockerModel;
        this.stockId = stockId;
        this.chartResolution = chartResolution;
        this.chartType = chartType;
        this.alarmColor = alarmColor;
        this.movingAvgColor = movingAvgColor;
        this.bollingerColor = bollingerColor;
        
        this.frameType = EFrameType.CHART;
        
        openFrameCount++;

        stockItem = getStock();
        if (stockItem == null) {
            System.err.println("Error: No stock data available.");
            return;
        }
        
        if (stockItem != null) {
            stockItem.addStockListener(this);
        }
        
        initializeData();
        
        createAndShowGUI();
        
        this.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                if (stockItem != null) {
                    stockItem.removeStockListener(ChartFrame.this);
                    openFrameCount--;
                }
            };
        });
    }
    
    private void initializeData() {
        SwingWorker <Void, Void> dataWorker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                stockerModel.triggerDataGeneration(stockId, chartResolution);
                chartPanel.repaint();
        
                return null;
            }
        };
        dataWorker.execute();
    }

    private IStockItem getStock() {
        SwingWorker<IStockItem, Void> worker = new SwingWorker<IStockItem, Void>() {
            @Override
            protected IStockItem doInBackground() {
                return stockerModel.getStock(stockId);
            }
        };
        worker.execute();
        try {
            return worker.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Creates the and show GUI.
     */
    @Override
    protected void createAndShowGUI() {
        super.createAndShowGUI();
        
        updateTitle();

        this.setPreferredSize(new DimensionUIResource(500, 400));
        this.setSize(500, 400);
        
        createMenus();

        chartPanel = new ChartPanel(this, stockItem);
        this.add(chartPanel);
        
        setVisible(true);
    }
    
    private void updateTitle() {
        this.setTitle("Stock: " + this.stockId + " - Chart type: " + chartType + " - Interval: " + chartResolution.getWindowTitleString());
    }
    
    private void createMenus() {
        menuBar = new JMenuBar();

        mChartType = new JMenu("Chart type");
        mCandles = new JMenuItem("Candles");
        mLine = new JMenuItem("Line");
        mChartType.add(mCandles);
        mChartType.add(mLine);
        
        // buttons to switch the chart type
        mCandles.addActionListener(e -> setChartType(EChartType.CANDLE));
        mLine.addActionListener(e -> setChartType(EChartType.LINE));
        
        // auto-generated interval menu, one menu item per enum member
        mChartInterval = new JMenu("Interval");
        for (EChartResolution r : EChartResolution.values()) {
            JMenuItem item = new JMenuItem(r.getWindowTitleString());
            item.addActionListener(e -> {
                SwingUtilities.invokeLater(() -> setChartResolution(r));
            });
            mChartInterval.add(item);
        }
        
        mTools = new JMenu("Chart tools");
        mIndicators = new JMenuItem("Indicators");
        mAlarms = new JMenuItem("Alarms");
        mTools.add(mIndicators);        
        mTools.add(mAlarms);        
        
        // calls the methods to open the corresponding dialogs on button click
        mIndicators.addActionListener(e -> showIndicatorOverviewDialog());
        mAlarms.addActionListener(e -> showAlarmOverviewDialog());

        menuBar.add(mChartType);
        menuBar.add(mChartInterval);
        menuBar.add(mTools);
    
        this.setJMenuBar(menuBar);
    }
    
    /**
     * Gets the id of the displayed stock.
     *
     * @return the stock id
     */
    public String getStockId() {
        return stockId;
    }
    
    /**
     * Gets the chart resolution of this frame.
     *
     * @return the chart resolution
     */
    public EChartResolution getChartResolution() {
        return chartResolution;
    }
    
    /**
     * Gets the chart type of this frame.
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
        updateTitle();
        chartPanel.repaint();
    }
    
    /**
     * Sets the chart resolution, initializes the data for the new resolution
     * and updates the title of the frame.
     *
     * @param chartResolution the new chart resolution
     */
    public void setChartResolution(EChartResolution chartResolution) {
        this.chartResolution = chartResolution;
        initializeData();
        updateTitle();
        chartPanel.repaint();
    }
    
    /**
     * Shows the overview dialog of chart indicators. The dialog displays the
     * currently active indicators and allows the user to select an indicator
     * type to add to the chart.
     */
    private void showIndicatorOverviewDialog() {
        
        JPanel indicatorPanel = new JPanel();
        
        JPanel availableIndicators = new JPanel();

        DefaultListModel<String> listModelAvailable = new DefaultListModel<>();
        listModelAvailable.addElement("Simple Moving Average");
        listModelAvailable.addElement("Bollinger Band");
        JList<String> listAvailable = new JList<String>(listModelAvailable);
        listAvailable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listAvailable.setLayoutOrientation(JList.VERTICAL);
        listAvailable.setVisibleRowCount(-1);
        JScrollPane spAvailable = new JScrollPane(listAvailable);
        spAvailable.setPreferredSize(new DimensionUIResource(200, 100));
        
        JButton addButton = new JButton("Add");
        availableIndicators.add(spAvailable);
        availableIndicators.add(addButton);
        availableIndicators.setBorder(BorderFactory.createTitledBorder("Available Indicators"));
        
        JPanel activeIndicators = new JPanel();

        // updates the list model of indicators active in this frame, outside of
        // this method, to properly display them in the JList
        updateIndicatorListModel();
        JList<PaintableChartComponent> listActive = new JList<PaintableChartComponent>(activeIndicatorListModel);
        listActive.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listActive.setLayoutOrientation(JList.VERTICAL);
        listActive.setVisibleRowCount(-1);
        JScrollPane spActive = new JScrollPane(listActive);
        spActive.setPreferredSize(new DimensionUIResource(200, 100));
        
        JButton removeButton = new JButton("Remove");

        activeIndicators.add(spActive);
        activeIndicators.add(removeButton);
        activeIndicators.setBorder(BorderFactory.createTitledBorder("Active Indicators"));

        indicatorPanel.add(availableIndicators);
        indicatorPanel.add(activeIndicators);
        
        // show dialog to add a new indicator depending on which was selected in the list
        addButton.addActionListener(e -> {
            if (listAvailable.getSelectedIndex() == 0) {
                showAddMovingAvgDialog();
            } else if (listAvailable.getSelectedIndex() == 1) {
                showAddBollingerDialog();
            }
        });
        
        // remove selected indicator
        removeButton.addActionListener(e -> {
            removeIndicator(listActive.getSelectedValue());
            updateIndicatorListModel();
            chartPanel.repaint();
        });
        
        JOptionPane.showMessageDialog(this, indicatorPanel, "Indicators", JOptionPane.PLAIN_MESSAGE);
        
    }
    
    /**
     * Shows the dialog to add a simple moving average indicator to the chart
     */
    private void showAddMovingAvgDialog() {
        JPanel inputPanel = new JPanel();
        JLabel nLabel = new JLabel("n: ");
        JTextField nField = new JTextField(10);
        JLabel colorLabel = new JLabel("Color: ");
        JColorChooser cc = new JColorChooser(getMovingAvgColor());

        // display only one panel for the color chooser
        AbstractColorChooserPanel[] panels = cc.getChooserPanels();
        for (AbstractColorChooserPanel accp : panels) {
           if(!accp.getDisplayName().equals("Swatches")) {
              cc.removeChooserPanel(accp);
           } 
        }
        
        inputPanel.add(nLabel);
        inputPanel.add(nField);
        inputPanel.add(colorLabel);
        inputPanel.add(cc);

        int option = JOptionPane.showConfirmDialog(this, inputPanel, "Add Simple Moving Average", JOptionPane.OK_CANCEL_OPTION);
        
        // collects user input on ok click
        if (option == JOptionPane.OK_OPTION) {
            int n = Integer.parseInt(nField.getText());
            Color color = cc.getColor();
            addMovingAvg(n, color);
            updateIndicatorListModel();
            chartPanel.repaint();
        }
    }
    
    /**
     * Shows the dialog to add a bollinger band indicator to the chart
     */
    private void showAddBollingerDialog() {
        JPanel inputPanel = new JPanel();
        JLabel fLabel = new JLabel("f: ");
        JTextField fField = new JTextField(10);
        JLabel nLabel = new JLabel("n: ");
        JTextField nField = new JTextField(10);
        JLabel colorLabel = new JLabel("Color: ");
        JColorChooser cc = new JColorChooser(getBollingerColor());

        // display only one panel for the color chooser
        AbstractColorChooserPanel[] panels = cc.getChooserPanels();
        for (AbstractColorChooserPanel accp : panels) {
           if(!accp.getDisplayName().equals("Swatches")) {
              cc.removeChooserPanel(accp);
           } 
        }
        
        inputPanel.add(fLabel);
        inputPanel.add(fField);
        inputPanel.add(nLabel);
        inputPanel.add(nField);
        inputPanel.add(colorLabel);
        inputPanel.add(cc);

        int option = JOptionPane.showConfirmDialog(this, inputPanel, "Add Bollinger Band", JOptionPane.OK_CANCEL_OPTION);
        
        // collects user input on ok click
        if (option == JOptionPane.OK_OPTION) {
            double f = Double.parseDouble(fField.getText());
            int n = Integer.parseInt(nField.getText());
            Color color = cc.getColor();
            addBollingerBand(f, n, color);
            updateIndicatorListModel();
            chartPanel.repaint();
        }
    }

    /**
     * Shows the overview dialog for stock alarms. The dialog displays the
     * currently active alarms and allows the user to add an alarm
     */
    private void showAlarmOverviewDialog() {
        JPanel alarmPanel = new JPanel();
        
        JPanel activeAlarms = new JPanel();
        
        updateAlarmListModel();
        JList<AlarmUnit> listActive = new JList<AlarmUnit>(activeAlarmListModel);
        listActive.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listActive.setLayoutOrientation(JList.VERTICAL);
        listActive.setVisibleRowCount(-1);
        JScrollPane spActive = new JScrollPane(listActive);
        spActive.setPreferredSize(new DimensionUIResource(200, 100));

        JButton addButton = new JButton("Add");
        JButton removeButton = new JButton("Remove");

        activeAlarms.add(spActive);
        activeAlarms.add(addButton);
        activeAlarms.add(removeButton);
        activeAlarms.setBorder(BorderFactory.createTitledBorder("Active Alarms"));

        alarmPanel.add(activeAlarms);
        
        addButton.addActionListener(e -> showAddAlarmDialog());
        removeButton.addActionListener(e -> {
            double threshold = listActive.getSelectedValue().getThreshold();
            stockerModel.removeAlarm(stockId, threshold);
            updateAlarmListModel();
            chartPanel.repaint();
        });

        JOptionPane.showMessageDialog(this, alarmPanel, "Alarms", JOptionPane.PLAIN_MESSAGE);
    }
    
    /**
     * Shows the dialog to add an alarm to this stock
     */
    private void showAddAlarmDialog() {
        JPanel inputPanel = new JPanel();
        JLabel tLabel = new JLabel("Threshold: ");
        JTextField tField = new JTextField(10);
        
        JLabel colorLabel = new JLabel("Color: ");
        JColorChooser cc = new JColorChooser(getAlarmColor());

        // display only one panel for the color chooser
        AbstractColorChooserPanel[] panels = cc.getChooserPanels();
        for (AbstractColorChooserPanel accp : panels) {
           if(!accp.getDisplayName().equals("Swatches")) {
              cc.removeChooserPanel(accp);
           } 
        }
        
        JPanel currentPanel = new JPanel();
        String cString = "Current price for stock " + stockItem.getDescription() + " is: " + stockItem.getCurPrice() + ".";
        JLabel cLabel = new JLabel(cString);
        currentPanel.add(cLabel);
        
        inputPanel.add(currentPanel);
        inputPanel.add(tLabel);
        inputPanel.add(tField);
        inputPanel.add(colorLabel);
        inputPanel.add(cc);

        int option = JOptionPane.showConfirmDialog(this, inputPanel, "Add Alarm", JOptionPane.OK_CANCEL_OPTION);
        
        // collects user input on ok click
        if (option == JOptionPane.OK_OPTION) {
            double threshold = Double.parseDouble(tField.getText());
            Color color = cc.getColor();
            stockerModel.addAlarm(stockId, threshold, color);
            updateAlarmListModel();
            chartPanel.repaint();
        }
    }
    
    /**
     * Updates the list model of indicators active in this frame
     */
    private void updateIndicatorListModel() {
        activeIndicatorListModel.clear();
        activeIndicatorListModel.addAll(movingAvgs);
        activeIndicatorListModel.addAll(bollingerBands);
    }
    
    /**
     * Updates the list model of active alarms for this stock
     */
    private void updateAlarmListModel() {
        activeAlarmListModel.clear();
        activeAlarmListModel.addAll(stockItem.getAlarmUnits());
    }
    
    /**
     * Gets the list of Bollinger Band indicators displayed in this chart frame.
     *
     * @return the list of Bollinger Band indicators
     */
    public List<BollingerBand> getBollingerBands() {
        return bollingerBands;
    }
    
    /**
     * Gets the list of Simple Moving Average indicators displayed in this chart
     * frame.
     *
     * @return the list of Simple Moving Average indicators
     */
    public List<SimpleMovingAverage> getMovingAvgs() {
        return movingAvgs;
    }
    
    /**
     * Adds a Bollinger Band indicator with the specified parameters
     *
     * @param f the standard deviation factor f
     * @param n the period n
     * @param color the color
     */
    public void addBollingerBand(double f, int n, Color color) {
        BollingerBand bb;
        // calls the stock method to generate the Bollinger Band values of the
        // stock item is available
        if (stockItem.isAvailable()) {
            bb = stockItem.getBollingerBand(chartResolution, f, n);
        } else {
            bb = new BollingerBand(f, n, null, null, null);
        }
        if (bb != null) {
            bb.setColor(color);
            bollingerBands.add(bb);
        }
    }
    
    /**
     * Adds a Simple Moving Average indicator with the specified parameters
     *
     * @param n the period n
     * @param color the color
     */
    public void addMovingAvg(int n, Color color) {
        SimpleMovingAverage sma;
        if (stockItem.isAvailable()) {
            sma = stockItem.getMovingAvg(chartResolution, n);
        } else {
            sma = new SimpleMovingAverage(n, null);
        }
        if (sma != null) {
            sma.setColor(color);
            movingAvgs.add(sma);
        }
    }
    
    /**
     * Removes the indicator from the corresponding list
     *
     * @param indicator the indicator to be removed
     */
    private void removeIndicator(PaintableChartComponent indicator) {
        if (indicator instanceof BollingerBand) {
            bollingerBands.remove(indicator);
        } else if (indicator instanceof SimpleMovingAverage) {
            movingAvgs.remove(indicator);
        }
    }
    
    /**
     * Gets the maximum candle price for the displayed resolution which is used
     * to fix the perspective on the chart panel
     *
     * @return the maximum candle price
     */
    public double getMaxCandlePrice() {
        double max = 0;
        ChartCandle[] candles = stockItem.getCandleArray(chartResolution);
        if (candles != null) {
            for (ChartCandle c : candles) {
                if (c.getPriceHigh() > max) {
                    max = c.getPriceHigh();
                }
            }
        }
        
        return max;
    }
    
    /**
     * Gets the minimum candle price for the displayed resolution which is used
     * to fix the perspective on the chart panel
     *
     * @return the minimum candle price
     */
    public double getMinCandlePrice() {
        double min = 0;
        ChartCandle[] candles = stockItem.getCandleArray(chartResolution);
        if (candles != null) {
            min = candles[0].getPriceLow();
            for (ChartCandle c : candles) {
                if (c.getPriceLow() < min) {
                    min = c.getPriceLow();
                }
            }
        }
        
        return min;
    }

    /**
     * {@inheritDoc}
     * 
     * Repaints the chart panel with new data.
     */
    @Override
    public void stockDataUpdated(IStockItem s) {
        if (chartPanel != null) {
            chartPanel.repaint();
        }
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
     * Gets the Moving Average indicator color.
     *
     * @return the Moving Average color
     */
    public Color getMovingAvgColor() {
        return movingAvgColor;
    }

    /**
     * Gets the Bollinger Band indicator color.
     *
     * @return the Bollinger Band indicator color
     */
    public Color getBollingerColor() {
        return bollingerColor;
    }

    /**
     * Gets the open frame count which is used stagger the opened frames so that
     * they don't complete overlap.
     *
     * @return the open frame count
     */
    public int getOpenFrameCount() {
        return openFrameCount;
    }

    /**
     * Gets the x offset used for staggering the frames.
     *
     * @return the x offset
     */
    public int getXOffset() {
        return xOffset;
    }

    /**
     * Gets the y offset used for staggering the frames.
     *
     * @return the y offset
     */
    public int getYOffset() {
        return yOffset;
    }

}
