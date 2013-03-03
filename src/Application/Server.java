package Application;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.crypto.NoSuchPaddingException;
import javax.naming.InvalidNameException;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class Server {
	static final String LOG_PATH = "server.log";
	public static void main(String[] args) throws IOException,
			NoSuchAlgorithmException, KeyStoreException, CertificateException,
			UnrecoverableKeyException, KeyManagementException,
			InvalidNameException, InvalidKeyException, NoSuchPaddingException {

		Logger logger = Logger.getLogger("ServerLogger");
		FileHandler fh;
		int limit = 5000000;
		try {
			// This block configure the logger with handler and formatter
			fh = new FileHandler(LOG_PATH, limit, 1, true);
			logger.addHandler(fh);
			// logger.setLevel(Level.ALL);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);

			// the following statement is used to log any messages
			logger.info("Server started.");

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
		   @Override
		   public void run() {
		    System.out.println("Shutting down...");
		   }
		});
		
		System.setProperty("javax.net.ssl.trustStore", "keys/hca_trusted.jks");

		SSLContext ctx;
		KeyManagerFactory kmf;
		KeyStore ks;
		System.out.println("Please enter the password for user/key '"+Shared.SERVER_KEY+"'");
		Scanner scan = new Scanner(System.in);
		String passphrase = Shared.readPassword(scan);

		ctx = SSLContext.getInstance("TLS");
		kmf = KeyManagerFactory.getInstance("SunX509");
		ks = KeyStore.getInstance("JKS");

		ks.load(new FileInputStream("keys/"+Shared.SERVER_KEY+".jks"), null);
		kmf.init(ks, passphrase.toCharArray());
		ctx.init(kmf.getKeyManagers(), null, null);
		SSLServerSocketFactory factory =  ctx.getServerSocketFactory();
		
		String salt = "A6GXcTrb9wOW2jb1ILkESY15";
		DatabaseStorage dbs = new DatabaseStorage(passphrase,salt);
		Database db = dbs.load();

		SSLServerSocket s = (SSLServerSocket) factory.createServerSocket(Shared.SERVER_PORT);
		System.out.println("Server started and accepting connections on port "+ Shared.SERVER_PORT);
		SSLSocket socket = null;
		while (true) {
			try {
				socket = (SSLSocket) s.accept();
				socket.setNeedClientAuth(true);
				String username = Shared.commonNameFrom(socket);

				socket.setKeepAlive(true);
				Person user;
				if ((user = db.searchUser(username)) != null) {
					System.out.println("user " + username + " connected as "
							+ user.getClass().getSimpleName());
					// start separate thread
					ServerConnection sc = new ServerConnection(socket, user,
							db, logger, dbs);
					Thread clientThread = new Thread(sc);
					clientThread.setName("hc:" + user.getName());
					clientThread.start();
					logger.info(user.getName() + " have been authenticated.");
				} else {
					System.out.println("ERROR: Unknown user. ");
					socket.close();
					logger.info("Login attempted and failed with username: " + username);
				}
			} catch (Exception ex) {
				System.err.println("ERROR accepting socket connection");
				ex.printStackTrace();
				logger.info("Authentication attempt error: Accepting socket connection failed.");
				if (socket != null) {
					socket.close();
				}
			}
		}
	}
}
