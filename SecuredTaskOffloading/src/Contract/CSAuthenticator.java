package Contract;

import java.io.Serializable;

/**
 * Authenticator object for mutual authentication between client and server
 * Contains encrypted usernames, verification string, and session key
 */
public class CSAuthenticator implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // Username in plain text
    private String plainUserName;
    
    // Username encrypted with sender's private key (for authentication)
    private String cipherUserName;
    
    // Random verification string (encrypted with public key or session key)
    private String verificationString;
    
    // Session key encrypted with recipient's public key
    private String sessionKey;
    
    /**
     * Constructor
     */
    public CSAuthenticator(String plainUserName, String cipherUserName, 
                          String verificationString, String sessionKey) {
        this.plainUserName = plainUserName;
        this.cipherUserName = cipherUserName;
        this.verificationString = verificationString;
        this.sessionKey = sessionKey;
    }
    
    // Getters and Setters
    
    public String getPlainUserName() {
        return plainUserName;
    }
    
    public void setPlainUserName(String plainUserName) {
        this.plainUserName = plainUserName;
    }
    
    public String getCipherUserName() {
        return cipherUserName;
    }
    
    public void setCipherUserName(String cipherUserName) {
        this.cipherUserName = cipherUserName;
    }
    
    public String getVerificationString() {
        return verificationString;
    }
    
    public void setVerificationString(String verificationString) {
        this.verificationString = verificationString;
    }
    
    public String getSessionKey() {
        return sessionKey;
    }
    
    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }
    
    @Override
    public String toString() {
        return "CSAuthenticator{" +
                "plainUserName='" + plainUserName + '\'' +
                ", cipherUserName='" + (cipherUserName != null ? "ENCRYPTED" : "null") + '\'' +
                ", verificationString='" + (verificationString != null ? "ENCRYPTED" : "null") + '\'' +
                ", sessionKey='" + (sessionKey != null ? "ENCRYPTED" : "null") + '\'' +
                '}';
    }
}
