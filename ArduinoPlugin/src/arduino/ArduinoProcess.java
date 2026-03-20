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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.SwingWorker;
import PamView.dialog.PamDialog;
import PamguardMVC.PamProcess;

/**
 * @author mo55
 *
 */
public class ArduinoProcess  extends PamProcess {
	
	/**
	 * ArduinoControl object
	 */
	private ArduinoControl arduinoControl;
	
	/**
	 * Arduino parameters
	 */
	private ArduinoParams arduinoParams;
	
	/**
	 * Listener dedicated to listening for UDP messages from the Arduino in a separate thread
	 */
	SwingWorker<Void,Void> arduinoListener;

	/**
	 * Flag controlling whether or not this object is currently listening for an Arduino message
	 */
	private boolean listening;
	
	/**
	 * The local socket for UDP communication
	 */
	private DatagramSocket localSocket;
	
	/**
	 * A list of all ArduinoDevice objects that the Arduino can control
	 */
	private ArrayList<ArduinoDevice> deviceList = new ArrayList<ArduinoDevice>();
	
	/**
	 * Flag indicating whether or not UDP communication is ready
	 */
	private boolean commReady = false;

	/**
	 * The output data block.  Used for database logging
	 */
	private ArduinoDataBlock arduinoDataBlock;

	
	/**
	 * @param arduinoControl 
	 * 
	 */
	public ArduinoProcess(ArduinoControl arduinoControl) {
		super(arduinoControl, null);
		this.arduinoControl = arduinoControl;
		arduinoDataBlock = new ArduinoDataBlock(this, 0);
		arduinoDataBlock.SetLogging(new ArduinoLogging(arduinoDataBlock));
		addOutputDataBlock(arduinoDataBlock);
	}
	
	/**
	 * Open the UDP communications channel and start the Arduino listener in a new thread
	 * 
	 * @param arduinoParams The parameters to use for initiialization
	 */
	void initialize(ArduinoParams arduinoParams) {
		commReady=false;
		closeComms();
		
		// if the parameters are null, exit.  Otherwise, load them into the fields
		if (arduinoParams==null) return;
		this.arduinoParams = arduinoParams;
		
		// open the local socket for UDP send and receive
		try {
			System.out.println("Opening UDP send socket...");
			localSocket = new DatagramSocket(arduinoParams.getLocalPort());
		} catch (SocketException e) {
			e.printStackTrace();
			System.out.println(String.format("Unable to open local port %d", arduinoParams.getLocalPort()));
			return;
		} catch (NumberFormatException e) {
			PamDialog.showWarning(null, "Invalid port number", "Enter a valid integer port number (Default 8888");
			return;
		}
		
		// create a SwingWorker object to monitor the Arduino responses
		arduinoListener = new SwingWorker<Void,Void>()
		{
		    @Override
		    protected Void doInBackground()
		    {
		    	listenToArduino();
		    	System.out.println("All finished listening");
		        return null;
		    }
		};
		arduinoListener.execute();
		commReady = true;
	}
	
	
	/**
	 * Register a device that the Arduino will send to and receive from
	 * 
	 * @param arduinoDevice
	 */
	void registerDevice(ArduinoDevice arduinoDevice) {
		if (deviceList.contains(arduinoDevice)) return;
		deviceList.add(arduinoDevice);
	}
	
	
	/**
	 * tell all currently registered Arduino devices to run their startup routines (if they have one)	 
	 */
	void initializeDevices() {
		for (int i=0; i<deviceList.size(); i++) {
			deviceList.get(i).initialize();
		}
	}

	
	/**
	 * Send a command to the Arduino.  Refer to the Arduino Communications Protocol document for command format.
	 * 
	 * @param udpCommand the command to send
	 * @return boolean true if successful, or false if there was an error.  Note this only indicates that there was no error
	 * in the UDP transmission of the message.  To know whether the Arduino has understood and processed the message properly, you
	 * need to register as an ArduinoDeviceListener to receive messages back from the Arduino
	 */
	boolean sendCommand(String udpCommand) {
		if (!commReady) {
			System.out.println("Arduino communication not ready - please check IP Address and Port settings");
			return false;
		}
		InetAddress inetAddress;
		try {
			inetAddress = InetAddress.getByName(arduinoParams.getArduinoAddress());
		} catch (UnknownHostException e) {
			System.out.println("Unknown host " + arduinoParams.getArduinoAddress());
			e.printStackTrace();
			commReady=false;
			return false;
		}
		DatagramPacket dg = new DatagramPacket(udpCommand.getBytes(),
				udpCommand.length(),
				inetAddress, 
				arduinoParams.getArduinoPort());
		try {
			localSocket.send(dg);
		} catch (IOException e) {
			System.out.println("Unable to send datagram command " + udpCommand);
			e.printStackTrace();
			return false;
		}
		return true;
	}


	/**
	 * Listen for responses coming from the Arduino.  This should be run in a separate thread, so that it
	 * doesn't block while waiting for a response
	 */
	void listenToArduino() {
		System.out.println("Creating thread to listen to Arduino...");
		byte[] rxBuff = new byte[50];
		DatagramPacket rx = new DatagramPacket(rxBuff, rxBuff.length);
		
		listening = true;
		while(listening) {
//			System.out.println("Listening...");
			try {
				localSocket.setSoTimeout(1000);
				localSocket.receive(rx);
			} 
			
			// if we timeout, just start the loop again.  Add in this break, so that
			// we can stop the execution externally at any point by setting
			// the listening boolean to false
			catch (SocketTimeoutException e) {
//				System.out.println("waiting for Arduino message...");
				continue;
			}
			
			// if there's a different error, close the socket and return
			catch (SocketException e) {
				listening = false;
				System.out.println("Socket closed - can't listen for Arduino messages");
//				e.printStackTrace();
				commReady=false;
				return;
			} catch (IOException e) {
				listening = false;
				System.out.println("I/O error - can't listen for Arduino messages");
				e.printStackTrace();
				return;
			}			
			
			// If we've gotten to this stage, we have a message from the Arduino.
			String received = new String(rx.getData(),0,rx.getLength());
			String[] command = received.split(","); 
//			System.out.println("Arduino message received");
			
			// send the message to all registered devices
//			System.out.println("Sending message to devices:");
//			System.out.println(received);
			sendMessageToDevices(command);
//			System.out.println("go back to listening");
		}
	}

	
	/**
	 * @param command
	 */
	private void sendMessageToDevices(String[] command) {
		for (int i=0; i<deviceList.size(); i++) {
			deviceList.get(i).receiveMessage(command);
		}
	}
	

	void closeComms() {
		// stop listening and close the socket
		commReady=false;
		listening = false;
		if (localSocket!=null) localSocket.close();
	}
	

	/**
	 * tell all registered devices that we're shutting down
	 */
	void shutDownDevices() {
		for (int i=0; i<deviceList.size(); i++) {
			deviceList.get(i).shutDown();
		}
	}


	@Override
	public void setupProcess() {
		super.setupProcess();
		arduinoControl.initWithCurrentParams();
	}

	@Override
	public void pamStart() {
	}


	@Override
	public void pamStop() {
	}



}
