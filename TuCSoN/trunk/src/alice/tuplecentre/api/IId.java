package alice.tuplecentre.api;

/**
 * Represents an operation emitter's interface: it could be both an agent of a
 * tuple centre
 * 
 * @author teo
 * 
 */
public interface IId {

    boolean isAgent();

    boolean isEnv();

    boolean isTC();

}
