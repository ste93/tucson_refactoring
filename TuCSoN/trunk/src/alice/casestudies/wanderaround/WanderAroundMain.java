package alice.casestudies.wanderaround;

import java.io.IOException;

import alice.logictuple.LogicTuple;
import alice.logictuple.Value;
import alice.respect.api.TupleCentreId;
import alice.tucson.api.AbstractTucsonAgent;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.SynchACC;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

/**
 * 
 * Main per il caso di studio "Wander Around".
 * 
 * @author Steven maraldi
 * 
 */

public class WanderAroundMain extends AbstractTucsonAgent {

    // ACC
    private static SynchACC acc;
    private static TupleCentreId TC_Avoid;
    // Actuators
    private static TupleCentreId TC_M1; // Left servomotor actuator
    private static TupleCentreId TC_M2; // Right servomotor actuator
    private static TupleCentreId TC_Motor;
    private static TupleCentreId TC_RunAway;
    // Application tuple centres
    private static TupleCentreId TC_Sonar;
    // Sensors
    private static TupleCentreId TC_U1; // Front ultrasonic sensor
    private static TupleCentreId TC_U2; // Right ultrasonic sensor
    private static TupleCentreId TC_U3; // Back ultrasonic sensor

    private static TupleCentreId TC_U4; // Left ultrasonic sensor

    // Environment configuration tuple centre
    private static TupleCentreId tcId;

    public static void main(final String[] args) {
        try {
            WanderAroundMain.TC_U1 =
                    new TupleCentreId("tc_u1", "localhost", "" + 20504);
            WanderAroundMain.TC_U2 =
                    new TupleCentreId("tc_u2", "localhost", "" + 20504);
            WanderAroundMain.TC_U3 =
                    new TupleCentreId("tc_u3", "localhost", "" + 20504);
            WanderAroundMain.TC_U4 =
                    new TupleCentreId("tc_u4", "localhost", "" + 20504);
            WanderAroundMain.TC_M1 =
                    new TupleCentreId("tc_m1", "localhost", "" + 20504);
            WanderAroundMain.TC_M2 =
                    new TupleCentreId("tc_m2", "localhost", "" + 20504);
            WanderAroundMain.TC_Sonar =
                    new TupleCentreId("tc_sonar", "localhost", "" + 20504);
            WanderAroundMain.TC_RunAway =
                    new TupleCentreId("tc_runaway", "localhost", "" + 20504);
            WanderAroundMain.TC_Motor =
                    new TupleCentreId("tc_motor", "localhost", "" + 20504);
            WanderAroundMain.TC_Avoid =
                    new TupleCentreId("tc_avoid", "localhost", "" + 20504);
            WanderAroundMain.tcId =
                    new TupleCentreId("envConfigTC", "localhost", "" + 20504);

            // Starting test
            final WanderAroundMain mainTest = new WanderAroundMain("main");
            mainTest.go();
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void createAgents() throws TucsonInvalidAgentIdException {
        WanderAroundMain.speak("Creating agents");
        final AG_FeelForce ag_feelforce = new AG_FeelForce("ag_feelforce");
        final AG_Collide ag_collide = new AG_Collide("ag_collide");
        final AG_Wander ag_wander = new AG_Wander("ag_wander");

        WanderAroundMain.speak("Starting agents");
        ag_collide.go();
        ag_feelforce.go();
        ag_wander.go();
        WanderAroundMain.speak("Agents created and started");
    }

    private static void createTCs() throws IOException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        WanderAroundMain.speak("Creating TCs...");
        // Sensor's TCs
        WanderAroundMain
                .speak("Injecting 'table' ReSpecT specification in tc < "
                        + WanderAroundMain.TC_U1.toString() + " >...");
        LogicTuple specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/casestudies/wanderaround/specTcSensore.rsp")));
        WanderAroundMain.acc.setS(WanderAroundMain.TC_U1, specTuple, null);
        WanderAroundMain
                .speak("Injecting 'table' ReSpecT specification in tc < "
                        + WanderAroundMain.TC_U2.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/casestudies/wanderaround/specTcSensore.rsp")));
        WanderAroundMain.acc.setS(WanderAroundMain.TC_U2, specTuple, null);
        WanderAroundMain
                .speak("Injecting 'table' ReSpecT specification in tc < "
                        + WanderAroundMain.TC_U3.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/casestudies/wanderaround/specTcSensore.rsp")));
        WanderAroundMain.acc.setS(WanderAroundMain.TC_U3, specTuple, null);
        WanderAroundMain
                .speak("Injecting 'table' ReSpecT specification in tc < "
                        + WanderAroundMain.TC_U4.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/casestudies/wanderaround/specTcSensore.rsp")));
        WanderAroundMain.acc.setS(WanderAroundMain.TC_U4, specTuple, null);

        // Actuator's TCs
        WanderAroundMain
                .speak("Injecting 'table' ReSpecT specification in tc < "
                        + WanderAroundMain.TC_M1.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/casestudies/wanderaround/specTcAttuatoreLeft.rsp")));
        WanderAroundMain.acc.setS(WanderAroundMain.TC_M1, specTuple, null);
        WanderAroundMain
                .speak("Injecting 'table' ReSpecT specification in tc < "
                        + WanderAroundMain.TC_M2.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/casestudies/wanderaround/specTcAttuatoreRight.rsp")));
        WanderAroundMain.acc.setS(WanderAroundMain.TC_M2, specTuple, null);

        // TC Sonar
        WanderAroundMain
                .speak("Injecting 'table' ReSpecT specification in tc < "
                        + WanderAroundMain.TC_Sonar.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/casestudies/wanderaround/specTcSonar.rsp")));
        WanderAroundMain.acc.setS(WanderAroundMain.TC_Sonar, specTuple, null);

        // TC Run Away
        WanderAroundMain
                .speak("Injecting 'table' ReSpecT specification in tc < "
                        + WanderAroundMain.TC_RunAway.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/casestudies/wanderaround/specTcRunAway.rsp")));
        WanderAroundMain.acc.setS(WanderAroundMain.TC_RunAway, specTuple, null);

        // TC Motor
        WanderAroundMain
                .speak("Injecting 'table' ReSpecT specification in tc < "
                        + WanderAroundMain.TC_Motor.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/casestudies/wanderaround/specTcMotor.rsp")));
        WanderAroundMain.acc.setS(WanderAroundMain.TC_Motor, specTuple, null);

        // TC Avoid
        WanderAroundMain
                .speak("Injecting 'table' ReSpecT specification in tc < "
                        + WanderAroundMain.TC_Avoid.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/casestudies/wanderaround/specTcAvoid.rsp")));
        WanderAroundMain.acc.setS(WanderAroundMain.TC_Avoid, specTuple, null);
        WanderAroundMain.speak("TCs created");
    }

    private static void createTransducers()
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        WanderAroundMain.speak("Creating transducers");
        LogicTuple t =
                new LogicTuple(
                        "createTransducerSensor",
                        new Value(WanderAroundMain.TC_U1.toString()),
                        new Value(
                                "alice.casestudies.wanderaround.TransducerSens"),
                        new Value("tSensorFront"),
                        new Value(
                                "alice.casestudies.wanderaround.NXT_UltrasonicSensor"),
                        new Value("ultrasonicSensorFront"));
        WanderAroundMain.acc.out(WanderAroundMain.tcId, t, null);
        t =
                new LogicTuple(
                        "createTransducerSensor",
                        new Value(WanderAroundMain.TC_U2.toString()),
                        new Value(
                                "alice.casestudies.wanderaround.TransducerSens"),
                        new Value("tSensorRight"),
                        new Value(
                                "alice.casestudies.wanderaround.NXT_UltrasonicSensor"),
                        new Value("ultrasonicSensorRight"));
        WanderAroundMain.acc.out(WanderAroundMain.tcId, t, null);
        t =
                new LogicTuple(
                        "createTransducerSensor",
                        new Value(WanderAroundMain.TC_U3.toString()),
                        new Value(
                                "alice.casestudies.wanderaround.TransducerSens"),
                        new Value("tSensorBack"),
                        new Value(
                                "alice.casestudies.wanderaround.NXT_UltrasonicSensor"),
                        new Value("ultrasonicSensorBack"));
        WanderAroundMain.acc.out(WanderAroundMain.tcId, t, null);
        t =
                new LogicTuple(
                        "createTransducerSensor",
                        new Value(WanderAroundMain.TC_U4.toString()),
                        new Value(
                                "alice.casestudies.wanderaround.TransducerSens"),
                        new Value("tSensorLeft"),
                        new Value(
                                "alice.casestudies.wanderaround.NXT_UltrasonicSensor"),
                        new Value("ultrasonicSensorLeft"));
        WanderAroundMain.acc.out(WanderAroundMain.tcId, t, null);

        t =
                new LogicTuple(
                        "createTransducerActuator",
                        new Value(WanderAroundMain.TC_M1.toString()),
                        new Value(
                                "alice.casestudies.wanderaround.TransducerAct"),
                        new Value("tMotorLeft"),
                        new Value(
                                "alice.casestudies.wanderaround.NXT_ServoMotorActuator"),
                        new Value("servoMotorActuatorLeft"));
        WanderAroundMain.acc.out(WanderAroundMain.tcId, t, null);
        t =
                new LogicTuple(
                        "createTransducerActuator",
                        new Value(WanderAroundMain.TC_M2.toString()),
                        new Value(
                                "alice.casestudies.wanderaround.TransducerAct"),
                        new Value("tMotorRight"),
                        new Value(
                                "alice.casestudies.wanderaround.NXT_ServoMotorActuator"),
                        new Value("servoMotorActuatorRight"));
        WanderAroundMain.acc.out(WanderAroundMain.tcId, t, null);
        WanderAroundMain.speak("Transducers created");
    }

    private static void speak(final String msg) {
        System.out.println(msg);
    }

    public WanderAroundMain(final String id)
            throws TucsonInvalidAgentIdException {
        super(id);
    }

    @Override
    public void operationCompleted(final ITucsonOperation op) {
        /*
         * 
         */
    }

    @Override
    protected void main() {
        try {
            WanderAroundMain.acc = this.getContext();

            WanderAroundMain.speak("Configuring application environment");

            WanderAroundMain.createTCs();
            WanderAroundMain.createTransducers();

            Thread.sleep(1500);

            WanderAroundMain.createAgents();

            System.out.println("Configuration complete");
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
