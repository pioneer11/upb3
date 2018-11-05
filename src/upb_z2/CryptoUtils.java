package upb_z2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.*;


public class CryptoUtils {
	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES";

	public static void encrypt(String key, File inputFile, File outputFile) throws Exception {
		doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
	}

	public static void decrypt(String key, File inputFile, File outputFile) throws Exception {
		doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
	}

	private static void doCrypto(int cipherMode, String key, File inputFile, File outputFile) throws Exception {
		try {
			Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(cipherMode, secretKey);

			FileInputStream inputStream = new FileInputStream(inputFile);
			byte[] inputBytes = new byte[(int) inputFile.length()];
			inputStream.read(inputBytes);

			byte[] outputBytes = cipher.doFinal(inputBytes);

			FileOutputStream outputStream = new FileOutputStream(outputFile);
			outputStream.write(outputBytes);

			inputStream.close();
			outputStream.close();

		} catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException
				| IllegalBlockSizeException | IOException ex) {
			throw new Exception("Error encrypting/decrypting file", ex);
		}
	}
	
    static String generateKey() throws NoSuchAlgorithmException{
    	KeyGenerator keyGen = KeyGenerator.getInstance("AES");
    	keyGen.init(128); 
    	SecretKey secretKey = keyGen.generateKey();
    	String key = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        return key;
    }
    
    static String readKeyFile(String file){
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
               sb.append(line);
            }
            return sb.toString();
        }catch (IOException e) 
        {
            System.out.println(e);
            return null;
        }
    }
    
    static void writeKeyFile(String file, String aesKey){
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(aesKey);
        } catch (IOException e) 
        {
            System.out.println(e);
        }
    }
    
}
