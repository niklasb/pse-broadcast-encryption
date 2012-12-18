package cryptocast.crypto;

import javax.crypto.SecretKey;

public interface OutputCipherControl {
    public SecretKey getKey(); 
    public void updateKey();    
    public void reinitializeCipher();
}
