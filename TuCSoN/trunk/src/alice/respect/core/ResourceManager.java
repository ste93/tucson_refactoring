package alice.respect.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import alice.respect.probe.ISimpleProbe;
import alice.respect.probe.ProbeId;
import alice.respect.transducer.TransducerId;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;

/**
 * 
 * Class used for managing probes.
 * 
 * @author Steven maraldi
 *
 */
public class ResourceManager {

	/** List of all probes on a single node **/
	private HashMap< ProbeId, ISimpleProbe > probeList;
	
	/** The ResourceManager instance **/
	private static ResourceManager rm;
	
	private ResourceManager(){
		probeList = new HashMap< ProbeId, ISimpleProbe >();
	}
	
	/**
	 * Gets the static instance of the resource manager
	 * 
	 * @return
	 * 		the resource manager
	 */
	public static ResourceManager getResourceManager(){
		if( ResourceManager.rm == null )
			ResourceManager.rm = new ResourceManager();
		
		return ResourceManager.rm;
	}

	/**
	 * Creates a resource
	 * 
	 * @param className
	 * 		the concrete implementative class of the resource
	 * @param id
	 * 		the identifier of the resource
	 *
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public boolean createResource( String className, ProbeId id ) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		if( probeList.containsKey(id) ){
			speakErr("The probe "+id.getLocalName()+" already exist");
			return false;
		}
		
		String normClassName = className.substring( 1, className.length()-1 );
		Class<?> c = Class.forName( normClassName );
        Constructor<?> ctor = c.getConstructor(new Class[] { ProbeId.class });
        ISimpleProbe probe = (ISimpleProbe) ctor.newInstance(new Object[] { id });
        
        probeList.put( id, probe );
		speak("Resource "+id.getLocalName()+" has been registered");
		return true;
	}
	
	/**
	 * Sets the transducer which the probe will communicate with.
	 *  
	 * @param pId
	 * 		the probe's identifier
	 * @param tId
	 * 		the transducer's identifier
	 */
	public void setTransducer( ProbeId pId, TransducerId tId ){
		getResource(pId).setTransducer( tId );
		if( tId != null )
			speak("Transducer "+tId.getAgentName()+" setted to probe "+pId.getLocalName());
	}
	
	/**
	 * Removes a resource from the list
	 * 
	 * @param id
	 * 		the identifier of the resource to remove
	 * @throws TucsonOperationNotPossibleException 
	 */
	public boolean removeResource( ProbeId id ) throws TucsonOperationNotPossibleException{
		speak("Removing probe "+id.getLocalName()+" from the resource list");
		if( !probeList.containsKey(id) ){
			speakErr("Resource "+id.getLocalName()+" doesn't exist");
			return false;
		}
		TransducerManager.getTransducerManager().removeResource( id );
		probeList.remove( id );
		return true;
	}
	
	/**
	 * Gets the resource by its identifier
	 * 
	 * @param id
	 * 		the resource's identifier
	 * @return
	 */
	public ISimpleProbe getResource( ProbeId id ){
		if( probeList.containsKey( id ) )
			return probeList.get( id );
		else{
			speakErr("Resource "+id.getLocalName()+" isn't registered.");
			return null;
		}
	}
	
	/**
	 * Gets the resource by its local name
	 * 
	 * @param name
	 * 		resource's local name
	 * @return
	 */
	public ISimpleProbe getResourceByName( String name ){
		Object[] keySet = probeList.keySet().toArray();
		for( int i=0; i<keySet.length; i++ ){
			if( ((ProbeId)keySet[i]).getLocalName().equals( name ) ){
				return probeList.get( (ProbeId)keySet[i] );
			}
		}
		speakErr("Resource "+name+" isn't registered.");
		return null;
	}
		
	/**
	 * Utility method used to communicate an output message to the console.
	 * 
	 * @param msg
	 * 		message to print
	 */
	private void speak( String msg ){
		System.out.println("[ResourceManager] "+msg);
	}
	
	private void speakErr( String msg ){
		System.err.println("[ResourceManager] "+msg);
	}
}
