/**
 * Thermostat.java
 */
package sm.nonjunit.situatedness;

import java.io.IOException;

import alice.logictuple.LogicTuple;
import alice.logictuple.TupleArgument;
import alice.logictuple.Value;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.logictuple.exceptions.InvalidTupleOperationException;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonMetaACC;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.examples.utilities.Utils;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

/**
 * @author ste (mailto: s.mariani@unibo.it) on 05/nov/2013
 * 
 */
public class Thermostat {

    private final static String DEFAULT_HOST = "localhost";
    private final static String DEFAULT_PORT = "20504";
    private final static int HIGH = 22;
    private final static int ITERS = 10;
    private final static int LOW = 18;

    /**
     * @param args
     */
    public static void main(final String[] args) {
        try {
            final TucsonAgentId aid = new TucsonAgentId("thermostat");
            final EnhancedSynchACC acc =
                    TucsonMetaACC.getContext(aid, Thermostat.DEFAULT_HOST,
                            Integer.valueOf(Thermostat.DEFAULT_PORT));
            final TucsonTupleCentreId configTc =
                    new TucsonTupleCentreId("envConfigTC",
                            Thermostat.DEFAULT_HOST, Thermostat.DEFAULT_PORT);
            /* Set up temperature */
            final TucsonTupleCentreId tempTc =
                    new TucsonTupleCentreId("tempTc", Thermostat.DEFAULT_HOST,
                            Thermostat.DEFAULT_PORT);
            int bootT;
            do {
                // 10 < bootT < LOW || HIGH < bootT < 30
                bootT = Math.round((int) (Math.random() * 20)) + 10;
            } while ((bootT >= Thermostat.LOW) && (bootT <= Thermostat.HIGH));
            final LogicTuple bootTemp =
                    new LogicTuple("temp", new Value(bootT));
            acc.out(tempTc, bootTemp, null);
            /* Set up sensor */
            Thermostat.log(aid.toString(), "Set up sensor...");
            final TucsonTupleCentreId sensorTc =
                    new TucsonTupleCentreId("sensorTc",
                            Thermostat.DEFAULT_HOST, Thermostat.DEFAULT_PORT);
            try {
                acc.setS(
                        sensorTc,
                        Utils.fileToString("sm/nonjunit/situatedness/sensorSpec.rsp"),
                        null);
            } catch (final IOException e) {
                e.printStackTrace();
            }
            final LogicTuple sensorTuple =
                    new LogicTuple("createTransducerSensor", new TupleArgument(
                            sensorTc.toTerm()), new Value(
                            "sm.nonjunit.situatedness.SensorTransducer"),
                            new Value("sensorTransducer"), new Value(
                                    "sm.nonjunit.situatedness.ActualSensor"),
                            new Value("sensor"));
            acc.out(configTc, sensorTuple, null);
            /* Set up actuator */
            Thermostat.log(aid.toString(), "Set up actuator...");
            final TucsonTupleCentreId actuatorTc =
                    new TucsonTupleCentreId("actuatorTc",
                            Thermostat.DEFAULT_HOST, Thermostat.DEFAULT_PORT);
            try {
                acc.setS(
                        actuatorTc,
                        Utils.fileToString("sm/nonjunit/situatedness/actuatorSpec.rsp"),
                        null);
            } catch (final IOException e) {
                e.printStackTrace();
            }
            final LogicTuple actuatorTuple =
                    new LogicTuple(
                            "createTransducerActuator",
                            new TupleArgument(actuatorTc.toTerm()),
                            new Value(
                                    "sm.nonjunit.situatedness.ActuatorTransducer"),
                            new Value("actuatorTransducer"), new Value(
                                    "sm.nonjunit.situatedness.ActualActuator"),
                            new Value("actuator"));
            acc.out(configTc, actuatorTuple, null);
            /* Start perception-reason-action loop */
            Thermostat.log(aid.toString(),
                    "Start perception-reason-action loop...");
            LogicTuple template;
            ITucsonOperation op;
            int temp;
            LogicTuple action = null;
            for (int i = 0; i < Thermostat.ITERS; i++) {
                Thread.sleep(3000);
                /* Perception */
                template = LogicTuple.parse("sense(temp(_))");
                op = acc.in(sensorTc, template, null);
                if (op.isResultSuccess()) {
                    temp =
                            op.getLogicTupleResult().getArg(0).getArg(0)
                                    .intValue();
                    Thermostat.log(aid.toString(), "temp is " + temp
                            + " hence...");
                    /* Reason */
                    if ((temp >= Thermostat.LOW) && (temp <= Thermostat.HIGH)) {
                        Thermostat.log(aid.toString(), "...nothing to do");
                        continue;
                    } else if (temp < Thermostat.LOW) {
                        Thermostat.log(aid.toString(), "...heating up");
                        action = LogicTuple.parse("act(temp(" + ++temp + "))");
                    } else if (temp > Thermostat.HIGH) {
                        Thermostat.log(aid.toString(), "...cooling down");
                        action = LogicTuple.parse("act(temp(" + --temp + "))");
                    }
                    /* Action */
                    acc.out(actuatorTc, action, null);
                }
            }
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
        } catch (final TucsonInvalidAgentIdException e) {
            e.printStackTrace();
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
        } catch (final InvalidLogicTupleException e) {
            e.printStackTrace();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        } catch (final InvalidTupleOperationException e) {
            e.printStackTrace();
        }
    }

    private static void log(final String who, final String msg) {
        System.out.println("[" + who + "]: " + msg);
    }

}
