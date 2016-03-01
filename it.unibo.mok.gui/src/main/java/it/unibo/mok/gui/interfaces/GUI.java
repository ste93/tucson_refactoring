package it.unibo.mok.gui.interfaces;

public interface GUI {

    /**
     * Add an executable
     * 
     * @param executable
     */
    void addExecutable(Executable executable);

    /**
     * Add a link between two nodes
     * 
     * @param firstNode
     * @param secondNode
     * @return true if added, false otherwise
     */
    boolean addLink(String firstNode, String secondNode);

    /**
     * Add molecule to a node
     * 
     * @param molecule
     * @param node
     * @return true if added, false otherwise
     */
    boolean addMolecule(Molecule molecule, String node);

    /**
     * Add a node
     * 
     * @param node
     * @return true if added, false otherwise
     */
    boolean addNode(Node node);

    /**
     * Revert to initial state
     */
    void clear();

    /**
     * Remove a link between two nodes
     * 
     * @param firstNode
     * @param secondNode
     * @return true if removed, false otherwise
     */
    boolean removeLink(String firstNode, String secondNode);

    /**
     * Remove a molecule from a node
     * 
     * @param moleculeId
     * @param node
     * @return true if removed, false otherwise
     */
    boolean removeMolecule(String moleculeId, String node);

    /**
     * Remove a node
     * 
     * @param nodeIdentifier
     * @return true if removed, false otherwise
     */
    boolean removeNode(String nodeIdentifier);

    /**
     * Set molecule concentration
     * 
     * @param moleculeId
     * @param node
     * @param concentration
     * @return true if set, false otherwise
     */
    boolean setMoleculeConcentration(String moleculeId, String node,
            float concentration);

    /**
     * Show GUI frame
     */
    void show();

    /**
     * Callback called by canvas when a transfer animation ended
     * 
     * @param sourceMolecule
     * @param destination
     * @param link
     * @param addToDest
     */
    void transferAnimationCompleted(Molecule sourceMolecule, Node destination,
            Link link, boolean addToDest);

    /**
     * Transfer a molecule from a source to a destination node. Source and
     * destination must be linked.
     * 
     * This call is blocking-by-link: if the link is already used for another
     * transfer, the call will block until that transfer ends.
     * 
     * @param moleculeId
     * @param sourceNode
     * @param destinationNode
     * @param concentrationToMove
     * @return true if transfer accepted, false otherwise
     */
    boolean transferMolecule(String moleculeId, String sourceNode,
            String destinationNode, float concentrationToMove, 
            boolean addToDest);

}
