package de.stocker.model.dataWrappers;

/**
 * The Class SearchResult is a wrapper class containing all information for a
 * single search result parsed from a data provider response.
 * 
 * @author Matthias Rudolph
 */
public class SearchResult {
    
    private String stockId;
    private String name;
    private String displaySymbol;

    /**
     * Instantiates a new search result with the details about a stock.
     *
     * @param stockId the stock id
     * @param name the name
     * @param displaySymbol the display symbol
     */
    public SearchResult(String stockId, String name, String displaySymbol) {
        this.stockId = stockId;
        this.name = name;
        this.displaySymbol = displaySymbol;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the stock id.
     *
     * @return the stock id
     */
    public String getStockId() {
        return stockId;
    }

    /**
     * Gets the display symbol.
     *
     * @return the display symbol
     */
    public String getDisplaySymbol() {
        return displaySymbol;
    }

}
