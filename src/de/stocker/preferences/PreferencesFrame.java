package de.stocker.preferences;

import java.awt.*;

import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.plaf.DimensionUIResource;

import de.stocker.common.*;
import de.stocker.model.dataWrappers.DataProvider;

/**
 * The Class PreferencesFrame displays the preferences dialog allowing the user
 * to change the change application settings.
 * 
 * @author Matthias Rudolph
 */
public class PreferencesFrame extends BaseInternalFrame {
    
    private IPreferencesController preferencesController;
    private PreferencesModel preferencesModel;
    
    private PreferencesTabs preferencesTabs;
    private WindowsTab windowsTab;
    private ChartsTab chartsTab;
    
    /**
     * Instantiates a new preferences frame with references to the preferences
     * controller governing the settings and the preferences model storing all
     * saved settings.
     *
     * @param preferencesController the preferences controller
     * @param preferencesModel the preferences model
     */
    public PreferencesFrame(IPreferencesController preferencesController, PreferencesModel preferencesModel) {
        this.preferencesController = preferencesController;
        this.preferencesModel = preferencesModel;
        
        title = "Preferences";
        
        this.setMinimumSize(new DimensionUIResource(700, 300));
        
        SwingUtilities.invokeLater(() -> createAndShowGUI());

    }
    
    /**
     * Creates the and show GUI.
     */
    @Override
    protected void createAndShowGUI() {
        super.createAndShowGUI();

        this.setTitle(title);
        
        this.setPreferredSize(new DimensionUIResource(600, 350));
        this.setLayout(new BorderLayout());
        this.setSize(700, 350);
        
        //components
        this.add(preferencesTabs = new PreferencesTabs());
        this.add(new ButtonPanel(), BorderLayout.SOUTH);
        
        this.setLocation(400, 200);
        this.setVisible(true);
    }
    
    /**
     * The Class PreferencesTabs.
     */
    class PreferencesTabs extends JTabbedPane {
        
        /**
         * Instantiates a new preferences tabs.
         */
        public PreferencesTabs() {
            generateTabs();
        }
        
        /**
         * Generate tabs.
         */
        public void generateTabs() {
            this.removeAll();
            this.addTab("Windows", windowsTab = new WindowsTab());
            this.addTab("Charts", new JScrollPane(chartsTab = new ChartsTab()));
            this.addTab("Data", new DataTab());
        }
    }
    
    /**
     * The Class WindowsTab.
     */
    class WindowsTab extends JPanel {
        
        private PrefTextField widthChart;
        private PrefTextField heightChart;
        private PrefTextField widthWatchlist;
        private PrefTextField heightWatchlist;
        private PrefTextField widthSearch;
        private PrefTextField heightSearch;
        
        /**
         * Instantiates a new windows tab.
         */
        public WindowsTab() {
            // Charts
            widthChart = new PrefTextField(preferencesModel.getMinWidthChartFrame());
            heightChart = new PrefTextField(preferencesModel.getMinHeightChartFrame());
            JPanel chart = new JPanel();
            chart.add(new WidthLabel());
            chart.add(widthChart);
            chart.add(new HeightLabel());
            chart.add(heightChart);
            chart.setBorder(BorderFactory.createTitledBorder("Chart window"));

            // Watchlist
            widthWatchlist = new PrefTextField(preferencesModel.getMinWidthWatchlistFrame());
            heightWatchlist = new PrefTextField(preferencesModel.getMinHeightWatchlistFrame());
            JPanel watchlist = new JPanel();
            watchlist.add(new WidthLabel());
            watchlist.add(widthWatchlist);
            watchlist.add(new HeightLabel());
            watchlist.add(heightWatchlist);
            watchlist.setBorder(BorderFactory.createTitledBorder("Watchlist window"));

            // Search
            widthSearch = new PrefTextField(preferencesModel.getMinWidthSearchFrame());
            heightSearch = new PrefTextField(preferencesModel.getMinHeightSearchFrame());
            JPanel search = new JPanel();
            search.add(new WidthLabel());
            search.add(widthSearch);
            search.add(new HeightLabel());
            search.add(heightSearch);
            search.setBorder(BorderFactory.createTitledBorder("Search window"));

            this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

            this.add(Box.createRigidArea(new DimensionUIResource(0, 10)));
            this.add(chart);
            this.add(Box.createRigidArea(new DimensionUIResource(0, 10)));
            this.add(watchlist);
            this.add(Box.createRigidArea(new DimensionUIResource(0, 10)));
            this.add(search);
            this.add(Box.createRigidArea(new DimensionUIResource(0, 10)));

            this.setBorder(BorderFactory.createTitledBorder("Set minimum size"));
        }
        
        /**
         * Gets the min width chart frame.
         *
         * @return the min width chart frame
         */
        // Get methods to read out user input
        public int getMinWidthChartFrame() {
            return Integer.parseInt(widthChart.getText());
        }
        
        /**
         * Gets the min height chart frame.
         *
         * @return the min height chart frame
         */
        public int getMinHeightChartFrame() {
            return Integer.parseInt(heightChart.getText());
        }
        
        /**
         * Gets the min width watchlist frame.
         *
         * @return the min width watchlist frame
         */
        public int getMinWidthWatchlistFrame() {
            return Integer.parseInt(widthWatchlist.getText());
        }
        
        /**
         * Gets the min height watchlist frame.
         *
         * @return the min height watchlist frame
         */
        public int getMinHeightWatchlistFrame() {
            return Integer.parseInt(heightWatchlist.getText());
        }
        
        /**
         * Gets the min width search frame.
         *
         * @return the min width search frame
         */
        public int getMinWidthSearchFrame() {
            return Integer.parseInt(widthSearch.getText());
        }
        
        /**
         * Gets the min height search frame.
         *
         * @return the min height search frame
         */
        public int getMinHeightSearchFrame() {
            return Integer.parseInt(heightSearch.getText());
        }
    }
    
    /**
     * The Class ChartsTab.
     */
    class ChartsTab extends JPanel {
        
        private JComboBox<EChartType> typeBox;
        private JComboBox<EChartResolution> resolutionBox;
        private JColorChooser aCC;
        private JColorChooser bbCC;
        private JColorChooser smaCC;

        /**
         * Instantiates a new charts tab.
         */
        public ChartsTab() {
        
            JPanel typePanel = new JPanel();
            JLabel chartTypeLabel = new JLabel("Standard chart type");
            typeBox = new JComboBox<>(EChartType.values());
            typePanel.add(chartTypeLabel);
            typePanel.add(typeBox);

            JPanel resolutionPanel = new JPanel();
            JLabel chartResolutionLabel = new JLabel("Standard chart resolution");
            resolutionBox = new JComboBox<>(EChartResolution.values());
            resolutionPanel.add(chartResolutionLabel);
            resolutionPanel.add(resolutionBox);
            
            JPanel alarmPanel = new JPanel();
            aCC = new JColorChooser(preferencesModel.getColorAlarm());
            AbstractColorChooserPanel[] aPanels = aCC.getChooserPanels();
            for (AbstractColorChooserPanel accp : aPanels) {
               if(!accp.getDisplayName().equals("Swatches")) {
                  aCC.removeChooserPanel(accp);
               } 
            }
            alarmPanel.add(aCC);
            alarmPanel.setBorder(BorderFactory.createTitledBorder("Alarm color"));
            
            JPanel bollingerPanel = new JPanel();
            bbCC = new JColorChooser(preferencesModel.getColorBollinger());
            AbstractColorChooserPanel[] bbPanels = bbCC.getChooserPanels();
            for (AbstractColorChooserPanel accp : bbPanels) {
               if(!accp.getDisplayName().equals("Swatches")) {
                  bbCC.removeChooserPanel(accp);
               } 
            }
            bollingerPanel.add(bbCC);
            bollingerPanel.setBorder(BorderFactory.createTitledBorder("Bollinger Band color"));
            
            JPanel movAvgPanel = new JPanel();
            smaCC = new JColorChooser(preferencesModel.getColorMovingAvg());
            AbstractColorChooserPanel[] smaPanels = smaCC.getChooserPanels();
            for (AbstractColorChooserPanel accp : smaPanels) {
               if(!accp.getDisplayName().equals("Swatches")) {
                  smaCC.removeChooserPanel(accp);
               } 
            }
            movAvgPanel.add(smaCC);
            movAvgPanel.setBorder(BorderFactory.createTitledBorder("Simple Moving Average color"));
            
            typeBox.setSelectedItem(preferencesModel.getChartType());
            resolutionBox.addActionListener(e -> preferencesModel.setChartType(getSelectedChartType()));
            
            resolutionBox.setSelectedItem(preferencesModel.getChartResolution());
            resolutionBox.addActionListener(e -> preferencesModel.setChartResolution(getSelectedChartResolution()));
            
            this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            
            this.add(typePanel);
            this.add(resolutionPanel);
            this.add(alarmPanel);
            this.add(bollingerPanel);
            this.add(movAvgPanel);
        }
        
        /**
         * Gets the selected chart type.
         *
         * @return the selected chart type
         */
        // Get methods to read out user input
        public EChartType getSelectedChartType() {
            return (EChartType) typeBox.getSelectedItem();
        }
        
        /**
         * Gets the selected chart resolution.
         *
         * @return the selected chart resolution
         */
        public EChartResolution getSelectedChartResolution() {
            return (EChartResolution) resolutionBox.getSelectedItem();
        }
        
        /**
         * Gets the alarm color.
         *
         * @return the alarm color
         */
        public Color getAlarmColor() {
            return aCC.getColor();
        }
        
        /**
         * Gets the bollinger color.
         *
         * @return the bollinger color
         */
        public Color getBollingerColor() {
            return bbCC.getColor();
        }
        
        /**
         * Gets the mov avg color.
         *
         * @return the mov avg color
         */
        public Color getMovAvgColor() {
            return smaCC.getColor();
        }
    }
    
    /**
     * The Class DataTab.
     */
    class DataTab extends JPanel {
        
        /**
         * Instantiates a new data tab.
         */
        public DataTab() {
            DataProvTableModel dataTableModel = preferencesModel.getDataTableModel();
            JTable dataTable = new JTable(dataTableModel);
            dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            dataTable.setPreferredScrollableViewportSize(dataTable.getPreferredSize());
            JScrollPane sp = new JScrollPane(dataTable);
            this.add(sp);
            
            JButton bSetActive = new JButton("Set active");
            this.add(bSetActive);
            
            JButton bAddDataProv = new JButton("Add new data provider");
            this.add(bAddDataProv);
            
            bSetActive.addActionListener(e -> {
                int selectedRow = dataTable.getSelectedRow();
                if (selectedRow != -1) {
                    preferencesModel.setActiveDataProvider(selectedRow);
                }
            });
            
            bAddDataProv.addActionListener(e -> showAddDataProvDialog());
        }
    }
    
    private void showAddDataProvDialog() {
        
        JPanel inputPanel = new JPanel();
        JLabel nameLabel = new JLabel("Name: ");
        JTextField nameField = new JTextField(10);
        JLabel keyLabel = new JLabel("API Key: ");
        JTextField keyField = new JTextField(10);
        JLabel pullLabel = new JLabel("Pull URL: ");
        JTextField pullField = new JTextField(10);
        JLabel pushLabel = new JLabel("Push URL: ");
        JTextField pushField = new JTextField(10);
        
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(keyLabel);
        inputPanel.add(keyField);
        inputPanel.add(pullLabel);
        inputPanel.add(pullField);
        inputPanel.add(pushLabel);
        inputPanel.add(pushField);
        
        int option = JOptionPane.showConfirmDialog(this, inputPanel, "Add new data provider", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText().strip();
            String key = keyField.getText().strip();
            String pull = pullField.getText().strip();
            String push = pushField.getText().strip();
            DataProvider p = new DataProvider(name, key, pull, push);
            preferencesModel.addDataProvider(p);
            preferencesModel.getDataTableModel().fireTableDataChanged();
        }
    }
    
    /**
     * The Class WidthLabel.
     */
    class WidthLabel extends JLabel {
        
        /**
         * Instantiates a new width label.
         */
        public WidthLabel() {
            super("Minimum width: ");
        }
    }
    
    /**
     * The Class HeightLabel.
     */
    class HeightLabel extends JLabel {
        
        /**
         * Instantiates a new height label.
         */
        public HeightLabel() {
            super("Minimum height: ");
        }
    }
    
    /**
     * The Class PrefTextField.
     */
    class PrefTextField extends JTextField {
        
        /**
         * Instantiates a new pref text field.
         *
         * @param value the value
         */
        public PrefTextField(int value) {
            super(String.valueOf(value), 5);
            setHorizontalAlignment(JTextField.TRAILING);
            }
    }
    
    /**
     * The Class ButtonPanel.
     */
    class ButtonPanel extends JPanel {
        
        /**
         * Instantiates a new button panel.
         */
        public ButtonPanel() {
            this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            JButton resetButton = new JButton("Reset to defaults");
            JButton applyButton = new JButton("Apply & Close");
            JButton cancelButton = new JButton("Cancel");
            this.add(resetButton);
            this.add(Box.createHorizontalGlue());
            this.add(applyButton);
            this.add(cancelButton);

            resetButton.addActionListener(e -> {
                preferencesModel.setDefaults();
                preferencesTabs.generateTabs();
            });

            applyButton.addActionListener(e -> {
                try {
                    savePreferences();
                    preferencesController.onPreferencesChanged();
                    PreferencesFrame.this.doDefaultCloseAction();
                } catch (Exception exc) {
                }
            });

            cancelButton.addActionListener(e -> PreferencesFrame.this.doDefaultCloseAction());
        }
    }    
    
    private void savePreferences() {
        
        // Window preferences
        preferencesModel.setMinWidthChartFrame(windowsTab.getMinWidthChartFrame());
        preferencesModel.setMinHeightChartFrame(windowsTab.getMinHeightChartFrame());
        preferencesModel.setMinWidthSearchFrame(windowsTab.getMinWidthSearchFrame());
        preferencesModel.setMinHeightSearchFrame(windowsTab.getMinHeightSearchFrame());
        preferencesModel.setMinWidthWatchlistFrame(windowsTab.getMinWidthWatchlistFrame());
        preferencesModel.setMinHeightWatchlistFrame(windowsTab.getMinHeightWatchlistFrame());
        
        // Chart preferences
        preferencesModel.setColorAlarm(chartsTab.getAlarmColor());
        preferencesModel.setColorBollinger(chartsTab.getBollingerColor());
        preferencesModel.setColorMovingAvg(chartsTab.getMovAvgColor());
        System.out.println(chartsTab.getAlarmColor());
        System.out.println(chartsTab.getBollingerColor());
        System.out.println(chartsTab.getMovAvgColor());
    }
}