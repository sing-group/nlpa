/*
 * Source made by Ponzeto and Navigli. It was downloaded from:
 * https://github.com/iucl/l2-writing-assistant/tree/master/babelnet-api-2.0/
 */
package org.nlpa.util.babelnet;

import java.util.Collection;

//import it.uniroma1.lcl.knowledge.KnowledgeBase;
//import it.uniroma1.lcl.knowledge.graph.KnowledgeGraphPath;

/**
 * The common interface for different strategies used to filter a
 * {@link KnowledgeGraphPath}.
 * 
 * @author ponzetto
 * 
 */
public interface KnowledgeGraphPathFilter
{
	/**
	 * Filters a collection of paths, possibly using a specific KB, e.g. to
	 * extract the concepts lexicalizations.
	 * 
	 * @param paths The Paths
	 * @param kb The knowledgeBase
	 */
	void filter(Collection<KnowledgeGraphPath> paths, KnowledgeBase kb);
}