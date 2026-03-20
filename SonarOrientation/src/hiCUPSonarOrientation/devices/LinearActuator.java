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
public class LinearActuator extends ArduinoDevice {

	/**
	 * ACTPWR - turn power on or off to linear actuators
	 */
	private static final String ACTPWR = "ACTPWR";
	
	/**
	 * ACTPWRCHK - status of pin controlling actuator relay power
	 */
	private static final String ACTPWRCHK = "ACTPWRCHK";
	
	/**
	 * ACTPOS - change actuator position
	 */
	private static final String ACTPOS = "ACTPOS";

	/**
	 * ACTRST - reset C2-20 control board fault status	
	 */
	private static final String ACTRST = "ACTRST";
	
	public static final int MINACTUATORVAL = 0;

	public static final int MAXACTUATORVAL = 254;
	
	public static final int CTRACTUATORVAL = (MINACTUATORVAL+MAXACTUATORVAL)/2;
	
	/**
	 * current position
	 */
	private int curPos;
	

	/**
	 * 
	 * @param mediator
	 * @param arduino
	 * @param deviceName
	 * @param deviceID
	 */
	public LinearActuator(ArduinoControl arduino, String deviceName, int deviceID) {
		super(arduino, deviceName, deviceID, ACTPWR, ACTPWRCHK);
	}

	@Override
	public void initialize() {
		// start with the linear actuator off
		turnOff();
	}
	
	/**
	 * return the current position
	 * 
	 * @return current position
	 */
	public int getCurrentPos() {
		return curPos;
	}
	
	/**
	 * Set the current position field
	 * 
	 * @param curPos
	 */
	public void setCurrentPos(int curPos) {
		this.curPos = curPos;
	}
	
	
	/**
	 * Send UDP message to move the actuator to a new position
	 * 
	 * @param posVal the value to change to
	 * @return success (true) or failure (false)
	 */
	public boolean moveActuator(int posVal) {
		
		// Make sure it is within the range allowed
		int newPos = Math.min(posVal, MAXACTUATORVAL);
		newPos = Math.max(newPos, MINACTUATORVAL);

		// send the message
		String udpCommand = String.format("%s,%d,%d", ACTPOS, deviceID, newPos);
		boolean success = arduino.sendCommand(udpCommand);
		if (success) {
			curPos = newPos;
		}
		return (success);
	}
	
	
	/**
	 * Move the actuator up one position
	 * 
	 * @return success (true) or failure (false)
	 */
	public boolean upOne() {
		return(moveActuator(curPos+1));
	}


	/**
	 * Move the actuator up one position
	 * 
	 * @return success (true) or failure (false)
	 */
	public boolean downOne() {
		return(moveActuator(curPos-1));
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
		
		// if we get here, we know that message[1] = deviceID but it doesn't have anything to do with the power
		if (message[0].equals(ACTPOS)) {
			int loc = Integer.valueOf(message[2]);
			arduino.displayArduinoMessage(String.format("Actuator %d position changed to %d", deviceID,loc));
		}

		// ACTRST
		else if (message[0].equals(ACTRST)) {
			arduino.displayArduinoMessage(String.format("Actuator %d reset acknowledged", deviceID));
		}
	}

	@Override
	public void shutDown() {
		turnOff();
	}

}
