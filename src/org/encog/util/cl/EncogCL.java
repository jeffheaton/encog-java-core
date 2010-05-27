package org.encog.util.cl;

import java.util.ArrayList;
import java.util.List;

import org.encog.EncogError;
import org.jocl.CL;
import org.jocl.cl_platform_id;

/**
 * An OpenCL platform. A platform is a collection of OpenCL devices from the
 * same vendor. Often, you will have only a single platform.
 */
public class EncogCL {
	/**
	 * The platforms detected.
	 */
	private final List<EncogCLPlatform> platforms = 
		new ArrayList<EncogCLPlatform>();

	/**
	 * All devices, from all platforms.
	 */
	private final List<EncogCLDevice> devices = new ArrayList<EncogCLDevice>();

	/**
	 * Allows you to determine how much work the CPU and GPU get. For example,
	 * to give the CL/GPU twice as much work as the CPU, specify 1.5. To give it
	 * half as much, choose 0.5.
	 */
	private final double enforcedCLRatio;

	/**
	 * The number of CL threads to use, defaults to 200.
	 */
	private final int clThreads;

	/**
	 * The size of a CL workload, defaults to 10.
	 */
	private final int clWorkloadSize;

	/**
	 * Construct an Encog OpenCL object.
	 */
	public EncogCL() {
		final int[] numPlatforms = new int[1];
		this.enforcedCLRatio = 1.0;
		this.clThreads = 200;
		this.clWorkloadSize = 10;

		final cl_platform_id[] platformIDs = new cl_platform_id[5];
		CL.clGetPlatformIDs(platformIDs.length, platformIDs, numPlatforms);

		if (numPlatforms[0] == 0) {
			throw new EncogError("Can't find any OpenCL platforms");
		}

		for (int i = 0; i < numPlatforms[0]; i++) {
			final cl_platform_id platformID = platformIDs[i];
			final EncogCLPlatform platform = new EncogCLPlatform(platformID);
			this.platforms.add(platform);

			for (final EncogCLDevice device : platform.getDevices()) {
				this.devices.add(device);
			}
		}
		
		
		CL.setExceptionsEnabled(true);
	}

	/**
	 * @return True if CPUs are present.
	 */
	public boolean areCPUsPresent() {
		for (final EncogCLDevice device : this.devices) {
			if (device.isCPU()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Choose a device. Simply returns the first device detected.
	 * 
	 * @return The first device detected.
	 */
	public EncogCLDevice chooseDevice() {
		if (this.devices.size() < 1) {
			return null;
		} else {
			return this.devices.get(0);
		}
	}

	/**
	 * Disable all devices that are CPU's. This is a good idea to do if you are
	 * going to use regular CPU processing in conjunction with OpenCL processing
	 * where the CPU is made to look like a OpenCL device. Otherwise, you end up
	 * with the CPU serving as both a "regular CPU training task" and as an
	 * "OpenCL training task".
	 */
	public void disableAllCPUs() {
		for (final EncogCLDevice device : this.devices) {
			if (device.isCPU()) {
				device.setEnabled(false);
			}
		}
	}

	/**
	 * Enable all devices that are CPU's.
	 */
	public void enableAllCPUs() {
		for (final EncogCLDevice device : this.devices) {
			if (device.isCPU()) {
				device.setEnabled(true);
			}
		}
	}

	/**
	 * @return The devices used by EncogCL, this spans over platform boundaries.
	 */
	public List<EncogCLDevice> getDevices() {
		return this.devices;
	}

	/**
	 * @return The devices used by EncogCL, this spans over platform boundaries.
	 *         Does not include disabled devices or devices from disabled
	 *         platforms.
	 */
	public List<EncogCLDevice> getEnabledDevices() {

		final List<EncogCLDevice> result = new ArrayList<EncogCLDevice>();
		for (final EncogCLDevice device : this.devices) {
			if (device.isEnabled() && device.getPlatform().isEnabled()) {
				result.add(device);
			}
		}

		return result;
	}

	/**
	 * @return All platforms detected.
	 */
	public List<EncogCLPlatform> getPlatforms() {
		return this.platforms;
	}

	/**
	 * @return Dump all devices as a string.
	 */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		for (final EncogCLDevice device : this.devices) {
			result.append(device.toString());
			result.append("\n");
		}
		return result.toString();
	}

	/**
	 * @return the enforcedCLRatio
	 */
	public double getEnforcedCLRatio() {
		return enforcedCLRatio;
	}

	/**
	 * @return the clThreads
	 */
	public int getCLThreads() {
		return clThreads;
	}

	/**
	 * @return the clWorkloadSize
	 */
	public int getCLWorkloadSize() {
		return clWorkloadSize;
	}
	
	
}
