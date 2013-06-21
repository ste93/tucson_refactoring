/*
 * ReSpecT - Copyright (C) aliCE team at deis.unibo.it This library is free
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
package alice.respect.core;

import alice.logictuple.LogicTuple;
import alice.logictuple.TupleArgument;

/**
 * Represents an internal operation of a ReSpecT virtual machine.
 * 
 * The possible argument of the operation is represented by a logic tuple
 * 
 * @author aricci
 * 
 */
public class InternalOperation {

    private static final int OPTYPE_GET_ENV = 77;
    private static final int OPTYPE_GET_R = 5;

    private static final int OPTYPE_GET_S_R = 18;
    private static final int OPTYPE_IN_ALLR = 11;
    private static final int OPTYPE_IN_SR = 15;
    private static final int OPTYPE_INR = 2;
    private static final int OPTYPE_NO_ALLR = 13;
    private static final int OPTYPE_NO_SR = 17;

    private static final int OPTYPE_NOR = 4;
    private static final int OPTYPE_OUT_ALLR = 10;
    private static final int OPTYPE_OUT_SR = 14;
    private static final int OPTYPE_OUTR = 1;
    private static final int OPTYPE_RD_ALLR = 12;
    private static final int OPTYPE_RD_SR = 16;
    private static final int OPTYPE_RDR = 3;

    private static final int OPTYPE_SET_ENV = 78;

    private static final int OPTYPE_SET_R = 6;
    private static final int OPTYPE_SET_S_R = 19;
    private static final int OPTYPE_SPAWN_R = 66;
    private static final int OPTYPE_UINR = 7;
    private static final int OPTYPE_UNOR = 9;
    private static final int OPTYPE_URDR = 8;

    static public InternalOperation makeGet_sR(final LogicTuple t) {
        return new InternalOperation(InternalOperation.OPTYPE_GET_S_R, t);
    }

    static public InternalOperation makeGetEnv(final LogicTuple t) {
        return new InternalOperation(InternalOperation.OPTYPE_GET_ENV, t);
    }

    static public InternalOperation makeGetR(final LogicTuple t) {
        return new InternalOperation(InternalOperation.OPTYPE_GET_R, t);
    }

    static public InternalOperation makeIn_sR(final LogicTuple t) {
        return new InternalOperation(InternalOperation.OPTYPE_IN_SR, t);
    }

    public static InternalOperation makeInAllR(final LogicTuple t) {
        return new InternalOperation(InternalOperation.OPTYPE_IN_ALLR, t);
    }

    static public InternalOperation makeInR(final LogicTuple t) {
        return new InternalOperation(InternalOperation.OPTYPE_INR, t);
    }

    static public InternalOperation makeNo_sR(final LogicTuple t) {
        return new InternalOperation(InternalOperation.OPTYPE_NO_SR, t);
    }

    public static InternalOperation makeNoAllR(final LogicTuple t) {
        return new InternalOperation(InternalOperation.OPTYPE_NO_ALLR, t);
    }

    static public InternalOperation makeNoR(final LogicTuple t) {
        return new InternalOperation(InternalOperation.OPTYPE_NOR, t);
    }

    static public InternalOperation makeOut_sR(final LogicTuple t) {
        return new InternalOperation(InternalOperation.OPTYPE_OUT_SR, t);
    }

    public static InternalOperation makeOutAllR(final LogicTuple t) {
        return new InternalOperation(InternalOperation.OPTYPE_OUT_ALLR, t);
    }

    static public InternalOperation makeOutR(final LogicTuple t) {
        return new InternalOperation(InternalOperation.OPTYPE_OUTR, t);
    }

    static public InternalOperation makeRd_sR(final LogicTuple t) {
        return new InternalOperation(InternalOperation.OPTYPE_RD_SR, t);
    }

    public static InternalOperation makeRdAllR(final LogicTuple t) {
        return new InternalOperation(InternalOperation.OPTYPE_RD_ALLR, t);
    }

    static public InternalOperation makeRdR(final LogicTuple t) {
        return new InternalOperation(InternalOperation.OPTYPE_RDR, t);
    }

    static public InternalOperation makeSet_sR(final LogicTuple t) {
        return new InternalOperation(InternalOperation.OPTYPE_SET_S_R, t);
    }

    static public InternalOperation makeSetEnv(final LogicTuple t) {
        return new InternalOperation(InternalOperation.OPTYPE_SET_ENV, t);
    }

    static public InternalOperation makeSetR(final LogicTuple t) {
        return new InternalOperation(InternalOperation.OPTYPE_SET_R, t);
    }

    static public InternalOperation makeSpawnR(final LogicTuple t) {
        return new InternalOperation(InternalOperation.OPTYPE_SPAWN_R, t);
    }

    public static InternalOperation makeUinR(final LogicTuple t) {
        return new InternalOperation(InternalOperation.OPTYPE_UINR, t);
    }

    public static InternalOperation makeUnoR(final LogicTuple t) {
        return new InternalOperation(InternalOperation.OPTYPE_UNOR, t);
    }

    public static InternalOperation makeUrdR(final LogicTuple t) {
        return new InternalOperation(InternalOperation.OPTYPE_URDR, t);
    }

    private final LogicTuple arg;

    private LogicTuple result;

    private final int type;

    private InternalOperation(final int t, final LogicTuple a) {
        this.arg = a;
        this.type = t;
    }

    public LogicTuple getArgument() {
        return this.arg;
    }

    public LogicTuple getResult() {
        return this.result;
    }

    public boolean isGet_sR() {
        return this.type == InternalOperation.OPTYPE_GET_S_R;
    }

    public boolean isGetEnv() {
        return this.type == InternalOperation.OPTYPE_GET_ENV;
    }

    public boolean isGetR() {
        return this.type == InternalOperation.OPTYPE_GET_R;
    }

    public boolean isIn_sR() {
        return this.type == InternalOperation.OPTYPE_IN_SR;
    }

    public boolean isInAllR() {
        return this.type == InternalOperation.OPTYPE_IN_ALLR;
    }

    public boolean isInR() {
        return this.type == InternalOperation.OPTYPE_INR;
    }

    public boolean isNo_sR() {
        return this.type == InternalOperation.OPTYPE_NO_SR;
    }

    public boolean isNoAllR() {
        return this.type == InternalOperation.OPTYPE_NO_ALLR;
    }

    public boolean isNoR() {
        return this.type == InternalOperation.OPTYPE_NOR;
    }

    public boolean isOut_sR() {
        return this.type == InternalOperation.OPTYPE_OUT_SR;
    }

    public boolean isOutAllR() {
        return this.type == InternalOperation.OPTYPE_OUT_ALLR;
    }

    public boolean isOutR() {
        return this.type == InternalOperation.OPTYPE_OUTR;
    }

    public boolean isRd_sR() {
        return this.type == InternalOperation.OPTYPE_RD_SR;
    }

    public boolean isRdAllR() {
        return this.type == InternalOperation.OPTYPE_RD_ALLR;
    }

    public boolean isRdR() {
        return this.type == InternalOperation.OPTYPE_RDR;

    }

    public boolean isSet_sR() {
        return this.type == InternalOperation.OPTYPE_SET_S_R;
    }

    public boolean isSetEnv() {
        return this.type == InternalOperation.OPTYPE_SET_ENV;
    }

    public boolean isSetR() {
        return this.type == InternalOperation.OPTYPE_SET_R;
    }

    public boolean isSpawnR() {
        return this.type == InternalOperation.OPTYPE_SPAWN_R;
    }

    public boolean isUinR() {
        return this.type == InternalOperation.OPTYPE_UINR;
    }

    public boolean isUnoR() {
        return this.type == InternalOperation.OPTYPE_UNOR;
    }

    public boolean isUrdR() {
        return this.type == InternalOperation.OPTYPE_URDR;
    }

    public void setResult(final LogicTuple t) {
        this.result = t;
    }

    @Override
    public String toString() {
        return this.toTuple().toString();
    }

    public LogicTuple toTuple() {
        LogicTuple t = null;
        if (this.result != null) {
            t = this.getResult();
        } else {
            t = this.getArgument();
        }
        String opName;
        if (this.isSpawnR()) {
            opName = "spawn";
        } else if (this.isOutR()) {
            opName = "out";
        } else if (this.isInR()) {
            opName = "in";
        } else if (this.isRdR()) {
            opName = "rd";
        } else if (this.isNoR()) {
            opName = "no";
        } else if (this.isOutAllR()) {
            opName = "out_all";
        } else if (this.isInAllR()) {
            opName = "in_all";
        } else if (this.isRdAllR()) {
            opName = "rd_all";
        } else if (this.isNoAllR()) {
            opName = "no_all";
        } else if (this.isUrdR()) {
            opName = "urd";
        } else if (this.isUinR()) {
            opName = "uin";
        } else if (this.isUnoR()) {
            opName = "uno";
        } else if (this.isGetR()) {
            opName = "get";
        } else if (this.isSetR()) {
            opName = "set";
        } else if (this.isOut_sR()) {
            opName = "out_s";
        } else if (this.isIn_sR()) {
            opName = "in_s";
        } else if (this.isRd_sR()) {
            opName = "rd_s";
        } else if (this.isNo_sR()) {
            opName = "no_s";
        } else if (this.isGet_sR()) {
            opName = "get_s";
        } else if (this.isSet_sR()) {
            opName = "set_s";
        } else if (this.isGetEnv()) {
            return t;
        } else if (this.isSetEnv()) {
            return t;
        } else {
            opName = "unknownOp";
        }
        return new LogicTuple(opName, new TupleArgument(t.toTerm()));
    }

}
