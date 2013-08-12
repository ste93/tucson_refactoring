package alice.casestudies.supervisor;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import alice.respect.core.TransducerManager;
import alice.respect.situated.AbstractProbeId;
import alice.respect.situated.ISimpleProbe;
import alice.respect.situated.TransducerId;
import alice.respect.situated.TransducerStandardInterface;

public class SliderProbeSw implements ChangeListener, ISimpleProbe {

    private final AbstractProbeId id;
    private int sliderValue = 50;
    private TransducerId tId;
    private TransducerStandardInterface transducer;

    public SliderProbeSw(final AbstractProbeId i) {
        this.id = i;
        SupervisorGUI.getLightGUI().addSliderChangeListener(this);
        this.readValue("intensity");
    }

    public AbstractProbeId getIdentifier() {
        return this.id;
    }

    public TransducerId getTransducer() {
        return this.tId;
    }

    public boolean readValue(final String key) {
        if (!"intensity".equals(key)) {
            return false;
        }
        try {
            if (this.transducer == null) {
                TransducerManager.getTransducerManager();
                this.transducer =
                        TransducerManager
                                .getTransducer(this.tId.getAgentName());
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

    public void stateChanged(final ChangeEvent ce) {
        final JSlider source = (JSlider) ce.getSource();
        // if (!source.getValueIsAdjusting()) {
        this.sliderValue = source.getValue();
        // valueNormalized = sliderValue/10;
        try {
            if (this.transducer == null) {
                TransducerManager.getTransducerManager();
                this.transducer =
                        TransducerManager
                                .getTransducer(this.tId.getAgentName());
            }
            this.transducer.notifyEnvEvent("intensity", this.sliderValue);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        // }
    }

    public boolean writeValue(final String key, final int value) {
        return false;
    }

}
