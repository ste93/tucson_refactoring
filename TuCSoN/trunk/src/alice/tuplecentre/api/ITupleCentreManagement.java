/*
 * Created on Feb 19, 2004 To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package alice.tuplecentre.api;

import alice.tuplecentre.api.exceptions.OperationNotPossibleException;
import alice.tuplecentre.core.AbstractBehaviourSpecification;

/**
 * @author aricci
 */
public interface ITupleCentreManagement {

    /**
     * 
     * @return the ReSpecT specification retrieved
     */
    AbstractBehaviourSpecification getReactionSpec();

    /**
     * 
     * @throws OperationNotPossibleException
     *             if the operation cannot be performed
     */
    void goCommand() throws OperationNotPossibleException;

    /**
     * 
     * @throws OperationNotPossibleException
     *             if the operation cannot be performed
     */
    void nextStepCommand() throws OperationNotPossibleException;

    /**
     * 
     * @param activate
     *            wether the 'management mode' should be activated or not
     */
    void setManagementMode(boolean activate);

    /**
     * 
     * @param spec
     *            the ReSpecT specification to set
     * @return wether the ReSpecT specification has been succesfully set
     */
    boolean setReactionSpec(AbstractBehaviourSpecification spec);

    /**
     * 
     * @throws OperationNotPossibleException
     *             if the operation cannot be performed
     */
    void stopCommand() throws OperationNotPossibleException;

}
