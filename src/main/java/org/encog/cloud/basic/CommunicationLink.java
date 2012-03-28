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
package org.encog.cloud.basic;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import org.encog.EncogError;
import org.encog.cloud.node.CloudNode;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ParseCSVLine;
import org.encog.util.logging.EncogLogging;

public class CommunicationLink {	
	public final int SOCKET_TIMEOUT = 25000;
	private Socket socket;
	private ByteArrayOutputStream outputHolder;
	private DataOutputStream outputToRemote;
	private BufferedReader inputFromRemote;
	private OutputStream socketOut;
	private ParseCSVLine parseLine = new ParseCSVLine(CSVFormat.EG_FORMAT);
	private int packets;
	private CloudNode parentNode;

	public static String simpleHash(String str) {
		int result = 0;
		for(int i=0;i<str.length();i++) {
			if( Character.isLetterOrDigit(str.charAt(i)))
				result+=str.charAt(i)*(i*10);
			result %= 0xffff;
		}
		
		return Integer.toHexString(result).toLowerCase();
	}
	
	public CommunicationLink(CloudNode node, Socket s) {
		try {
			this.parentNode = node;
			this.socket = s;
			this.socket.setSoTimeout(SOCKET_TIMEOUT);
			
			this.socketOut = this.socket.getOutputStream();
			this.outputHolder = new ByteArrayOutputStream();
			this.outputToRemote = new DataOutputStream(this.outputHolder);
			this.inputFromRemote = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		} catch (IOException ex) {
			throw new CloudError(ex);
		}

	}

	
	public void writePacket(String command, Object[] args) {
		try {
			StringBuilder line = new StringBuilder();
			line.append("\"");
			line.append(command);
			line.append("\"");
			for (int i = 0; i < args.length; i++) {
				line.append(",\"");
				line.append(args[i].toString());
				line.append("\"");
			}
			line.append("\n");
			this.outputToRemote.writeChars(line.toString());
			this.flushOutput();
		} catch (IOException ex) {
			throw new CloudError(ex);
		}
	}

	public CloudPacket readPacket() {
		
		try {
			String str = this.inputFromRemote.readLine();
			List<String> list = parseLine.parse(str);
			this.packets++;
			
			EncogLogging.log(EncogLogging.LEVEL_DEBUG, "Received Packet: " + str);
			return new CloudPacket(list);	
		} catch(IOException ex) {
			throw new CloudError(ex);
		}
		
	}
	
	private void flushOutput() {
		try {
			this.socketOut.write(this.outputHolder.toByteArray());
			this.outputHolder.reset();	
		} catch (IOException e) {
			throw new EncogError(e);
		}
		
	}

	public Socket getSocket() {
		return this.socket;
	}
	
	public int getPackets() {
		return this.packets;
	}

	public void close() {
		try {
			this.socket.close();
		} catch (IOException e) {
			// ignore, we were trying to close
		}
		
	}

	public void requestSignal(List<String> dataSource) {
		writePacket("signals",dataSource.toArray());
		
	}
}
