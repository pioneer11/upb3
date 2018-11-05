package upbv2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.encryptor4j.util.FileEncryptor;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;



/*
 * @author Martin Novotny
 */
public class UPBv2 {
    
    public static void main(String[] args) throws GeneralSecurityException, Exception {

         boolean b =  verifySignature("Test");
       //dat podmienku, ze ak b == true... moze pokracovat v desifrovani
        
        doEncrypt(new File("input.txt"), new File("encrypted.txt"), "key.txt");
        doDecrypt(new File("encrypted.txt"), new File("decrypted.txt"), "key.txt");
        
        String key = readKeyFile("key.txt");
          
        String encKey = Asymmetric_encryption.do_RSA(key);  //kluc v key.txt zasifrovany asymetricky
        
        int size_of_key = encKey.length();
        System.out.println("Key length: " + size_of_key);
                
        try {
        Files.write(Paths.get("encrypted.txt"), encKey.getBytes(), StandardOpenOption.APPEND);
        }catch (IOException e) {
    //exception handling left as an exercise for the reader
        }   
        
    Path path = Paths.get("encrypted.txt");    
    ByteBuffer buffer = ByteBuffer.allocate(1024);
        try {
            FileChannel channel = FileChannel.open(path, StandardOpenOption.READ);
            channel.read(buffer, channel.size() - size_of_key);
            System.out.println("Key in encrypted file = " + new String(buffer.array()));
        } catch (IOException e) {
            e.printStackTrace();
        }
     
        String s = readKeyFile("encrypted.txt");
        int size = s.length() - size_of_key;
        String text_to_encrypt = s.substring(0, size);
        System.out.println("Text to encrypt: " + text_to_encrypt);
        
   /*      try {
        Files.write(Paths.get("encrypted.txt"), text_to_encrypt.getBytes(), StandardOpenOption.WRITE);
        }catch (IOException e) {
    //exception handling left as an exercise for the reader
        } */

           
    }
    

    static void doEncrypt(File srcFile, File destFile, String keyFile) throws GeneralSecurityException, IOException{
        SecretKey aesKey = generateKey();
        FileEncryptor fe = new FileEncryptor(aesKey);
        writeKeyFile(keyFile, aesKey);
        fe.encrypt(srcFile, destFile);
        System.out.println("Encryption successful.");
    }
    
    static void doDecrypt(File srcFile, File destFile, String keyFile) throws GeneralSecurityException, IOException{
        SecretKey aesKey = StringToKey(readKeyFile(keyFile));
        FileEncryptor fe = new FileEncryptor(aesKey);
        fe.decrypt(srcFile, destFile);
        System.out.println("Decryption successful.");
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
    
    static void writeKeyFile(String file, SecretKey aesKey){
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(keyToString(aesKey));
        } catch (IOException e) 
        {
            System.out.println(e);
        }
    }
    
    static SecretKey generateKey() throws NoSuchAlgorithmException{
        KeyGenerator keygenerator = KeyGenerator.getInstance("AES");
        SecretKey aesKey = keygenerator.generateKey();
        return aesKey;
    }
    
    static String keyToString(SecretKey secretKey){
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }
    
    static SecretKey StringToKey(String encodedKey){
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 
    }
    
    static boolean verifySignature(String mess) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException{
    //Signature
    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

    KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "BC");

    keyGen.initialize(512, new SecureRandom());

    KeyPair keyPair = keyGen.generateKeyPair();
    Signature signature = Signature.getInstance("SHA1withRSA", "BC");

    signature.initSign(keyPair.getPrivate(), new SecureRandom());

    byte[] message = "Test".getBytes();     //tu nemam sajnu co ma byt ako message
    signature.update(message);   
    byte[] message2 = mess.getBytes();  
    byte[] sigBytes = signature.sign();
    signature.initVerify(keyPair.getPublic());
    signature.update(message2);
    System.out.println("Signature verify: " + signature.verify(sigBytes));    
    
    return signature.verify(sigBytes);
    
    }
 


}