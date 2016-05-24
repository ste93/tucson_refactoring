/**
 * Created by Stefano Mariani on 24/mag/2016 (mailto: s.mariani@unibo.it)
 */

/*
 * Copyright 1999-2014 Alma Mater Studiorum - Universita' di Bologna
 *
 * This file is part of TuCSoN4JADE <http://tucson4jade.apice.unibo.it>.
 *
 *    TuCSoN4JADE is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published
 *    by the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    TuCSoN4JADE is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with TuCSoN4JADE.  If not, see
 *    <https://www.gnu.org/licenses/lgpl.html>.
 *
 */

package alice.respect.api.exceptions;

/**
 * @author Stefano Mariani (mailto: s.mariani@unibo.it)
 *
 */
public final class InvalidAccessException extends Exception {

    /**
     * @param msg
     */
    public InvalidAccessException(String msg) {
        super(msg);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

}
