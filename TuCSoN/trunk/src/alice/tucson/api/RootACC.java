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
package alice.tucson.api;

import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;

/**
 * Root ACC, no Linda nor TuCSoN operations available, only ACC release back to
 * TuCSoN node is possible.
 * 
 * @author ste (mailto: s.mariani@unibo.it)
 */
public interface RootACC {

    /**
     * Release of the ACC and exit from the TuCSoN system.
     * 
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be carried out
     */
    void exit() throws TucsonOperationNotPossibleException;

}
