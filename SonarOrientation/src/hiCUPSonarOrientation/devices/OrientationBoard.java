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



package hiCUPSonarOrientation.devices;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import arduino.ArduinoControl;
import arduino.ArduinoDevice;

/**
 * @author mo55
 *
 */
public class OrientationBoard extends ArduinoDevice {

	/**
	 * ORIENTPWR - turn power on or off to orientation boards
	 */
	private static final String ORIENTPWR = "ORIENTPWR";

	/**
	 * ORIENTPWR - status of pin controlling orientation board relay power
	 */
	private static final String ORIENTPWRCHK = "ORIENTPWRCHK";
	
	/**
	 * GETORIENT - get orientation reading from orientation board
	 */
	private static final String GETORIENT = "GETORIENT";
	
	/**
	 * A timer controlling when readings are taken from the orientation boards
	 */
	private Timer orientationTimer;
	


	/**
	 * @param arduino
	 * @param powerCommand
	 * @param deviceID
	 */
	public OrientationBoard(ArduinoControl arduino, String deviceName, int deviceID) {
		super(arduino, deviceName, deviceID, ORIENTPWR, ORIENTPWRCHK);
	}

	@Override
	public void initialize() {
		// turn orientation board on, get the reading and
		// start a timer to check orientation board every couple of second
		turnOn();
		orientationTimer = new Timer(2000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				arduino.sendCommand(String.format("%s,%d",GETORIENT,deviceID));
			}
		});
		orientationTimer.start();
	}

	@Override
	public void receiveMessage(String[] message) {
		
		// first call the super to check for a power status message
		super.receiveMessage(message);
		
		// ignore message if there is no device id
		if (message.length==1) return;
		
		// ignore message if it isn't the correct id
		int device = Integer.valueOf(message[1]);
		if (device!=deviceID) return;
		
		// ignore message if it's an error (info has already been sent to listeners)
		if (message[message.length-1]==ArduinoDevice.NAK) return;
		
		// if we get here, we know that message[1] = deviceID but it doesn't have anything to do with the power
		if (message[0].equals(GETORIENT)) {
			String[] orientVals = new String[3];
			orientVals[0] = message[2];
			orientVals[1] = message[3];
			orientVals[2] = message[4];
//			arduino.displayArduinoMessage(String.format("Board %s (id=%d) reports %4.1f yaw, %4.1f pitch, %4.1f roll", deviceName, deviceID, yaw, pitch, roll));

			// change the text in the labels
			arduino.sendMessageToListeners(deviceName, deviceID, orientVals);
		}
	}

	@Override
	public void shutDown() {
		if (orientationTimer!=null) {
			orientationTimer.stop();
			orientationTimer = null;
		}
		turnOff();
	}

}
