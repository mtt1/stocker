package de.stocker.common;

/**
 * The Enum EChartResolution used for data fetching and displaying the correct
 * data for the requested interval. Intervals follow finnhub and
 * Kursdatengenerator defaults.
 * 
 * @author Matthias Rudolph
 */
public enum EChartResolution {
    
    /** The one. */
    ONE("1", "1 min"),
    
    /** The five. */
    FIVE("5", "5 min"),
    
    /** The fifteen. */
    FIFTEEN("15", "15 min"),
    
    /** The thirty. */
    THIRTY("30", "30 min"),
    
    /** The sixty. */
    SIXTY("60", "60 min"),
    
    /** The day. */
    DAY("D", "Day"),
    
    /** The week. */
    WEEK("W", "Week"),
    
    /** The month. */
    MONTH("M", "Month");
    
    private String urlString;
    private String windowTitleString;

    /**
     * Instantiates a new chart resolution enum.
     *
     * @param urlString the URL string used for API requests.
     * @param windowTitleString the window title string used to display the
     * resolution in a human readable form.
     */
    EChartResolution(String urlString, String windowTitleString) {
        this.urlString = urlString;
        this.windowTitleString = windowTitleString;
    }
    
    /**
     * Gets the url string used for API requests.
     *
     * @return the URL string
     */
    public String getUrlString() {
        return urlString;
    }
    
    /**
     * Gets the window title string used to display the resolution in a human
     * readable form.
     *
     * @return the window title string
     */
    public String getWindowTitleString() {
        return windowTitleString;
    }
}
