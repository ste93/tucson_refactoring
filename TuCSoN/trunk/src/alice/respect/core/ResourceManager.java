package alice.respect.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import alice.respect.situated.AbstractProbeId;
import alice.respect.situated.ISimpleProbe;
import alice.respect.situated.TransducerId;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;

/**
 * 
 * Class used for managing probes.
 * 
 * @author Steven maraldi
 * 
 */
public final class ResourceManager {

    /** The ResourceManager instance **/
    private static ResourceManager rm;

    /**
     * Gets the static instance of the resource manager
     * 
     * @return the resource manager
     */
    public static ResourceManager getResourceManager() {
        if (ResourceManager.rm == null) {
            ResourceManager.rm = new ResourceManager();
        }

        return ResourceManager.rm;
    }

    /**
     * Utility method used to communicate an output message to the console.
     * 
     * @param msg
     *            message to print
     */
    private static void speak(final String msg) {
        System.out.println("[ResourceManager] " + msg);
    }

    private static void speakErr(final String msg) {
        System.err.println("[ResourceManager] " + msg);
    }

    /** List of all probes on a single node **/
    private final Map<AbstractProbeId, ISimpleProbe> probeList;

    private ResourceManager() {
        this.probeList = new HashMap<AbstractProbeId, ISimpleProbe>();
    }

    /**
     * Creates a resource
     * 
     * @param className
     *            the concrete implementative class of the resource
     * @param id
     *            the identifier of the resource
     * @return wether the Resource has been succesfully created.
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
    public boolean createResource(final String className, final AbstractProbeId id)
            throws ClassNotFoundException, NoSuchMethodException,
            InstantiationException, IllegalAccessException,
            InvocationTargetException {
        if (this.probeList.containsKey(id)) {
            ResourceManager.speakErr("The probe " + id.getLocalName()
                    + " already exist");
            return false;
        }

        final String normClassName =
                className.substring(1, className.length() - 1);
        final Class<?> c = Class.forName(normClassName);
        final Constructor<?> ctor =
                c.getConstructor(new Class[] { AbstractProbeId.class });
        final ISimpleProbe probe =
                (ISimpleProbe) ctor.newInstance(new Object[] { id });

        this.probeList.put(id, probe);
        ResourceManager.speak("Resource " + id.getLocalName()
                + " has been registered");
        return true;
    }

    /**
     * Gets the resource by its identifier
     * 
     * @param id
     *            the resource's identifier
     * @return an interface toward the resource whose identifier has been given
     */
    public ISimpleProbe getResource(final AbstractProbeId id) {
        if (this.probeList.containsKey(id)) {
            return this.probeList.get(id);
        }
        ResourceManager.speakErr("Resource " + id.getLocalName()
                + " isn't registered.");
        return null;
    }

    /**
     * Gets the resource by its local name
     * 
     * @param name
     *            resource's local name
     * @return an interface toward the resource whose logical name has been
     *         given
     */
    public ISimpleProbe getResourceByName(final String name) {
        final Object[] keySet = this.probeList.keySet().toArray();
        for (final Object element : keySet) {
            if (((AbstractProbeId) element).getLocalName().equals(name)) {
                return this.probeList.get(element);
            }
        }
        ResourceManager.speakErr("Resource " + name + " isn't registered.");
        return null;
    }

    /**
     * Removes a resource from the list
     * 
     * @param id
     *            the identifier of the resource to remove
     * @return wether the resource has been successfully removed
     * @throws TucsonOperationNotPossibleException
     */
    public boolean removeResource(final AbstractProbeId id)
            throws TucsonOperationNotPossibleException {
        ResourceManager.speak("Removing probe " + id.getLocalName()
                + " from the resource list");
        if (!this.probeList.containsKey(id)) {
            ResourceManager.speakErr("Resource " + id.getLocalName()
                    + " doesn't exist");
            return false;
        }
        TransducerManager.getTransducerManager();
        TransducerManager.removeResource(id);
        this.probeList.remove(id);
        return true;
    }

    /**
     * Sets the transducer which the probe will communicate with.
     * 
     * @param pId
     *            the probe's identifier
     * @param tId
     *            the transducer's identifier
     */
    public void setTransducer(final AbstractProbeId pId, final TransducerId tId) {
        this.getResource(pId).setTransducer(tId);
        if (tId != null) {
            ResourceManager.speak("Transducer " + tId.getAgentName()
                    + " setted to probe " + pId.getLocalName());
        }
    }
}