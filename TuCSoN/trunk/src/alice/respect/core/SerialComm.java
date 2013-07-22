package alice.respect.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * 
 * Class used for serial communication.
 * 
 * It provides methods to receive/send information from/to a serial port.
 * 
 * @author Steven Maraldi
 *
 */

public class SerialComm implements SerialPortEventListener {
		
	/** The serial port we're going to use */
	private static SerialPort serialPort;

	/** Default port for supported operative systems */
	private static final String PORT_NAMES[] = { 
			"/dev/tty.usbserial-A9007UX1", // Mac OS X
			"/dev/ttyUSB0", // Linux
			"COM3", // Windows
	};

	/** Input stream buffered reader from the port */
	private BufferedReader input;
	
	/** Output stream from the port */
	private static OutputStream output;
	
	/** Milliseconds to wait while port is opening */
	private static final int TIME_OUT = 2000;
	
	/** Default bits per second for COM port ( Baud rate ). 
	 *  Must be coherent with boud rate setted into the device(s) which we're going to communicate with */
	private static final int DATA_RATE = 9600;
	
	/** Listeners of events from the serial port **/
	private static ArrayList<ISerialEventListener> listeners;
	
	/** Serial communicator instances ( one for port ) **/
	private static HashMap< String, SerialComm> comm;
	
	/**
	 *  Default Constructor. 
	 *  
	 *  Checks if one of the ports specified in PORT_NAMES is available and configure it for use.
	 */
	private SerialComm() {
		speak("Starting default initialization");
		
		// Getting all the system ports to check their availability.
		CommPortIdentifier portId = null;
		Enumeration<?> portEnum = CommPortIdentifier.getPortIdentifiers();

		// Finding an instance of serial port as set in PORT_NAMES.
		while ( portEnum.hasMoreElements() ) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					break;
				}
			}
		}
		if (portId == null) {
			speak("Could not find COM port.");
			return;
		}
		
		// Initializing communication
		init( portId, DATA_RATE );
	}
	
	/**
	 * Constructor with port name
	 * 
	 * @param portName
	 * 		name of the port which we're going to communicate with.
	 * @param rate
	 * 		baud rate of the communication
	 */
	private SerialComm( String portName, int rate){
		speak("Starting initialization");
		
		// Getting all the system ports to check their availability.
		CommPortIdentifier portId = null;
		Enumeration<?> portEnum = CommPortIdentifier.getPortIdentifiers();

		// Finding an instance of serial port as set in PORT_NAMES.
		while ( portEnum.hasMoreElements() ) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			if (currPortId.getName().equals(portName)) {
				portId = currPortId;
				break;
			}
		}
		if (portId == null) {
			speak("Could not find any device on port "+portName);
			return;
		}
		
		// Initializing communication
		init( portId, rate );
	}
	
	/**
	 * 
	 * Initializes communication properties.
	 * 
	 * @param portId
	 * 		port's identifier
	 * @param rate
	 * 		baud rate of the communication
	 */
	private void init( CommPortIdentifier portId, int rate ){
		try {
			// Opening the serial port and use class name for the appName.
			serialPort = (SerialPort) portId.open( this.getClass().getName(), TIME_OUT );

			// Setting port properties
			serialPort.setSerialPortParams(rate,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// Open the streams from/to the board
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
			output = serialPort.getOutputStream();

			// Adding event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
			
			// Cleaning output stream
			output.flush();
			Thread.sleep(100);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		speak("Communication initialized with success. PORT: "+portId.getName()+" SPEED: "+rate);
	}
	
	/**
	 * Gets the static instance of serial communicator
	 * 
	 * @return
	 * 		the static instance of serial communicator
	 */
	public synchronized static SerialComm getSerialComm(){
		if( comm == null )
			comm = new HashMap< String, SerialComm >();
		
		if( listeners == null )
			listeners = new ArrayList<ISerialEventListener>();
		
		if( !comm.containsKey("DEFAULT") ){
			for( int i=0; i<PORT_NAMES.length; i++ ){
				if( comm.containsKey( PORT_NAMES[i] ) )
					return comm.get( PORT_NAMES[i] );
			}
			comm.put( "DEFAULT", new SerialComm() );
		}
			
		return comm.get("DEFAULT");
	}
	
	/**
	 * Gets the static instance of serial communicator with default baud rate
	 * 
	 * @param portName
	 * 		name of the port
	 * 
	 * @return
	 * 		the static instance of serial communicator
	 */
	public synchronized static SerialComm getSerialComm( String portName ){
		return SerialComm.getSerialComm(portName, DATA_RATE);
	}
	
	/**
	 * Gets the static instance of serial communicator with specified baud rate
	 * 
	 * @param portName
	 * 		name of the port
	 * @param rate
	 * 		baud rate of the communication
	 * 
	 * @return
	 * 		the static instance of serial communicator
	 */
	public static SerialComm getSerialComm( String portName, int rate ){
		if( comm == null )
			comm = new HashMap< String, SerialComm >();
		
		if( listeners == null )
			listeners = new ArrayList<ISerialEventListener>();
			
		if( !comm.containsKey( portName ) ){
			for( int i=0; i<PORT_NAMES.length; i++){
				if( portName.equals( PORT_NAMES[i] ) && comm.containsKey("DEFAULT") )
					return comm.get("DEFAULT");
			}
			comm.put( portName, new SerialComm( portName, rate ) );
		}
			
		return comm.get( portName );
	}
	
	/**
	 * Adds a listener for serial events generated from the device
	 * 
	 * @param l
	 * 		the serial event listener
	 */
	public synchronized void addListener( ISerialEventListener l ){
		listeners.add( l );
	}
	
	/**
	 * Removes a listener from the communication. The listener won't be able to hear serial events from the device.
	 * 
	 * @param l
	 * 		the serial event listener
	 */
	public synchronized void removeListener( ISerialEventListener l ){
		if( listeners.contains( l ) ){
			listeners.remove( l );
		}
	}

	/**
	 * Closing procedure.
	 * 
	 * This should be called when you stop using the port.
	 * This will prevent port locking.
	 */
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
			input = null;
			output = null;
			comm = null;
		}
	}

	/**
	 * Handle an event on the serial port.
	 * 
	 * @param oEvent
	 * 		event to handle
	 */
	public synchronized void serialEvent( SerialPortEvent oEvent ) {
		if( oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE ) {
			try {
				String[] inputLine = input.readLine().split("/");
				String destination = inputLine[0];
				String value = inputLine[1] + "/" + inputLine[2];
				for( int i=0; i<listeners.size(); i++ ){
					if( listeners.get(i).getListenerName().equals( destination ) ){
						speak("Notify event to listener "+listeners.get(i).getListenerName());
						listeners.get(i).notifyEvent( value );
					}
				}
				
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
	}
	
	/**
	 * Gets the input buffered reader used by the communicator
	 * 
	 * @return
	 * 		input buffered reader
	 */
	public BufferedReader getInputStream(){
		return input;
	}
	
	/**
	 * Gets the output stream used by the communicator
	 * 
	 * @return
	 * 		output stream
	 */
	public OutputStream getOutputStream(){
		return output;
	}
	
	/**
	 *  Utility method to print a message to console
	 *  
	 * @param msg
	 * 		message to print
	 */	
	private static void speak( String msg ){
		System.out.println("[Serial Communicator] "+ msg );
	}
}