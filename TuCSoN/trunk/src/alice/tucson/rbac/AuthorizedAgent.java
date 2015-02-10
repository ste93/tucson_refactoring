package alice.tucson.rbac;

import java.io.Serializable;

public interface AuthorizedAgent extends Serializable {

	public String getAgentName();
}
