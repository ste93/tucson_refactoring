package it.unibo.sd.jade.coordination;

import alice.tucson.service.TucsonNodeService;

/**
 * 
 * @author Luca Sangiorgi (mailto: luca.sangiorgi6@studio.unibo.it)
 * 
 */
public final class TucsonNodeLifecycleManager {

    /*
     * The node service is in charge of listening to incoming operation
     * invocations on the associated port of the device dispatching them to the
     * target tuple centre returning the operations completion to the source
     * agent
     */
    private static TucsonNodeService tns;

    /**
     * 
     * @param port
     *            the IP port on which the TuCSoN Node should be listening for
     *            incoming requests
     */
    public static synchronized void startTucsonNode(final int port) {
        // try {
        // if (isTucsonNodeRunning(port)) {
        // return;
        // }
        // } catch (IOException e) {
        // System.err.println("[TucsonNodeUtility]: "+e);
        // e.printStackTrace();
        // }
        TucsonNodeLifecycleManager.tns = new TucsonNodeService(port);
        TucsonNodeLifecycleManager.tns.install();
    }

    /**
     * 
     * @param port
     *            the IP port which the TuCSoN service to be stopped is bound to
     */
    public static synchronized void stopTucsonNode(final int port) {
        // try {
        // if (!isTucsonNodeRunning(port)) {
        // return;
        // }
        // } catch (IOException e) {
        // System.err.println("[TucsonNodeUtility]: "+e);
        // e.printStackTrace();
        // }
        TucsonNodeLifecycleManager.tns.shutdown();
    }

    private TucsonNodeLifecycleManager() {
        /*
         * Enforce non-instantiability
         */
    }
}
