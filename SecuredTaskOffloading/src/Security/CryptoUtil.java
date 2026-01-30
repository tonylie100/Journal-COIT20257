package Security;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;
import java.io.*;

/**
 * Utility class for cryptographic operations
 * Handles RSA (asymmetric) and AES (symmetric) encryption
 */
public class CryptoUtil {
    
    // ========== KEY GENERATION ==========
    
    /**
     * Generate RSA key pair (2048-bit for security)
     * Used for client and server authentication
     */
    public static KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }
    
    /**
     * Generate AES session key (256-bit for security)
     * Used for encrypting communication after authentication
     */
    public static SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // 256-bit AES
        return keyGen.generateKey();
    }
    
    // ========== RSA ENCRYPTION/DECRYPTION ==========
    
    /**
     * Encrypt string with RSA (used for authentication)
     * @param plainText The text to encrypt
     * @param key The public or private key
     * @return Base64 encoded encrypted string
     */
    public static String encryptRSA(String plainText, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encrypted);
    }
    
    /**
     * Decrypt string with RSA
     * @param cipherText Base64 encoded encrypted string
     * @param key The public or private key
     * @return Decrypted plain text
     */
    public static String decryptRSA(String cipherText, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decoded = Base64.getDecoder().decode(cipherText);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted, "UTF-8");
    }
    
    // ========== AES ENCRYPTION/DECRYPTION (for Strings) ==========
    
    /**
     * Encrypt string with AES (used for session communication)
     * @param plainText The text to encrypt
     * @param key The AES session key
     * @return Base64 encoded encrypted string
     */
    public static String encryptAES(String plainText, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encrypted);
    }
    
    /**
     * Decrypt string with AES
     * @param cipherText Base64 encoded encrypted string
     * @param key The AES session key
     * @return Decrypted plain text
     */
    public static String decryptAES(String cipherText, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decoded = Base64.getDecoder().decode(cipherText);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted, "UTF-8");
    }
    
    // ========== AES ENCRYPTION/DECRYPTION (for Objects) ==========
    
    /**
     * Encrypt any Serializable object with AES
     * Used for encrypting Task objects, CFile objects, etc.
     * @param obj The object to encrypt
     * @param key The AES session key
     * @return Encrypted byte array
     */
    public static byte[] encryptObject(Serializable obj, SecretKey key) throws Exception {
        // Step 1: Serialize object to bytes
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        oos.flush();
        byte[] objectBytes = bos.toByteArray();
        oos.close();
        bos.close();
        
        // Step 2: Encrypt the bytes
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(objectBytes);
    }
    
    /**
     * Decrypt byte array back to object with AES
     * @param encryptedData The encrypted byte array
     * @param key The AES session key
     * @return The original object
     */
    public static Object decryptObject(byte[] encryptedData, SecretKey key) throws Exception {
        // Step 1: Decrypt the bytes
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = cipher.doFinal(encryptedData);
        
        // Step 2: Deserialize bytes back to object
        ByteArrayInputStream bis = new ByteArrayInputStream(decryptedBytes);
        ObjectInputStream ois = new ObjectInputStream(bis);
        Object obj = ois.readObject();
        ois.close();
        bis.close();
        
        return obj;
    }
    
    // ========== KEY CONVERSION ==========
    
    /**
     * Convert SecretKey to String for transmission
     * Used when sending session key from server to client
     * @param key The AES session key
     * @return Base64 encoded key string
     */
    public static String keyToString(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
    
    /**
     * Convert String back to SecretKey
     * Used when client receives session key from server
     * @param keyStr Base64 encoded key string
     * @return The AES session key
     */
    public static SecretKey stringToKey(String keyStr) {
        byte[] decodedKey = Base64.getDecoder().decode(keyStr);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }
    
    // ========== VERIFICATION STRING ==========
    
    /**
     * Generate random alphanumeric verification string
     * Used in authentication protocol
     * @param length Length of the string (e.g., 128)
     * @return Random string
     */
    public static String generateVerificationString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
    
    // ========== HELPER METHODS ==========
    
    /**
     * Print key information (for debugging)
     */
    public static void printKeyInfo(Key key) {
        System.out.println("Key Algorithm: " + key.getAlgorithm());
        System.out.println("Key Format: " + key.getFormat());
        System.out.println("Key (Base64): " + Base64.getEncoder().encodeToString(key.getEncoded()).substring(0, 50) + "...");
    }
}
