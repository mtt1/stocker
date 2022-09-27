package de.stocker.json;

/**
 * The Class JsonSearchResult contains the information of a single search result
 * delivered by the data provider.
 * 
 * @author Matthias Rudolph
 */
public class JsonSearchResult {

    private String description;
    private String displaySymbol;
    private String symbol;
    private String type;

    /**
     * Gets the description string.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the display symbol, used as display format of the unique stock id.
     *
     * @return the display symbol
     */
    public String getDisplaySymbol() {
        return displaySymbol;
    }

    /**
     * Gets the symbol, used as unique stock id in this program.
     *
     * @return the symbol
     */
    public String getSymbol() {
        return symbol;
    }

}
