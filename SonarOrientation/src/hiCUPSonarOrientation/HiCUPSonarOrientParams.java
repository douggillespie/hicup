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



package hiCUPSonarOrientation;

import java.io.Serializable;


/**
 * @author mo55
 *
 */
public class HiCUPSonarOrientParams implements Serializable, Cloneable {

	public static final long serialVersionUID = 1L;

//	private String arduinoAddress = "192.168.0.207";
//
//	private int arduinoPort = 8888; 
//	
//	private int localPort = 9090;
	
	private int tilt = 0;
	
	private int roll = 0;
	
//	private boolean sonar0On = false;
//	
//	private boolean sonar1On = false;
	
	private int uvSonarDC = 20;
	
	private int uvPAMDC = 20;
	
	
//	public String getArduinoAddress() {
//		return arduinoAddress;
//	}
//
//
//	public void setArduinoAddress(String arduinoAddress) {
//		this.arduinoAddress = arduinoAddress;
//	}
//
//
//	public int getArduinoPort() {
//		return arduinoPort;
//	}
//
//
//	public void setArduinoPort(int arduinoPort) {
//		this.arduinoPort = arduinoPort;
//	}


	public int getTilt() {
		return tilt;
	}


	public void setTilt(int tilt) {
		this.tilt = tilt;
	}


	public int getRoll() {
		return roll;
	}


	public void setRoll(int roll) {
		this.roll = roll;
	}
	
//	public int getCenterVal() {
//		return (minActuatorVal+maxActuatorVal)/2;
//	}
//	


//	public int getLocalPort() {
//		return localPort;
//	}
//
//
//	public void setLocalPort(int localPort) {
//		this.localPort = localPort;
//	}


	public int getUvSonarDC() {
		return uvSonarDC;
	}


	public void setUvSonarDC(int uvSonarDC) {
		this.uvSonarDC = uvSonarDC;
	}


	public int getUvPAMDC() {
		return uvPAMDC;
	}


	public void setUvPAMDC(int uvPAMDC) {
		this.uvPAMDC = uvPAMDC;
	}


//	public boolean isSonar0On() {
//		return sonar0On;
//	}
//
//
//	public void setSonar0On(boolean sonar0On) {
//		this.sonar0On = sonar0On;
//	}
//
//
//	public boolean isSonar1On() {
//		return sonar1On;
//	}
//
//
//	public void setSonar1On(boolean sonar1On) {
//		this.sonar1On = sonar1On;
//	}


//	public static int getSonarorient() {
//		return sonarOrient;
//	}


	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public HiCUPSonarOrientParams clone() {
		try {
			return (HiCUPSonarOrientParams) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	
}
