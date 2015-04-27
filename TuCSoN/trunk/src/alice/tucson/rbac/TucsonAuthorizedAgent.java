package alice.tucson.rbac;

import java.security.NoSuchAlgorithmException;
import alice.logictuple.LogicTuple;
import alice.logictuple.Value;
import alice.tucson.service.tools.TucsonACCTool;

/**
 * 
 * 
 * @author Emanuele Buccelli
 * @author (contributor) Stefano Mariani (mailto: s.mariani@unibo.it)
 *
 */
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

    private final String agentClass;
    private String password;

    private String username;

    public TucsonAuthorizedAgent(final String ac,
            final String uname, final String psw) {
        this.agentClass = ac;
        this.username = uname;
        this.password = psw;
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
    public void setPassword(final String psw) {
        this.password = psw;
    }

    @Override
    public void setUsername(final String uname) {
        this.username = uname;
    }
}
