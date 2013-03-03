package Application;

import java.io.IOException;
import java.util.Scanner;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.security.cert.X509Certificate;

public class Shared {
	static final String SERVER_KEY = "server";
	static final int SERVER_PORT = 1337;
	protected static char[] readPassword(Scanner scan) throws IOException {
		if (System.console() != null) {
			return System.console().readPassword();
		} else {
			return scan.next().toCharArray();
		}

	}
	protected static String commonNameFrom(SSLSocket socket) throws SSLPeerUnverifiedException{
		SSLSession session = socket.getSession();
		X509Certificate cert = session.getPeerCertificateChain()[0];
		// extract CN from DN
		LdapName ldapDN = null;
		try {
			ldapDN = new LdapName(cert.getSubjectDN().getName());
		} catch (InvalidNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(ldapDN == null){
			return "";
		}
		String username = "";
		for (Rdn rdn : ldapDN.getRdns()) {
			if (rdn.getType().trim().toUpperCase().equals("CN")) {
				username = rdn.getValue().toString().trim()
						.toLowerCase();
			}
		}
		return username;
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
	private static void printServerSocketInfo(SSLServerSocket s) {
        System.out.println("Server socket class: " + s.getClass());
        System.out.println("   Socker address = "
                        + s.getInetAddress().toString());
        System.out.println("   Socker port = " + s.getLocalPort());
        System.out.println("   Need client authentication = "
                        + s.getNeedClientAuth());
        System.out.println("   Want client authentication = "
                        + s.getWantClientAuth());
        System.out.println("   Use client mode = " + s.getUseClientMode());
	}
}
