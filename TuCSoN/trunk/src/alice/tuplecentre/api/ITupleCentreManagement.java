/*
 * Created on Feb 19, 2004 To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package alice.tuplecentre.api;

import alice.tuplecentre.api.exceptions.OperationNotPossibleException;
import alice.tuplecentre.core.BehaviourSpecification;

/**
 * @author aricci
 */
public interface ITupleCentreManagement {

    BehaviourSpecification getReactionSpec();

    void goCommand() throws OperationNotPossibleException;

    void nextStepCommand() throws OperationNotPossibleException;

    void setManagementMode(boolean activate);

    boolean setReactionSpec(BehaviourSpecification spec);

    void stopCommand() throws OperationNotPossibleException;

}
