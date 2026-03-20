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

import PamguardMVC.PamProcess;

/**
 * @author mo55
 *
 */
public class HiCUPSonarOrientProcess extends PamProcess {
	
	private HiCUPSonarOrientControl sonarControl;
	
	private HiCUPSonarOrientDataBlock sonarDataBlock;
	
	private HiCUPSonarOrientLogging sonarLogging;
	

	/**
	 * @param pamControlledUnit
	 * @param parentDataBlock
	 * @param processName
	 */
	public HiCUPSonarOrientProcess(HiCUPSonarOrientControl pamControlledUnit) {
		super(pamControlledUnit, null);
		this.sonarControl = pamControlledUnit;
		sonarDataBlock = new HiCUPSonarOrientDataBlock(this, 0);
		sonarDataBlock.SetLogging(new HiCUPSonarOrientLogging(sonarDataBlock));
		addOutputDataBlock(sonarDataBlock);
	}

	public HiCUPSonarOrientDataBlock getSonarDataBlock() {
		return sonarDataBlock;
	}

	@Override
	public void pamStart() {
	}

	@Override
	public void pamStop() {
	}

}
