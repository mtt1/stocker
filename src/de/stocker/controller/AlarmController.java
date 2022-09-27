package de.stocker.controller;

import java.util.*;

import de.stocker.common.*;
import de.stocker.model.dataWrappers.AlarmUnit;
import de.stocker.view.AlarmView;

/**
 * The Class AlarmController, implements both IAlarmListener and IStockListener.
 * The alarm controller is registered as an alarm listener with the model to
 * get informed when new alarms are added and registers itself as a stock
 * listener with every stock for which an alarm is added to receive price
 * updates.
 * 
 * @author Matthias Rudolph
 */
public class AlarmController implements IAlarmListener, IStockListener {
    
    private AlarmView alarmView;
    
    /**
     * Instantiates a new alarm controller, initializing the  alarm view
     * responsible for showing a pop-up dialog when a alarm is fired.
     */
    public AlarmController() {
        alarmView = new AlarmView();
    }

    /**
     * Private method called by the alarm controller itself to fire an alarm
     * after the stock data has been updated. Calls the alarm view to display a
     * pop-up message and removes the alarm after it has been fired.
     *
     * @param stockItem the stock item for which the alarm is fired. Used to
     * display details about the stock in the pop-up message.
     * @param alarmUnit the alarm unit. Used to display details about the alarm
     * in the pop-up message.
     */
    private void fireAlarm(IStockItem stockItem, AlarmUnit alarmUnit) {
        alarmView.showAlarmFiredDialog(stockItem, alarmUnit);
        stockItem.removeAlarm(alarmUnit.getThreshold());
    }

    /**
     * Called by the model when an alarm is added.
     *
     * @param stockItem the stock item for which the alarm was added
     */
    @Override
    public void alarmAdded(IStockItem stockItem) {
        stockItem.addStockListener(this);
    }

    /**
     * Interface method from IStockListener, called by stock when stock data
     * has been updated. Used here to determine if an alarm threshold has been
     * crossed.
     *
     * @param stockItem the stock item for which the stock data has been updated
     */
    @Override
    public void stockDataUpdated(IStockItem stockItem) {
        Set<AlarmUnit> alarmUnits = stockItem.getAlarmUnits();
        double curPrice = stockItem.getCurPrice();
        if (alarmUnits != null && curPrice != 0) {
            for (AlarmUnit alarmUnit : alarmUnits) {
                double threshold = alarmUnit.getThreshold();
                EAlarmPos position = alarmUnit.getPosition();

                switch (position) {
                case ALARM_ADDED_WHEN_PRICE_ABOVE_THRESHOLD:
                    if (curPrice <= threshold) {
                        fireAlarm(stockItem, alarmUnit);
                    }
                    break;
                case ALARM_ADDED_WHEN_PRICE_BELOW_THRESHOLD:
                    if (curPrice >= threshold) {
                        fireAlarm(stockItem, alarmUnit);
                    }
                    break;
                default:
                    break;
                }
            }
        }
    }

}
