package alice.respect.core;

import java.lang.reflect.InvocationTargetException;
import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.respect.api.TupleCentreId;
import alice.respect.api.exceptions.InvalidTupleCentreIdException;
import alice.respect.situatedness.AbstractProbeId;
import alice.respect.situatedness.ActuatorId;
import alice.respect.situatedness.ISimpleProbe;
import alice.respect.situatedness.SensorId;
import alice.respect.situatedness.TransducerId;
import alice.tucson.api.AbstractTucsonAgent;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.SynchACC;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.exceptions.InvalidOperationException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;
import alice.tuplecentre.core.AbstractTupleCentreOperation;

/**
 *
 * Environment configuration agent.
 *
 * It checks for requests on '$ENV' and delegates them to the TransducerManager.
 *
 * @author Steven Maraldi
 *
 */
public class EnvConfigAgent extends AbstractTucsonAgent {

    /** Add an actuator request's type **/
    private static final String ADD_ACTUATOR = "addActuator";
    /** Add a sensor request's type **/
    private static final String ADD_SENSOR = "addSensor";
    /** Change the transducer associated to a resource request's type **/
    private static final String CHANGE_TRANSDUCER = "changeTransducer";
    /** Create transducer actuator request's type **/
    private static final String CREATE_TRANSDUCER_ACTUATOR = "createTransducerActuator";
    /** Create transducer sensor request's type **/
    private static final String CREATE_TRANSDUCER_SENSOR = "createTransducerSensor";
    /** Remove a resource request's type **/
    private static final String REMOVE_RESOURCE = "removeResource";
    /** The tuple centre used for environment configuration **/
    private TupleCentreId idEnvTC;
    private boolean iteraction = true;

    /**
     *
     * @param ipAddress
     *            the netid of the TuCSoN Node this environment configuration
     *            agent works with
     * @param portno
     *            the listening port of the TuCSoN Node this environment
     *            configuration agent works with
     * @throws TucsonInvalidAgentIdException
     *             this cannot actually happen, since this agent identifier is
     *             given, then well-formed
     */
    public EnvConfigAgent(final String ipAddress, final int portno)
            throws TucsonInvalidAgentIdException {
        super("'$EnvAgent'", ipAddress, portno);
        try {
            this.idEnvTC = new TupleCentreId("'$ENV'", ipAddress,
                    String.valueOf(portno));
        } catch (final InvalidTupleCentreIdException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.go();
    }

    @Override
    public void main() {
        final SynchACC acc = this.getContext();
        while (this.iteraction) {
            try {
                // Gets the command from the tuple space
                LogicTuple t = LogicTuple.parse("cmd(Type)");
                t = acc.in(this.idEnvTC, t, null).getLogicTupleResult();
                if (EnvConfigAgent.CREATE_TRANSDUCER_SENSOR.equals(t.getArg(0)
                        .toString())) {
                    t = LogicTuple
                            .parse("createTransducerSensor(Tcid,Tclass,Tid,Pclass,Pid)");
                    t = acc.in(this.idEnvTC, t, null).getLogicTupleResult();
                    // Obtaining transducer
                    final TransducerId tId = new TransducerId(t.getArg(2)
                            .getName());
                    // Obtaining tuple centre and its properties
                    final String[] sTcId = t.getArg(0).toString().split(":"); // '@'(name,':'(node,port))
                    final String tcName = sTcId[0].substring(
                            sTcId[0].indexOf('(') + 1, sTcId[0].indexOf(','));
                    final String[] tcNodeAndPort = sTcId[1].substring(
                            sTcId[1].indexOf('(') + 1, sTcId[1].indexOf(')'))
                            .split(",");
                    final TupleCentreId tcId = new TupleCentreId(tcName,
                            tcNodeAndPort[0], tcNodeAndPort[1]);
                    this.speak("Serving 'createTransducer' request < TransducerId="
                            + tId
                            + ", associated TC="
                            + t.getArg(0).toString()
                            + " >...");
                    // Obtaining resource
                    final AbstractProbeId pId = new SensorId(t.getArg(4)
                            .getName());
                    ProbesManager.INSTANCE.createProbe(t.getArg(3).toString(),
                            pId);
                    final TransducersManager tm = TransducersManager.INSTANCE;
                    tm.createTransducer(t.getArg(1).toString(), tId, tcId, pId);
                } else if (EnvConfigAgent.CREATE_TRANSDUCER_ACTUATOR.equals(t
                        .getArg(0).toString())) {
                    t = LogicTuple
                            .parse("createTransducerActuator(Tcid,Tclass,Tid,Pclass,Pid)");
                    t = acc.in(this.idEnvTC, t, null).getLogicTupleResult();
                    // Obtaining transducer
                    final TransducerId tId = new TransducerId(t.getArg(2)
                            .getName());
                    // Obtaining tuple centre and its properties
                    final String[] sTcId = t.getArg(0).toString().split(":"); // '@'(name,':'(node,port))
                    final String tcName = sTcId[0].substring(
                            sTcId[0].indexOf('(') + 1, sTcId[0].indexOf(','));
                    final String[] tcNodeAndPort = sTcId[1].substring(
                            sTcId[1].indexOf('(') + 1, sTcId[1].indexOf(')'))
                            .split(",");
                    final TupleCentreId tcId = new TupleCentreId(tcName,
                            tcNodeAndPort[0], tcNodeAndPort[1]);
                    this.speak("Serving 'createTransducer' request < TransducerId="
                            + tId
                            + ", associated TC="
                            + t.getArg(0).toString()
                            + " >...");
                    // Obtaining resource
                    final AbstractProbeId pId = new ActuatorId(t.getArg(4)
                            .getName());
                    ProbesManager.INSTANCE.createProbe(t.getArg(3).toString(),
                            pId);
                    final TransducersManager tm = TransducersManager.INSTANCE;
                    // Building transducer
                    tm.createTransducer(t.getArg(1).toString(), tId, tcId, pId);
                } else if (EnvConfigAgent.ADD_SENSOR.equals(t.getArg(0)
                        .toString())) {
                    t = LogicTuple.parse("addSensor(Class,Pid,Tid)");
                    t = acc.in(this.idEnvTC, t, null).getLogicTupleResult();
                    // Creating resource
                    final AbstractProbeId pId = new SensorId(t.getArg(1)
                            .getName());
                    final ProbesManager rm = ProbesManager.INSTANCE;
                    rm.createProbe(t.getArg(0).toString(), pId);
                    final ISimpleProbe probe = rm.getProbe(pId);
                    final TransducersManager tm = TransducersManager.INSTANCE;
                    final TransducerId tId = tm.getTransducer(
                            t.getArg(2).getName()).getIdentifier();
                    this.speak("Serving 'addSensor' request < ProbeId=" + pId
                            + ", associated transducer=" + tId + " >...");
                    tm.addProbe(probe.getIdentifier(), tId, probe);
                } else if (EnvConfigAgent.ADD_ACTUATOR.equals(t.getArg(0)
                        .toString())) {
                    t = LogicTuple.parse("addActuator(Class,Pid,Tid)");
                    t = acc.in(this.idEnvTC, t, null).getLogicTupleResult();
                    // Creating resource
                    final AbstractProbeId pId = new ActuatorId(t.getArg(1)
                            .getName());
                    final ProbesManager rm = ProbesManager.INSTANCE;
                    rm.createProbe(t.getArg(0).toString(), pId);
                    final ISimpleProbe probe = rm.getProbe(pId);
                    final TransducersManager tm = TransducersManager.INSTANCE;
                    final TransducerId tId = tm.getTransducer(
                            t.getArg(2).getName()).getIdentifier();
                    this.speak("Serving 'addActuator' request < ProbeId=" + pId
                            + ", associated transducer=" + tId + " >...");
                    tm.addProbe(probe.getIdentifier(), tId, probe);
                } else if (EnvConfigAgent.REMOVE_RESOURCE.equals(t.getArg(0)
                        .toString())) {
                    this.speak("Serving 'removeResource' request < ProbeId="
                            + t.getArg(0).getName() + " >...");
                    t = LogicTuple.parse("removeResource(Pid)");
                    t = acc.in(this.idEnvTC, t, null).getLogicTupleResult();
                    final ProbesManager rm = ProbesManager.INSTANCE;
                    final ISimpleProbe probe = rm.getProbeByName(t.getArg(0)
                            .getName());
                    rm.removeProbe(probe.getIdentifier());
                } else if (EnvConfigAgent.CHANGE_TRANSDUCER.equals(t.getArg(0)
                        .toString())) {
                    t = LogicTuple.parse("changeTransducer(Pid,Tid)");
                    t = acc.in(this.idEnvTC, t, null).getLogicTupleResult();
                    final ProbesManager rm = ProbesManager.INSTANCE;
                    final AbstractProbeId pId = rm.getProbeByName(
                            t.getArg(0).getName()).getIdentifier();
                    final TransducersManager tm = TransducersManager.INSTANCE;
                    final TransducerId tId = tm.getTransducer(
                            t.getArg(1).getName()).getIdentifier();
                    this.speak("Serving 'changeTransducer' request < ProbeId="
                            + pId + ", new transducer=" + tId + " >...");
                    rm.setTransducer(pId, tId);
                }
            } catch (final InvalidLogicTupleException e) {
                e.printStackTrace();
            } catch (final TucsonOperationNotPossibleException e) {
                e.printStackTrace();
            } catch (final UnreachableNodeException e) {
                e.printStackTrace();
            } catch (final OperationTimeOutException e) {
                e.printStackTrace();
            } catch (final InvalidOperationException e) {
                e.printStackTrace();
            } catch (final TucsonInvalidAgentIdException e) {
                e.printStackTrace();
            } catch (final InvalidTupleCentreIdException e) {
                e.printStackTrace();
            } catch (final ClassNotFoundException e) {
                e.printStackTrace();
            } catch (final NoSuchMethodException e) {
                e.printStackTrace();
            } catch (final InstantiationException e) {
                e.printStackTrace();
            } catch (final IllegalAccessException e) {
                e.printStackTrace();
            } catch (final InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void operationCompleted(final AbstractTupleCentreOperation op) {
        /*
         * not used atm
         */
    }

    @Override
    public void operationCompleted(final ITucsonOperation op) {
        /*
         * not used atm
         */
    }

    /**
     *
     */
    public void stopIteraction() {
        this.iteraction = false;
    }

    private void speak(final Object msg) {
        System.out.println("..[$EnvAgent (" + this.idEnvTC + ")]: " + msg);
    }
}
