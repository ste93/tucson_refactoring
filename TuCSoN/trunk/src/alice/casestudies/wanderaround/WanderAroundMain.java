package alice.casestudies.wanderaround;

import java.io.IOException;

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
 * Main per il caso di studio "Wander Around".
 * 
 * @author Steven maraldi
 *
 */

public class WanderAroundMain extends TucsonAgent{

		// Sensors
		private static TupleCentreId TC_U1; // Front ultrasonic sensor
		private static TupleCentreId TC_U2; // Right ultrasonic sensor
		private	static TupleCentreId TC_U3; // Back ultrasonic sensor
		private	static TupleCentreId TC_U4; // Left ultrasonic sensor
		// Actuators
		private	static TupleCentreId TC_M1;	// Left servomotor actuator
		private	static TupleCentreId TC_M2; // Right servomotor actuator
		// Application tuple centres
		private static TupleCentreId TC_Sonar;
		private static TupleCentreId TC_RunAway;
		private static TupleCentreId TC_Motor;
		private static TupleCentreId TC_Avoid;
		
		// Environment configuration tuple centre
		private static TupleCentreId tcId;
		
		// ACC
		private static SynchACC acc;
		
		public static void main(String[] args){
			try{
				TC_U1 = new TupleCentreId( "tc_u1", "localhost", ""+20504);
				TC_U2 = new TupleCentreId( "tc_u2", "localhost", ""+20504);
				TC_U3 = new TupleCentreId( "tc_u3", "localhost", ""+20504);
				TC_U4 = new TupleCentreId( "tc_u4", "localhost", ""+20504);
				TC_M1 = new TupleCentreId( "tc_m1", "localhost", ""+20504);
				TC_M2 = new TupleCentreId( "tc_m2", "localhost", ""+20504);
				TC_Sonar = new TupleCentreId( "tc_sonar", "localhost", ""+20504);
				TC_RunAway = new TupleCentreId( "tc_runaway", "localhost", ""+20504);
				TC_Motor = new TupleCentreId( "tc_motor", "localhost", ""+20504);
				TC_Avoid = new TupleCentreId( "tc_avoid", "localhost", ""+20504);
				tcId = new TupleCentreId( "envConfigTC","localhost",""+20504);
				
				// Starting test
				WanderAroundMain mainTest = new WanderAroundMain("main");
				mainTest.go();
			}catch ( Exception ex ){
				ex.printStackTrace();
			}
		}
	
	public WanderAroundMain( String id ) throws TucsonInvalidAgentIdException{
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
		// Sensor's TCs
		speak("Injecting 'table' ReSpecT specification in tc < " + TC_U1.toString() + " >...");
		LogicTuple specTuple = new LogicTuple("spec", new Value(Utils.fileToString("alice/casestudies/wanderaround/specTcSensore.rsp")) );
		acc.set_s(TC_U1, specTuple, null);
		speak("Injecting 'table' ReSpecT specification in tc < " + TC_U2.toString() + " >...");
		specTuple = new LogicTuple("spec", new Value(Utils.fileToString("alice/casestudies/wanderaround/specTcSensore.rsp")) );
		acc.set_s(TC_U2, specTuple, null);
		speak("Injecting 'table' ReSpecT specification in tc < " + TC_U3.toString() + " >...");
		specTuple = new LogicTuple("spec", new Value(Utils.fileToString("alice/casestudies/wanderaround/specTcSensore.rsp")) );
		acc.set_s(TC_U3, specTuple, null);
		speak("Injecting 'table' ReSpecT specification in tc < " + TC_U4.toString() + " >...");
		specTuple = new LogicTuple("spec", new Value(Utils.fileToString("alice/casestudies/wanderaround/specTcSensore.rsp")) );
		acc.set_s(TC_U4, specTuple, null);
		
		// Actuator's TCs
		speak("Injecting 'table' ReSpecT specification in tc < " + TC_M1.toString() + " >...");
		specTuple = new LogicTuple("spec", new Value(Utils.fileToString("alice/casestudies/wanderaround/specTcAttuatoreLeft.rsp")) );
		acc.set_s(TC_M1, specTuple, null);
		speak("Injecting 'table' ReSpecT specification in tc < " + TC_M2.toString() + " >...");
		specTuple = new LogicTuple("spec", new Value(Utils.fileToString("alice/casestudies/wanderaround/specTcAttuatoreRight.rsp")) );
		acc.set_s(TC_M2, specTuple, null);
		
		// TC Sonar
		speak("Injecting 'table' ReSpecT specification in tc < " + TC_Sonar.toString() + " >...");
		specTuple = new LogicTuple("spec", new Value(Utils.fileToString("alice/casestudies/wanderaround/specTcSonar.rsp")) );
		acc.set_s(TC_Sonar, specTuple, null);
		
		// TC Run Away
		speak("Injecting 'table' ReSpecT specification in tc < " + TC_RunAway.toString() + " >...");
		specTuple = new LogicTuple("spec", new Value(Utils.fileToString("alice/casestudies/wanderaround/specTcRunAway.rsp")) );
		acc.set_s(TC_RunAway, specTuple, null);
		
		// TC Motor
		speak("Injecting 'table' ReSpecT specification in tc < " + TC_Motor.toString() + " >...");
		specTuple = new LogicTuple("spec", new Value(Utils.fileToString("alice/casestudies/wanderaround/specTcMotor.rsp")) );
		acc.set_s(TC_Motor, specTuple, null);
				
		// TC Avoid
		speak("Injecting 'table' ReSpecT specification in tc < " + TC_Avoid.toString() + " >...");
		specTuple = new LogicTuple("spec", new Value(Utils.fileToString("alice/casestudies/wanderaround/specTcAvoid.rsp")) );
		acc.set_s(TC_Avoid, specTuple, null);
		speak("TCs created");
	}
	
	private void createTransducers() throws TucsonOperationNotPossibleException, UnreachableNodeException, OperationTimeOutException{
		speak("Creating transducers");
		LogicTuple t = new LogicTuple("createTransducerSensor", new Value(TC_U1.toString()), new Value("alice.casestudies.wanderaround.TransducerSens"), new Value("tSensorFront"), new Value("alice.casestudies.wanderaround.NXT_UltrasonicSensor"), new Value("ultrasonicSensorFront"));
		acc.out(tcId, t, null);
		t = new LogicTuple("createTransducerSensor", new Value(TC_U2.toString()), new Value("alice.casestudies.wanderaround.TransducerSens"), new Value("tSensorRight"), new Value("alice.casestudies.wanderaround.NXT_UltrasonicSensor"), new Value("ultrasonicSensorRight"));
		acc.out(tcId, t, null);
		t = new LogicTuple("createTransducerSensor", new Value(TC_U3.toString()), new Value("alice.casestudies.wanderaround.TransducerSens"), new Value("tSensorBack"), new Value("alice.casestudies.wanderaround.NXT_UltrasonicSensor"), new Value("ultrasonicSensorBack"));
		acc.out(tcId, t, null);
		t = new LogicTuple("createTransducerSensor", new Value(TC_U4.toString()), new Value("alice.casestudies.wanderaround.TransducerSens"), new Value("tSensorLeft"), new Value("alice.casestudies.wanderaround.NXT_UltrasonicSensor"), new Value("ultrasonicSensorLeft"));
		acc.out(tcId, t, null);
		
		t = new LogicTuple("createTransducerActuator", new Value(TC_M1.toString()), new Value("alice.casestudies.wanderaround.TransducerAct"), new Value("tMotorLeft"), new Value("alice.casestudies.wanderaround.NXT_ServoMotorActuator"), new Value("servoMotorActuatorLeft"));
		acc.out(tcId, t, null);
		t = new LogicTuple("createTransducerActuator", new Value(TC_M2.toString()), new Value("alice.casestudies.wanderaround.TransducerAct"), new Value("tMotorRight"), new Value("alice.casestudies.wanderaround.NXT_ServoMotorActuator"), new Value("servoMotorActuatorRight"));
		acc.out(tcId, t, null);
		speak("Transducers created");
	}
	
	private void createAgents() throws TucsonInvalidAgentIdException{
		speak("Creating agents");
		AG_FeelForce ag_feelforce = new AG_FeelForce("ag_feelforce");
		AG_Collide ag_collide = new AG_Collide("ag_collide");
		AG_Wander ag_wander = new AG_Wander("ag_wander");
		
		speak("Starting agents");
		ag_collide.go();
		ag_feelforce.go();
		ag_wander.go();
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
