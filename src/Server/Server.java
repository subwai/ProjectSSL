package Server;

import java.io.*;
import java.net.Socket;

import javax.net.ssl.*;
import javax.security.cert.X509Certificate;

import java.security.KeyStore;

public class Server {
	public static void main(String[] args) throws IOException{
		String host = "localhost";
		int port = 60212;
		String path = "";
		
		SSLServerSocketFactory factory = null;
		
		try {
            SSLContext ctx;
            KeyManagerFactory kmf;
            KeyStore ks;
            char[] passphrase = "passphrase".toCharArray();

            ctx = SSLContext.getInstance("TLS");
            kmf = KeyManagerFactory.getInstance("SunX509");
            ks = KeyStore.getInstance("JKS");

            ks.load(new FileInputStream("testkeys"), passphrase);

            kmf.init(ks, passphrase);
            ctx.init(kmf.getKeyManagers(), null, null);
            factory = ctx.getServerSocketFactory();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
		
		SSLServerSocket s = (SSLServerSocket)factory.createServerSocket(8888);
        
		while(true){
			SSLSocket socket = (SSLSocket)s.accept();
			socket.setNeedClientAuth(true);
			SSLSession session = socket.getSession();
			X509Certificate cert = (X509Certificate)session.getPeerCertificateChain()[0];
			String userName = cert.getSubjectDN().getName();
			System.out.println(userName);
			
			socket.close();
		}
	}
}
