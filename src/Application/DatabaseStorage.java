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

public class DatabaseStorage {
	
	static final String DB_PATH = "database.jsd"; //"java serialized des" 
	
	private Cipher cipher;
	private SecretKey key;
	
	public DatabaseStorage(String passphrase, String salt){
        try {
			cipher = Cipher.getInstance("DESede");
			DESedeKeySpec keyspec = new DESedeKeySpec((passphrase+salt).getBytes("UTF8"));
			key = SecretKeyFactory.getInstance("DESede").generateSecret(keyspec);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public Database load(){
		
		Database db = null;
		try {
			initKey(Cipher.DECRYPT_MODE);
			
			FileInputStream fileIn = new FileInputStream(DB_PATH);
			CipherInputStream cis = new CipherInputStream(fileIn, cipher);
			ObjectInputStream in = new ObjectInputStream(cis);
			db = (Database) in.readObject();
			in.close();
			cis.close();
			fileIn.close();
			System.out.println("Successfully unlocked existing database file " + DB_PATH);
		} catch (IOException e) {
			System.out.println("database file '" + DB_PATH + "' not found, creating new database");
		} catch (ClassNotFoundException e) {
			System.err.println("ERROR: database file '" + DB_PATH + "' has incompatible file structure.");
			System.exit(2);
		}
		
		if(db == null){
			db = new Database();
			save(db);
		}
		
		return db;
	}
	public void save(Database db){
		try {
			initKey(Cipher.ENCRYPT_MODE);
			
			FileOutputStream fileOut = new FileOutputStream(DB_PATH);
			CipherOutputStream cos = new CipherOutputStream(fileOut, cipher);
			ObjectOutputStream out = new ObjectOutputStream(cos);
			
			out.writeObject(db);
			out.close();
			cos.close();
			fileOut.close();
			
		} catch (FileNotFoundException e) {
			System.err.println("ERROR1: could not write database file " + DB_PATH);
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			System.err.println("ERROR2: could not write database file " + DB_PATH);
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
