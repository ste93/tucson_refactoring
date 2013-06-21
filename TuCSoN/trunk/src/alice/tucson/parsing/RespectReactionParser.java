package alice.tucson.parsing;

import alice.logictuple.LogicTuple;
import alice.logictuple.TupleArgument;
import alice.logictuple.exceptions.InvalidTupleOperationException;
import alice.util.Tools;

/**
 * 
 * @author ste
 * 
 */
public class RespectReactionParser {

    private int nGuards;
    private int nTimes;
    private String spec;
    private final LogicTuple t;
    boolean flag;

    public RespectReactionParser(final LogicTuple lt) {
        this.t = lt;
        this.spec = "'reaction(";
        this.nGuards = 0;
        this.flag = false;
        this.nTimes = 0;
    }

    /**
     * 
     * @return
     */
    public String parse() {
        try {
            if (this.t.getName().equals("[]")) {
                return "";
            }
            for (int i = 0; i < this.t.getArity(); i++) {
                this.parse(this.t.getArg(i));
            }
        } catch (final InvalidTupleOperationException e) {
            e.printStackTrace();
        }
        RespectReactionParser.log("spec = "
                + this.spec.substring(1, this.spec.length() - 1));
        return this.spec.substring(1, this.spec.length() - 1);
    }

    /**
     * 
     * @param arg
     * @return
     * @throws InvalidTupleOperationException
     */
    private static boolean bigFatCondition(final TupleArgument arg)
            throws InvalidTupleOperationException {
        return arg.getName().equals("request")
                || arg.getName().equals("response")
                || arg.getName().equals("success")
                || arg.getName().equals("failure")
                || arg.getName().equals("endo") || arg.getName().equals("exo")
                || arg.getName().equals("intra")
                || arg.getName().equals("inter")
                || arg.getName().equals("from_agent")
                || arg.getName().equals("to_agent")
                || arg.getName().equals("from_tc")
                || arg.getName().equals("to_tc")
                || arg.getName().equals("before")
                || arg.getName().equals("after")
                || arg.getName().equals("from_agent")
                || arg.getName().equals("invocation")
                || arg.getName().equals("inv") || arg.getName().equals("req")
                || arg.getName().equals("pre")
                || arg.getName().equals("completion")
                || arg.getName().equals("compl")
                || arg.getName().equals("resp") || arg.getName().equals("post")
                || arg.getName().equals("between")
                || arg.getName().equals("operation")
                || arg.getName().equals("link_out")
                || arg.getName().equals("link_in")
                || arg.getName().equals("internal");
    }

    /**
     * 
     * @param msg
     */
    private static void log(final String msg) {
        System.out.println("[RespectReactionParser]: " + msg);
    }

    /**
     * 
     * @param arg
     */
    private void parse(final TupleArgument arg) {
        try {
            if (arg.toString().equals("[]")) {
                if (this.nTimes == 1) {
                    this.spec =
                            (this.spec.substring(0, this.spec.length() - 1))
                                    + ")).'";
                } else {
                    this.spec =
                            (this.spec.substring(0, this.spec.length() - 1))
                                    + ".'";
                }
            } else if (Tools.removeApices(arg.getName()).equals(",")) {
                for (int i = 0; i < arg.getArity(); i++) {
                    this.parse(arg.getArg(i));
                }
                if (this.flag) {
                    this.nTimes++;
                    if (this.nTimes == 1) {
                        this.spec =
                                this.spec.substring(0, this.spec.length() - 1);
                        this.spec += ")).";
                    }
                }
            } else if (Tools.removeApices(arg.getName()).equals(".")) {
                this.spec += " reaction(";
                this.nGuards = 0;
                this.flag = false;
                this.nTimes = 0;
                for (int i = 0; i < arg.getArity(); i++) {
                    this.parse(arg.getArg(i));
                }
            } else {
                if (RespectReactionParser.bigFatCondition(arg)) {
                    this.nGuards++;
                    if (this.nGuards == 1) {
                        this.flag = false;
                        this.spec += "(" + arg + ",";
                    } else {
                        this.spec += arg + ",";
                    }
                } else {
                    if (this.nGuards > 0) {
                        this.nGuards = 0;
                        this.flag = true;
                        this.spec =
                                this.spec.substring(0, this.spec.length() - 1);
                        this.spec += "),(" + arg + ",";
                    } else {
                        this.spec += arg + ",";
                    }
                }
            }
        } catch (final InvalidTupleOperationException e) {
            e.printStackTrace();
        }
    }

}
