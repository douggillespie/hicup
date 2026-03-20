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

import java.awt.Window;

import javax.swing.JButton;

import PamView.dialog.PamDialog;

/**
 * @author mo55
 *
 */
public class HiCUPSonarOrientDialog extends PamDialog {

	private static HiCUPSonarOrientDialog singleInstance;
	
	private HiCUPSonarOrientDialogPanel sonarDialogPanel;

	private HiCUPSonarOrientParams sonarParams;
	
	/**
	 * @param parentFrame
	 * @param sonarDataBlock2 
	 * @param title
	 * @param hasDefault
	 */
	public HiCUPSonarOrientDialog(Window parentFrame, HiCUPSonarOrientDataBlock sonarDataBlock) {
		super(parentFrame, "HiCUP Sonar Orientation", true);
		sonarDialogPanel = new HiCUPSonarOrientDialogPanel(sonarDataBlock);
		setDialogComponent(sonarDialogPanel.getDialogComponent());
	}

	public static HiCUPSonarOrientParams showDialog(Window parent, HiCUPSonarOrientParams sonarParameters, HiCUPSonarOrientDataBlock sonarDataBlock) {
		if (singleInstance == null || singleInstance.getOwner() != parent) {
			singleInstance = new HiCUPSonarOrientDialog(parent, sonarDataBlock);
		}
		singleInstance.setParams(sonarParameters);
		singleInstance.setVisible(true);
		return singleInstance.sonarParams;
	}

	private void setParams(HiCUPSonarOrientParams sonarParameters) {
		this.sonarParams = sonarParameters;
		sonarDialogPanel.setParams(sonarParameters);
	}

	@Override
	public boolean getParams() {
		sonarParams = sonarDialogPanel.getParams();
		return sonarParams != null;
	}

	@Override
	public void cancelButtonPressed() {
		sonarDialogPanel.stopEverything(false);
	}
	

	@Override
	protected void okButtonPressed() {
		sonarDialogPanel.stopEverything(true);
		super.okButtonPressed();
	}

	@Override
	public void restoreDefaultSettings() {
		setParams(new HiCUPSonarOrientParams());
	}

}
