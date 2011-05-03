/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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
package org.encog.util.logging;


public class EncogLogging {
	
	public static final int LEVEL_DEBUG = 0;
	public static final int LEVEL_INFO = 1;
	public static final int LEVEL_ERROR = 2;
	public static final int LEVEL_CRITICAL = 3;
	
	public static int currentLevel;
	
	public synchronized int getCurrentLevel() {
		return currentLevel;
	}
	
	public synchronized void setCurrentLevel(int level) {
		this.currentLevel = level;
	}
	
	public static void log(int level, String message) {
		
	}
	
	public static void log(int level, Throwable t) { 
		
	}

	public static void log(Throwable t) {
		log(LEVEL_ERROR,t);
		
	}
	
	
}
