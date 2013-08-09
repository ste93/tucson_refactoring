package alice.casestudies.supervisor;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import alice.respect.core.TransducerManager;
import alice.respect.probe.ISimpleProbe;
import alice.respect.probe.ProbeId;
import alice.respect.transducer.TransducerId;
import alice.respect.transducer.TransducerStandardInterface;

public class SliderProbeSw implements ChangeListener, ISimpleProbe {

    private final ProbeId id;
    private int sliderValue = 50;
    private TransducerId tId;
    private TransducerStandardInterface transducer;

    public SliderProbeSw(final ProbeId i) {
        this.id = i;
        SupervisorGUI.getLightGUI().addSliderChangeListener(this);
        this.readValue("intensity");
    }

    public ProbeId getIdentifier() {
        return this.id;
    }

    public TransducerId getTransducer() {
        return this.tId;
    }

    public boolean readValue(final String key) {
        if (!key.equals("intensity")) {
            return false;
        }
        try {
            if (this.transducer == null) {
                this.transducer =
                        TransducerManager.getTransducerManager().getTransducer(
                                this.tId.getAgentName());
            }
            this.transducer.notifyEnvEvent(key, this.sliderValue);
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public void setTransducer(final TransducerId t) {
        this.tId = t;
    }

    public void stateChanged(final ChangeEvent e) {
        final JSlider source = (JSlider) e.getSource();
        // if (!source.getValueIsAdjusting()) {
        this.sliderValue = source.getValue();
        // valueNormalized = sliderValue/10;
        try {
            if (this.transducer == null) {
                this.transducer =
                        TransducerManager.getTransducerManager().getTransducer(
                                this.tId.getAgentName());
            }
            this.transducer.notifyEnvEvent("intensity", this.sliderValue);
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
        // }
    }

    public boolean writeValue(final String key, final int value) {
        return false;
    }

}
