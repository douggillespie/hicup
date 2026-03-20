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
public class HiCUPSonarOrientLogging extends SQLLogging {

	PamTableItem tilt;
	PamTableItem roll;
	PamTableItem sonarYaw;
	PamTableItem sonarPitch;
	PamTableItem sonarRoll;
	PamTableItem frameYaw;
	PamTableItem framePitch;
	PamTableItem frameRoll;
	PamTableItem uvSonar;
	PamTableItem uvPAM;
	
	private PamTableDefinition tableDefinition;

	
	/**
	 * @param pamDataBlock
	 */
	protected HiCUPSonarOrientLogging(PamDataBlock pamDataBlock) {
		super(pamDataBlock);
		setCanView(false);		// cannot load this data in Viewer mode
		
		tableDefinition = new PamTableDefinition("HiCUP Sonar Orientation", UPDATE_POLICY_WRITENEW);
		tableDefinition.addTableItem(tilt = new PamTableItem("TiltActuatorVal", Types.INTEGER));
		tableDefinition.addTableItem(roll = new PamTableItem("RollActuatorVal", Types.INTEGER));
		tableDefinition.addTableItem(sonarYaw = new PamTableItem("SonarYaw", Types.FLOAT));
		tableDefinition.addTableItem(sonarPitch = new PamTableItem("SonarPitch", Types.FLOAT));
		tableDefinition.addTableItem(sonarRoll = new PamTableItem("SonarRoll", Types.FLOAT));
		tableDefinition.addTableItem(frameYaw = new PamTableItem("FrameYaw", Types.FLOAT));
		tableDefinition.addTableItem(framePitch = new PamTableItem("FramePitch", Types.FLOAT));
		tableDefinition.addTableItem(frameRoll = new PamTableItem("FrameRoll", Types.FLOAT));
		tableDefinition.addTableItem(uvSonar = new PamTableItem("UVSonarDutyCycle", Types.INTEGER));
		tableDefinition.addTableItem(uvPAM = new PamTableItem("UVPAMDutyCycle", Types.INTEGER));
		
        setTableDefinition(tableDefinition);
	}

	/**
	 * Transfer data from the PamDataUnit into the database table objects
	 */
	@Override
	public void setTableData(SQLTypes sqlTypes, PamDataUnit pamDataUnit) {
		HiCUPSonarOrientDataUnit dataUnit = (HiCUPSonarOrientDataUnit) pamDataUnit;
		
		tilt.setValue(dataUnit.getSonarParams().getTilt());
		roll.setValue(dataUnit.getSonarParams().getRoll());
		sonarYaw.setValue(dataUnit.getSonarYaw());
		sonarPitch.setValue(dataUnit.getSonarPitch());
		sonarRoll.setValue(dataUnit.getSonarRoll());
		frameYaw.setValue(dataUnit.getFrameYaw());
		framePitch.setValue(dataUnit.getFramePitch());
		frameRoll.setValue(dataUnit.getFrameRoll());
		uvSonar.setValue(dataUnit.getUvSonar());
		uvPAM.setValue(dataUnit.getUvPAM());
	}

	@Override
	protected PamDataUnit createDataUnit(SQLTypes sqlTypes, long timeMilliseconds, int databaseIndex) {
		
		HiCUPSonarOrientDataUnit dataUnit = new HiCUPSonarOrientDataUnit(timeMilliseconds);
		dataUnit.setDatabaseIndex(databaseIndex);
		
		// put the values from the database table into the data unit
		dataUnit.getSonarParams().setTilt(tilt.getIntegerValue());
		dataUnit.getSonarParams().setRoll(roll.getIntegerValue());
		dataUnit.setSonarYaw(sonarYaw.getFloatValue());
		dataUnit.setSonarPitch(sonarPitch.getFloatValue());
		dataUnit.setSonarRoll(sonarRoll.getFloatValue());
		dataUnit.setFrameYaw(frameYaw.getFloatValue());
		dataUnit.setFramePitch(framePitch.getFloatValue());
		dataUnit.setFrameRoll(frameRoll.getFloatValue());
		dataUnit.setUvSonar(uvSonar.getIntegerValue());
		dataUnit.setUvPAM(uvPAM.getIntegerValue());
		
		return dataUnit;
	}

	
	
	
	
}
