/*
 * Encog(tm) Core v2.5 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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
package org.encog.engine.opencl.exceptions;

/**
 * This error is thrown when the native JOCL adapter was found, but the OpenCL
 * driver was not found. This generally means you need to install a video driver
 * that is compatable with OpenCL.
 * 
 */
public class MissingOpenCLError extends OpenCLError {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;

	public MissingOpenCLError(Throwable t) {
		super(
				"Native JOCL DLL, found, but can't find OpenCL. (see: http://www.heatonresearch.com/encog/troubleshooting/nocl.html)",
				t);
	}

}
