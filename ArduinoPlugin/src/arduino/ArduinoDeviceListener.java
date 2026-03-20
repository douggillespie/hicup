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
 * A Listener class that receives messages from the Arduino.
 * 
 * @author mo55
 *
 */
public interface ArduinoDeviceListener {

	/**
	 * Process a message from the Arduino.  Usually these are device-specific messages, such as
	 * an OrientationBoard object sending yaw, pitch and roll data.  Listeners should be able
	 * to recognize any commands for the devices they are interested in.
	 * 
	 * Error messages (those ending in 'NAK' instead of 'ACK') will also be sent to this method.  If you need to process errors, you
	 * can recognize the initial command causing the error by looking at message[0]
	 *  
	 * @param deviceName the name of the device sending the message
	 * @param deviceID the device ID
	 * @param message the message being sent
	 */
	void messageFromDevice(String deviceName, int deviceID, String[] message);

	
	/**
	 * Power-related messages from the Arduino device.  Note that the boolean only indicates whether the
	 * Arduino pin that controls power to the device (typically through a relay) is on or off.
	 * 
	 * @param deviceName the name of the device sending the message
	 * @param deviceID the device ID
	 * @param isOn whether the device is on (true) or off (false).
	 */
	void devicePowerStat(String deviceName, int deviceID, boolean isOn);

}
