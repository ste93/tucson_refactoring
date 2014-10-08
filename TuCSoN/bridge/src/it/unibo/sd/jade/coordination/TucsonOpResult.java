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
package it.unibo.sd.jade.coordination;

import java.util.LinkedList;
import java.util.List;
import alice.tucson.service.TucsonOpCompletionEvent;

/**
 * TucsonOpResult. Object wrapping TuCSoN coordination operation results to JADE
 * agents.
 * 
 * @author Luca Sangiorgi (mailto: luca.sangiorgi6@studio.unibo.it)
 * @author (contributor) Stefano Mariani (mailto: s.mariani@unibo.it)
 * 
 */
public class TucsonOpResult {
    /*
     * next result to provide. Used to give the result to requesting agents and
     * test if operation is still to be requested
     */
    private int nextRes;
    /*
     * used to know if the operation result is ready
     */
    private boolean ready;
    private final List<TucsonOpCompletionEvent> tucsonCompletionEvents;

    /**
     * 
     */
    public TucsonOpResult() {
        this.tucsonCompletionEvents = new LinkedList<TucsonOpCompletionEvent>();
        this.nextRes = 0;
        this.ready = false;
    }

    /**
     * @return the next result
     */
    public int getNextRes() {
        return this.nextRes;
    }

    /**
     * @return the list of completion events
     */
    public List<TucsonOpCompletionEvent> getTucsonCompletionEvents() {
        return this.tucsonCompletionEvents;
    }

    /**
     * @return wether the operation result is ready
     */
    public boolean isReady() {
        return this.ready;
    }

    /**
     * @param n
     *            the next result to set
     */
    public void setNextRes(final int n) {
        this.nextRes = n;
    }

    /**
     * @param r
     *            wether the operation result is ready
     */
    public void setReady(final boolean r) {
        this.ready = r;
    }
}
