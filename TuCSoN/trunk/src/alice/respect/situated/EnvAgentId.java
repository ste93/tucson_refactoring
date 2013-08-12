package alice.respect.situated;

import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;

/**
 * Environmental Agent Identifier
 * 
 * @author Steven Maraldi
 * 
 */
public class EnvAgentId extends TucsonAgentId {

    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;

    /**
     * 
     * @param aid
     *            the String representation of the environmental agent
     *            identifier
     * @throws TucsonInvalidAgentIdException
     *             if the given String does not represent a valid TuCSoN agent
     *             identifier
     */
    public EnvAgentId(final String aid) throws TucsonInvalidAgentIdException {
        super(aid);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean isAgent() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isEnv() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isTC() {
        // TODO Auto-generated method stub
        return false;
    }

}
