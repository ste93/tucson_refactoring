package alice.respect.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import alice.respect.api.EnvId;
import alice.respect.probe.ISimpleProbe;
import alice.respect.probe.AbstractProbeId;
import alice.respect.transducer.AbstractTransducer;
import alice.respect.transducer.TransducerId;
import alice.respect.transducer.TransducerStandardInterface;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tuplecentre.api.TupleCentreId;
import alice.tuprolog.Term;

/**
 * 
 * Class used for managing transducers. It can be used for environment
 * configuration and as a provider of information concerning transducer's
 * associations.
 * 
 * @author Steven Maraldi
 * 
 */
public final class TransducerManager {
    /** List of the associations transducer/probes **/
    private static Map<TransducerId, List<AbstractProbeId>> resourceList;

    /** The TransducerManager instance **/
    private static TransducerManager tm;

    /** List of all the transducers on a single node **/
    private static Map<TransducerId, AbstractTransducer> transducerList;

    /** List of the associations tuple centre/transducers **/
    private static Map<TupleCentreId, List<TransducerId>> tupleCentresAssociated;

    /**
     * Adds a new resource and associate it to the transducer tId.
     * 
     * @param id
     *            new environment resource identifier
     * @param tId
     *            transducer associated
     * @param probe
     *            the probe itself
     * @return wether the resource has been successfully added
     */
    public static boolean addResource(final AbstractProbeId id,
            final TransducerId tId, final ISimpleProbe probe) {
        TransducerManager.speak("Adding new resource " + id.getLocalName()
                + " to transducer " + tId.getAgentName());
        if (!TransducerManager.resourceList.containsKey(tId)) {
            TransducerManager.speakErr("Transducer " + tId.getAgentName()
                    + " doesn't exist.");
            return false;
        } else if (TransducerManager.resourceList.get(tId).contains(probe)) {
            TransducerManager.speak("Transducer " + tId.getAgentName()
                    + " is already associated to probe " + id.getLocalName());
            return false;
        }
        TransducerManager.transducerList.get(tId).addProbe(id, probe);
        TransducerManager.resourceList.get(tId).add(id);
        ResourceManager.getResourceManager().setTransducer(id, tId);
        return true;
    }

    /**
     * Creates a new transducer
     * 
     * @param className
     *            name of the concrete implementative class of transducer
     * @param id
     *            the transducer's identifier
     * @param tcId
     *            the tuple center with which the transducer will interact
     * @param probeId
     *            resource's identifier associated to the transducer
     * @return wether the transducer has been successfully created
     * 
     * @throws ClassNotFoundException
     *             if the given Java full class name cannot be found within
     *             known paths
     * @throws NoSuchMethodException
     *             if the Java method name cannot be found
     * @throws InstantiationException
     *             if the given Java class cannot be instantiated
     * @throws IllegalAccessException
     *             if the caller has no rights to access class, methods, or
     *             fields
     * @throws InvocationTargetException
     *             if the callee cannot be found
     */
    public static boolean createTransducer(final String className,
            final TransducerId id, final TupleCentreId tcId,
            final AbstractProbeId probeId) throws InstantiationException,
            IllegalAccessException, ClassNotFoundException,
            NoSuchMethodException, InvocationTargetException {
        // Checking if the transducer already exist
        if (TransducerManager.transducerList.containsKey(id)) {
            TransducerManager.speakErr("Transducer " + id.toString()
                    + " is already been registered");
            return false;
        }

        // Registering tuple centre <> transducer association
        if (TransducerManager.tupleCentresAssociated.containsKey(tcId)) {
            TransducerManager.tupleCentresAssociated.get(tcId).add(id);
        } else {
            final ArrayList<TransducerId> transducers =
                    new ArrayList<TransducerId>();
            transducers.add(id);
            TransducerManager.tupleCentresAssociated.put(tcId, transducers);
        }

        // Instantiating the concrete class
        final String normClassName =
                className.substring(1, className.length() - 1);
        final Class<?> c = Class.forName(normClassName);
        final Constructor<?> ctor =
                c.getConstructor(new Class[] { TransducerId.class,
                        TupleCentreId.class, AbstractProbeId.class });
        final AbstractTransducer t =
                (AbstractTransducer) ctor.newInstance(new Object[] { id, tcId,
                        probeId });
        TransducerManager.transducerList.put(id, t);

        // Adding probe to the transducer
        final ArrayList<AbstractProbeId> probes =
                new ArrayList<AbstractProbeId>();
        probes.add(probeId);
        TransducerManager.resourceList.put(id, probes);
        TransducerManager.addResource(probeId, id, ResourceManager
                .getResourceManager().getResource(probeId));
        TransducerManager.speak("Transducer " + id.toString()
                + " has been registered");
        return true;
    }

    /**
     * Returns the resource list associated to the transducer identified by tId
     * 
     * @param tId
     *            the transducer's identifier
     * @return a resource list as a ProbeId array.
     */
    public static AbstractProbeId[] getResources(final TransducerId tId) {
        if (!TransducerManager.resourceList.containsKey(tId)) {
            TransducerManager.speakErr("The transducer " + tId.getAgentName()
                    + " doesn't exist");
            return null;
        }
        final Object[] values =
                TransducerManager.resourceList.get(tId).toArray();
        final AbstractProbeId[] probes = new AbstractProbeId[values.length];
        for (int i = 0; i < probes.length; i++) {
            probes[i] = (AbstractProbeId) values[i];
        }
        return probes;
    }

    /**
     * Returns the transducer identified by tId
     * 
     * @param tId
     *            the transducer's name
     * @return the transducer
     */
    public static TransducerStandardInterface getTransducer(final String tId) {
        final Object[] keySet =
                TransducerManager.transducerList.keySet().toArray();
        for (final Object element : keySet) {
            if (((TransducerId) element).getAgentName().equals(tId)) {
                return TransducerManager.transducerList.get(element);
            }
        }
        return null;
    }

    /**
     * Returns the transducer's identifier associated to the input resource
     * 
     * @param probe
     *            resource associated to the transducer
     * @return the transducer identifier
     */
    public static TransducerId getTransducerId(final EnvId probe) {
        final Set<TransducerId> set = TransducerManager.resourceList.keySet();
        final Object[] keySet = set.toArray();
        for (final Object element : keySet) {
            for (int j = 0; j < TransducerManager.resourceList.get(element)
                    .size(); j++) {
                final Term pId =
                        TransducerManager.resourceList.get(element).get(j)
                                .toTerm();
                if (pId.equals(probe.toTerm())) {
                    return (TransducerId) element;
                }
            }
        }
        TransducerManager.speakErr("The resource " + probe.getLocalName()
                + " isn't associated to any transducer");
        return null;
    }

    /**
     * Gets the list of transducer ids associated to the tuple centre identified
     * by tcId
     * 
     * @param tcId
     *            the tuple centre's identifier
     * @return list of transducer id associated to tcId
     */
    public static TransducerId[] getTransducerIds(final TupleCentreId tcId) {
        final Object[] tcIds =
                TransducerManager.tupleCentresAssociated.keySet().toArray();
        for (final Object tcId2 : tcIds) {
            if (((TupleCentreId) tcId2).toString().equals(tcId.toString())) {
                final Object[] values =
                        TransducerManager.tupleCentresAssociated.get(tcId2)
                                .toArray();
                final TransducerId[] transducerIds =
                        new TransducerId[values.length];
                for (int j = 0; j < values.length; j++) {
                    transducerIds[j] = (TransducerId) values[j];
                }
                return transducerIds;
            }
        }
        TransducerManager
                .speakErr("There's no transducer associated to tuple centre "
                        + tcId + " or it doesn't exist at all");
        return new TransducerId[] {};
    }

    /**
     * Gets the static instance of the transducer manager
     * 
     * @return the transducer manager
     */
    public static synchronized TransducerManager getTransducerManager() {
        if (TransducerManager.tm == null) {
            TransducerManager.tm = new TransducerManager();
        }

        return TransducerManager.tm;
    }

    /**
     * Returns the identifier of the tuple centre associated to the transducer
     * identified by tId
     * 
     * @param tId
     *            the transducer's identifier
     * @return the tuple centre's identifier
     */
    public static TupleCentreId getTupleCentreId(final TransducerId tId) {
        if (TransducerManager.tupleCentresAssociated.containsValue(tId)) {
            final TupleCentreId[] tcArray =
                    TransducerManager.tupleCentresAssociated.keySet().toArray(
                            new TupleCentreId[] {});
            for (final TupleCentreId element : tcArray) {
                if (TransducerManager.tupleCentresAssociated.get(element)
                        .contains(tId)) {
                    return element;
                }
            }
        }
        TransducerManager.speakErr("The transducer " + tId.getAgentName()
                + " doesn't exist");
        return null;
    }

    /**
     * Stops and removes the transducer identified by id
     * 
     * @param id
     *            the transducer identifier
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be successfully performed
     */
    public static void stopTransducer(final TransducerId id)
            throws TucsonOperationNotPossibleException {
        if (!TransducerManager.transducerList.containsKey(id)) {
            TransducerManager.speakErr("The transducer " + id
                    + " doesn't exist.");
            return;
        }
        TransducerManager.transducerList.get(id).exit();
        // Decouple the transducer from the probes associated.
        final Object[] pIds = TransducerManager.resourceList.get(id).toArray();
        for (final Object pId : pIds) {
            ResourceManager.getResourceManager()
                    .getResource((AbstractProbeId) pId).setTransducer(null);
        }

        TransducerManager.transducerList.remove(id);
        TransducerManager.resourceList.remove(id);
        final TupleCentreId tcAssociated =
                TransducerManager.getTupleCentreId(id);
        TransducerManager.tupleCentresAssociated.get(tcAssociated).remove(id);
        // If the tc doesn't have any transducer associated, it will be removed
        if (TransducerManager.tupleCentresAssociated.get(tcAssociated)
                .isEmpty()) {
            TransducerManager.tupleCentresAssociated.remove(tcAssociated);
        }
        TransducerManager.speak("Transducer " + id + " has been removed.");
    }

    /**
     * Utility methods used to communicate an output message to the console.
     * 
     * @param msg
     *            message to print.
     */
    private static void speak(final String msg) {
        System.out.println("[TransducerManager] " + msg);
    }

    private static void speakErr(final String msg) {
        System.err.println("[TransducerManager] " + msg);
    }

    private TransducerManager() {
        TransducerManager.transducerList =
                new HashMap<TransducerId, AbstractTransducer>();
        TransducerManager.resourceList =
                new HashMap<TransducerId, List<AbstractProbeId>>();
        TransducerManager.tupleCentresAssociated =
                new HashMap<TupleCentreId, List<TransducerId>>();
    }

    /**
     * Removes a probe from the resource list
     * 
     * @param probe
     *            the resource's identifier to remove
     * @return wether the resource has been succesfully removed
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be succesfully carried out
     */
    public static boolean removeResource(final AbstractProbeId probe)
            throws TucsonOperationNotPossibleException {
        if (!TransducerManager.resourceList.containsValue(probe)) {
            return false;
        }
        final TransducerId tId = TransducerManager.getTransducerId(probe);
        TransducerManager.transducerList.get(tId).removeProbe(probe);
        TransducerManager.resourceList.get(tId).remove(probe);
        ResourceManager.getResourceManager().getResource(probe)
                .setTransducer(null);
        // Se il transducer e' rimasto senza risorse associate viene terminato
        if (TransducerManager.resourceList.get(tId).isEmpty()) {
            TransducerManager
                    .speak("Transducer "
                            + tId.toString()
                            + " has no more resources associated. Its execution will be stopped");
            TransducerManager.stopTransducer(tId);
        }
        return true;
    }
}
