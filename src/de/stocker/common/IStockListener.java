package de.stocker.common;

/**
 * The listener interface for receiving notifications when
 * the price and data of a stock has been updated.
 *
 * @author Matthias Rudolph
 */
public interface IStockListener {
    
    /**
     * Invoked when the stock data has been updated.
     *
     * @param stockItem the stock item
     */
    void stockDataUpdated(IStockItem stockItem);

}
