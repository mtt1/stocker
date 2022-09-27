package de.stocker.common;

/**
 * The Enum EAlarmPos is used to store if the alarm was when the current stock
 * price is above or below the threshold.
 * 
 * @author Matthias Rudolph
 */
public enum EAlarmPos {
    
    /** State when alarm was added while the price was above the alarm threshold. */
    ALARM_ADDED_WHEN_PRICE_ABOVE_THRESHOLD,
    
    /** State when alarm was added while the price was below the alarm threshold. */
    ALARM_ADDED_WHEN_PRICE_BELOW_THRESHOLD

}
