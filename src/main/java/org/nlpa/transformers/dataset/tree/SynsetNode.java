/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nlpa.transformers.dataset.tree;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author María Novo
 */
public class SynsetNode {

    private Set<String> synsets;
    private Set<SynsetInstance> instances;
    private SynsetNode parent;
    private Set<SynsetNode> children;

    private Map<Serializable, Double> frequenciesCache;

    /**
     * Constructor only with synset id
     *
     * @param synsetId Synset id
     */
    public SynsetNode(String synsetId) {
        this(synsetId, null);
    }

    /**
     * Constructor with a collection of synset ids
     *
     * @param synsetIds Collection of Synset ids
     */
    public SynsetNode(Collection<String> synsetIds) {
        this(synsetIds, null);
    }

    /**
     * Constructor with a synset id and the parent node
     *
     * @param synsetId Synset id
     * @param parent SynsetNode parent
     */
    public SynsetNode(String synsetId, SynsetNode parent) {
        this(synsetId, parent, emptySet());
    }

    /**
     * Constructor with a collection of synset ids and parent
     *
     * @param synsetIds Collection of synset ids
     * @param parent SynsetNode parent
     */
    public SynsetNode(Collection<String> synsetIds, SynsetNode parent) {
        this(synsetIds, parent, emptySet());
    }

    /**
     * Constructor with a synset id,the parent node and his children
     *
     * @param synsetId Synset id
     * @param parent SynsetNode parent
     * @param children Collection of children
     */
    public SynsetNode(String synsetId, SynsetNode parent, Collection<SynsetNode> children) {
        this(singleton(synsetId), parent, children);
    }

    /**
     * Constructor with a collection of synset ids,the parent node and his
     * children
     *
     * @param synsetIds Collection of synset ids
     * @param parent SynsetNode parent
     * @param children Collection of children
     */
    public SynsetNode(Collection<String> synsetIds, SynsetNode parent, Collection<SynsetNode> children) {
        this.synsets = new LinkedHashSet<>(synsetIds);

        this.parent = parent;
        this.children = new LinkedHashSet<>(children);

        this.instances = new HashSet<>();
        this.frequenciesCache = new HashMap<>();
    }

    /**
     * Gets the parent node
     *
     * @return The parent node
     */
    public SynsetNode getParent() {
        return this.parent;
    }

    /**
     * Query if the node has parent
     *
     * @return True if the node has parent, false otherwise
     */
    public boolean hasParent() {
        return this.parent != null;
    }

    /**
     * Estabilish the parent of node
     *
     * @param parent the parent node
     * @return True if parent has estabilished
     */
    public boolean setParent(SynsetNode parent) {
        if (this.parent == parent) {
            return false;
        }

        if (this.parent != null) {
            this.parent.internalRemoveChild(this);
        }

        this.parent = parent;
        if (this.parent != null) {
            this.parent.internalAddChild(this);
        }

        return true;
    }

    /**
     * Checks if a node is parent of another
     *
     * @param child Node to check parent
     * @return True if a node is parent of child, false otherwise
     */
    public boolean isParentOf(SynsetNode child) {
        return this.children.contains(child);
    }

    /**
     * Gets the first child of a node
     *
     * @return The first child of a node
     */
    public SynsetNode getChild() {
        if (this.children.isEmpty()) {
            return null;
        }
        return this.children.iterator().next();
    }

    /**
     * Gets the list of children for a node
     *
     * @return The list of children for a node
     */
    public List<SynsetNode> getChildren() {
        return unmodifiableList(new ArrayList<>(this.children));
    }

    /**
     * Add a new child to a node
     *
     * @param child Node to add
     * @return True if child is added, false otherwise
     */
    public boolean addChild(SynsetNode child) {
        return child.setParent(this);
    }

    private void internalAddChild(SynsetNode child) {
        this.children.add(child);
    }

    /**
     * Remove a child from a node
     *
     * @param child Child node to remove
     * @return True if the child has removed successfully, false otherwise
     */
    public boolean removeChild(SynsetNode child) {
        if (this.isParentOf(child)) {
            return child.setParent(null);
        } else {
            return false;
        }
    }

    private void internalRemoveChild(SynsetNode child) {
        this.children.remove(child);
    }

    /**
     * Gets the top node that doesn't have parent
     *
     * @return The top node that doesn't have parent
     */
    public SynsetNode getRoot() {
        return this.hasParent() ? this.getParent().getRoot() : this;
    }

    /**
     * Gets the leafs of a node, this is the nodes without children
     *
     * @return The leafs of a node
     */
    public List<SynsetNode> getLeafs() {
        if (this.children.isEmpty()) {
            return singletonList(this);
        } else {
            List<SynsetNode> leafs = this.children.stream()
                    .map(SynsetNode::getLeafs)
                    .flatMap(List::stream)
                    .collect(toList());

            return unmodifiableList(leafs);
        }
    }

    /**
     * Gets a list with all descendants nodes
     *
     * @return A list with all descendants nodes
     */
    public List<SynsetNode> getDescendants() {
        return this.getDescendants(Integer.MAX_VALUE);
    }

    /**
     * Gets a list with the descendants nodes until a max distance
     *
     * @param maxDistance Max distance to get descendants
     * @return A list with the descendants nodes until a max distance
     */
    public List<SynsetNode> getDescendants(int maxDistance) {
        if (maxDistance <= 0 || this.children.isEmpty()) {
            return emptyList();
        } else {
            List<SynsetNode> descendants = new ArrayList<>(this.children);
            this.children.forEach(child -> descendants.addAll(child.getDescendants(maxDistance - 1)));
            return unmodifiableList(descendants);
        }
    }

    /**
     * Gets a list with the descendants nodes until a max distance and excluding
     * a collection of synset nodes
     *
     * @param maxDistance Max distance to get descendants
     * @param exclusions Collection of synsets nodes to exclude
     * @return A list with the descendants nodes until a max distance and
     * excluding a collection of synset nodes
     */
    public List<SynsetNodePath> getPathToDescendants(int maxDistance, Collection<SynsetNode> exclusions) {
        List<SynsetNodePath> paths = new ArrayList<>();
        paths.add(new SynsetNodePath(this));

        if (maxDistance > 0 && !this.children.isEmpty()) {
            this.children.stream()
                    .filter(node -> !exclusions.contains(node))
                    .map(child -> child.getPathToDescendants(maxDistance - 1, exclusions))
                    .flatMap(List::stream)
                    .map(path -> path.prepend(this))
                    .forEach(paths::add);
        }
        return paths;
    }

    /**
     * Gets a list with all descendants nodes including the self node
     *
     * @return A list with all descendants nodes including the self node
     */
    public List<SynsetNode> getDescendantAndSelf() {
        List<SynsetNode> descendants = new ArrayList<>(this.getDescendants());
        descendants.add(this);

        return unmodifiableList(descendants);
    }

    /**
     * Gets a list with all descendants nodes including the self node from a
     * synset id
     *
     * @param synsetId Synset id
     * @return A list with all descendants nodes including the self node from a
     * synset id
     */
    public SynsetNode getSelfOrDescendantBySynset(String synsetId) {
        if (this.synsets.contains(synsetId)) {
            return this;
        } else {
            for (SynsetNode child : children) {
                SynsetNode synsetNode = child.getSelfOrDescendantBySynset(synsetId);
                if (synsetNode != null) {
                    return synsetNode;
                }
            }
            return null;
        }
    }

    /**
     * Gets the degree if first ancestor with instances
     *
     * @return The degree if first ancestor with instances
     */
    public int getFirstAncestorWithInstancesDegree() {
        int degree = 1;

        SynsetNode ancestor = this.getParent();
        if (!ancestor.hasInstances()) {
            degree++;
            while (ancestor.hasParent() && !ancestor.getParent().hasInstances()) {
                degree++;
                ancestor = ancestor.getParent();
            }
            if (!ancestor.hasParent()) {
                return Integer.MAX_VALUE;
            }
        }
        return degree;
    }

    /**
     * Gets the first ancestor with instances
     *
     * @return the first ancestor with instances
     */
    public SynsetNode getFirstAncestorWithInstances() {
        SynsetNode ancestor = this.getParent();
        if (!ancestor.hasInstances()) {
            while (ancestor.hasParent() && !ancestor.getParent().hasInstances()) {
                ancestor = ancestor.getParent();
            }
        }
        return ancestor.getParent();
    }

    /**
     * Checks if a node is ancestor of another
     *
     * @param descendant Descendant node to check
     * @return True if the node is ancestor of the descendant node
     */
    public boolean isAncestorOf(SynsetNode descendant) {
        if (this.isParentOf(descendant)) {
            return true;
        } else {
            return this.children.stream().anyMatch((child) -> (child.isAncestorOf(descendant)));
        }
    }

    /**
     * Remove the descendant of a node
     *
     * @param descendant The node to remove
     * @return True if the descendant was successfully removed, false otherwise
     */
    public boolean removeDescendant(SynsetNode descendant) {
        if (this.removeChild(descendant)) {
            return true;
        } else {
            return this.children.stream().anyMatch((child) -> (child.removeDescendant(descendant)));
        }
    }

    /**
     * Gets the first synset in synsets list
     *
     * @return The first synset in synsets list
     */
    public String getReferenceSynset() {
        if (this.synsets.isEmpty()) {
            return "";
        }
        return this.synsets.iterator().next();
    }

    /**
     * Gets a list of synsets from a node
     *
     * @return A list of synsets from a node
     */
    public List<String> getSynsets() {
        return unmodifiableList(new ArrayList<>(this.synsets));
    }

    /**
     * Indicates if a node has synsets or not
     *
     * @return True if a node has synsets, false otherwise
     */
    public boolean hasSynsets() {
        return !this.synsets.isEmpty();
    }

    /**
     * Checks if node or its children has synsets
     *
     * @return True if node or its children has synsets
     */
    public boolean hasSynsetsDeep() {
        if (this.children.isEmpty()) {
            return this.hasSynsets();
        } else if (this.hasSynsets()) {
            return true;
        } else {
            return this.children.stream().anyMatch((child) -> (child.hasSynsetsDeep()));
        }
    }

    /**
     * Checks if the node contains the given id
     *
     * @param synsetId
     * @return True if the node contains the given id
     */
    public boolean hasSynset(String synsetId) {
        return this.synsets.contains(synsetId);
    }

    /**
     * Checks if the node or any of its descendants contains the given synset id
     *
     * @param synsetId
     * @return True if the node or any of its descendants contains the given
     * synset id
     */
    public boolean hasDescendantSynset(String synsetId) {
        if (this.hasSynset(synsetId)) {
            return true;
        } else {
            return children.stream().anyMatch((child) -> (child.hasDescendantSynset(synsetId)));
        }
    }

    /**
     * Gets the number of synsets from a node
     *
     * @return The number of synsets from a node
     */
    public int countSynsets() {
        return this.synsets.size();
    }

    /**
     * Gets the instances of a node
     *
     * @return The instances of a node
     */
    public Set<SynsetInstance> getInstances() {
        return this.hasInstances() ? unmodifiableSet(this.instances) : this.instances;
    }

    /**
     * Checks if a node has instances
     *
     * @return True if a node has instances, false otherwise
     */
    public boolean hasInstances() {
        return !this.instances.isEmpty();
    }

    /**
     * Checks if a node or its children has instances
     *
     * @return True if a node or its children has instances, false otherwise
     */
    public boolean hasInstancesDeep() {
        if (this.hasInstances()) {
            return true;
        } else {
            return this.children.stream().anyMatch((child) -> (child.hasInstancesDeep()));
        }
    }

    /**
     * Add an instance to a node
     *
     * @param instance The instance to add
     * @return True if the instance was added successfully, false otherwise
     */
    public boolean addInstance(SynsetInstance instance) {
        if (this.instances.add(instance)) {
            this.clearFrequencyCache();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets the frequency to the given target
     *
     * @param target The target that represents the class of instance(ham/spam,
     * 0/1, etc)
     * @return The frequency to the given target
     */
    public double getTargetFrequency(Serializable target) {
        if (!this.frequenciesCache.containsKey(target)) {
            this.frequenciesCache.put(target, calculateTargetFrequency(target, this.instances));
        }

        return frequenciesCache.get(target);
    }

    /**
     * Combines instances of both nodes and then gets the frequency to the given
     * target
     *
     * @param target The target that represents the class of instance(ham/spam,
     * 0/1, etc)
     * @param node The node to combine frequencies
     * @return The combined frequency
     */
    public double getCombinedTargetFrequency(Serializable target, SynsetNode node) {
        Set<SynsetInstance> combinedInstances = new HashSet<>(this.instances);
        combinedInstances.addAll(node.instances);

        return calculateTargetFrequency(target, combinedInstances);
    }

    /**
     * Combines instances of all nodes and then gets the frequency to the given
     * target
     *
     * @param target The target that represents the class of instance(ham/spam,
     * 0/1, etc)
     * @param node The collection of nodes to combine frequencies
     * @return The combined frequency
     */
    public double getCombinedTargetFrequency(Serializable target, Collection<SynsetNode> node) {
        Set<SynsetInstance> combinedInstances = new HashSet<>(this.instances);
        node.stream()
                .map(SynsetNode::getInstances)
                .forEach(combinedInstances::addAll);

        return calculateTargetFrequency(target, combinedInstances);
    }

    /**
     * Clear frequency caché
     */
    private void clearFrequencyCache() {
        this.frequenciesCache.clear();
    }

    /**
     * Calculate the frequency to the given target from instances
     *
     * @param target The target that represents the class of instance(ham/spam,
     * 0/1, etc)
     * @param instances Instances to calculate frequency
     * @return The frequency to the given target from instances
     */
    private static double calculateTargetFrequency(Serializable target, Set<SynsetInstance> instances) {
        double targetCount = instances.stream()
                .filter(sample -> sample.getTarget().equals(target))
                .count();

        return targetCount / instances.size();
    }

    /**
     *
     * @param nodes
     */
    public void generalize(SynsetNode... nodes) {
        this.generalize(Arrays.asList(nodes));
    }

    /**
     * 
     * @param nodes 
     */
    public void generalize(Collection<SynsetNode> nodes) {
//        for (SynsetNode node : nodes) {
//            if (node.getSynsets().isEmpty() || !this.isAncestorOf(node)) {
//                throw new IllegalArgumentException("Only descendants with synset id/s can be collapsed.");
//            }
//        }

        for (SynsetNode node : nodes) {
            this.synsets.addAll(node.synsets);
            node.synsets.clear();

            this.instances.addAll(node.instances);
        }

        this.clearFrequencyCache();
    }

    public SynsetNodePath getPathTo(SynsetNode targetNode) {
        SynsetNodePath thisPathToRoot = this.getPathToRoot();
        SynsetNodePath targetPathToRoot = targetNode.getPathToRoot();

        for (SynsetNode node : thisPathToRoot.getPath()) {
            if (targetPathToRoot.contains(node)) {
                List<SynsetNode> thisNodes = thisPathToRoot.getPath();
                List<SynsetNode> targetNodes = targetPathToRoot.getPath();

                List<SynsetNode> thisPath = thisNodes.subList(0, thisNodes.indexOf(node) + 1);
                List<SynsetNode> targetPath = targetNodes.subList(0, targetNodes.indexOf(node));
                Collections.reverse(targetPath);

                thisPath.addAll(targetPath);

                return new SynsetNodePath(thisPath);
            }
        }

        throw new IllegalArgumentException("Nodes do not share a common ancestor");
    }

//    public List<SynsetNodePath> getPathToSiblingsAndCousins(int degree) {
//        return null;
//    }
//    
//    private List<SynsetNodePath> getPathToSiblingsAndCousins(SynsetNodePath currentPath, int degree) {
//        if (degree <= 0) {
//            return Arrays.asList(currentPath);
//        } else {
//            return null;
//        }
//    }
    private SynsetNodePath getPathToRoot() {
        List<SynsetNode> pathToRoot = new ArrayList<>();

        SynsetNode node = this;
        while (node != null) {
            pathToRoot.add(node);
            node = node.getParent();
        }

        return new SynsetNodePath(pathToRoot);
    }

    public int getDegree() {
        return this.parent == null ? 0 : this.parent.getDegree() + 1;
    }

    public int getMaxDegree() {
        return this.children.stream()
                .mapToInt(SynsetNode::getMaxDegree)
                .max().orElse(this.getDegree());
    }

    public boolean prune() {
        if (this.canBePrunedDeep()) {
            return true;
        } else {
            Iterator<SynsetNode> itChildren = this.children.iterator();

            while (itChildren.hasNext()) {
                SynsetNode child = itChildren.next();

                if (child.canBePrunedDeep()) {
                    itChildren.remove();
                } else {
                    child.prune();
                }
            }

            return false;
        }
    }

    private boolean canBePruned() {
        return this.hasSynsets() ? !this.hasInstances() : true;
    }

    private boolean canBePrunedDeep() {
        return this.canBePruned() && this.children.stream().allMatch(SynsetNode::canBePrunedDeep);
    }

    @Override
    public String toString() {
        String synsets = this.synsets.stream().collect(joining(","));
        String instances = this.instances.stream().map(instance -> instance.getName() + ":" + instance.getTarget()).collect(joining(","));

        return instances.isEmpty() ? synsets : synsets + " ### " + this.getTargetFrequency(1d);
    }

    public String toStringDeep() {
        return this.toStringDeep("");
    }

    private String toStringDeep(String prefix) {
        StringBuilder builder = new StringBuilder(prefix);
        builder.append(this.toString());
        builder.append("\n");

        SynsetNode[] children = this.children.toArray(new SynsetNode[this.children.size()]);
        for (int i = 0; i < children.length; i++) {
            builder.append(children[i].toStringDeep(buildPrefix(prefix, i == children.length - 1)));
        }

        return builder.toString();
    }

    private static String buildPrefix(String parentPrefix, boolean isLast) {
        String prefix = "";

        int parentLength = parentPrefix.length();
        if (parentLength > 0) {
            prefix = parentPrefix.substring(0, parentLength - 2);

            String suffix = parentPrefix.substring(parentLength - 2, parentLength);
            if (suffix.equals("└─")) {
                prefix += "  ";
            } else {
                prefix += "│ ";
            }
        }

        prefix += isLast ? "└─" : "├─";
        return prefix;
    }
}
