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
package org.encog.engine.opencl;

import org.encog.engine.EncogEngineError;

/**
 * This exception is thrown when the underlying OpenCL system returns with the
 * CL_OUT_OF_RESOURCES. This generally either means that you have run out of
 * OpenCL resources. Or the kernel took too long to execute, and the OS shut it
 * down. By default, most operating systems do not allow kernel's much time to
 * execute, because the user cannot interact with the display while a kernel is
 * running.
 * 
 * The usual solution to this is to adjust the OpenCLTrainingProfile object to
 * process fewer training items per kernel execution.
 * 
 * See:
 * 
 * http://www.heatonresearch.com/encog/troubleshooting/ooresource.html
 */
public class OutOfOpenCLResources extends EncogEngineError {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;

	public OutOfOpenCLResources(Throwable t) {
		super(
				"Out of OpenCL resources or hit OS-imposed timeout. (see: http://www.heatonresearch.com/encog/troubleshooting/ooresource.html)",
				t);
	}

}
