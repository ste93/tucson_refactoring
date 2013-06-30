/*
 * TuCSoN coordination infrastructure - Copyright (C) 2001-2002  aliCE team at deis.unibo.it
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package alice.tucson.service;

import alice.logictuple.*;
import alice.logictuple.exceptions.InvalidTupleArgumentException;
import alice.respect.api.exceptions.InvalidTupleCentreIdException;
import alice.respect.core.RespectTC;
import alice.tucson.api.exceptions.*;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.network.TPConfig;
import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.MalformedGoalException;
import alice.tuprolog.Prolog;
import alice.tuprolog.Theory;
import alice.tuprolog.lib.InvalidObjectIdException;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.*;

/**
 * 
 */
public class TucsonNodeService{
	
	private ArrayList<Thread> nodeAgents;
	private ACCProvider ctxman;
	private WelcomeAgent welcome;
	private ArrayList<RespectTC> tcs;
	
//	private static TucsonNodeService instance = null;
	private Date installationDate;
	private HashMap<String, TucsonTCUsers> cores;

	private TucsonAgentId nodeAid;
	private TucsonTupleCentreId idConfigTC;
	private TucsonTupleCentreId idObsTC;
	private ObservationService obsService;

	private Prolog configManager;
	private String configFile;
	private static final String DEFAULT_BOOT_SPEC_FILE = "alice/tucson/service/config/boot_spec.rsp";
	private static final String DEFAULT_OBS_SPEC_FILE = "alice/tucson/service/config/obs_spec.rsp";
	private static final String PERSISTENCY_PATH = "./persistent/";
	private static final String BOOT_SETUP_THEORY = "alice/tucson/service/config/boot.pl";
	private static final int DEFAULT_TCP_PORT = 20504; 
	
//	how to set a "proper" number?
	private static final int MAX_EVENT_QUEUE_SIZE = 1000;
	
	private int tcp_port = DEFAULT_TCP_PORT;
	private TupleArgument persistencyTemplate;
	private boolean observed;
	private ArrayList<TucsonAgentId> agents;

	/**
	 * 
	 * @param configFile
	 * @param portNumber
	 * @param persistTempl
	 * @throws TucsonGenericException
	 */
	public TucsonNodeService(String configFile, int portNumber, TupleArgument persistTempl) throws TucsonGenericException{
		
		log("--------------------------------------------------------------------------------");
		log("Welcome to the TuCSoN infrastructure :)");
		log("Version " + getVersion());
		log("--------------------------------------------------------------------------------");
		
		this.configFile = configFile;
		tcp_port = portNumber;
		persistencyTemplate = persistTempl;
		
		try{
			nodeAid = new TucsonAgentId("'$TucsonNodeService-Agent'");
			idConfigTC = new TucsonTupleCentreId("'$ORG'", "localhost", ""+tcp_port);
			idObsTC = new TucsonTupleCentreId("'$OBS'", "localhost", ""+tcp_port);
		}catch(TucsonInvalidAgentIdException e){
			System.err.println("[TucsonNodeService]: " + e);
			e.printStackTrace();
		}catch(TucsonInvalidTupleCentreIdException e){
			System.err.println("[TucsonNodeService]: " + e);
			e.printStackTrace();
		}
		
		observed = false;
		agents = new ArrayList<TucsonAgentId>();
		nodeAgents = new ArrayList<Thread>();
		tcs = new ArrayList<RespectTC>();
		
	}
	
	public TucsonNodeService(int portno) throws TucsonGenericException{
		this(null, portno, null);
	}

	public TucsonNodeService() throws TucsonGenericException{
		this(null, DEFAULT_TCP_PORT, null);
	}

	public synchronized void install(){
		install(null);
	}

	/**
	 * 
	 * @param repo
	 */
	public synchronized void install(NodeInstallationReport repo){
		
		log("Beginning TuCSoN Node Service installation...");
		log("" + new Date());
		
		configManager = new Prolog();
		cores = new HashMap<String, TucsonTCUsers>();
//		if(configFile != null)
//			log("User-provided configuration file chosen is < " + configFile + " >");
//		else
//			log("Default configuration file chosen");
		
		log("Configuring TuCSoN Node Service...");
		try{
			setupConfiguration(configFile);
		}catch(TucsonGenericException e){
			if(repo != null)
				repo.setReport(false, "Failed", null);
			System.err.println("[TucsonNodeService]: " + e);
			e.printStackTrace();
		}catch(InvalidConfigException e){
			if(repo != null)
				repo.setReport(false, "Failed", null);
			System.err.println("[TucsonNodeService]: " + e);
			e.printStackTrace();
		}
		log("Setting up Observation Service...");
		setupObsTupleCentre();
		log("Setting up Management Service...");
		setupConfigTupleCentre();
//		log("Check Persistency service...");
		checkPersistentTupleCentres(PERSISTENCY_PATH);

		installationDate = new Date();
//		instance = this;

		log("Spawning management agents...");
		bootManagementAgents();

		if(repo != null)
			repo.setReport(true, "Installed", installationDate);
		
//		synchronized(this){
//			try{
//				wait();
//			}catch(InterruptedException e){
//				if(repo != null)
//					repo.setReport(false, "Failed", null);
//				System.err.println("[TucsonNodeService]: " + e);
//				e.printStackTrace();
//			}
//		}
			
	}
	
	/*
	 * Caller too is killed -.-
	 */
	public void shutdown(){
		log("Node is shutting down management agents and proxies...");
		for(Thread t: nodeAgents){
			if(t.isAlive()){
				log("  ...shutting down <" + t.getName() + ">");
				t.interrupt();
			}else{
				log("  ...<" + t.getName() + "> is already dead");
//				t = null; nodeAgents.remove(t);
			}
		}
		welcome.shutdown();
		try {
			ctxman.shutdown();
		} catch (InterruptedException e) {
			log("ACCProvider may still have tasks executing...");
		}
//		nodeAgents.clear();
//		agents.clear();
		log("Node is shutting down ReSpecT VMs...");
		for(RespectTC tc: tcs){
			Thread t = tc.getVMThread();
			if(t.isAlive()){
				log("  ...shutting down <" + tc.getId() + ">");
				t.interrupt();
			}else{
				log("  ...<" + tc.getId() + "> is already dead");
			}
		}
		log("TuCSoN Node shutdown completed, see you :)");
	}

	public static String getVersion(){
		return "TuCSoN-1.10.3.0207";
	}

	/**
	 * 
	 * @param tcName
	 * @return
	 */
	public synchronized TucsonTCUsers resolveCore(String tcName){
		
		if(tcName.indexOf("@")<0)
			tcName +="@localhost";
		if(tcName.indexOf(":")<0)
			tcName +=tcp_port;
		
		TucsonTupleCentreId tid;
		try{
			tid = new TucsonTupleCentreId(tcName);
		}catch(TucsonInvalidTupleCentreIdException e){
			System.err.println("[TucsonNodeService]: " + e);
			e.printStackTrace();
			return null;
		}
		String realName = tid.getName();
		TucsonTCUsers core = cores.get(realName);
		
		if(core == null){
			
			log("Booting new tuple centre < " + realName + " >...");
			try{
				core = bootTupleCentre(realName);
			}catch(TucsonInvalidTupleCentreIdException e){
				System.err.println("[TucsonNodeService]: " + e);
				e.printStackTrace();
				return null;
			}catch(TCInstantiationNotPossibleException e){
				System.err.println("[TucsonNodeService]: " + e);
				e.printStackTrace();
				return null;
			}

		}
		
		return (TucsonTCUsers) core;
		
	}

	/**
	 * 
	 * @param name
	 * @return
	 * @throws TucsonInvalidTupleCentreIdException
	 * @throws TCInstantiationNotPossibleException
	 */
	private TucsonTCUsers bootTupleCentre(String name) throws TucsonInvalidTupleCentreIdException, TCInstantiationNotPossibleException{
		
		if(name.indexOf("@")<0)
			name +="@localhost";
		if(name.indexOf(":")<0)
			name +=(":"+tcp_port);
		
		TucsonTupleCentreId id = new TucsonTupleCentreId(name);
		try {
			tcs.add(TupleCentreContainer.createTC(id, MAX_EVENT_QUEUE_SIZE, tcp_port));
		} catch (InvalidTupleCentreIdException e1) {
			log("TupleCentreContainer.createTC(...) error");
			e1.printStackTrace();
		}
		
		if(observed){
			try{
				TupleCentreContainer.doManagementOperation(TucsonOperation.addObsCode(), id, obsService);
			}catch(TucsonOperationNotPossibleException e){
				System.err.println("[TucsonNodeService]: " + e);
				e.printStackTrace();
			}catch(TucsonInvalidLogicTupleException e){
				System.err.println("[TucsonNodeService]: " + e);
				e.printStackTrace();
			}
			obsService.tcCreated(id);
		}
		
		TucsonTCUsers tcUsers = new TucsonTCUsers(id);
		cores.put(id.getName(), tcUsers);

//		try{
//			TupleArgument tid = Value.parse(name);
//			if (persistencyTemplate != null && persistencyTemplate.match(tid)){
//				log("Enabling persistency on tuple centre < " + name + " >...");
//				TupleCentreContainer.enablePersistence(id, PERSISTENCY_PATH + "tc_" + tid.toString() + ".dat");
//				TupleCentreContainer.out(nodeAid, id, new LogicTuple("is_persistent", new Value(name)));
//			}
//		}catch (Exception e){
//			System.err.println("[TucsonNodeService]: " + e);
//			e.printStackTrace();
//		}
		
		return tcUsers;
		
	}

	/**
	 * 
	 * @param tcName
	 * @return
	 */
//	exception handling is a mess, review it...
	public synchronized boolean destroyCore(String tcName){
			
		if(tcName.indexOf("@")<0)
			tcName +="@localhost";
		if(tcName.indexOf(":")<0)
			tcName +=tcp_port;
		
		TucsonTupleCentreId tid;
		try{
			tid = new TucsonTupleCentreId(tcName);
		}catch(TucsonInvalidTupleCentreIdException e){
			System.err.println("[TucsonNodeService]: " + e);
			e.printStackTrace();
			return false;
		}
		String realName = tcName;
		TucsonTupleCentreId core = cores.get(realName).getTucsonTupleCentreId();

		if(core != null){
			
			log("Destroying tuple centre < " + realName + " >...");
			TupleCentreContainer.destroyTC(cores.get(tcName).getTucsonTupleCentreId());
			if(observed)
				obsService.tcDestroyed(tid);

			try{
				try{
					TupleArgument tcArg = TupleArgument.parse(tcName);
					TupleCentreContainer.doBlockingOperation(TucsonOperation.inpCode(), nodeAid, idConfigTC, new LogicTuple("tuple_centre", tcArg));
					TupleCentreContainer.doBlockingOperation(TucsonOperation.inpCode(), nodeAid, idConfigTC, new LogicTuple("is_persistent", new Value(tcName)));
				}catch(Exception ex){
					TupleCentreContainer.doBlockingOperation(TucsonOperation.inpCode(), nodeAid, idConfigTC, new LogicTuple("tuple_centre", new Value(tcName)));
				}
			}catch(Exception e){
				System.err.println("[TucsonNodeService]: " + e);
				e.printStackTrace();
			}
			
			cores.remove(realName);
			return true;
			
		}else
			return false;
		
	}

	/**
	 * 
	 * @param template
	 */
	public synchronized void enablePersistence(TupleArgument template){
		
		persistencyTemplate = template;
		Iterator<TucsonTCUsers> it = cores.values().iterator();
		while(it.hasNext()){
			TucsonTCUsers tc = it.next();
			try{
				TupleArgument tid = Value.parse(tc.getTucsonTupleCentreId().getName());
				if(template.match(tid)){
					TupleCentreContainer.enablePersistence(tc.getTucsonTupleCentreId(), PERSISTENCY_PATH + "tc_" + tc.getTucsonTupleCentreId().getName() + ".dat");
					TupleCentreContainer.doBlockingOperation(TucsonOperation.outCode(), nodeAid, tc.getTucsonTupleCentreId(), new LogicTuple("is_persistent", new Value(tc.getTucsonTupleCentreId().getName())));
				}
			}catch(InvalidTupleArgumentException e){
				System.err.println("[TucsonNodeService]: " + e);
				e.printStackTrace();
			}catch(Exception e){
				System.err.println("[TucsonNodeService]: " + e);
				e.printStackTrace();
			}
		}
	
	}

	/**
	 * 
	 * @param tc
	 */
	public synchronized void enablePersistence(String tc){
		
		TucsonTCUsers tar = cores.get(tc);
		TupleCentreContainer.enablePersistence(tar.getTucsonTupleCentreId(), PERSISTENCY_PATH + "tc_" + tar.getTucsonTupleCentreId().getName() + ".dat");
		try{
			TupleCentreContainer.doBlockingOperation(TucsonOperation.outCode(), nodeAid, tar.getTucsonTupleCentreId(), new LogicTuple("is_persistent", new Value(tar.getTucsonTupleCentreId().getName())));
		}catch(Exception e){
			System.err.println("[TucsonNodeService]: " + e);
			e.printStackTrace();
		}
	
	}

	/**
	 * 
	 * @param template
	 */
	public synchronized void disablePersistence(TupleArgument template){
		
		if(persistencyTemplate!=null){
			
			Iterator<TucsonTCUsers> it = cores.values().iterator();
			while(it.hasNext()){
				TucsonTCUsers tc = it.next();
				try{
					TupleArgument tid = Value.parse(tc.getTucsonTupleCentreId().getName());
					if(template.match(tid)){
						TupleCentreContainer.disablePersistence(tc.getTucsonTupleCentreId());
						TupleCentreContainer.doBlockingOperation(TucsonOperation.inCode(), nodeAid, tc.getTucsonTupleCentreId(), new LogicTuple("is_persistent", new Value(tc.getTucsonTupleCentreId().getName())));
					}
				}catch(InvalidTupleArgumentException e){
					System.err.println("[TucsonNodeService]: " + e);
					e.printStackTrace();
				}catch(Exception e){
					System.err.println("[TucsonNodeService]: " + e);
					e.printStackTrace();
				}
			}
			if (persistencyTemplate.match(template))
				persistencyTemplate = null;
	 	}
		
	}

	/**
	 * 
	 * @param tc
	 */
	public synchronized void disablePersistence(String tc){
		
		TucsonTCUsers tar = cores.get(tc);
		TupleCentreContainer.disablePersistence(tar.getTucsonTupleCentreId());
		try{
			TupleCentreContainer.doBlockingOperation(TucsonOperation.inCode(), nodeAid, tar.getTucsonTupleCentreId(), new LogicTuple("is_persistent", new Value(tar.getTucsonTupleCentreId().getName())));
		}catch(Exception e){
			System.err.println("[TucsonNodeService]: " + e);
			e.printStackTrace();
		}
		
	}

	/**
	 * 
	 * @param dirName
	 */
	private void checkPersistentTupleCentres(String dirName){
		
		File dir = new File(dirName);
		if(dir.exists() && dir.isDirectory()){
			String[] files = dir.list();
			for(int i = 0; i < files.length; i++){
				if(files[i].startsWith("tc_") && files[i].endsWith(".dat")){
					String tcName = files[i].substring(3, files[i].length() - 4);
					try{
						bootTupleCentre(tcName);
					}catch(TucsonInvalidTupleCentreIdException e){
						System.err.println("[TucsonNodeService]: " + e);
						e.printStackTrace();
					}catch(TCInstantiationNotPossibleException e){
						System.err.println("[TucsonNodeService]: " + e);
						e.printStackTrace();
					}
					TupleCentreContainer.loadPersistentInformation(cores.get(tcName).getTucsonTupleCentreId(), PERSISTENCY_PATH + files[i]);
					TupleCentreContainer.enablePersistence(cores.get(tcName).getTucsonTupleCentreId(), PERSISTENCY_PATH + files[i]);
					try{
						TupleCentreContainer.doBlockingOperation(TucsonOperation.outCode(), nodeAid, cores.get(tcName).getTucsonTupleCentreId(), new LogicTuple("is_persistent", new Value(tcName)));
					}catch(Exception e){
						System.err.println("[TucsonNodeService]: " + e);
						e.printStackTrace();
					}
				}
			}
		}else
			dir.mkdir();
		
	}

	public Date getInstallationDate(){
		return installationDate;
	}
	
	public int getTCPPort(){
		return tcp_port;
	}

	private static void log(String m){
		System.out.println("[TuCSoN Node Service]: " + m);
	}

	/**
	 * 
	 * @param configFile
	 * @throws TucsonGenericException
	 * @throws InvalidConfigException
	 */
//	exception handling is a mess, review it...
	private void setupConfiguration(String configFile) throws TucsonGenericException, InvalidConfigException{
		
		alice.tuprolog.lib.JavaLibrary jlib = (alice.tuprolog.lib.JavaLibrary) configManager.getLibrary("alice.tuprolog.lib.JavaLibrary");
		try{
			jlib.register(new alice.tuprolog.Struct("config"), this);
		}catch(InvalidObjectIdException e){
			throw new TucsonGenericException("Internal Failure: loading JavaLibrary in Prolog Configuration Engine failed.");
		}

		if(configFile != null){
			
			try{
				InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(BOOT_SETUP_THEORY);
				Theory setup_th = new Theory(new BufferedInputStream(is));
				configManager.setTheory(setup_th);
			}catch (alice.tuprolog.InvalidTheoryException ex){
				throw new TucsonGenericException("Internal Failure: invalid Prolog Setup Engine theory.");
			}catch (IOException ex){
				throw new TucsonGenericException("Internal Failure: loading Prolog Setup Engine theory failed.");
			}
			
			Theory cfg_th;
			alice.tuprolog.SolveInfo info;
			try{
				cfg_th = new Theory(new FileInputStream(configFile));
				configManager.addTheory(cfg_th);
				info = configManager.solve("setup.");
			}catch(FileNotFoundException e){
				throw new TucsonGenericException("Internal Failure: Prolog Configuration Engine theory not found.");
			}catch(IOException e){
				throw new TucsonGenericException("Internal Failure: loading Prolog Configuration Engine theory failed.");
			}catch(InvalidTheoryException e){
				throw new InvalidConfigException();
			}catch(MalformedGoalException e){
				throw new TucsonGenericException("Internal Failure: solving Prolog Configuration Engine theory failed.");
			}
			
			if (!info.isSuccess())
				throw new InvalidConfigException();
			
		}
		
	}

	/**
	 * 
	 */
	private void setupConfigTupleCentre(){
		
		try{
			bootTupleCentre(idConfigTC.getName());
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(DEFAULT_BOOT_SPEC_FILE);
			String spec = alice.util.Tools.loadText(new BufferedInputStream(is));
			LogicTuple specTuple = new LogicTuple("spec", new Value(spec));
			TupleCentreContainer.doBlockingSpecOperation(TucsonOperation.set_sCode(), nodeAid, idConfigTC, specTuple);
			TupleCentreContainer.doNonBlockingOperation(TucsonOperation.outCode(), nodeAid, idConfigTC, new LogicTuple("boot"), null);
			addAgent(nodeAid);
		}catch(TucsonInvalidTupleCentreIdException e){
			System.err.println("[TucsonNodeService]: " + e);
			e.printStackTrace();
		}catch(TCInstantiationNotPossibleException e){
			System.err.println("[TucsonNodeService]: " + e);
			e.printStackTrace();
		}catch(IOException e){
			System.err.println("[TucsonNodeService]: " + e);
			e.printStackTrace();
		}catch(Exception e){
			System.err.println("[TucsonNodeService]: " + e);
			e.printStackTrace();
		}

	}

	/**
	 * 
	 */
	private void setupObsTupleCentre(){
		
		try{
			bootTupleCentre(idObsTC.getName());
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(DEFAULT_OBS_SPEC_FILE);
			String spec = alice.util.Tools.loadText(new BufferedInputStream(is));
			LogicTuple specTuple = new LogicTuple("spec", new Value(spec));
			TupleCentreContainer.doBlockingSpecOperation(TucsonOperation.set_sCode(), nodeAid, idObsTC, specTuple);
			TupleCentreContainer.doNonBlockingOperation(TucsonOperation.outCode(), nodeAid, idObsTC, new LogicTuple("boot"), null);
			obsService = new ObservationService(idObsTC);
		}catch(TucsonInvalidTupleCentreIdException e){
			System.err.println("[TucsonNodeService]: " + e);
			e.printStackTrace();
		}catch(TCInstantiationNotPossibleException e){
			System.err.println("[TucsonNodeService]: " + e);
			e.printStackTrace();
		}catch(IOException e){
			System.err.println("[TucsonNodeService]: " + e);
			e.printStackTrace();
		}catch(Exception e){
			System.err.println("[TucsonNodeService]: " + e);
			e.printStackTrace();
		}

	}

	/**
	 * 
	 */
	private void bootManagementAgents(){
		
		log("Spawning Node Management Agent...");
		nodeAgents.add(new NodeManagementAgent(idConfigTC, this));

		log("--------------------------------------------------------------------------------");
		log("Spawning ACC Provider Agent...");
		ctxman = new ACCProvider(this, idConfigTC);
		
		log("Spawning Welcome Agent...");
		welcome = new WelcomeAgent(this, ctxman);

	}
	
	synchronized public void addNodeAgent(Thread t){
		nodeAgents.add(t);
	}
	synchronized public void removeNodeAgent(Thread t){
		nodeAgents.remove(t);
	}

	synchronized public void addAgent(TucsonAgentId aid){
		
		boolean present = false;
		if(!agents.contains(aid))
			agents.add(aid);
		else
			present = true;

		if(observed && !present)
			obsService.accEntered(aid);
		
	}

	synchronized public void removeAgent(TucsonAgentId aid){
		
		boolean present = true;
		if(agents.contains(aid))
			agents.remove(aid);
		else
			present = false;
		
		if(observed && present)
			obsService.accQuit(aid);

		Iterator<TucsonTCUsers> it = cores.values().iterator();
		while (it.hasNext())
			it.next().removeUser(aid);
		
	}

	public static boolean isInstalled(int port) throws IOException{
		SocketAddress addr = new InetSocketAddress(port);
		Socket sock = new Socket();
		try {
			sock.bind(addr);
		} catch (IOException e) {
			return true;
		} finally {
			sock.close();
		}
		return false;
	}

//	public static TucsonNodeService getInstance(){
//		return instance;
//	}
//	
//	public static Prolog getEngineProlog(){
//		return TucsonNodeService.getInstance().configManager;
//	}

	/**
	 * 
	 */
	synchronized public void activateObservability(){		
		observed = true;
		Iterator<TucsonTCUsers> it = cores.values().iterator();
		try{
			while(it.hasNext()){
				TucsonTCUsers tc = it.next();
				TupleCentreContainer.doManagementOperation(TucsonOperation.addObsCode(), tc.getTucsonTupleCentreId(), obsService);
			}
		}catch(TucsonOperationNotPossibleException e){
			System.err.println("[TucsonNodeService]: " + e);
			e.printStackTrace();
		}catch(TucsonInvalidLogicTupleException e){
			System.err.println("[TucsonNodeService]: " + e);
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	synchronized public void deactivateObservability(){
		observed = false;
		Iterator<TucsonTCUsers> it = cores.values().iterator();
		try{
			while(it.hasNext()){
				TucsonTCUsers tc = it.next();
				TupleCentreContainer.doManagementOperation(TucsonOperation.rmvObsCode(), tc.getTucsonTupleCentreId(), obsService);
			}
		}catch(TucsonOperationNotPossibleException e){
			System.err.println("[TucsonNodeService]: " + e);
			e.printStackTrace();
		}catch(TucsonInvalidLogicTupleException e){
			System.err.println("[TucsonNodeService]: " + e);
			e.printStackTrace();
		}
	}

	public NodeServiceListener getListener(){
		return obsService;
	}

	public HashMap<String, TucsonTCUsers> getCores(){
		return cores;
	}

	/**
	 * 
	 * @param agentId
	 * @param tid
	 */
//	why another slightly different method to add an agent? is this for inter-tc agents?
	public void addTCAgent(TucsonAgentId agentId, TucsonTupleCentreId tid){
		cores.get(tid.getName()).addUser(agentId);
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String args[]){
		
		if(alice.util.Tools.isOpt(args, "-help") || alice.util.Tools.isOpt(args, "-?")){
			log("Arguments: -portno {portNumber} {-? | -help}");
//			log("Arguments: -port {portNumber} -config {configFile} -persistency {persistencyTemplate} {-? | -help}");
		}else{
						
			String portInfo = alice.util.Tools.getOpt(args, "-portno");
			String configInfo = alice.util.Tools.getOpt(args, "-config");
			String persistencyInfo = alice.util.Tools.getOpt(args, "-persistency");
			
			if(portInfo != null){
				try{
					int portNumber;
					portNumber = Integer.parseInt(portInfo);
					TPConfig.getInstance().setTcpPort(portNumber);
				}catch(NumberFormatException e){
					System.err.println("Invalid port number");
					System.exit(-1);
				}catch (IllegalArgumentException e) {
					System.err.println("Invalid port number");
					System.exit(-1);
				}
			}
			
//			at least check if the config file exists...you don't say?
			
			TupleArgument template = null;
			if(persistencyInfo != null){
				try {
					template = TupleArgument.parse(persistencyInfo);
				} catch (InvalidTupleArgumentException e) {
					System.err.println("Invalid persistency template");
					System.exit(-1);
				}
			}
			
			try{
				// Only for compatibility, this code should be removed.
				// TPConfigNodeSide is a singleton
				int portNumber = TPConfig.getInstance().getNodeTcpPort();
				new TucsonNodeService(configInfo, portNumber, template).install();
			}catch(TucsonGenericException e) {
				System.err.println("Tucson Service Node installation failed: " + e.getMsg());
				System.exit(-1);
			}
			
		}
		
	}
	
}
