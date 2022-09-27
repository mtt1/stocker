package de.stocker.common;

import javax.swing.JInternalFrame;

import de.stocker.model.dataWrappers.DataProvider;
import de.stocker.preferences.PreferencesModel;

/**
 * The Interface IPreferencesController offers the abstract methods to handle
 * preference changes and to get information from the preference controller.
 * 
 * @author Matthias Rudolph
 */
public interface IPreferencesController {

    /**
     * Actions to be executed when the preferences have been changed. Notifying
     * all frames of new minimum sizes, setting the data provider, saving the
     * preferences to disk.
     */
    void onPreferencesChanged();

    /**
     * Gets the preferences frame.
     *
     * @return the JInternalFrame displaying the preferences
     */
    JInternalFrame getPreferencesFrame();

    /**
     * Gets the active data provider.
     *
     * @return the active data provider
     */
    DataProvider getActiveDataProvider();

    /**
     * Gets the preferences model.
     *
     * @return the preferences model
     */
    PreferencesModel getPreferencesModel();

}