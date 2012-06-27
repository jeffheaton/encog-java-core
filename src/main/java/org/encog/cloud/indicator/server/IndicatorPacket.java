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

import java.util.List;

/**
 * An indicator packet.
 */
public class IndicatorPacket {
	
	/**
	 * The command.
	 */
	private final String command;
	
	/**
	 * The arguments.
	 */
	private final String[] args;
	
	/**
	 * Construct a packet from he list of arguments.
	 * @param list THe argument list.
	 */
	public IndicatorPacket(List<String> list) {
		this.command = list.get(0).toUpperCase();
		
		if( list.size() == 1 ) {
			this.args = new String[0];
		} else {
			this.args = new String[list.size()-1];
		}
		
		for(int i=0;i<list.size()-1;i++) {
			this.args[i] = list.get(i+1);
		}
	}

	/**
	 * @return The command.
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * The arguments.
	 * @return The arguments.
	 */
	public String[] getArgs() {
		return args;
	}	
}
