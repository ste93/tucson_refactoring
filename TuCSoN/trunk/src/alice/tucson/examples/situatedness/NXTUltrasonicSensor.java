package alice.tucson.examples.situatedness;

import alice.respect.core.TransducerManager;
import alice.respect.situatedness.AbstractProbeId;
import alice.respect.situatedness.ISimpleProbe;
import alice.respect.situatedness.TransducerId;
import alice.respect.situatedness.TransducerStandardInterface;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

public class NXTUltrasonicSensor implements ISimpleProbe, ISensorEventListener {

    private int distance = 0;
    private final AbstractProbeId id;
    private TransducerId tId;

    private TransducerStandardInterface transducer;

    public NXTUltrasonicSensor(final AbstractProbeId i) {
        this.id = i;
        final DistanceGenerator resource = new DistanceGenerator();
        resource.addListener(this);
        resource.start();
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

    public void notifyEvent(final String key, final int value) {
        try {
            if ("distance".equals(key)) {
                this.distance = value;
                if (this.transducer == null) {
                    TransducerManager.getTransducerManager();
                    this.speakErr("this.tId = " + this.tId
                            + ", this.tId.getAgentName() = "
                            + this.tId.getAgentName());
                    this.transducer =
                            TransducerManager.getTransducer(this.tId
                                    .getAgentName());
                }
                this.transducer.notifyEnvEvent(key, this.distance);
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
        try {
            if ("distance".equals(key)) {
                if (this.transducer == null) {
                    TransducerManager.getTransducerManager();
                    this.transducer =
                            TransducerManager.getTransducer(this.tId
                                    .getAgentName());
                }
                this.transducer.notifyEnvEvent(key, this.distance);
            }
            return true;
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setTransducer(final TransducerId t) {
        this.tId = t;
    }

    public boolean writeValue(final String key, final int value) {
        this.speakErr("I'm a sensor, I can't set values. Try asking to an actuator next time!!");
        return false;
    }

    private void speakErr(final String msg) {
        System.err.println("[**ENVIRONMENT**][RESOURCE "
                + this.id.getLocalName() + "] " + msg);
    }
}
