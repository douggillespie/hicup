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

import java.sql.Types;

import PamguardMVC.PamDataBlock;
import PamguardMVC.PamDataUnit;
import generalDatabase.PamTableDefinition;
import generalDatabase.PamTableItem;
import generalDatabase.SQLLogging;
import generalDatabase.SQLTypes;

/**
 * @author mo55
 *
 */
public class ArduinoLogging extends SQLLogging {

	PamTableItem arduinoAdress;
	PamTableItem arduinoPort;
	PamTableItem localPort;
	private PamTableDefinition tableDefinition;

	/**
	 * @param pamDataBlock
	 */
	public ArduinoLogging(PamDataBlock pamDataBlock) {
		super(pamDataBlock);
		setCanView(false);		// cannot load this data in Viewer mode
		
		tableDefinition = new PamTableDefinition("Arduino Communication", UPDATE_POLICY_WRITENEW);
		tableDefinition.addTableItem(arduinoAdress = new PamTableItem("ArduinoAdress", Types.CHAR, 16));
		tableDefinition.addTableItem(arduinoPort = new PamTableItem("ArduinoPort", Types.INTEGER));
		tableDefinition.addTableItem(localPort =  new PamTableItem("LocalPort", Types.INTEGER));
		setTableDefinition(tableDefinition);
	}

	@Override
	public void setTableData(SQLTypes sqlTypes, PamDataUnit pamDataUnit) {
		ArduinoDataUnit dataUnit = (ArduinoDataUnit) pamDataUnit;
		
		arduinoAdress.setValue(dataUnit.getArduinoParams().getArduinoAddress());
		arduinoPort.setValue(dataUnit.getArduinoParams().getArduinoPort());
		localPort.setValue(dataUnit.getArduinoParams().getLocalPort());
	}

	
	@Override
	protected PamDataUnit createDataUnit(SQLTypes sqlTypes, long timeMilliseconds, int databaseIndex) {
		
		ArduinoDataUnit dataUnit = new ArduinoDataUnit(timeMilliseconds);
		dataUnit.setDatabaseIndex(databaseIndex);
		
		// put the values from the database table into the data unit
		dataUnit.getArduinoParams().setArduinoAddress(arduinoAdress.getStringValue());
		dataUnit.getArduinoParams().setArduinoPort(arduinoPort.getIntegerValue());
		dataUnit.getArduinoParams().setLocalPort(localPort.getIntegerValue());
		
		return dataUnit;
	}
}
