package de.stocker.common;

import javax.swing.JInternalFrame;

import de.stocker.model.dataWrappers.DataProvider;
import de.stocker.preferences.PreferencesModel;

/**
 * The Interface IMainController is implemented by the main controller of the
 * stocker program and allows the separate functional units to communicate with
 * each other according to the mediator pattern.
 * 
 * @author Matthias Rudolph
 */
public interface IMainController {
    
    /**
     * Adds an internal frame to the desktop pane of the main frame.
     *
     * @param iFrame the internal frame
     */
    void addToMainFrame(JInternalFrame iFrame);
    
    /**
     * Opens a new chart frame for a specified stock.
     *
     * @param stockId the stock id
     */
    void openChartFrame(String stockId);
    
    /**
     * Opens the search frame.
     */
    void openSearchFrame();
    
    /**
     * Opens the watchlist frame.
     */
    void openWatchlistFrame();
    
    /**
     * Opens the preferences frame.
     */
    void openPreferencesFrame();
    
    /**
     * Notifies all frames of new minimum sizes after preferences have been set.
     *
     */
    void notifyFramesOfMinSize();
    
    /**
     * Updates data provider after a preferences change.
     *
     * @param dataProvider the data provider
     */
    void updateDataProvider(DataProvider dataProvider);
    
    /**
     * Saves all persistence information to file.
     */
    void savePersistence();
    
    /**
     * Gets the current preferences model with all currently active preferences.
     *
     * @return the preferences model
     */
    PreferencesModel getPreferences();
    
    /**
     * Gets the current data base model.
     *
     * @return the data base model, called stocker model
     */
    IStockerModel getStockerModel();
    
    /**
     * Gets all internal frames currently added to the main frame, used for saving the current state of the application
     *
     * @return the internal frames
     */
    JInternalFrame[] getIFrames();

    /**
     * Saves the last position of the watchlist frame to restore the watchlist
     * to that position after re-opening the frame.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    void saveWatchlistLocation(int x, int y);

    /**
     * Resets the application: clears all frames from the desktop pane and
     * resets the data model to an empty state. Preference changes are preserved
     * and can be reset separately.
     */
    void resetApplication();
}
