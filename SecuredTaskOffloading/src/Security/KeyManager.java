package Security;

import java.io.*;
import java.security.*;
import java.security.spec.*;

/**
 * Manages RSA key pairs for clients and server
 * Handles saving keys to files and loading them back
 */
public class KeyManager {
    
    // Directory where keys are stored
    private static final String KEYS_DIRECTORY = "keys/";
    
    /**
     * Initialize key storage directory
     */
    public static void initializeKeyDirectory() {
        File dir = new File(KEYS_DIRECTORY);
        if (!dir.exists()) {
            dir.mkdir();
            System.out.println("Created keys directory: " + KEYS_DIRECTORY);
        }
    }
    
    // ========== KEY GENERATION ==========
    
    /**
     * Generate and save a new key pair for a user/server
     * @param name Name of the entity (e.g., "Stephen_Smith", "CENTRE")
     * @return The generated key pair
     */
    public static KeyPair generateAndSaveKeyPair(String name) throws Exception {
        initializeKeyDirectory();
        
        // Generate new key pair
        KeyPair keyPair = CryptoUtil.generateRSAKeyPair();
        
        // Save keys to files
        savePrivateKey(name, keyPair.getPrivate());
        savePublicKey(name, keyPair.getPublic());
        
        System.out.println("Generated and saved key pair for: " + name);
        return keyPair;
    }
    
    // ========== SAVE KEYS ==========
    
    /**
     * Save private key to file
     * @param name Entity name
     * @param privateKey The private key to save
     */
    public static void savePrivateKey(String name, PrivateKey privateKey) throws IOException {
        String filename = KEYS_DIRECTORY + name + "_private.key";
        FileOutputStream fos = new FileOutputStream(filename);
        fos.write(privateKey.getEncoded());
        fos.close();
        System.out.println("Saved private key: " + filename);
    }
    
    /**
     * Save public key to file
     * @param name Entity name
     * @param publicKey The public key to save
     */
    public static void savePublicKey(String name, PublicKey publicKey) throws IOException {
        String filename = KEYS_DIRECTORY + name + "_public.key";
        FileOutputStream fos = new FileOutputStream(filename);
        fos.write(publicKey.getEncoded());
        fos.close();
        System.out.println("Saved public key: " + filename);
    }
    
    // ========== LOAD KEYS ==========
    
    /**
     * Load public key from file
     * @param name Entity name
     * @return The public key
     */
    public static PublicKey loadPublicKey(String name) throws Exception {
        String filename = KEYS_DIRECTORY + name + "_public.key";
        File file = new File(filename);
        
        if (!file.exists()) {
            throw new FileNotFoundException("Public key not found: " + filename);
        }
        
        FileInputStream fis = new FileInputStream(file);
        byte[] encodedKey = fis.readAllBytes();
        fis.close();
        
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        
        System.out.println("Loaded public key: " + filename);
        return keyFactory.generatePublic(keySpec);
    }
    
    /**
     * Load private key from file
     * @param name Entity name
     * @return The private key
     */
    public static PrivateKey loadPrivateKey(String name) throws Exception {
        String filename = KEYS_DIRECTORY + name + "_private.key";
        File file = new File(filename);
        
        if (!file.exists()) {
            throw new FileNotFoundException("Private key not found: " + filename);
        }
        
        FileInputStream fis = new FileInputStream(file);
        byte[] encodedKey = fis.readAllBytes();
        fis.close();
        
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        
        System.out.println("Loaded private key: " + filename);
        return keyFactory.generatePrivate(keySpec);
    }
    
    /**
     * Load complete key pair from files
     * @param name Entity name
     * @return The key pair
     */
    public static KeyPair loadKeyPair(String name) throws Exception {
        PublicKey publicKey = loadPublicKey(name);
        PrivateKey privateKey = loadPrivateKey(name);
        return new KeyPair(publicKey, privateKey);
    }
    
    // ========== KEY CHECKING ==========
    
    /**
     * Check if keys exist for an entity
     * @param name Entity name
     * @return true if both public and private keys exist
     */
    public static boolean keysExist(String name) {
        File publicKeyFile = new File(KEYS_DIRECTORY + name + "_public.key");
        File privateKeyFile = new File(KEYS_DIRECTORY + name + "_private.key");
        return publicKeyFile.exists() && privateKeyFile.exists();
    }
    
    /**
     * List all available keys
     */
    public static void listKeys() {
        File dir = new File(KEYS_DIRECTORY);
        if (!dir.exists()) {
            System.out.println("No keys directory found.");
            return;
        }
        
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("No keys found.");
            return;
        }
        
        System.out.println("Available keys:");
        for (File file : files) {
            System.out.println("  - " + file.getName());
        }
    }
    
    // ========== KEY SETUP UTILITY ==========
    
    /**
     * Setup keys for server and clients
     * Run this once to generate all necessary keys
     */
    public static void setupAllKeys() throws Exception {
        System.out.println("=== Setting up all keys ===");
        
        // Generate server key
        if (!keysExist("CENTRE")) {
            generateAndSaveKeyPair("CENTRE");
        } else {
            System.out.println("Server keys already exist.");
        }
        
        // Generate client keys
        String[] clients = {"Stephen_Smith", "Michael_Fox"};
        for (String client : clients) {
            if (!keysExist(client)) {
                generateAndSaveKeyPair(client);
            } else {
                System.out.println("Keys for " + client + " already exist.");
            }
        }
        
        System.out.println("=== Key setup complete ===");
        listKeys();
    }
}
