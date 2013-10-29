package alice.respect.core;

import java.lang.reflect.InvocationTargetException;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.logictuple.exceptions.InvalidTupleOperationException;
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
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

/**
 * 
 * Environment configuration agent.
 * 
 * It checks for requests on tcEnvConfig and delegates them to the
 * TransducerManager.
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
    private static final String CREATE_TRANSDUCER_ACTUATOR =
            "createTransducerActuator";
    /** Create transducer sensor request's type **/
    private static final String CREATE_TRANSDUCER_SENSOR =
            "createTransducerSensor";
    /** Remove a resource request's type **/
    private static final String REMOVE_RESOURCE = "removeResource";

    private static void speak(final Object msg) {
        System.out.println("[EnvConfigAgent] " + msg);
    }

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
        super("envConfigAgent", ipAddress, portno);
        try {
            this.idEnvTC =
                    new TupleCentreId("envConfigTC", ipAddress,
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
                    t =
                            LogicTuple
                                    .parse("createTransducerSensor(Tcid,Tclass,Tid,Pclass,Pid)");
                    t = acc.in(this.idEnvTC, t, null).getLogicTupleResult();
                    // Obtaining transducer
                    final TransducerId tId =
                            new TransducerId(t.getArg(2).getName());
                    // Obtaining tuple centre and its properties
                    final String[] sTcId = t.getArg(0).toString().split(":"); // '@'(name,':'(node,port))
                    final String tcName =
                            sTcId[0].substring(sTcId[0].indexOf('(') + 1,
                                    sTcId[0].indexOf(','));
                    final String[] tcNodeAndPort =
                            sTcId[1].substring(sTcId[1].indexOf('(') + 1,
                                    sTcId[1].indexOf(')')).split(",");
                    final TupleCentreId tcId =
                            new TupleCentreId(tcName, tcNodeAndPort[0],
                                    tcNodeAndPort[1]);
                    EnvConfigAgent
                            .speak("Serving create transducer request. TransducerId:"
                                    + tId
                                    + " TC Associated:"
                                    + t.getArg(0).toString());
                    // Obtaining resource
                    final AbstractProbeId pId =
                            new SensorId(t.getArg(4).getName());
                    ResourceManager.getResourceManager().createResource(
                            t.getArg(3).toString(), pId);
                    TransducerManager.getTransducerManager();
                    // Building transducer
                    TransducerManager.createTransducer(t.getArg(1).toString(),
                            tId, tcId, pId);
                } else if (EnvConfigAgent.CREATE_TRANSDUCER_ACTUATOR.equals(t
                        .getArg(0).toString())) {
                    t =
                            LogicTuple
                                    .parse("createTransducerActuator(Tcid,Tclass,Tid,Pclass,Pid)");
                    t = acc.in(this.idEnvTC, t, null).getLogicTupleResult();
                    // Obtaining transducer
                    final TransducerId tId =
                            new TransducerId(t.getArg(2).getName());
                    // Obtaining tuple centre and its properties
                    final String[] sTcId = t.getArg(0).toString().split(":"); // '@'(name,':'(node,port))
                    final String tcName =
                            sTcId[0].substring(sTcId[0].indexOf('(') + 1,
                                    sTcId[0].indexOf(','));
                    final String[] tcNodeAndPort =
                            sTcId[1].substring(sTcId[1].indexOf('(') + 1,
                                    sTcId[1].indexOf(')')).split(",");
                    final TupleCentreId tcId =
                            new TupleCentreId(tcName, tcNodeAndPort[0],
                                    tcNodeAndPort[1]);
                    EnvConfigAgent
                            .speak("Serving create transducer request. TransducerId:"
                                    + tId
                                    + " TC Associated:"
                                    + t.getArg(0).toString());
                    // Obtaining resource
                    final AbstractProbeId pId =
                            new ActuatorId(t.getArg(4).getName());
                    ResourceManager.getResourceManager().createResource(
                            t.getArg(3).toString(), pId);
                    TransducerManager.getTransducerManager();
                    // Building transducer
                    TransducerManager.createTransducer(t.getArg(1).toString(),
                            tId, tcId, pId);
                } else if (EnvConfigAgent.ADD_SENSOR.equals(t.getArg(0)
                        .toString())) {
                    EnvConfigAgent.speak("Serving add sensor request");
                    t = LogicTuple.parse("addSensor(Class,Pid,Tid)");
                    t = acc.in(this.idEnvTC, t, null).getLogicTupleResult();
                    // Creating resource
                    final AbstractProbeId pId =
                            new SensorId(t.getArg(1).getName());
                    ResourceManager.getResourceManager().createResource(
                            t.getArg(0).toString(), pId);
                    final ISimpleProbe probe =
                            ResourceManager.getResourceManager().getResource(
                                    pId);
                    TransducerManager.getTransducerManager();
                    final TransducerId tId =
                            TransducerManager.getTransducer(
                                    t.getArg(2).getName()).getIdentifier();
                    TransducerManager.getTransducerManager();
                    TransducerManager.addResource(probe.getIdentifier(), tId,
                            probe);
                } else if (EnvConfigAgent.ADD_ACTUATOR.equals(t.getArg(0)
                        .toString())) {
                    EnvConfigAgent.speak("Serving add actuator request");
                    t = LogicTuple.parse("addActuator(Class,Pid,Tid)");
                    t = acc.in(this.idEnvTC, t, null).getLogicTupleResult();
                    // Creating resource
                    final AbstractProbeId pId =
                            new ActuatorId(t.getArg(1).getName());
                    ResourceManager.getResourceManager().createResource(
                            t.getArg(0).toString(), pId);
                    final ISimpleProbe probe =
                            ResourceManager.getResourceManager().getResource(
                                    pId);
                    TransducerManager.getTransducerManager();
                    final TransducerId tId =
                            TransducerManager.getTransducer(
                                    t.getArg(2).getName()).getIdentifier();
                    TransducerManager.getTransducerManager();
                    TransducerManager.addResource(probe.getIdentifier(), tId,
                            probe);
                } else if (EnvConfigAgent.REMOVE_RESOURCE.equals(t.getArg(0)
                        .toString())) {
                    EnvConfigAgent.speak("Serving remove resource request");
                    t = LogicTuple.parse("removeResource(Pid)");
                    t = acc.in(this.idEnvTC, t, null).getLogicTupleResult();
                    final ISimpleProbe probe =
                            ResourceManager.getResourceManager()
                                    .getResourceByName(t.getArg(0).getName());
                    ResourceManager.getResourceManager().removeResource(
                            probe.getIdentifier());
                } else if (EnvConfigAgent.CHANGE_TRANSDUCER.equals(t.getArg(0)
                        .toString())) {
                    EnvConfigAgent.speak("Serving change transducer request");
                    t = LogicTuple.parse("changeTransducer(Pid,Tid)");
                    t = acc.in(this.idEnvTC, t, null).getLogicTupleResult();
                    final AbstractProbeId pId =
                            ResourceManager.getResourceManager()
                                    .getResourceByName(t.getArg(0).getName())
                                    .getIdentifier();
                    TransducerManager.getTransducerManager();
                    final TransducerId tId =
                            TransducerManager.getTransducer(
                                    t.getArg(1).getName()).getIdentifier();
                    ResourceManager.getResourceManager()
                            .setTransducer(pId, tId);
                }
            } catch (final InvalidLogicTupleException e) {
                e.printStackTrace();
            } catch (final TucsonOperationNotPossibleException e) {
                e.printStackTrace();
            } catch (final UnreachableNodeException e) {
                e.printStackTrace();
            } catch (final OperationTimeOutException e) {
                e.printStackTrace();
            } catch (final InvalidTupleOperationException e) {
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
    public void operationCompleted(final ITucsonOperation op) {
        // TODO Auto-generated method stub

    }

    /**
     * 
     */
    public void stopIteraction() {
        this.iteraction = false;
    }
}
