package alice.tucson.rbac;

import java.io.Serializable;

/**
 * 
 * 
 * @author Emanuele Buccelli
 * @author (contributor) Stefano Mariani (mailto: s.mariani@unibo.it)
 *
 */
public interface AuthorizedAgent extends Serializable {

    String getAgentClass();

    String getPassword();

    String getUsername();

    void setPassword(String password);

    void setUsername(String username);
}
