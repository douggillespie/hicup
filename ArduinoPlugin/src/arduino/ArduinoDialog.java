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

import java.awt.Frame;
import java.awt.Window;
import PamView.dialog.PamDialog;

/**
 * @author mo55
 *
 */
public class ArduinoDialog extends PamDialog {

	private static ArduinoDialog singleInstance;
	
	private ArduinoParams arduinoParams;
	
	/**
	 * JPanel containing text boxes for the IP Address and ports
	 */
	private ArduinoDialogPanel arduinoDialogPanel;
	
	
	/**
	 * @param parentFrame
	 * @param arduinoProcess 
	 * @param title
	 * @param hasDefault
	 */
	public ArduinoDialog(Window parentFrame, ArduinoProcess arduinoProcess) {
		super(parentFrame, "Arduino Communication", true);
		arduinoDialogPanel = new ArduinoDialogPanel(arduinoProcess);
		setDialogComponent(arduinoDialogPanel.getDialogComponent());
	}

	/**
	 * @param parentFrame
	 * @param arduinoParams
	 * @param arduinoProcess 
	 * @return
	 */
	public static ArduinoParams showDialog(Frame parentFrame, ArduinoParams arduinoParams, ArduinoProcess arduinoProcess) {
		if (singleInstance == null || singleInstance.getOwner() != parentFrame) {
			singleInstance = new ArduinoDialog(parentFrame, arduinoProcess);
		}
		singleInstance.setParams(arduinoParams);
		singleInstance.setVisible(true);
		return singleInstance.arduinoParams;
	}
	
	
	/**
	 * @param arduinoParams
	 */
	private void setParams(ArduinoParams arduinoParams) {
		this.arduinoParams = arduinoParams;
		arduinoDialogPanel.setParams(arduinoParams);
	}

	@Override
	public boolean getParams() {
		this.arduinoParams = arduinoDialogPanel.getParams();
		return (arduinoParams != null);
	}

	@Override
	public void cancelButtonPressed() {
		arduinoParams = null;
	}

	@Override
	public void restoreDefaultSettings() {
		setParams(new ArduinoParams());
	}


}
