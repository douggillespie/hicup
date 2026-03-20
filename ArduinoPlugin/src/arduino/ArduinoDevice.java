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

/**
 * @author mo55
 *
 */
public abstract class ArduinoDevice {
	
	/**
	 * acknowledgement of Arduino command
	 */
	public static final String ACK = "ACK";
	
	/**
	 * error trying to execute Arduino command
	 */
	public static final String NAK = "NAK";
	
	/**
	 * device is powered on
	 */
	public static final int POWERON = 1;
	
	/**
	 * device is powered off
	 */
	public static final int POWEROFF = 0;

	/**
	 * The arduino object handling communication
	 */
	protected ArduinoControl arduino;
	
	/**
	 * A name to recognize the device by
	 */
	protected String deviceName;

	/**
	 * device ID number, to distinguish it if there are multiple devices
	 */
	protected int deviceID;
	
	/**
	 * The String command that the Arduino would recognize as turning the device on/off
	 * e.g. ACTPWR, ORIENTPWR, etc 
	 * 	 
	 */
	String powerCommand;
	
	/**
	 * The String response that the Arduino would recognize as checking the status of the device power
	 * e.g. ACTPWRCHK, ORIENTPWRCHK, etc 
	 * 	 
	 */
	String powerChkCommand;
	
	/**
	 * boolean indicating whether the device is currently powered on (true) or off (false)
	 */
	public boolean isOn;
	
	/**
	 * @param powerCommand
	 */
	public ArduinoDevice(ArduinoControl arduino, String deviceName, int deviceID, String powerCommand, String powerChkCommand) {
		this.arduino = arduino;
		this.deviceName = deviceName;
		this.deviceID = deviceID;
		this.powerCommand = powerCommand;
		this.powerChkCommand = powerChkCommand;
		arduino.registerDevice(this);
	}
	
	/**
	 * Turn on the power to the device, and then do a check to make sure the Arduino pin has actually changed
	 * 
	 * @return success true if the messages were sent successfully, false if there was a problem 
	 */
	public boolean turnOn() {
		if (powerCommand==null) return false;
		String udpCommand = String.format("%s,%d,ON", powerCommand, deviceID);
		boolean success = arduino.sendCommand(udpCommand);
		if (!success) return false;
		return (checkPower());
	}
	
	/**
	 * Turn off the power to the device, and then do a check to make sure the Arduino pin has actually changed
	 * 
	 * @return success true if the messages were sent successfully, false if there was a problem 
	 */
	public boolean turnOff() {
		if (powerCommand==null) return false;
		String udpCommand = String.format("%s,%d,OFF", powerCommand, deviceID);
		boolean success = arduino.sendCommand(udpCommand);
		if (!success) return false;
		return (checkPower());
	}
		
	/**
	 * Check what state the Arduino pin that controls power to the device is in
	 * 
	 * @return success true if the messages were sent successfully, false if there was a problem 
	 */
	public boolean checkPower() {
		if (powerCommand==null) return false;
		String udpCommand = String.format("%s,%d,CHECK", powerCommand, deviceID);
		return (arduino.sendCommand(udpCommand));
	}
		
	public boolean isOn() {
		return isOn;
	}
	
	public void setPowerFlag(boolean isOn) {
		this.isOn = isOn;
	}
	
	/**
	 * Process a message sent from the Arduino.  This could be an echo of the initial
	 * command to indicate command acceptance (with ACK at the end) or rejection (with NAK
	 * on the end instead).  It might also be a device-specific message, such as the the
	 * Arduino sending orientation board yaw, pitch and roll values.
	 * 
	 * This method checks for power on/off/check messages, which are common to all devices. 
	 * For device-specific messages, each device should override this method and perform
	 * the necessary checks.  For an OrientationBoard object, that means listening for
	 * GETORIENT commands, processing the yaw, pitch and roll values, and then passing
	 * those on to the registered ArduinoDeviceListener objects by calling the
	 * ArduinoControl.sendMessageToListeners method.
	 * 
	 * The first index in the message array is the command,
	 * and the second is the deviceId.  This can be followed by any number of fields depending
	 * on the command.
	 * 
	 * @param message
	 */
	public void receiveMessage(String[] message) {
		
		// ignore message if there is no device id
		if (message.length==1) return;
		
		// if we received an error message, print it out and alert the device listeners
		if (message[message.length-1].equals(NAK)) {
			arduino.displayArduinoMessage("Error responding to command");
			arduino.displayArduinoMessage(String.join(",", message));
			arduino.sendMessageToListeners(deviceName, deviceID, message);
			return;
		}
		
		// ignore message if it isn't the correct id
		int device = Integer.valueOf(message[1]);
		if (device!=deviceID) return;
		
		// if this is to turn the device on or off, process
		if (message[0].equals(powerCommand)) {
			String power = message[2];
			arduino.displayArduinoMessage(String.format("%s (id=%d) turned %s", deviceName, deviceID, power));
		}

		// if this is a power check, process
		else if (message[0].equals(powerChkCommand)) {

			int pwrStatus = Integer.valueOf(message[2]);
			String power = "Off";
			isOn = false;
			if (pwrStatus==ArduinoDevice.POWERON) {
				power = "On";
				isOn = true;
			}
			arduino.displayArduinoMessage(String.format("Power Check %s (id=%d) power is %s", deviceName, deviceID, power));

			// tell all registered listeners that this device is on or off
			arduino.devicePowerStat(deviceName, deviceID, isOn);
		}
	}
	
	

	
	/**
	 * Perform any necessary operations to start device
	 */
	abstract public void initialize();
	
	/**
	 * Perform any necessary operations prior to shutting down communications with the Arduino
	 */
	abstract public void shutDown();
	
	

}
