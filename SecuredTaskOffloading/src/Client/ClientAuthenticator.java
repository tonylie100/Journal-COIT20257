package Client;

import Contract.CSAuthenticator;
import Security.CryptoUtil;
import java.security.*;
import javax.crypto.SecretKey;

/**
 * Handles client-side authentication with the server
 * Implements the mutual authentication protocol
 */
public class ClientAuthenticator {
    
    private String userName;
    private KeyPair clientKeyPair;
    private PublicKey serverPublicKey;
    private SecretKey sessionKey;
    private String verificationString;
    private boolean authenticated;
    
    /**
     * Constructor
     * @param userName Client's username (e.g., "Stephen_Smith")
     * @param clientKeyPair Client's RSA key pair
     * @param serverPublicKey Server's public key
     */
    public ClientAuthenticator(String userName, KeyPair clientKeyPair, 
                              PublicKey serverPublicKey) {
        this.userName = userName;
        this.clientKeyPair = clientKeyPair;
        this.serverPublicKey = serverPublicKey;
        this.authenticated = false;
    }
    
    /**
     * STEP 1: Client creates authenticator to send to server
     * Creates CSAuthenticator with:
     * - Plain username
     * - Username encrypted with client's private key (proves identity)
     * - Verification string encrypted with server's public key (challenge)
     */
    public CSAuthenticator createClientAuthenticator() throws Exception {
        System.out.println("\n=== CLIENT: Creating authenticator ===");
        
        // Generate random verification string (128 characters)
        verificationString = CryptoUtil.generateVerificationString(128);
        System.out.println("Generated verification string: " + 
                          verificationString.substring(0, 20) + "...");
        
        // Encrypt username with CLIENT's PRIVATE key
        // This proves we have the private key matching our claimed identity
        String cipherUserName = CryptoUtil.encryptRSA(userName, 
                                                      clientKeyPair.getPrivate());
        System.out.println("Encrypted username with private key");
        
        // Encrypt verification string with SERVER's PUBLIC key
        // Only server can decrypt this with its private key
        String encryptedVerification = CryptoUtil.encryptRSA(verificationString, 
                                                             serverPublicKey);
        System.out.println("Encrypted verification string with server's public key");
        
        // Create and return authenticator (sessionKey is null at this stage)
        CSAuthenticator auth = new CSAuthenticator(userName, cipherUserName, 
                                                  encryptedVerification, null);
        System.out.println("Client authenticator created");
        
        return auth;
    }
    
    /**
     * STEPS 6-9: Client validates server's response and extracts session key
     * Server response contains:
     * - Server's name encrypted with server's private key
     * - Verification string encrypted with session key
     * - Session key encrypted with client's public key
     */
    public boolean validateServerAuthenticator(CSAuthenticator serverAuth) 
            throws Exception {
        System.out.println("\n=== CLIENT: Validating server authenticator ===");
        
        // STEP 6: Decrypt session key using CLIENT's PRIVATE key
        String sessionKeyStr = CryptoUtil.decryptRSA(serverAuth.getSessionKey(), 
                                                     clientKeyPair.getPrivate());
        this.sessionKey = CryptoUtil.stringToKey(sessionKeyStr);
        System.out.println("✓ Step 6: Session key decrypted");
        
        // STEP 7: Decrypt and verify SERVER's username
        // Server encrypted its name with its private key, we decrypt with server's public key
        String decryptedServerName = CryptoUtil.decryptRSA(
                                        serverAuth.getCipherUserName(), 
                                        serverPublicKey);
        
        if (!decryptedServerName.equals(serverAuth.getPlainUserName())) {
            System.out.println("✗ Step 7 FAILED: Server name mismatch");
            return false;
        }
        System.out.println("✓ Step 7: Server identity verified (" + decryptedServerName + ")");
        
        // STEP 8: Decrypt and verify verification string
        // Server encrypted our verification string with the session key
        String decryptedVerification = CryptoUtil.decryptAES(
                                        serverAuth.getVerificationString(), 
                                        sessionKey);
        
        if (!decryptedVerification.equals(verificationString)) {
            System.out.println("✗ Step 8 FAILED: Verification string mismatch");
            return false;
        }
        System.out.println("✓ Step 8: Verification string confirmed");
        
        // STEP 9: Authentication successful!
        authenticated = true;
        System.out.println("✓ Step 9: AUTHENTICATION SUCCESSFUL!");
        System.out.println("Session key established for secure communication\n");
        
        return true;
    }
    
    /**
     * Get the session key (only available after successful authentication)
     */
    public SecretKey getSessionKey() {
        if (!authenticated) {
            throw new IllegalStateException("Not authenticated yet!");
        }
        return sessionKey;
    }
    
    /**
     * Check if authenticated
     */
    public boolean isAuthenticated() {
        return authenticated;
    }
    
    /**
     * Get username
     */
    public String getUserName() {
        return userName;
    }
}
