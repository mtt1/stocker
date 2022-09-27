package de.stocker.persistence;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Set;

import de.stocker.common.*;
import de.stocker.json.JsonFactory;
import de.stocker.model.dataWrappers.*;
import de.stocker.preferences.PreferencesModel;
import de.stocker.view.*;

/**
 * The Class PersistenceController is responsible for writing the persistence
 * models to disk and for restoring the application state from the models when
 * they are read from file.
 * 
 * @author Matthias Rudolph
 */
public class PersistenceController {
    
    private DataPersistenceModel dataPersistenceModel;
    private FramePersistenceModel framePersistenceModel;
    private PreferencesModel preferencesModel;
    private PersistenceModelWrapper persistenceModelWrapper;
    
    private IMainController mainController;

    // file name
    private final String persistenceFileName = "./stocker_3266494.json";
    private final Path persistenceFilePath = Paths.get(persistenceFileName);
    
    /**
     * Instantiates a new persistence controller, passing a reference to the
     * main controller which is needed for restoring the application state.
     *
     * @param mainController the main controller
     */
    public PersistenceController(IMainController mainController) {
        this.mainController = mainController;
    }
    
    /**
     * Saves the persistence models to file.
     */
    public void savePersistenceToFile() {
        PreferencesModel pM = mainController.getPreferences();
        DataPersistenceModel dPM = new DataPersistenceModel(mainController.getStockerModel());
        FramePersistenceModel fPM = new FramePersistenceModel(mainController.getIFrames());
        
        PersistenceModelWrapper preferencesModelWrapper = new PersistenceModelWrapper(pM, dPM, fPM);
        String persistenceJson = JsonFactory.objectToJson(preferencesModelWrapper);
        
        try {
            Files.writeString(persistenceFilePath, persistenceJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    /**
     * Reads the persistence models from file.
     */
    public void readModelsFromFile() {
        try {
            if (Files.exists(persistenceFilePath) && Files.isRegularFile(persistenceFilePath)) {
                String jsonString = Files.readString(persistenceFilePath);
                persistenceModelWrapper = JsonFactory.jsonToObject(jsonString, PersistenceModelWrapper.class);
                preferencesModel = persistenceModelWrapper.getPreferencesModel();
                dataPersistenceModel = persistenceModelWrapper.getDataPersistenceModel();
                framePersistenceModel = persistenceModelWrapper.getFramePersistenceModel();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Rebuilds the data model.
     */
    public void rebuildDataModel() {
        IStockerModel stockerModel = mainController.getStockerModel();
        if (dataPersistenceModel != null) {
            String[] activeStocks = dataPersistenceModel.getActiveStockIds();
            String[] watchlistStocks = dataPersistenceModel.getWatchlistStockIds();
            Set<AlarmUnit> alarmUnits = dataPersistenceModel.getAlarmUnits();

            // Add all active stocks back into the data base model
            if (activeStocks != null) {
                for (String s : activeStocks) {
                    stockerModel.addStock(s);
                }
            }

            // Rebuild the watchlist
            if (watchlistStocks != null) {
                for (String s : watchlistStocks) {
                    stockerModel.addWatchlistEntry(s);
                }
            }

            // Re-add all alarms
            if (alarmUnits != null) {
                for (AlarmUnit a : alarmUnits) {
                    stockerModel.addAlarm(a.getStockId(), a.getThreshold(), a.getColor());
                }
            }
        }
    }
    
    /**
     * Rebuild after restart.
     */
    public void rebuildAfterRestart() {
        readModelsFromFile();
    }
    
    /**
     * Rebuilds the UI from the frame persistence model.
     */
    public void rebuildUI() {
        if (framePersistenceModel != null && !framePersistenceModel.getOpenFrames().isEmpty()) {
            IStockerModel stockerModel = mainController.getStockerModel();

            BaseInternalFrame bif = null;
            List<OpenFrame> openFrames = framePersistenceModel.getOpenFrames();
            for (OpenFrame f : openFrames) {
                switch (f.getFrameType()) {
                case SEARCH:
                    bif = new SearchFrame(mainController, stockerModel);
                    break;
                case WATCHLIST:
                    bif = new WatchlistFrame(mainController, stockerModel);
                    break;
                // Handle all the additional information needed to restore a
                // chart frame
                case CHART:
                    String stockId = f.getStockId();
                    EChartResolution chartResolution = f.getResolution();
                    EChartType chartType = f.getChartType();
                    Color alarmColor = f.getAlarmColor();
                    Color movingAvgColor = f.getMovingAvgColor();
                    Color bollingerColor = f.getBollingerColor();

                    List<SimpleMovingAverage> movingAvgs = f.getMovingAvgs();
                    List<BollingerBand> bollingers = f.getBollingers();

                    if (stockId != null) {
                        bif = new ChartFrame(stockerModel, stockId,
                                chartResolution, chartType, alarmColor,
                                movingAvgColor, bollingerColor);

                        if (movingAvgs != null) {
                            for (SimpleMovingAverage a : movingAvgs) {
                                int n = a.getN();
                                Color c = a.getColor();
                                ((ChartFrame) bif).addMovingAvg(n, c);
                            }
                        }

                        if (bollingers != null) {
                            for (BollingerBand b : bollingers) {
                                double ff = b.getF();
                                int n = b.getN();
                                Color c = b.getColor();
                                ((ChartFrame) bif).addBollingerBand(ff, n, c);
                            }
                        }
                    }
                    break;
                default:
                    break;
                }
                if (bif != null) {
                    bif.setSize(f.getWidth(), f.getHeight());
                    bif.setLocation(f.getXPosition(), f.getYPosition());
                    mainController.addToMainFrame(bif);
                }
            }
        } else {
            // Default to opening the watchlist if no other frames have been persisted
            mainController.openWatchlistFrame();
        }
    }

    /**
     * Gets the preferences model.
     *
     * @return the preferences model
     */
    public PreferencesModel getPreferencesModel() {
        return preferencesModel;
    }

}
