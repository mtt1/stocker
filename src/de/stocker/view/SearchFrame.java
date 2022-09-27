package de.stocker.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;

import de.stocker.common.*;
import de.stocker.model.SearchResultTableModel;

/**
 * The Class SearchFrame shows the search frame, allows the user to search and
 * to interact with the results.
 */
public class SearchFrame extends BaseInternalFrame {
    
    private IMainController mainController;
    private IStockerModel stockerModel;
    private SearchResultTableModel searchResultTableModel;
    
    private JTextField searchField;
    
    /**
     * Instantiates a new search frame with additional information which is used
     * to restore a search frame from the persistence file after restart.
     *
     * @param mainController the main controller
     * @param stockerModel the stocker model
     * @param width the width
     * @param height the height
     * @param xPos the x position
     * @param yPos the y position
     */
    public SearchFrame(IMainController mainController, IStockerModel stockerModel,
            int width, int height, int xPos, int yPos) {
        this(mainController, stockerModel);
        this.setSize(new DimensionUIResource(width, height));
        this.setLocation(xPos, yPos);
    }

    /**
     * Instantiates a new search frame, pulling the table model from the data model.
     *
     * @param mainController the main controller
     * @param stockerModel the stocker model
     */
    public SearchFrame(IMainController mainController, IStockerModel stockerModel) {
        this.mainController = mainController;
        this.stockerModel = stockerModel;
        this.searchResultTableModel = stockerModel.getSearchResultTableModel();
        
        this.frameType = EFrameType.SEARCH;
        
        title = "Stock search";
        
        createAndShowGUI();
    }
    
    /**
     * Creates and shows the GUI, with the text field for user input and a table
     * for the results. Also adds the button functionality to reach other parts
     * of the application from the search.
     */
    @Override
    protected void createAndShowGUI() {
        super.createAndShowGUI();
        
        this.setTitle(title);
        this.setPreferredSize(new DimensionUIResource(500, 300));
        this.setLayout(new BorderLayout());
        this.setSize(600, 300);
        this.setLocation(400, 200);

        // Search pane
        JLabel searchLabel = new JLabel("Search for: ");
        searchField = new JTextField(30);
        JButton searchButton = new JButton("Search");
        JPanel searchPanel = new JPanel();
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        this.add(searchPanel, BorderLayout.NORTH);
        
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchString = searchField.getText().strip();
                if (!searchString.isBlank()) {
                    search(searchString);
                }
            }
        });
        
        // Results pane
        JTable searchResultTable = new JTable(searchResultTableModel);
        searchResultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(searchResultTable);
        this.add(scrollPane);
       
        // Set the return key to activate the search button
        this.getRootPane().setDefaultButton(searchButton);
        
        JButton watchlistButton = new JButton("Add to watchlist");
        JButton chartButton = new JButton("Open chart");
        JButton clearButton = new JButton("Clear search results");
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(watchlistButton);
        bottomPanel.add(chartButton);
        bottomPanel.add(clearButton);
        this.add(bottomPanel, BorderLayout.SOUTH);
        
        // add a watchlist entry from the search
        watchlistButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int rowIndex = searchResultTable.getSelectedRow();
                if (rowIndex != -1) {
                    int rowModelIndex = searchResultTable.convertRowIndexToModel(rowIndex);
                    String stockId = searchResultTable.getValueAt(rowModelIndex, 1).toString();
                    addWatchlistEntry(stockId);
                    mainController.openWatchlistFrame();
                }
            }
        });
        
        // open a chart from the search
        chartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int rowIndex = searchResultTable.getSelectedRow();
                if (rowIndex != -1) {
                    int rowModelIndex = searchResultTable.convertRowIndexToModel(rowIndex);
                    String stockId = searchResultTable.getValueAt(rowModelIndex, 1).toString();
                    // Use a swing worker to avoid hanging GUI
                    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() {
                            mainController.openChartFrame(stockId);
                            return null;
                        }
                    };
                    worker.execute();
                }
            }
        });
        
        // clears the search results
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stockerModel.clearSearchResults();
            }
        });
        
        this.setVisible(true);
    }
    
    
    /**
     * Executes the search using a swing worker to do the data fetching in the
     * background in order to not slow down the GUI.
     *
     * @param searchString the search string
     */
    private void search(String searchString) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                stockerModel.triggerSearch(searchString);
                return null;
            }
        };
        worker.execute();
    }
    
    /**
     * Adds a watchlist entry for the corresponding stock.
     *
     * @param stockId the stock id
     */
    private void addWatchlistEntry(String stockId) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                stockerModel.addWatchlistEntry(stockId);
                return null;
            }
        };
        worker.execute();
    }
    
}
