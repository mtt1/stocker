package de.stocker.preferences;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import de.stocker.model.dataWrappers.DataProvider;

/**
 * The Class DataProvTableModel extends AbstractTableModel to display
 * information about the available data providers in the preferences dialog.
 * 
 * @author Matthias Rudolph
 */
public class DataProvTableModel extends AbstractTableModel {
    
    private String[] columnNames = { "Active", "Name", "API key", "Pull URL", "Push URL" };
    
    private List<DataProvider> dataProviders;
    private int activeDataProv;
    
    /**
     * Instantiates a new data provider table model.
     *
     * @param dataProviders the list of data providers
     * @param activeDataProv the index of the active data provider
     */
    public DataProvTableModel(List<DataProvider> dataProviders, int activeDataProv) {
        this.dataProviders = dataProviders;
        this.activeDataProv = activeDataProv;
    }

    /**
     * Gets the number of rows in the table model.
     *
     * @return the row count
     */
    @Override
    public int getRowCount() {
        return dataProviders.size();
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
     * @return the column class, Boolean for the first column displaying which
     * data provider is active, String for the rest
     */
    @Override
    public Class<?> getColumnClass(int columnIndex){
        switch (columnIndex) {
        case 0:
            return Boolean.class;
        default:
            return String.class;
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
        DataProvider d = dataProviders.get(rowIndex);
        switch (columnIndex) {
        case 0:
            if (rowIndex == activeDataProv) {
                return true;
            } else {
                return false;
            }
        case 1:
            return d.getName();
        case 2:
            return d.getApiKey();
        case 3:
            return d.getPullUrl();
        case 4:
            return d.getPushUrl();
        default:
            return null;
        }
    }
    
    /**
     * Sets the active data provider.
     *
     * @param activeDataProv the index of the new active data provider
     */
    public void setActiveDataProvider(int activeDataProv) {
        this.activeDataProv = activeDataProv;
    }

}