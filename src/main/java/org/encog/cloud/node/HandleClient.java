/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
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
