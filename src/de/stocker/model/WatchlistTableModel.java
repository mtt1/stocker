package de.stocker.model;

import java.util.*;

import javax.swing.Timer;
import javax.swing.table.AbstractTableModel;

import de.stocker.common.IStockItem;

/**
 * The Class WatchlistTableModel extends AbstractTableModel to display the
 * contents of the watchlist specifically in the format to display them in a
 * watchlist frame.
 * 
 * @author Matthias Rudolph
 */
public class WatchlistTableModel extends AbstractTableModel {
    
    private String[] columnNames = { "Name", "Stock ID", "Price", "% change" };
    
    private List<IStockItem> watchlist;
    private Map<Integer, Boolean> timerStates;
    private Map<Integer, Timer> timerMap = new HashMap<Integer, Timer>();
    
    /**
     * Instantiates a new watchlist table model for the provided list of
     * watchlist entries/stock items.
     *
     * @param watchlist the watchlist of stock items
     */
    public WatchlistTableModel(List<IStockItem> watchlist) {
        this.watchlist = watchlist;
    }

    /**
     * Gets the number of rows in the table model.
     *
     * @return the row count
     */
    @Override
    public int getRowCount() {
        return watchlist.size();
    }

    /**
     * Gets the number of columns in the table model.
     *
     * @return the column count
     */
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    /**
     * Gets the number of columns in the table model.
     *
     * @param column the column
     * @return the column count
     */
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    /**
     * Gets the class of objects contained in the specified column.
     *
     * @param columnIndex the column index
     * @return String or Double Class, depending on the requested column
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
        case 0:
        case 1:
            return String.class;
        case 2:
        case 3:
            return Double.class;
        default: return Double.class;
        }
    }

    /**
     * Gets the value at the specified row and column location.
     *
     * @param rowIndex the row index
     * @param columnIndex the column index
     * @return the value at the location
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        IStockItem stockItem = getWatchlistEntry(rowIndex);
        
        if (stockItem.isAvailable()) {
            switch (columnIndex) {
            case 0:
                return stockItem.getDescription();
            case 1:
                return stockItem.getStockId();
            case 2:
                return stockItem.getCurPrice();
            case 3:
                return stockItem.getChange();
            default:
                return null;
            }
        } else {
            // display a no data row with empty values for a stock item that is
            // not available at the data provider
            switch (columnIndex) {
            case 0:
                return "No data avaible.";
            case 1:
                return stockItem.getStockId();
            case 2:
            case 3:
            default:
                return null;
            }
        }
    }
    
    /**
     * Gets the watchlist entry at the specified row index in the model.
     *
     * @param rowIndex the row index
     * @return the watchlist entry/stock item
     */
    public IStockItem getWatchlistEntry(int rowIndex) {
        return watchlist.get(rowIndex);
    }
    
    /**
     * Gets the index of a stock item in the watchlist.
     *
     * @param stockItem the stock item
     * @return the index in the watchlist
     */
    public int getWatchlistEntryIndex(IStockItem stockItem) {
        return watchlist.indexOf(stockItem);
    }

    /**
     * Generates new timer states. This ensures that there are no old timers
     * interfering.
     */
    private void generateTimerStates() {
        timerStates = new HashMap<Integer, Boolean>();
        for (int i = 0; i < watchlist.size(); i++) {
            timerStates.put(i, false);
        }
    }
    
    /**
     * Gets the timer state for a specific row, i. e. watchlist entry. This is
     * used by the watchlist to determine in which color to paint the background
     * of the "price" cell.
     *
     * @param i the i
     * @return the timer state
     */
    public boolean getTimerState(int i) {
        return timerStates.get(i);
    }
    
    /**
     * {@inheritDoc}
     * 
     * Generates new timer states every time the table structure has been
     * updated. This ensures that there are no old timers interfering.
     */
    @Override
    public void fireTableDataChanged() {
        super.fireTableDataChanged();
        generateTimerStates();
    }
    
    /**
     * {@inheritDoc}
     * 
     * Sets the timer for the row that has been updated.
     *
     */
    @Override
    public void fireTableRowsUpdated(int firstRow, int lastRow) {
        super.fireTableRowsUpdated(firstRow, lastRow);
        setTimer(firstRow);
    }
    
    /**
     * Sets the timer for the specified row. The method first stops any old
     * timers that might still be running. It then sets the timer state for this
     * row to active and starts a new timer that sets the timer state to false
     * when it's finished. The timer also calls fireTableRowsUpdated, but the
     * super class method so that the table is repainted when a timer runs
     * out and the background color is removed. Calling the super class method
     * is important, otherwise it would create an infinite timer loop.
     *
     * @param index the index of the row / watchlist entry for which the new
     * timer is added
     */
    private void setTimer(int index) {
        Timer oldTimer = timerMap.get(index);
        if (oldTimer != null) {
            oldTimer.stop();
        }

        timerStates.put(index, true);

        Timer timer = new Timer(300, e -> {
            timerStates.put(index, false);
            super.fireTableRowsUpdated(index, index);
        });

        timerMap.put(index, timer);

        timer.setRepeats(false);
        timer.start();
    }
    

}
