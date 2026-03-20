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

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JMenuItem;

import PamController.PamControlledUnit;
import PamController.PamControlledUnitSettings;
import PamController.PamSettingManager;
import PamController.PamSettings;

/**
 * @author mo55
 *
 */
public class ArduinoControl extends PamControlledUnit implements PamSettings {
	
	public static final String arduinoType = "Arduino Interface";
	
	private ArduinoParams arduinoParams = new ArduinoParams();
	
	private ArduinoProcess arduinoProcess;

	/**
	 * A list of all ArduinoDeviceListener objects that are ready for a device message
	 */
	private ArrayList<ArduinoDeviceListener> deviceListenerList = new ArrayList<ArduinoDeviceListener>();
	
	/**
	 * @param unitType
	 * @param unitName
	 */
	public ArduinoControl(String unitName) {
		super(arduinoType, unitName);
		PamSettingManager.getInstance().registerSettings(this);
		addPamProcess(arduinoProcess = new ArduinoProcess(this));
	}
	
	@Override
	public JMenuItem createDetectionMenu(Frame parentFrame) {
		JMenuItem menuItem = new JMenuItem("Arduino Communication");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showArduinoDialog(parentFrame);
			}
		});
		return menuItem;
	}


	private void showArduinoDialog(Frame parentFrame) {
		ArduinoParams newParams = ArduinoDialog.showDialog(parentFrame, arduinoParams, arduinoProcess);
		if (newParams != null) {
			arduinoParams = newParams;
		}
	}

	
	/**
	 * Initialize the Arduino communications using the current parameters
	 */
	public void initWithCurrentParams() {
		arduinoProcess.initialize(arduinoParams);
	}


	/**
	 * Tell any registered Arduino devices to run their startup routines if they have them.  This should be called before an object tries to
	 * send/receive Arduino messages.  
	 */
	public void initializeDevices() {
		arduinoProcess.initializeDevices();
	}
	
	
	/**
	 * Register a device that the Arduino will send to and receive from
	 * 
	 * @param arduinoDevice
	 */
	public void registerDevice(ArduinoDevice arduinoDevice) {
		arduinoProcess.registerDevice(arduinoDevice);
	}

	
	/**
	 * Send a command to the Arduino.  Refer to the Arduino Communications Protocol document for command format.
	 * 
	 * @param udpCommand the command to send
	 * @return boolean true if successful, or false if there was an error.  Note this only indicates that there was no error
	 * in the UDP transmission of the message.  To know whether the Arduino has understood and processed the message properly, you
	 * need to register as an ArduinoDeviceListener to receive messages back from the Arduino
	 */
	public boolean sendCommand(String udpCommand) {
		return (arduinoProcess.sendCommand(udpCommand));
	}

	
	
	/**
	 * Display a message sent from the Arduino
	 * 
	 * @param mess the String to display
	 */
	public void displayArduinoMessage(String mess) {
		if (arduinoParams.isDisplayMessages()) {
			System.out.println(mess);
		}
	}
	
	
	/**
	 * tell all registered devices to run their shutdown routines (if they have one)
	 */
	public void shutDownDevices() {
		arduinoProcess.shutDownDevices();
	}
	
	
	/**
	 * Register an object as a 'listener'.  Listeners will receive messages sent from all the
	 * registered Arduino devices.
	 * 
	 * @param listener the object to be registered as a listener
	 */
	public void registerDeviceListener(ArduinoDeviceListener listener) {
		if (deviceListenerList.contains(listener)) return;
		deviceListenerList.add(listener);
	}
	
	
	/**
	 * 
	 * @param deviceName
	 * @param deviceID
	 * @param message
	 */
	public void sendMessageToListeners(String deviceName, int deviceID, String[] message) {
		for (int i=0; i<deviceListenerList.size(); i++) {
			deviceListenerList.get(i).messageFromDevice(deviceName, deviceID, message);
		}
	}

	
	/**
	 * @param deviceName
	 * @param deviceID
	 * @param isOn
	 */
	public void devicePowerStat(String deviceName, int deviceID, boolean isOn) {
		for (int i=0; i<deviceListenerList.size(); i++) {
			deviceListenerList.get(i).devicePowerStat(deviceName, deviceID, isOn);
		}
	}

	
	@Override
	public void pamClose() {
		super.pamClose();
		closeComms();
	}

	/**
	 * Close the Arduino communications channel
	 */
	public void closeComms() {
		arduinoProcess.closeComms();
	}


	@Override
	public Serializable getSettingsReference() {
		return arduinoParams;
	}

	@Override
	public long getSettingsVersion() {
		return ArduinoParams.serialVersionUID;
	}

	@Override
	public boolean restoreSettings(PamControlledUnitSettings pamControlledUnitSettings) {
		arduinoParams = ((ArduinoParams) pamControlledUnitSettings.getSettings()).clone();
		return true;
	}
	

}
