package de.stocker.network;

import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import de.stocker.common.INetworkController;

/**
 * The Class PushClient used to connect to the web socket server of a data
 * provider, sending and receiving messages.
 * 
 * @author Matthias Rudolph
 */
public class PushClient extends WebSocketClient {
    
    private INetworkController networkController;
    
    /**
     * Instantiates a new push client, belonging to a specific network
     * controller, connecting the specified server URI.
     *
     * @param networkController the network controller responsible for handling
     * the incoming messages
     * @param serverUri the web socket server URI to connect to
     */
    public PushClient(INetworkController networkController, String serverUri) {
        super(URI.create(serverUri));
        this.networkController = networkController;
    }

    /**
     * Logging a successful connection to the console.
     *
     * @param handshake the server handshake
     */
    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("Connected to web socket server.");
    }
    
    /**
     * Calling the network controller when a new web socket message is received
     * and passing the message contents to it.
     *
     * @param message the message string
     */
    @Override
    public void onMessage(String message) {
        // Used when debugging
        // System.out.println("received> " + message);
        networkController.newPushDataReceived(message);
    }

    /**
     * Logging a connection close to console.
     *
     * @param code the code
     * @param reason the reason
     * @param remote the remote
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed by " + (remote ? "server" : "client") + ", Code: " + code
                + ", Reason: " + reason);
    }

    /**
     * Logging an error to console.
     *
     * @param ex the exception object
     */
    @Override
    public void onError(Exception ex) {
        if (ex != null) {
            System.err.println("An error has occured: " + ex.getMessage());
            ex.printStackTrace();
        } else
            System.out.println("Unkown error!");
    }

    
}
