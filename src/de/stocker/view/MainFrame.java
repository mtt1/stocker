package de.stocker.view;

import java.awt.Color;
import java.awt.event.*;
import java.beans.PropertyVetoException;

import javax.swing.*;

import de.stocker.common.IMainController;

/**
 * The Class MainFrame extends a JFrame, whose main part is a desktop pane for
 * all the internal frame from the different parts of the application.
 * 
 * @author Matthias Rudolph
 */
public class MainFrame extends JFrame {

    private IMainController mainController;

    private final JDesktopPane desktopPane = new JDesktopPane();

    private JMenuBar menuBar;
    private JMenu menuFile;
    private JMenuItem menuCloseWindows;
    private JMenuItem menuResetApp;
    private JMenuItem menuExitApp;
    private JMenu menuTools;
    private JMenuItem menuOpenSearch;
    private JMenuItem menuOpenWatchlist;
    private JMenuItem menuOpenPreferences;
    private JMenu menuWindows;

    /**
     * Instantiates a new main frame with a reference to the mediator/main
     * controller.
     *
     * @param mainController the main controller
     */
    public MainFrame(IMainController mainController) {
        this.mainController = mainController;

        createAndShowGUI();
    }

    /**
     * Creates and shows the GUI, setting the look and feel for the application,
     * automating the "Windows" menu, setting up the desktop pane and the
     * closing routine to trigger the persistence.
     */
    private void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        SwingUtilities.updateComponentTreeUI(this);

        // Automates the generation of the "Windows" menu, refreshing it
        // whenever a component has been added or removed from the desktop pane
        desktopPane.addContainerListener(new ContainerListener() {
            @Override
            public void componentRemoved(ContainerEvent e) {
                refreshWindowsMenu();
            }

            @Override
            public void componentAdded(ContainerEvent e) {
                refreshWindowsMenu();
            }
        });

        desktopPane.setBackground(Color.LIGHT_GRAY);
        desktopPane.setVisible(true);
        
        this.add(desktopPane);

        this.setTitle("Stocker -- Matthias Rudolph -- 3266494");
        this.setSize(1500, 800);
        this.setLocationRelativeTo(null);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Saves the persistence before closing the application
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mainController.savePersistence();
            }

        });

        createMenus();

        this.setVisible(true);
    }

    /**
     * Creates the menus.
     */
    private void createMenus() {
        menuBar = new JMenuBar();

        menuFile = new JMenu("File");
        menuCloseWindows = new JMenuItem("Close all windows");
        menuResetApp = new JMenuItem("Close all windows & reset application");
        menuExitApp = new JMenuItem("Exit application");

        menuOpenSearch = new JMenuItem("Stock search");
        menuOpenWatchlist = new JMenuItem("Watchlist");
        menuOpenPreferences = new JMenuItem("Preferences");

        menuFile.add(menuCloseWindows);
        menuFile.add(menuResetApp);
        menuFile.addSeparator();
        menuFile.add(menuExitApp);

        menuTools = new JMenu("Tools");
        menuTools.add(menuOpenSearch);
        menuTools.add(menuOpenWatchlist);
        menuTools.addSeparator();
        menuTools.add(menuOpenPreferences);

        menuWindows = new JMenu("Windows");

        menuBar.add(menuFile);
        menuBar.add(menuTools);
        menuBar.add(menuWindows);

        menuCloseWindows.addActionListener(e -> {
            resetDesktop();
        });
        menuResetApp.addActionListener(e -> {
            mainController.resetApplication();
        });
        menuExitApp.addActionListener(e -> this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));

        menuOpenSearch.addActionListener(e -> mainController.openSearchFrame());
        menuOpenWatchlist.addActionListener(e -> mainController.openWatchlistFrame());
        menuOpenPreferences.addActionListener(e -> mainController.openPreferencesFrame());

        this.setJMenuBar(menuBar);
    }

    /**
     * Refreshes the "Windows" menu, generating an entry for every open internal
     * frame.
     */
    private void refreshWindowsMenu() {
        menuWindows.removeAll();
        
        for (JInternalFrame i : desktopPane.getAllFrames()) {
            JMenuItem menuItem = new JMenuItem(i.getTitle());
            menuItem.addActionListener(e -> bringToFront(i));
            menuWindows.add(menuItem);
        }
    }

    /**
     * Adds internal frame to the main frame's desktop pane.
     *
     * @param iFrame the internal frame
     */
    public void addToMainFrame(JInternalFrame iFrame) {
        desktopPane.add(iFrame);
    }

    /**
     * Gets an array of all displayed internal frames.
     *
     * @return the array of internal frames
     */
    public JInternalFrame[] getIFrames() {
        return desktopPane.getAllFrames();
    }

    /**
     * Resets the desktop pane, removing all frames.
     */
    public void resetDesktop() {
        desktopPane.removeAll();
        this.repaint();
    }

    /**
     * Brings an internal frame to the front and sets focus.
     *
     * @param f the internal frame that is brought to the front
     */
    public void bringToFront(JInternalFrame f) {
        f.show();
        try {
            f.setIcon(false);
            f.setSelected(true);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        f.toFront();
    }

}
