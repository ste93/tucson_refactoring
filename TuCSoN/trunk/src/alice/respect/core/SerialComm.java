package alice.respect.core;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

    /** Serial communicator instances ( one for port ) **/
    private static HashMap<String, SerialComm> comm;

    /**
     * Default bits per second for COM port ( Baud rate ). Must be coherent with
     * boud rate setted into the device(s) which we're going to communicate with
     */
    private static final int DATA_RATE = 9600;

    /** Listeners of events from the serial port **/
    private static ArrayList<ISerialEventListener> listeners;

    /** Output stream from the port */
    private static OutputStream output;

    /** Default port for supported operative systems */
    private static final String PORT_NAMES[] = { "/dev/tty.usbserial-A9007UX1", // Mac
                                                                                // OS
                                                                                // X
            "/dev/ttyUSB0", // Linux
            "COM3", // Windows
    };

    /** The serial port we're going to use */
    private static SerialPort serialPort;

    /** Milliseconds to wait while port is opening */
    private static final int TIME_OUT = 2000;

    /**
     * Gets the static instance of serial communicator
     * 
     * @return the static instance of serial communicator
     */
    public synchronized static SerialComm getSerialComm() {
        if (SerialComm.comm == null) {
            SerialComm.comm = new HashMap<String, SerialComm>();
        }

        if (SerialComm.listeners == null) {
            SerialComm.listeners = new ArrayList<ISerialEventListener>();
        }

        if (!SerialComm.comm.containsKey("DEFAULT")) {
            for (final String element : SerialComm.PORT_NAMES) {
                if (SerialComm.comm.containsKey(element)) {
                    return SerialComm.comm.get(element);
                }
            }
            SerialComm.comm.put("DEFAULT", new SerialComm());
        }

        return SerialComm.comm.get("DEFAULT");
    }

    /**
     * Gets the static instance of serial communicator with default baud rate
     * 
     * @param portName
     *            name of the port
     * 
     * @return the static instance of serial communicator
     */
    public synchronized static SerialComm getSerialComm(final String portName) {
        return SerialComm.getSerialComm(portName, SerialComm.DATA_RATE);
    }

    /**
     * Gets the static instance of serial communicator with specified baud rate
     * 
     * @param portName
     *            name of the port
     * @param rate
     *            baud rate of the communication
     * 
     * @return the static instance of serial communicator
     */
    public static SerialComm
            getSerialComm(final String portName, final int rate) {
        if (SerialComm.comm == null) {
            SerialComm.comm = new HashMap<String, SerialComm>();
        }

        if (SerialComm.listeners == null) {
            SerialComm.listeners = new ArrayList<ISerialEventListener>();
        }

        if (!SerialComm.comm.containsKey(portName)) {
            for (final String element : SerialComm.PORT_NAMES) {
                if (portName.equals(element)
                        && SerialComm.comm.containsKey("DEFAULT")) {
                    return SerialComm.comm.get("DEFAULT");
                }
            }
            SerialComm.comm.put(portName, new SerialComm(portName, rate));
        }

        return SerialComm.comm.get(portName);
    }

    /**
     * Utility method to print a message to console
     * 
     * @param msg
     *            message to print
     */
    private static void speak(final String msg) {
        System.out.println("[Serial Communicator] " + msg);
    }

    /** Input stream buffered reader from the port */
    private BufferedReader input;

    /**
     * Default Constructor.
     * 
     * Checks if one of the ports specified in PORT_NAMES is available and
     * configure it for use.
     */
    private SerialComm() {
        SerialComm.speak("Starting default initialization");

        // Getting all the system ports to check their availability.
        CommPortIdentifier portId = null;
        final Enumeration<?> portEnum = CommPortIdentifier.getPortIdentifiers();

        // Finding an instance of serial port as set in PORT_NAMES.
        while (portEnum.hasMoreElements()) {
            final CommPortIdentifier currPortId =
                    (CommPortIdentifier) portEnum.nextElement();
            for (final String portName : SerialComm.PORT_NAMES) {
                if (currPortId.getName().equals(portName)) {
                    portId = currPortId;
                    break;
                }
            }
        }
        if (portId == null) {
            SerialComm.speak("Could not find COM port.");
            return;
        }

        // Initializing communication
        this.init(portId, SerialComm.DATA_RATE);
    }

    /**
     * Constructor with port name
     * 
     * @param portName
     *            name of the port which we're going to communicate with.
     * @param rate
     *            baud rate of the communication
     */
    private SerialComm(final String portName, final int rate) {
        SerialComm.speak("Starting initialization");

        // Getting all the system ports to check their availability.
        CommPortIdentifier portId = null;
        final Enumeration<?> portEnum = CommPortIdentifier.getPortIdentifiers();

        // Finding an instance of serial port as set in PORT_NAMES.
        while (portEnum.hasMoreElements()) {
            final CommPortIdentifier currPortId =
                    (CommPortIdentifier) portEnum.nextElement();
            if (currPortId.getName().equals(portName)) {
                portId = currPortId;
                break;
            }
        }
        if (portId == null) {
            SerialComm.speak("Could not find any device on port " + portName);
            return;
        }

        // Initializing communication
        this.init(portId, rate);
    }

    /**
     * Adds a listener for serial events generated from the device
     * 
     * @param l
     *            the serial event listener
     */
    public synchronized void addListener(final ISerialEventListener l) {
        SerialComm.listeners.add(l);
    }

    /**
     * Closing procedure.
     * 
     * This should be called when you stop using the port. This will prevent
     * port locking.
     */
    public synchronized void close() {
        if (SerialComm.serialPort != null) {
            SerialComm.serialPort.removeEventListener();
            SerialComm.serialPort.close();
            this.input = null;
            SerialComm.output = null;
            SerialComm.comm = null;
        }
    }

    /**
     * Gets the input buffered reader used by the communicator
     * 
     * @return input buffered reader
     */
    public BufferedReader getInputStream() {
        return this.input;
    }

    /**
     * Gets the output stream used by the communicator
     * 
     * @return output stream
     */
    public OutputStream getOutputStream() {
        return SerialComm.output;
    }

    /**
     * Removes a listener from the communication. The listener won't be able to
     * hear serial events from the device.
     * 
     * @param l
     *            the serial event listener
     */
    public synchronized void removeListener(final ISerialEventListener l) {
        if (SerialComm.listeners.contains(l)) {
            SerialComm.listeners.remove(l);
        }
    }

    /**
     * Handle an event on the serial port.
     * 
     * @param oEvent
     *            event to handle
     */
    public synchronized void serialEvent(final SerialPortEvent oEvent) {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                final String[] inputLine = this.input.readLine().split("/");
                final String destination = inputLine[0];
                final String value = inputLine[1] + "/" + inputLine[2];
                for (int i = 0; i < SerialComm.listeners.size(); i++) {
                    if (SerialComm.listeners.get(i).getListenerName()
                            .equals(destination)) {
                        SerialComm
                                .speak("Notify event to listener "
                                        + SerialComm.listeners.get(i)
                                                .getListenerName());
                        SerialComm.listeners.get(i).notifyEvent(value);
                    }
                }

            } catch (final Exception e) {
                // e.printStackTrace();
            }
        }
    }

    /**
     * 
     * Initializes communication properties.
     * 
     * @param portId
     *            port's identifier
     * @param rate
     *            baud rate of the communication
     */
    private void init(final CommPortIdentifier portId, final int rate) {
        try {
            // Opening the serial port and use class name for the appName.
            SerialComm.serialPort =
                    (SerialPort) portId.open(this.getClass().getName(),
                            SerialComm.TIME_OUT);

            // Setting port properties
            SerialComm.serialPort.setSerialPortParams(rate,
                    SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            // Open the streams from/to the board
            this.input =
                    new BufferedReader(new InputStreamReader(
                            SerialComm.serialPort.getInputStream()));
            SerialComm.output = SerialComm.serialPort.getOutputStream();

            // Adding event listeners
            SerialComm.serialPort.addEventListener(this);
            SerialComm.serialPort.notifyOnDataAvailable(true);

            // Cleaning output stream
            SerialComm.output.flush();
            Thread.sleep(100);
        } catch (final Exception e) {
            e.printStackTrace();
            return;
        }

        SerialComm.speak("Communication initialized with success. PORT: "
                + portId.getName() + " SPEED: " + rate);
    }
}