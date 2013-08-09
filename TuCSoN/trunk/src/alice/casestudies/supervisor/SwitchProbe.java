package alice.casestudies.supervisor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import alice.respect.core.TransducerManager;
import alice.respect.probe.ISimpleProbe;
import alice.respect.probe.ProbeId;
import alice.respect.transducer.TransducerId;
import alice.respect.transducer.TransducerStandardInterface;

public class SwitchProbe implements ActionListener, ISimpleProbe {

    private final ProbeId id;
    private boolean lock = false;
    private TransducerId tId;
    private TransducerStandardInterface transducer;

    public SwitchProbe(final ProbeId i) {
        this.id = i;
        SupervisorGUI.getLightGUI().addSwitchActionListener(this);
    }

    public void actionPerformed(final ActionEvent e) {
        this.lock = !this.lock;
        try {
            if (this.transducer == null) {
                this.transducer =
                        TransducerManager.getTransducerManager().getTransducer(
                                this.tId.getAgentName());
            }
            if (this.lock) {
                this.transducer.notifyEnvEvent("lock", 1);
            } else {
                this.transducer.notifyEnvEvent("lock", 0);
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    public ProbeId getIdentifier() {
        return this.id;
    }

    public TransducerId getTransducer() {
        return this.tId;
    }

    public boolean readValue(final String key) {
        if (!key.equals("lock")) {
            return false;
        }
        try {
            if (this.transducer == null) {
                this.transducer =
                        TransducerManager.getTransducerManager().getTransducer(
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
