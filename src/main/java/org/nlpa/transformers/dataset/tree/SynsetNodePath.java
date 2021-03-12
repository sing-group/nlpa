/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nlpa.transformers.dataset.tree;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *
 * @author María Novo
 */
public class SynsetNodePath {
    private final SynsetNode root;
    private final LinkedList<SynsetNode> path;
    
    public SynsetNodePath(SynsetNode node) {
        this.path = new LinkedList<>();
        this.path.add(node);
        this.root = node;
    }
    
    public SynsetNodePath(SynsetNode root, Collection<SynsetNode> path) {
        if (path.contains(root)) {
            throw new IllegalArgumentException("root is not in path");
        }
        
        this.path = new LinkedList<>(path);
        this.root = root;
    }
    
    public SynsetNodePath(Collection<SynsetNode> path) {
        this.path = new LinkedList<>(path);
        this.root = path.stream()
            .sorted((s1, s2) -> Integer.compare(s1.getDegree(), s2.getDegree()))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }
    
    private SynsetNodePath(Collection<SynsetNode> path, SynsetNode prepend, SynsetNode append) {
        this.path = new LinkedList<>(path);
        
        if (prepend != null) {
            this.path.addFirst(prepend);
        }
        if (append != null) {
            this.path.addLast(append);
        }
        
        this.root = this.path.stream()
            .sorted((s1, s2) -> Integer.compare(s1.getDegree(), s2.getDegree()))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }

    public SynsetNode getRoot() {
        return root;
    }

    public List<SynsetNode> getPath() {
        return Collections.unmodifiableList(path);
    }

    public List<SynsetNode> getPathWithoutRoot() {
        return this.path.stream()
                .filter(node -> node != this.root)
                .collect(Collectors.toList());
    }
    
    public SynsetNode getFirst() {
        return this.path.getFirst();
    }
    
    public SynsetNode getLast() {
        return this.path.getLast();
    }
    
    public int getLength() {
        return this.path.size();
    }

    public boolean contains(SynsetNode node) {
        return this.path.contains(node);
    }
    
    public boolean allMatch(Predicate<SynsetNode> test) {
        return this.path.stream().allMatch(test);
    }
    
    public SynsetNodePath prepend(SynsetNode node) {
        return new SynsetNodePath(this.path, node, null);
    }
    
    public SynsetNodePath append(SynsetNode node) {
        return new SynsetNodePath(this.path, null, node);
    }
}
