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
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.MalformedGoalException;
import alice.tuprolog.Prolog;
import alice.tuprolog.Theory;
import alice.tuprolog.lib.InvalidObjectIdException;

/**
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

    public static String getVersion() {
        return "TuCSoN-1.10.3.0207";
    }

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
     */
    public static void main(final String args[]) {

        if (alice.util.Tools.isOpt(args, "-help")
                || alice.util.Tools.isOpt(args, "-?")) {
            TucsonNodeService
                    .log("Arguments: -portno {portNumber} {-? | -help}");
        } else {

            final String portInfo = alice.util.Tools.getOpt(args, "-portno");
            final String configInfo = alice.util.Tools.getOpt(args, "-config");
            final String persistencyInfo =
                    alice.util.Tools.getOpt(args, "-persistency");

            int portNumber = TucsonNodeService.DEFAULT_TCP_PORT;
            if (portInfo != null) {
                try {
                    portNumber = Integer.parseInt(portInfo);
                } catch (final NumberFormatException e) {
                    System.err.println("Invalid port number");
                    System.exit(-1);
                }
                if ((portNumber < 0) || (portNumber > 64000)) {
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

            try {
                new TucsonNodeService(configInfo, portNumber, template)
                        .install();
            } catch (final TucsonGenericException e) {
                System.err.println("Tucson Service Node installation failed: "
                        + e.getMsg());
                System.exit(-1);
            }

        }

    }

    private static void log(final String m) {
        System.out.println("[TuCSoN Node Service]: " + m);
    }

    private final ArrayList<TucsonAgentId> agents;
    private final String configFile;
    private Prolog configManager;
    private HashMap<String, TucsonTCUsers> cores;
    private ACCProvider ctxman;
    private TucsonTupleCentreId idConfigTC;
    private TucsonTupleCentreId idObsTC;

    private Date installationDate;

    private final ArrayList<Thread> nodeAgents;
    private TucsonAgentId nodeAid;
    private boolean observed;
    private ObservationService obsService;

    private TupleArgument persistencyTemplate;

    private int tcp_port = TucsonNodeService.DEFAULT_TCP_PORT;

    private final ArrayList<RespectTC> tcs;

    private WelcomeAgent welcome;

    public TucsonNodeService() throws TucsonGenericException {
        this(null, TucsonNodeService.DEFAULT_TCP_PORT, null);
    }

    public TucsonNodeService(final int portno) throws TucsonGenericException {
        this(null, portno, null);
    }

    /**
     * 
     * @param conf
     * @param portNumber
     * @param persistTempl
     * @throws TucsonGenericException
     */
    public TucsonNodeService(final String conf, final int portNumber,
            final TupleArgument persistTempl) throws TucsonGenericException {

        TucsonNodeService
                .log("--------------------------------------------------------------------------------");
        TucsonNodeService.log("Welcome to the TuCSoN infrastructure :)");
        TucsonNodeService.log("Version " + TucsonNodeService.getVersion());
        TucsonNodeService
                .log("--------------------------------------------------------------------------------");

        this.configFile = conf;
        this.tcp_port = portNumber;
        this.persistencyTemplate = persistTempl;

        try {
            this.nodeAid = new TucsonAgentId("'$TucsonNodeService-Agent'");
            this.idConfigTC =
                    new TucsonTupleCentreId("'$ORG'", "localhost", ""
                            + this.tcp_port);
            this.idObsTC =
                    new TucsonTupleCentreId("'$OBS'", "localhost", ""
                            + this.tcp_port);
        } catch (final TucsonInvalidAgentIdException e) {
            System.err.println("[TucsonNodeService]: " + e);
            e.printStackTrace();
        } catch (final TucsonInvalidTupleCentreIdException e) {
            System.err.println("[TucsonNodeService]: " + e);
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
    synchronized public void activateObservability() {
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
            System.err.println("[TucsonNodeService]: " + e);
            e.printStackTrace();
        } catch (final TucsonInvalidLogicTupleException e) {
            System.err.println("[TucsonNodeService]: " + e);
            e.printStackTrace();
        }
    }

    synchronized public void addAgent(final TucsonAgentId aid) {

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

    synchronized public void addNodeAgent(final Thread t) {
        this.nodeAgents.add(t);
    }

    /**
     * 
     * @param agentId
     * @param tid
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
    synchronized public void deactivateObservability() {
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
            System.err.println("[TucsonNodeService]: " + e);
            e.printStackTrace();
        } catch (final TucsonInvalidLogicTupleException e) {
            System.err.println("[TucsonNodeService]: " + e);
            e.printStackTrace();
        }
    }

    /**
     * 
     * @return
     */
    // exception handling is a mess, review it...
    public synchronized boolean destroyCore(String tcn) {

        String tcName = tcn;

        if (tcName.indexOf("@") < 0) {
            tcName += "@localhost";
        }
        if (tcName.indexOf(":") < 0) {
            tcName += this.tcp_port;
        }

        TucsonTupleCentreId tid;
        try {
            tid = new TucsonTupleCentreId(tcName);
        } catch (final TucsonInvalidTupleCentreIdException e) {
            System.err.println("[TucsonNodeService]: " + e);
            e.printStackTrace();
            return false;
        }
        final String realName = tcName;
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
                try {
                    final TupleArgument tcArg = TupleArgument.parse(tcName);
                    TupleCentreContainer.doBlockingOperation(TucsonOperation
                            .inpCode(), this.nodeAid, this.idConfigTC,
                            new LogicTuple("tuple_centre", tcArg));
                    TupleCentreContainer.doBlockingOperation(TucsonOperation
                            .inpCode(), this.nodeAid, this.idConfigTC,
                            new LogicTuple("is_persistent", new Value(tcName)));
                } catch (final Exception ex) {
                    TupleCentreContainer.doBlockingOperation(TucsonOperation
                            .inpCode(), this.nodeAid, this.idConfigTC,
                            new LogicTuple("tuple_centre", new Value(tcName)));
                }
            } catch (final Exception e) {
                System.err.println("[TucsonNodeService]: " + e);
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
     */
    public synchronized void disablePersistence(final String tc) {

        final TucsonTCUsers tar = this.cores.get(tc);
        TupleCentreContainer.disablePersistence();
        try {
            TupleCentreContainer.doBlockingOperation(TucsonOperation.inCode(),
                    this.nodeAid, tar.getTucsonTupleCentreId(), new LogicTuple(
                            "is_persistent", new Value(tar
                                    .getTucsonTupleCentreId().getName())));
        } catch (final Exception e) {
            System.err.println("[TucsonNodeService]: " + e);
            e.printStackTrace();
        }

    }

    /**
     * 
     * @param template
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
                    System.err.println("[TucsonNodeService]: " + e);
                    e.printStackTrace();
                } catch (final Exception e) {
                    System.err.println("[TucsonNodeService]: " + e);
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
     */
    public synchronized void enablePersistence(final String tc) {

        final TucsonTCUsers tar = this.cores.get(tc);
        TupleCentreContainer.enablePersistence();
        try {
            TupleCentreContainer.doBlockingOperation(TucsonOperation.outCode(),
                    this.nodeAid, tar.getTucsonTupleCentreId(), new LogicTuple(
                            "is_persistent", new Value(tar
                                    .getTucsonTupleCentreId().getName())));
        } catch (final Exception e) {
            System.err.println("[TucsonNodeService]: " + e);
            e.printStackTrace();
        }

    }

    /**
     * 
     * @param template
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
                System.err.println("[TucsonNodeService]: " + e);
                e.printStackTrace();
            } catch (final Exception e) {
                System.err.println("[TucsonNodeService]: " + e);
                e.printStackTrace();
            }
        }

    }

    public HashMap<String, TucsonTCUsers> getCores() {
        return this.cores;
    }

    public Date getInstallationDate() {
        return this.installationDate;
    }

    public NodeServiceListener getListener() {
        return this.obsService;
    }

    public int getTCPPort() {
        return this.tcp_port;
    }

    public synchronized void install() {
        this.install(null);
    }

    /**
     * 
     * @param repo
     */
    public synchronized void install(final NodeInstallationReport repo) {

        TucsonNodeService.log("Beginning TuCSoN Node Service installation...");
        TucsonNodeService.log("" + new Date());

        this.configManager = new Prolog();
        this.cores = new HashMap<String, TucsonTCUsers>();

        TucsonNodeService.log("Configuring TuCSoN Node Service...");
        try {
            this.setupConfiguration(this.configFile);
        } catch (final TucsonGenericException e) {
            if (repo != null) {
                repo.setReport(false, "Failed", null);
            }
            System.err.println("[TucsonNodeService]: " + e);
            e.printStackTrace();
        } catch (final InvalidConfigException e) {
            if (repo != null) {
                repo.setReport(false, "Failed", null);
            }
            System.err.println("[TucsonNodeService]: " + e);
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

        if (repo != null) {
            repo.setReport(true, "Installed", this.installationDate);
        }

    }

    synchronized public void removeAgent(final TucsonAgentId aid) {

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

    synchronized public void removeNodeAgent(final Thread t) {
        this.nodeAgents.remove(t);
    }

    /**
     * 
     * @return
     */
    public synchronized TucsonTCUsers resolveCore(String tcn) {

        String tcName = tcn;

        if (tcName.indexOf("@") < 0) {
            tcName += "@localhost";
        }
        if (tcName.indexOf(":") < 0) {
            tcName += this.tcp_port;
        }

        TucsonTupleCentreId tid;
        try {
            tid = new TucsonTupleCentreId(tcName);
        } catch (final TucsonInvalidTupleCentreIdException e) {
            System.err.println("[TucsonNodeService]: " + e);
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
                System.err.println("[TucsonNodeService]: " + e);
                e.printStackTrace();
                return null;
            } catch (final TCInstantiationNotPossibleException e) {
                System.err.println("[TucsonNodeService]: " + e);
                e.printStackTrace();
                return null;
            }

        }

        return core;

    }

    /*
     * Caller too is killed -.-
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
        this.welcome = new WelcomeAgent(this.tcp_port, this, this.ctxman);

    }

    /**
     * 
     * @param name
     * @return
     * @throws TucsonInvalidTupleCentreIdException
     * @throws TCInstantiationNotPossibleException
     */
    private TucsonTCUsers bootTupleCentre(String n)
            throws TucsonInvalidTupleCentreIdException,
            TCInstantiationNotPossibleException {

        String name = n;

        if (name.indexOf("@") < 0) {
            name += "@localhost";
        }
        if (name.indexOf(":") < 0) {
            name += (":" + this.tcp_port);
        }

        final TucsonTupleCentreId id = new TucsonTupleCentreId(name);
        try {
            this.tcs.add(TupleCentreContainer.createTC(id,
                    TucsonNodeService.MAX_EVENT_QUEUE_SIZE, this.tcp_port));
        } catch (final InvalidTupleCentreIdException e1) {
            TucsonNodeService.log("TupleCentreContainer.createTC(...) error");
            e1.printStackTrace();
        }

        if (this.observed) {
            try {
                TupleCentreContainer.doManagementOperation(
                        TucsonOperation.addObsCode(), id, this.obsService);
            } catch (final TucsonOperationNotPossibleException e) {
                System.err.println("[TucsonNodeService]: " + e);
                e.printStackTrace();
            } catch (final TucsonInvalidLogicTupleException e) {
                System.err.println("[TucsonNodeService]: " + e);
                e.printStackTrace();
            }
            this.obsService.tcCreated(id);
        }

        final TucsonTCUsers tcUsers = new TucsonTCUsers(id);
        this.cores.put(id.getName(), tcUsers);

        // try {
        // TupleArgument tid = Value.parse(name);
        // if (persistencyTemplate != null && persistencyTemplate.match(tid)) {
        // log("Enabling persistency on tuple centre < " + name + " >...");
        // TupleCentreContainer.enablePersistence(id, PERSISTENCY_PATH
        // + "tc_" + tid.toString() + ".dat");
        // TupleCentreContainer.out(nodeAid, id, new LogicTuple(
        // "is_persistent", new Value(name)));
        // }
        // } catch (Exception e) {
        // System.err.println("[TucsonNodeService]: " + e);
        // e.printStackTrace();
        // }

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
                        System.err.println("[TucsonNodeService]: " + e);
                        e.printStackTrace();
                    } catch (final TCInstantiationNotPossibleException e) {
                        System.err.println("[TucsonNodeService]: " + e);
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
                    } catch (final Exception e) {
                        System.err.println("[TucsonNodeService]: " + e);
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
                    TucsonOperation.set_sCode(), this.nodeAid, this.idConfigTC,
                    specTuple);
            TupleCentreContainer.doNonBlockingOperation(
                    TucsonOperation.outCode(), this.nodeAid, this.idConfigTC,
                    new LogicTuple("boot"), null);
            this.addAgent(this.nodeAid);
        } catch (final TucsonInvalidTupleCentreIdException e) {
            System.err.println("[TucsonNodeService]: " + e);
            e.printStackTrace();
        } catch (final TCInstantiationNotPossibleException e) {
            System.err.println("[TucsonNodeService]: " + e);
            e.printStackTrace();
        } catch (final IOException e) {
            System.err.println("[TucsonNodeService]: " + e);
            e.printStackTrace();
        } catch (final Exception e) {
            System.err.println("[TucsonNodeService]: " + e);
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
                final Theory setup_th = new Theory(new BufferedInputStream(is));
                this.configManager.setTheory(setup_th);
            } catch (final alice.tuprolog.InvalidTheoryException ex) {
                throw new TucsonGenericException(
                        "Internal Failure: invalid Prolog Setup Engine theory.");
            } catch (final IOException ex) {
                throw new TucsonGenericException(
                        "Internal Failure: loading Prolog Setup Engine theory failed.");
            }

            Theory cfg_th;
            alice.tuprolog.SolveInfo info;
            try {
                cfg_th = new Theory(new FileInputStream(conf));
                this.configManager.addTheory(cfg_th);
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
                    TucsonOperation.set_sCode(), this.nodeAid, this.idObsTC,
                    specTuple);
            TupleCentreContainer.doNonBlockingOperation(
                    TucsonOperation.outCode(), this.nodeAid, this.idObsTC,
                    new LogicTuple("boot"), null);
            this.obsService = new ObservationService(this.idObsTC);
        } catch (final TucsonInvalidTupleCentreIdException e) {
            System.err.println("[TucsonNodeService]: " + e);
            e.printStackTrace();
        } catch (final TCInstantiationNotPossibleException e) {
            System.err.println("[TucsonNodeService]: " + e);
            e.printStackTrace();
        } catch (final IOException e) {
            System.err.println("[TucsonNodeService]: " + e);
            e.printStackTrace();
        } catch (final Exception e) {
            System.err.println("[TucsonNodeService]: " + e);
            e.printStackTrace();
        }

    }

}
