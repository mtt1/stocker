package de.stocker.common;

/**
 * The Enum EFrameType to store the type of internal frames. Used storing the 
 * persistence and for restoring the GUI after a restart.
 * 
 * @author Matthias Rudolph
 */
public enum EFrameType {
    
    /** Chart frame. */
    CHART,
    
    /** Watchlist frame. */
    WATCHLIST,
    
    /** Search frame. */
    SEARCH
}
