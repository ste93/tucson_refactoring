/*
 * TuCSoN coordination infrastructure - Copyright (C) 2001-2002  aliCE team at deis.unibo.it
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package alice.tucson.api;

import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * FSA-like TuCSoN agent.
 * 
 * @author unknown
 */
public abstract class Automaton extends TucsonAgent implements Serializable{

	private static final long serialVersionUID = -8327090817152965357L;
	protected String state = "boot";
	protected Object[] arguments = null;
	static protected Class<?>[] argType;

	/**
	 * @param aid name of the agent (must be a valid Prolog term)
	 * 
	 * @throws TucsonInvalidAgentIdException
	 * 
	 * @see alice.tuprolog.Term Term
	 */
	public Automaton(String aid) throws TucsonInvalidAgentIdException{
		super(aid);
	}

	/**
	 * To change state.
	 * 
	 * @param s the string representing the state to become
	 */
	protected void become(String s){
		if(!state.equals("end")){
			state = s;
			arguments = null;
		}
	}

	/**
	 * To change state
	 * 
	 * @param s the string representing the state to become
	 * @param args arguments to be used in the target state
	 */
	protected void become(String s, Object[] args){
		if(!state.equals("end")){
			state = s;
			arguments = args;
		}
	}

	/**
	 * Init state.
	 */
	protected abstract void boot();

	/**
	 * Main FSA cycle.
	 */
	final protected void main(){
		
//		I don't think  the string is correct...
		try{
			argType = new Class[] { Class.forName("java.lang.Object") };
		}catch(ClassNotFoundException e){
			System.err.println("[Automaton]: " + e);
			e.printStackTrace();
			error();
		}
		
		while(true){
			if(!state.equals("end")){
				if(arguments == null){
					Method m;
					try{
						m = this.getClass().getDeclaredMethod(state, (Class<?>[])null);
						m.setAccessible(true);
						m.invoke(this, (Object[])null);
					}catch(SecurityException e){
						System.err.println("[Automaton]: " + e);
						e.printStackTrace();
						error();
					}catch(NoSuchMethodException e){
						System.err.println("[Automaton]: " + e);
						e.printStackTrace();
						error();
					}catch(IllegalArgumentException e){
						System.err.println("[Automaton]: " + e);
						e.printStackTrace();
						error();
					}catch(IllegalAccessException e){
						System.err.println("[Automaton]: " + e);
						e.printStackTrace();
						error();
					}catch(InvocationTargetException e){
						System.err.println("[Automaton]: " + e);
						e.printStackTrace();
						error();
					}
				}else{
					Method m;
					try{
						m = this.getClass().getDeclaredMethod(state, argType);
						m.setAccessible(true);
						m.invoke(this, arguments);
					}catch(SecurityException e){
						System.err.println("[Automaton]: " + e);
						e.printStackTrace();
						error();
					}catch(NoSuchMethodException e){
						System.err.println("[Automaton]: " + e);
						e.printStackTrace();
						error();
					}catch(IllegalArgumentException e){
						System.err.println("[Automaton]: " + e);
						e.printStackTrace();
						error();
					}catch(IllegalAccessException e){
						System.err.println("[Automaton]: " + e);
						e.printStackTrace();
						error();
					}catch(InvocationTargetException e){
						System.err.println("[Automaton]: " + e);
						e.printStackTrace();
						error();
					}
				}
			}else{
				try{
					end();
				}catch(TucsonOperationNotPossibleException e){
					System.err.println("[Automaton]: " + e);
					e.printStackTrace();
					error();
				}
				break;
			}
		}
		
	}

	/**
	 * End state.
	 * 
	 * @throws TucsonOperationNotPossibleException
	 */
	protected void end() throws TucsonOperationNotPossibleException{
		getContext().exit();
	}

	/**
	 * Error state.
	 */
	protected void error(){
		become("end");
	}
	
}
