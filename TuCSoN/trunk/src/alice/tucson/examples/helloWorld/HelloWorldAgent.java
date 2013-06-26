package alice.tucson.examples.helloWorld;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.SynchACC;
import alice.tucson.api.TucsonAgent;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

/**
 * Java TuCSoN Agent extending alice.tucson.api.TucsonAgent base class.
 * 
 * @author s.mariani@unibo.it
 */
/*
 * 1) Extend alice.tucson.api.TucsonAgent base class.
 */
public class HelloWorldAgent extends TucsonAgent {

    /**
     * @param args
     *            the name of the TuCSoN coordinable (optional).
     */
    public static void main(final String[] args) {
        String aid = null;
        if (args.length == 0) {
            aid = "helloWorldAgent";
        } else if (args.length == 1) {
            aid = args[0];
        }
        /*
         * 10) Instantiate your agent and 11) start executing its 'main()' using
         * method 'go()'.
         */
        try {
            new HelloWorldAgent(aid).go();
        } catch (final TucsonInvalidAgentIdException e) {
            // TODO Properly handle Exception
        }
    }

    /*
     * 2) Choose one of the given constructors.
     */
    public HelloWorldAgent(final String aid)
            throws TucsonInvalidAgentIdException {
        super(aid);
    }

    /*
     * To override only for asynchronous coordination operations.
     */
    @Override
    public void operationCompleted(final ITucsonOperation arg0) {
        /*
         * 
         */
    }

    /*
     * 3) To be overridden by TuCSoN programmers with their agent business
     * logic.
     */
    @Override
    protected void main() {
        /*
         * 4) Get your ACC from the super-class.
         */
        final SynchACC acc = this.getContext();
        /*
         * 5) Define the tuplecentre target of your coordination operations.
         */
        try {
            final TucsonTupleCentreId tid =
                    new TucsonTupleCentreId("default", "localhost", "20504");
            /*
             * 6) Build the tuple e.g. using TuCSoN parsing facilities.
             */
            final LogicTuple tuple = LogicTuple.parse("hello(world)");
            /*
             * 7) Perform the coordination operation using the preferred
             * coordination primitive.
             */
            ITucsonOperation op = acc.out(tid, tuple, null);
            /*
             * 8) Check requested operation success.
             */
            LogicTuple res = null;
            if (op.isResultSuccess()) {
                this.say("Operation succeeded.");
                /*
                 * 9) Get requested operation result.
                 */
                res = op.getLogicTupleResult();
                this.say("Operation result is " + res);
            } else {
                this.say("Operation failed.");
            }
            /*
             * Another success test to be sure.
             */
            final LogicTuple template = LogicTuple.parse("hello(Who)");
            op = acc.rdp(tid, template, null);
            if (op.isResultSuccess()) {
                res = op.getLogicTupleResult();
                this.say("Operation result is " + res);
            } else {
                this.say("Operation failed.");
            }
            /*
             * ACC release is automatically done by the TucsonAgent base class.
             */
        } catch (final TucsonInvalidTupleCentreIdException e) {
            // TODO Properly handle Exception
        } catch (final InvalidLogicTupleException e) {
            /*
             * String to be parsed is not in a valid Prolog syntax.
             */
            // TODO Properly handle Exception
        } catch (final TucsonOperationNotPossibleException e) {
            // TODO Properly handle Exception
        } catch (final UnreachableNodeException e) {
            // TODO Properly handle Exception
        } catch (final OperationTimeOutException e) {
            // TODO Properly handle Exception
        }
    }

}
