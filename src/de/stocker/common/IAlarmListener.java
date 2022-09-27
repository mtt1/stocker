package de.stocker.common;

/**
 * The listener interface for the method being called when a new alarm is added
 * in the model. The class implementing this interface is responsible for
 * handling and firing alarms.
 *
 * @author Matthias Rudolph
 */
public interface IAlarmListener {

    /**
     * Called by the model when an alarm was added.
     *
     * @param stockItem the stock item for which the alarm was added
     */
    void alarmAdded(IStockItem stockItem);
    
}
