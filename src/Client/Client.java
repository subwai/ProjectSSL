package Client;

import java.io.*;

import javax.net.ssl.*;

import java.security.KeyStore;

public class Client {
	public static void main(String[] args) throws IOException{
		String host = "localhost";
		int port = 60212;
		String path = "";
		
		System.setProperty("javax.net.ssl.trustStore", "hca_trusted.jks");
		System.setProperty("javax.net.ssl.trustStorePassword", "qweqwe");
		
		SSLSocketFactory factory = null;
		
		try {
            SSLContext ctx;
            KeyManagerFactory kmf;
            KeyStore ks;
            char[] passphrase = "qweqwe".toCharArray();

            ctx = SSLContext.getInstance("TLS");
            kmf = KeyManagerFactory.getInstance("SunX509");
            ks = KeyStore.getInstance("JKS");

            ks.load(new FileInputStream("keys/socialstyrelsen.jks"), passphrase);

            kmf.init(ks, passphrase);
            ctx.init(kmf.getKeyManagers(), null, null);

            factory = ctx.getSocketFactory();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        
		SSLSocket socket = (SSLSocket)factory.createSocket(host, port);
		//socket.setNeedClientAuth(true);
		
		printSocketInfo(socket);

		socket.startHandshake();
		
		PrintWriter out = new PrintWriter(
                new BufferedWriter(
                new OutputStreamWriter(
                socket.getOutputStream())));
		
		out.println("GET " + path + " HTTP/1.0");
		out.println();
		out.flush();
		
		/*
         * Make sure there were no surprises
         */
        if (out.checkError())
            System.out.println(
                "SSLSocketClient: java.io.PrintWriter error");

        /* read response */
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                socket.getInputStream()));

        String inputLine;

        while ((inputLine = in.readLine()) != null)
            System.out.println(inputLine);

        in.close();
        out.close();
        socket.close();
		
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
}
