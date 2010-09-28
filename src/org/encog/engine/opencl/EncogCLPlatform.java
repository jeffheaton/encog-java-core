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

import java.util.ArrayList;
import java.util.List;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_context;
import org.jocl.cl_context_properties;
import org.jocl.cl_device_id;
import org.jocl.cl_platform_id;

/**
 * An Encog CL platform.
 * 
 */
public class EncogCLPlatform extends EncogCLItem {

	/**
	 * The OpenCL platform.
	 */
	private final cl_platform_id platform;

	/**
	 * The OpenCL context for this platform. One context is created for each
	 * platform.W
	 */
	private final cl_context context;

	/**
	 * All of the devices on this platform.
	 */
	private final List<EncogCLDevice> devices = new ArrayList<EncogCLDevice>();

	/**
	 * Construct an OpenCL platform.
	 * 
	 * @param platform
	 *            The OpenCL platform.
	 */
	public EncogCLPlatform(final cl_platform_id platform) {
		final long[] numBytes = new long[1];
		this.platform = platform;

		final cl_context_properties contextProperties 
			= new cl_context_properties();
		contextProperties.addProperty(CL.CL_CONTEXT_PLATFORM, platform);

		this.context = CL.clCreateContextFromType(contextProperties,
				CL.CL_DEVICE_TYPE_ALL, null, null, null);

		setName(getPlatformString(CL.CL_PLATFORM_NAME).trim());
		setVender(getPlatformString(CL.CL_PLATFORM_VENDOR));
		setEnabled(true);

		CL.clGetContextInfo(this.context, CL.CL_CONTEXT_DEVICES, 0, null,
				numBytes);

		final int numDevices = (int) numBytes[0] / Sizeof.cl_device_id;
		final cl_device_id[] devicesList = new cl_device_id[numDevices];

		CL.clGetContextInfo(this.context, CL.CL_CONTEXT_DEVICES, numBytes[0],
				Pointer.to(devicesList), null);

		for (final cl_device_id deviceID : devicesList) {
			final EncogCLDevice adapter = new EncogCLDevice(this, deviceID);
			this.devices.add(adapter);
		}
	}

	/**
	 * @return The context for this platform.
	 */
	public cl_context getContext() {
		return this.context;
	}

	/**
	 * @return All devices on this platform.
	 */
	public List<EncogCLDevice> getDevices() {
		return this.devices;
	}

	/**
	 * @return The OpenCL platform.
	 */
	public cl_platform_id getPlatform() {
		return this.platform;
	}

	/**
	 * Get a config string from the platform.
	 * 
	 * @param param
	 *            The param to get.
	 * @return The config string.
	 */
	public String getPlatformString(final int param) {
		final byte[] buffer = new byte[255];
		final long[] len = new long[1];

		CL.clGetPlatformInfo(this.platform, param, buffer.length, Pointer
				.to(buffer), len);
		final String name = new String(buffer, 0, (int) len[0]);
		return name;
	}

}
