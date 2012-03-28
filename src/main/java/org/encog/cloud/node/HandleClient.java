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

import org.encog.cloud.basic.CloudError;
import org.encog.cloud.basic.CloudPacket;
import org.encog.cloud.basic.CommunicationLink;
import org.encog.util.logging.EncogLogging;

public class HandleClient implements Runnable {

	private CommunicationLink link;
	private boolean done;
	private CloudNode server;
	private String userID;
	private String remoteType = "Unknown";
	
	public HandleClient(CloudNode s, CommunicationLink l) {
		this.link = l;
		this.server = s;
	}
		

	
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	@Override
	public void run() {
		EncogLogging.log(EncogLogging.LEVEL_DEBUG,"Waiting for packets");
		while(!done) {					
			try {
				CloudPacket packet = this.link.readPacket();
				
				// really do not care if we timeout, just keep listening
				if( packet==null ) {
					continue;
				} else {
					if( packet.getCommand().equalsIgnoreCase("hello") ) {
						this.remoteType = packet.getArgs()[0];
					}
					else if( packet.getCommand().equalsIgnoreCase("goodbye") ) {
						this.done = true;
					}
					else {
						this.server.notifyListenersPacket(packet);
					}
				}				
			} catch (CloudError ex) {
				EncogLogging.log(EncogLogging.LEVEL_DEBUG,"Client ended connection.");
				this.done = true;
			} 
		}		
		this.link.close();
		this.server.getConnections().remove(this);
		this.server.notifyListenersConnections(this.link,false);
		EncogLogging.log(EncogLogging.LEVEL_DEBUG,"Shutting down client handler");
	}
	
	public String getRemoteType() {
		return this.remoteType;
	}



	public CommunicationLink getLink() {
		return this.link;
	}
}
