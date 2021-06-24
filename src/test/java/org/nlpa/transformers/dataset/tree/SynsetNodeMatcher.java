/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nlpa.transformers.dataset.tree;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeMatcher;
import org.nlpa.transformers.dataset.tree.SynsetNode;

/**
 *
 * @author María Novo
 */
public class SynsetNodeMatcher extends TypeSafeMatcher<SynsetNode> {

    private final SynsetNode node;

    public SynsetNodeMatcher(SynsetNode node) {
        this.node = node;
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue(this.node);
    }

    @Override
    protected boolean matchesSafely(SynsetNode item) {

        return this.node.getSynsets().equals(item.getSynsets())
                && this.node.getInstances().equals(item.getInstances());
    }

    @Factory
    public static SynsetNodeMatcher equalTo(SynsetNode node) {
        return new SynsetNodeMatcher(node);
    }
}
