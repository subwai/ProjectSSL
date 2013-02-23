package Client;

import java.io.*;

import javax.net.ssl.*;
import java.security.KeyStore;

public class Client {
	public static void main(String[] args) throws IOException{
		String host = "localhost";
		int port = 60212;
		String path = "";
		
		SSLSocketFactory factory = null;
		
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

            factory = ctx.getSocketFactory();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        
		SSLSocket socket = (SSLSocket)factory.createSocket(host, port);

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
}
