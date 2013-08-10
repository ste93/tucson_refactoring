package alice.casestudies.supervisor;

import alice.respect.core.ISerialEventListener;
import alice.respect.core.SerialComm;
import alice.respect.core.TransducerManager;
import alice.respect.probe.ISimpleProbe;
import alice.respect.probe.ProbeId;
import alice.respect.transducer.TransducerId;
import alice.respect.transducer.TransducerStandardInterface;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

/**
 * 
 * Probe used for communication from/to the ligh actuator hardware
 * 
 * @author Steven Maraldi
 * 
 */
public class LightProbe extends Thread implements ISimpleProbe,
        ISerialEventListener {

    private final ProbeId id;
    private TransducerId tId;
    private TransducerStandardInterface transducer;

    public LightProbe(final ProbeId i) {
        super();
        this.id = i;
        SerialComm.getSerialComm();
        SerialComm.addListener(this);
    }

    public ProbeId getIdentifier() {
        return this.id;
    }

    public String getListenerName() {
        return this.id.getLocalName();
    }

    public TransducerId getTransducer() {
        return this.tId;
    }

    public void notifyEvent(final String value) {
        try {
            final String[] key_value = value.split("/");
            if (key_value[0].equals("intensity")) {
                if (this.transducer == null) {
                    TransducerManager.getTransducerManager();
                    this.transducer =
                            TransducerManager.getTransducer(this.tId
                                    .getAgentName());
                }
                this.transducer.notifyEnvEvent(key_value[0],
                        Integer.parseInt(key_value[1]));
            }
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
        }
    }

    public boolean readValue(final String key) {
        if ("intensity".equals(key)) {
            final String msg = "RI"; // RP means Read Power
            try {
                SerialComm.getSerialComm();
                SerialComm.getOutputStream().write(msg.getBytes());
            } catch (final Exception e) {
                e.printStackTrace();
                SerialComm.getSerialComm().close();
                return false;
            }
            return true;
        }
        return false;
    }

    public void setTransducer(final TransducerId t) {
        this.tId = t;
    }

    public boolean writeValue(final String key, final int value) {
        this.speak("WRITE REQUEST ( " + key + ", " + value + " )");
        if ("intensity".equals(key)) {
            final String msg = "LI"; // Light intensity
            try {
                SerialComm.getSerialComm();
                SerialComm.getOutputStream().write(msg.getBytes());
                SerialComm.getSerialComm();
                SerialComm.getOutputStream().write(value);
            } catch (final Exception e) {
                e.printStackTrace();
                SerialComm.getSerialComm().close();
                return false;
            }
            return true;
        }
        return false;
    }

    private void speak(final String msg) {
        System.out.println("[**ENVIRONMENT**][RESOURCE "
                + this.id.getLocalName() + "] " + msg);
    }
}
