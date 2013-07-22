package alice.respect.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import alice.respect.api.EnvId;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tuplecentre.api.TupleCentreId;
import alice.tuprolog.Term;
import alice.respect.probe.ISimpleProbe;
import alice.respect.probe.ProbeId;
import alice.respect.transducer.Transducer;
import alice.respect.transducer.TransducerId;
import alice.respect.transducer.TransducerStandardInterface;

/**
 * 
 * Class used for managing transducers. It can be used for environment configuration and as a provider of 
 * information concerning transducer's associations.
 * 
 * @author Steven Maraldi
 *
 */
public class TransducerManager
{
	/** List of all the transducers on a single node **/
	private static HashMap<TransducerId,Transducer> transducerList;
	
	/** List of the associations transducer/probes **/
	private static HashMap<TransducerId, ArrayList<ProbeId>> resourceList;
	
	/** List of the associations tuple centre/transducers **/
	private static HashMap<TupleCentreId, ArrayList<TransducerId>> tupleCentresAssociated;
	
	/** The TransducerManager instance **/
	private static TransducerManager tm;
	
	private TransducerManager(){
		transducerList = new HashMap< TransducerId, Transducer >();
		resourceList = new HashMap< TransducerId, ArrayList<ProbeId> >();
		tupleCentresAssociated = new HashMap< TupleCentreId, ArrayList<TransducerId> >();
	}
	
	/**
	 * Gets the static instance of the transducer manager
	 * 
	 * @return
	 * 		the transducer manager
	 */
	public synchronized static TransducerManager getTransducerManager(){
		if ( TransducerManager.tm == null)
				TransducerManager.tm = new TransducerManager();

		return TransducerManager.tm;
	}
	
	/**
	 * Creates a new transducer
	 * 
	 * @param className
	 * 		name of the concrete implementative class of transducer
	 * @param id
	 * 		the transducer's identifier
	 * @param tcId
	 * 		the tuple center with which the transducer will interact
	 * @param probeId
	 * 		resource's identifier associated to the transducer
	 * 
	 * 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 */	
	public boolean createTransducer( String className, TransducerId id, TupleCentreId tcId, ProbeId probeId ) throws InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException{
		// Checking if the transducer already exist
		if(transducerList.containsKey( id )){
			speakErr("Transducer "+id.toString()+" is already been registered");
			return false;
		}
		
		// Registering tuple centre <> transducer association
		if(tupleCentresAssociated.containsKey( tcId )){
			tupleCentresAssociated.get( tcId ).add( id );
		}else{
			ArrayList<TransducerId> transducers = new ArrayList<TransducerId>();
			transducers.add( id );
			tupleCentresAssociated.put( tcId, transducers );
		}
		
		// Instantiating the concrete class
		String normClassName = className.substring( 1, className.length()-1 );
		Class<?> c = Class.forName( normClassName );
        Constructor<?> ctor = c.getConstructor(new Class[] { TransducerId.class, TupleCentreId.class, ProbeId.class });
        Transducer t = (Transducer) ctor.newInstance(new Object[] { id, tcId, probeId });
		transducerList.put( id, t );
		
		// Adding probe to the transducer
		ArrayList< ProbeId > probes = new ArrayList< ProbeId >();
		probes.add( probeId );
		resourceList.put( id, probes );
		addResource( probeId, id, ResourceManager.getResourceManager().getResource(probeId) );
		speak("Transducer "+id.toString()+" has been registered");
		return true;
	}
	
	/**
	 * Stops and removes the transducer identified by id
	 * 
	 * @param id
	 * 		the transducer identifier
	 * @throws TucsonOperationNotPossibleException 
	 */
	public void stopTransducer( TransducerId id ) throws TucsonOperationNotPossibleException{
		if(!transducerList.containsKey(id)){
			speakErr("The transducer "+id+" doesn't exist.");
			return;
		}
		transducerList.get(id).exit();
		// Decouple the transducer from the probes associated.
		Object[] pIds = resourceList.get(id).toArray();
		for( int i=0; i<pIds.length; i++){
			ResourceManager.getResourceManager().getResource( (ProbeId)pIds[i] ).setTransducer( null );
		}
		
		transducerList.remove(id);
		resourceList.remove(id);
		TupleCentreId tcAssociated = getTupleCentreId(id);
		tupleCentresAssociated.get( tcAssociated ).remove(id);
		// If the tc doesn't have any transducer associated, it will be removed
		if( tupleCentresAssociated.get( tcAssociated ).isEmpty() )
			tupleCentresAssociated.remove( tcAssociated );
		speak("Transducer "+id+" has been removed.");
	}
	
	/**
	 * Adds a new resource and associate it to the transducer tId.
	 * 
	 * @param id
	 * 		new environment resource identifier
	 * @param tId
	 * 		transducer associated
	 * @param probe
	 * 		the probe itself
	 */
	public boolean addResource( ProbeId id, TransducerId tId, ISimpleProbe probe){
		speak("Adding new resource "+id.getLocalName()+" to transducer "+tId.getAgentName());
		if(!resourceList.containsKey(tId)){
			speakErr("Transducer "+tId.getAgentName()+" doesn't exist.");
			return false;
		}else if(resourceList.get(tId).contains(probe)){
			speak("Transducer "+tId.getAgentName()+" is already associated to probe "+id.getLocalName());
			return false;
		}
		transducerList.get(tId).addProbe(id,probe);
		resourceList.get(tId).add(id);
		ResourceManager.getResourceManager().setTransducer( id, tId );
		return true;
	}
	
	/**
	 * Removes a probe from the resource list
	 * 
	 * @param probe
	 * 		the resource's identifier to remove
	 * @throws TucsonOperationNotPossibleException 
	 */
	public boolean removeResource( ProbeId probe ) throws TucsonOperationNotPossibleException{
		if( !resourceList.containsValue(probe) ){
			return false;
		}
		TransducerId tId = getTransducerId( probe );
		transducerList.get(tId).removeProbe(probe);
		resourceList.get(tId).remove(probe);
		ResourceManager.getResourceManager().getResource( probe ).setTransducer( null );
		// Se il transducer è rimasto senza risorse associate viene terminato
		if( resourceList.get( tId ).isEmpty() ){
			speak("Transducer "+tId.toString()+" has no more resources associated. Its execution will be stopped");
			stopTransducer( tId );
		}
		return true;
	}
	
	/**
	 * Returns the transducer's identifier associated to the input resource
	 * 
	 * @param probe
	 * 		resource associated to the transducer
	 * @return
	 * 		the transducer identifier
	 */
	public TransducerId getTransducerId( EnvId probe ){	
		Set<TransducerId> set = resourceList.keySet();
		Object[] keySet = set.toArray();
		for( int i = 0; i < keySet.length; i++){
			for( int j = 0; j < resourceList.get( keySet[i] ).size(); j++ ){
				Term pId = resourceList.get( keySet[i] ).get( j ).toTerm();
				if( pId.equals( probe.toTerm() ) ){
					return (TransducerId) keySet[i];
				}
			}			
		}
		speakErr("The resource "+probe.getLocalName()+" isn't associated to any transducer");
		return null;
	}
	
	/**
	 * Gets the list of transducer ids associated to the tuple centre identified by tcId
	 * 
	 * @param tcId
	 * 		the tuple centre's identifier
	 * @return
	 * 		list of transducer id associated to tcId
	 */
	public TransducerId[] getTransducerIds( TupleCentreId tcId ){
		Object[] tcIds = tupleCentresAssociated.keySet().toArray();
		for( int i=0; i<tcIds.length; i++ ){
			if( ((TupleCentreId)tcIds[i]).toString().equals( tcId.toString() ) ){
				Object[] values = tupleCentresAssociated.get( (TupleCentreId)tcIds[i] ).toArray();
				TransducerId[] transducerIds = new TransducerId[values.length];
				for( int j=0; j<values.length; j++ )
					transducerIds[j] = (TransducerId) values[j];
				return transducerIds;
			}				
		}
		speakErr("There's no transducer associated to tuple centre "+tcId+" or it doesn't exist at all");
		return null;		
	}
	
	/**
	 * Returns the transducer identified by tId
	 * 
	 * @param tId
	 * 		the transducer's name
	 * @return
	 * 		the transducer
	 */
	public TransducerStandardInterface getTransducer( String tId ){
		Object[] keySet = transducerList.keySet().toArray();
		for( int i = 0; i < keySet.length; i++){
			if( ((TransducerId)keySet[i]).getAgentName().equals(tId) )
				return transducerList.get( (TransducerId)keySet[i] );
		}
		return null;
	}
	
	/**
	 * Returns the resource list associated to the transducer identified by tId
	 * 
	 * @param tId
	 * 		the transducer's identifier
	 * @return
	 * 		a resource list as a ProbeId array.
	 */
	public ProbeId[] getResources( TransducerId tId ){
		if( !resourceList.containsKey(tId) ){
			speakErr("The transducer "+tId.getAgentName()+" doesn't exist");
			return null;
		}
		Object[] values = resourceList.get(tId).toArray();
		ProbeId[] probes = new ProbeId[values.length];
		for( int i=0; i<probes.length; i++ ){
			probes[i] = (ProbeId) values[i];
		}
		return probes;
	}
	
	/**
	 * Returns the identifier of the tuple centre associated to the transducer identified by tId
	 * 
	 * @param tId
	 * 		the transducer's identifier
	 * @return
	 * 		the tuple centre's identifier
	 */
	public TupleCentreId getTupleCentreId( TransducerId tId ){
		if( tupleCentresAssociated.containsValue( tId ) ){
			TupleCentreId[] tcArray = (TupleCentreId[])tupleCentresAssociated.keySet().toArray();
			for( int i = 0; i<tcArray.length; i++ ){
				if( tupleCentresAssociated.get( tcArray[i] ).contains( tId ) ){
					return tcArray[i];
				}
			}
		}
		speakErr("The transducer "+tId.getAgentName()+" doesn't exist");
		return null;
	}
	
	/**
	 * Utility methods used to communicate an output message to the console.
	 * 
	 * @param msg
	 * 		message to print.
	 */
	private void speak( String msg ){
		System.out.println("[TransducerManager] "+msg);
	}
	
	private void speakErr( String msg ){
		System.err.println("[TransducerManager] "+msg);
	}
}
