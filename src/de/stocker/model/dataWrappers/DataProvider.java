package de.stocker.model.dataWrappers;

/**
 * The Class DataProvider containing all relevant information for the network
 * interface to connect to the data provider. A new instance of this class can
 * also be created by user input in the preferences to add new data providers.
 * 
 * @author Matthias Rudolph
 */
public class DataProvider {

    private String name;
    private String apiKey;
    private String pullUrl;
    private String pushUrl;

    /**
     * Instantiates a new data provider object.
     *
     * @param name the name
     * @param apiKey the API key used for authentication with the data provider
     * @param pullUrl the URL used for pull requests
     * @param pushUrl the URL used to connect to the web socket to receive push updates
     */
    public DataProvider(String name, String apiKey, String pullUrl, String pushUrl) {
        this.name = name;
        this.apiKey = apiKey;
        this.pullUrl = pullUrl;
        this.pushUrl = pushUrl;
    }

    /**
     * Gets the data provider name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the data provider name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the API key.
     *
     * @return the API key
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * Sets the API key.
     *
     * @param apiKey the new API key
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    /**
     * Gets the URL used for pull requests
     *
     * @return the pull URL
     */
    public String getPullUrl() {
        return pullUrl;
    }
    
    /**
     * Sets the URL used for pull requests
     *
     * @param pullUrl the new pull URL
     */
    public void setPullUrl(String pullUrl) {
        this.pullUrl = pullUrl;
    }
    
    /**
     * Gets the URL used to connect to the web socket to receive push updates
     *
     * @return the push URL
     */
    public String getPushUrl() {
        return pushUrl;
    }
    
    /**
     * Sets the URL used to connect to the web socket to receive push updates
     *
     * @param pushUrl the new push URL
     */
    public void setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl;
    }
    
}
