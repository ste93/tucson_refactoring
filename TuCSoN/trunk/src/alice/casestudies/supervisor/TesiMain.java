package alice.casestudies.supervisor;

import java.io.IOException;

import alice.casestudies.wanderaround.Utils;
import alice.logictuple.LogicTuple;
import alice.logictuple.Value;
import alice.respect.api.TupleCentreId;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.SynchACC;
import alice.tucson.api.TucsonAgent;
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

public class TesiMain extends TucsonAgent{

	// Sensors tuple centres
	private static TupleCentreId TC_PH; // Potentiometer hardware
	private static TupleCentreId TC_PS; // Potentiometer software
	private	static TupleCentreId TC_Lock; // Lock toggle button
	private	static TupleCentreId TC_Range; // Range control panel
	private static TupleCentreId TC_Connection; // Connection status
	// Actuators tuple centres
	private	static TupleCentreId TC_Light; // Light intensity
	// Utility tuple centres
	private static TupleCentreId TC_Intensity;
	private static TupleCentreId TC_Timer;
	
	// Environment configuration tuple centre
	private static TupleCentreId tcId;
	
	// ACC
	private static SynchACC acc;
	
	public static void main(String[] args){
		try{
			TC_PH = new TupleCentreId( "tc_ph", "localhost", ""+20504);
			TC_PS = new TupleCentreId( "tc_ps", "localhost", ""+20504);
			TC_Lock = new TupleCentreId( "tc_lock", "localhost", ""+20504);
			TC_Range = new TupleCentreId( "tc_range", "localhost", ""+20504);
			TC_Light = new TupleCentreId( "tc_light", "localhost", ""+20504);
			TC_Intensity = new TupleCentreId( "tc_intensity", "localhost", ""+20504);
			TC_Connection = new TupleCentreId( "tc_connection", "localhost", ""+20504);
			TC_Timer = new TupleCentreId( "tc_timer", "localhost", ""+20504);
			
			// Tuple centre for environment configuration
			tcId = new TupleCentreId( "envConfigTC","localhost",""+20504);
			
			// Starting main test
			TesiMain mainTest = new TesiMain("main");
			mainTest.go();
		}catch ( Exception ex ){
			ex.printStackTrace();
		}
	}
	
	public TesiMain( String id ) throws TucsonInvalidAgentIdException{
		super(id);
	}

	@Override
	protected void main() {
		try{
			acc = getContext();
			
			speak("Configuring application environment");
			
			createTCs();
			createTransducers();
			
			Thread.sleep(1500);
			
			createAgents();
			
			System.out.println("Configuration complete");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void createTCs() throws IOException, TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException{
		speak("Creating TCs...");
		// TC Sensors
		speak("Injecting 'table' ReSpecT specification in tc < " + TC_PH.toString() + " >...");
		LogicTuple specTuple = new LogicTuple("spec", new Value(Utils.fileToString("alice/casestudies/supervisor/specTcPot.rsp")) );
		acc.set_s(TC_PH, specTuple, null);
		speak("Injecting 'table' ReSpecT specification in tc < " + TC_PS.toString() + " >...");
		specTuple = new LogicTuple("spec", new Value(Utils.fileToString("alice/casestudies/supervisor/specTcPot.rsp")) );
		acc.set_s(TC_PS, specTuple, null);
		speak("Injecting 'table' ReSpecT specification in tc < " + TC_Lock.toString() + " >...");
		specTuple = new LogicTuple("spec", new Value(Utils.fileToString("alice/casestudies/supervisor/specTcLock.rsp")) );
		acc.set_s(TC_Lock, specTuple, null);
		speak("Injecting 'table' ReSpecT specification in tc < " + TC_Range.toString() + " >...");
		specTuple = new LogicTuple("spec", new Value(Utils.fileToString("alice/casestudies/supervisor/specTcRange.rsp")) );
		acc.set_s(TC_Range, specTuple, null);
		
		// TC Actuators
		speak("Injecting 'table' ReSpecT specification in tc < " + TC_Light.toString() + " >...");
		specTuple = new LogicTuple("spec", new Value(Utils.fileToString("alice/casestudies/supervisor/specTcLight.rsp")) );
		acc.set_s(TC_Light, specTuple, null);
		speak("Injecting 'table' ReSpecT specification in tc < " + TC_Connection.toString() + " >...");
		specTuple = new LogicTuple("spec", new Value(Utils.fileToString("alice/casestudies/supervisor/specTcConnection.rsp")) );
		acc.set_s(TC_Connection, specTuple, null);
		
		// TC Intensity
		speak("Injecting 'table' ReSpecT specification in tc < " + TC_Intensity.toString() + " >...");
		specTuple = new LogicTuple("spec", new Value(Utils.fileToString("alice/casestudies/supervisor/specTcIntensity.rsp")) );
		acc.set_s(TC_Intensity, specTuple, null);
		
		// TC Timer
		speak("Injecting 'table' ReSpecT specification in tc < " + TC_Timer.toString() + " >...");
		specTuple = new LogicTuple("spec", new Value(Utils.fileToString("alice/casestudies/supervisor/specTcTimer.rsp")) );
		acc.set_s(TC_Timer, specTuple, null);
		speak("TCs created");
	}
	
	private void createTransducers() throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException{
		speak("Creating transducers");
		LogicTuple t = new LogicTuple("createTransducerSensor", new Value(TC_PH.toString()), new Value("alice.casestudies.supervisor.TransducerSens"), new Value("tPotHardware"), new Value("alice.casestudies.supervisor.SliderProbe"), new Value("potHardware"));
		acc.out(tcId, t, null);
		t = new LogicTuple("createTransducerSensor", new Value(TC_PS.toString()), new Value("alice.casestudies.supervisor.TransducerSens"), new Value("tPotSoftware"), new Value("alice.casestudies.supervisor.SliderProbeSw"), new Value("potSoftware"));
		acc.out(tcId, t, null);
		t = new LogicTuple("createTransducerSensor", new Value(TC_Lock.toString()), new Value("alice.casestudies.supervisor.TransducerSens"), new Value("tLock"), new Value("alice.casestudies.supervisor.SwitchProbe"), new Value("lockButton"));
		acc.out(tcId, t, null);
		t = new LogicTuple("createTransducerSensor", new Value(TC_Range.toString()), new Value("alice.casestudies.supervisor.TransducerSens"), new Value("tRange"), new Value("alice.casestudies.supervisor.RangeProbe"), new Value("rangeButton"));
		acc.out(tcId, t, null);
		
		t = new LogicTuple("createTransducerActuator", new Value(TC_Light.toString()), new Value("alice.casestudies.supervisor.TransducerAct"), new Value("tLight"), new Value("alice.casestudies.supervisor.LightProbe"), new Value("led"));
		acc.out(tcId, t, null);
		t = new LogicTuple("addActuator", new Value("alice.casestudies.supervisor.LightProbeSw"), new Value("ledSoftware"), new Value("tLight"));
		acc.out(tcId, t, null);
		
		t = new LogicTuple("createTransducerActuator", new Value(TC_Connection.toString()), new Value("alice.casestudies.supervisor.TransducerAct"), new Value("tConn"), new Value("alice.casestudies.supervisor.ConnectionProbe"), new Value("connection"));
		acc.out(tcId, t, null);
		
		t = new LogicTuple("createTransducerActuator", new Value(TC_Timer.toString()), new Value("alice.casestudies.supervisor.TransducerAct"), new Value("tTimer"), new Value("alice.casestudies.supervisor.TimerProbe"), new Value("connectionTimer"));
		acc.out(tcId, t, null);
		speak("Transducers created");
	}
	
	private void createAgents() throws TucsonInvalidAgentIdException{
		speak("Creating agents");
		AG_Intensity ag_intensity = new AG_Intensity("ag_intensity");
		AG_Range ag_range = new AG_Range("ag_range");
		AG_Connection ag_connection = new AG_Connection("ag_connection");
		
		speak("Starting agents");
		ag_intensity.go();
		ag_range.go();
		ag_connection.go();
		speak("Agents created and started");
	}

	@Override
	public void operationCompleted(ITucsonOperation op) {
		// TODO Auto-generated method stub
		
	}
	
	private void speak( String msg ){
		System.out.println(msg);
	}
}
