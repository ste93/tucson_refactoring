package alice.tucson.service;

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
import alice.tucson.api.NegotiationACC;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.AgentNotAllowedException;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.rbac.Policy;
import alice.tucson.rbac.Role;
import alice.tucson.rbac.TucsonPolicy;
import alice.tucson.rbac.TucsonRole;
import alice.tucson.service.tools.TucsonACCTool;
import alice.tuplecentre.api.TupleCentreId;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

/**
 * 
 * 
 * @author Emanuele Buccelli
 * @author (contributor) Stefano Mariani (mailto: s.mariani@unibo.it)
 *
 */
public class NegotiationACCProxyAgentSide implements NegotiationACC {

    /**
     * 
     */
    protected static final String TC_AGENT = "negotAgent";
    /**
     * 
     */
    protected static final String TC_ORG = "'$ORG'";
    private static final int DEF_PORT = 20504;

    private static Policy chooseRole(final List<Policy> policies,
            final List<String> permissionsId) {
        Policy bestPolicy = null;
        int bestPermissionsArity = 10000;

        for (final Policy pol : policies) {
            if (pol.hasPermissions(permissionsId)) {
                if (pol.getPermissions().size() < bestPermissionsArity) {
                    bestPermissionsArity = pol.getPermissions().size();
                    bestPolicy = pol;
                }
            }
        }

        return bestPolicy;
    }

    private final Object agentAid;
    private String agentClass;
    private final EnhancedACC internalACC;

    private final TupleCentreId tid;

    /**
     * 
     */
    protected String node;
    /**
     * 
     */
    protected int port;

    public NegotiationACCProxyAgentSide(final Object aid)
            throws TucsonInvalidAgentIdException,
            TucsonInvalidTupleCentreIdException {
        this(aid, "localhost", DEF_PORT);
    }

    public NegotiationACCProxyAgentSide(final Object aid, final String n,
            final int p) throws TucsonInvalidAgentIdException,
            TucsonInvalidTupleCentreIdException {
        this.internalACC = new ACCProxyAgentSide(
                NegotiationACCProxyAgentSide.TC_AGENT, n, p);
        this.node = n;
        this.port = p;
        this.agentAid = aid;
        this.tid = new TucsonTupleCentreId(NegotiationACCProxyAgentSide.TC_ORG,
                "'" + n + "'", "" + p);
        this.setBaseAgentClass();
    }

    /*
     * Attiva un ACC fuori dalla struttura RBAC nel caso questa non sia stata
     * installata nel nodo.
     */
    @Override
    public EnhancedACC activateDefaultRole()
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException,
            TucsonInvalidAgentIdException {
        if (!this.isRBACInstalled(this.tid)) {
            return new ACCProxyAgentSide(this.agentAid, this.node, this.port);
        }
        return null;
    }

    @Override
    public EnhancedACC activateRole(final String roleName)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException,
            TucsonInvalidAgentIdException, AgentNotAllowedException {
        return this.activateRole(roleName, null);
    }

    @Override
    public EnhancedACC activateRole(final String roleName, final Long l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException,
            TucsonInvalidAgentIdException, AgentNotAllowedException {

        if (!this.isRBACInstalled(this.tid)) {
            return new ACCProxyAgentSide(this.agentAid, this.node, this.port);
        }

        final UUID agentUUID = UUID.randomUUID();

        final Role newRole = TucsonACCTool.activateRole(
                this.agentAid.toString(), agentUUID, this.getAgentClass(),
                roleName, this.tid, this.internalACC);

        if (newRole == null) {
            return null;
        }

        return new RoleACCProxyAgentSide(this.agentAid.toString(), this.node,
                this.port, newRole, agentUUID);
    }

    @Override
    public EnhancedACC activateRoleWithPermission(
            final List<String> permissionsId)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException,
            TucsonInvalidAgentIdException, AgentNotAllowedException {
        return this.activateRoleWithPermission(permissionsId, null);
    }

    /*
     * Il metodo cerca di attivare un ruolo con i privilegi minimi necessari. 1
     * - Ottiene la lista delle policy 2 - Viene scelta la policy migliore. 3 -
     * Viene ricercato il ruolo che detiene la policy, ed attivato.
     */
    @Override
    public synchronized EnhancedACC activateRoleWithPermission(
            final List<String> permissionsId, final Long l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException,
            TucsonInvalidAgentIdException, AgentNotAllowedException {

        if (!this.isRBACInstalled(this.tid)) {
            return new ACCProxyAgentSide(this.agentAid, this.node, this.port);
        }

        final List<Policy> policies = TucsonACCTool.getPolicyList(
                this.agentClass, this.tid, this.internalACC);

        final Policy policy = NegotiationACCProxyAgentSide.chooseRole(policies,
                permissionsId);
        if (policy == null) {
            throw new AgentNotAllowedException();
        }

        final UUID agentUUID = UUID.randomUUID();
        final Role newRole = TucsonACCTool.activateRoleWithPolicy(
                this.agentAid.toString(), agentUUID, this.getAgentClass(),
                policy, this.tid, this.internalACC);
        if (newRole == null) {
            throw new AgentNotAllowedException();
        }

        return new RoleACCProxyAgentSide(this.agentAid, this.node, this.port,
                newRole, agentUUID);
    }

    // TODO: Lista dei ruoli!!!
    @Override
    public List<Role> listActivableRoles() throws InvalidVarNameException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        final ITucsonOperation op = this.internalACC.inp(this.tid,
                new LogicTuple("role_list_request", new Value(this.agentClass),
                        new Var("Result")), (Long) null);
        final List<Role> roles = new ArrayList<Role>();
        if (op.isResultSuccess()) {
            final LogicTuple res = op.getLogicTupleResult();
            if (res.getArg(1).getName().equalsIgnoreCase("ok")) {
                final TupleArgument[] rolesList = res.getArg(1).getArg(0)
                        .toArray();
                for (final TupleArgument term : rolesList) {
                    final String roleName = term.getArg(0).toString();
                    final String roleDescription = term.getArg(1).toString();
                    final String policyName = term.getArg(2).toString();
                    final TupleArgument[] permissionsTuples = term.getArg(3)
                            .toArray();

                    final Policy newPolicy = TucsonPolicy.createPolicy(
                            policyName, permissionsTuples);
                    final Role newRole = new TucsonRole(roleName,
                            this.agentClass);
                    newRole.setDescription(roleDescription);
                    newRole.setPolicy(newPolicy);
                    roles.add(newRole);
                }
            }
        }

        return roles;
    }

    @Override
    public boolean login(final String username, final String password)
            throws NoSuchAlgorithmException, InvalidVarNameException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        final LogicTuple loginTuple = new LogicTuple("login_request",
                new Value(username + ":" + TucsonACCTool.encrypt(password)),
                new Var("Result"));

        final ITucsonOperation op = this.internalACC.inp(this.tid, loginTuple,
                (Long) null);
        if (op.isResultSuccess()) {
            final TupleArgument response = op.getLogicTupleResult().getArg(1);
            this.setAgentClass(response.getArg(0).toString());
            if (response.getName().equals("ok")) {
                return true;
            }
            return false;
        }
        return false;
    }

    private String getAgentClass() {
        return this.agentClass;
    }

    private boolean isRBACInstalled(final TupleCentreId t)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        LogicTuple rbacInstalled = null;
        try {
            rbacInstalled = new LogicTuple("rbac_installed", new Var("Result"));
        } catch (final InvalidVarNameException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        final ITucsonOperation op = this.internalACC.rd(t, rbacInstalled,
                (Long) null);
        if (op.isResultSuccess()) {
            final LogicTuple res = op.getLogicTupleResult();
            if (res.getArg(0).toString().equals("yes")) {
                return true;
            }
        }
        return false;
    }

    private void setAgentClass(final String agClass) {
        this.agentClass = agClass;
    }

    private void setBaseAgentClass() {
        try {
            final LogicTuple baseClassTuple = new LogicTuple(
                    "get_base_agent_class", new Var("Response"));
            final ITucsonOperation op = this.internalACC.inp(this.tid,
                    baseClassTuple, (Long) null);
            if (op.isResultSuccess()) {
                final String baseClass = op.getLogicTupleResult().getArg(0)
                        .toString();
                this.setAgentClass(baseClass);
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

    }

    protected void log(final String msg) {
        System.out.println("[ACCProxyAgentSide]: " + msg);
    }
}
