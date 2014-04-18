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
package alice.tucson.introspection;

import java.io.IOException;
import java.util.List;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tuplecentre.api.Tuple;

/**
 * Defines the basic context for TuCSoN Inspector
 * 
 * @author Unknown...
 * 
 */
public interface InspectorContext {
    /**
     * waits and processes TuCSoN virtual machine events
     * 
     * @throws ClassNotFoundException
     *             if the Java Object class to be read cannot be found
     * @throws IOException
     *             if some I/O error occurs
     */
    void acceptVMEvent() throws ClassNotFoundException, IOException;

    /**
     * adds a new listener to the event generated by this context
     * 
     * @param l
     *            the listener of inspection events to add
     */
    void addInspectorContextListener(InspectorContextListener l);

    /**
     * shutdown inspector
     * 
     * @throws IOException
     *             if some I/O error occurs
     */
    void exit() throws IOException;

    /**
     * gets a snapshot of tuple set
     * 
     * @param snapshotType
     *            the type of snapshot the inspector wants to receive
     * @throws IOException
     *             if some I/O error occurs
     */
    void getSnapshot(byte snapshotType) throws IOException;

    /**
     * 
     * @return the identifier of the tuple centre under inspection
     */
    TucsonTupleCentreId getTid();

    /**
     * when doing trace, asks for a new virtual machine step
     * 
     * @throws IOException
     *             if some I/O error occurs
     */
    void nextStep() throws IOException;

    /**
     * Removes a listener to Inspector Events
     * 
     * @param l
     *            the listener of inspection events to remove
     */
    void removeInspectorContextListener(InspectorContextListener l);

    /**
     * resets the tuple centre
     * 
     * @throws IOException
     *             if some I/O error occurs
     */
    void reset() throws IOException;

    /**
     * sets the entire content of the event set
     * 
     * @param tset
     *            the list of tuples representing events to overwrite current
     *            InQ with
     * @throws IOException
     *             if some I/O error occurs
     */
    void setEventSet(List<Tuple> tset) throws IOException;

    /**
     * specifies the protocol used by the inspector
     * 
     * @param p
     *            the inspection protocol to be used
     * @throws IOException
     *             if some I/O error occurs
     */
    void setProtocol(InspectorProtocol p) throws IOException;

    /**
     * sets the entire content of the tuple set
     * 
     * @param tset
     *            the list of tuples to overwrite current tuple set with
     * @throws IOException
     *             if some I/O error occurs
     */
    void setTupleSet(List<Tuple> tset) throws IOException;
}
