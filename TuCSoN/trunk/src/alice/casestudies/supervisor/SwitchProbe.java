package alice.casestudies.supervisor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import alice.respect.core.TransducerManager;
import alice.respect.situated.AbstractProbeId;
import alice.respect.situated.ISimpleProbe;
import alice.respect.situated.TransducerId;
import alice.respect.situated.TransducerStandardInterface;

public class SwitchProbe implements ActionListener, ISimpleProbe {

    private final AbstractProbeId id;
    private boolean lock = false;
    private TransducerId tId;
    private TransducerStandardInterface transducer;

    public SwitchProbe(final AbstractProbeId i) {
        this.id = i;
        SupervisorGUI.getLightGUI().addSwitchActionListener(this);
    }

    public void actionPerformed(final ActionEvent ae) {
        this.lock ^= true;
        try {
            if (this.transducer == null) {
                TransducerManager.getTransducerManager();
                this.transducer =
                        TransducerManager
                                .getTransducer(this.tId.getAgentName());
            }
            if (this.lock) {
                this.transducer.notifyEnvEvent("lock", 1);
            } else {
                this.transducer.notifyEnvEvent("lock", 0);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public AbstractProbeId getIdentifier() {
        return this.id;
    }

    public TransducerId getTransducer() {
        return this.tId;
    }

    public boolean readValue(final String key) {
        if (!"lock".equals(key)) {
            return false;
        }
        try {
            if (this.transducer == null) {
                TransducerManager.getTransducerManager();
                this.transducer =
                        TransducerManager.getTransducer(
                                this.tId.getAgentName());
            }
            if (this.lock) {
                this.transducer.notifyEnvEvent(key, 1);
            } else {
                this.transducer.notifyEnvEvent(key, 0);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public void setTransducer(final TransducerId t) {
        this.tId = t;
    }

    public boolean writeValue(final String key, final int value) {
        return false;
    }

}
