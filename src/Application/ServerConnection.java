package Application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;

public class ServerConnection extends Thread{
	
	private Socket socket;
	private Person p;
	private Gson gson;
	
	public ServerConnection(Socket socket, Person p){
		this.socket = socket;
		this.p = p;
		this.gson = new Gson();
	}
	
	//thread start: server client connection
	public void run(){
		System.out.println("Starting thread for " + p.getName());
		try{
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                socket.getInputStream()));
        
		PrintWriter out = new PrintWriter(
                new BufferedWriter(
                new OutputStreamWriter(
                socket.getOutputStream())));
        
        while(!socket.isInputShutdown()){
        	String msg = in.readLine();
        	if(msg == null){
        		break;
        	}
        	if(!msg.equals("REQUEST")){
        		continue;
        	}
        	int length = Integer.parseInt(in.readLine());
        	StringBuilder sb = new StringBuilder();
        	while(length > sb.length()){
        		sb.append(in.readLine());
        	}
	        Request req = gson.fromJson(sb.toString(),Request.class);
	        
	        Response resp = new Response();
	        resp.message = "server received action:" + req.action;
	        resp.message += "\nargs:" + req.args.toString();
			
	        String json = gson.toJson(resp);
	        out.println("RESPONSE");
	        out.println(json.length());
			out.println(json);
			out.flush();
        }
        
        System.err.println("Closing connection with " + p.getName());
        socket.close();

		}catch(IOException e){
			System.err.println("Socket error in thread");
			e.printStackTrace();
		}
	}

}
