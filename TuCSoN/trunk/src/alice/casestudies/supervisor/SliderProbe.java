package alice.casestudies.supervisor;

import alice.respect.core.ISerialEventListener;
import alice.respect.core.SerialComm;
import alice.respect.core.TransducerManager;
import alice.respect.probe.ISimpleProbe;
import alice.respect.probe.ProbeId;
import alice.respect.transducer.TransducerId;
import alice.respect.transducer.TransducerStandardInterface;

public class SliderProbe implements ISerialEventListener, ISimpleProbe {

    private final ProbeId id;
    private TransducerId tId;
    private TransducerStandardInterface transducer;

    public SliderProbe(final ProbeId i) {
        this.id = i;
        SerialComm.getSerialComm();
        SerialComm.addListener(this);
    }

    public ProbeId getIdentifier() {
        return this.id;
    }

    public String getListenerName() {
        return this.id.getLocalName();
    }

    public TransducerId getTransducer() {
        return this.tId;
    }

    public void notifyEvent(final String value) {
        final String[] key_value = value.split("/");
        final int sliderValue = Integer.parseInt(key_value[1]);
        // valueNormalized = sliderValue/10;
        try {
            if (this.transducer == null) {
                TransducerManager.getTransducerManager();
                this.transducer =
                        TransducerManager
                                .getTransducer(this.tId.getAgentName());
            }
            this.transducer.notifyEnvEvent(key_value[0], sliderValue);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public boolean readValue(final String key) {
        if ("intensity".equals(key)) {
            final String msg = "RI"; // RD means Read Distance
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
        return false;
    }

}
