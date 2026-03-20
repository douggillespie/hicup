package eteccontrol;

import java.awt.Window;

import PamView.dialog.PamDialog;
import arduino.ArduinoControl;

public class EtecDialog extends PamDialog {
	
	private static EtecDialog singleInstance;
	
	private EtecDialogPanel etecDialogPanel;

	private EtecParameters etecParameters;
	
	private ArduinoControl arduino;

	private EtecDialog(Window parentFrame) {
		super(parentFrame, "Etec Preampifier control", true);
		etecDialogPanel = new EtecDialogPanel();
		setDialogComponent(etecDialogPanel.getDialogComponent());
	}
	
	public static EtecParameters showDialog(Window parent, EtecParameters etecParameters, ArduinoControl arduino) {
		if (singleInstance == null || singleInstance.getOwner() != parent) {
			singleInstance = new EtecDialog(parent);
		}
		singleInstance.setParams(etecParameters);
		singleInstance.arduino = arduino;
		arduino.initializeDevices(); // initialize the preamp
		singleInstance.setVisible(true);
		return singleInstance.etecParameters;
	}

	private void setParams(EtecParameters etecParameters) {
		this.etecParameters = etecParameters;
		etecDialogPanel.setParams(etecParameters);
	}

	@Override
	public boolean getParams() {
		etecParameters = etecDialogPanel.getParams();
		return etecParameters != null;
	}

	@Override
	protected void okButtonPressed() {
		// tell the arduino devices to run their shutdown routines
		singleInstance.arduino.shutDownDevices();

		super.okButtonPressed();
	}

	@Override
	public void cancelButtonPressed() {
		etecParameters = null;
		
		// tell the arduino devices to run their shutdown routines
		singleInstance.arduino.shutDownDevices();
	}

	@Override
	public void restoreDefaultSettings() {
		setParams(new EtecParameters());
	}

}
