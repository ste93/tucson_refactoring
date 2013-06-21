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

import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonTupleCentreId;

public class Inspector extends Thread implements InspectorContextListener {

    protected InspectorContext context;
    protected boolean quit;

    public Inspector(final TucsonAgentId id, final TucsonTupleCentreId tid) {
        this.context = new InspectorContextStub(id, tid);
        this.context.addInspectorContextListener(this);
        this.quit = false;
    }

    public InspectorContext getContext() {
        return this.context;
    }

    public void onContextEvent(final InspectorContextEvent ev) {
        /*
         * FIXME Decide what to do here
         */
    }

    public void quit() {
        try {
            this.quit = true;
            this.context.exit();
            this.interrupt();
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public synchronized void run() {
        System.out.println("[Inspector]: Started inspecting TuCSoN Node < "
                + this.context.getTid().getName() + "@"
                + this.context.getTid().getNode() + ":"
                + this.context.getTid().getPort() + " >");
        while (!this.quit) {
            try {
                this.context.acceptVMEvent();
            } catch (final Exception e) {
                e.printStackTrace();
                break;
            }
        }
        System.out.println("[Inspector]: Stopped inspecting TuCSoN Node < "
                + this.context.getTid().getName() + "@"
                + this.context.getTid().getNode() + ":"
                + this.context.getTid().getPort() + " >");
    }

}
