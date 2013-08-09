package alice.respect.transducer;

import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;

/**
 * A transducer identifier.
 * 
 * Transducer can be thought as agents when interact directly with tuple centres
 * but it also represents a portion of the environment.
 * 
 * @author Steven Maraldi
 * 
 */
public class TransducerId extends EnvAgentId {

    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;

    public TransducerId(final String id) throws TucsonInvalidAgentIdException {
        super(id);
    }
}
