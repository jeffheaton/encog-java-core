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

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.encog.EncogError;

public class CommunicationLink {	
	public final int SOCKET_TIMEOUT = 25000;
	private Socket socket;
	private ByteArrayOutputStream outputHolder;
	private DataOutputStream outputToRemote;
	private DataInputStream inputFromRemote;
	private OutputStream socketOut;

	public static String simpleHash(String str) {
		int result = 0;
		for(int i=0;i<str.length();i++) {
			if( Character.isLetterOrDigit(str.charAt(i)))
				result+=str.charAt(i)*(i*10);
			result %= 0xffff;
		}
		
		return Integer.toHexString(result).toLowerCase();
	}
	
	public CommunicationLink(Socket s) {
		try {
			this.socket = s;
			this.socket.setSoTimeout(SOCKET_TIMEOUT);
			
			this.socketOut = this.socket.getOutputStream();
			this.outputHolder = new ByteArrayOutputStream();
			this.outputToRemote = new DataOutputStream(this.outputHolder);
			this.inputFromRemote = new DataInputStream(this.socket
					.getInputStream());
		} catch (IOException ex) {
			throw new CloudError(ex);
		}

	}

	public void writePacket(int command) {
		String[] args = new String[0];
		writePacket(command,args);
	}
	
	public void writePacketLogin(String uid, String pwd) {
		String[] args = { uid, pwd };
		writePacket(CloudPacket.PACKET_LOGIN,args);
	}
	
	public void writePacket(int command, String[] args) {
		try {
			// first determine length
			int length = 0;
			for(int i=0;i<args.length;i++) {
				length+=args[i].length();
				length++;
			}
			
			// write the packet
			this.outputToRemote.writeByte('E');
			this.outputToRemote.writeByte('N');
			this.outputToRemote.writeByte('C');
			this.outputToRemote.writeByte('O');
			this.outputToRemote.writeByte('G');
			this.outputToRemote.writeByte(0);// string terminator
			this.outputToRemote.writeByte(0);//version
			this.outputToRemote.writeByte(command);
			this.outputToRemote.writeByte(0);//count
			this.outputToRemote.writeLong(0);//time
			this.outputToRemote.writeInt(length);//size
			
			// write the arguments
			byte[] b = new byte[length];
			int index = 0;
			
			for(int i=0;i<args.length;i++) {
				String str = args[i];
				for(int j=0;j<str.length();j++) {
					b[index++] = (byte)str.charAt(j);
				}
				b[index++] = 0;
			}
			
			this.outputToRemote.write(b);
			this.flushOutput();
			
		} catch (IOException ex) {
			throw new CloudError(ex);
		}
	}

	public CloudPacket readPacket() throws IOException {
		
		try {
		String[] args = null;

		if (this.inputFromRemote.readByte() != 'E')
			return null;
		if (this.inputFromRemote.readByte() != 'R')
			return null;
		if (this.inputFromRemote.readByte() != 'T')
			return null;
		if (this.inputFromRemote.readByte() != 'P')
			return null;
		
		int version = this.inputFromRemote.readByte();
		int command = this.inputFromRemote.readByte();
		int count = this.inputFromRemote.readByte();
		long time = this.inputFromRemote.readLong();
		int size = this.inputFromRemote.readInt();
		
		
		
		if( size>0 ) {
			List<String> list = new ArrayList<String>();
			StringBuilder builder = new StringBuilder();
			byte[] b = new byte[size];
			this.inputFromRemote.read(b);
			for(int i=0;i<b.length;i++) {
				if( b[i]==0 ) {
					list.add(builder.toString());
					builder.setLength(0);
				} else {
					builder.append((char)b[i]);
				}
			}
			
			args = new String[list.size()];
			for(int i=0;i<args.length;i++) {
				args[i] = list.get(i);
			}
		}
		
		return new CloudPacket(command,args);
		}
		catch(SocketTimeoutException ex) {
			return null;
		}
	}

	public void writeStatus(boolean s, String message) {
		String[] args = new String[2];
		args[0] = s ? "1":"0";
		args[1] = message;
		writePacket(CloudPacket.PACKET_STATUS,args);		
	}

	public void writePacketIdentify(String name) {
		String[] args = { name };
		writePacket(CloudPacket.PACKET_IDENTIFY,args);
		
	}
	
	private void flushOutput() {
		try {
			this.socketOut.write(this.outputHolder.toByteArray());
			this.outputHolder.reset();	
		} catch (IOException e) {
			throw new EncogError(e);
		}
		
	}
}
