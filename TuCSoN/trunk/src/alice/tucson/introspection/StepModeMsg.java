package alice.tucson.introspection;

import alice.tucson.api.TucsonAgentId;

public class StepModeMsg extends NodeMsg {
	private static final long serialVersionUID = -6748034977696183466L;

	/**
     * @param id
     *            the agent id of the sender
     */
	public StepModeMsg(final TucsonAgentId id) {
		super(id, "stepMode");
	}

}
