package alice.casestudies.supervisor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import alice.respect.core.TransducerManager;
import alice.respect.probe.ISimpleProbe;
import alice.respect.probe.ProbeId;
import alice.respect.transducer.TransducerId;
import alice.respect.transducer.TransducerStandardInterface;

public class RangeProbe implements ActionListener, ISimpleProbe {

    private final SupervisorGUI gui;
    private final ProbeId id;
    private TransducerId tId;
    private TransducerStandardInterface transducer;

    public RangeProbe(final ProbeId i) {
        this.id = i;
        this.gui = SupervisorGUI.getLightGUI();
        this.gui.addRangeButtonActionListener(this);
    }

    public void actionPerformed(final ActionEvent arg0) {
        try {
            if (((javax.swing.JButton) arg0.getSource()).getName().equals(
                    "btnMin")) {
                if (this.transducer == null) {
                    this.transducer =
                            TransducerManager.getTransducerManager()
                                    .getTransducer(this.tId.getAgentName());
                }
                this.transducer.notifyEnvEvent("min",
                        Integer.parseInt(this.gui.getMinValue()));
            } else if (((javax.swing.JButton) arg0.getSource()).getName()
                    .equals("btnMax")) {
                if (this.transducer == null) {
                    this.transducer =
                            TransducerManager.getTransducerManager()
                                    .getTransducer(this.tId.getAgentName());
                }
                this.transducer.notifyEnvEvent("max",
                        Integer.parseInt(this.gui.getMaxValue()));
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public ProbeId getIdentifier() {
        return this.id;
    }

    public TransducerId getTransducer() {
        return this.tId;
    }

    public boolean readValue(final String key) {
        try {
            if (key.equals("min")) {
                if (this.transducer == null) {
                    this.transducer =
                            TransducerManager.getTransducerManager()
                                    .getTransducer(this.tId.getAgentName());
                }
                this.transducer.notifyEnvEvent(key,
                        Integer.parseInt(this.gui.getMinValue()));
                return true;
            } else if (key.equals("max")) {
                if (this.transducer == null) {
                    this.transducer =
                            TransducerManager.getTransducerManager()
                                    .getTransducer(this.tId.getAgentName());
                }
                this.transducer.notifyEnvEvent(key,
                        Integer.parseInt(this.gui.getMaxValue()));
                return true;
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
