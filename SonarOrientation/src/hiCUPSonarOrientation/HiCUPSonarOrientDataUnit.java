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

import PamguardMVC.PamDataUnit;
import arduino.ArduinoParams;

/**
 * @author mo55
 * @param <T>
 * @param <U>
 *
 */
public class HiCUPSonarOrientDataUnit extends PamDataUnit {
	
	private HiCUPSonarOrientParams sonarParams;
	
	private float sonarYaw;
	
	private float sonarPitch;
	
	private float sonarRoll;
	
	private float frameYaw;
	
	private float framePitch;
	
	private float frameRoll;
	
	private int uvSonar;
	
	private int uvPAM;

	/**
	 * Basic constructor - used when retrieving data from database
	 * 
	 * @param timeMilliseconds
	 */
	public HiCUPSonarOrientDataUnit(long timeMilliseconds) {
		super(timeMilliseconds);
	}

	
	/**
	 * @param timeMilliseconds
	 * @param sonarParams
	 * @param sonarYaw
	 * @param sonarPitch
	 * @param sonarRoll
	 * @param frameYaw
	 * @param framePitch
	 * @param frameRoll
	 */
	public HiCUPSonarOrientDataUnit(long timeMilliseconds,
			HiCUPSonarOrientParams sonarParams,
			float sonarYaw,
			float sonarPitch,
			float sonarRoll,
			float frameYaw, 
			float framePitch, 
			float frameRoll,
			int uvSonar,
			int uvPAM) {
		super(timeMilliseconds);
		this.sonarParams = sonarParams;
		this.sonarYaw = sonarYaw;
		this.sonarPitch = sonarPitch;
		this.sonarRoll = sonarRoll;
		this.frameYaw = frameYaw;
		this.framePitch = framePitch;
		this.frameRoll = frameRoll;
		this.uvSonar = uvSonar;
		this.uvPAM = uvPAM;
	}

	public HiCUPSonarOrientParams getSonarParams() {
		return sonarParams;
	}

	public void setSonarParams(HiCUPSonarOrientParams sonarParams) {
		this.sonarParams = sonarParams;
	}

	public float getSonarYaw() {
		return sonarYaw;
	}

	public void setSonarYaw(float sonarYaw) {
		this.sonarYaw = sonarYaw;
	}

	public float getSonarPitch() {
		return sonarPitch;
	}

	public void setSonarPitch(float sonarPitch) {
		this.sonarPitch = sonarPitch;
	}

	public float getSonarRoll() {
		return sonarRoll;
	}

	public void setSonarRoll(float sonarRoll) {
		this.sonarRoll = sonarRoll;
	}

	public float getFrameYaw() {
		return frameYaw;
	}

	public void setFrameYaw(float frameYaw) {
		this.frameYaw = frameYaw;
	}

	public float getFramePitch() {
		return framePitch;
	}

	public void setFramePitch(float framePitch) {
		this.framePitch = framePitch;
	}

	public float getFrameRoll() {
		return frameRoll;
	}

	public void setFrameRoll(float frameRoll) {
		this.frameRoll = frameRoll;
	}


	public int getUvSonar() {
		return uvSonar;
	}


	public void setUvSonar(int uvSonar) {
		this.uvSonar = uvSonar;
	}


	public int getUvPAM() {
		return uvPAM;
	}


	public void setUvPAM(int uvPAM) {
		this.uvPAM = uvPAM;
	}
	
	

}
