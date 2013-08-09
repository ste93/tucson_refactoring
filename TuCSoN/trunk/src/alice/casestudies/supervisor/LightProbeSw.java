package alice.casestudies.supervisor;

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
 * Probe used for communication from/to light actuator software
 * 
 * @author Steven Maraldi
 * 
 */

public class LightProbeSw extends Thread implements ISimpleProbe {

    private final SupervisorGUI gui;
    private final ProbeId id;
    private TransducerId tId;
    private TransducerStandardInterface transducer;

    public LightProbeSw(final ProbeId i) {
        this.id = i;
        this.gui = SupervisorGUI.getLightGUI();
    }

    public ProbeId getIdentifier() {
        return this.id;
    }

    public TransducerId getTransducer() {
        return this.tId;
    }

    public boolean readValue(final String key) {
        if (key.equals("intensity")) {
            final int intensity =
                    Integer.parseInt(this.gui.getIntensityValue());
            if (this.transducer == null) {
                this.transducer =
                        TransducerManager.getTransducerManager().getTransducer(
                                this.tId.getAgentName());
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
        if (key.equals("intensity")) {
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
