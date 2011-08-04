package org.encog.cloud.node;

import java.io.IOException;

import org.encog.cloud.basic.CommunicationLink;
import org.encog.cloud.basic.CloudPacket;

public class HandleClient implements Runnable {

	private CommunicationLink link;
	private boolean done;
	private CloudNode server;
	private String userID;
	
	public HandleClient(CloudNode s, CommunicationLink l) {
		this.link = l;
		this.server = s;
	}
		
	private void handleLogin(CloudPacket packet) {
		String uid = packet.getArgs()[0];
		String pwd = packet.getArgs()[1];
		
		System.out.println("UID:" + uid);
		System.out.println("PWD:" + pwd);
		
		boolean success = false;
		
		if( server.getAccounts().size()==0 ) {
			success = true;
		} else if( !server.getAccounts().containsKey(uid.toLowerCase()) ) {
			success = false;
		} else {
			String p = server.getAccounts().get(uid.toLowerCase());
			success = p.equals(pwd);
		}
		
		if( success ) {
			this.userID = uid;
			this.link.writeStatus(true,"");	
		} else {
			this.userID = null;
			this.link.writeStatus(false,"Login failure.");
		}		
	}
	
	private void handleIdentify() {
		this.link.writeStatus(true, "");
	}
	
	private void handleLogout() {
		this.link.writeStatus(true, "");
		done = true;
		System.out.println("Logging out client");
	}
	
	
	
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	@Override
	public void run() {
		while(!done) {					
			try {
				System.out.println("Waiting for packets");
				CloudPacket packet = this.link.readPacket();
				
				// really do not care if we timeout, just keep listening
				if( packet==null ) {
					continue;
				}
				
				if( packet!=null ) {
					switch(packet.getCommand()) {
					case CloudPacket.PACKET_LOGIN:
						handleLogin(packet);
						break;
					case CloudPacket.PACKET_LOGOUT:
						handleLogout();
						break;
					case CloudPacket.PACKET_IDENTIFY:
						handleIdentify();
						break;
					}
				}
				
				Thread.sleep(1000);
			} catch (InterruptedException e) {

			} catch (IOException ex) {
				System.out.println("Client ended connection.");
				return;
			} 
		}		
		System.out.println("Shutting down client handler");
	}
}
