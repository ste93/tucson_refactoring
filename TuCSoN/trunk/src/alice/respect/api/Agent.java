/*
 * ReSpceT Copyright (C) aliCE team at deis.unibo.it This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package alice.respect.api;

import java.lang.reflect.Method;

import alice.respect.core.RespectTC;

/**
 * Base class for building ReSpecT agents.
 * 
 * @author aricci
 * 
 */
public abstract class Agent {

    final class PlanExecutor extends Thread {
        private final Method activity;
        private final Agent agent;

        PlanExecutor(final Agent ag, final Method m) {
            super();
            this.agent = ag;
            this.activity = m;
        }

        @Override
        public void run() {
            try {
                this.activity.invoke(this.agent, Agent.ARGS);
            } catch (final Exception ex) {
                // TODO Properly handle Exception
            }
        }
    }

    private static final Object[] ARGS = new Object[] {};
    @SuppressWarnings("unchecked")
    private static final Class<? extends Object>[] ARGS_CLASS = new Class[] {};
    private final AgentId id;

    private RespectTC tc;

    protected Agent(final AgentId aid) {
        this.id = aid;
    }

    protected Agent(final AgentId aid, final RespectTC rtc) {
        this.id = aid;
        this.tc = rtc;
    }

    public AgentId getId() {
        return this.id;
    }

    /**
     * @return the tc
     */
    public RespectTC getTc() {
        return this.tc;
    }

    /**
     * Starts agent execution
     */
    final public void go() {
        this.execPlan("mainPlan");
    }

    /**
     * @param rtc
     *            the tc to set
     */
    public void setTc(final RespectTC rtc) {
        this.tc = rtc;
    }

    final protected void execPlan(final String name) {
        Method m = null;
        try {
            m = this.getClass().getDeclaredMethod(name, Agent.ARGS_CLASS);
        } catch (final NoSuchMethodException e) {
            // TODO Auto-generated catch block
        } catch (final SecurityException e) {
            // TODO Auto-generated catch block
        }
        m.setAccessible(true);
        new PlanExecutor(this, m).start();
    }

    /**
     * Body of the agent
     */
    abstract protected void mainPlan();

}
