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
package org.encog.platformspecific.j2se;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.Format;
import org.encog.util.simple.EncogUtility;


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
	
	/**
	 * Train, using the specified training method, display progress to a dialog
	 * box.
	 * 
	 * @param train
	 *            The training method to use.
	 * @param network
	 *            The network to train.
	 * @param trainingSet
	 *            The training set to use.
	 */
	public static void trainDialog(final MLTrain train,
			final BasicNetwork network, final MLDataSet trainingSet) {

		final TrainingDialog dialog = new TrainingDialog();
		dialog.setVisible(true);

		final long start = System.currentTimeMillis();
		do {
			train.iteration();
			int iteration = train.getIteration();
			final long current = System.currentTimeMillis();
			final long elapsed = (current - start) / 1000;// seconds
			dialog.setIterations(iteration);
			dialog.setError(train.getError());
			dialog.setTime((int) elapsed);
		} while (!dialog.shouldStop());
		train.finishTraining();
		dialog.dispose();
	}

	/**
	 * Train using SCG and display progress to a dialog box.
	 * 
	 * @param network
	 *            The network to train.
	 * @param trainingSet
	 *            The training set to use.
	 */
	public static void trainDialog(final BasicNetwork network,
			final MLDataSet trainingSet) {
		final Propagation train = new ResilientPropagation(network, trainingSet);
		train.setThreadCount(0);
		TrainingDialog.trainDialog(train, network, trainingSet);
	}

}
