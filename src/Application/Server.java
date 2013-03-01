package Application;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.security.cert.X509Certificate;

public class Server{
	private static ArrayList<Record> records;
	private static ArrayList<Person> users;

	public static void main(String[] args) throws IOException,
			NoSuchAlgorithmException, KeyStoreException, CertificateException,
			UnrecoverableKeyException, KeyManagementException,
			InvalidNameException {
		int port = 1337;
		
		Logger logger = Logger.getLogger("ServerLog");  
        FileHandler fh; 
        try {  
              
            // This block configure the logger with handler and formatter  
            fh = new FileHandler("src/ServerLog.log");  
            logger.addHandler(fh);  
            //logger.setLevel(Level.ALL);  
            SimpleFormatter formatter = new SimpleFormatter();  
            fh.setFormatter(formatter);  
              
            // the following statement is used to log any messages  
            logger.info("Server log initiated.");  
              
        } catch (SecurityException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } 
		
		records = new ArrayList<Record>();
		users = new ArrayList<Person>();

		Division surgery = new Division("surgery");
		Division xray = new Division("xray");
		Division quarantine = new Division("quarantine");

		users.add(new Patient("Johan"));
		users.add(new Nurse("Sven", surgery));
		users.add(new Doctor("Mergim", surgery));
		users.add(new Admin("socialstyrelsen"));

		

		System.setProperty("javax.net.ssl.trustStore", "keys/hca_trusted.jks");

		SSLServerSocketFactory factory = null;

		SSLContext ctx;
		KeyManagerFactory kmf;
		KeyStore ks;
		char[] passphrase = "qweqwe".toCharArray();

		ctx = SSLContext.getInstance("TLS");
		kmf = KeyManagerFactory.getInstance("SunX509");
		ks = KeyStore.getInstance("JKS");

		ks.load(new FileInputStream("keys/server.jks"), passphrase);

		kmf.init(ks, passphrase);
		ctx.init(kmf.getKeyManagers(), null, null);
		factory = ctx.getServerSocketFactory();

		SSLServerSocket s = (SSLServerSocket) factory.createServerSocket(port);
		System.out.println("Server started and accepting connections on port "
				+ port);
		printServerSocketInfo(s);
		SSLSocket socket = null;
		while (true) {
			try{
				socket = (SSLSocket) s.accept();
				socket.setNeedClientAuth(true);
				printSocketInfo(socket);
				SSLSession session = socket.getSession();
				X509Certificate cert = session.getPeerCertificateChain()[0];
				// extract CN from DN
				LdapName ldapDN = new LdapName(cert.getSubjectDN().getName());
				String username = "";
				for (Rdn rdn : ldapDN.getRdns()) {
					if (rdn.getType().trim().toUpperCase().equals("CN")) {
						username = rdn.getValue().toString().trim().toLowerCase();
					}
				}
	
				ArrayList<Person> pArr = filter(users,
						new Predicate<Person>(new String[] { username }) {
							@Override
							public boolean apply(Person p) {
								return ((String) this.args[0]).equalsIgnoreCase(p
										.getName());
							}
						});
				
				socket.setKeepAlive(true);
				
				if(pArr.size() > 0){
					Person person = pArr.get(0);
					System.out.println("user " + username + " connected as " + person.getClass().getSimpleName());
					//start separate thread
					ServerConnection sc = new ServerConnection(socket,person);
					Thread clientThread = new Thread(sc);
					clientThread.setName("hc:"+person.getName());
					clientThread.start();
					
				}else{
					System.out.println("ERROR: Unknown user. ");
					socket.close();
				}
			}catch(Exception ex){
				System.err.println("ERROR accepting socket connection");
				ex.printStackTrace();
				if(socket != null){
					socket.close();
				}
			}
		}
	}

	public List<Record> listRecords(Person user) {
		return filter(records, new Predicate<Record>(new Object[] { user }) {
			@Override
			public boolean apply(Record r) {
				return ((Person) this.args[0]).hasReadAccess(r);
			}
		});
	}

	public List<Record> searchRecords(Person user, String patient) {
		return filter(records, new Predicate<Record>(new Object[] { user,
				patient }) {
			@Override
			public boolean apply(Record r) {
				return ((Person) this.args[0]).hasReadAccess(r)
						&& ((String) this.args[1]).equalsIgnoreCase(r
								.getPatient().getName());
			}
		});
	}

	public boolean createRecord(Patient patient, Doctor doctor, Nurse nurse,
			Division div, String data) {
		return true;
	}

	public boolean deleteRecord(Record record, Person person) {
		return true;
	}

	private static <T> ArrayList<T> filter(ArrayList<T> target,
			Predicate<T> predicate) {
		ArrayList<T> result = new ArrayList<T>();
		for (T element : target) {
			if (predicate.apply(element)) {
				result.add(element);
			}
		}
		return result;
	}

	private static void printSocketInfo(SSLSocket s) {
		System.out.println("Socket class: " + s.getClass());
		System.out.println("   Remote address = "
				+ s.getInetAddress().toString());
		System.out.println("   Remote port = " + s.getPort());
		System.out.println("   Local socket address = "
				+ s.getLocalSocketAddress().toString());
		System.out.println("   Local address = "
				+ s.getLocalAddress().toString());
		System.out.println("   Local port = " + s.getLocalPort());
		System.out.println("   Need client authentication = "
				+ s.getNeedClientAuth());
		SSLSession ss = s.getSession();
		System.out.println("   Cipher suite = " + ss.getCipherSuite());
		System.out.println("   Protocol = " + ss.getProtocol());
	}

	private static void printServerSocketInfo(SSLServerSocket s) {
		System.out.println("Server socket class: " + s.getClass());
		System.out.println("   Socker address = "
				+ s.getInetAddress().toString());
		System.out.println("   Socker port = " + s.getLocalPort());
		System.out.println("   Need client authentication = "
				+ s.getNeedClientAuth());
		System.out.println("   Want client authentication = "
				+ s.getWantClientAuth());
		System.out.println("   Use client mode = " + s.getUseClientMode());
	}
}
