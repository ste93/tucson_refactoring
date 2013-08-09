package alice.respect.core;

import alice.logictuple.LogicTuple;
import alice.respect.api.TupleCentreId;
import alice.respect.api.exceptions.InvalidTupleCentreIdException;
import alice.respect.probe.ActuatorId;
import alice.respect.probe.ISimpleProbe;
import alice.respect.probe.ProbeId;
import alice.respect.probe.SensorId;
import alice.respect.transducer.TransducerId;
import alice.tucson.api.AbstractTucsonAgent;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.SynchACC;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;

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

    private static void speak(final Object msg) {
        System.out.println("[EnvConfigAgent] " + msg);
    }

    /** Add an actuator request's type **/
    private final String ADD_ACTUATOR = "addActuator";

    /** Add a sensor request's type **/
    private final String ADD_SENSOR = "addSensor";
    /** Change the transducer associated to a resource request's type **/
    private final String CHANGE_TRANSDUCER = "changeTransducer";
    /** Create transducer actuator request's type **/
    private final String CREATE_TRANSDUCER_ACTUATOR =
            "createTransducerActuator";
    /** Create transducer sensor request's type **/
    private final String CREATE_TRANSDUCER_SENSOR = "createTransducerSensor";
    /** The tuple centre used for environment configuration **/
    private TupleCentreId idEnvTC;
    private boolean iteraction = true;

    /** Remove a resource request's type **/
    private final String REMOVE_RESOURCE = "removeResource";

    public EnvConfigAgent(final String ipAddress, final int portno)
            throws TucsonInvalidAgentIdException {
        super("envConfigAgent", ipAddress, portno);
        try {
            this.idEnvTC =
                    new TupleCentreId("envConfigTC", ipAddress, "" + portno);
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

                if (this.CREATE_TRANSDUCER_SENSOR
                        .equals(t.getArg(0).toString())) {
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
                    final ProbeId pId = new SensorId(t.getArg(4).getName());
                    ResourceManager.getResourceManager().createResource(
                            t.getArg(3).toString(), pId);
                    // Building transducer
                    TransducerManager.getTransducerManager().createTransducer(
                            t.getArg(1).toString(), tId, tcId, pId);
                } else if (this.CREATE_TRANSDUCER_ACTUATOR.equals(t.getArg(0)
                        .toString())) {
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
                    final ProbeId pId = new ActuatorId(t.getArg(4).getName());
                    ResourceManager.getResourceManager().createResource(
                            t.getArg(3).toString(), pId);
                    // Building transducer
                    TransducerManager.getTransducerManager().createTransducer(
                            t.getArg(1).toString(), tId, tcId, pId);
                } else if (this.ADD_SENSOR.equals(t.getArg(0).toString())) {
                    EnvConfigAgent.speak("Serving add sensor request");
                    t = LogicTuple.parse("addSensor(Class,Pid,Tid)");
                    t = acc.in(this.idEnvTC, t, null).getLogicTupleResult();
                    // Creating resource
                    final ProbeId pId = new SensorId(t.getArg(1).getName());
                    ResourceManager.getResourceManager().createResource(
                            t.getArg(0).toString(), pId);
                    final ISimpleProbe probe =
                            ResourceManager.getResourceManager().getResource(
                                    pId);
                    final TransducerId tId =
                            TransducerManager.getTransducerManager()
                                    .getTransducer(t.getArg(2).getName())
                                    .getIdentifier();
                    TransducerManager.getTransducerManager().addResource(
                            probe.getIdentifier(), tId, probe);
                } else if (this.ADD_ACTUATOR.equals(t.getArg(0).toString())) {
                    EnvConfigAgent.speak("Serving add actuator request");
                    t = LogicTuple.parse("addActuator(Class,Pid,Tid)");
                    t = acc.in(this.idEnvTC, t, null).getLogicTupleResult();
                    // Creating resource
                    final ProbeId pId = new ActuatorId(t.getArg(1).getName());
                    ResourceManager.getResourceManager().createResource(
                            t.getArg(0).toString(), pId);
                    final ISimpleProbe probe =
                            ResourceManager.getResourceManager().getResource(
                                    pId);
                    final TransducerId tId =
                            TransducerManager.getTransducerManager()
                                    .getTransducer(t.getArg(2).getName())
                                    .getIdentifier();
                    TransducerManager.getTransducerManager().addResource(
                            probe.getIdentifier(), tId, probe);
                } else if (this.REMOVE_RESOURCE.equals(t.getArg(0).toString())) {
                    EnvConfigAgent.speak("Serving remove resource request");
                    t = LogicTuple.parse("removeResource(Pid)");
                    t = acc.in(this.idEnvTC, t, null).getLogicTupleResult();
                    final ISimpleProbe probe =
                            ResourceManager.getResourceManager()
                                    .getResourceByName(t.getArg(0).getName());
                    ResourceManager.getResourceManager().removeResource(
                            probe.getIdentifier());
                } else if (this.CHANGE_TRANSDUCER
                        .equals(t.getArg(0).toString())) {
                    EnvConfigAgent.speak("Serving change transducer request");
                    t = LogicTuple.parse("changeTransducer(Pid,Tid)");
                    t = acc.in(this.idEnvTC, t, null).getLogicTupleResult();
                    final ProbeId pId =
                            ResourceManager.getResourceManager()
                                    .getResourceByName(t.getArg(0).getName())
                                    .getIdentifier();
                    final TransducerId tId =
                            TransducerManager.getTransducerManager()
                                    .getTransducer(t.getArg(1).getName())
                                    .getIdentifier();
                    ResourceManager.getResourceManager()
                            .setTransducer(pId, tId);
                }
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void operationCompleted(final ITucsonOperation op) {
        // TODO Auto-generated method stub

    }

    public void stopIteraction() {
        this.iteraction = false;
    }
}
