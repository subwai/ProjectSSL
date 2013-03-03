package Application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

import com.google.gson.Gson;

public class ServerConnection implements Runnable {

	private Socket socket;
	private Person user;
	private Gson gson;
	private BufferedReader in;
	private PrintWriter out;
	private Database db;
	private Logger logger;
	private ObjectStorage dbs;

	public ServerConnection(Socket socket, Person user, Database db,
			Logger logger, ObjectStorage dbs) throws IOException {
		this.logger = logger;
		this.db = db;
		this.dbs = dbs;
		this.socket = socket;
		this.user = user;
		this.gson = new Gson();

		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
				socket.getOutputStream())));
	}

	// thread start: server client connection
	public void run() {
		try {
			while (!out.checkError()) {
				String msg = in.readLine();
				if (msg == null) {
					break;
				}
				if (!msg.equals("REQUEST")) {
					continue;
				}
				int length = Integer.parseInt(in.readLine());
				StringBuilder sb = new StringBuilder();
				while (length > sb.length()) {
					sb.append(in.readLine());
				}
				Request req = gson.fromJson(sb.toString(), Request.class);
				
				Response resp = new Response();
				synchronized(db){//build one response at a time
					resp.build(req, user, db, logger);
					dbs.save(db);
				}

				String json = gson.toJson(resp);
				out.println("RESPONSE");
				out.println(json.length());
				out.println(json);
				out.flush();
			}
			socket.close();

		} catch (IOException e) {
			System.err.println("Socket error in thread");
			try {
				socket.close();
			} catch (IOException e1) {}
			e.printStackTrace();
		}
		logger.info("Closing connection with " + user.getName());
	}

}
