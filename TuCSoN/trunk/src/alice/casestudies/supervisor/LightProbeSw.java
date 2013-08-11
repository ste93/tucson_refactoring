package alice.casestudies.supervisor;

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
 * Probe used for communication from/to light actuator software
 * 
 * @author Steven Maraldi
 * 
 */

public class LightProbeSw extends Thread implements ISimpleProbe {

    private final SupervisorGUI gui;
    private final AbstractProbeId id;
    private TransducerId tId;
    private TransducerStandardInterface transducer;

    public LightProbeSw(final AbstractProbeId i) {
        super();
        this.id = i;
        this.gui = SupervisorGUI.getLightGUI();
    }

    public AbstractProbeId getIdentifier() {
        return this.id;
    }

    public TransducerId getTransducer() {
        return this.tId;
    }

    public boolean readValue(final String key) {
        if ("intensity".equals(key)) {
            final int intensity =
                    Integer.parseInt(this.gui.getIntensityValue());
            if (this.transducer == null) {
                TransducerManager.getTransducerManager();
                this.transducer =
                        TransducerManager
                                .getTransducer(this.tId.getAgentName());
            }
            try {
                this.transducer.notifyEnvEvent(key, intensity);
            } catch (final TucsonOperationNotPossibleException e) {
                e.printStackTrace();
                return false;
            } catch (final UnreachableNodeException e) {
                e.printStackTrace();
                return false;
            } catch (final OperationTimeOutException e) {
                e.printStackTrace();
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
            if (value > 0) {
                this.gui.setLedStatus(true);
            } else {
                this.gui.setLedStatus(false);
            }

            this.gui.setIntensityValue(value);
            return true;
        }
        return false;
    }

    private void speak(final String msg) {
        System.out.println("[**ENVIRONMENT**][RESOURCE "
                + this.id.getLocalName() + "] " + msg);
    }
}
