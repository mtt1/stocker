package de.stocker.preferences;

import java.util.List;

import javax.swing.JInternalFrame;

import de.stocker.common.*;
import de.stocker.model.dataWrappers.DataProvider;

/**
 * The PreferencesController handles the coordination between the user
 * preferences and the different parts of the application so that every part
 * knows the preferences that are relevant for it.
 * 
 * @author Matthias Rudolph
 */
public class PreferencesController implements IPreferencesController {
    
    private IMainController mainController;
    private PreferencesModel preferencesModel;
    
    /**
     * Instantiates a new preferences controller. The constructor checks if it
     * has been given a filled-in preferences model (from file persistence) and
     * if not it generates a new preferences model and sets the default values.
     *
     * @param mainController the main controller
     * @param preferencesModel the preferences model
     */
    public PreferencesController(IMainController mainController, PreferencesModel preferencesModel) {
        this.mainController = mainController;
        
        if (preferencesModel != null) {
            this.preferencesModel = preferencesModel;
        } else {
            this.preferencesModel = new PreferencesModel();
            this.preferencesModel.setDefaults();
        }

        checkForApiKeys();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onPreferencesChanged() {
        // Notify all existing frames of new min sizes
        mainController.notifyFramesOfMinSize();
        
        // Set the data provider
        mainController.updateDataProvider(getActiveDataProvider());
        
        mainController.savePersistence();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public JInternalFrame getPreferencesFrame() {
        return new PreferencesFrame(this, preferencesModel);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public DataProvider getActiveDataProvider() {
        int index = preferencesModel.getActiveDataProvider();
        DataProvider p = preferencesModel.getDataProviders().get(index);
        return p;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public PreferencesModel getPreferencesModel() {
        return preferencesModel;
    }
    
    
    /**
     * Checks if there are API keys for all data providers. The default
     * preferences settings pre-configures two data providers but without valid
     * keys. These need to be added at start-up.
     */
    private void checkForApiKeys() {
        List<DataProvider> dataProviders = preferencesModel.getDataProviders();
        for (DataProvider dataProvider : dataProviders) {
            if (dataProvider.getApiKey() == null || dataProvider.getApiKey().isBlank()) {
                new EnterApiKeyDialog(dataProvider);
            }
        }
    }

}