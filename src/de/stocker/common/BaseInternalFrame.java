package de.stocker.common;

import javax.swing.JInternalFrame;

/**
 * The Class BaseInternalFrame provides all shared settings for the internal
 * frame sub classes used in the Stocker application.
 * 
 * @author Matthias Rudolph
 */
public abstract class BaseInternalFrame extends JInternalFrame {
    
    protected EFrameType frameType;

    protected final Boolean resizable = true;
    protected final Boolean closable = true; 
    protected final Boolean maximizable = false; 
    protected final Boolean iconifiable = true; 
    
    /**
     * Sets the GUI properties shared by all internal frames.
     */
    protected void createAndShowGUI() {
        this.setResizable(resizable);
        this.setClosable(closable);
        this.setMaximizable(maximizable);
        this.setIconifiable(iconifiable);
    }
    
    /**
     * Gets the frame type.
     *
     * @return the frame type
     */
    public EFrameType getFrameType() {
        return frameType;
    }

}
