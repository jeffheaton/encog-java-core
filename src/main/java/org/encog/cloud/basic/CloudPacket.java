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

public class CloudPacket {
	
	public static final int PACKET_STATUS = 0;
	public static final int PACKET_LOGIN = 1;
	public static final int PACKET_LOGOUT = 2;
	public static final int PACKET_IDENTIFY = 3;
	
	private final int command;
	private final String[] args;
	
	public CloudPacket(int c, String[] a) {
		this.command = c;
		
		if( a==null ) {
			this.args = new String[0];
		} else {
			this.args = new String[a.length];
		}
		
		for(int i=0;i<this.args.length;i++) {
			this.args[i] = a[i];
		}
	}

	public int getCommand() {
		return command;
	}

	public String[] getArgs() {
		return args;
	}
	
	
	
}
