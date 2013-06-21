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

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;

/**
 * FSA-like TuCSoN agent.
 * 
 * @author unknown
 */
public abstract class Automaton extends TucsonAgent implements Serializable {

    static protected Class<?>[] argType;
    private static final long serialVersionUID = -8327090817152965357L;
    protected Object[] arguments = null;
    protected String state = "boot";

    /**
     * @param aid
     *            name of the agent (must be a valid Prolog term)
     * 
     * @throws TucsonInvalidAgentIdException
     * 
     * @see alice.tuprolog.Term Term
     */
    public Automaton(final String aid) throws TucsonInvalidAgentIdException {
        super(aid);
    }

    /**
     * To change state.
     * 
     * @param s
     *            the string representing the state to become
     */
    protected void become(final String s) {
        if (!this.state.equals("end")) {
            this.state = s;
            this.arguments = null;
        }
    }

    /**
     * To change state
     * 
     * @param s
     *            the string representing the state to become
     * @param args
     *            arguments to be used in the target state
     */
    protected void become(final String s, final Object[] args) {
        if (!this.state.equals("end")) {
            this.state = s;
            this.arguments = args;
        }
    }

    /**
     * Init state.
     */
    protected abstract void boot();

    /**
     * End state.
     * 
     * @throws TucsonOperationNotPossibleException
     */
    protected void end() throws TucsonOperationNotPossibleException {
        this.getContext().exit();
    }

    /**
     * Error state.
     */
    protected void error() {
        this.become("end");
    }

    /**
     * Main FSA cycle.
     */
    @Override
    final protected void main() {

        // TODO I don't think the string is correct...
        try {
            Automaton.argType =
                    new Class[] { Class.forName("java.lang.Object") };
        } catch (final ClassNotFoundException e) {
            System.err.println("[Automaton]: " + e);
            e.printStackTrace();
            this.error();
        }

        while (true) {
            if (!this.state.equals("end")) {
                if (this.arguments == null) {
                    Method m;
                    try {
                        m =
                                this.getClass().getDeclaredMethod(this.state,
                                        (Class<?>[]) null);
                        m.setAccessible(true);
                        m.invoke(this, (Object[]) null);
                    } catch (final SecurityException e) {
                        System.err.println("[Automaton]: " + e);
                        e.printStackTrace();
                        this.error();
                    } catch (final NoSuchMethodException e) {
                        System.err.println("[Automaton]: " + e);
                        e.printStackTrace();
                        this.error();
                    } catch (final IllegalArgumentException e) {
                        System.err.println("[Automaton]: " + e);
                        e.printStackTrace();
                        this.error();
                    } catch (final IllegalAccessException e) {
                        System.err.println("[Automaton]: " + e);
                        e.printStackTrace();
                        this.error();
                    } catch (final InvocationTargetException e) {
                        System.err.println("[Automaton]: " + e);
                        e.printStackTrace();
                        this.error();
                    }
                } else {
                    Method m;
                    try {
                        m =
                                this.getClass().getDeclaredMethod(this.state,
                                        Automaton.argType);
                        m.setAccessible(true);
                        m.invoke(this, this.arguments);
                    } catch (final SecurityException e) {
                        System.err.println("[Automaton]: " + e);
                        e.printStackTrace();
                        this.error();
                    } catch (final NoSuchMethodException e) {
                        System.err.println("[Automaton]: " + e);
                        e.printStackTrace();
                        this.error();
                    } catch (final IllegalArgumentException e) {
                        System.err.println("[Automaton]: " + e);
                        e.printStackTrace();
                        this.error();
                    } catch (final IllegalAccessException e) {
                        System.err.println("[Automaton]: " + e);
                        e.printStackTrace();
                        this.error();
                    } catch (final InvocationTargetException e) {
                        System.err.println("[Automaton]: " + e);
                        e.printStackTrace();
                        this.error();
                    }
                }
            } else {
                try {
                    this.end();
                } catch (final TucsonOperationNotPossibleException e) {
                    System.err.println("[Automaton]: " + e);
                    e.printStackTrace();
                    this.error();
                }
                break;
            }
        }

    }

}
