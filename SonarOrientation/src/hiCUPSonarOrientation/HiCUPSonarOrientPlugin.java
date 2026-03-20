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

import PamModel.PamDependency;
import PamModel.PamPluginInterface;

/**
 * @author mo55
 *
 */
public class HiCUPSonarOrientPlugin implements PamPluginInterface {
	
	private String jarFile;

	@Override
	public String getDefaultName() {
		return "HiCUP Sonar Orientation";
	}

	@Override
	public String getHelpSetName() {
		return null;
	}

	@Override
	public void setJarFile(String jarFile) {
		this.jarFile = jarFile;
	}

	@Override
	public String getJarFile() {
		return jarFile;
	}

	@Override
	public String getDeveloperName() {
		return "Michael Oswald";
	}

	@Override
	public String getContactEmail() {
		return "support@pamguard.org";
	}

	@Override
	public String getVersion() {
		return "1.3";
	}

	@Override
	public String getPamVerDevelopedOn() {
		return "2.01.03e";
	}

	@Override
	public String getPamVerTestedOn() {
		return "2.01.03e";
	}

	@Override
	public String getAboutText() {
		return null;
	}

	@Override
	public String getClassName() {
		return HiCUPSonarOrientControl.class.getName();
	}

	@Override
	public String getDescription() {
		return "HiCUP Sonar Orientation";
	}

	@Override
	public String getMenuGroup() {
		return "Utilities";
	}

	@Override
	public String getToolTip() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PamDependency getDependency() {
		return (new PamDependency(null, "arduino.ArduinoControl"));
	}

	@Override
	public int getMinNumber() {
		return 0;
	}

	@Override
	public int getMaxNumber() {
		return 1;
	}

	@Override
	public int getNInstances() {
		return 1;
	}

	@Override
	public boolean isItHidden() {
		return false;
	}

	@Override
	public int allowedModes() {
		return PamPluginInterface.NOTINVIEWER;
	}

}
