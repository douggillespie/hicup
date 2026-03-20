package eteccontrol;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JMenuItem;

import Acquisition.AcquisitionControl;
import PamController.PamControlledUnit;
import PamController.PamControlledUnitSettings;
import PamController.PamController;
import PamController.PamSettingManager;
import PamController.PamSettings;
import PamUtils.PamCalendar;
import PamguardMVC.PamDataBlock;
import PamguardMVC.PamProcess;
import arduino.ArduinoControl;
import arduino.ArduinoDevice;
import arduino.ArduinoDeviceListener;
import dataGram.Datagram;
import warnings.PamWarning;
import warnings.WarningSystem;

public class EtecControl extends PamControlledUnit implements PamSettings, ArduinoDeviceListener {

	private static final String etecType = "ETEC PREAMP";
	
	private EtecParameters etecParameters = new EtecParameters();
	
	private EtecPreamplifier preamp;
	
	private PamWarning preampWarning = new PamWarning("ETEC Preamplifier Warning", "", 0);

	public EtecDataBlock etecDataBlock;
	
	private ArduinoControl arduino;
	
	public EtecControl(String unitName) {
		super(etecType, unitName);
		PamSettingManager.getInstance().registerSettings(this);
		addPamProcess(new EtecProcess());

		// create all of the Arduino devices and register this class as a device listener
		arduino = (ArduinoControl) PamController.getInstance().findControlledUnit(ArduinoControl.arduinoType);
		preamp = new EtecPreamplifier(arduino, etecType, 0);	// use 0 as the deviceID
		arduino.registerDeviceListener(this);
	}

	@Override
	public Serializable getSettingsReference() {
		return etecParameters;
	}

	@Override
	public long getSettingsVersion() {
		return EtecParameters.serialVersionUID;
	}

	@Override
	public boolean restoreSettings(PamControlledUnitSettings pamControlledUnitSettings) {
		etecParameters = ((EtecParameters) pamControlledUnitSettings.getSettings()).clone();
		return true;
	}

	/* (non-Javadoc)
	 * @see PamController.PamControlledUnit#notifyModelChanged(int)
	 */
	@Override
	public void notifyModelChanged(int changeType) {
		super.notifyModelChanged(changeType);
		if (changeType == PamController.INITIALIZATION_COMPLETE) {
			preamp.setGainAndFilter(etecParameters);
			EtecDataUnit etecDataUnit = new EtecDataUnit(PamCalendar.getTimeInMillis(), etecParameters);
			etecDataBlock.addPamData(etecDataUnit);
		}
	}


	/* (non-Javadoc)
	 * @see PamController.PamControlledUnit#createDetectionMenu(java.awt.Frame)
	 */
	@Override
	public JMenuItem createDetectionMenu(Frame parentFrame) {
		JMenuItem menuItem = new JMenuItem("Etec Preamplifier settings ...");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showEtecDialog(parentFrame);
			}
		});
		return menuItem;
	}

	protected void showEtecDialog(Frame parentFrame) {
		EtecParameters newParams = EtecDialog.showDialog(parentFrame, etecParameters, arduino);
		if (newParams != null) {
			etecParameters = newParams;
			preamp.setGainAndFilter(etecParameters);
			EtecDataUnit etecDataUnit = new EtecDataUnit(PamCalendar.getTimeInMillis(), etecParameters);
			etecDataBlock.addPamData(etecDataUnit);
		}
	}

	
	private class EtecProcess extends PamProcess {

		public EtecProcess() {
			super(EtecControl.this, null);
			etecDataBlock = new EtecDataBlock(this, 0);
			etecDataBlock.SetLogging(new EtecLogging(etecDataBlock));
			addOutputDataBlock(etecDataBlock);
		}

		@Override
		public void pamStart() {
		}

		@Override
		public void pamStop() {
			
		}

		/* (non-Javadoc)
		 * @see PamguardMVC.PamProcess#prepareProcess()
		 */
		@Override
		public void prepareProcess() {
			super.prepareProcess();
			preamp.setGainAndFilter(etecParameters);
			EtecDataUnit etecDataUnit = new EtecDataUnit(PamCalendar.getTimeInMillis(), etecParameters);
			etecDataBlock.addPamData(etecDataUnit);
		}
		
	}

	@Override
	public void messageFromDevice(String deviceName, int deviceID, String[] message) {
		
		// if this is an error message, throw a warning to alert the user
		if (message[message.length-1].equals(ArduinoDevice.NAK) && deviceName.equals(etecType)) {
			preampWarning.setWarningMessage("There was an error applying the latest filter/gain settings");
			preampWarning.setWarningTip("<html>The Arduino has sent the following error message:<br>"
					+ "     " + String.join(",", message) + "     <br>"
                    + "Gain and filter values may not be set.  Please check and try again</html>");
			preampWarning.setWarnignLevel(2);
			WarningSystem.getWarningSystem().addWarning(preampWarning);
		} else {
			WarningSystem.getWarningSystem().removeWarning(preampWarning);
		}
	}

	@Override
	public void devicePowerStat(String deviceName, int deviceID, boolean isOn) {
		// don't do anything - preamp is always on
	}
}
