package org.encog.cloud.client;

import java.io.IOException;
import java.net.Socket;

import org.encog.cloud.basic.CommunicationLink;
import org.encog.cloud.basic.CloudError;
import org.encog.cloud.basic.CloudPacket;

public class CloudClient {

	private String name;
	private String host;
	private int port;
	private CommunicationLink link;

	public CloudClient(String n, String h, int p) {
		try {
			this.name = n;
			this.port = p;
			this.host = h;
			Socket clientSocket = new Socket(h, port);
			this.link = new CommunicationLink(clientSocket);
		} catch (IOException e) {
			throw new CloudError(e);
		}
	}

	public CloudClient(String n, String h) {
		this(n, h, 7500);
	}

	public CloudClient(String n) {
		this(n, "localhost");
	}
	
	private void expectSuccessPacket() throws IOException {
		CloudPacket packet = this.link.readPacket();
		
		if( packet==null ) {
			throw new CloudError("Timeout waiting for a response packet.");
		}

		if (packet.getCommand() != CloudPacket.PACKET_STATUS) {
			throw new CloudError("Wrong packet type: " + packet.getCommand());
		}

		String[] args = packet.getArgs();

		if (args.length < 2) {
			throw new CloudError("Wrong number of string arguments: "
					+ args.length);
		}

		if (!args[0].equals("1")) {
			throw new CloudError("Error: " + args[1]);
		}

		
	}

	public void login(String uid, String pwd) {
		try {
			this.link.writePacketLogin(uid, CommunicationLink.simpleHash(pwd));
			expectSuccessPacket();
		} catch (IOException ex) {
			throw new CloudError(ex);
		}

	}

	public void logout() {
		try {
			this.link.writePacket(CloudPacket.PACKET_LOGOUT);
			this.link.readPacket();
		} catch (IOException ex) {
			throw new CloudError(ex);
		}
	}

	public static void main(String argv[]) throws Exception {
		CloudClient client = new CloudClient("local");
		client.login("test", "test");
		client.identify();
		client.logout();
	}

	public void identify() throws IOException {
		this.link.writePacketIdentify(this.name);
		expectSuccessPacket();
		
	}
}