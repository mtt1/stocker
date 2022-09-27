package de.stocker.view;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.table.*;

import de.stocker.common.*;
import de.stocker.model.WatchlistTableModel;

/**
 * The Class WatchlistFrame shows the watchlist and allows the user to interact
 * with its content.
 * 
 * @author Matthias Rudolph
 */
public class WatchlistFrame extends BaseInternalFrame {
    
    private IMainController mainController;
    private IStockerModel stockerModel;
    private WatchlistTableModel watchlistTableModel;
    
    /**
     * Instantiates a new watchlist frame with references to the main controller
     * for the communication with other application parts and to the stocker
     * model for the watchlist data.
     *
     * @param mainController the main controller
     * @param stockerModel the stocker model
     */
    public WatchlistFrame(IMainController mainController, IStockerModel stockerModel) {
        this.mainController = mainController;
        this.stockerModel = stockerModel;
        this.watchlistTableModel = stockerModel.getWatchlistTableModel();
        
        title = "Watchlist";
        
        this.frameType = EFrameType.WATCHLIST;
        
        createAndShowGUI();
    }
    
    /**
     * Creates and shows the GUI. The frame shows the watchlist table model,
     * implements a flashing rendering on a price change and adds the buttons.
     */
    @Override
    protected void createAndShowGUI() {
        super.createAndShowGUI();
        
        this.setTitle(title);
        this.setPreferredSize(new DimensionUIResource(300, 500));
        this.setSize(300, 500);

        this.setLayout(new BorderLayout());
        
        this.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                int x = WatchlistFrame.this.getX();
                int y = WatchlistFrame.this.getY();
                mainController.saveWatchlistLocation(x, y);
            }
        });
        
        JTable watchlistTable = new JTable(watchlistTableModel) {
            // Changes background for the price column depending on the timer
            // and whether the price has fallen or risen
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                
                if (column == 2 && watchlistTableModel.getTimerState(this.convertRowIndexToModel(row))) {
                    IStockItem stock = watchlistTableModel.getWatchlistEntry(row);
                    if (stock.getCurPrice() < stock.getCurPriceOld()) {
                        c.setBackground(Color.RED);
                    } else if (stock.getCurPrice() > stock.getCurPriceOld()) {
                        c.setBackground(Color.GREEN);
                    }
                } else {
                    c.setBackground(null);
                }
                return c;
            }
        };
        
        watchlistTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        watchlistTable.setRowSorter(new TableRowSorter<WatchlistTableModel>(watchlistTableModel));
        JScrollPane scrollPane = new JScrollPane(watchlistTable);
        this.add(scrollPane);
        
        JButton addButton = new JButton("Add new stock");
        this.add(addButton, BorderLayout.SOUTH);
        
        addButton.addActionListener(e -> mainController.openSearchFrame());
        
        // Adds a pop up menu
        JPopupMenu popupMenu = new JPopupMenu();
        
        JMenuItem menuRemove = new JMenuItem("Remove from watchlist");
        JMenuItem menuOpenChart = new JMenuItem("Open chart");
        
        popupMenu.add(menuOpenChart);
        popupMenu.add(menuRemove);

        menuOpenChart.addActionListener(e -> {
            int rowIndex = watchlistTable.getSelectedRow();
            if (rowIndex != -1) {
                // hardcoded column 1, that's where the stock id is
                String stockId = watchlistTable.getValueAt(rowIndex, 1).toString();
                mainController.openChartFrame(stockId);
            }
        });
        
        menuRemove.addActionListener(e -> {
            int rowIndex = watchlistTable.getSelectedRow();
            if (rowIndex != -1) {
                // hardcoded column 1, that's where the stock id is
                String stockId = watchlistTable.getValueAt(rowIndex, 1).toString();
                stockerModel.removeWatchlistEntry(stockId);
            }
        });
        
        // Makes the right click to open the pop up menu also select the row it was clicked on.
        popupMenu.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        int rowAtPoint = watchlistTable.rowAtPoint(SwingUtilities.convertPoint(popupMenu, new Point(0, 0), watchlistTable));
                        if (rowAtPoint > -1) {
                            watchlistTable.setRowSelectionInterval(rowAtPoint, rowAtPoint);
                        }
                    }
                });
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });
        
        watchlistTable.setComponentPopupMenu(popupMenu);
        
        this.setVisible(true);
    }
    
}
