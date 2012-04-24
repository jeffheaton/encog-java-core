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
package org.encog.cloud.indicator;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.cloud.basic.CloudError;
import org.encog.cloud.basic.CloudPacket;
import org.encog.util.logging.EncogLogging;

public class IndicatorServer implements Runnable {
	public static final int STANDARD_ENCOG_PORT = 5128;
	private int port;
	private ServerSocket listenSocket;
	private Thread thread;
	private Map<String, String> accounts = new HashMap<String, String>();
	private boolean running;
	private final List<HandleClient> connections = new ArrayList<HandleClient>();
	private final List<IndicatorListener> listeners = new ArrayList<IndicatorListener>();

	public IndicatorServer(int p) {
		this.port = p;
	}

	public void addUser(String uid, String pwd) {
		this.accounts.put(uid.toLowerCase(), IndicatorLink.simpleHash(pwd));
	}

	public IndicatorServer() {
		this(STANDARD_ENCOG_PORT);
	}

	public Map<String, String> getAccounts() {
		return this.accounts;
	}

	@Override
	public void run() {
		this.running = true;
		while (this.running) {
			try {
				EncogLogging.log(EncogLogging.LEVEL_DEBUG, "Begin listen");				
				Socket connectionSocket = listenSocket.accept();
				EncogLogging.log(EncogLogging.LEVEL_DEBUG, "Connection from: " + connectionSocket.getRemoteSocketAddress().toString());
				IndicatorLink link = new IndicatorLink(this,connectionSocket);				
				notifyListenersConnections(link, true);
				HandleClient hc = new HandleClient(this, link);
				this.connections.add(hc);
				Thread t = new Thread(hc);
				t.start();
			}
			catch (SocketTimeoutException ex) {
				// ignore
			}
			catch (IOException ex) {
				throw new CloudError(ex);
			}
		}

		try {
			this.listenSocket.close();
		} catch (IOException ex) {
			throw new CloudError(ex);
		}
	}

	public void start() {
		try {
			this.listenSocket = new ServerSocket(port);
			this.listenSocket.setSoTimeout(2000);
			this.thread = new Thread(this);
			this.thread.start();
		} catch (IOException ex) {
			throw new CloudError(ex);
		}
	}

	public void shutdown() {
		this.running = false;
	}

	public int getPort() {
		return this.port;
	}

	public List<HandleClient> getConnections() {
		return this.connections;
	}

	/**
	 * @return the listeners
	 */
	public List<IndicatorListener> getListeners() {
		return listeners;
	}
	
	public void addListener(IndicatorListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeListener(IndicatorListener listener) {
		this.listeners.remove(listener);
	}
	
	public void clearListeners() {
		this.listeners.clear();
	}
	
	public void notifyListenersConnections(IndicatorLink link, boolean hasOpened) {
		Object[] list = this.listeners.toArray();
		
		for(int i=0;i<list.length;i++) {
			IndicatorListener listener = (IndicatorListener)list[i];
			listener.notifyConnections(link, hasOpened);
		}
	}
	
	public void notifyListenersPacket(CloudPacket packet) {
		Object[] list = this.listeners.toArray();
		
		for(int i=0;i<list.length;i++) {
			IndicatorListener listener = (IndicatorListener)list[i];
			listener.notifyPacket(packet);
		}
	}
}
