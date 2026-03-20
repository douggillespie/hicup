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

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.JMenuItem;

import PamController.PamControlledUnit;
import PamController.PamControlledUnitSettings;
import PamController.PamSettingManager;
import PamController.PamSettings;

/**
 * @author mo55
 *
 */
public class HiCUPSonarOrientControl extends PamControlledUnit implements PamSettings {

	public static final String hiCUPSonarType = "HiCUP Sonar Orientation";
	
	private HiCUPSonarOrientParams sonarParams = new HiCUPSonarOrientParams();
	private HiCUPSonarOrientProcess sonarProcess;
	

	/**
	 * @param unitType
	 * @param unitName
	 */
	public HiCUPSonarOrientControl(String unitName) {
		super(hiCUPSonarType, unitName);
		PamSettingManager.getInstance().registerSettings(this);
		addPamProcess(sonarProcess = new HiCUPSonarOrientProcess(this));
	}

	@Override
	public JMenuItem createDetectionMenu(Frame parentFrame) {
		JMenuItem menuItem = new JMenuItem("HiCUP Sonar Orientation");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showSonarOrientationDialog(parentFrame);
			}
		});
		return menuItem;
	}


	private void showSonarOrientationDialog(Frame parentFrame) {
		HiCUPSonarOrientParams newParams = HiCUPSonarOrientDialog.showDialog(parentFrame, sonarParams, sonarProcess.getSonarDataBlock());
		if (newParams != null) {
			sonarParams = newParams;
		}
	}



	@Override
	public Serializable getSettingsReference() {
		return sonarParams;
	}

	@Override
	public long getSettingsVersion() {
		return sonarParams.serialVersionUID;
	}

	@Override
	public boolean restoreSettings(PamControlledUnitSettings pamControlledUnitSettings) {
		sonarParams = ((HiCUPSonarOrientParams) pamControlledUnitSettings.getSettings()).clone();
		return true;
	}


}
