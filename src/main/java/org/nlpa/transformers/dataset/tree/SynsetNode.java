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
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public SynsetNode(String synsetId) {
        this(synsetId, null);
    }

    public SynsetNode(Collection<String> synsetIds) {
        this(synsetIds, null);
    }

    public SynsetNode(String synsetId, SynsetNode parent) {
        this(synsetId, parent, emptySet());
    }

    public SynsetNode(Collection<String> synsetIds, SynsetNode parent) {
        this(synsetIds, parent, emptySet());
    }

    public SynsetNode(String synsetId, SynsetNode parent, Collection<SynsetNode> children) {
        this(singleton(synsetId), parent, children);
    }

    public SynsetNode(Collection<String> synsetIds, SynsetNode parent, Collection<SynsetNode> children) {
        this.synsets = new LinkedHashSet<>(synsetIds);

        this.parent = parent;
        this.children = new LinkedHashSet<>(children);

        this.instances = new HashSet<>();
        this.frequenciesCache = new HashMap<>();
    }

    public SynsetNode getParent() {
        return this.parent;
    }

    public boolean hasParent() {
        return this.parent != null;
    }

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

    public boolean isParentOf(SynsetNode child) {
        return this.children.contains(child);
    }

    public SynsetNode getChild() {
        if (this.children.isEmpty()) {
            return null;
        }
        return this.children.iterator().next();
    }

    public List<SynsetNode> getChildren() {
        return unmodifiableList(new ArrayList<>(this.children));
    }

    public boolean addChild(SynsetNode child) {
        return child.setParent(this);
    }

    private void internalAddChild(SynsetNode child) {
        this.children.add(child);
    }

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

    public SynsetNode getRoot() {
        return this.hasParent() ? this.getParent().getRoot() : this;
    }

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

    public List<SynsetNode> getDescendants() {
        return this.getDescendants(Integer.MAX_VALUE);
    }
    
    public List<SynsetNode> getDescendants(int maxDistance) {
        if (maxDistance <= 0 || this.children.isEmpty()) {
            return emptyList();
        } else {
            List<SynsetNode> descendants = new ArrayList<>(this.children);

            this.children.forEach(child -> descendants.addAll(child.getDescendants(maxDistance - 1)));
            
            return unmodifiableList(descendants);
        }
    }
    
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

    public List<SynsetNode> getDescendantAndSelf() {
        List<SynsetNode> descendants = new ArrayList<>(this.getDescendants());
        descendants.add(this);

        return unmodifiableList(descendants);

    }

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

     public SynsetNode getFirstAncestorWithInstances() {
        
        SynsetNode ancestor = this.getParent();
        if (!ancestor.hasInstances()) {
            while (ancestor.hasParent() && !ancestor.getParent().hasInstances()) {
                ancestor = ancestor.getParent();
            }
        }
        return ancestor.getParent();
    }

    public boolean isAncestorOf(SynsetNode descendant) {
        if (this.isParentOf(descendant)) {
            return true;
        } else {
            for (SynsetNode child : this.children) {
                if (child.isAncestorOf(descendant)) {
                    return true;
                }
            }

            return false;
        }
    }

    public boolean removeDescendant(SynsetNode descendant) {
        if (this.removeChild(descendant)) {
            return true;
        } else {
            for (SynsetNode child : this.children) {
                if (child.removeDescendant(descendant)) {
                    return true;
                }
            }

            return false;
        }
    }

    public String getReferenceSynset() {
        if (this.synsets.isEmpty()) {
            return "";
        }
        return this.synsets.iterator().next();
    }

    public List<String> getSynsets() {
        return unmodifiableList(new ArrayList<>(this.synsets));
    }

    public boolean hasSynsets() {
        return !this.synsets.isEmpty();
    }

    public boolean hasSynsetsDeep() {
        if (this.children.isEmpty()) {
            return this.hasSynsets();
        } else if (this.hasSynsets()) {
            return true;
        } else {
            for (SynsetNode child : this.children) {
                if (child.hasSynsetsDeep()) {
                    return true;
                }
            }

            return false;
        }
    }

    public boolean hasSynset(String synsetId) {
        return this.synsets.contains(synsetId);
    }

    public boolean hasDescendantSynset(String synsetId) {
        if (this.hasSynset(synsetId)) {
            return true;
        } else {
            for (SynsetNode child : children) {
                if (child.hasDescendantSynset(synsetId)) {
                    return true;
                }
            }
            return false;
        }
    }

    public int countSynsets() {
        return this.synsets.size();
    }

    public Set<SynsetInstance> getInstances() {
        return this.hasInstances() ? unmodifiableSet(this.instances) : this.instances;
    }

    public boolean hasInstances() {
        return !this.instances.isEmpty();
    }

    public boolean hasInstancesDeep() {
        if (this.hasInstances()) {
            return true;
        } else {
            for (SynsetNode child : this.children) {
                if (child.hasInstancesDeep()) {
                    return true;
                }
            }

            return false;
        }
    }

    public boolean addInstance(SynsetInstance instance) {
        if (this.instances.add(instance)) {
            this.clearFrequencyCache();
            return true;
        } else {
            return false;
        }
    }

    public double getTargetFrequency(Serializable target) {
        if (!this.frequenciesCache.containsKey(target)) {
            this.frequenciesCache.put(target, calculateTargetFrequency(target, this.instances));
        }

        return frequenciesCache.get(target);
    }

    public double getCombinedTargetFrequency(Serializable target, SynsetNode node) {
        Set<SynsetInstance> combinedInstances = new HashSet<>(this.instances);
        combinedInstances.addAll(node.instances);

        return calculateTargetFrequency(target, combinedInstances);
    }

    public double getCombinedTargetFrequency(Serializable target, Collection<SynsetNode> node) {
        Set<SynsetInstance> combinedInstances = new HashSet<>(this.instances);
        node.stream()
            .map(SynsetNode::getInstances)
            .forEach(combinedInstances::addAll);

        return calculateTargetFrequency(target, combinedInstances);
    }

    private void clearFrequencyCache() {
        this.frequenciesCache.clear();
    }

    private static double calculateTargetFrequency(Serializable target, Set<SynsetInstance> instances) {
        double targetCount = instances.stream()
                .filter(sample -> sample.getTarget().equals(target))
                .count();

        return targetCount / instances.size();
    }

    public void generalize(SynsetNode... nodes) {
        this.generalize(Arrays.asList(nodes));
    }

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
