package alice.tucson.examples.situatedness;

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
    
    private static final String DEFAULT_PORT = "20504";

    // ACC
    private static SynchACC acc;
    private static TupleCentreId tcAvoid;
    // Environment configuration tuple centre
    private static TupleCentreId tcId;
    // Actuators
    private static TupleCentreId tcM1; // Left servomotor actuator
    private static TupleCentreId tcM2; // Right servomotor actuator
    private static TupleCentreId tcMotor;
    private static TupleCentreId tcRunAway;
    // Application tuple centres
    private static TupleCentreId tcSonar;
    // Sensors
    private static TupleCentreId tcU1; // Front ultrasonic sensor
    private static TupleCentreId tcU2; // Right ultrasonic sensor

    private static TupleCentreId tcU3; // Back ultrasonic sensor

    private static TupleCentreId tcU4; // Left ultrasonic sensor

    public static void main(final String[] args) {
        try {
            WanderAroundMain.tcU1 =
                    new TupleCentreId("tc_u1", "localhost",
                            DEFAULT_PORT);
            WanderAroundMain.tcU2 =
                    new TupleCentreId("tc_u2", "localhost",
                            DEFAULT_PORT);
            WanderAroundMain.tcU3 =
                    new TupleCentreId("tc_u3", "localhost",
                            DEFAULT_PORT);
            WanderAroundMain.tcU4 =
                    new TupleCentreId("tc_u4", "localhost",
                            DEFAULT_PORT);
            WanderAroundMain.tcM1 =
                    new TupleCentreId("tc_m1", "localhost",
                            DEFAULT_PORT);
            WanderAroundMain.tcM2 =
                    new TupleCentreId("tc_m2", "localhost",
                            DEFAULT_PORT);
            WanderAroundMain.tcSonar =
                    new TupleCentreId("tc_sonar", "localhost",
                            DEFAULT_PORT);
            WanderAroundMain.tcRunAway =
                    new TupleCentreId("tc_runaway", "localhost",
                            DEFAULT_PORT);
            WanderAroundMain.tcMotor =
                    new TupleCentreId("tc_motor", "localhost",
                            DEFAULT_PORT);
            WanderAroundMain.tcAvoid =
                    new TupleCentreId("tc_avoid", "localhost",
                            DEFAULT_PORT);
            WanderAroundMain.tcId =
                    new TupleCentreId("envConfigTC", "localhost",
                            DEFAULT_PORT);

            // Starting test
            final WanderAroundMain mainTest = new WanderAroundMain("main");
            mainTest.go();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private static void createAgents() throws TucsonInvalidAgentIdException {
        WanderAroundMain.speak("Creating agents");
        final AgentFeelForce agFeelforce = new AgentFeelForce("ag_feelforce");
        final AgentCollide agCollide = new AgentCollide("ag_collide");
        final AgentWander agWander = new AgentWander("ag_wander");

        WanderAroundMain.speak("Starting agents");
        agCollide.go();
        agFeelforce.go();
        agWander.go();
        WanderAroundMain.speak("Agents created and started");
    }

    private static void createTCs() throws IOException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        WanderAroundMain.speak("Creating TCs...");
        // Sensor's TCs
        WanderAroundMain
                .speak("Injecting 'sensor' ReSpecT specification in tc < "
                        + WanderAroundMain.tcU1.toString() + " >...");
        LogicTuple specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/tucson/examples/situatedness/specTcSensore.rsp")));
        WanderAroundMain.acc.setS(WanderAroundMain.tcU1, specTuple, null);
        WanderAroundMain
                .speak("Injecting 'sensor' ReSpecT specification in tc < "
                        + WanderAroundMain.tcU2.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/tucson/examples/situatedness/specTcSensore.rsp")));
        WanderAroundMain.acc.setS(WanderAroundMain.tcU2, specTuple, null);
        WanderAroundMain
                .speak("Injecting 'sensor' ReSpecT specification in tc < "
                        + WanderAroundMain.tcU3.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/tucson/examples/situatedness/specTcSensore.rsp")));
        WanderAroundMain.acc.setS(WanderAroundMain.tcU3, specTuple, null);
        WanderAroundMain
                .speak("Injecting 'sensor' ReSpecT specification in tc < "
                        + WanderAroundMain.tcU4.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/tucson/examples/situatedness/specTcSensore.rsp")));
        WanderAroundMain.acc.setS(WanderAroundMain.tcU4, specTuple, null);

        // Actuator's TCs
        WanderAroundMain
                .speak("Injecting 'left actuator' ReSpecT specification in tc < "
                        + WanderAroundMain.tcM1.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/tucson/examples/situatedness/specTcAttuatoreLeft.rsp")));
        WanderAroundMain.acc.setS(WanderAroundMain.tcM1, specTuple, null);
        WanderAroundMain
                .speak("Injecting 'right actuator' ReSpecT specification in tc < "
                        + WanderAroundMain.tcM2.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/tucson/examples/situatedness/specTcAttuatoreRight.rsp")));
        WanderAroundMain.acc.setS(WanderAroundMain.tcM2, specTuple, null);

        // TC Sonar
        WanderAroundMain
                .speak("Injecting 'sonar' ReSpecT specification in tc < "
                        + WanderAroundMain.tcSonar.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/tucson/examples/situatedness/specTcSonar.rsp")));
        WanderAroundMain.acc.setS(WanderAroundMain.tcSonar, specTuple, null);

        // TC Run Away
        WanderAroundMain
                .speak("Injecting 'run away' ReSpecT specification in tc < "
                        + WanderAroundMain.tcRunAway.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/tucson/examples/situatedness/specTcRunAway.rsp")));
        WanderAroundMain.acc.setS(WanderAroundMain.tcRunAway, specTuple, null);

        // TC Motor
        WanderAroundMain
                .speak("Injecting 'motion' ReSpecT specification in tc < "
                        + WanderAroundMain.tcMotor.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/tucson/examples/situatedness/specTcMotor.rsp")));
        WanderAroundMain.acc.setS(WanderAroundMain.tcMotor, specTuple, null);

        // TC Avoid
        WanderAroundMain
                .speak("Injecting 'avoidance' ReSpecT specification in tc < "
                        + WanderAroundMain.tcAvoid.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/tucson/examples/situatedness/specTcAvoid.rsp")));
        WanderAroundMain.acc.setS(WanderAroundMain.tcAvoid, specTuple, null);
        WanderAroundMain.speak("TCs created");
    }

    private static void createTransducers()
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        WanderAroundMain.speak("Creating transducers");
        LogicTuple t =
                new LogicTuple(
                        "createTransducerSensor",
                        new Value(WanderAroundMain.tcU1.toString()),
                        new Value(
                                "alice.tucson.examples.situatedness.TransducerSens"),
                        new Value("tSensorFront"),
                        new Value(
                                "alice.tucson.examples.situatedness.NXTUltrasonicSensor"),
                        new Value("ultrasonicSensorFront"));
        WanderAroundMain.acc.out(WanderAroundMain.tcId, t, null);
        t =
                new LogicTuple(
                        "createTransducerSensor",
                        new Value(WanderAroundMain.tcU2.toString()),
                        new Value(
                                "alice.tucson.examples.situatedness.TransducerSens"),
                        new Value("tSensorRight"),
                        new Value(
                                "alice.tucson.examples.situatedness.NXTUltrasonicSensor"),
                        new Value("ultrasonicSensorRight"));
        WanderAroundMain.acc.out(WanderAroundMain.tcId, t, null);
        t =
                new LogicTuple(
                        "createTransducerSensor",
                        new Value(WanderAroundMain.tcU3.toString()),
                        new Value(
                                "alice.tucson.examples.situatedness.TransducerSens"),
                        new Value("tSensorBack"),
                        new Value(
                                "alice.tucson.examples.situatedness.NXTUltrasonicSensor"),
                        new Value("ultrasonicSensorBack"));
        WanderAroundMain.acc.out(WanderAroundMain.tcId, t, null);
        t =
                new LogicTuple(
                        "createTransducerSensor",
                        new Value(WanderAroundMain.tcU4.toString()),
                        new Value(
                                "alice.tucson.examples.situatedness.TransducerSens"),
                        new Value("tSensorLeft"),
                        new Value(
                                "alice.tucson.examples.situatedness.NXTUltrasonicSensor"),
                        new Value("ultrasonicSensorLeft"));
        WanderAroundMain.acc.out(WanderAroundMain.tcId, t, null);

        t =
                new LogicTuple(
                        "createTransducerActuator",
                        new Value(WanderAroundMain.tcM1.toString()),
                        new Value(
                                "alice.tucson.examples.situatedness.TransducerAct"),
                        new Value("tMotorLeft"),
                        new Value(
                                "alice.tucson.examples.situatedness.NXTServoMotorActuator"),
                        new Value("servoMotorActuatorLeft"));
        WanderAroundMain.acc.out(WanderAroundMain.tcId, t, null);
        t =
                new LogicTuple(
                        "createTransducerActuator",
                        new Value(WanderAroundMain.tcM2.toString()),
                        new Value(
                                "alice.tucson.examples.situatedness.TransducerAct"),
                        new Value("tMotorRight"),
                        new Value(
                                "alice.tucson.examples.situatedness.NXTServoMotorActuator"),
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
