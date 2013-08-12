package alice.casestudies.supervisor;

import alice.casestudies.utils.ISerialEventListener;
import alice.casestudies.utils.SerialComm;
import alice.respect.core.TransducerManager;
import alice.respect.situated.AbstractProbeId;
import alice.respect.situated.ISimpleProbe;
import alice.respect.situated.TransducerId;
import alice.respect.situated.TransducerStandardInterface;
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

    private final AbstractProbeId id;
    private TransducerId tId;
    private TransducerStandardInterface transducer;

    public LightProbe(final AbstractProbeId i) {
        super();
        this.id = i;
        SerialComm.getSerialComm();
        SerialComm.addListener(this);
    }

    public AbstractProbeId getIdentifier() {
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
            final String[] keyValue = value.split("/");
            if ("intensity".equals(keyValue[0])) {
                if (this.transducer == null) {
                    TransducerManager.getTransducerManager();
                    this.transducer =
                            TransducerManager.getTransducer(this.tId
                                    .getAgentName());
                }
                this.transducer.notifyEnvEvent(keyValue[0],
                        Integer.parseInt(keyValue[1]));
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
