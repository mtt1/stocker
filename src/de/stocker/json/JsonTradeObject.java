package de.stocker.json;

import de.stocker.model.dataWrappers.TradeDataUnit;

/**
 * The Class JsonTradeObject is a wrapper class for new push data delivered by
 * the data provider. The actual data is inside the array of TradeDataUnit
 * objects.
 */
public class JsonTradeObject {

    private TradeDataUnit[] data;
    private String type;

    /**
     * Gets the type of push data, "data" is for a data array.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }
    
    /**
     * Gets the data array containing the push information.
     *
     * @return the data array
     */
    public TradeDataUnit[] getDataArray() {
        return data;
    }
    
}
