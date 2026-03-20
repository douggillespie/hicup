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
public class Sonar extends ArduinoDevice {

	/**
	 * SONARPWR - turn sonar unit on or off, or check status of pin
	 */
	private static final String SONARPWR = "SONARPWR";
	
	/**
	 * SONARPWRCHK - status of pin controlling sonar relay power
	 */
	private static final String SONARPWRCHK = "SONARPWRCHK";
	

	/**
	 * @param mediator
	 * @param arduino
	 * @param deviceName
	 * @param deviceID
	 * @param powerCommand
	 * @param powerChkCommand
	 */
	public Sonar(ArduinoControl arduino, String deviceName, int deviceID) {
		super(arduino, deviceName, deviceID, SONARPWR, SONARPWRCHK);
	}

	@Override
	public void initialize() {
		checkPower();
	}

	@Override
	public void shutDown() {
	}

}
