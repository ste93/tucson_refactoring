package alice.casestudies.wanderaround;

import alice.respect.core.TransducerManager;
import alice.respect.probe.ISimpleProbe;
import alice.respect.probe.ProbeId;
import alice.respect.transducer.TransducerId;
import alice.respect.transducer.TransducerStandardInterface;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

public class NXT_ServoMotorActuator implements ISimpleProbe {

    private int angle = 0, power = 0;
    private final NxtSimulatorGUI gui;
    private final ProbeId id;
    private TransducerId tId;

    private TransducerStandardInterface transducer;

    public NXT_ServoMotorActuator(final ProbeId i) {
        this.id = i;
        this.gui = NxtSimulatorGUI.getNxtSimulatorGUI();
    }

    public ProbeId getIdentifier() {
        return this.id;
    }

    public TransducerId getTransducer() {
        return this.tId;
    }

    public boolean readValue(final String key) {
        try {
            if (key.equals("power")) {
                if (this.transducer == null) {
                    this.transducer =
                            TransducerManager.getTransducerManager()
                                    .getTransducer(this.tId.getAgentName());
                }
                this.transducer.notifyEnvEvent(key, this.power);
                return true;
            } else if (key.equals("angle")) {
                if (this.transducer == null) {
                    this.transducer =
                            TransducerManager.getTransducerManager()
                                    .getTransducer(this.tId.getAgentName());
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
        if (key.equals("power")) {
            this.power = value;
            if (this.id.getLocalName().equals("servoMotorActuatorLeft")) {
                this.gui.setMotorParameters("left", "power", this.power);
            } else if (this.id.getLocalName().equals("servoMotorActuatorRight")) {
                this.gui.setMotorParameters("right", "power", this.power);
            }
            return true;
        } else if (key.equals("angle")) {
            this.angle = value;
            if (this.id.getLocalName().equals("servoMotorActuatorLeft")) {
                this.gui.setMotorParameters("left", "angle", this.angle);
            } else if (this.id.getLocalName().equals("servoMotorActuatorRight")) {
                this.gui.setMotorParameters("right", "angle", this.angle);
            }
            return true;
        }
        return false;
    }
}
