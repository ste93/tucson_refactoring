package alice.respect.core;

import alice.respect.api.IOrdinarySynchInterface;
import alice.respect.api.ISpecificationSynchInterface;
import alice.respect.api.ILinkContext;
import alice.respect.api.IManagementContext;
import alice.respect.api.IOrdinaryAsynchInterface;
import alice.respect.api.ISpecificationAsynchInterface;
import alice.respect.api.IRemoteLinkProvider;
import alice.respect.api.ITCRegistry;
import alice.respect.api.TupleCentreId;
import alice.respect.api.exceptions.InstantiationNotPossibleException;
import alice.respect.api.exceptions.OperationNotPossibleException;
import alice.tucson.service.RemoteLinkProvider;

/**
 * A Container for ReSpecT tuple centres
 * 
 * @author matteo casadei v 2.1.1
 * 
 */
public class RespectTCContainer
{
	private ITCRegistry registry;

	private IRemoteLinkProvider stub;

	private static RespectTCContainer container;
	private static int defaultport;
	
	public static final int QUEUE_SIZE = 10;
	
	// Definizione del Reasoner

	private RespectTCContainer()
	{
		this.registry = new RespectLocalRegistry();
		// Creazione del Reasoner
		this.stub = null;
	}

	public static RespectTCContainer getRespectTCContainer()
	{
		if (RespectTCContainer.container == null)
		{
			RespectTCContainer.container = new RespectTCContainer();
		}

		return RespectTCContainer.container;
	}

	public void setDefPort(int port){
		defaultport = port;
	}
	
	public int getDefPort(){
		return defaultport;
	}
	
	// Metodo per ottenere il Reasoner

	// Qui devo passare il riferimento all'Otologia relativa al tc
	public boolean createRespectTC(TupleCentreId id, Integer q) throws InstantiationNotPossibleException
	{
		// Qui devo passare il riferimento all'Ontologia relativa al tc
		registry.addTC(new RespectTC(id, this, q));
		return true;
	}

	public IOrdinarySynchInterface getBlockingContext(TupleCentreId id) throws OperationNotPossibleException
	{
		try
		{
			return ((RespectTC) registry.getTC(id)).getBlockingContext();
		}
		catch (Exception e)
		{
			RespectTC tc = new RespectTC(id, this, QUEUE_SIZE);
			this.registry.addTC(tc);
			return tc.getBlockingContext();
		}
	}

	public IOrdinaryAsynchInterface getNonBlockingContext(TupleCentreId id)
	{
		try
		{
			return ((RespectTC) registry.getTC(id)).getNonBlockingContext();
		}
		catch (Exception e)
		{
			RespectTC tc = new RespectTC(id, this, QUEUE_SIZE);
			this.registry.addTC(tc);
			return tc.getNonBlockingContext();
		}
	}

	/**
	 * Return a LinkContext for remote/local call
	 * 
	 * @param id
	 *            the identifier of the tuple centre target (local or remote)
	 * @throws OperationNotPossibleException
	 */
	public ILinkContext getLinkContext(TupleCentreId id) throws OperationNotPossibleException{
		
//		System.out.println("[RespectTCContainer]:  id = " + id.toString());
		if ( (id.getNode().equals("localhost") || id.getNode().equals("127.0.0.1")) && id.getPort() == defaultport ){
			try{
				return ((RespectTC) registry.getTC(id)).getLinkContext();
			}catch (InstantiationNotPossibleException e){
				RespectTC tc = new RespectTC(id, this, QUEUE_SIZE);
				this.registry.addTC(tc);
				return tc.getLinkContext();
			}
		}

		if (this.stub == null)
			this.stub = new RemoteLinkProvider();
//			throw new OperationNotPossibleException();
		return stub.getRemoteLinkContext(id);

	}

	public void addStub(IRemoteLinkProvider stub)
	{
		// per aggiungere lo stub nel caso in cui sia presente l'infrastruttura TuCSoN 
		if (stub == null)
		{
			this.stub = stub;
		}
	}

	public ISpecificationAsynchInterface getNonBlockingSpecContext(TupleCentreId id) throws OperationNotPossibleException
	{
		try
		{
			return ((RespectTC) registry.getTC(id)).getNonBlockingSpecContext();
		}
		catch (Exception e)
		{
			RespectTC tc = new RespectTC(id, this, QUEUE_SIZE);
			this.registry.addTC(tc);
			return tc.getNonBlockingSpecContext();
		}
	}

	public ISpecificationSynchInterface getBlockingSpecContext(TupleCentreId id) throws OperationNotPossibleException
	{
		try
		{
			return ((RespectTC) registry.getTC(id)).getBlockingSpecContext();
		}
		catch (Exception e)
		{
			RespectTC tc = new RespectTC(id, this, QUEUE_SIZE);
			this.registry.addTC(tc);
			return tc.getBlockingSpecContext();
		}
	}

	public IManagementContext getManagementContext(TupleCentreId id) throws OperationNotPossibleException
	{
		try
		{
			return ((RespectTC) registry.getTC(id)).getManagementContext();
		}
		catch (Exception e)
		{
			RespectTC tc = new RespectTC(id, this, QUEUE_SIZE);
			this.registry.addTC(tc);
			return tc.getManagementContext();
		}
	}

	// Was used by RespectFramework	and Inspector
	/**
	 * This method should not be used. Use instead get*ContextMethods 
	 * 
	 * @deprecated
	 * 
	 */	
	public RespectTC getTC(TupleCentreId id) throws InstantiationNotPossibleException 
	{ 				 
		return (RespectTC) registry.getTC(id);
	}	 
	
	public ITCRegistry getRegistry(){
		return registry;
	}
}
