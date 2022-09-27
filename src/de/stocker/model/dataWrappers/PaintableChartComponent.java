package de.stocker.model.dataWrappers;

import java.awt.Color;

/**
 * The Class PaintableChartComponent is an abstract super class of all classes
 * whose objects can also be painted on a chart frame. This abstract class
 * provides all common fields.
 * 
 * @author Matthias Rudolph
 */
public abstract class PaintableChartComponent {
    
    /** The name used for displaying the label text. */
    protected String name;
    
    /** The color used to draw. */
    protected Color color;
    
    /** The default amount of candles drawn on a frame. */
    protected final int DEFAULT_CANDLE_DRAW_AMOUNT = 30;
    
    /**
     * Sets the drawing color.
     *
     * @param color the new color
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Gets the drawing color.
     *
     * @return the color
     */
    public Color getColor() {
       return color;
    }

}
