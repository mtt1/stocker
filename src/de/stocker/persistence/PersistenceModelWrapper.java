package de.stocker.persistence;

import de.stocker.preferences.PreferencesModel;

/**
 * The Class PersistenceModelWrapper is a wrapper class gathering all three
 * parts of the application persistence into one model. This allows for
 * convenient saving of all parts of the persistence information to one file.
 * 
 * @author Matthias Rudolph
 */
public class PersistenceModelWrapper {
    
    private PreferencesModel preferencesModel;
    private DataPersistenceModel dataPersistenceModel;
    private FramePersistenceModel framePersistenceModel;

    /**
     * Instantiates a new persistence model wrapper with all three parts of the
     * persistence information: preferences, data and GUI.
     *
     * @param preferencesModel the preferences model
     * @param dataPersistenceModel the data persistence model
     * @param framePersistenceModel the frame persistence model
     */
    public PersistenceModelWrapper(PreferencesModel preferencesModel, DataPersistenceModel dataPersistenceModel,
            FramePersistenceModel framePersistenceModel) {
        this.preferencesModel = preferencesModel;
        this.dataPersistenceModel = dataPersistenceModel;
        this.framePersistenceModel = framePersistenceModel;
    }

    /**
     * Gets the preferences model.
     *
     * @return the preferences model
     */
    public PreferencesModel getPreferencesModel() {
        return preferencesModel;
    }
    
    /**
     * Gets the data persistence model.
     *
     * @return the data persistence model
     */
    public DataPersistenceModel getDataPersistenceModel() {
        return dataPersistenceModel;
    }

    /**
     * Gets the frame persistence model.
     *
     * @return the frame persistence model
     */
    public FramePersistenceModel getFramePersistenceModel() {
        return framePersistenceModel;
    }

}
