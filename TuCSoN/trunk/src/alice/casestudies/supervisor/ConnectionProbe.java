package alice.casestudies.supervisor;

import alice.respect.core.ISerialEventListener;
import alice.respect.core.SerialComm;
import alice.respect.core.TransducerManager;
import alice.respect.probe.ISimpleProbe;
import alice.respect.probe.AbstractProbeId;
import alice.respect.transducer.TransducerId;
import alice.respect.transducer.TransducerStandardInterface;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

/**
 * 
 * Probe used for interaction with Arduino board and SupervisorGUI. Used for
 * checking connection status.
 * 
 * @author Steven maraldi
 * 
 */

public class ConnectionProbe extends Thread implements ISimpleProbe,
        ISerialEventListener {

    private final SupervisorGUI gui;
    private final AbstractProbeId id;
    private TransducerId tId;
    private TransducerStandardInterface transducer;

    public ConnectionProbe(final AbstractProbeId i) {
        super();
        this.id = i;
        SerialComm.getSerialComm();
        SerialComm.addListener(this);
        this.gui = SupervisorGUI.getLightGUI();
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
            if ("status".equals(keyValue[0])) {
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
        if ("status".equals(key)) {
            final String msg = "RC"; // RP means Read Power
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
        if ("status".equals(key)) {
            if (value > 0) {
                this.gui.setConnectionStatus(true);
            } else {
                this.gui.setConnectionStatus(false);
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
