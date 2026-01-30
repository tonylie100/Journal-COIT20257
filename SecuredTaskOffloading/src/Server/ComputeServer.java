package Server;

import Security.KeyManager;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;

/**
 * Main server application for remote task offloading
 * Accepts client connections and spawns handler threads
 */
public class ComputeServer {
    
    private int port;
    private ServerSocket serverSocket;
    private ServerAuthenticator authenticator;
    private boolean running;
    private int clientCount;
    
    /**
     * Constructor
     * @param port Port to listen on (e.g., 8888)
     */
    public ComputeServer(int port) {
        this.port = port;
        this.running = false;
        this.clientCount = 0;
    }
    
    /**
     * Initialize the server
     * Load keys and set up authenticator
     */
    public void initialize() throws Exception {
        System.out.println("========================================");
        System.out.println("INITIALIZING COMPUTE SERVER");
        System.out.println("========================================\n");
        
        // Load server's key pair
        System.out.println("Loading server keys...");
        KeyPair serverKeyPair = KeyManager.loadKeyPair("CENTRE");
        System.out.println("✓ Server keys loaded\n");
        
        // Create authenticator
        authenticator = new ServerAuthenticator(serverKeyPair);
        
        // Register known clients (load their public keys)
        System.out.println("Registering clients...");
        registerClient("Stephen_Smith");
        registerClient("Michael_Fox");
        System.out.println();
        
        System.out.println("========================================");
        System.out.println("SERVER INITIALIZED");
        System.out.println("========================================\n");
    }
    
    /**
     * Register a client by loading their public key
     */
    private void registerClient(String userName) throws Exception {
        PublicKey publicKey = KeyManager.loadPublicKey(userName);
        authenticator.registerClient(userName, publicKey);
    }
    
    /**
     * Start the server and accept connections
     */
    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;
        
        System.out.println("========================================");
        System.out.println("SERVER STARTED");
        System.out.println("Listening on port: " + port);
        System.out.println("Waiting for clients...");
        System.out.println("========================================\n");
        
        // Accept client connections
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                clientCount++;
                
                // Create a new thread to handle this client
                ClientHandler handler = new ClientHandler(clientSocket, authenticator);
                Thread clientThread = new Thread(handler);
                clientThread.setName("Client-" + clientCount);
                clientThread.start();
                
            } catch (IOException e) {
                if (running) {
                    System.err.println("Error accepting client connection: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Stop the server
     */
    public void stop() throws IOException {
        running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
        System.out.println("\nServer stopped.");
    }
    
    /**
     * Main method - Start the server
     */
    public static void main(String[] args) {
        ComputeServer server = new ComputeServer(8888);
        
        try {
            // Initialize server (load keys, register clients)
            server.initialize();
            
            // Start accepting connections
            server.start();
            
        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                server.stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
