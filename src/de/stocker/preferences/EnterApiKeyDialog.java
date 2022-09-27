package de.stocker.preferences;

import javax.swing.*;

import de.stocker.model.dataWrappers.DataProvider;

/**
 * The sole purpose of the Class EnterApiKeyDialog is to show a dialog prompting
 * the user to enter a missing API key for one of the data providers.
 * 
 * @author Matthias Rudolph
 */
public class EnterApiKeyDialog {
    
    /**
     * Instantiates a new dialog to enter a missing API key.
     *
     * @param dataProvider the data provider for which the key is missing
     */
    public EnterApiKeyDialog(DataProvider dataProvider) {
        JPanel dialogPanel = new JPanel();
        
        String message = "Please enter a valid API key for the data provider " + dataProvider.getName() + ":";
        JLabel messageLabel = new JLabel(message);
        JTextField keyField = new JTextField(10);
        
        dialogPanel.add(messageLabel);
        dialogPanel.add(keyField);
        
        int option = JOptionPane.showConfirmDialog(null, dialogPanel, "API Key", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            dataProvider.setApiKey(keyField.getText().strip());
        }
    }
}
