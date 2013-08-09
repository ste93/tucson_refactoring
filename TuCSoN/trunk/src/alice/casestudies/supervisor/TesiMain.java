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

    // ACC
    private static SynchACC acc;
    private static TupleCentreId TC_Connection; // Connection status
    // Utility tuple centres
    private static TupleCentreId TC_Intensity;
    // Actuators tuple centres
    private static TupleCentreId TC_Light; // Light intensity
    private static TupleCentreId TC_Lock; // Lock toggle button
    // Sensors tuple centres
    private static TupleCentreId TC_PH; // Potentiometer hardware
    private static TupleCentreId TC_PS; // Potentiometer software
    private static TupleCentreId TC_Range; // Range control panel

    private static TupleCentreId TC_Timer;

    // Environment configuration tuple centre
    private static TupleCentreId tcId;

    public static void main(final String[] args) {
        try {
            TesiMain.TC_PH =
                    new TupleCentreId("tc_ph", "localhost", "" + 20504);
            TesiMain.TC_PS =
                    new TupleCentreId("tc_ps", "localhost", "" + 20504);
            TesiMain.TC_Lock =
                    new TupleCentreId("tc_lock", "localhost", "" + 20504);
            TesiMain.TC_Range =
                    new TupleCentreId("tc_range", "localhost", "" + 20504);
            TesiMain.TC_Light =
                    new TupleCentreId("tc_light", "localhost", "" + 20504);
            TesiMain.TC_Intensity =
                    new TupleCentreId("tc_intensity", "localhost", "" + 20504);
            TesiMain.TC_Connection =
                    new TupleCentreId("tc_connection", "localhost", "" + 20504);
            TesiMain.TC_Timer =
                    new TupleCentreId("tc_timer", "localhost", "" + 20504);

            // Tuple centre for environment configuration
            TesiMain.tcId =
                    new TupleCentreId("envConfigTC", "localhost", "" + 20504);

            // Starting main test
            final TesiMain mainTest = new TesiMain("main");
            mainTest.go();
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void createAgents() throws TucsonInvalidAgentIdException {
        TesiMain.speak("Creating agents");
        final AG_Intensity ag_intensity = new AG_Intensity("ag_intensity");
        final AG_Range ag_range = new AG_Range("ag_range");
        final AG_Connection ag_connection = new AG_Connection("ag_connection");

        TesiMain.speak("Starting agents");
        ag_intensity.go();
        ag_range.go();
        ag_connection.go();
        TesiMain.speak("Agents created and started");
    }

    private static void createTCs() throws IOException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        TesiMain.speak("Creating TCs...");
        // TC Sensors
        TesiMain.speak("Injecting 'table' ReSpecT specification in tc < "
                + TesiMain.TC_PH.toString() + " >...");
        LogicTuple specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/casestudies/supervisor/specTcPot.rsp")));
        TesiMain.acc.setS(TesiMain.TC_PH, specTuple, null);
        TesiMain.speak("Injecting 'table' ReSpecT specification in tc < "
                + TesiMain.TC_PS.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/casestudies/supervisor/specTcPot.rsp")));
        TesiMain.acc.setS(TesiMain.TC_PS, specTuple, null);
        TesiMain.speak("Injecting 'table' ReSpecT specification in tc < "
                + TesiMain.TC_Lock.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/casestudies/supervisor/specTcLock.rsp")));
        TesiMain.acc.setS(TesiMain.TC_Lock, specTuple, null);
        TesiMain.speak("Injecting 'table' ReSpecT specification in tc < "
                + TesiMain.TC_Range.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/casestudies/supervisor/specTcRange.rsp")));
        TesiMain.acc.setS(TesiMain.TC_Range, specTuple, null);

        // TC Actuators
        TesiMain.speak("Injecting 'table' ReSpecT specification in tc < "
                + TesiMain.TC_Light.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/casestudies/supervisor/specTcLight.rsp")));
        TesiMain.acc.setS(TesiMain.TC_Light, specTuple, null);
        TesiMain.speak("Injecting 'table' ReSpecT specification in tc < "
                + TesiMain.TC_Connection.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/casestudies/supervisor/specTcConnection.rsp")));
        TesiMain.acc.setS(TesiMain.TC_Connection, specTuple, null);

        // TC Intensity
        TesiMain.speak("Injecting 'table' ReSpecT specification in tc < "
                + TesiMain.TC_Intensity.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/casestudies/supervisor/specTcIntensity.rsp")));
        TesiMain.acc.setS(TesiMain.TC_Intensity, specTuple, null);

        // TC Timer
        TesiMain.speak("Injecting 'table' ReSpecT specification in tc < "
                + TesiMain.TC_Timer.toString() + " >...");
        specTuple =
                new LogicTuple(
                        "spec",
                        new Value(
                                Utils.fileToString("alice/casestudies/supervisor/specTcTimer.rsp")));
        TesiMain.acc.setS(TesiMain.TC_Timer, specTuple, null);
        TesiMain.speak("TCs created");
    }

    private static void createTransducers()
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        TesiMain.speak("Creating transducers");
        LogicTuple t =
                new LogicTuple("createTransducerSensor", new Value(
                        TesiMain.TC_PH.toString()), new Value(
                        "alice.casestudies.supervisor.TransducerSens"),
                        new Value("tPotHardware"), new Value(
                                "alice.casestudies.supervisor.SliderProbe"),
                        new Value("potHardware"));
        TesiMain.acc.out(TesiMain.tcId, t, null);
        t =
                new LogicTuple("createTransducerSensor", new Value(
                        TesiMain.TC_PS.toString()), new Value(
                        "alice.casestudies.supervisor.TransducerSens"),
                        new Value("tPotSoftware"), new Value(
                                "alice.casestudies.supervisor.SliderProbeSw"),
                        new Value("potSoftware"));
        TesiMain.acc.out(TesiMain.tcId, t, null);
        t =
                new LogicTuple("createTransducerSensor", new Value(
                        TesiMain.TC_Lock.toString()), new Value(
                        "alice.casestudies.supervisor.TransducerSens"),
                        new Value("tLock"), new Value(
                                "alice.casestudies.supervisor.SwitchProbe"),
                        new Value("lockButton"));
        TesiMain.acc.out(TesiMain.tcId, t, null);
        t =
                new LogicTuple("createTransducerSensor", new Value(
                        TesiMain.TC_Range.toString()), new Value(
                        "alice.casestudies.supervisor.TransducerSens"),
                        new Value("tRange"), new Value(
                                "alice.casestudies.supervisor.RangeProbe"),
                        new Value("rangeButton"));
        TesiMain.acc.out(TesiMain.tcId, t, null);

        t =
                new LogicTuple("createTransducerActuator", new Value(
                        TesiMain.TC_Light.toString()), new Value(
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
                        new Value(TesiMain.TC_Connection.toString()),
                        new Value("alice.casestudies.supervisor.TransducerAct"),
                        new Value("tConn"),
                        new Value(
                                "alice.casestudies.supervisor.ConnectionProbe"),
                        new Value("connection"));
        TesiMain.acc.out(TesiMain.tcId, t, null);

        t =
                new LogicTuple("createTransducerActuator", new Value(
                        TesiMain.TC_Timer.toString()), new Value(
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
