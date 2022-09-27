package de.stocker.network;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.stocker.common.*;
import de.stocker.model.dataWrappers.DataProvider;

/**
 * The Class NetworkController implements the INetworkInterface and handles the
 * connections to the data provider.
 * 
 * @author Matthias Rudolph
 */
public class NetworkController implements INetworkController {
    
    private DataProvider activeDataProvider;
    private String apiKey;
    private PushClient pushClient;
    private List<IPushSubscriber> pushSubscribers;
    
    /**
     * Instantiates a new network controller to connect to the specified data
     * provider.
     *
     * @param dataProvider the data provider
     */
    public NetworkController(DataProvider dataProvider) {
        this.activeDataProvider = dataProvider;
        this.apiKey = dataProvider.getApiKey();
        
        this.pushSubscribers = new ArrayList<IPushSubscriber>();
        
        dialUpConnection();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setActiveDataProvider(DataProvider dataProvider) {
            this.activeDataProvider = dataProvider;
            this.apiKey = activeDataProvider.getApiKey();
            dialUpConnection();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void registerPushSubscriber(IPushSubscriber pushSub) {
        if (!pushSubscribers.contains(pushSub)) {
            pushSubscribers.add(pushSub);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void newPushDataReceived(String message) {
        if (pushSubscribers != null) {
            for (IPushSubscriber pushSub : pushSubscribers) {
                pushSub.newPushData(message);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getCandles(String symbol, String resolution, String from, String to) {
        StringBuilder sb = new StringBuilder();
        
        sb.append(activeDataProvider.getPullUrl());
        sb.append(EPullOperation.CANDLE.getPath());
        sb.append("?symbol=" + symbol);
        sb.append("&resolution=" + resolution);
        sb.append("&from=" + from);
        sb.append("&to=" + to);
        sb.append("&token=" + apiKey);
        
        String url = sb.toString();
        String result = httpGet(url);
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getQuote(String symbol) {
        StringBuilder sb = new StringBuilder();
        
        sb.append(activeDataProvider.getPullUrl());
        sb.append(EPullOperation.QUOTE.getPath());
        sb.append("?symbol=" + symbol);
        sb.append("&token=" + apiKey);
        
        String url = sb.toString();
        String result = httpGet(url);
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getSearch(String symbol) {
        StringBuilder sb = new StringBuilder();
        
        sb.append(activeDataProvider.getPullUrl());
        sb.append(EPullOperation.SEARCH.getPath());
        sb.append("?q=" + symbol);
        sb.append("&token=" + apiKey);
        
        String url = sb.toString();
        String result = httpGet(url);
        return result;

    }
    
    private String httpGet(String url) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int code = conn.getResponseCode();
            if (code >= 400) {
                System.err.println("Request failed. HTTP error code: " + code);
                return null;
            }
            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String requestResult = in.readLine();
                return requestResult;
            }
        } catch (IOException ex) {
            System.err.println("An error occurred: " + ex.getMessage());
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendWebSocketMessage(String message) {
        while (!pushClient.isOpen()) {
            try {
                System.out.println("Waiting for connection...");
                Thread.sleep(100L);
            } catch (InterruptedException iex) {
            }
        }
        pushClient.send(message);
    }
    
    /**
     * Closes the the currently active connection and dials up a new one. This
     * is used for a data provider change.
     */
    private void dialUpConnection() {
        if (pushClient != null) {
            pushClient.close();
        }
        pushClient = new PushClient(this, activeDataProvider.getPushUrl() + "?token=" + apiKey);
        pushClient.connect();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataProvider getActiveDataProvider() {
        return activeDataProvider;
    }
    
}
