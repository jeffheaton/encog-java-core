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
package org.encog.cloud.indicator.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.cloud.indicator.IndicatorConnectionListener;
import org.encog.cloud.indicator.IndicatorError;
import org.encog.cloud.indicator.IndicatorFactory;
import org.encog.cloud.indicator.IndicatorListener;
import org.encog.cloud.indicator.basic.DownloadIndicatorFactory;
import org.encog.util.logging.EncogLogging;

/**
 * The Encog Indicator server.  This allows the Encog Framework Indicator to be
 * used in a trading platform, such as NinjaTrader or MT4/5.  The remote indicator
 * is always created in whatever native language the trading platform requires. Then
 * a socketed connection is made back to Encog.  
 */
public class IndicatorServer implements Runnable {
	
	/**
	 * The default port.
	 */
	public static final int STANDARD_ENCOG_PORT = 5128;
	
	/**
	 * The port actually being used.
	 */
	private int port;
	
	/**
	 * The socket that we are listening on.
	 */
	private ServerSocket listenSocket;
	
	/**
	 * The background thread used to listen.
	 */
	private Thread thread;
	
	/**
	 * True, if the server is running.
	 */
	private boolean running;
	
	/**
	 * The connections that have been made to the server.
	 */
	private final List<HandleClient> connections = new ArrayList<HandleClient>();
	
	/**
	 * ALl registered listeners.
	 */
	private final List<IndicatorConnectionListener> listeners = new ArrayList<IndicatorConnectionListener>();
	
	/** 
	 * The indicator factories by name.
	 */
	private final Map<String,IndicatorFactory> factoryMap = new HashMap<String,IndicatorFactory>(); 

	/**
	 * Construct a server.
	 * @param p The port.
	 */
	public IndicatorServer(int p) {
		this.port = p;
	}

	/**
	 * Construct a server, and use the standard port (5128).
	 */
	public IndicatorServer() {
		this(STANDARD_ENCOG_PORT);
	}

	/**
	 * {@inheritDoc}
	 */
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
				IndicatorListener listener = this.factoryMap.values().iterator().next().create();
				HandleClient hc = new HandleClient(this, link, listener);
				this.connections.add(hc);
				Thread t = new Thread(hc);
				t.start();
			}
			catch (SocketTimeoutException ex) {
				// ignore
			}
			catch (IOException ex) {
				throw new IndicatorError(ex);
			}
		}

		try {
			this.listenSocket.close();
		} catch (IOException ex) {
			throw new IndicatorError(ex);
		}
	}

	/**
	 * Start the server.
	 */
	public void start() {
		try {
			this.listenSocket = new ServerSocket(port);
			this.listenSocket.setSoTimeout(2000);
			this.thread = new Thread(this);
			this.thread.start();
		} catch (IOException ex) {
			throw new IndicatorError(ex);
		}
	}

	/**
	 * Shutdown the server.
	 */
	public void shutdown() {
		this.running = false;
	}

	/**
	 * @return The port the server is listening on.
	 */
	public int getPort() {
		return this.port;
	}

	/**
	 * @return The connections.
	 */
	public List<HandleClient> getConnections() {
		return this.connections;
	}

	/**
	 * @return the listeners
	 */
	public List<IndicatorConnectionListener> getListeners() {
		return listeners;
	}
	
	/**
	 * Add a listener.
	 * @param listener The listener to add.
	 */
	public void addListener(IndicatorConnectionListener listener) {
		this.listeners.add(listener);
	}
		
	public void removeListener(IndicatorConnectionListener listener) {
		this.listeners.remove(listener);
	}
	
	public void clearListeners() {
		this.listeners.clear();
	}
	
	public void notifyListenersConnections(IndicatorLink link, boolean hasOpened) {
		Object[] list = this.listeners.toArray();
		
		for(int i=0;i<list.length;i++) {
			IndicatorConnectionListener listener = (IndicatorConnectionListener)list[i];
			listener.notifyConnections(link, hasOpened);
		}
	}

	public void addIndicatorFactory(DownloadIndicatorFactory ind) {
		this.factoryMap.put(ind.getName(), ind);		
	}

	public void waitForIndicatorCompletion() {
		// first wait for at least one indicator to start up
		while( this.connections.size()==0 ) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// now wait for indicators to go to zero
		while( this.connections.size()!=0 ) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// shutdown
		shutdown();
	}
}
