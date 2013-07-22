/*
 * TuCSoN coordination infrastructure - Copyright (C) 2001-2002 aliCE team at
 * deis.unibo.it This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */

package alice.tucson.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import alice.logictuple.LogicTuple;
import alice.logictuple.TupleArgument;
import alice.logictuple.Value;
import alice.logictuple.exceptions.InvalidTupleArgumentException;
import alice.respect.api.exceptions.InvalidTupleCentreIdException;
import alice.respect.core.RespectTC;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.InvalidConfigException;
import alice.tucson.api.exceptions.TCInstantiationNotPossibleException;
import alice.tucson.api.exceptions.TucsonGenericException;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidLogicTupleException;
import alice.tucson.api.exceptions.TucsonInvalidSpecificationException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.network.TPConfig;
import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.MalformedGoalException;
import alice.tuprolog.Prolog;
import alice.tuprolog.Theory;
import alice.tuprolog.lib.InvalidObjectIdException;

/**
 * 
 * @author ste (mailto: s.mariani@unibo.it) on 17/lug/2013
 * 
 */
public class TucsonNodeService {

    private static final String BOOT_SETUP_THEORY =
            "alice/tucson/service/config/boot.pl";
    private static final String DEFAULT_BOOT_SPEC_FILE =
            "alice/tucson/service/config/boot_spec.rsp";
    private static final String DEFAULT_OBS_SPEC_FILE =
            "alice/tucson/service/config/obs_spec.rsp";
    private static final int DEFAULT_TCP_PORT = 20504;

    // how to set a "proper" number?
    private static final int MAX_EVENT_QUEUE_SIZE = 1000;
    private static final String PERSISTENCY_PATH = "./persistent/";

    /**
     * 
     * @return the String representation of the TuCSoN version
     */
    public static String getVersion() {
        return "TuCSoN-1.10.3.0207";
    }

    /**
     * 
     * @param port
     *            the listening port where to test if a TuCSoN node is up &
     *            running on
     * @return whether a TuCSoN node is up & active on the given port
     * @throws IOException
     *             if the given port cannot be reached over the network
     */
    public static boolean isInstalled(final int port) throws IOException {
        final SocketAddress addr = new InetSocketAddress(port);
        final Socket sock = new Socket();
        try {
            sock.bind(addr);
        } catch (final IOException e) {
            return true;
        } finally {
            sock.close();
        }
        return false;
    }

    /**
     * 
     * @param args
     *            the arguments to start the TuCSoN node with
     */
    public static void main(final String[] args) {

        if (alice.util.Tools.isOpt(args, "-help")
                || alice.util.Tools.isOpt(args, "-?")) {
            TucsonNodeService
                    .log("Arguments: -portno {portNumber} {-? | -help}");
        } else {

            final String portInfo = alice.util.Tools.getOpt(args, "-portno");
            final String configInfo = alice.util.Tools.getOpt(args, "-config");
            final String persistencyInfo =
                    alice.util.Tools.getOpt(args, "-persistency");

            int portNumber;
            if (portInfo != null) {
                try {
                    portNumber = Integer.parseInt(portInfo);
                    if ((portNumber > 0) || (portNumber < 64000)) {
                        TPConfig.getInstance().setTcpPort(portNumber);
                    } else {
                        System.err.println("Invalid port number");
                        System.exit(-1);
                    }
                } catch (final NumberFormatException e) {
                    System.err.println("Invalid port number");
                    System.exit(-1);
                }
            }

            // at least check if the config file exists...you don't say?

            TupleArgument template = null;
            if (persistencyInfo != null) {
                try {
                    template = TupleArgument.parse(persistencyInfo);
                } catch (final InvalidTupleArgumentException e) {
                    System.err.println("Invalid persistency template");
                    System.exit(-1);
                }
            }

            /*
             * TODO CICORA: Only for compatibility, this code should be removed
             * TPConfigNodeSide is a singleton
             */
            portNumber = TPConfig.getInstance().getNodeTcpPort();
            new TucsonNodeService(configInfo, portNumber, template).install();

        }

    }

    private static void log(final String m) {
        System.out.println("[TuCSoN Node Service]: " + m);
    }

    private final List<TucsonAgentId> agents;
    private final String configFile;
    private Prolog configManager;
    private Map<String, TucsonTCUsers> cores;
    private ACCProvider ctxman;
    private TucsonTupleCentreId idConfigTC;
    private TucsonTupleCentreId idObsTC;

    private Date installationDate;

    private final List<Thread> nodeAgents;
    private TucsonAgentId nodeAid;
    private boolean observed;
    private ObservationService obsService;

    private TupleArgument persistencyTemplate;

    private int tcpPort = TucsonNodeService.DEFAULT_TCP_PORT;

    private final List<RespectTC> tcs;

    private WelcomeAgent welcome;

    /**
     * 
     */
    public TucsonNodeService() {
        this(null, TucsonNodeService.DEFAULT_TCP_PORT, null);
    }

    /**
     * 
     * @param portno
     *            the default listening port of this TuCSoN node
     */
    public TucsonNodeService(final int portno) {
        this(null, portno, null);
    }

    /**
     * 
     * @param conf
     *            the configuration file to load
     * @param portNumber
     *            the default listening port of this TuCSoN node
     * @param persistTempl
     *            the persistency template to be used to permanently store
     *            tuples
     */
    public TucsonNodeService(final String conf, final int portNumber,
            final TupleArgument persistTempl) {

        TucsonNodeService
                .log("--------------------------------------------------------------------------------");
        TucsonNodeService.log("Welcome to the TuCSoN infrastructure :)");
        TucsonNodeService.log("Version " + TucsonNodeService.getVersion());
        TucsonNodeService
                .log("--------------------------------------------------------------------------------");

        this.configFile = conf;
        this.tcpPort = portNumber;
        this.persistencyTemplate = persistTempl;

        try {
            this.nodeAid = new TucsonAgentId("'$TucsonNodeService-Agent'");
            this.idConfigTC =
                    new TucsonTupleCentreId("'$ORG'", "localhost",
                            String.valueOf(this.tcpPort));
            this.idObsTC =
                    new TucsonTupleCentreId("'$OBS'", "localhost",
                            String.valueOf(this.tcpPort));
        } catch (final TucsonInvalidAgentIdException e) {
            e.printStackTrace();
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
        }

        this.observed = false;
        this.agents = new ArrayList<TucsonAgentId>();
        this.nodeAgents = new ArrayList<Thread>();
        this.tcs = new ArrayList<RespectTC>();

    }

    /**
     * 
     */
    public synchronized void activateObservability() {
        this.observed = true;
        final Iterator<TucsonTCUsers> it = this.cores.values().iterator();
        try {
            while (it.hasNext()) {
                final TucsonTCUsers tc = it.next();
                TupleCentreContainer.doManagementOperation(
                        TucsonOperation.addObsCode(),
                        tc.getTucsonTupleCentreId(), this.obsService);
            }
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
        } catch (final TucsonInvalidLogicTupleException e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param aid
     *            the identifier of the agent to add to this TuCSoN node
     */
    public synchronized void addAgent(final TucsonAgentId aid) {

        boolean present = false;
        if (!this.agents.contains(aid)) {
            this.agents.add(aid);
        } else {
            present = true;
        }

        if (this.observed && !present) {
            this.obsService.accEntered(aid);
        }

    }

    /**
     * 
     * @param t
     *            the identifier of the internal management agent to add to this
     *            TuCSoN node
     */
    public synchronized void addNodeAgent(final Thread t) {
        this.nodeAgents.add(t);
    }

    /**
     * 
     * @param agentId
     *            the identifier of the tuple centre agent to add to this TuCSoN
     *            node
     * @param tid
     *            the identifier of the tuple centre whose agent has to be added
     */
    // why another slightly different method to add an agent? is this for
    // inter-tc agents?
    public void addTCAgent(final TucsonAgentId agentId,
            final TucsonTupleCentreId tid) {
        this.cores.get(tid.getName()).addUser(agentId);
    }

    /**
     * 
     */
    public synchronized void deactivateObservability() {
        this.observed = false;
        final Iterator<TucsonTCUsers> it = this.cores.values().iterator();
        try {
            while (it.hasNext()) {
                final TucsonTCUsers tc = it.next();
                TupleCentreContainer.doManagementOperation(
                        TucsonOperation.rmvObsCode(),
                        tc.getTucsonTupleCentreId(), this.obsService);
            }
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
        } catch (final TucsonInvalidLogicTupleException e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param tcn
     *            the String representing the tuple centre identifier to destroy
     * @return wether the operation has been succesfully carried out or not
     */
    // exception handling is a mess, review it...
    public synchronized boolean destroyCore(final String tcn) {

        final StringBuffer tcName = new StringBuffer(tcn);

        if (tcn.indexOf('@') < 0) {
            tcName.append("@localhost");
        }
        if (tcn.indexOf(':') < 0) {
            tcName.append(this.tcpPort);
        }

        TucsonTupleCentreId tid;
        try {
            tid = new TucsonTupleCentreId(tcName.toString());
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
            return false;
        }
        final String realName = tcName.toString();
        final TucsonTupleCentreId core =
                this.cores.get(realName).getTucsonTupleCentreId();

        if (core != null) {

            TucsonNodeService.log("Destroying tuple centre < " + realName
                    + " >...");
            TupleCentreContainer.destroyTC();
            if (this.observed) {
                this.obsService.tcDestroyed(tid);
            }

            try {
                final TupleArgument tcArg = TupleArgument.parse(realName);
                TupleCentreContainer.doBlockingOperation(
                        TucsonOperation.inpCode(), this.nodeAid,
                        this.idConfigTC, new LogicTuple("tuple_centre", tcArg));
                TupleCentreContainer.doBlockingOperation(TucsonOperation
                        .inpCode(), this.nodeAid, this.idConfigTC,
                        new LogicTuple("is_persistent", new Value(realName)));
            } catch (final TucsonOperationNotPossibleException e) {
                e.printStackTrace();
            } catch (final InvalidTupleArgumentException e) {
                e.printStackTrace();
            } catch (final TucsonInvalidLogicTupleException e) {
                e.printStackTrace();
            }

            this.cores.remove(realName);
            return true;

        }
        return false;

    }

    /**
     * 
     * @param tc
     *            the identifier of the tuple centre whose persistency service
     *            should be disabled
     */
    public synchronized void disablePersistence(final String tc) {

        final TucsonTCUsers tar = this.cores.get(tc);
        TupleCentreContainer.disablePersistence();
        try {
            TupleCentreContainer.doBlockingOperation(TucsonOperation.inCode(),
                    this.nodeAid, tar.getTucsonTupleCentreId(), new LogicTuple(
                            "is_persistent", new Value(tar
                                    .getTucsonTupleCentreId().getName())));
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
        } catch (final TucsonInvalidLogicTupleException e) {
            e.printStackTrace();
        }

    }

    /**
     * 
     * @param template
     *            the tuple template to be used in filtering tuple centre
     *            identifiers whose persistency service should be disabled
     */
    public synchronized void disablePersistence(final TupleArgument template) {

        if (this.persistencyTemplate != null) {

            final Iterator<TucsonTCUsers> it = this.cores.values().iterator();
            while (it.hasNext()) {
                final TucsonTCUsers tc = it.next();
                try {
                    final TupleArgument tid =
                            TupleArgument.parse(tc.getTucsonTupleCentreId()
                                    .getName());
                    if (template.match(tid)) {
                        TupleCentreContainer.disablePersistence();
                        TupleCentreContainer.doBlockingOperation(
                                TucsonOperation.inCode(), this.nodeAid, tc
                                        .getTucsonTupleCentreId(),
                                new LogicTuple("is_persistent", new Value(tc
                                        .getTucsonTupleCentreId().getName())));
                    }
                } catch (final InvalidTupleArgumentException e) {
                    e.printStackTrace();
                } catch (final TucsonOperationNotPossibleException e) {
                    e.printStackTrace();
                } catch (final TucsonInvalidLogicTupleException e) {
                    e.printStackTrace();
                }
            }
            if (this.persistencyTemplate.match(template)) {
                this.persistencyTemplate = null;
            }
        }

    }

    /**
     * 
     * @param tc
     *            the identifier of the tuple centre whose persistency service
     *            should be enabled
     */
    public synchronized void enablePersistence(final String tc) {

        final TucsonTCUsers tar = this.cores.get(tc);
        TupleCentreContainer.enablePersistence();
        try {
            TupleCentreContainer.doBlockingOperation(TucsonOperation.outCode(),
                    this.nodeAid, tar.getTucsonTupleCentreId(), new LogicTuple(
                            "is_persistent", new Value(tar
                                    .getTucsonTupleCentreId().getName())));
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
        } catch (final TucsonInvalidLogicTupleException e) {
            e.printStackTrace();
        }

    }

    /**
     * 
     * @param template
     *            the tuple template to be used in filtering tuple centre
     *            identifiers whose persistency service should be enabled
     */
    public synchronized void enablePersistence(final TupleArgument template) {

        this.persistencyTemplate = template;
        final Iterator<TucsonTCUsers> it = this.cores.values().iterator();
        while (it.hasNext()) {
            final TucsonTCUsers tc = it.next();
            try {
                final TupleArgument tid =
                        TupleArgument.parse(tc.getTucsonTupleCentreId()
                                .getName());
                if (template.match(tid)) {
                    TupleCentreContainer.enablePersistence();
                    TupleCentreContainer.doBlockingOperation(TucsonOperation
                            .outCode(), this.nodeAid, tc
                            .getTucsonTupleCentreId(), new LogicTuple(
                            "is_persistent", new Value(tc
                                    .getTucsonTupleCentreId().getName())));
                }
            } catch (final InvalidTupleArgumentException e) {
                e.printStackTrace();
            } catch (final TucsonOperationNotPossibleException e) {
                e.printStackTrace();
            } catch (final TucsonInvalidLogicTupleException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 
     * @return a Map storing associations between String representations of
     *         tuple centres along with the list of their users
     */
    public Map<String, TucsonTCUsers> getCores() {
        return this.cores;
    }

    /**
     * 
     * @return the date when the TuCSoN node was installed
     */
    public Date getInstallationDate() {
        return this.installationDate;
    }

    /**
     * 
     * @return the observer of the TuCSoN node, if any
     */
    public NodeServiceListener getListener() {
        return this.obsService;
    }

    /**
     * 
     * @return the listening port this TuCSoN node is bound to
     */
    public int getTCPPort() {
        return this.tcpPort;
    }

    /**
     * 
     */
    public synchronized void install() {

        TucsonNodeService.log("Beginning TuCSoN Node Service installation...");
        TucsonNodeService.log(new Date().toString());

        this.configManager = new Prolog();
        this.cores = new HashMap<String, TucsonTCUsers>();

        TucsonNodeService.log("Configuring TuCSoN Node Service...");
        try {
            this.setupConfiguration(this.configFile);
        } catch (final TucsonGenericException e) {
            e.printStackTrace();
        } catch (final InvalidConfigException e) {
            e.printStackTrace();
        }
        TucsonNodeService.log("Setting up Observation Service...");
        this.setupObsTupleCentre();
        TucsonNodeService.log("Setting up Management Service...");
        this.setupConfigTupleCentre();
        this.checkPersistentTupleCentres(TucsonNodeService.PERSISTENCY_PATH);

        this.installationDate = new Date();

        TucsonNodeService.log("Spawning management agents...");
        this.bootManagementAgents();

    }

    /**
     * 
     * @param aid
     *            the identifier of the TuCSoN agent to be removed from users
     */
    public synchronized void removeAgent(final TucsonAgentId aid) {

        boolean present = true;
        if (this.agents.contains(aid)) {
            this.agents.remove(aid);
        } else {
            present = false;
        }

        if (this.observed && present) {
            this.obsService.accQuit(aid);
        }

        final Iterator<TucsonTCUsers> it = this.cores.values().iterator();
        while (it.hasNext()) {
            it.next().removeUser(aid);
        }

    }

    /**
     * 
     * @param t
     *            the Thread object executing the internal management agent to
     *            be removed
     */
    public synchronized void removeNodeAgent(final Thread t) {
        this.nodeAgents.remove(t);
    }

    /**
     * @param tcn
     *            the String representation of the tuple centre whose usage
     *            associations should be retrieved
     * @return the Object representing associations between agents and tuple
     *         centre they're using
     */
    public synchronized TucsonTCUsers resolveCore(final String tcn) {

        final StringBuffer tcName = new StringBuffer(tcn);

        if (tcn.indexOf('@') < 0) {
            tcName.append("@localhost");
        }
        if (tcn.indexOf(':') < 0) {
            tcName.append(this.tcpPort);
        }

        TucsonTupleCentreId tid;
        try {
            tid = new TucsonTupleCentreId(tcName.toString());
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
            return null;
        }
        final String realName = tid.getName();
        TucsonTCUsers core = this.cores.get(realName);

        if (core == null) {

            TucsonNodeService.log("Booting new tuple centre < " + realName
                    + " >...");
            try {
                core = this.bootTupleCentre(realName);
            } catch (final TucsonInvalidTupleCentreIdException e) {
                e.printStackTrace();
                return null;
            } catch (final TCInstantiationNotPossibleException e) {
                e.printStackTrace();
                return null;
            }

        }

        return core;

    }

    /**
     * 
     */
    public void shutdown() {
        TucsonNodeService
                .log("Node is shutting down management agents and proxies...");
        for (final Thread t : this.nodeAgents) {
            if (t.isAlive()) {
                TucsonNodeService.log("  ...shutting down <" + t.getName()
                        + ">");
                t.interrupt();
            } else {
                TucsonNodeService.log("  ...<" + t.getName()
                        + "> is already dead");
            }
        }
        this.welcome.shutdown();
        try {
            this.ctxman.shutdown();
        } catch (final InterruptedException e) {
            TucsonNodeService
                    .log("ACCProvider may still have tasks executing...");
        }
        TucsonNodeService.log("Node is shutting down ReSpecT VMs...");
        for (final RespectTC tc : this.tcs) {
            final Thread t = tc.getVMThread();
            if (t.isAlive()) {
                TucsonNodeService
                        .log("  ...shutting down <" + tc.getId() + ">");
                t.interrupt();
            } else {
                TucsonNodeService.log("  ...<" + tc.getId()
                        + "> is already dead");
            }
        }
        TucsonNodeService.log("TuCSoN Node shutdown completed, see you :)");
    }

    /**
     * 
     */
    private void bootManagementAgents() {

        TucsonNodeService.log("Spawning Node Management Agent...");
        this.nodeAgents.add(new NodeManagementAgent(this.idConfigTC, this));

        TucsonNodeService
                .log("--------------------------------------------------------------------------------");
        TucsonNodeService.log("Spawning ACC Provider Agent...");
        this.ctxman = new ACCProvider(this, this.idConfigTC);

        TucsonNodeService.log("Spawning Welcome Agent...");
        this.welcome = new WelcomeAgent(this, this.ctxman);

    }

    /**
     * 
     * @param name
     * @return
     * @throws TucsonInvalidTupleCentreIdException
     * @throws TCInstantiationNotPossibleException
     */
    private TucsonTCUsers bootTupleCentre(final String n)
            throws TucsonInvalidTupleCentreIdException,
            TCInstantiationNotPossibleException {

        final StringBuffer name = new StringBuffer(n);

        if (n.indexOf('@') < 0) {
            name.append("@localhost");
        }
        if (n.indexOf(':') < 0) {
            name.append(':').append(this.tcpPort);
        }

        final TucsonTupleCentreId id = new TucsonTupleCentreId(name.toString());
        try {
            this.tcs.add(TupleCentreContainer.createTC(id,
                    TucsonNodeService.MAX_EVENT_QUEUE_SIZE, this.tcpPort));
        } catch (final InvalidTupleCentreIdException e) {
            TucsonNodeService.log("TupleCentreContainer.createTC(...) error");
            e.printStackTrace();
        }

        if (this.observed) {
            try {
                TupleCentreContainer.doManagementOperation(
                        TucsonOperation.addObsCode(), id, this.obsService);
            } catch (final TucsonOperationNotPossibleException e) {
                e.printStackTrace();
            } catch (final TucsonInvalidLogicTupleException e) {
                e.printStackTrace();
            }
            this.obsService.tcCreated(id);
        }

        final TucsonTCUsers tcUsers = new TucsonTCUsers(id);
        this.cores.put(id.getName(), tcUsers);

        return tcUsers;

    }

    /**
     * 
     * @param dirName
     */
    private void checkPersistentTupleCentres(final String dirName) {

        final File dir = new File(dirName);
        if (dir.exists() && dir.isDirectory()) {
            final String[] files = dir.list();
            for (final String file : files) {
                if (file.startsWith("tc_") && file.endsWith(".dat")) {
                    final String tcName = file.substring(3, file.length() - 4);
                    try {
                        this.bootTupleCentre(tcName);
                    } catch (final TucsonInvalidTupleCentreIdException e) {
                        e.printStackTrace();
                    } catch (final TCInstantiationNotPossibleException e) {
                        e.printStackTrace();
                    }
                    TupleCentreContainer.loadPersistentInformation();
                    TupleCentreContainer.enablePersistence();
                    try {
                        TupleCentreContainer
                                .doBlockingOperation(TucsonOperation.outCode(),
                                        this.nodeAid, this.cores.get(tcName)
                                                .getTucsonTupleCentreId(),
                                        new LogicTuple("is_persistent",
                                                new Value(tcName)));
                    } catch (final TucsonOperationNotPossibleException e) {
                        e.printStackTrace();
                    } catch (final TucsonInvalidLogicTupleException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            dir.mkdir();
        }

    }

    /**
     * 
     */
    private void setupConfigTupleCentre() {

        try {
            this.bootTupleCentre(this.idConfigTC.getName());
            final InputStream is =
                    Thread.currentThread()
                            .getContextClassLoader()
                            .getResourceAsStream(
                                    TucsonNodeService.DEFAULT_BOOT_SPEC_FILE);
            final String spec =
                    alice.util.Tools.loadText(new BufferedInputStream(is));
            final LogicTuple specTuple =
                    new LogicTuple("spec", new Value(spec));
            TupleCentreContainer.doBlockingSpecOperation(
                    TucsonOperation.setSCode(), this.nodeAid, this.idConfigTC,
                    specTuple);
            TupleCentreContainer.doNonBlockingOperation(
                    TucsonOperation.outCode(), this.nodeAid, this.idConfigTC,
                    new LogicTuple("boot"), null);
            this.addAgent(this.nodeAid);
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
        } catch (final TCInstantiationNotPossibleException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
        } catch (final TucsonInvalidLogicTupleException e) {
            e.printStackTrace();
        } catch (final TucsonInvalidSpecificationException e) {
            e.printStackTrace();
        }

    }

    /**
     * 
     * @param conf
     * @throws TucsonGenericException
     * @throws InvalidConfigException
     */
    // exception handling is a mess, review it...
    private void setupConfiguration(final String conf)
            throws TucsonGenericException, InvalidConfigException {

        final alice.tuprolog.lib.JavaLibrary jlib =
                (alice.tuprolog.lib.JavaLibrary) this.configManager
                        .getLibrary("alice.tuprolog.lib.JavaLibrary");
        try {
            jlib.register(new alice.tuprolog.Struct("config"), this);
        } catch (final InvalidObjectIdException e) {
            throw new TucsonGenericException(
                    "Internal Failure: loading JavaLibrary in Prolog Configuration Engine failed.");
        }

        if (conf != null) {

            try {
                final InputStream is =
                        Thread.currentThread()
                                .getContextClassLoader()
                                .getResourceAsStream(
                                        TucsonNodeService.BOOT_SETUP_THEORY);
                final Theory setupTh = new Theory(new BufferedInputStream(is));
                this.configManager.setTheory(setupTh);
            } catch (final alice.tuprolog.InvalidTheoryException ex) {
                throw new TucsonGenericException(
                        "Internal Failure: invalid Prolog Setup Engine theory.");
            } catch (final IOException ex) {
                throw new TucsonGenericException(
                        "Internal Failure: loading Prolog Setup Engine theory failed.");
            }

            Theory cfgTh;
            alice.tuprolog.SolveInfo info;
            try {
                cfgTh = new Theory(new FileInputStream(conf));
                this.configManager.addTheory(cfgTh);
                info = this.configManager.solve("setup.");
            } catch (final FileNotFoundException e) {
                throw new TucsonGenericException(
                        "Internal Failure: Prolog Configuration Engine theory not found.");
            } catch (final IOException e) {
                throw new TucsonGenericException(
                        "Internal Failure: loading Prolog Configuration Engine theory failed.");
            } catch (final InvalidTheoryException e) {
                throw new InvalidConfigException();
            } catch (final MalformedGoalException e) {
                throw new TucsonGenericException(
                        "Internal Failure: solving Prolog Configuration Engine theory failed.");
            }

            if (!info.isSuccess()) {
                throw new InvalidConfigException();
            }

        }

    }

    /**
     * 
     */
    private void setupObsTupleCentre() {

        try {
            this.bootTupleCentre(this.idObsTC.getName());
            final InputStream is =
                    Thread.currentThread()
                            .getContextClassLoader()
                            .getResourceAsStream(
                                    TucsonNodeService.DEFAULT_OBS_SPEC_FILE);
            final String spec =
                    alice.util.Tools.loadText(new BufferedInputStream(is));
            final LogicTuple specTuple =
                    new LogicTuple("spec", new Value(spec));
            TupleCentreContainer.doBlockingSpecOperation(
                    TucsonOperation.setSCode(), this.nodeAid, this.idObsTC,
                    specTuple);
            TupleCentreContainer.doNonBlockingOperation(
                    TucsonOperation.outCode(), this.nodeAid, this.idObsTC,
                    new LogicTuple("boot"), null);
            this.obsService = new ObservationService(this.idObsTC);
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
        } catch (final TCInstantiationNotPossibleException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
        } catch (final TucsonInvalidLogicTupleException e) {
            e.printStackTrace();
        } catch (final TucsonInvalidSpecificationException e) {
            e.printStackTrace();
        }

    }

}
