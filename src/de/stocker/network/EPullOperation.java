package de.stocker.network;

/**
 * The Enum EPullOperation enumerates all possible API requests/HTTP pull
 * operations and their corresponding URL paths.
 * 
 * @author Matthias Rudolph
 */
public enum EPullOperation {
    
    /** The candle request. */
    CANDLE("/stock/candle"),
    
    /** The quote request. */
    QUOTE("/quote"),
    
    /** The search request. */
    SEARCH("/search");  
    
    private String path;
    
    private EPullOperation(String path) {
        this.path = path;
    }
    
    /**
     * Gets the path that has to be appended to the API URL for the selected
     * operation type.
     *
     * @return the path
     */
    public String getPath() {
        return path;
    }

}