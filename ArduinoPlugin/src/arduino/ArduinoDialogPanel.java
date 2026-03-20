/*
 *  PAMGUARD - Passive Acoustic Monitoring GUARDianship.
 * To assist in the Detection Classification and Localisation
 * of marine mammals (cetaceans).
 *
 * Copyright (C) 2006
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */



package arduino;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import PamView.dialog.PamDialog;

/**
 * @author mo55
 *
 */
public class ArduinoDialogPanel {
	
	private ArduinoProcess arduinoProcess;
	
	private ArduinoParams arduinoParams;

	private JPanel mainPanel;
	
	private JTextField txtIP;

	private JTextField txtPort;

	private JTextField txtLocalPort;
	
	private JLabel lblDispMess;
	
	private JCheckBox chkDispMess;
	

	/**
	 * @param arduinoProcess 
	 * @wbp.parser.entryPoint
	 * 
	 */
	public ArduinoDialogPanel(ArduinoProcess arduinoProcess) {
		this.arduinoProcess = arduinoProcess;
		
		mainPanel = new JPanel();
		mainPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Arduino Communication", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GridBagLayout gbl_pnlCommDetails = new GridBagLayout();
		gbl_pnlCommDetails.columnWidths = new int[]{0, 0, 0};
		gbl_pnlCommDetails.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_pnlCommDetails.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gbl_pnlCommDetails.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		mainPanel.setLayout(gbl_pnlCommDetails);
		
		JLabel lblArdIP = new JLabel("Arduino IP Address");
		GridBagConstraints gbc_lblArdIP = new GridBagConstraints();
		gbc_lblArdIP.anchor = GridBagConstraints.WEST;
		gbc_lblArdIP.insets = new Insets(5, 5, 5, 5);
		gbc_lblArdIP.gridx = 0;
		gbc_lblArdIP.gridy = 0;
		mainPanel.add(lblArdIP, gbc_lblArdIP);
		
		txtIP = new JTextField();
		txtIP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				arduinoProcess.initialize(getParams());
			}
		});
		GridBagConstraints gbc_txtIP = new GridBagConstraints();
		gbc_txtIP.anchor = GridBagConstraints.NORTH;
		gbc_txtIP.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtIP.insets = new Insets(5, 2, 5, 0);
		gbc_txtIP.gridx = 1;
		gbc_txtIP.gridy = 0;
		mainPanel.add(txtIP, gbc_txtIP);
		txtIP.setColumns(10);
		
		JLabel lblArdPort = new JLabel("Arduino Port");
		GridBagConstraints gbc_lblArdPort = new GridBagConstraints();
		gbc_lblArdPort.anchor = GridBagConstraints.WEST;
		gbc_lblArdPort.insets = new Insets(2, 5, 5, 5);
		gbc_lblArdPort.gridx = 0;
		gbc_lblArdPort.gridy = 1;
		mainPanel.add(lblArdPort, gbc_lblArdPort);
		
		txtPort = new JTextField();
		txtPort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				arduinoProcess.initialize(getParams());
			}
		});
		GridBagConstraints gbc_txtPort = new GridBagConstraints();
		gbc_txtPort.insets = new Insets(2, 2, 5, 0);
		gbc_txtPort.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtPort.gridx = 1;
		gbc_txtPort.gridy = 1;
		mainPanel.add(txtPort, gbc_txtPort);
		txtPort.setColumns(10);
		
		JLabel lblLocalPort = new JLabel("Local Port");
		GridBagConstraints gbc_lblLocalPort = new GridBagConstraints();
		gbc_lblLocalPort.anchor = GridBagConstraints.WEST;
		gbc_lblLocalPort.insets = new Insets(2, 5, 5, 5);
		gbc_lblLocalPort.gridx = 0;
		gbc_lblLocalPort.gridy = 2;
		mainPanel.add(lblLocalPort, gbc_lblLocalPort);
		
		txtLocalPort = new JTextField();
		txtLocalPort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				arduinoProcess.initialize(getParams());
			}
		});
		GridBagConstraints gbc_txtLocalPort = new GridBagConstraints();
		gbc_txtLocalPort.insets = new Insets(2, 2, 5, 0);
		gbc_txtLocalPort.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtLocalPort.gridx = 1;
		gbc_txtLocalPort.gridy = 2;
		mainPanel.add(txtLocalPort, gbc_txtLocalPort);
		txtLocalPort.setColumns(10);
		
		lblDispMess = new JLabel("<html>Display Messages<br>in Console Window</html>");
		String tip = "<html>Display messages from the Arduino and registered Arduino Devices <br>" +
					"in the console window.  This is helpful for debugging purposes, but can<br>" +
					"cause a lot of messages to be displayed.</html>";
		lblDispMess.setToolTipText(tip);
		GridBagConstraints gbc_lblDispMess = new GridBagConstraints();
		gbc_lblDispMess.anchor = GridBagConstraints.WEST;
		gbc_lblDispMess.insets = new Insets(2, 5, 5, 5);
		gbc_lblDispMess.gridx = 0;
		gbc_lblDispMess.gridy = 3;
		mainPanel.add(lblDispMess, gbc_lblDispMess);
		
		chkDispMess = new JCheckBox("");
		chkDispMess.setToolTipText(tip);
		GridBagConstraints gbc_chkDispMess = new GridBagConstraints();
		gbc_chkDispMess.insets = new Insets(2, 2, 5, 0);
		gbc_chkDispMess.gridx = 1;
		gbc_chkDispMess.gridy = 3;
		mainPanel.add(chkDispMess, gbc_chkDispMess);
	}


	/**
	 * @return
	 */
	public JComponent getDialogComponent() {
		return mainPanel;
	}

	/**
	 * @param arduinoParams
	 */
	void setParams(ArduinoParams arduinoParams) {
		this.arduinoParams = arduinoParams.clone();
		this.txtIP.setText(arduinoParams.getArduinoAddress());
		this.txtPort.setText(String.valueOf(arduinoParams.getArduinoPort()));
		this.txtLocalPort.setText(String.valueOf(arduinoParams.getLocalPort()));
		this.chkDispMess.setSelected(arduinoParams.isDisplayMessages());
		arduinoProcess.initialize(arduinoParams);
	}

	public ArduinoParams getParams() {
		arduinoParams.setArduinoAddress(txtIP.getText());
		arduinoParams.setDisplayMessages(chkDispMess.isSelected());
		try {
			arduinoParams.setArduinoPort(Integer.valueOf(txtPort.getText()));
		}
		catch (NumberFormatException e) {
			PamDialog.showWarning(null, "Invalid Arduino port number", "Enter a valid integer port number (Default 8888)");
			return null;
		}
		try {
			arduinoParams.setLocalPort(Integer.valueOf(txtLocalPort.getText()));
		}
		catch (NumberFormatException e) {
			PamDialog.showWarning(null, "Invalid local port number", "Enter a valid integer port number (Default 9090)");
			return null;
		}
		return arduinoParams;
	}


}
