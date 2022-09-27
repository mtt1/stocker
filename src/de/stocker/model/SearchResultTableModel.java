package de.stocker.model;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import de.stocker.model.dataWrappers.SearchResult;

/**
 * The Class SearchResultTableModel extends AbstractTableModel to display
 * information about the results of a search request specifically in the format
 * to display them in a search frame.
 * 
 * @author Matthias Rudolph
 */
public class SearchResultTableModel extends AbstractTableModel {
    
    private String[] columnNames = { "Name", "Stock ID" };
    
    private List<SearchResult> searchResults;
    
    /**
     * Instantiates a new search result table model for the provided list of
     * search results.
     *
     * @param searchResults the list of search result objects
     */
    public SearchResultTableModel(List<SearchResult> searchResults) {
        this.searchResults = searchResults;
    }

    /**
     * Gets the number of rows in the table model.
     *
     * @return the row count
     */
    @Override
    public int getRowCount() {
        return searchResults.size();
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
     * Gets the name of the specified column.
     *
     * @param column the column
     * @return the column name
     */
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    /**
     * Gets the class of objects contained in the specified column.
     *
     * @param columnIndex the column index
     * @return String Class, because only Strings are contained in this table
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
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
        SearchResult sr = searchResults.get(rowIndex);
        switch (columnIndex) {
        case 0:
            return sr.getName();
        case 1:
            return sr.getStockId();
        default:
            return null;
        }
    }
    
    /**
     * Gets the search result at the specified row index in the model.
     *
     * @param rowIndex the row index
     * @return the search result
     */
    public SearchResult getSearchResult(int rowIndex) {
        return searchResults.get(rowIndex);
    }
    
}
