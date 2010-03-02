/*
 * Encog(tm) Core v2.4
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

package org.encog.util.simple;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.encog.util.Format;

/**
 * Display a training dialog.
 */
public class TrainingDialog extends JDialog implements ActionListener {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -6847676575773420316L;

	/**
	 * Holds the current error.
	 */
	private JLabel labelError;

	/**
	 * Holds the iteration count.
	 */
	private JLabel labelIterations;
	
	/**
	 * Holds the current total training time.
	 */
	private JLabel labelTime;
	
	/**
	 * The stop button.
	 */
	private JButton buttonStop;
	
	/**
	 * Set to true if the network should stop after the current iteration.
	 */
	private boolean shouldStop = false;

	/**
	 * Construct the training dialog.
	 */
	public TrainingDialog() {
		this.setSize(320, 100);
		setTitle("Training");
		final Container content = getContentPane();
		content.setLayout(new BorderLayout());
		final JPanel statsPanel = new JPanel();
		statsPanel.setLayout(new GridLayout(3, 2));

		statsPanel.add(new JLabel("Current Error:"));
		statsPanel.add(this.labelError = new JLabel("Starting..."));
		statsPanel.add(new JLabel("Iterations:"));
		statsPanel.add(this.labelIterations = new JLabel(""));
		statsPanel.add(new JLabel("Training Time:"));
		statsPanel.add(this.labelTime = new JLabel(""));
		content.add(this.buttonStop = new JButton("Stop"), BorderLayout.SOUTH);
		content.add(statsPanel, BorderLayout.CENTER);
		this.buttonStop.addActionListener(this);
	}

	/**
	 * Called when the user clicks the stop button.
	 * @param e The action event.
	 */
	public void actionPerformed(final ActionEvent e) {
		if (e.getSource() == this.buttonStop) {
			this.buttonStop.setEnabled(false);
			this.buttonStop.setText("Stopping...");
			this.shouldStop = true;
		}
	}

	/**
	 * Set the current error.
	 * @param e The current error.
	 */
	public void setError(final double e) {
		this.labelError.setText(Format.formatPercent(e));
	}

	/**
	 * Set the number of iterations.
	 * @param iteration The current iteration.
	 */
	public void setIterations(final int iteration) {
		this.labelIterations.setText(Format.formatInteger(iteration));
	}

	/**
	 * Set the time.
	 * @param seconds The time in seconds.
	 */
	public void setTime(final int seconds) {
		this.labelTime.setText(Format.formatTimeSpan(seconds));
	}

	/**
	 * @return True if training should stop after current iteration.
	 */
	public boolean shouldStop() {
		return this.shouldStop;
	}

}
