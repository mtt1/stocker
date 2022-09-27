package de.stocker.json;

/**
 * The Class JsonSearchObject is a wrapper class for the response to a search
 * request delivered by the data provider.
 * 
 * @author Matthias Rudolph
 */
public class JsonSearchObject {
    
    // necessary for de-serializing the json string
    private long count;
    private JsonSearchResult[] result;

    /**
     * Gets array of search result information.
     *
     * @return the result array
     */
    public JsonSearchResult[] getResult() {
        return result;
    }

    /**
     * Filters the result array for a result whose stock id matches the one
     * requested and returns the matching result if available.
     *
     * @param stockId the stock id to filter the search results by
     * @return the matching result if available, null otherwise
     */
    public JsonSearchResult getMatchingResult(String stockId) {
        for (JsonSearchResult jsr : result) {
            if (jsr.getSymbol().equals(stockId)) {
                return jsr;
            }
        }
        return null;
    }


}