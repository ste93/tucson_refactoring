package alice.tucson.rbac;

import java.security.NoSuchAlgorithmException;
import alice.logictuple.LogicTuple;
import alice.logictuple.Value;
import alice.tucson.service.tools.TucsonACCTool;

public class TucsonAuthorizedAgent implements AuthorizedAgent {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static LogicTuple getLogicTuple(final AuthorizedAgent agent)
            throws NoSuchAlgorithmException {
        return new LogicTuple("authorized_agent", new Value(agent.getUsername()
                + ":" + TucsonACCTool.encrypt(agent.getPassword())), new Value(
                        agent.getAgentClass()));
    }

    private String agentAid;

    private final String agentClass;
    private String password;

    private String username;

    public TucsonAuthorizedAgent(final String agentClass,
            final String username, final String password) {
        this.agentClass = agentClass;
        this.username = username;
        this.password = password;
    }

    @Override
    public String getAgentClass() {
        return this.agentClass;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public void setPassword(final String pass) {
        this.password = pass;
    }

    @Override
    public void setUsername(final String username) {
        this.username = username;
    }
}
