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

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonTupleCentreId;

/**
 * 
 */
public class TucsonTCUsers {

    private final Date creationDate;
    private final List<TucsonAgentId> currentAidUsers;
    private final TucsonTupleCentreId tid;

    public TucsonTCUsers(final TucsonTupleCentreId id) {
        this.tid = id;
        this.creationDate = new Date();
        this.currentAidUsers = new LinkedList<TucsonAgentId>();
    }

    public void addUser(final TucsonAgentId aid) {
        synchronized (this.currentAidUsers) {
            if (!this.currentAidUsers.contains(aid)) {
                this.currentAidUsers.add(aid);
            }
        }
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public TucsonTupleCentreId getTucsonTupleCentreId() {
        return this.tid;
    }

    public List<TucsonAgentId> getUsers() {
        synchronized (this.currentAidUsers) {
            return this.currentAidUsers;
        }
    }

    public void removeUser(final TucsonAgentId aid) {
        synchronized (this.currentAidUsers) {
            if (this.currentAidUsers.contains(aid)) {
                this.currentAidUsers.remove(aid);
            }
        }
    }

}
