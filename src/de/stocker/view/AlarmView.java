package de.stocker.view;

import javax.swing.*;

import de.stocker.common.IStockItem;
import de.stocker.model.dataWrappers.AlarmUnit;

/**
 * The Class AlarmView offers a single method to show a dialog when a stock
 * alarm is fired because a threshold has been crossed. The dialog contains
 * details about the stock and the alarm.
 * 
 * @author Matthias Rudolph
 */
public class AlarmView {

    /**
     * Shows the dialog for the alarm that is fired
     *
     * @param stockItem the stock item
     * @param alarmUnit the alarm unit
     */
    public void showAlarmFiredDialog(IStockItem stockItem, AlarmUnit alarmUnit) {
        String alarmMessage;

        switch (alarmUnit.getPosition()) {
        case ALARM_ADDED_WHEN_PRICE_ABOVE_THRESHOLD:
            alarmMessage = "The price of stock " + stockItem.getDescription()
                    + " has fallen below the threshold of "
                    + String.valueOf(alarmUnit.getThreshold() + ".");
            break;
        case ALARM_ADDED_WHEN_PRICE_BELOW_THRESHOLD:
            alarmMessage = "The price of stock " + stockItem.getDescription()
                    + " has risen above the threshold of "
                    + String.valueOf(alarmUnit.getThreshold() + ".");
            break;
        default:
            alarmMessage = "";
            break;
        }

        JOptionPane.showMessageDialog(null, alarmMessage, "Alarm: " + stockItem.getStockId(), JOptionPane.INFORMATION_MESSAGE);
    }
}
