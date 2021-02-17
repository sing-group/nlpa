/*
 * Source made by Ponzeto and Navigli. It was downloaded from:
 * https://github.com/iucl/l2-writing-assistant/tree/master/babelnet-api-2.0/
 */
package org.nlpa.util.babelnet;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import com.babelscape.util.UniversalPOS;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.mit.jwi.item.IPointer;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.IWord;
import it.uniroma1.lcl.babelnet.BabelNet;
import it.uniroma1.lcl.babelnet.BabelNetQuery;
import it.uniroma1.lcl.babelnet.BabelSense;
import it.uniroma1.lcl.babelnet.BabelSynset;
import it.uniroma1.lcl.babelnet.BabelSynsetID;
import it.uniroma1.lcl.babelnet.BabelSynsetRelation;
import it.uniroma1.lcl.babelnet.data.BabelPointer;
import it.uniroma1.lcl.jlt.Configuration;
import it.uniroma1.lcl.jlt.ling.Word;
import it.uniroma1.lcl.jlt.util.Language;
import it.uniroma1.lcl.jlt.util.ScoredItem;
import it.uniroma1.lcl.jlt.wordnet.WordNet;
import it.uniroma1.lcl.jlt.wordnet.WordNetOffsetIterator;
import it.uniroma1.lcl.jlt.wordnet.data.WordNetGlossPointer;
import it.uniroma1.lcl.jlt.wordnet.data.WordNetGlosses;
import it.uniroma1.lcl.jlt.wordnet.data.WordNetWeights;
import it.uniroma1.lcl.jlt.wordnetplusplus.WordNetPlusPlus;
import it.uniroma1.lcl.jlt.wordnetplusplus.WordNetPlusPlusPointer;

/**
 * The enumeration of the available knowledge bases.
 * 
 * @author flati, ponzetto
 * 
 */
public enum KnowledgeBase
{
	WORDNET
	{
		@Override
		public List<String> getConcepts(Word word)
		{
			Language language = word.getLanguage();
			if (language != null && language != Language.EN)
				throw new IllegalArgumentException("Unsupported language: " + language);
			
			final WordNet wn = WordNet.getInstance();
			final List<String> offsets = new ArrayList<String>();
			for (ISynset synset : wn.getSynsets(word.getWord(), word.getPOS()))
				offsets.add(WordNet.synsetToString(synset));
			return offsets;
		}

		@Override
		public Multimap<Language, Word> getConceptWordsByLanguage(String concept, Language language)
		{
			if (language != null && language != Language.EN)
				throw new IllegalArgumentException("Unsupported language: " + language);
			
			final WordNet wn = WordNet.getInstance();
			final Multimap<Language, Word> words = //new HashMultimap<Language, Word>();
                HashMultimap.create();
			final ISynset synset = wn.getSynsetFromOffset(concept);
			if (synset != null)
			{
				for (IWord sense : synset.getWords())
					words.put(Language.EN, new Word(sense.getLemma(), sense.getPOS().getTag()));
			}
			else throw new IllegalArgumentException("Unknown WN offset: " + concept);
			return words;
		}

		@Override
		public Set<String> getRelatedConcepts(String concept, Language language)
		{
			if (language != null && language != Language.EN)
				throw new IllegalArgumentException("Unsupported language: " + language);

			// adds the relations from lexical and semantic pointers
			final WordNet wn = WordNet.getInstance();
			final Set<String> related = new HashSet<String>();
			final ISynset synset = wn.getSynsetFromOffset(concept);
			if (synset != null)
			{
				final Set<ISynset> relatedSynsets = wn.getRelatedSynsets(synset, true);
				for (ISynset relatedSynset : relatedSynsets)
					related.add(WordNet.synsetToString(relatedSynset));
			}
			else throw new IllegalArgumentException("Unknown WN offset: " + concept);
			return related;
		}

		@Override
		public Multimap<IPointer, ScoredItem<String>> getRelatedConceptsMap(String concept,
																			 Language language)
		{
			if (language != null && language != Language.EN)
				throw new IllegalArgumentException("Unsupported language: " + language);

			// adds the relations from lexical and semantic pointers
			final WordNet wn = WordNet.getInstance();
			final WordNetWeights wnWeights =
				Configuration.getInstance().getWordNetWeightType().getImplementation();
			final Multimap<IPointer, ScoredItem<String>> related = 
				//new HashMultimap<IPointer, ScoredItem<String>>();
                HashMultimap.create();

			final ISynset synset = wn.getSynsetFromOffset(concept);
			final String offset = WordNet.synsetToString(synset);
			if (synset != null)
			{
				final Multimap<IPointer, ISynset> relatedSynsetsMap = 
					wn.getRelatedSynsetsMap(synset, true);
				for (IPointer pointer : relatedSynsetsMap.keySet())
				{
					for (ISynset relatedSynset : relatedSynsetsMap.get(pointer))
					{
						final String relatedOffset =
							WordNet.synsetToString(relatedSynset);
						double weight = wnWeights.getWeight(offset, relatedOffset);
						related.put(pointer,
									new ScoredItem<String>(WordNet.synsetToString(relatedSynset),
														   weight));
					}
				}
			}
			else throw new IllegalArgumentException("Unknown WN offset: " + concept);
			return related;
		}
		
		@Override
		public String conceptToString(String concept, Language language, boolean verbose)
		{
			if (language != null && language != Language.EN)
				throw new IllegalArgumentException("Unsupported language: " + language);
			
			final WordNet wn = WordNet.getInstance();
			final ISynset synset = wn.getSynsetFromOffset(concept);
			
			if (synset != null)
			{
				if (verbose)
					return wn.synsetToSenseString(synset);
				else
					return wn.synsetToFirstSenseString(synset);
			}
			return "";
		}

		@Override
		public Iterator<String> getConceptIterator()
		{
			return new WordNetOffsetIterator(WordNet.getInstance());
		}

		@Override
		public IPointer getPointer(String pointerSymbol)
		{
			return WordNetPlusPlusPointer.getPointerType(pointerSymbol);
		}
	},
	
	WORDNET_GLOSS
	{
		@Override
		public List<String> getConcepts(Word word)
		{
			return WORDNET.getConcepts(word);
		}

		@Override
		public Multimap<Language, Word> getConceptWordsByLanguage(String concept, Language language)
		{
			return WORDNET.getConceptWordsByLanguage(concept, language);
		}
		
		@Override
		public Set<String> getRelatedConcepts(String concept, Language language)
		{
			final ISynset synset = WordNet.getInstance().getSynsetFromOffset(concept);
			final Set<String> related = WORDNET.getRelatedConcepts(concept, language);
			
			// add WordNet gloss relations
			WordNetGlosses glosses = WordNetGlosses.getInstance();
			// a. monosemous words
			related.addAll(
				glosses.getMonosemousGlossWordsOffsets(synset));
			// b. disambiguated words
			related.addAll(
				glosses.getDisambiguatedGlossWordsOffsets(synset));
			
			return related;
		}

		@Override
		public Multimap<IPointer, ScoredItem<String>> getRelatedConceptsMap(String concept,
																			Language language)
		{
			final Multimap<IPointer, ScoredItem<String>> related =
				WORDNET.getRelatedConceptsMap(concept, language);
			final WordNetWeights wnWeights =
				Configuration.getInstance().getWordNetWeightType().getImplementation();
			final ISynset synset = WordNet.getInstance().getSynsetFromOffset(concept);
			final String offset = WordNet.synsetToString(synset);
			
			// add WordNet gloss relations
			WordNetGlosses glosses = WordNetGlosses.getInstance();
			// a. monosemous words
			for (String glossOffset : glosses.getMonosemousGlossWordsOffsets(synset))
			{
				double weight = wnWeights.getWeight(offset, glossOffset);
				related.put(
						WordNetGlossPointer.GLOSS_MONOSEMOUS,
						new ScoredItem<String>(glossOffset, weight)
				);
			}
			// b. disambiguated words
			for (String glossOffset : glosses.getDisambiguatedGlossWordsOffsets(synset))
			{
				double weight = wnWeights.getWeight(offset, glossOffset);
				related.put(
						WordNetGlossPointer.GLOSS_DISAMBIGUATED,
						new ScoredItem<String>(glossOffset, weight)
				);
			}
			
			return related;
		}
		
		@Override
		public String conceptToString(String concept, Language language, boolean verbose)
		{
			return WORDNET.conceptToString(concept, language, verbose);
		}

		@Override
		public Iterator<String> getConceptIterator()
		{
			return WORDNET.getConceptIterator();
		}

		@Override
		public IPointer getPointer(String pointerSymbol)
		{
			return WordNetPlusPlusPointer.getPointerType(pointerSymbol);
		}
	},
	
	WORDNETPLUSPLUS
	{
		@Override
		public List<String> getConcepts(Word word)
		{
			return WORDNET.getConcepts(word);
		}

		@Override
		public Multimap<Language, Word> getConceptWordsByLanguage(String concept, Language language)
		{
			return WORDNET.getConceptWordsByLanguage(concept, language);
		}

		@Override
		public Set<String> getRelatedConcepts(String concept, Language language)
		{
			// 1. WordNet relations
			final WordNetPlusPlus wnpp = WordNetPlusPlus.getInstance();
			final ISynset synset = WordNet.getInstance().getSynsetFromOffset(concept);
			final Set<String> related = WORDNET.getRelatedConcepts(concept, language);
			
			// 2. optionally collects the relations from the WordNet glosses
			Configuration configuration = Configuration.getInstance();
			boolean bGlossMono = configuration.useWordNetMonosemousGlossWords();
			boolean bGlossDis = configuration.useWordNetDisambiguatedGlossWords();
			boolean bGloss = bGlossMono || bGlossDis;
			
			if (bGloss)
			{
				WordNetGlosses glosses = WordNetGlosses.getInstance();
				// a. monosemous words
				if (bGlossMono)
					related.addAll(
						glosses.getMonosemousGlossWordsOffsets(synset));
				// b. disambiguated words
				if (bGlossDis)
					related.addAll(
						glosses.getDisambiguatedGlossWordsOffsets(synset));
			}
			
			// 3. WN++ specific relations (Wikipedia-projected)
			for (ScoredItem<String> wnppRelatedSynset : wnpp.getRelatedSynsets(concept))
				related.add(wnppRelatedSynset.getItem());
			
			return related;
		}

		@Override
		public Multimap<IPointer, ScoredItem<String>> getRelatedConceptsMap(
																String concept,
																Language language)
		{
			final WordNetPlusPlus wnpp = WordNetPlusPlus.getInstance();
			final WordNetWeights wnWeights =
				Configuration.getInstance().getWordNetWeightType().getImplementation();
			
			final ISynset synset = WordNet.getInstance().getSynsetFromOffset(concept);
			final String offset = WordNet.synsetToString(synset);
			
			// 1. WordNet relations
			final Multimap<IPointer, ScoredItem<String>> related =
				WORDNET.getRelatedConceptsMap(concept, language);
			
			// 2. optionally collects the relations from the WordNet glosses
			Configuration configuration = Configuration.getInstance();
			boolean bGlossMono = configuration.useWordNetMonosemousGlossWords();
			boolean bGlossDis = configuration.useWordNetDisambiguatedGlossWords();
			boolean bGloss = bGlossMono || bGlossDis;
			
			if (bGloss)
			{
				WordNetGlosses glosses = WordNetGlosses.getInstance();
				// a. monosemous words
				if (bGlossMono)
				{
					for (String glossOffset : glosses.getMonosemousGlossWordsOffsets(synset))
					{
						double weight = wnWeights.getWeight(offset, glossOffset);
						related.put(
								WordNetGlossPointer.GLOSS_MONOSEMOUS,
								new ScoredItem<String>(glossOffset, weight)
						);
					}
				}
				// b. disambiguated words
				if (bGlossDis)
				{
					for (String glossOffset : glosses.getDisambiguatedGlossWordsOffsets(synset))
					{
						double weight = wnWeights.getWeight(offset, glossOffset);
						related.put(
								WordNetGlossPointer.GLOSS_DISAMBIGUATED,
								new ScoredItem<String>(glossOffset, weight)
						);
					}
				}
			}
			
			// 3. WN++ specific relations (Wikipedia-projected)
			for (ScoredItem<String> wnppRelatedSynset : wnpp.getRelatedSynsets(concept))
				related.put(WordNetPlusPlusPointer.SEMANTICALLY_RELATED, wnppRelatedSynset);
			
			return related;
		}
		
		@Override
		public String conceptToString(String concept, Language language, boolean verbose)
		{
			return WORDNET.conceptToString(concept, language, verbose);
		}

		@Override
		public Iterator<String> getConceptIterator()
		{
			return WORDNET.getConceptIterator();
		}

		@Override
		public IPointer getPointer(String pointerSymbol)
		{
			return WordNetPlusPlusPointer.getPointerType(pointerSymbol);
		}
	},
	
	CUSTOM
	{
		@Override
		public Multimap<Language, Word> getConceptWordsByLanguage(String concept, Language language)
		{
			return CustomKB.getInstance().getConceptWordsByLanguage(concept, language);
		}

		@Override
		public List<String> getConcepts(Word word)
		{
			return CustomKB.getInstance().getConcepts(word);
		}

		@Override
		public Set<String> getRelatedConcepts(String concept, Language language)
		{
			return CustomKB.getInstance().getRelatedConcepts(concept, language);
		}

		@Override
		public Multimap<IPointer, ScoredItem<String>> getRelatedConceptsMap(String concept, Language language)
		{
			return CustomKB.getInstance().getRelatedConceptsMap(concept, language);
		}

		@Override
		public String conceptToString(String concept, Language language,boolean verbose)
		{
			return CustomKB.getInstance().conceptToString(concept, language, verbose);
		}

		@Override
		public IPointer getPointer(String pointerSymbol)
		{
			return CustomKB.getInstance().getPointer(pointerSymbol);
		}

		@Override
		public Iterator<String> getConceptIterator()
		{
			return CustomKB.getInstance().getConceptIterator();
		}		
	},
	
	BABELNET
	{
		@Override
		public List<String> getConcepts(Word word)
		{
			final BabelNet bn = BabelNet.getInstance();
			final List<String> offsets = new ArrayList<String>();
			
			try
			{
				// checks the language: defaults to English
				Language language = word.getLanguage();
				if (language == null) language = Language.EN; 
				
                BabelNetQuery query = new BabelNetQuery.Builder(word.getWord())
                        .POS(UniversalPOS.valueOf(word.getPOS().getTag()))
			            .from(language)
			            .to(Arrays.asList(language))
			            .build();

				List<BabelSynset> synsets = bn.getSynsets(query);
					//bn.getSynsets(word.getWord(), language, it.uniroma1.lcl.babelnet.data.BabelPOS.valueOf(word.getPOS().getTag()), BabelSenseSource.BABELNET);

                //TODO: Check if toString() can be used in the following sentence
				for (BabelSynset synset : synsets) offsets.add(synset.getId().toString());
			} 
			catch (/*IOException*/Exception e)
			{ log.warn("Cannot query BABELNET -- WORD: " + word); }
			
			return offsets;
		}

		@Override
		public Multimap<Language, String> getConceptTermsByLanguage(String concept, Language language)
		{
			final BabelNet bn = BabelNet.getInstance();
			final Multimap<Language, String> terms = //new HashMultimap<Language, String>();
                HashMultimap.create();
			try
			{
				final BabelSynset synset = //bn.getSynsetFromId(concept);
                    (new BabelSynsetID(concept)).toSynset();
				if (synset != null)
				{
					for (BabelSense sense : synset.getSenses())
					{
						if (language != null && !sense.getLanguage().equals(language)) continue;
						terms.put(sense.getLanguage(), /*sense.getLemma()*/sense.getSimpleLemma());
					}
				}
			} 
			catch (/*IOException*/Exception e)
			{ log.warn("Cannot query BABELNET -- LANGUAGE: " + language + " CONCEPT: " + concept); }

			return terms;
		}
		
		@Override
		public Multimap<Language, Word> getConceptWordsByLanguage(String concept, Language language)
		{
			final BabelNet bn = BabelNet.getInstance();
			final Multimap<Language, Word> words = //new HashMultimap<Language, Word>();
                HashMultimap.create();
			try
			{
				final BabelSynset synset = //bn.getSynsetFromId(concept);
                    (new BabelSynsetID(concept)).toSynset();
				if (synset != null)
				{
					for (BabelSense sense : synset.getSenses())
					{
						if (language != null && !sense.getLanguage().equals(language)) continue;
						Word word = new Word(sense.getSimpleLemma(), sense.getPOS().getTag());
						word.setLanguage(sense.getLanguage());
						words.put(sense.getLanguage(), word);
					}
				}
			} 
			catch (/*IOException*/Exception e)
			{ log.warn("Cannot query BABELNET -- LANGUAGE: " + language + " CONCEPT: " + concept); }

			return words;
		}

		@Override
		public Set<String> getRelatedConcepts(String concept, Language language)
		{ 
			return getRelatedConcepts(concept, language, BABELNET_GRAPH_THRESHOLD);
		}
		
		public Set<String> getRelatedConcepts(String concept, Language language,
											  double edgeThreshold)
		{		
			final Set<String> related = new HashSet<String>();
			final BabelNet bn = BabelNet.getInstance();
			
			try
			{
				// collects the actual relations from BabelNet^TM
				//List<BabelNetGraphEdge> edges = bn.getSuccessorEdges(concept);
                //List<BabelSynsetIDRelation> edges = bn.getSuccessorEdges(new BabelSynsetID(concept),Arrays.asList(language));
                List<BabelSynsetRelation> edges = (new BabelSynsetID(concept)).getOutgoingEdges();
                
				for (/*BabelNetGraphEdge*/ BabelSynsetRelation edge : edges)
				{
					if (language == null || edge.getLanguage() == language)
					{
						double edgeWeight = edge.getWeight();
						/*IPointer*/it.uniroma1.lcl.babelnet.data.BabelPointer edgePointer = edge.getPointer();
						if (
								// keep all relations other than babel ones
								edgePointer != it.uniroma1.lcl.babelnet.data.BabelPointer.SEMANTICALLY_RELATED
							||
								// put a threshold on the ones from babelnet
								edgeWeight > edgeThreshold)
							related.add(edge.getTarget());
					}
				}
			}
			catch (/*IOException*/Exception e)
			{ log.warn("Cannot query BABELGRAPH -- LANGUAGE: " + language + " CONCEPT: " + concept); }
			
			return related;	
		}
		
		@Override
		public Multimap<IPointer, ScoredItem<String>> getRelatedConceptsMap(String concept, Language language)
		{
			// by default we use the normalized weights and the edge threshold
			// specified in the babelnet properties file under "babelnet.minEdgeWeight"
			return getRelatedConceptsMap(concept, language, true, BABELNET_GRAPH_THRESHOLD);
		}
		
		/**
		 * Gives a related concept map, optionally with normalized scores and a
		 * minimum input score
		 * 
		 * @param concept
		 * @param language
		 * @param useNormalizedWeights
		 * 
		 * @return
		 */
		public Multimap<IPointer, ScoredItem<String>> getRelatedConceptsMap(String concept, 
																			Language language,
																			boolean useNormalizedWeights,
																			double edgeThreshold)
		{
			final BabelNet bn = BabelNet.getInstance();
			final Multimap<IPointer, ScoredItem<String>> related = 
				//new HashMultimap<IPointer, ScoredItem<String>>();
                HashMultimap.create();
			try
			{
				// collects the actual relations from BabelNet^TM
				List</*BabelNetGraphEdge*/BabelSynsetRelation> edges = //bn.getSuccessorEdges(concept);
                    (new BabelSynsetID(concept)).getOutgoingEdges();
				
				for (/*BabelNetGraphEdge*/BabelSynsetRelation edge : edges)
				{
					if (language == null || edge.getLanguage() == language)
					{
						double edgeWeight = edge.getWeight();
						/*IPointer*/it.uniroma1.lcl.babelnet.data.BabelPointer edgePointer = edge.getPointer();
						if (
								// keep all relations other than babel ones
								edgePointer != it.uniroma1.lcl.babelnet.data.BabelPointer.SEMANTICALLY_RELATED
							||
								// put a threshold on the ones from babelnet
								edgeWeight > edgeThreshold)
						{
							ScoredItem<String> weightedEdge = null;
							if (useNormalizedWeights)
								weightedEdge =
									new ScoredItem<String>(edge.getTarget(),
														   edge.getNormalizedWeight());
							else
								weightedEdge =
									new ScoredItem<String>(edge.getTarget(), edgeWeight);

							related.put(edge.getPointer(), weightedEdge);
						}
					}
				}
			}
			catch (/*IOException*/Exception e)
			{ log.warn("Cannot query BABELGRAPH -- LANGUAGE: " + language + " CONCEPT: " + concept); }
	
			return related;
		}
		
		@Override
		public String conceptToString(String concept, Language language, boolean verbose)
		{
			final BabelNet bn = BabelNet.getInstance();

			try
			{
				final BabelSynset synset = /*bn.getSynsetFromId(concept)*/ (new BabelSynsetID(concept)).toSynset(); 
				if (synset != null)
				{
					if (verbose)
						return synset.toString(language);
					else
						return synset.toString();
				}
				else throw new IllegalArgumentException("Unknown BABEL offset: " + concept);
			}
			catch (/*IOException*/Exception e)
			{
				log.warn("Cannot query BABELNET -- LANGUAGE: " + language + " CONCEPT: " + concept);
				return "";
			}
		}

		@Override
		public Iterator<String> getConceptIterator()
		{ return BabelNet.getInstance().getOffsetIterator(); }

		@Override
		public IPointer getPointer(String pointerSymbol)
		{
			return BabelPointer.getPointerType(pointerSymbol);
		}
	};
	
	private final static Log log = LogFactory.getLog(KnowledgeBase.class);
	
	/**
	 * Gets the {@link Word}s corresponding to a concept, ie its synonyms
	 * 
	 * @param concept The concept
	 * @return the input concept's lexicalizations
	 */
	public Set<Word> getConceptWords(String concept)
	{
		return getConceptWords(concept, null);
	}

	/**
	 * Gets the {@link Word}s corresponding to a concept in a certain language.
	 * 
	 * @param concept The concept
	 * @param language The language
	 * @see KnowledgeBase#getConceptTerms(String, Language) for getting terms
	 *      instead of words
	 * @return the input concept's lexicalizations
	 */
	public Set<Word> getConceptWords(String concept, Language language)
	{
		return new HashSet<Word>(getConceptWordsByLanguage(concept, language).values());
	}

	/**
	 * Gets the string terms corresponding to a concept in a certain language.
	 * Allows instances to distinguish between terms and words. For instance, 
	 * terms and words are not distinguished in WordNet BUT, in BabelNet
	 * words are taken from page titles <b>without parenthesis</b> whereas
	 * terms include parenthesis.
	 * 
	 * @param concept The Concept
	 * @param language the language
	 * @return the input concept's lexicalizations
	 */
	public Set<String> getConceptTerms(String concept, Language language)
	{
		return new HashSet<String>(getConceptTermsByLanguage(concept, language).values());
	}
	
	public Multimap<Language, Word> getConceptWordsByLanguage(String concept)
	{
		return getConceptWordsByLanguage(concept, null);
	}
	
	public abstract Multimap<Language, Word> getConceptWordsByLanguage(String concept, Language language);
	
	public Multimap<Language, String> getConceptTermsByLanguage(String concept, Language language)
	{
		Multimap<Language, String> terms = /*new HashMultimap<Language, String>()*/HashMultimap.create();
		Multimap<Language, Word> words = getConceptWordsByLanguage(concept, language);
		for (Language lang : words.keySet())
			for (Word word : words.get(lang))
				terms.put(lang, word.getWord());
		return terms;
	}
	
	/**
	 * Get the concepts an input word can refer to.
	 * 
	 * @param word The word
	 * @return the possible meanings (i.e., concepts) for a word
	 */
	public abstract List<String> getConcepts(Word word);
	
	/**
	 * Get the concepts related to an input one -- i.e., typically the
	 * adjacent ones within the knowledge base
	 * 
	 * @param concept The concept
	 * @return the concepts related to the input one
	 */
	public Set<String> getRelatedConcepts(String concept)
	{
		return getRelatedConcepts(concept, null);
	}
	
	/**
	 * 
	 * Get the concepts related to an input one -- i.e., typically the
	 * adjacent ones <b>coming from a specific language only</b> within the
	 * knowledge base
	 * 
	 * @param concept The concept
	 * @param language The language
	 * @return the concepts related to the input one
	 */
	public abstract Set<String> getRelatedConcepts(String concept, Language language);
	
	/**
	 * Gets the concepts related to the input one, keyed on the connecting
	 * relation
	 * 
	 * @param concept The concept
	 * @return the concepts related to the input one
	 */
	public Multimap<IPointer, ScoredItem<String>> getRelatedConceptsMap(String concept)
	{
		return getRelatedConceptsMap(concept, null);
	}

	/**
	 * Gets the concepts related to the input one, keyed on the connecting
	 * relation and <b>coming from a specific language only</b>
	 * 
	 * @param concept The concept
     * @param language The language
	 * @return the concepts related to the input one
	 */
	public abstract Multimap<IPointer, ScoredItem<String>> getRelatedConceptsMap(String concept, Language language);
	
	/**
	 * Prints a concept from a {@link KnowledgeBase} into a readable string
	 * 
	 * @param concept the input concept to be printed
	 * @param language the language of interested (in case the concept is lexicalized
	 *            in many languages)
	 * @param verbose whether to print a full label or not
	 * @return a stringified representation for the input concept
	 */
	public abstract String conceptToString(String concept, Language language, boolean verbose);

	/**
	 * Prints a concept from a {@link KnowledgeBase} into a readable string
	 * (<b>short</b> label)
	 * 
	 * @param concept the input concept to be printed
	 * @param language the language of interested (in case the concept is lexicalized
	 *            in many languages)
	 * @return a stringified representation for the input concept
	 */
	public String conceptToString(String concept, Language language)
	{
		return conceptToString(concept, language, false);
	}
	
	/**
	 * Prints a concept from a {@link KnowledgeBase} into a readable string
	 * (<b>short</b> label with lexicalizations for <b>all</b> languages)
	 * 
	 * @param concept the input concept to be printed
	 * @return a stringified representation for the input concept
	 */
	public String conceptToString(String concept)
	{
		return conceptToString(concept, null);
	}

	/**
	 * Collects the WordNet offsets corresponding to the input concept, e.g.
	 * Babel synsets
	 * 
	 * @param concept the input concept
	 * @return the WordNet synset offsets corresponding to the input concept
	 */
	public List<String> conceptToWordNetOffset(String concept)
	{
		switch (this)
		{
			case WORDNET:
			case WORDNET_GLOSS:
			case WORDNETPLUSPLUS:
				return Collections.singletonList(concept);
			case BABELNET:
				try
				{
					BabelSynset synset = /*BabelNet.getInstance().getSynsetFromId(concept)*/(new BabelSynsetID(concept).toSynset());
					if (synset != null)
						return //synset.getWordNetOffsets();
                            synset.getWordNetOffsets().stream()
                            .map(Object::toString)
                            .collect(Collectors.toList());
					else
						return new ArrayList<String>();
				}
				catch (/*IOException*/Exception e) { return new ArrayList<String>(); }
			default:
				throw new IllegalArgumentException("Unknown knowledge base: " + this);
		}
	}
	
	/**
	 * Given an IPointer given its symbol
	 * 
	 * @param pointerSymbol The pointer symbol
	 * 
	 * @see IPointer
	 * @see WordNetPlusPlusPointer
	 * @see BabelPointer
	 * 
	 * @return an object of type IPointer given its string representation
	 */
	public abstract IPointer getPointer(String pointerSymbol);
	
	
	/**
	 * Retrieves all paths in a KnowledgeBase from a starting concept up
	 * to a certain depth
	 * 
	 * @param srcConcept The src Concept
	 * @param maxDepth the maxDepth
	 * @return collects all paths starting from the input concept up to a
	 *         maximum depth
	 */
	public Set<List<String>> getAllPathsFrom(final String srcConcept, int maxDepth)
	{
		final Set<List<String>> paths = new HashSet<List<String>>();
		getAllPathsFrom(srcConcept, "", -1.0, 0, new HashSet<String>(), new ArrayList<String>(), paths, maxDepth);
		return paths;
	}
	
	private void getAllPathsFrom(final String currentConcept, final String currentRelation,
								   final double currentRelationWeight, final int currentDepth,
								   final Set<String> done, final List<String> currentPath, 
								   final Set<List<String>> paths, final int maxDepth)
	{
		if (currentDepth > maxDepth) return; // we are done here
		if (done.contains(currentConcept)) return; // cycle: return 
		
		// aggiorna i nodi visti
		Set<String> newDone = new HashSet<String>(done);
		newDone.add(currentConcept);

		// aggiorna il cammino fino a qui
		List<String> newPath = new ArrayList<String>(currentPath);
		if (!currentRelation.isEmpty())
			newPath.add(currentRelation);
		if (currentRelationWeight != -1.0)
			newPath.add(NUMBER_FORMAT.format(currentRelationWeight));
			
		newPath.add(currentConcept);
		paths.add(newPath);
		
		Multimap<IPointer, ScoredItem<String>> relatedConceptsMap = 
			getRelatedConceptsMap(currentConcept);
		for (IPointer pointer : relatedConceptsMap.keySet())
		{
			String pointerSymbol = pointer.getSymbol();
			Collection<ScoredItem<String>> relatedConcepts = relatedConceptsMap.get(pointer);
			for (ScoredItem<String> relatedConcept : relatedConcepts)
			{
				getAllPathsFrom(relatedConcept.getItem(), pointerSymbol,
								relatedConcept.getScore(), currentDepth+1,
								newDone, newPath, paths, maxDepth);
			}
		}
	}
	
	/**
	 * Provides an iterator over all concepts of a knowledge base
	 * 
	 * @return an Iterator over the KB's concepts
	 */
	public abstract Iterator<String> getConceptIterator();
	
	private static double BABELNET_GRAPH_THRESHOLD =
		//BabelNetConfiguration.getInstance().getBabelEdgeWeightThreshold();
        0.5d; //TODO: Try to guess the best value for this
	
	private static NumberFormat NUMBER_FORMAT = DecimalFormat.getInstance(Locale.US);
    static { NUMBER_FORMAT.setMaximumFractionDigits(5); }
	
	/**
	 * Just for testing
	 * 
	 * @param args The args
	 */
	public static void main(String[] args)
	{
		try
		{
			KnowledgeBase kb = WORDNET;
			Word word = new Word("vehicle", 'n');
			String firstSense = kb.getConcepts(word).get(0);
			System.out.println("COLLECTING PATHS FROM FIRST SENSE OF: " + word);

			Set<List<String>> paths = kb.getAllPathsFrom(firstSense, 2);
			for (List<String> path : paths)
			{
				System.out.println("  PATH:" + path);
			}
			System.out.println("DONE!");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}