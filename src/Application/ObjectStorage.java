package Application;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

//encrypted object storage
public class ObjectStorage {
	
	private Cipher cipher;
	private SecretKey key;
	private String filename;
	
	public ObjectStorage(String filename, String passphrase, String salt){
		this.filename = filename;
        try {
			cipher = Cipher.getInstance("DESede");
			DESedeKeySpec keyspec = new DESedeKeySpec((passphrase+salt).getBytes("UTF8"));
			key = SecretKeyFactory.getInstance("DESede").generateSecret(keyspec);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(10);
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			System.exit(11);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			System.exit(12);
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			System.exit(13);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.exit(14);
		}
	}
	
	public Object load(){
		
		Object fileObject = null;
		try {
			initKey(Cipher.DECRYPT_MODE);
			
			FileInputStream fileIn = new FileInputStream(filename);
			CipherInputStream cis = new CipherInputStream(fileIn, cipher);
			ObjectInputStream in = new ObjectInputStream(cis);
			fileObject = in.readObject();
			in.close();
			cis.close();
			fileIn.close();
			System.out.println("Successfully unlocked existing file " + filename);
		} catch (IOException e) {
			System.out.println("File '" + filename + "' not found, creating new");
		} catch (ClassNotFoundException e) {
			System.err.println("ERROR: File '" + filename + "' has incompatible file structure.");
			System.exit(2);
		}
		
		return fileObject;
	}
	public void save(Object db){
		try {
			initKey(Cipher.ENCRYPT_MODE);
			
			FileOutputStream fileOut = new FileOutputStream(filename);
			CipherOutputStream cos = new CipherOutputStream(fileOut, cipher);
			ObjectOutputStream out = new ObjectOutputStream(cos);
			
			out.writeObject(db);
			out.close();
			cos.close();
			fileOut.close();
			
		} catch (FileNotFoundException e) {
			System.err.println("ERROR1: could not write file " + filename);
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			System.err.println("ERROR2: could not write file " + filename);
			e.printStackTrace();
			System.exit(3);
		}
		
	}
	
	private void initKey(int mode){
		try {
			cipher.init(mode, key);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
