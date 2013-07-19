package alice.tucson.examples.helloWorld;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.tucson.api.AbstractTucsonAgent;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.SynchACC;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

/*
 * 1) Extend alice.tucson.api.TucsonAgent base class.
 */
/**
 * Java TuCSoN Agent extending alice.tucson.api.TucsonAgent base class.
 * 
 * @author s.mariani@unibo.it
 */
public class HelloWorldAgent extends AbstractTucsonAgent {

    /**
     * @param args
     *            the name of the TuCSoN coordinable (optional).
     */
    public static void main(final String[] args) {
        String aid = null;
        if (args.length == 1) {
            aid = args[0];
        } else {
            aid = "helloWorldAgent";
        }
        /*
         * 10) Instantiate your agent and 11) start executing its 'main()' using
         * method 'go()'.
         */
        try {
            new HelloWorldAgent(aid).go();
        } catch (final TucsonInvalidAgentIdException e) {
            e.printStackTrace();
        }
    }

    /*
     * 2) Choose one of the given constructors.
     */
    /**
     * 
     * @param aid
     *            the String representation of a valid TuCSoN agent identifier
     * @throws TucsonInvalidAgentIdException
     *             if the given String does not represent a valid TuCSoN agent
     *             identifier
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
            e.printStackTrace();
        } catch (final InvalidLogicTupleException e) {
            /*
             * String to be parsed is not in a valid Prolog syntax.
             */
            e.printStackTrace();
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
        }
    }

}
