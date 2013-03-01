package Client;

import java.io.*;
import java.net.ConnectException;

import javax.net.ssl.*;

import java.util.*;

import java.security.KeyStore;


public class Client {
	private SSLSocket socket;
	
	public static void main(String[] args) throws IOException{
		new Client();
				
	}
	
	public Client() throws IOException{
		run();
	}
	
	private void run() throws IOException{
		String host = "localhost";
		int port = 1337;
		String path = "";
		Scanner scan = new Scanner(System.in);
		
		System.setProperty("javax.net.ssl.trustStore", "keys/hca_trusted.jks");
		System.setProperty("javax.net.ssl.trustStorePassword", "qweqwe");
		
		System.out.println("Enter username (e.g socialstyrelsen):");
		String user = scan.next();
		System.out.println("Enter password:");
		String pass = scan.next();
		
		SSLSocketFactory factory = null;
		
		try {
            SSLContext ctx;
            KeyManagerFactory kmf;
            KeyStore ks;
            char[] passphrase = pass.toCharArray();

            ctx = SSLContext.getInstance("TLS");
            kmf = KeyManagerFactory.getInstance("SunX509");
            ks = KeyStore.getInstance("JKS");
            
            try{
            	ks.load(new FileInputStream("keys/"+user+".jks"), passphrase);
            }catch(IOException ex){
            	System.out.println("Invalid user credentials. Exiting");
            	return;
            }

            kmf.init(ks, passphrase);
            ctx.init(kmf.getKeyManagers(), null, null);

            factory = ctx.getSocketFactory();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }

        try{
        	socket = (SSLSocket)factory.createSocket(host, port);
        }catch(ConnectException ex){
        	System.out.println("Could not connect to server. Reason: " + ex.getMessage());
        	return;
        }
        
		socket.setNeedClientAuth(true);
		
		//printSocketInfo(socket);

		socket.startHandshake();
		System.out.println("Connected to server");

		String list = "list";
		String create = "create";
		String delete = "delete";
		String exit = "exit";
		boolean input = true;
		while(input){
			System.out.println("Possible commands: list, create, delete, exit.");
			String command = scan.next();
			if(command.equals(list)){
				String patient = scan.next();
				if(patient.equalsIgnoreCase("all")){
					System.out.println("TODO: command List all");
				}else{
					System.out.println("TODO: List all with patient: " + patient);
				}
			}else if(command.equals(create)){
				String patient = scan.next();
				String doctor = scan.next();
				String nurse = scan.next();
				String division = scan.next();
				String data = scan.next();
				System.out.println("Skapar journal med: Patient(" + patient + ") " +
						"Doctor(" + doctor + ") Nurse(" + nurse + ") Division" + division + ") Data(" + data + ")");
				//TODO metodanrop server! glšm ej kolla access doctor.
			}else if(command.equals(delete)){
				String record = scan.next();
				System.out.println("TODO: Remove record: " + record);

			}else if(command.equals(exit)){
				break;
			}
			else{
				System.out.println("Error: Unknown command");
			}
		}
		
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
	public static char[] readPassword(){
		Console cons = System.console();
		return cons.readPassword();
	}
}
