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

import arduino.ArduinoControl;
import arduino.ArduinoDevice;

/**
 * @author mo55
 *
 */
public class UVLight extends ArduinoDevice {
	
	private static final String UVPWR = "UVPWR";

	private static final String UVPWRCHK = "UVPWRCHK";
	
	private static final String UVDUTY = "UVDUTY";
	
	/**
	 * duty cycle in minutes
	 */
	private int dutyCycle;

	/**
	 * @param mediator
	 * @param arduino
	 * @param deviceName
	 * @param deviceID
	 * @param powerCommand
	 * @param powerChkCommand
	 */
	public UVLight(ArduinoControl arduino, String deviceName, int deviceID) {
		super(arduino, deviceName, deviceID, UVPWR, UVPWRCHK);
	}

	@Override
	public void initialize() {
		checkPower();
	}

	public int getDutyCycle() {
		return dutyCycle;
	}

	public boolean setDutyCycle(int dutyCycle) {
		this.dutyCycle = dutyCycle;
		String udpCommand = String.format("%s,%d,%d", UVDUTY, deviceID, dutyCycle);
		boolean success = arduino.sendCommand(udpCommand);
		return (success);
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
		
		// the UV power check also returns the current duty cycle
		if (message[0].equals(UVPWRCHK)) {
			dutyCycle = Integer.valueOf(message[3]);
			String[] dutyCycleString = new String[1];
			dutyCycleString[0] = message[3];
			arduino.sendMessageToListeners(deviceName, deviceID, dutyCycleString);
		}
		
		else if (message[0].equals(UVDUTY)) {
			// don't need to do anything here
		}
	}
	
	@Override
	public void shutDown() {
	}

}
