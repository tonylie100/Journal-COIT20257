package Security;

/**
 * One-time setup utility to generate all RSA keys
 * Run this BEFORE starting client or server
 */
public class KeySetup {
    
    public static void main(String[] args) {
        try {
            System.out.println("Starting key generation...\n");
            
            // This will create:
            // - CENTRE_public.key and CENTRE_private.key (server)
            // - Stephen_Smith_public.key and Stephen_Smith_private.key (client 1)
            // - Michael_Fox_public.key and Michael_Fox_private.key (client 2)
            KeyManager.setupAllKeys();
            
            System.out.println("\n✓ All keys generated successfully!");
            System.out.println("Keys are stored in the 'keys/' directory");
            System.out.println("You can now run the server and clients.");
            
        } catch (Exception e) {
            System.err.println("Error generating keys: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
