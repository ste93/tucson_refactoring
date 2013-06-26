package alice.tucson.service;

import java.util.Date;

/**
 * 
 */
public class NodeInstallationReport {

    private Date date;
    private String descr;
    private boolean succeeded;
    private final Object synch;

    public NodeInstallationReport() {
        this.descr = "";
        this.succeeded = false;
        this.synch = new Object();
    }

    public synchronized Date getDate() {
        return this.date;
    }

    public synchronized String getDescr() {
        return this.descr;
    }

    public synchronized boolean isSucceeded() {
        return this.succeeded;
    }

    public synchronized void setReport(final boolean success, final String d,
            final Date da) {
        this.succeeded = success;
        this.descr = d;
        this.date = da;
        synchronized (this.synch) {
            this.synch.notifyAll();
        }
    }

    public void waitForReport() throws InterruptedException {
        synchronized (this.synch) {
            this.synch.wait();
        }
    }

}
