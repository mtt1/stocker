package de.stocker.controller;

import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;

import de.stocker.common.*;
import de.stocker.model.StockerModel;
import de.stocker.model.dataWrappers.DataProvider;
import de.stocker.network.NetworkController;
import de.stocker.persistence.PersistenceController;
import de.stocker.preferences.*;
import de.stocker.view.*;

/**
 * The Class MainController implements the interface IMainController to allow
 * the separate functional units to communicate with each other according to the
 * mediator pattern.
 * 
 * @author Matthias Rudolph
 */
public class MainController implements IMainController {
    
    private INetworkController networkController;
    private IStockerModel stockerModel;
    
    private IPreferencesController preferencesController;
    private PreferencesModel preferencesModel;
    
    private MainFrame mainFrame;
    
    private PersistenceController persistenceController;
    
    private AlarmController alarmController;
    
    // used to remember the last location of the watchlist frame
    private int watchlistX = 0;
    private int watchlistY = 0;
    
    /**
     * Instantiates a new main controller. The main controller generates and
     * connects all the different parts of the application in its constructor:
     * Main frame, persistence, preferences, network, data model, alarms.
     */
    public MainController() {
        this.mainFrame = new MainFrame(this);
        
        this.persistenceController = new PersistenceController(this);
        persistenceController.rebuildAfterRestart();
        
        this.preferencesController = new PreferencesController(this, persistenceController.getPreferencesModel());
        this.preferencesModel = preferencesController.getPreferencesModel();
        
        this.networkController = new NetworkController(preferencesController.getActiveDataProvider());

        this.stockerModel = new StockerModel(networkController, preferencesModel);
        
        this.alarmController = new AlarmController();
        getStockerModel().setAlarmListener(alarmController);
        
        persistenceController.rebuildDataModel();
        
        persistenceController.rebuildUI();
        notifyFramesOfMinSize();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addToMainFrame(JInternalFrame iFrame) {
        mainFrame.addToMainFrame(iFrame);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void openChartFrame(String stockId) {
        SwingUtilities.invokeLater(() -> {
            ChartFrame chartFrame = new ChartFrame(getStockerModel(), stockId, preferencesModel.getChartResolution(),
                    preferencesModel.getChartType(), preferencesModel.getColorAlarm(),
                    preferencesModel.getColorMovingAvg(), preferencesModel.getColorBollinger());
            DimensionUIResource minSize = new DimensionUIResource(preferencesModel.getMinWidthChartFrame(),
                    preferencesModel.getMinHeightChartFrame());
            chartFrame.setMinimumSize(minSize);

            addToMainFrame(chartFrame);

            // stagger the frames locations to avoid complete overlap
            chartFrame.setLocation(chartFrame.getDesktopPane().getWidth() - chartFrame.getWidth() - (chartFrame.getXOffset() * chartFrame.getOpenFrameCount()), chartFrame.getYOffset() * chartFrame.getOpenFrameCount());
            mainFrame.bringToFront(chartFrame);
        });                
    }    
    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void openSearchFrame() {
        // Checks if the search frame is already open, then bring it to the
        // front. Otherwise, open a new one.
        JInternalFrame[] frames = mainFrame.getIFrames();
        for (JInternalFrame f : frames) {
            if (f instanceof SearchFrame) {
                mainFrame.bringToFront(f);
                return;
            }
        }

        SwingUtilities.invokeLater(() -> {
            SearchFrame searchFrame = new SearchFrame(MainController.this, stockerModel);
            DimensionUIResource minSize = new DimensionUIResource(preferencesModel.getMinWidthSearchFrame(),
                    preferencesModel.getMinHeightSearchFrame());
            searchFrame.setMinimumSize(minSize);

            addToMainFrame(searchFrame);
            mainFrame.bringToFront(searchFrame);
        });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void openWatchlistFrame() {
        // Checks if the watchlist frame is already open, then bring it to the
        // front. Otherwise, open a new one.
        JInternalFrame[] frames = mainFrame.getIFrames();
        for (JInternalFrame f : frames) {
            if (f instanceof WatchlistFrame) {
                mainFrame.bringToFront(f);
                return;
            }
        }

        SwingUtilities.invokeLater(() -> {
            WatchlistFrame watchlistFrame = new WatchlistFrame(MainController.this, stockerModel);
            DimensionUIResource minSize = new DimensionUIResource(preferencesModel.getMinWidthWatchlistFrame(),
                    preferencesModel.getMinHeightWatchlistFrame());
            watchlistFrame.setMinimumSize(minSize);

            addToMainFrame(watchlistFrame);
            watchlistFrame.setLocation(watchlistX, watchlistY);
            mainFrame.bringToFront(watchlistFrame);
        });
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void openPreferencesFrame() {
        // Checks if the preferences frame is already open, then bring it to the
        // front. Otherwise, open a new one.
        JInternalFrame[] frames = mainFrame.getIFrames();
        for (JInternalFrame f : frames) {
            if (f instanceof PreferencesFrame) {
                mainFrame.bringToFront(f);
                return;
            }
        }
        SwingUtilities.invokeLater(() -> addToMainFrame(preferencesController.getPreferencesFrame()));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyFramesOfMinSize() {
        JInternalFrame[] frames = mainFrame.getIFrames();
        int minW, minH;

        for (JInternalFrame f : frames) {
            if (f instanceof ChartFrame) {
                minW = preferencesModel.getMinWidthChartFrame();
                minH = preferencesModel.getMinHeightChartFrame();
                DimensionUIResource minSize = new DimensionUIResource(minW, minH);
                f.setMinimumSize(minSize);
                if (f.getWidth() < minW || f.getHeight() < minH) {
                    f.setSize(minSize);
                    f.revalidate();
                    f.repaint();
                }
            }

            if (f instanceof SearchFrame) {
                minW = preferencesModel.getMinWidthSearchFrame();
                minH = preferencesModel.getMinHeightSearchFrame();
                DimensionUIResource minSize = new DimensionUIResource(minW, minH);
                f.setMinimumSize(minSize);
                if (f.getWidth() < minW || f.getHeight() < minH) {
                    f.setSize(minSize);
                    f.revalidate();
                    f.repaint();
                }
            }

            if (f instanceof WatchlistFrame) {
                minW = preferencesModel.getMinWidthWatchlistFrame();
                minH = preferencesModel.getMinHeightWatchlistFrame();
                DimensionUIResource minSize = new DimensionUIResource(minW, minH);
                f.setMinimumSize(minSize);
                if (f.getWidth() < minW || f.getHeight() < minH) {
                    f.setSize(minSize);
                    f.revalidate();
                    f.repaint();
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateDataProvider(DataProvider dataProvider) {
        
        SwingWorker<Void, Void> connectionWorker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                System.out.println("Data provider changed.");
                
                savePersistence();
                networkController.setActiveDataProvider(dataProvider);
                resetApplication();
                
                persistenceController.rebuildAfterRestart();
                persistenceController.rebuildDataModel();
                persistenceController.rebuildUI();

                return null;
            }
        };

        DataProvider currDP = networkController.getActiveDataProvider();
        boolean name = currDP.getName().equals(dataProvider.getName());
        boolean apiKey = currDP.getApiKey().equals(dataProvider.getApiKey());
        boolean pull = currDP.getPullUrl().equals(dataProvider.getPullUrl());
        boolean push = currDP.getPushUrl().equals(dataProvider.getPushUrl());

        // execute the provider change only if the provider has actually changed
        if (!(name && apiKey && pull && push)) {
            connectionWorker.execute();
        }
    }      
      

    /**
     * {@inheritDoc}
     */
    @Override
    public PreferencesModel getPreferences() {
        return preferencesController.getPreferencesModel();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void savePersistence() {
        persistenceController.savePersistenceToFile();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public IStockerModel getStockerModel() {
        return stockerModel;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public JInternalFrame[] getIFrames() {
        return mainFrame.getIFrames();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveWatchlistLocation(int x, int y) {
        watchlistX = x;
        watchlistY = y;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void resetApplication() {
        stockerModel.resetModel();
        mainFrame.resetDesktop();
    }
    
}
