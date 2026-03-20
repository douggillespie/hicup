package hiCUPSonarOrientation.devices;

import arduino.ArduinoControl;
import arduino.ArduinoDevice;

public class Camera extends ArduinoDevice {

	/**
	 * BATTCHARGER - charge the battery
	 */
	private static final String CAMERA = "CAMERA";
	
	private static final String CAMERACHK = "CAMERACHK";

	
	/**
	 * @param mediator
	 * @param arduino
	 * @param deviceName
	 * @param deviceID
	 * @param powerCommand
	 * @param powerChkCommand
	 */
	public Camera(ArduinoControl arduino, String deviceName, int deviceID) {
		super(arduino, deviceName, deviceID, CAMERA, CAMERACHK);
	}

	@Override
	public void initialize() {
	}

	@Override
	public void shutDown() {
		turnOff();
	}

}
