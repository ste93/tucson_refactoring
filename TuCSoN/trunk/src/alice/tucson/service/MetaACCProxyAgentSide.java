package alice.tucson.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import alice.logictuple.LogicTuple;
import alice.logictuple.TupleArgument;
import alice.logictuple.Value;
import alice.logictuple.Var;
import alice.logictuple.exceptions.InvalidTupleArgumentException;
import alice.logictuple.exceptions.InvalidVarNameException;
import alice.respect.api.exceptions.OperationNotAllowedException;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.MetaACC;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.rbac.AuthorizedAgent;
import alice.tucson.rbac.Permission;
import alice.tucson.rbac.Policy;
import alice.tucson.rbac.RBAC;
import alice.tucson.rbac.Role;
import alice.tucson.rbac.TucsonAuthorizedAgent;
import alice.tucson.service.tools.TucsonACCTool;
import alice.tucson.utilities.Utils;
import alice.tuplecentre.api.TupleCentreId;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

public class MetaACCProxyAgentSide extends ACCProxyAgentSide implements MetaACC {

    private boolean admin_authorized;
    private final TupleCentreId tid;

    public MetaACCProxyAgentSide(final Object id)
            throws TucsonInvalidAgentIdException,
            TucsonInvalidTupleCentreIdException {
        this(id, "localhost", 20504);
    }

    public MetaACCProxyAgentSide(final Object aid, final String node,
            final int port) throws TucsonInvalidAgentIdException,
            TucsonInvalidTupleCentreIdException {
        this(aid, node, port, "", "");
    }

    public MetaACCProxyAgentSide(final Object aid, final String node,
            final int port, final String username, final String password)
            throws TucsonInvalidAgentIdException,
            TucsonInvalidTupleCentreIdException {
        super(aid, node, port);
        this.setUsername(username);
        this.setPassword(password);
        this.admin_authorized = false;
        this.tid = this.getTid(node, port);// new TucsonTupleCentreId(tcOrg,
        // "'"+node+"'", ""+port);
        try {
            this.activateAdminRole();
        } catch (final NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add(final AuthorizedAgent agent)
            throws NoSuchAlgorithmException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        this.addAuthorizedAgent(agent, (Long) null);
    }

    @Override
    public void add(final Policy policy) throws InvalidTupleArgumentException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        this.addPolicy(policy, (Long) null);
    }

    @Override
    public void add(final RBAC rbac)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException,
            InvalidTupleArgumentException, OperationNotAllowedException,
            NoSuchAlgorithmException {
        this.add(rbac, null, this.node, this.port);
    }

    @Override
    public void add(final RBAC rbac, final Long l, final String n, final int p)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException,
            InvalidTupleArgumentException, OperationNotAllowedException,
            NoSuchAlgorithmException {
        if (rbac == null) {
            return;
        }

        if (!this.admin_authorized) {
            throw new OperationNotAllowedException();
        }

        this.installRBACSupport();

        final LogicTuple template = new LogicTuple("organisation_name",
                new Value(rbac.getOrgName()));
        ITucsonOperation op = this.out(this.tid, template, l);
        if (op.isResultSuccess()) {
            final LogicTuple res = op.getLogicTupleResult();
            this.log("addRBAC: " + res);
        } else {
            this.log("problem with addRBAC: " + rbac.getOrgName());
        }

        final LogicTuple baseClassTuple = new LogicTuple(
                "set_base_agent_class", new Value(rbac.getBaseAgentClass()));
        op = this.inp(this.tid, baseClassTuple, l);

        for (final Role role : rbac.getRoles()) {
            this.addRole(role, l);
        }

        for (final Policy policy : rbac.getPolicies()) {
            this.addPolicy(policy, l);
        }

        for (final AuthorizedAgent authAgent : rbac.getAuthorizedAgents()) {
            this.addAuthorizedAgent(authAgent, l);
        }

        final LogicTuple loginTuple = new LogicTuple("set_login", new Value(
                rbac.getLoginRequired() ? "yes" : "no"));
        /*
         * if(rbac.getLoginRequired()){ loginTuple = new LogicTuple("set_login",
         * new Value("yes")); } else { loginTuple = new LogicTuple("set_login",
         * new Value("no")); }
         */

        op = this.inp(this.tid, loginTuple, l);

        LogicTuple inspectorsTuple = new LogicTuple("authorize_inspectors",
                new Value(rbac.getAuthorizedInspectors() ? "yes" : "no"));
        if (rbac.getAuthorizedInspectors()) {
            inspectorsTuple = new LogicTuple("authorize_inspectors", new Value(
                    "yes"));
        } else {
            inspectorsTuple = new LogicTuple("authorize_inspectors", new Value(
                    "no"));
        }

        op = this.inp(this.tid, inspectorsTuple, l);
        if (op.isResultSuccess()) {
            final LogicTuple res = op.getLogicTupleResult();
            this.log("Inspectors activation: " + res.getArg(0));
        } else {
            this.log("Inspectors activation: ERROR");
        }
    }

    @Override
    public void add(final Role role)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        this.addRole(role, (Long) null);
    }

    // TODO: Non funziona l'inserimento in lista
    @Override
    public void addPermissionToPolicy(final Permission permission,
            final String policyName)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {

        try {
            final LogicTuple setPermissionTuple = new LogicTuple(
                    "add_permission", new Value(policyName), new Value(
                            permission.getPermissionName()), new Var("Result"));
            final ITucsonOperation op = this.inp(this.tid, setPermissionTuple,
                    (Long) null);
            if (op.isResultSuccess()) {
                final LogicTuple res = op.getLogicTupleResult();
                if (res.getArg(2).getName().equalsIgnoreCase("ok")) {
                    this.log("[" + this.aid.toString() + "]: permission "
                            + permission.getPermissionName() + " added to "
                            + policyName + ".");
                } else if (res.getArg(2).getName().equalsIgnoreCase("failed")) {
                    final String failReason = res.getArg(2).getArg(0)
                            .toString();
                    this.log("[" + this.aid.toString() + "]: permission "
                            + permission.getPermissionName()
                            + " not added because " + failReason + ".");
                }
            }
        } catch (final InvalidVarNameException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(final String agentName) {
        // TODO
    }

    @Override
    public void removePolicy(final String policyName)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        final LogicTuple policyDestructionTuple = new LogicTuple(
                "remove_policy", new Value(policyName));
        this.inp(this.tid, policyDestructionTuple, (Long) null);
    }

    @Override
    public void removeRBAC() throws OperationNotAllowedException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {
        this.removeRBAC(null, this.node, this.port);
    }

    @Override
    public void removeRBAC(final Long l, final String n, final int p)
            throws OperationNotAllowedException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException {

        if (!this.admin_authorized) {
            throw new OperationNotAllowedException();
        }
        this.inp(this.tid, new LogicTuple("disinstall_rbac"), (Long) null);
    }

    @Override
    public void removeRole(final String roleName)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        final LogicTuple roleDestructionTuple = new LogicTuple("remove_role",
                new Value(Utils.decapitalize(roleName)));
        this.inp(this.tid, roleDestructionTuple, (Long) null);
    }

    @Override
    public void setBaseAgentClass(final String newBaseAgentClass)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        final LogicTuple newClassTuple = new LogicTuple("set_base_agent_class",
                new Value(newBaseAgentClass));

        final ITucsonOperation op = this.inp(this.tid, newClassTuple,
                (Long) null);
        if (op.isResultSuccess()) {
            this.log("[MetaACC]: changed base agent class to "
                    + newBaseAgentClass);
        } else {
            this.log("[MetaACC]: failure in changing base agent class");
        }
    }

    @Override
    public void setRoleAgentClass(final String roleName, final String agentClass)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        LogicTuple setRoleClassTuple;
        try {
            setRoleClassTuple = new LogicTuple("set_role_class", new Value(
                    roleName), new Value(agentClass), new Var("Result"));
            final ITucsonOperation op = this.inp(this.tid, setRoleClassTuple,
                    (Long) null);
            if (op.isResultSuccess()) {
                final LogicTuple res = op.getLogicTupleResult();
                if (res.getArg(2).getName().equalsIgnoreCase("ok")) {
                    this.log("[" + this.aid.toString()
                            + "]: agent class of role " + roleName
                            + " changed to " + agentClass + ".");
                } else if (res.getArg(2).getName().equalsIgnoreCase("failed")) {
                    final String failReason = res.getArg(2).getArg(0)
                            .toString();
                    this.log("[" + this.aid.toString()
                            + "]: agent class of role " + roleName
                            + " changed because " + failReason + ".");
                }
            }
        } catch (final InvalidVarNameException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setRolePolicy(final String roleName, final String policyName)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {
        LogicTuple setPolicyTuple;
        try {
            setPolicyTuple = new LogicTuple("set_role_policy", new Value(
                    roleName), new Value(policyName), new Var("Result"));
            final ITucsonOperation op = this.inp(this.tid, setPolicyTuple,
                    (Long) null);
            if (op.isResultSuccess()) {
                final LogicTuple res = op.getLogicTupleResult();
                if (res.getArg(2).getName().equalsIgnoreCase("ok")) {
                    this.log("[" + this.aid.toString() + "]: policy of role "
                            + roleName + " changed to " + policyName + ".");
                } else if (res.getArg(2).getName().equalsIgnoreCase("failed")) {
                    final String failReason = res.getArg(2).getArg(0)
                            .toString();
                    this.log("[" + this.aid.toString() + "]: policy of role "
                            + roleName + " not changed because " + failReason
                            + ".");
                }
            }
        } catch (final InvalidVarNameException e) {
            e.printStackTrace();
        }
    }

    private void activateAdminRole() throws NoSuchAlgorithmException {

        try {
            final LogicTuple template = new LogicTuple("admin_login_request",
                    new Value(this.getUsername() + ":"
                            + TucsonACCTool.encrypt(this.getPassword())),
                    new Var("Result"));
            final ITucsonOperation op = this.inp(this.tid, template,
                    (Long) null);
            if (op.isResultSuccess()) {
                final LogicTuple res = op.getLogicTupleResult();
                if (res != null
                        && res.getArg(1).getName().equalsIgnoreCase("ok")) {
                    this.admin_authorized = true;
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
    }

    private void addAuthorizedAgent(final AuthorizedAgent agent, final Long l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException,
            NoSuchAlgorithmException {
        final LogicTuple authTuple = TucsonAuthorizedAgent.getLogicTuple(agent);
        final ITucsonOperation op = this.out(this.tid, authTuple, l);
        if (op.isResultSuccess()) {
            final LogicTuple res = op.getLogicTupleResult();
            this.log("Authorized agent added: " + res);
        } else {
            this.log("Problem with addAuthorizedAgent.");
        }
    }

    private void addPolicy(final Policy policy, final Long l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException,
            InvalidTupleArgumentException {
        String permissions = "[";
        for (final Permission perm : policy.getPermissions()) {
            permissions += perm.getPermissionName() + ",";
        }

        permissions = permissions.substring(0, permissions.length() - 1);
        permissions += "]";
        final LogicTuple policyTuple = new LogicTuple("policy", new Value(
                policy.getPolicyName()), TupleArgument.parse(permissions));
        final ITucsonOperation op = this.out(this.tid, policyTuple, l);

        if (op.isResultSuccess()) {
            final LogicTuple res = op.getLogicTupleResult();
            this.log("addPolicy: " + res);
        } else {
            this.log("problem with addPolicy: " + policy.getPolicyName());
        }

    }

    private void addRole(final Role role, final Long l)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {

        final LogicTuple roleTuple = new LogicTuple("role", new Value(
                role.getRoleName()), new Value(role.getDescription()),
                new Value(role.getAgentClass()));

        final ITucsonOperation op = this.out(this.tid, roleTuple, l);

        if (op.isResultSuccess()) {
            final LogicTuple res = op.getLogicTupleResult();
            this.log("addRole: " + res);
        } else {
            this.log("problem with addRole: " + role.getRoleName());
        }

        this.addRolePolicy(role.getPolicy(), role.getRoleName(), l);
    }

    private void addRolePolicy(final Policy policy, final String roleName,
            final Long l) throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException {

        final LogicTuple policyTuple = new LogicTuple("role_policy", new Value(
                roleName), new Value(policy.getPolicyName()));
        final ITucsonOperation op = this.out(this.tid, policyTuple, l);
        if (op.isResultSuccess()) {
            final LogicTuple res = op.getLogicTupleResult();
            this.log("addRolePolicy: " + res);
        } else {
            this.log("problem with addRolePolicy: " + policy.getPolicyName());
        }
    }

    private TupleCentreId getTid(final String n, final int p)
            throws TucsonInvalidTupleCentreIdException {
        String tmpNode;
        int tmpPort;
        if (n != null && !n.equals("")) {
            tmpNode = n;
            tmpPort = p;
        } else {
            tmpNode = this.node;
            tmpPort = this.port;
        }

        if (tmpNode.equalsIgnoreCase("localhost")
                || tmpNode.equalsIgnoreCase("127.0.0.1")
                || tmpNode.equalsIgnoreCase("'127.0.0.1'")) {
            InetAddress localhost;
            try {
                localhost = InetAddress.getLocalHost();
                final String local_node_address = localhost.getHostAddress();
                tmpNode = local_node_address;
            } catch (final UnknownHostException e) {
                return new TucsonTupleCentreId(ACCProxyAgentSide.tcOrg, "'"
                        + tmpNode + "'", "" + tmpPort);
            }
        }
        return new TucsonTupleCentreId(ACCProxyAgentSide.tcOrg, "'" + tmpNode
                + "'", "" + tmpPort);
    }

    // Passiamo da una fase in cui RBAC non � supportato ad una fase nella quale
    // va tutto come progettato
    // admin si aggiunge agli utenti autorizzati
    private void installRBACSupport()
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException,
            OperationNotAllowedException {

        if (!this.admin_authorized) {
            throw new OperationNotAllowedException();
        }

        this.inp(this.tid, new LogicTuple("install_rbac"), (Long) null);
        // TODO: Serve admin autorizzato? Se non fosse autorizzato non potrebbe
        // farlo!
        final LogicTuple adminAuthorized = new LogicTuple("authorized_agent",
                new Value(this.aid.toString()));
        this.out(this.tid, adminAuthorized, (Long) null);
    }
}
