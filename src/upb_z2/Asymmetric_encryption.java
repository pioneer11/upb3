package upb_z2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;

public class Asymmetric_encryption {
    
  public static String do_RSA(String key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException{
	  KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
	  keyPairGenerator.initialize(1024);
	  KeyPair keyPair = keyPairGenerator.generateKeyPair();
	  PrivateKey privateKey = keyPair.getPrivate();
	  PublicKey publicKey = keyPair.getPublic();
	  
	  String dataToBeEncrypted = key; 
	
	  Cipher cipher = Cipher.getInstance("RSA");
	
	  cipher.init(Cipher.ENCRYPT_MODE, publicKey);
	  String encryptedData = Base64.encodeBase64String(cipher.doFinal(dataToBeEncrypted.getBytes())); 
	  System.out.println("Encrypted key: " + encryptedData);
	  
	  Cipher dipher = Cipher.getInstance("RSA");
	
	      System.out.println("Public key: " + publicKey);
	      System.out.println("Private key: " + privateKey);
	      
	  dipher.init(Cipher.DECRYPT_MODE, privateKey);
	  System.out.println("Decrypted key: " + new String(dipher.doFinal(Base64.decodeBase64(encryptedData))));
	  SaveKeyPair("/usr/local/apache-tomcat-9.0.12/uploads", keyPair);
	  return encryptedData;
  }
  
  public static void SaveKeyPair(String path, KeyPair keyPair) throws IOException {
		PrivateKey privateKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();

		// Store Public Key.
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
		FileOutputStream fos = new FileOutputStream(path + "/public.key");
		fos.write(x509EncodedKeySpec.getEncoded());
		fos.close();

		// Store Private Key.
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
		fos = new FileOutputStream(path + "/private.key");
		fos.write(pkcs8EncodedKeySpec.getEncoded());
		fos.close();
	}
  
}
