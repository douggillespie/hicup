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

import java.io.Serializable;

/**
 * @author mo55
 *
 */
public class ArduinoParams implements Serializable, Cloneable {
	
	/**
	 * 
	 */
	public static final long serialVersionUID = 1L;

	private String arduinoAddress = "192.168.0.207";

	private int arduinoPort = 8888; 
	
	private int localPort = 9090;
	
	private boolean displayMessages = false;

	public String getArduinoAddress() {
		return arduinoAddress;
	}

	public void setArduinoAddress(String arduinoAddress) {
		this.arduinoAddress = arduinoAddress;
	}

	public int getArduinoPort() {
		return arduinoPort;
	}

	public void setArduinoPort(int arduinoPort) {
		this.arduinoPort = arduinoPort;
	}

	public int getLocalPort() {
		return localPort;
	}

	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}

	/**
	 * @return the displayMessages
	 */
	public boolean isDisplayMessages() {
		return displayMessages;
	}

	/**
	 * @param displayMessages the displayMessages to set
	 */
	public void setDisplayMessages(boolean displayMessages) {
		this.displayMessages = displayMessages;
	}

	@Override
	protected ArduinoParams clone() {
		try {
			return (ArduinoParams) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
