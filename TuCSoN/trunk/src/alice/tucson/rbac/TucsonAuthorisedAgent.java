package alice.tucson.rbac;

import alice.logictuple.LogicTuple;
import alice.logictuple.Value;
import alice.tucson.service.tools.TucsonACCTool;

/**
 * The class implementing a TuCSoN agent authorised by the RBAC structure
 * configured.
 *
 * @author Emanuele Buccelli
 * @author (contributor) Stefano Mariani (mailto: s.mariani@unibo.it)
 *
 */
public class TucsonAuthorisedAgent implements AuthorisedAgent {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static LogicTuple asLogicTuple(final AuthorisedAgent agent) {
        return new LogicTuple("authorised_agent", new Value(agent.getUsername()
                + ":" + TucsonACCTool.encrypt(agent.getPassword())), new Value(
                        agent.getAgentClass()));
    }

    private final String agentClass;
    private final String password;
    private final String username;

    /**
     * Builds the authorised agent representation.
     *
     * @param ac
     *            the agent class
     * @param uname
     *            the agent user name
     * @param psw
     *            the agent password
     */
    public TucsonAuthorisedAgent(final String ac, final String uname,
            final String psw) {
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

}
