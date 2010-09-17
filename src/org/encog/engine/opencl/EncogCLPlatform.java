/*
 * Encog(tm) Core v2.5 
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.engine.opencl;

import java.util.ArrayList;
import java.util.List;

import org.encog.engine.opencl.kernels.KernelNetworkTrain;
import org.encog.engine.opencl.kernels.KernelVectorAdd;
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
	 * platform.
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
