/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
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
 */

package org.encog.util.simple;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.encog.util.Format;

public class TrainingDialog extends JDialog implements ActionListener {

	private JLabel labelError;
	private JLabel labelIterations;
	private JLabel labelTime;
	private JButton buttonStop;
	private boolean shouldStop = false;
	
	public TrainingDialog()
	{
		this.setSize(320, 100);
		this.setTitle("Training");
		Container content = this.getContentPane();
		content.setLayout(new BorderLayout());
		JPanel statsPanel = new JPanel();
		statsPanel.setLayout(new GridLayout(3,2));
		
		statsPanel.add(new JLabel("Current Error:"));
		statsPanel.add(this.labelError = new JLabel("Starting..."));
		statsPanel.add(new JLabel("Iterations:"));
		statsPanel.add(this.labelIterations = new JLabel(""));
		statsPanel.add(new JLabel("Training Time:"));
		statsPanel.add(this.labelTime = new JLabel(""));
		content.add(this.buttonStop = new JButton("Stop"),BorderLayout.SOUTH);
		content.add(statsPanel,BorderLayout.CENTER);
		this.buttonStop.addActionListener(this);
	}
	
	public void setIterations(int iteration)
	{
		this.labelIterations.setText(Format.formatInteger(iteration));
	}
	
	public void setError(double e)
	{
		this.labelError.setText(Format.formatPercent(e));
	}
	
	public void setTime(int seconds)
	{
		this.labelTime.setText(Format.formatTimeSpan(seconds));
	}

	public void actionPerformed(ActionEvent e) {
		if( e.getSource()==this.buttonStop) {
			this.buttonStop.setEnabled(false);
			this.buttonStop.setText("Stopping...");
			shouldStop = true;
		}
	}
	
	public boolean shouldStop()
	{
		return this.shouldStop;
	}
	

}
