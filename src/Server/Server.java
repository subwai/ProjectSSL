package Server;

import java.io.*;
import java.net.Socket;

import javax.net.ssl.*;
import javax.security.cert.X509Certificate;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

public class Server {
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, KeyStoreException, CertificateException, UnrecoverableKeyException, KeyManagementException, InvalidNameException{
		int port = 1337;
		//"https://ytoucksandsoffiestakedst:Fl8YfMjOi44jQbhpUkNDbkoh@baversjo.cloudant.com/medical
		
		//Start fulkod/hŒrdkodning av anvŠndare osv..
		Division surgery = new Division("surgery");
		Division xray = new Division("xray");
		Division quarantine = new Division("quarantine");
		
		Patient patient1 = new Patient("");
		Patient patient2 = new Patient("");
		Patient patient3 = new Patient("");
		
		Nurse nurse1 = new Nurse("Eva", surgery);
		Nurse nurse2 = new Nurse("martin", xray);
		Nurse nurse3 = new Nurse("karin", quarantine);
		
		Doctor doctor = new Doctor("Peter", surgery);
		Doctor doctor2 = new Doctor("Lars", quarantine);
		
		Admin admin1 = new Admin("Socialstyrelsen");
		//Slut fulkod.
		
		System.setProperty("javax.net.ssl.trustStore", "keys/hca_trusted.jks");
		System.setProperty("javax.net.ssl.trustStorePassword", "qweqwe");
		
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

		
		SSLServerSocket s = (SSLServerSocket)factory.createServerSocket(port);
        System.out.println("Server started and accepting connections on port " +port);
        printServerSocketInfo(s);
		while(true){
			SSLSocket socket = (SSLSocket)s.accept();
			socket.setNeedClientAuth(true);
			printSocketInfo(socket);
			SSLSession session = socket.getSession();
			X509Certificate cert = (X509Certificate)session.getPeerCertificateChain()[0];
			//extract CN from DN
			LdapName ldapDN = new LdapName(cert.getSubjectDN().getName());
			String userName = "";
			for(Rdn rdn: ldapDN.getRdns()) {
				if(rdn.getType().trim().toUpperCase().equals("CN")){
					userName = rdn.getValue().toString().trim().toLowerCase();
				}
			}
			if(userName.length() > 0){//autheniticated. now authorize request.
				System.out.println("user " + userName + " authenticated");
			}
			socket.close();
		}
	}
	
	public void deleteRecord(Record record, Person person){
		
	}
	private static void printSocketInfo(SSLSocket s) {
	      System.out.println("Socket class: "+s.getClass());
	      System.out.println("   Remote address = "
	         +s.getInetAddress().toString());
	      System.out.println("   Remote port = "+s.getPort());
	      System.out.println("   Local socket address = "
	         +s.getLocalSocketAddress().toString());
	      System.out.println("   Local address = "
	         +s.getLocalAddress().toString());
	      System.out.println("   Local port = "+s.getLocalPort());
	      System.out.println("   Need client authentication = "
	         +s.getNeedClientAuth());
	      SSLSession ss = s.getSession();
	      System.out.println("   Cipher suite = "+ss.getCipherSuite());
	      System.out.println("   Protocol = "+ss.getProtocol());
    }
    private static void printServerSocketInfo(SSLServerSocket s) {
      System.out.println("Server socket class: "+s.getClass());
      System.out.println("   Socker address = "
         +s.getInetAddress().toString());
      System.out.println("   Socker port = "
         +s.getLocalPort());
      System.out.println("   Need client authentication = "
         +s.getNeedClientAuth());
      System.out.println("   Want client authentication = "
         +s.getWantClientAuth());
      System.out.println("   Use client mode = "
         +s.getUseClientMode());
    }
}
