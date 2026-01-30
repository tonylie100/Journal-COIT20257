package Server;

import Contract.*;
import Security.CryptoUtil;
import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Handles communication with a single client
 * Runs in a separate thread for each connected client
 */
public class ClientHandler implements Runnable {
    
    private Socket clientSocket;
    private ServerAuthenticator authenticator;
    private String clientName;
    private SecretKey sessionKey;
    private boolean authenticated;
    
    private ObjectOutputStream out;
    private ObjectInputStream in;
    
    // Directory to store uploaded class files
    private static final String CLASS_DIR = "uploaded_classes/";
    
    /**
     * Constructor
     */
    public ClientHandler(Socket clientSocket, ServerAuthenticator authenticator) {
        this.clientSocket = clientSocket;
        this.authenticator = authenticator;
        this.authenticated = false;
        
        // Create directory for uploaded classes
        File dir = new File(CLASS_DIR);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }
    
    @Override
    public void run() {
        try {
            System.out.println("\n[NEW CLIENT] Connected from: " + 
                             clientSocket.getInetAddress().getHostAddress());
            
            // Setup streams (output first to avoid deadlock)
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(clientSocket.getInputStream());
            
            // Perform authentication
            handleAuthentication();
            
            // Handle client requests
            handleClientRequests();
            
        } catch (Exception e) {
            System.err.println("[ERROR] Client handler error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }
    
    /**
     * Handle the authentication process
     */
    private void handleAuthentication() throws Exception {
        System.out.println("\n========================================");
        System.out.println("HANDLING CLIENT AUTHENTICATION");
        System.out.println("========================================");
        
        // Receive client authenticator
        CSAuthenticator clientAuth = (CSAuthenticator) in.readObject();
        System.out.println("Received client authenticator");
        
        // Process authentication and create response
        CSAuthenticator serverAuth = authenticator.processClientAuthenticator(clientAuth);
        
        // Send server authenticator back to client
        out.writeObject(serverAuth);
        out.flush();
        System.out.println("Sent server authenticator to client");
        
        // Store client info
        clientName = clientAuth.getPlainUserName();
        sessionKey = authenticator.getSessionKey(clientName);
        authenticated = true;
        
        System.out.println("========================================");
        System.out.println("CLIENT AUTHENTICATED: " + clientName);
        System.out.println("========================================\n");
    }
    
    /**
     * Handle client requests after authentication
     */
    private void handleClientRequests() throws Exception {
        System.out.println("[" + clientName + "] Ready to receive requests\n");
        
        while (authenticated && !clientSocket.isClosed()) {
            try {
                // Receive encrypted data
                byte[] encryptedData = (byte[]) in.readObject();
                
                // Decrypt the data
                Object decryptedObject = CryptoUtil.decryptObject(encryptedData, sessionKey);
                
                // Determine what type of object was received
                if (decryptedObject instanceof CFile) {
                    handleClassFileUpload((CFile) decryptedObject);
                } else if (decryptedObject instanceof Task) {
                    handleTaskExecution((Task) decryptedObject);
                } else {
                    System.err.println("[" + clientName + "] Unknown object type received");
                }
                
            } catch (EOFException e) {
                // Client disconnected
                System.out.println("[" + clientName + "] Client disconnected");
                break;
            }
        }
    }
    
    /**
     * Handle class file upload from client
     */
    private void handleClassFileUpload(CFile cfile) throws Exception {
        System.out.println("\n[" + clientName + "] --- Receiving class file ---");
        System.out.println("File: " + cfile.getFileName() + 
                          " (" + cfile.getFileSize() + " bytes)");
        
        // Save the class file
        String filePath = CLASS_DIR + cfile.getFileName();
        FileOutputStream fos = new FileOutputStream(filePath);
        fos.write(cfile.getFileContent());
        fos.close();
        
        System.out.println("✓ Class file saved: " + filePath + "\n");
    }
    
    /**
     * Handle task execution
     */
    private void handleTaskExecution(Task task) throws Exception {
        System.out.println("\n[" + clientName + "] --- Executing task ---");
        System.out.println("Task: " + task.getDescription());
        
        // Execute the task
        Object result = task.execute();
        System.out.println("Task completed");
        System.out.println("Result: " + result);
        
        // Encrypt the result
        byte[] encryptedResult = CryptoUtil.encryptObject((java.io.Serializable) result, 
                                                          sessionKey);
        System.out.println("Result encrypted (" + encryptedResult.length + " bytes)");
        
        // Send encrypted result back to client
        out.writeObject(encryptedResult);
        out.flush();
        System.out.println("✓ Result sent to client\n");
    }
    
    /**
     * Cleanup resources
     */
    private void cleanup() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
            
            if (clientName != null) {
                System.out.println("[" + clientName + "] Connection closed\n");
            } else {
                System.out.println("[UNKNOWN CLIENT] Connection closed\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
