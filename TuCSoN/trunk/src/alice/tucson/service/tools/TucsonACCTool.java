package alice.tucson.service.tools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import alice.logictuple.LogicTuple;
import alice.logictuple.TupleArgument;
import alice.logictuple.Value;
import alice.logictuple.Var;
import alice.logictuple.exceptions.InvalidVarNameException;
import alice.tucson.api.EnhancedACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.exceptions.AgentNotAllowedException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.rbac.Policy;
import alice.tucson.rbac.Role;
import alice.tucson.rbac.TucsonPolicy;
import alice.tucson.rbac.TucsonRole;
import alice.tuplecentre.api.TupleCentreId;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

public final class TucsonACCTool {

    public static boolean activateContext(final String agentAid,
            final UUID agentUUID, final String agentClass,
            final TupleCentreId tid, final EnhancedACC acc) {
        try {
            final LogicTuple template = new LogicTuple("context_request",
                    new Value(agentAid), new Var("Result"), new Value(
                            agentClass), new Value(agentUUID.toString()));
            final ITucsonOperation op = acc.inp(tid, template, (Long) null);
            if (op.isResultSuccess()) {
                final LogicTuple res = op.getLogicTupleResult();
                if (res != null
                        && res.getArg(1).getName().equalsIgnoreCase("ok")) {
                    return true;
                } else if (res != null
                        && res.getArg(1).getName().equalsIgnoreCase("failed")
                        && res.getArg(1).getArg(0).toString()
                                .equalsIgnoreCase("agent_already_present")) {
                    return true;
                }
            }
        } catch (final InvalidVarNameException e) {
            e.printStackTrace();
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static Role activateRole(final String agentAid, final UUID accUUID,
            final String agentClass, final String roleName,
            final TupleCentreId tid, final EnhancedACC acc)
            throws AgentNotAllowedException {
        if (!TucsonACCTool.activateContext(agentAid, accUUID, agentClass, tid,
                acc)) {
            return null;
        }

        Role newRole = null;
        try {
            final LogicTuple template = new LogicTuple(
                    "role_activation_request", new Value(agentAid.toString()),
                    new Value(accUUID.toString()), new Value(roleName),
                    new Var("Result"));
            final ITucsonOperation op = acc.inp(tid, template, (Long) null);
            if (op.isResultSuccess()) {
                final LogicTuple res = op.getLogicTupleResult();
                if (res != null
                        && res.getArg(3).getName().equalsIgnoreCase("ok")) {
                    final String policyName = res.getArg(3).getArg(0)
                            .toString();
                    final TupleArgument[] permissionsList = res.getArg(3)
                            .getArg(1).toArray();
                    final Policy newPolicy = TucsonPolicy.createPolicy(
                            policyName, permissionsList);
                    newRole = new TucsonRole(roleName);
                    newRole.setPolicy(newPolicy);
                } else if (res != null
                        && res.getArg(3).getName().equalsIgnoreCase("failed")) {
                    throw new AgentNotAllowedException();
                }
            }
        } catch (final InvalidVarNameException e) {
            e.printStackTrace();
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
        }

        return newRole;
    }

    public static Role activateRoleWithPolicy(final String agentAid,
            final UUID accUUID, final String agentClass, final Policy policy,
            final TupleCentreId tid, final EnhancedACC acc) {
        if (!TucsonACCTool.activateContext(agentAid, accUUID, agentClass, tid,
                acc)) {
            return null;
        }

        Role newRole = null;

        try {
            final LogicTuple rolePolicyTemplate = new LogicTuple(
                    "policy_role_request", new Value(policy.getPolicyName()),
                    new Var("Result"));
            ITucsonOperation op = acc.inp(tid, rolePolicyTemplate, (Long) null);
            if (op.isResultSuccess()) {
                LogicTuple res = op.getLogicTupleResult();
                final String roleName = res.getArg(1).toString();

                final LogicTuple template = new LogicTuple(
                        "role_activation_request", new Value(
                                agentAid.toString()), new Value(
                                accUUID.toString()), new Value(roleName),
                        new Var("Result"));
                op = acc.inp(tid, template, (Long) null);
                if (op.isResultSuccess()) {
                    res = op.getLogicTupleResult();
                    if (res != null
                            && res.getArg(3).getName().equalsIgnoreCase("ok")) {
                        newRole = new TucsonRole(roleName);
                        newRole.setPolicy(policy);
                        newRole.setAgentClass(agentClass);
                    }
                }
            }
        } catch (final InvalidVarNameException e) {
            e.printStackTrace();
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
        }

        return newRole;
    }

    public static String encrypt(final String password)
            throws NoSuchAlgorithmException {
        final MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes());

        final byte byteData[] = md.digest();

        final StringBuffer sb = new StringBuffer();
        for (final byte element : byteData) {
            sb.append(Integer.toString((element & 0xff) + 0x100, 16).substring(
                    1));
        }
        return sb.toString();
    }

    public static List<Policy> getPolicyList(final String agentClass,
            final TupleCentreId tid, final EnhancedACC acc) {
        final List<Policy> policies = new ArrayList<Policy>();
        try {
            final LogicTuple policyListTuple = new LogicTuple(
                    "policy_list_request", new Value(agentClass), new Var(
                            "Result"));

            final ITucsonOperation op = acc.inp(tid, policyListTuple,
                    (Long) null);

            if (op.isResultSuccess()) {
                final LogicTuple res = op.getLogicTupleResult();
                if (res.getArg(1).getName().equalsIgnoreCase("ok")) {
                    final TupleArgument[] policiesList = res.getArg(1)
                            .getArg(0).toArray();
                    for (final TupleArgument term : policiesList) {
                        final TupleArgument[] permissionsTuples = term
                                .getArg(1).toArray();

                        final Policy newPolicy = TucsonPolicy.createPolicy(term
                                .getArg(0).toString(), permissionsTuples);
                        policies.add(newPolicy);
                    }
                }

            }
        } catch (final InvalidVarNameException e) {
            e.printStackTrace();
        } catch (final TucsonOperationNotPossibleException e) {
            e.printStackTrace();
        } catch (final UnreachableNodeException e) {
            e.printStackTrace();
        } catch (final OperationTimeOutException e) {
            e.printStackTrace();
        }

        return policies;
    }
}
