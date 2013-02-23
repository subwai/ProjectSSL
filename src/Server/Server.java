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

public class Server {
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, KeyStoreException, CertificateException, UnrecoverableKeyException, KeyManagementException{
		int port = 60212;
		
		System.setProperty("javax.net.ssl.trustStore", "hca_trusted.jks");
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
			String userName = cert.getSubjectDN().getName();
			System.out.println(userName);
			
			socket.close();
		}
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
