package Application;

import java.io.*;
import java.net.ConnectException;
import java.net.SocketException;

import javax.net.ssl.*;

import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.security.KeyStore;


public class Client {
	private SSLSocket socket;
	private PrintWriter out;
	private BufferedReader in;
	
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
		
		System.out.println("Enter username (e.g socialstyrelsen):");
		String user = scan.next();
		System.out.println("Enter password:");
		char[] passphrase = readPassword(scan);
		
		SSLSocketFactory factory = null;
		
		try {
            SSLContext ctx;
            KeyManagerFactory kmf;
            KeyStore ks;


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

		System.out.print("Connecting to server..  ");
		socket.startHandshake();
		socket.setKeepAlive(true);
		
		out = new PrintWriter(
                new BufferedWriter(
                new OutputStreamWriter(
                socket.getOutputStream())));
		
		in = new BufferedReader(
                new InputStreamReader(
                socket.getInputStream()));
		
		
		System.out.println("Connected");
		String help = "\n\nPossible commands:" +
				"\n\tlist patient|all" +
				"\n\tcreate patient doctor nurse division data" +
				"\n\tdelete record" +
				"\n\thelp" +
				"\n\texit\n";
		
		try{
			System.out.println(help);
			
			while(!out.checkError()){
				System.out.print("hc>");
				String command = scan.next();
				Request req = new Request();
				if(command.equals("list")){
					String patient = scan.next();
					req.action = "list";
					req.args.add(patient);//all or id
				}else if(command.equals("create")){
					req.action = "create";
					String patient = scan.next();
					String doctor = scan.next();
					String nurse = scan.next();
					String division = scan.next();
					String data = scan.next();
					req.args.add(patient);
					req.args.add(doctor);
					req.args.add(nurse);
					req.args.add(division);
					req.args.add(data);
				}else if(command.equals("delete")){
					String record = scan.next();
					req.action = "delete";
					req.args.add(record);
	
				}else if(command.equals("help")){
					System.out.println(help);
				}else if(command.equals("exit")){
					break;
				}
				else{
					System.out.print("Error: Unknown command");
					req.action="heartbeat";
				}
				if(req.action != null){
					Response resp = request(req);
					System.out.println(resp.message);
				}
			}
		}catch(SocketException ex){
			System.err.println("\nUnexpectedly Disconnected from server");
		}catch(IOException ex){
			System.err.println("\n"+ex.getMessage());
		}
		System.out.println("Closing connection.");
        socket.close();
	}
	
	
	private Response request(Request req) throws IOException, SocketException{
		Response response = null;
		try{
			
			Gson gson = new Gson();
			String json = gson.toJson(req);

	        out.println("REQUEST");
	        out.println(json.length());
			out.println(json);
			out.flush();
			
			while(!in.readLine().equals("RESPONSE")){
				System.err.println("proto error");
				continue;
			}

        	int length = Integer.parseInt(in.readLine());
        	StringBuilder sb = new StringBuilder();
        	while(length > sb.length()){
        		sb.append(in.readLine());
        	}
			String respStr = sb.toString();
	        response = gson.fromJson(sb.toString(), Response.class);
	        
		}catch(JsonSyntaxException ex){
			if(ex.getCause() != null){
				throw (SocketException)ex.getCause();
			}else{
				throw new IOException("Could not parse server response");
			}
		}
        return response;
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


	private char[] readPassword(Scanner scan) throws IOException {
	    if (System.console() != null){
	        return System.console().readPassword();
	    }else{
	    	return scan.next().toCharArray();
	    }
	    
	}
}
