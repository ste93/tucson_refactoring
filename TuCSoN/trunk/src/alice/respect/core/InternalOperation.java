/*
 * ReSpecT - Copyright (C) aliCE team at deis.unibo.it
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
package alice.respect.core;

import alice.logictuple.*;

/**
 * Represents an internal operation
 * of a ReSpecT virtual machine.
 *
 * The possible argument of the operation
 * is represented by a logic tuple
 *  
 * @author aricci
 *
 */
public class InternalOperation {
	
	private LogicTuple arg;
	private LogicTuple result;
	
	private static final int OPTYPE_OUTR = 1;
	private static final int OPTYPE_INR = 2;
	private static final int OPTYPE_RDR = 3;
	private static final int OPTYPE_NOR = 4;
	private static final int OPTYPE_GET_R = 5;
	private static final int OPTYPE_SET_R = 6;
	
	private static final int OPTYPE_UINR = 7;
	private static final int OPTYPE_URDR = 8;
	private static final int OPTYPE_UNOR = 9;
	private static final int OPTYPE_OUT_ALLR = 10;
	private static final int OPTYPE_IN_ALLR = 11;
	private static final int OPTYPE_RD_ALLR = 12;
	private static final int OPTYPE_NO_ALLR = 13;

	private static final int OPTYPE_SPAWN_R = 66;
	
	private static final int OPTYPE_OUT_SR = 14; 
	private static final int OPTYPE_IN_SR = 15;
	private static final int OPTYPE_RD_SR = 16;
	private static final int OPTYPE_NO_SR = 17;
	private static final int OPTYPE_GET_S_R = 18;
	private static final int OPTYPE_SET_S_R = 19;
	
	private static final int OPTYPE_GET_ENV = 77;
	private static final int OPTYPE_SET_ENV = 78;
 	
 	private int type;
 	
	private InternalOperation(int type,LogicTuple arg){
		this.arg=arg;	
		this.type=type;
	}
	
	public LogicTuple getArgument(){
		return arg;	
	}

	public void setResult(LogicTuple t){
		result=t;
	}
	
	public LogicTuple getResult(){
		return result;
	}
	
	public boolean isOutR(){
		return type == OPTYPE_OUTR;
	}
	
	public boolean isInR(){
		return type == OPTYPE_INR;
	}
	
	public boolean isRdR(){
		return type == OPTYPE_RDR;
	
	}
	public boolean isNoR(){
		return type == OPTYPE_NOR;
	}
	
	public boolean isGetR(){
		return type==OPTYPE_GET_R;
	}
	
	public boolean isSetR(){
		return type==OPTYPE_SET_R;
	}
	
	public boolean isUinR(){
		return type==OPTYPE_UINR;
	}
	
	public boolean isUrdR(){
		return type==OPTYPE_URDR;
	}
	
	public boolean isUnoR(){
		return type==OPTYPE_UNOR;
	}
	
	public boolean isOutAllR(){
		return type==OPTYPE_OUT_ALLR;
	}
	
	public boolean isInAllR(){
		return type==OPTYPE_IN_ALLR;
	}
	
	public boolean isRdAllR(){
		return type==OPTYPE_RD_ALLR;
	}
	
	public boolean isNoAllR(){
		return type==OPTYPE_NO_ALLR;
	}
	
	public boolean isOut_sR(){
		return type == OPTYPE_OUT_SR; 
	}
	
	public boolean isIn_sR(){
		return type == OPTYPE_IN_SR;
	}
	
	public boolean isRd_sR(){
		return type == OPTYPE_RD_SR;
	}
	
	public boolean isNo_sR(){
		return type == OPTYPE_NO_SR;
	}
	
	public boolean isGet_sR(){
		return type==OPTYPE_GET_S_R;
	}
	
	public boolean isSet_sR(){
		return type==OPTYPE_SET_S_R;
	}
	
	public boolean isGetEnv(){
		return type==OPTYPE_GET_ENV;
	}
	
	public boolean isSetEnv(){
		return type==OPTYPE_SET_ENV;
	}
	
	public boolean isSpawnR(){
		return type == OPTYPE_SPAWN_R;
	}
	
	static public InternalOperation makeOutR(LogicTuple t){
		return new InternalOperation(OPTYPE_OUTR,t);
	}
	
	static public InternalOperation makeInR(LogicTuple t){
		return new InternalOperation(OPTYPE_INR,t);
	}

	static public InternalOperation makeRdR(LogicTuple t){
		return new InternalOperation(OPTYPE_RDR,t);
	}

	static public InternalOperation makeNoR(LogicTuple t){
		return new InternalOperation(OPTYPE_NOR,t);
	}
	
	static public InternalOperation makeGetR(LogicTuple t){
		return new InternalOperation(OPTYPE_GET_R, t);
	}
	
	static public InternalOperation makeSetR(LogicTuple t){
		return new InternalOperation(OPTYPE_SET_R, t);
	}

	public static InternalOperation makeOutAllR(LogicTuple t){
		return new InternalOperation(OPTYPE_OUT_ALLR,t);
	}
	
	public static InternalOperation makeInAllR(LogicTuple t){
		return new InternalOperation(OPTYPE_IN_ALLR,t);
	}
	
	public static InternalOperation makeRdAllR(LogicTuple t){
		return new InternalOperation(OPTYPE_RD_ALLR,t);
	}
	
	public static InternalOperation makeNoAllR(LogicTuple t){
		return new InternalOperation(OPTYPE_NO_ALLR,t);
	}
	
	public static InternalOperation makeUrdR(LogicTuple t){
		return new InternalOperation(OPTYPE_URDR,t);
	}
	
	public static InternalOperation makeUinR(LogicTuple t){
		return new InternalOperation(OPTYPE_UINR,t);
	}
	
	public static InternalOperation makeUnoR(LogicTuple t){
		return new InternalOperation(OPTYPE_UNOR,t);
	}
	
	static public InternalOperation makeOut_sR(LogicTuple t){
		return new InternalOperation(OPTYPE_OUT_SR, t);
	}
	
	static public InternalOperation makeIn_sR(LogicTuple t){
		return new InternalOperation(OPTYPE_IN_SR, t);
	}
	
	static public InternalOperation makeRd_sR(LogicTuple t){
		return new InternalOperation(OPTYPE_RD_SR, t);
	}
	
	static public InternalOperation makeNo_sR(LogicTuple t){
		return new InternalOperation(OPTYPE_NO_SR,t);
	}
	
	static public InternalOperation makeGet_sR(LogicTuple t){
		return new InternalOperation(OPTYPE_GET_S_R, t);
	}
	
	static public InternalOperation makeSet_sR(LogicTuple t){
		return new InternalOperation(OPTYPE_SET_S_R, t);
	}
	
	static public InternalOperation makeSpawnR(LogicTuple t){
		return new InternalOperation(OPTYPE_SPAWN_R,t);
	}

	static public InternalOperation makeGetEnv(LogicTuple t){
		return new InternalOperation(OPTYPE_GET_ENV,t);
	}
	
	static public InternalOperation makeSetEnv(LogicTuple t){
		return new InternalOperation(OPTYPE_SET_ENV,t);
	}
	
}
