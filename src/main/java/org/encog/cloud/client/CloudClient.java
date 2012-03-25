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
			//this.link = new CommunicationLink(clientSocket);
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
}
