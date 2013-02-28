package Client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.util.Scanner;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class Client {
	public static void main(String[] args) throws IOException {
		String host = "localhost";
		int port = 1337;
		String path = "";
		Scanner scan = new Scanner(System.in);

		System.setProperty("javax.net.ssl.trustStore", "keys/hca_trusted.jks");
		System.out.println("Enter password:");
		// String pass = new String(readPassword()); // read pw without echo!
		// replaces scanner, must be run from console.
		String pass = scan.next();
		System.setProperty("javax.net.ssl.trustStorePassword", pass);

		SSLSocketFactory factory = null;

		try {
			SSLContext ctx;
			KeyManagerFactory kmf;
			KeyStore ks;
			char[] passphrase = pass.toCharArray();

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

		SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
		socket.setNeedClientAuth(true);

		printSocketInfo(socket);

		socket.startHandshake();

		String list = "list";
		String create = "create";
		String delete = "delete";
		boolean input = true;
		while (input) {
			String command = scan.next();
			if (command.equals(list)) {
				String patient = scan.next();
				if (patient.equalsIgnoreCase("all")) {
					System.out.println("Listar alla");
					// TODO metodanrop server!
				} else {
					System.out.println("Lista alla med patient " + patient);
					// TODO metodanrop server!
				}
			} else if (command.equals(create)) {
				String patient = scan.next();
				String doctor = scan.next();
				String nurse = scan.next();
				String division = scan.next();
				String data = scan.next();
				System.out.println("Skapar journal med: Patient(" + patient
						+ ") " + "Doctor(" + doctor + ") Nurse(" + nurse
						+ ") Division (" + division + ") Data(" + data + ")");
				// TODO metodanrop server! glöm ej kolla access doctor.
			} else if (command.equals(delete)) {
				String recordID = scan.next();
				System.out.println("Tar bort journal: " + recordID);
				// TODO metodanrop server glöm ej kolla access admin!

			} else {
				System.out.println("Okänt kommando: " + command);
			}

		}

		PrintWriter out = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(socket.getOutputStream())));

		out.println("GET " + path + " HTTP/1.0");
		out.println();
		out.flush();

		/*
		 * Make sure there were no surprises
		 */
		if (out.checkError())
			System.out.println("SSLSocketClient: java.io.PrintWriter error");

		/* read response */
		BufferedReader in = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));

		String inputLine;

		while ((inputLine = in.readLine()) != null)
			System.out.println(inputLine);

		in.close();
		out.close();
		socket.close();

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

	public static char[] readPassword() {
		Console cons = System.console();
		return cons.readPassword();
	}
}
