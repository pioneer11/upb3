package upb_z2;

import java.io.File;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.BadPaddingException;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;

public class Asymmetric_encryption {
    
  public static KeyPair do_RSA(String key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
  KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
  keyPairGenerator.initialize(1024);
  KeyPair keyPair = keyPairGenerator.generateKeyPair();
  PrivateKey privateKey = keyPair.getPrivate();
  PublicKey publicKey = keyPair.getPublic();
  
  String dataToBeEncrypted = key;   //tu by mal ist ten nahodne vygenerovany kluc, co je v key.txt

  Cipher cipher = Cipher.getInstance("RSA");

  cipher.init(Cipher.ENCRYPT_MODE, publicKey);
  String encryptedData = Base64.encodeBase64String(cipher.doFinal(dataToBeEncrypted.getBytes())); 
  System.out.println("Encrypted key: " + encryptedData);
  
  Cipher dipher = Cipher.getInstance("RSA");

      System.out.println("Public key: " + publicKey);
      System.out.println("Priate key: " + privateKey);
      
  dipher.init(Cipher.DECRYPT_MODE, privateKey);
  System.out.println("Decrypted key: " + new String(dipher.doFinal(Base64.decodeBase64(encryptedData))));
  
  return new KeyPair(publicKey, privateKey);
  }
  
}
