/*
 * tuProlog - Copyright (C) 2001-2002  aliCE team at deis.unibo.it
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

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidTupleOperationException;

import alice.tucson.api.exceptions.TucsonGenericException;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;

import alice.tuplecentre.api.exceptions.OperationTimeOutException;

import alice.tuprolog.Library;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;

import java.util.Iterator;
import java.util.List;

public class Tucson2PLibrary extends Library{
	
	private static final long serialVersionUID = 6716779172091533171L;
	private EnhancedACC context;
	private TucsonAgentId agentId;

	public Tucson2PLibrary() throws TucsonGenericException{		
	}

	public void setContext(EnhancedACC context){
		this.context = context;
	}

	public String getTheory(){
		return ":- op(551, xfx, '?'). \n"
				+ ":- op(550, xfx, '@'). \n"
				+ ":- op(549, xfx, ':'). \n"
				+ ":- op(548, xfx, '.'). \n"
				
				+ "out(T) :- out(T, default@localhost:20504). \n"
				+ "in(T) :- in(T, default@localhost:20504). \n"
				+ "inp(T) :- inp(T, default@localhost:20504). \n"
				+ "rd(T) :- rd(T, default@localhost:20504). \n"
				+ "rdp(T) :- rdp(T, default@localhost:20504). \n"
				+ "no(T) :- no(T, default@localhost:20504). \n"
				+ "nop(T) :- nop(T, default@localhost:20504). \n"
				+ "get(T) :- get(T, default@localhost:20504). \n"
				+ "set(T) :- set(T, default@localhost:20504). \n"
				
				+ "uin(T) :- uin(T, default@localhost:20504). \n"
				+ "uinp(T) :- uinp(T, default@localhost:20504). \n"
				+ "urd(T) :- urd(T, default@localhost:20504). \n"
				+ "urdp(T) :- urdp(T, default@localhost:20504). \n"
				+ "uno(T) :- uno(T, default@localhost:20504). \n"
				+ "unop(T) :- unop(T, default@localhost:20504). \n"
				+ "out_all(L) :- out_all(L, default@localhost:20504). \n"
				+ "in_all(T,L) :- in_all(T, L, default@localhost:20504). \n"
				+ "rd_all(T,L) :- rd_all(T, L, default@localhost:20504). \n"
				+ "no_all(T,L) :- no_all(T, L, default@localhost:20504). \n"
				
				+ "out_s(E,G,R) :- out_s(E,G,R, default@localhost:20504). \n"
				+ "in_s(E,G,R) :- in_s(E,G,R, default@localhost:20504). \n"
				+ "inp_s(E,G,R) :- inp_s(E,G,R, default@localhost:20504). \n"
				+ "rd_s(E,G,R) :- rd_s(E,G,R, default@localhost:20504). \n"
				+ "rdp_s(E,G,R) :- rdp_s(E,G,R, default@localhost:20504). \n"
				+ "no_s(E,G,R) :- no_s(E,G,R, default@localhost:20504). \n"
				+ "nop_s(E,G,R) :- nop_s(E,G,R, default@localhost:20504). \n"
				+ "get_s(L) :- get_s(L, default@localhost:20504). \n"
				+ "set_s(L) :- set_s(L, default@localhost:20504). \n"
				
				+ "TC@Netid:Port ? out(T) :- !, out(T, TC@Netid:Port). \n"
				+ "TC@Netid:Port ? in(T) :- !, in(T, TC@Netid:Port). \n"
				+ "TC@Netid:Port ? inp(T) :- !, inp(T, TC@Netid:Port). \n"
				+ "TC@Netid:Port ? rd(T) :- !, rd(T, TC@Netid:Port). \n"
				+ "TC@Netid:Port ? rdp(T) :- !, rdp(T, TC@Netid:Port). \n"
				+ "TC@Netid:Port ? no(T) :- !, no(T, TC@Netid:Port). \n"
				+ "TC@Netid:Port ? nop(T) :- !, nop(T, TC@Netid:Port). \n"
				+ "TC@Netid:Port ? get(L) :- !, get(L, TC@Netid:Port). \n"
				+ "TC@Netid:Port ? set(L) :- !, set(L, TC@Netid:Port). \n"
				
				+ "TC@Netid:Port ? uin(T) :- uin(T, TC@Netid:Port). \n"
				+ "TC@Netid:Port ? uinp(T) :- uinp(T, TC@Netid:Port). \n"
				+ "TC@Netid:Port ? urd(T) :- urd(T, TC@Netid:Port). \n"
				+ "TC@Netid:Port ? urdp(T) :- urdp(T, TC@Netid:Port). \n"
				+ "TC@Netid:Port ? uno(T) :- uno(T, TC@Netid:Port). \n"
				+ "TC@Netid:Port ? unop(T) :- unop(T, TC@Netid:Port). \n"
				+ "TC@Netid:Port ? out_all(L) :- out_all(L, TC@Netid:Port). \n"
				+ "TC@Netid:Port ? in_all(T,L) :- in_all(T, L, TC@Netid:Port). \n"
				+ "TC@Netid:Port ? rd_all(T,L) :- rd_all(T, L, TC@Netid:Port). \n"
				+ "TC@Netid:Port ? no_all(T,L) :- no_all(T, L, TC@Netid:Port). \n"
				
				+ "TC@Netid:Port ? out_s(E,G,R) :- !, out_s(E,G,R, TC@Netid:Port). \n"
				+ "TC@Netid:Port ? in_s(E,G,R) :- !, in_s(E,G,R, TC@Netid:Port). \n"
				+ "TC@Netid:Port ? inp_s(E,G,R) :- !, inp_s(E,G,R, TC@Netid:Port). \n"
				+ "TC@Netid:Port ? rd_s(E,G,R) :- !, rd_s(E,G,R, TC@Netid:Port). \n"
				+ "TC@Netid:Port ? rdp_s(E,G,R) :- !, rdp_s(E,G,R, TC@Netid:Port). \n"
				+ "TC@Netid:Port ? no_s(E,G,R) :- !, no_s(E,G,R, TC@Netid:Port). \n"
				+ "TC@Netid:Port ? nop_s(E,G,R) :- !, nop_s(E,G,R, TC@Netid:Port). \n"
				+ "TC@Netid:Port ? get_s(L) :- !, get_s(L, TC@Netid:Port). \n"
				+ "TC@Netid:Port ? set_s(L) :- !, set_s(L, TC@Netid:Port). \n";
	}

	public boolean out_2(Term arg0, Term arg1){
		if(context == null)
			return false;
		TucsonTupleCentreId tid;
		try{
			tid = new TucsonTupleCentreId(arg1.toString());
		}catch(TucsonInvalidTupleCentreIdException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		ITucsonOperation op;
		try{
			op = context.out(tid, new LogicTuple(arg0), (Long)null);
		}catch(TucsonOperationNotPossibleException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}catch(UnreachableNodeException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		} catch (OperationTimeOutException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		return op.isResultSuccess();
	}

	public boolean in_2(Term arg0, Term arg1){
		if(context == null)
			return false;
		TucsonTupleCentreId tid;
		try{
			tid = new TucsonTupleCentreId(arg1.toString());
		}catch(TucsonInvalidTupleCentreIdException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		ITucsonOperation op;
		try{
			op = context.in(tid, new LogicTuple(arg0), (Long)null);
		}catch(TucsonOperationNotPossibleException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}catch(UnreachableNodeException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}catch(OperationTimeOutException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		if(op.isResultSuccess())
			unify(arg0, op.getLogicTupleResult().toTerm());
		return op.isResultSuccess();
	}

	public boolean rd_2(Term arg0, Term arg1){
		if(context == null)
			return false;
		TucsonTupleCentreId tid;
		try{
			tid = new TucsonTupleCentreId(arg1.toString());
		}catch(TucsonInvalidTupleCentreIdException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		ITucsonOperation op;
		try{
			op = context.rd(tid, new LogicTuple(arg0), (Long)null);
		}catch(TucsonOperationNotPossibleException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}catch(UnreachableNodeException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}catch(OperationTimeOutException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		if(op.isResultSuccess())
			unify(arg0, op.getLogicTupleResult().toTerm());
		return op.isResultSuccess();
	}

	public boolean inp_2(Term arg0, Term arg1){
		if(context == null)
			return false;
		TucsonTupleCentreId tid;
		try{
			tid = new TucsonTupleCentreId(arg1.toString());
		}catch(TucsonInvalidTupleCentreIdException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		ITucsonOperation op;
		try{
			op = context.inp(tid, new LogicTuple(arg0), (Long)null);
		}catch(TucsonOperationNotPossibleException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}catch(UnreachableNodeException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		} catch (OperationTimeOutException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		if(op.isResultSuccess())
			unify(arg0, op.getLogicTupleResult().toTerm());
		return op.isResultSuccess();
	}

	public boolean rdp_2(Term arg0, Term arg1){
		if(context == null)
			return false;
		TucsonTupleCentreId tid;
		try{
			tid = new TucsonTupleCentreId(arg1.toString());
		}catch(TucsonInvalidTupleCentreIdException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		ITucsonOperation op;
		try{
			op = context.rdp(tid, new LogicTuple(arg0), (Long)null);
		}catch(TucsonOperationNotPossibleException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}catch(UnreachableNodeException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		} catch (OperationTimeOutException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		if(op.isResultSuccess())
			unify(arg0, op.getLogicTupleResult().toTerm());
		return op.isResultSuccess();
	}
	
	public boolean no_2(Term arg0, Term arg1){
		if(context == null)
			return false;
		TucsonTupleCentreId tid;
		try{
			tid = new TucsonTupleCentreId(arg1.toString());
		}catch(TucsonInvalidTupleCentreIdException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		ITucsonOperation op;
		try{
			op = context.no(tid, new LogicTuple(arg0), (Long)null);
		}catch(TucsonOperationNotPossibleException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}catch(UnreachableNodeException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		} catch (OperationTimeOutException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		if(!op.isResultSuccess())
			unify(arg0, op.getLogicTupleResult().toTerm());
		return op.isResultSuccess();
	}
	
	public boolean nop_2(Term arg0, Term arg1){
		if(context == null)
			return false;
		TucsonTupleCentreId tid;
		try{
			tid = new TucsonTupleCentreId(arg1.toString());
		}catch(TucsonInvalidTupleCentreIdException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		ITucsonOperation op;
		try{
			op = context.nop(tid, new LogicTuple(arg0), (Long)null);
		}catch(TucsonOperationNotPossibleException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}catch(UnreachableNodeException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		} catch (OperationTimeOutException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		if(!op.isResultSuccess())
			unify(arg0, op.getLogicTupleResult().toTerm());
		return op.isResultSuccess();
	}

	public boolean get_2(Term arg0, Term arg1){
		if(context == null)
			return false;
		TucsonTupleCentreId tid;
		try{
			tid = new TucsonTupleCentreId(arg1.toString());
		}catch(TucsonInvalidTupleCentreIdException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		ITucsonOperation op;
		try{
			op = context.get(tid, (Long)null);
		}catch(TucsonOperationNotPossibleException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}catch(UnreachableNodeException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		} catch (OperationTimeOutException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		if(op.isResultSuccess()){
			unify(arg0, list2tuple(op.getLogicTupleListResult()));
		}
		return op.isResultSuccess();
	}
	
	public boolean set_2(Term arg0, Term arg1){
		if(context == null)
			return false;
		TucsonTupleCentreId tid;
		try{
			tid = new TucsonTupleCentreId(arg1.toString());
		}catch(TucsonInvalidTupleCentreIdException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		ITucsonOperation op;
		try{
			op = context.set(tid, new LogicTuple(arg0), (Long)null);
		}catch(TucsonOperationNotPossibleException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}catch(UnreachableNodeException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		} catch (OperationTimeOutException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		return op.isResultSuccess();
	}
	
	
	
	public boolean out_s_4(Term arg0, Term arg1, Term arg2, Term arg3){
		if(context == null)
			return false;
		TucsonTupleCentreId tid;
		try{
			tid = new TucsonTupleCentreId(arg3.toString());
		}catch(TucsonInvalidTupleCentreIdException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		ITucsonOperation op;
		try{
			op = context.out_s(tid, new LogicTuple(arg0), new LogicTuple(arg1), new LogicTuple(arg2), (Long)null);
		}catch(TucsonOperationNotPossibleException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}catch(UnreachableNodeException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		} catch (OperationTimeOutException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		return op.isResultSuccess();
	}

	public boolean in_s_4(Term arg0, Term arg1, Term arg2, Term arg3){
		if(context == null)
			return false;
		TucsonTupleCentreId tid;
		try{
			tid = new TucsonTupleCentreId(arg3.toString());
		}catch(TucsonInvalidTupleCentreIdException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		ITucsonOperation op;
		try{
			op = context.in_s(tid, new LogicTuple(arg0), new LogicTuple(arg1), new LogicTuple(arg2), (Long)null);
		}catch(TucsonOperationNotPossibleException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}catch(UnreachableNodeException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}catch(OperationTimeOutException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		if(op.isResultSuccess())
			unify(arg0, op.getLogicTupleResult().toTerm());
		return op.isResultSuccess();
	}

	public boolean rd_s_4(Term arg0, Term arg1, Term arg2, Term arg3){
		if(context == null)
			return false;
		TucsonTupleCentreId tid;
		try{
			tid = new TucsonTupleCentreId(arg3.toString());
		}catch(TucsonInvalidTupleCentreIdException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		ITucsonOperation op;
		try{
			op = context.rd_s(tid, new LogicTuple(arg0), new LogicTuple(arg1), new LogicTuple(arg2), (Long)null);
		}catch(TucsonOperationNotPossibleException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}catch(UnreachableNodeException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}catch(OperationTimeOutException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		if(op.isResultSuccess())
			unify(arg0, op.getLogicTupleResult().toTerm());
		return op.isResultSuccess();
	}

	public boolean inp_s_4(Term arg0, Term arg1, Term arg2, Term arg3){
		if(context == null)
			return false;
		TucsonTupleCentreId tid;
		try{
			tid = new TucsonTupleCentreId(arg3.toString());
		}catch(TucsonInvalidTupleCentreIdException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		ITucsonOperation op;
		try{
			op = context.inp_s(tid, new LogicTuple(arg0), new LogicTuple(arg1), new LogicTuple(arg2), (Long)null);
		}catch(TucsonOperationNotPossibleException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}catch(UnreachableNodeException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		} catch (OperationTimeOutException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		if(op.isResultSuccess())
			unify(arg0, op.getLogicTupleResult().toTerm());
		return op.isResultSuccess();
	}

	public boolean rdp_s_4(Term arg0, Term arg1, Term arg2, Term arg3){
		if(context == null)
			return false;
		TucsonTupleCentreId tid;
		try{
			tid = new TucsonTupleCentreId(arg3.toString());
		}catch(TucsonInvalidTupleCentreIdException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		ITucsonOperation op;
		try{
			op = context.rdp_s(tid, new LogicTuple(arg0), new LogicTuple(arg1), new LogicTuple(arg2), (Long)null);
		}catch(TucsonOperationNotPossibleException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}catch(UnreachableNodeException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		} catch (OperationTimeOutException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		if(op.isResultSuccess())
			unify(arg0, op.getLogicTupleResult().toTerm());
		return op.isResultSuccess();
	}
	
	public boolean no_s_4(Term arg0, Term arg1, Term arg2, Term arg3){
		if(context == null)
			return false;
		TucsonTupleCentreId tid;
		try{
			tid = new TucsonTupleCentreId(arg3.toString());
		}catch(TucsonInvalidTupleCentreIdException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		ITucsonOperation op;
		try{
			op = context.no_s(tid, new LogicTuple(arg0), new LogicTuple(arg1), new LogicTuple(arg2), (Long)null);
		}catch(TucsonOperationNotPossibleException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}catch(UnreachableNodeException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		} catch (OperationTimeOutException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		if(!op.isResultSuccess())
			unify(arg0, op.getLogicTupleResult().toTerm());
		return op.isResultSuccess();
	}
	
	public boolean nop_s_4(Term arg0, Term arg1, Term arg2, Term arg3){
		if(context == null)
			return false;
		TucsonTupleCentreId tid;
		try{
			tid = new TucsonTupleCentreId(arg3.toString());
		}catch(TucsonInvalidTupleCentreIdException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		ITucsonOperation op;
		try{
			op = context.nop_s(tid, new LogicTuple(arg0), new LogicTuple(arg1), new LogicTuple(arg2), (Long)null);
		}catch(TucsonOperationNotPossibleException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}catch(UnreachableNodeException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		} catch (OperationTimeOutException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		if(!op.isResultSuccess())
			unify(arg0, op.getLogicTupleResult().toTerm());
		return op.isResultSuccess();
	}

	public boolean get_s_2(Term arg0, Term arg1){
		if(context == null)
			return false;
		TucsonTupleCentreId tid;
		try{
			tid = new TucsonTupleCentreId(arg1.toString());
		}catch(TucsonInvalidTupleCentreIdException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		ITucsonOperation op;
		try{
			op = context.get_s(tid, (Long)null);
		}catch(TucsonOperationNotPossibleException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}catch(UnreachableNodeException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		} catch (OperationTimeOutException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		} catch (InvalidTupleOperationException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		if(op.isResultSuccess()){
			unify(arg0, list2tuple(op.getLogicTupleListResult()));
		}
		return op.isResultSuccess();
	}
	
	public boolean set_s_2(Term arg0, Term arg1){
		if(context == null)
			return false;
		TucsonTupleCentreId tid;
		try{
			tid = new TucsonTupleCentreId(arg1.toString());
		}catch(TucsonInvalidTupleCentreIdException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		ITucsonOperation op;
		try{
			op = context.set_s(tid, new LogicTuple(arg0), (Long)null);
		}catch(TucsonOperationNotPossibleException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}catch(UnreachableNodeException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		} catch (OperationTimeOutException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		return op.isResultSuccess();
	}
	
	
	
	public boolean uin_2(Term arg0, Term arg1){
		if(context == null)
			return false;
		TucsonTupleCentreId tid;
		try{
			tid = new TucsonTupleCentreId(arg1.toString());
		}catch(TucsonInvalidTupleCentreIdException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		ITucsonOperation op;
		try{
			op = context.uin(tid, new LogicTuple(arg0), (Long)null);
		}catch(TucsonOperationNotPossibleException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}catch(UnreachableNodeException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}catch(OperationTimeOutException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		if(op.isResultSuccess())
			unify(arg0, op.getLogicTupleResult().toTerm());
		return op.isResultSuccess();
	}

	public boolean urd_2(Term arg0, Term arg1){
		if(context == null)
			return false;
		TucsonTupleCentreId tid;
		try{
			tid = new TucsonTupleCentreId(arg1.toString());
		}catch(TucsonInvalidTupleCentreIdException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		ITucsonOperation op;
		try{
			op = context.urd(tid, new LogicTuple(arg0), (Long)null);
		}catch(TucsonOperationNotPossibleException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}catch(UnreachableNodeException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}catch(OperationTimeOutException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		if(op.isResultSuccess())
			unify(arg0, op.getLogicTupleResult().toTerm());
		return op.isResultSuccess();
	}

	public boolean uinp_2(Term arg0, Term arg1){
		if(context == null)
			return false;
		TucsonTupleCentreId tid;
		try{
			tid = new TucsonTupleCentreId(arg1.toString());
		}catch(TucsonInvalidTupleCentreIdException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		ITucsonOperation op;
		try{
			op = context.uinp(tid, new LogicTuple(arg0), (Long)null);
		}catch(TucsonOperationNotPossibleException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}catch(UnreachableNodeException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		} catch (OperationTimeOutException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		if(op.isResultSuccess())
			unify(arg0, op.getLogicTupleResult().toTerm());
		return op.isResultSuccess();
	}

	public boolean urdp_2(Term arg0, Term arg1){
		if(context == null)
			return false;
		TucsonTupleCentreId tid;
		try{
			tid = new TucsonTupleCentreId(arg1.toString());
		}catch(TucsonInvalidTupleCentreIdException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		ITucsonOperation op;
		try{
			op = context.urdp(tid, new LogicTuple(arg0), (Long)null);
		}catch(TucsonOperationNotPossibleException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}catch(UnreachableNodeException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		} catch (OperationTimeOutException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		if(op.isResultSuccess())
			unify(arg0, op.getLogicTupleResult().toTerm());
		return op.isResultSuccess();
	}
	
	public boolean uno_2(Term arg0, Term arg1){
		if(context == null)
			return false;
		TucsonTupleCentreId tid;
		try{
			tid = new TucsonTupleCentreId(arg1.toString());
		}catch(TucsonInvalidTupleCentreIdException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		ITucsonOperation op;
		try{
			op = context.uno(tid, new LogicTuple(arg0), (Long)null);
		}catch(TucsonOperationNotPossibleException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}catch(UnreachableNodeException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		} catch (OperationTimeOutException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		if(!op.isResultSuccess())
			unify(arg0, op.getLogicTupleResult().toTerm());
		return op.isResultSuccess();
	}
	
	public boolean unop_2(Term arg0, Term arg1){
		if(context == null)
			return false;
		TucsonTupleCentreId tid;
		try{
			tid = new TucsonTupleCentreId(arg1.toString());
		}catch(TucsonInvalidTupleCentreIdException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		ITucsonOperation op;
		try{
			op = context.unop(tid, new LogicTuple(arg0), (Long)null);
		}catch(TucsonOperationNotPossibleException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}catch(UnreachableNodeException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		} catch (OperationTimeOutException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		if(!op.isResultSuccess())
			unify(arg0, op.getLogicTupleResult().toTerm());
		return op.isResultSuccess();
	}
	
	public boolean out_all_2(Term arg0, Term arg1){
		if(context == null)
			return false;
		TucsonTupleCentreId tid;
		try{
			tid = new TucsonTupleCentreId(arg1.toString());
		}catch(TucsonInvalidTupleCentreIdException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		ITucsonOperation op;
		try{
			op = context.out_all(tid, new LogicTuple(arg0), (Long)null);
		}catch(TucsonOperationNotPossibleException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}catch(UnreachableNodeException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		} catch (OperationTimeOutException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		return op.isResultSuccess();
	}
	
	public boolean in_all_3(Term arg0, Term arg1, Term arg2){
		if(context == null)
			return false;
		TucsonTupleCentreId tid;
		try{
			tid = new TucsonTupleCentreId(arg2.toString());
		}catch(TucsonInvalidTupleCentreIdException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		ITucsonOperation op;
		try{
			op = context.in_all(tid, new LogicTuple(arg0), (Long)null);
		}catch(TucsonOperationNotPossibleException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}catch(UnreachableNodeException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		} catch (OperationTimeOutException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		if(op.isResultSuccess()){
			unify(arg1, list2tuple(op.getLogicTupleListResult()));
		}
		return op.isResultSuccess();
	}
	
	public boolean rd_all_3(Term arg0, Term arg1, Term arg2){
		if(context == null)
			return false;
		TucsonTupleCentreId tid;
		try{
			tid = new TucsonTupleCentreId(arg2.toString());
		}catch(TucsonInvalidTupleCentreIdException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		ITucsonOperation op;
		try{
			op = context.rd_all(tid, new LogicTuple(arg0), (Long)null);
		}catch(TucsonOperationNotPossibleException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}catch(UnreachableNodeException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		} catch (OperationTimeOutException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		if(op.isResultSuccess()){
			unify(arg1, list2tuple(op.getLogicTupleListResult()));
		}
		return op.isResultSuccess();
	}
	
	public boolean no_all_3(Term arg0, Term arg1, Term arg2){
		if(context == null)
			return false;
		TucsonTupleCentreId tid;
		try{
			tid = new TucsonTupleCentreId(arg2.toString());
		}catch(TucsonInvalidTupleCentreIdException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		ITucsonOperation op;
		try{
			op = context.no_all(tid, new LogicTuple(arg0), (Long)null);
		}catch(TucsonOperationNotPossibleException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}catch(UnreachableNodeException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		} catch (OperationTimeOutException e) {
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		if(op.isResultSuccess()){
			unify(arg1, list2tuple(op.getLogicTupleListResult()));
		}
		return op.isResultSuccess();
	}
	
	

	public boolean get_context_1(Struct id){
		if(context != null){
			try{
				context.exit();
			}catch(TucsonOperationNotPossibleException e){
				System.err.println("[Tucson2PLibrary]: " + e);
				e.printStackTrace();
				return false;
			}
		}
		try{
			this.agentId = new TucsonAgentId(id.toString());
		}catch(TucsonInvalidAgentIdException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		context = TucsonMetaACC.getContext(agentId);
		return true;
	}
	
	public boolean exit_0(){
		try{
			context.exit();
		}catch(TucsonOperationNotPossibleException e){
			System.err.println("[Tucson2PLibrary]: " + e);
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	
	private Term list2tuple(List<LogicTuple> list){
		Term [] termArray = new Term[list.size()];
		Iterator<LogicTuple> it = list.iterator();
		int i=0;
		while(it.hasNext()){
			termArray[i] = ((LogicTuple)it.next()).toTerm();
			i++;
		}
		return new Struct(termArray);
	}
	
}
