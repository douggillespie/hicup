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



package eteccontrol;

import java.util.ArrayList;

import Acquisition.AcquisitionControl;
import PamController.PamControlledUnit;
import PamController.PamController;
import arduino.ArduinoControl;
import arduino.ArduinoDevice;

/**
 * @author mo55
 *
 */
public class EtecPreamplifier extends ArduinoDevice {

	public static final String GAINCOMMAND = "ETECGAIN";

	public static final String FILTERCOMMAND = "ETECFILTER";


	/**
	 * The constructor for the preamplifier device.  Both the power command and the power check command are
	 * null, because the preamplifier is always on (it is powered directly, and not through a relay controlled
	 * by the arduino).
	 * 
	 * @param arduino
	 * @param deviceName
	 * @param deviceID
	 */
	public EtecPreamplifier(ArduinoControl arduino, String deviceName, int deviceID) {
		super(arduino, deviceName, deviceID, null, null);
	}
	
	boolean setGainAndFilter(EtecParameters etecParameters) {
		String udpCommand = String.format("%s,%d", GAINCOMMAND, etecParameters.getGainBits());
		boolean success = arduino.sendCommand(udpCommand);
		udpCommand = String.format("%s,%d", FILTERCOMMAND, etecParameters.getFilterBite());
		success &= arduino.sendCommand(udpCommand);
		
		// now set the gain in ALL acquisition modules. 
		ArrayList<PamControlledUnit> daqModules = PamController.getInstance().findControlledUnits(AcquisitionControl.class);
		for (PamControlledUnit pcu:daqModules) {
			AcquisitionControl daq = (AcquisitionControl) pcu;
			daq.getAcquisitionParameters().preamplifier.setGain(etecParameters.getGain());
		}
		
		return success;
	}

	
	@Override
	public boolean isOn() {
		return true;
	}

	@Override
	public void initialize() {
	}

	@Override
	public void shutDown() {
	}

}
