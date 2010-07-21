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

/**
 * Common data held by OpenCL devices and platforms.
 */
public class EncogCLItem {

	/**
	 * Is this device or platform enabled. Disabling a platform will cause its
	 * devices to not be used either, regardless of their enabled/disabled
	 * status.
	 */
	private boolean enabled;

	/**
	 * The name of this device or platform.
	 */
	private String name;

	/**
	 * The vendor of this device or platform.
	 */
	private String vender;

	/**
	 * @return The name of this platform or device.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return The vender for this platform or device.
	 */
	public String getVender() {
		return this.vender;
	}

	/**
	 * @return True if this device or platform is enabled.
	 */
	public boolean isEnabled() {
		return this.enabled;
	}

	/**
	 * Enable or disable this device or platform.
	 * 
	 * @param enabled
	 *            True, if enabled.
	 */
	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Set the name of this platform or device.
	 * 
	 * @param name
	 *            The name of this platform or device.
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Set the vender for this platform or device.
	 * 
	 * @param vender
	 *            The vender.
	 */
	public void setVender(final String vender) {
		this.vender = vender;
	}

}
