package alice.casestudies.supervisor;

import alice.respect.core.TransducerManager;
import alice.respect.situated.AbstractProbeId;
import alice.respect.situated.ISimpleProbe;
import alice.respect.situated.TransducerId;
import alice.respect.situated.TransducerStandardInterface;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

public class TimerProbe extends Thread implements ISimpleProbe {

    private final SupervisorGUI gui;
    private final AbstractProbeId id;
    private TransducerId tId;
    private int time = 0;
    private TransducerStandardInterface transducer;

    public TimerProbe(final AbstractProbeId i) {
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
        if ("time".equals(key)) {
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
        if ("time".equals(key)) {
            if (value == 0) {
                this.time = value;
                this.gui.setTimeValue(0);
            } else {
                this.time++;
                this.gui.setTimeValue(this.time);
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
