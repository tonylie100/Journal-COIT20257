package Server;

import Contract.CSAuthenticator;
import Security.CryptoUtil;
import java.security.*;
import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles server-side authentication with clients
 * Implements Steps 2-5 of the mutual authentication protocol
 */
public class ServerAuthenticator {
    
    private String serverName = "CENTRE";
    private KeyPair serverKeyPair;
    private Map<String, PublicKey> clientPublicKeys;
    private Map<String, SecretKey> sessionKeys;
    
    /**
     * Constructor
     * @param serverKeyPair Server's RSA key pair
     */
    public ServerAuthenticator(KeyPair serverKeyPair) {
        this.serverKeyPair = serverKeyPair;
        this.clientPublicKeys = new HashMap<>();
        this.sessionKeys = new HashMap<>();
    }
    
    /**
     * Register a client's public key
     * This simulates the pre-shared public key requirement
     * @param userName Client's username
     * @param publicKey Client's public key
     */
    public void registerClient(String userName, PublicKey publicKey) {
        clientPublicKeys.put(userName, publicKey);
        System.out.println("Registered client: " + userName);
    }
    
    /**
     * STEPS 2-5: Process client's authenticator and create server's response
     * Client sent:
     * - Plain username
     * - Username encrypted with client's private key
     * - Verification string encrypted with server's public key
     */
    public CSAuthenticator processClientAuthenticator(CSAuthenticator clientAuth) 
            throws Exception {
        
        System.out.println("\n=== SERVER: Processing client authenticator ===");
        
        String clientName = clientAuth.getPlainUserName();
        System.out.println("Client claims to be: " + clientName);
        
        // Check if client is registered
        if (!clientPublicKeys.containsKey(clientName)) {
            throw new SecurityException("Client not registered: " + clientName);
        }
        
        PublicKey clientPublicKey = clientPublicKeys.get(clientName);
        
        // STEP 2: Decrypt and verify client's username
        // Client encrypted username with their private key, we decrypt with their public key
        String decryptedUserName = CryptoUtil.decryptRSA(
                                    clientAuth.getCipherUserName(), 
                                    clientPublicKey);
        
        if (!decryptedUserName.equals(clientName)) {
            throw new SecurityException("Client authentication failed - username mismatch");
        }
        System.out.println("✓ Step 2: Client identity verified");
        
        // STEP 3: Decrypt verification string using server's private key
        // Client encrypted it with our public key, we decrypt with our private key
        String verificationString = CryptoUtil.decryptRSA(
                                        clientAuth.getVerificationString(), 
                                        serverKeyPair.getPrivate());
        System.out.println("✓ Step 3: Verification string decrypted: " + 
                          verificationString.substring(0, 20) + "...");
        
        // STEP 4: Generate session key for this client
        SecretKey sessionKey = CryptoUtil.generateAESKey();
        sessionKeys.put(clientName, sessionKey);
        System.out.println("✓ Step 4: Session key generated for " + clientName);
        
        // STEP 5: Create server's authenticator response
        
        // Encrypt server name with SERVER's PRIVATE key (proves server identity)
        String cipherServerName = CryptoUtil.encryptRSA(serverName, 
                                                        serverKeyPair.getPrivate());
        
        // Encrypt verification string with SESSION KEY (proves we decrypted it)
        String encryptedVerification = CryptoUtil.encryptAES(verificationString, 
                                                             sessionKey);
        
        // Encrypt session key with CLIENT's PUBLIC key (only client can decrypt)
        String encryptedSessionKey = CryptoUtil.encryptRSA(
                                        CryptoUtil.keyToString(sessionKey), 
                                        clientPublicKey);
        
        CSAuthenticator serverAuth = new CSAuthenticator(serverName, cipherServerName, 
                                                        encryptedVerification, 
                                                        encryptedSessionKey);
        
        System.out.println("✓ Step 5: Server authenticator created");
        System.out.println("SERVER AUTHENTICATION COMPLETE\n");
        
        return serverAuth;
    }
    
    /**
     * Get session key for a specific client
     * @param userName Client's username
     * @return The session key for this client
     */
    public SecretKey getSessionKey(String userName) {
        SecretKey key = sessionKeys.get(userName);
        if (key == null) {
            throw new IllegalStateException("No session key for client: " + userName);
        }
        return key;
    }
    
    /**
     * Check if client is authenticated
     */
    public boolean isClientAuthenticated(String userName) {
        return sessionKeys.containsKey(userName);
    }
    
    /**
     * Get number of authenticated clients
     */
    public int getAuthenticatedClientCount() {
        return sessionKeys.size();
    }
}
