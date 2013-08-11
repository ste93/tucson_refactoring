package alice.casestudies.supervisor;

import java.io.IOException;

import alice.casestudies.wanderaround.Utils;
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
 * Main of the "LIGHT SUPERVISOR" Case study
 * 
 * @author Steven Maraldi
 * 
 */

public class TesiMain extends AbstractTucsonAgent {
    
    private static final String DEFAULT_PORT = "20504";

    // ACC
    private static SynchACC acc;
    private static TupleCentreId tcConnect; // Connection status
    // Environment configuration tuple centre
    private static TupleCentreId tcId;
    // Utility tuple centres
    private static TupleCentreId tcIntensity;
    // Actuators tuple centres
    private static TupleCentreId tcLight; // Light intensity
    private static TupleCentreId tcLock; // Lock toggle button
    // Sensors tuple centres
    private static TupleCentreId tcPh; // Potentiometer hardware
    private static TupleCentreId tcPs; // Potentiometer software

    private static TupleCentreId tcRange; // Range control panel

    private static TupleCentreId tcTimer;

    public static void main(final String[] args) {
        try {
            TesiMain.tcPh =
                    new TupleCentreId("tc_ph", "localhost",
                            DEFAULT_PORT);
            TesiMain.tcPs =
                    new TupleCentreId("tc_ps", "localhost",
                            DEFAULT_PORT);
            TesiMain.tcLock =
                    new TupleCentreId("tc_lock", "localhost",
                            DEFAULT_PORT);
            TesiMain.tcRange =
                    new TupleCentreId("tc_range", "localhost",
                            DEFAULT_PORT);
            TesiMain.tcLight =
                    new TupleCentreId("tc_light", "localhost",
                            DEFAULT_PORT);
            TesiMain.tcIntensity =
                    new TupleCentreId("tc_intensity", "localhost",
                            DEFAULT_PORT);
            TesiMain.tcConnect =
                    new TupleCentreId("tc_connection", "localhost",
                            DEFAULT_PORT);
            TesiMain.tcTimer =
                    new TupleCentreId("tc_timer", "localhost",
                            DEFAULT_PORT);

            // Tuple centre for environment configuration
            TesiMain.tcId =
                    new TupleCentreId("envConfigTC", "localhost",
                            DEFAULT_PORT);

            // Starting main test
            final TesiMain mainTest = new TesiMain("main");
            mainTest.go();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private static void createAgents() throws TucsonInvalidAgentIdException {
        TesiMain.speak("Creating agents");
        final AgentIntensity agIntensity = new AgentIntensity("ag_intensity");
        final AgentRange agRange = new AgentRange("ag_range");
        final AgentConnection agConnection = new AgentConnection("ag_connection");

        TesiMain.speak("Starting agents");
        agIntensity.go();
        agRange.go();
        agConnection.go();
        TesiMain.speak("Agents created and started");
    }

    private static void createTCs() throws IOException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        TesiMain.speak("Creating TCs...");
        // TC Sensors
        TesiMain.speak("Injecting 'table' ReSpecT specification in tc < "
                + TesiMain.tcPh.toString() + " >...");
        LogicTuple specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/casestudies/supervisor/specTcPot.rsp")));
        TesiMain.acc.setS(TesiMain.tcPh, specTuple, null);
        TesiMain.speak("Injecting 'table' ReSpecT specification in tc < "
                + TesiMain.tcPs.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/casestudies/supervisor/specTcPot.rsp")));
        TesiMain.acc.setS(TesiMain.tcPs, specTuple, null);
        TesiMain.speak("Injecting 'table' ReSpecT specification in tc < "
                + TesiMain.tcLock.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/casestudies/supervisor/specTcLock.rsp")));
        TesiMain.acc.setS(TesiMain.tcLock, specTuple, null);
        TesiMain.speak("Injecting 'table' ReSpecT specification in tc < "
                + TesiMain.tcRange.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/casestudies/supervisor/specTcRange.rsp")));
        TesiMain.acc.setS(TesiMain.tcRange, specTuple, null);

        // TC Actuators
        TesiMain.speak("Injecting 'table' ReSpecT specification in tc < "
                + TesiMain.tcLight.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/casestudies/supervisor/specTcLight.rsp")));
        TesiMain.acc.setS(TesiMain.tcLight, specTuple, null);
        TesiMain.speak("Injecting 'table' ReSpecT specification in tc < "
                + TesiMain.tcConnect.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/casestudies/supervisor/specTcConnection.rsp")));
        TesiMain.acc.setS(TesiMain.tcConnect, specTuple, null);

        // TC Intensity
        TesiMain.speak("Injecting 'table' ReSpecT specification in tc < "
                + TesiMain.tcIntensity.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/casestudies/supervisor/specTcIntensity.rsp")));
        TesiMain.acc.setS(TesiMain.tcIntensity, specTuple, null);

        // TC Timer
        TesiMain.speak("Injecting 'table' ReSpecT specification in tc < "
                + TesiMain.tcTimer.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/casestudies/supervisor/specTcTimer.rsp")));
        TesiMain.acc.setS(TesiMain.tcTimer, specTuple, null);
        TesiMain.speak("TCs created");
    }

    private static void createTransducers()
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        TesiMain.speak("Creating transducers");
        LogicTuple t =
                new LogicTuple("createTransducerSensor", new Value(
                        TesiMain.tcPh.toString()), new Value(
                        "alice.casestudies.supervisor.TransducerSens"),
                        new Value("tPotHardware"), new Value(
                                "alice.casestudies.supervisor.SliderProbe"),
                        new Value("potHardware"));
        TesiMain.acc.out(TesiMain.tcId, t, null);
        t =
                new LogicTuple("createTransducerSensor", new Value(
                        TesiMain.tcPs.toString()), new Value(
                        "alice.casestudies.supervisor.TransducerSens"),
                        new Value("tPotSoftware"), new Value(
                                "alice.casestudies.supervisor.SliderProbeSw"),
                        new Value("potSoftware"));
        TesiMain.acc.out(TesiMain.tcId, t, null);
        t =
                new LogicTuple("createTransducerSensor", new Value(
                        TesiMain.tcLock.toString()), new Value(
                        "alice.casestudies.supervisor.TransducerSens"),
                        new Value("tLock"), new Value(
                                "alice.casestudies.supervisor.SwitchProbe"),
                        new Value("lockButton"));
        TesiMain.acc.out(TesiMain.tcId, t, null);
        t =
                new LogicTuple("createTransducerSensor", new Value(
                        TesiMain.tcRange.toString()), new Value(
                        "alice.casestudies.supervisor.TransducerSens"),
                        new Value("tRange"), new Value(
                                "alice.casestudies.supervisor.RangeProbe"),
                        new Value("rangeButton"));
        TesiMain.acc.out(TesiMain.tcId, t, null);

        t =
                new LogicTuple("createTransducerActuator", new Value(
                        TesiMain.tcLight.toString()), new Value(
                        "alice.casestudies.supervisor.TransducerAct"),
                        new Value("tLight"), new Value(
                                "alice.casestudies.supervisor.LightProbe"),
                        new Value("led"));
        TesiMain.acc.out(TesiMain.tcId, t, null);
        t =
                new LogicTuple("addActuator", new Value(
                        "alice.casestudies.supervisor.LightProbeSw"),
                        new Value("ledSoftware"), new Value("tLight"));
        TesiMain.acc.out(TesiMain.tcId, t, null);

        t =
                new LogicTuple(
                        "createTransducerActuator",
                        new Value(TesiMain.tcConnect.toString()),
                        new Value("alice.casestudies.supervisor.TransducerAct"),
                        new Value("tConn"),
                        new Value(
                                "alice.casestudies.supervisor.ConnectionProbe"),
                        new Value("connection"));
        TesiMain.acc.out(TesiMain.tcId, t, null);

        t =
                new LogicTuple("createTransducerActuator", new Value(
                        TesiMain.tcTimer.toString()), new Value(
                        "alice.casestudies.supervisor.TransducerAct"),
                        new Value("tTimer"), new Value(
                                "alice.casestudies.supervisor.TimerProbe"),
                        new Value("connectionTimer"));
        TesiMain.acc.out(TesiMain.tcId, t, null);
        TesiMain.speak("Transducers created");
    }

    private static void speak(final String msg) {
        System.out.println(msg);
    }

    public TesiMain(final String id) throws TucsonInvalidAgentIdException {
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
            TesiMain.acc = this.getContext();

            TesiMain.speak("Configuring application environment");

            TesiMain.createTCs();
            TesiMain.createTransducers();

            Thread.sleep(1500);

            TesiMain.createAgents();

            System.out.println("Configuration complete");
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
