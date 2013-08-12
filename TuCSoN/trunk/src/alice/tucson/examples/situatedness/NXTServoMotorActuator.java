package alice.tucson.examples.situatedness;

import alice.respect.core.TransducerManager;
import alice.respect.situated.AbstractProbeId;
import alice.respect.situated.ISimpleProbe;
import alice.respect.situated.TransducerId;
import alice.respect.situated.TransducerStandardInterface;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

public class NXTServoMotorActuator implements ISimpleProbe {

    private int angle = 0, power = 0;
    private final NxtSimulatorGUI gui;
    private final AbstractProbeId id;
    private TransducerId tId;

    private TransducerStandardInterface transducer;

    public NXTServoMotorActuator(final AbstractProbeId i) {
        this.id = i;
        this.gui = NxtSimulatorGUI.getNxtSimulatorGUI();
    }

    public AbstractProbeId getIdentifier() {
        return this.id;
    }

    public TransducerId getTransducer() {
        return this.tId;
    }

    public boolean readValue(final String key) {
        try {
            if ("power".equals(key)) {
                if (this.transducer == null) {
                    TransducerManager.getTransducerManager();
                    this.transducer =
                            TransducerManager.getTransducer(this.tId
                                    .getAgentName());
                }
                this.transducer.notifyEnvEvent(key, this.power);
                return true;
            } else if ("angle".equals(key)) {
                if (this.transducer == null) {
                    TransducerManager.getTransducerManager();
                    this.transducer =
                            TransducerManager.getTransducer(this.tId
                                    .getAgentName());
                }
                this.transducer.notifyEnvEvent(key, this.angle);
                return true;
            }
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
        System.err.println("WRITE REQUEST ( " + key + ", " + value + " )");
        if ("power".equals(key)) {
            this.power = value;
            if ("servoMotorActuatorLeft".equals(this.id.getLocalName())) {
                this.gui.setMotorParameters("left", "power", this.power);
            } else if ("servoMotorActuatorRight".equals(this.id.getLocalName())) {
                this.gui.setMotorParameters("right", "power", this.power);
            }
            return true;
        } else if ("angle".equals(key)) {
            this.angle = value;
            if ("servoMotorActuatorLeft".equals(this.id.getLocalName())) {
                this.gui.setMotorParameters("left", "angle", this.angle);
            } else if ("servoMotorActuatorRight".equals(this.id.getLocalName())) {
                this.gui.setMotorParameters("right", "angle", this.angle);
            }
            return true;
        }
        return false;
    }
}
