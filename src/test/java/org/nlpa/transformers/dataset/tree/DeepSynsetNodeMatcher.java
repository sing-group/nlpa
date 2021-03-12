/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nlpa.transformers.dataset.tree;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeMatcher;
import org.nlpa.transformers.dataset.tree.SynsetNode;

/**
 *
 * @author María Novo
 */
public class DeepSynsetNodeMatcher extends TypeSafeMatcher<SynsetNode> {
	private final SynsetNode node;
	private final SynsetNodeMatcher nodeMatcher;
	private final DeepSynsetNodeMatcher[] childrenMatchers;
	
	private Consumer<Description> expectedDescription;
	private BiConsumer<SynsetNode, Description> actualDescription;
	
	public DeepSynsetNodeMatcher(SynsetNode node) {
		this.node = node;
		this.nodeMatcher = SynsetNodeMatcher.equalTo(node);
		
		this.childrenMatchers = node.getChildren().stream()
			.map(DeepSynsetNodeMatcher::new)
		.toArray(DeepSynsetNodeMatcher[]::new);
		
		this.expectedDescription = description -> {};
		this.actualDescription = (item, description) -> {
			this.describeMismatchSafely(item, description);
		};
	}

	@Override
	public void describeTo(Description description) {
		this.expectedDescription.accept(description);
	}
	
	@Override
	protected void describeMismatchSafely(SynsetNode item, Description mismatchDescription) {
		this.actualDescription.accept(item, mismatchDescription);
	}

	@Override
	protected boolean matchesSafely(SynsetNode item) {
		boolean matches = true;
		
		this.expectedDescription = d -> {};
		this.actualDescription = (node, description) -> {
			super.describeMismatchSafely(node, description);
		};
		
		if (!this.nodeMatcher.matches(item)) {
			matches = false;
			this.expectedDescription = descripton -> {
				descripton.appendValue(node);
			};
			this.actualDescription = (node, description) -> {
				super.describeMismatchSafely(item, description);
			};
		} else {
			List<SynsetNode> children = item.getChildren();
			final int childrenSize = children.size();
			
			if (childrenSize != this.childrenMatchers.length) {
				matches = false;
				
				this.expectedDescription = description -> {
					description.appendValue(node).appendText(" with ").appendValue(this.childrenMatchers.length);
					
					if (this.childrenMatchers.length == 1) {
						description.appendText(" child.");
					} else {
						description.appendText(" children.");
					}
				};
				this.actualDescription = (node, description) -> {
					description.appendText("was ").appendValue(item).appendText(" with ").appendValue(childrenSize);
					
					if (childrenSize == 1) {
						description.appendText(" child.");
					} else {
						description.appendText(" children.");
					}
				};
			} else {
				for (int i = 0; i < this.childrenMatchers.length; i++) {
					DeepSynsetNodeMatcher matcher = this.childrenMatchers[i];
					final SynsetNode child = children.get(i);
					
					if (!matcher.matches(child)) {
						matches = false;
						
						this.expectedDescription = matcher.expectedDescription;
						this.actualDescription = matcher.actualDescription;
						break;
					}
				}
			}
		}
		
		return matches;
	}
	
	@Factory
	public static DeepSynsetNodeMatcher deeplyEqualTo(SynsetNode node) {
		return new DeepSynsetNodeMatcher(node);
	}
}
