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

import org.encog.cloud.indicator.IndicatorError;
import org.encog.cloud.indicator.IndicatorListener;
import org.encog.util.logging.EncogLogging;

/**
 * Handle a remote client, such as Ninja Trader.
 */
public class HandleClient implements Runnable {

	/**
	 * The link to the remote.
	 */
	private IndicatorLink link;

	/**
	 * Are we done.
	 */
	private boolean done;

	/**
	 * The indicator server that we belog to.
	 */
	private IndicatorServer server;

	/**
	 * The remote type, i.e. Ninja Trader.
	 */
	private String remoteType = "Unknown";

	/**
	 * The indicator that is listening.
	 */
	private IndicatorListener listener;

	/**
	 * The name of the indicator.
	 */
	private String indicatorName;

	/**
	 * Construct a client handler.
	 * @param s The indicator server.
	 * @param l The indicator link.
	 * @param theListener The indicator that is listening for packets.
	 */
	public HandleClient(IndicatorServer s, IndicatorLink l) {
		this.link = l;
		this.server = s;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		EncogLogging.log(EncogLogging.LEVEL_DEBUG, "Waiting for packets");

		try {
			while (!done) {
				IndicatorPacket packet = this.link.readPacket();

				// really do not care if we timeout, just keep listening
				if (packet == null) {
					continue;
				} else {
					if (packet.getCommand().equalsIgnoreCase("hello")) {
						this.remoteType = packet.getArgs()[0];
						this.indicatorName = packet.getArgs()[1];
						this.listener = this.server
								.resolveIndicator(this.indicatorName);
						this.listener.notifyConnect(this.link);
					} else if (packet.getCommand().equalsIgnoreCase("goodbye")) {
						this.done = true;
					} else {
						this.listener.notifyPacket(packet);
					}
				}
			}
		} catch (IndicatorError ex) {
			ex.printStackTrace();
			if (ex.getCause() == null) {
				EncogLogging.log(EncogLogging.LEVEL_DEBUG,
						"Error, ending connection:" + ex.getMessage());
				String[] args = { ex.getMessage() };
				link.writePacket("error", args);
			} else {
				EncogLogging.log(EncogLogging.LEVEL_DEBUG,
						"Client ended connection:" + ex.getMessage());
			}
			this.done = true;
		} catch (Throwable t) {
			EncogLogging.log(EncogLogging.LEVEL_CRITICAL, t);
			t.printStackTrace();
		} finally {
			this.done = true;
			this.server.getConnections().remove(this);
			if (this.listener != null) {
				this.listener.notifyTermination();
			}
			this.server.notifyListenersConnections(this.link, false);
			EncogLogging.log(EncogLogging.LEVEL_DEBUG,
					"Shutting down client handler");
			this.link.close();
		}

	}

	/**
	 * @return The remote type, i.e. Ninja Trader.
	 */
	public String getRemoteType() {
		return this.remoteType;
	}

	/**
	 * @return the indicatorName
	 */
	public String getIndicatorName() {
		return indicatorName;
	}

	/**
	 * @return The link that we are using.
	 */
	public IndicatorLink getLink() {
		return this.link;
	}
}
