package it.unibo.mok.gui.impl;

import it.unibo.mok.gui.impl.model.Link2D;
import it.unibo.mok.gui.interfaces.Link;
import it.unibo.mok.gui.interfaces.Node;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Database {

    /*
     * **************************************************
     * Fields *************************************************
     */

    private final List<Link> links;
    private final Set<Node> nodes;

    /*
     * **************************************************
     * Constructor *************************************************
     */

    public Database() {
        this.nodes = new HashSet<>();
        this.links = new ArrayList<>();

    }

    /*
     * **************************************************
     * Commands *************************************************
     */

    public Link addLink(final String firstNode, final String secondNode) {
        Node first = null;
        Node second = null;
        for (final Node node : this.nodes) {
            if (node.getId().equals(firstNode)) {
                first = node;
            } else if (node.getId().equals(secondNode)) {
                second = node;
            }
            if (first != null && second != null) {
                break;
            }
        }
        if (first == null || second == null) {
            return null;
        } else {
            for (int i = 0; i < this.links.size(); i++) {
                if (first.equals(this.links.get(i).getFirst())
                        && second.equals(this.links.get(i).getSecond())
                        || first.equals(this.links.get(i).getSecond())
                        && second.equals(this.links.get(i).getFirst())) {
                    return null;
                }
            }
            final Link2D link = new Link2D(first, second);
            this.links.add(link);
            return link;
        }
    }

    public boolean addNode(final Node node) {
        return this.nodes.add(node);
    }

    public void clear() {
        this.nodes.clear();
        this.links.clear();
    }

    public Link getLink(final String firstNode, final String secondNode) {
        for (final Link link : this.getLinks()) {
            if (link.getFirst().getId().equals(firstNode)
                    && link.getSecond().getId().equals(secondNode)
                    || link.getFirst().getId().equals(secondNode)
                    && link.getSecond().getId().equals(firstNode)) {
                return link;
            }
        }
        return null;
    }

    public List<Link> getLinks() {
        return this.links;
    }

    public Node getNode(final String nodeID) {
        for (final Node node : this.nodes) {
            if (node.getId().equals(nodeID)) {
                return node;
            }
        }
        return null;
    }

    public Set<Node> getNodes() {
        return this.nodes;
    }

    public boolean removeLink(final String firstNode, final String secondNode) {
        final Iterator<Link> iter = this.links.iterator();
        while (iter.hasNext()) {
            final Link link = iter.next();
            if (link.getFirst().getId().equals(firstNode)
                    && link.getSecond().getId().equals(secondNode)
                    || link.getFirst().getId().equals(secondNode)
                    && link.getSecond().getId().equals(firstNode)) {
                iter.remove();
                return true;
            }
        }
        return false;
    }

    public boolean removeLinks(final String nodeIdentifier) {
        final Iterator<Link> iter = this.links.iterator();
        while (iter.hasNext()) {
            final Link link = iter.next();
            if (link.getFirst().getId().equals(nodeIdentifier)
                    || link.getSecond().equals(nodeIdentifier)) {
                iter.remove();
            }
        }
        return false;
    }

    public boolean removeNode(final String nodeIdentifier) {
        final Iterator<Node> iter = this.nodes.iterator();
        while (iter.hasNext()) {
            final Node node = iter.next();
            if (node.getId().equals(nodeIdentifier)) {
                iter.remove();
                return true;
            }
        }
        return false;
    }

}
