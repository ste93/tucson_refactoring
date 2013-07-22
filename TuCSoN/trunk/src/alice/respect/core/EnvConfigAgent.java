package alice.respect.core;

import alice.logictuple.LogicTuple;
import alice.respect.api.TupleCentreId;
import alice.respect.api.exceptions.InvalidTupleCentreIdException;
import alice.respect.probe.ActuatorId;
import alice.respect.probe.ISimpleProbe;
import alice.respect.probe.ProbeId;
import alice.respect.probe.SensorId;
import alice.respect.transducer.TransducerId;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.SynchACC;
import alice.tucson.api.TucsonAgent;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;

/**
 * 
 * Environment configuration agent. 
 * 
 * It checks for requests on tcEnvConfig and delegates them to the TransducerManager.
 * 
 * @author Steven Maraldi
 *
 */

public class EnvConfigAgent extends TucsonAgent{
	
	/** The tuple centre used for environment configuration **/
	private TupleCentreId idEnvTC;
	private boolean iteraction = true;
	
	/** Create transducer sensor request's type **/
	private final String CREATE_TRANSDUCER_SENSOR = "createTransducerSensor";
	/** Create transducer actuator request's type **/
	private final String CREATE_TRANSDUCER_ACTUATOR = "createTransducerActuator";
	/** Add a sensor request's type **/
	private final String ADD_SENSOR = "addSensor";
	/** Add an actuator request's type **/
	private final String ADD_ACTUATOR = "addActuator";
	/** Remove a resource request's type **/
	private final String REMOVE_RESOURCE = "removeResource";
	/** Change the transducer associated to a resource request's type **/
	private final String CHANGE_TRANSDUCER = "changeTransducer";
	
	public EnvConfigAgent( String ipAddress, int portno) throws TucsonInvalidAgentIdException{
		super( "envConfigAgent", ipAddress, portno );
		try {
			idEnvTC = new TupleCentreId( "envConfigTC", ipAddress, ""+portno );
		} catch (InvalidTupleCentreIdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.go();
	}

	@Override
	public void main() {
		SynchACC acc = getContext();
		
		while( iteraction ){
			try{
				// Gets the command from the tuple space
				LogicTuple t = LogicTuple.parse("cmd(Type)");
				t = acc.in(idEnvTC, t, null).getLogicTupleResult();
				
				switch( t.getArg(0).toString() ){
					case CREATE_TRANSDUCER_SENSOR:{
						t = LogicTuple.parse("createTransducerSensor(Tcid,Tclass,Tid,Pclass,Pid)");
						t = acc.in(idEnvTC, t, null).getLogicTupleResult();
						// Obtaining transducer
						TransducerId tId = new TransducerId( t.getArg(2).getName() );
						// Obtaining tuple centre and its properties
						String[] sTcId = t.getArg(0).toString().split(":"); // '@'(name,':'(node,port))
						String tcName = sTcId[0].substring(sTcId[0].indexOf('(')+1, sTcId[0].indexOf(','));
						String[] tcNodeAndPort = sTcId[1].substring(sTcId[1].indexOf('(')+1, sTcId[1].indexOf(')')).split(",");
						TupleCentreId tcId = new TupleCentreId( tcName,tcNodeAndPort[0],tcNodeAndPort[1] );
						speak("Serving create transducer request. TransducerId:"+tId+" TC Associated:"+t.getArg(0).toString());
						// Obtaining resource
						ProbeId pId = new SensorId( t.getArg(4).getName() );
						ResourceManager.getResourceManager().createResource( t.getArg(3).toString(), pId );
						// Building transducer
						TransducerManager.getTransducerManager().createTransducer( t.getArg(1).toString(), tId, tcId, pId );
						break;
					}case CREATE_TRANSDUCER_ACTUATOR:{
						t = LogicTuple.parse("createTransducerActuator(Tcid,Tclass,Tid,Pclass,Pid)");
						t = acc.in(idEnvTC, t, null).getLogicTupleResult();
						// Obtaining transducer
						TransducerId tId = new TransducerId( t.getArg(2).getName() );
						// Obtaining tuple centre and its properties
						String[] sTcId = t.getArg(0).toString().split(":"); // '@'(name,':'(node,port))
						String tcName = sTcId[0].substring(sTcId[0].indexOf('(')+1, sTcId[0].indexOf(','));
						String[] tcNodeAndPort = sTcId[1].substring(sTcId[1].indexOf('(')+1, sTcId[1].indexOf(')')).split(",");
						TupleCentreId tcId = new TupleCentreId( tcName,tcNodeAndPort[0],tcNodeAndPort[1] );
						speak("Serving create transducer request. TransducerId:"+tId+" TC Associated:"+t.getArg(0).toString());
						// Obtaining resource
						ProbeId pId = new ActuatorId( t.getArg(4).getName() );
						ResourceManager.getResourceManager().createResource( t.getArg(3).toString(), pId );
						// Building transducer
						TransducerManager.getTransducerManager().createTransducer( t.getArg(1).toString(), tId, tcId, pId );
						break;
					}case ADD_SENSOR:{
						speak("Serving add sensor request");
						t = LogicTuple.parse("addSensor(Class,Pid,Tid)");
						t = acc.in(idEnvTC, t, null).getLogicTupleResult();
						// Creating resource
						ProbeId pId = new SensorId( t.getArg(1).getName() );
						ResourceManager.getResourceManager().createResource( t.getArg(0).toString(), pId );
						ISimpleProbe probe = ResourceManager.getResourceManager().getResource( pId );
						TransducerId tId = TransducerManager.getTransducerManager().getTransducer( t.getArg(2).getName() ).getIdentifier();
						TransducerManager.getTransducerManager().addResource( probe.getIdentifier(), tId, probe);
						break;
					}case ADD_ACTUATOR:{
						speak("Serving add actuator request");
						t = LogicTuple.parse("addActuator(Class,Pid,Tid)");
						t = acc.in(idEnvTC, t, null).getLogicTupleResult();
						// Creating resource
						ProbeId pId = new ActuatorId( t.getArg(1).getName() );
						ResourceManager.getResourceManager().createResource( t.getArg(0).toString(), pId );
						ISimpleProbe probe = ResourceManager.getResourceManager().getResource( pId );
						TransducerId tId = TransducerManager.getTransducerManager().getTransducer( t.getArg(2).getName() ).getIdentifier();
						TransducerManager.getTransducerManager().addResource( probe.getIdentifier(), tId, probe);
						break;
					}case REMOVE_RESOURCE:{
						speak("Serving remove resource request");
						t = LogicTuple.parse("removeResource(Pid)");
						t = acc.in(idEnvTC, t, null).getLogicTupleResult();
						ISimpleProbe probe = ResourceManager.getResourceManager().getResourceByName( t.getArg(0).getName() );
						ResourceManager.getResourceManager().removeResource( probe.getIdentifier() );
						break;
					}case CHANGE_TRANSDUCER:{
						speak("Serving change transducer request");
						t = LogicTuple.parse("changeTransducer(Pid,Tid)");
						t = acc.in(idEnvTC, t, null).getLogicTupleResult();
						ProbeId pId = ResourceManager.getResourceManager().getResourceByName( t.getArg(0).getName() ).getIdentifier();
						TransducerId tId = TransducerManager.getTransducerManager().getTransducer( t.getArg(1).getName() ).getIdentifier();
						ResourceManager.getResourceManager().setTransducer( pId, tId );
						break;
					}
				}
			}catch( Exception ex ){
				ex.printStackTrace();
			}
		}
	}
	
	public void stopIteraction(){
		this.iteraction = false;
	}
	
	private void speak(Object msg){
		System.out.println("[EnvConfigAgent] "+msg);
	}

	@Override
	public void operationCompleted(ITucsonOperation op) {
		// TODO Auto-generated method stub
		
	}
}
