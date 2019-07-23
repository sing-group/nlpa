/*-
 * #%L
 * NLPA
 * %%
 * Copyright (C) 2018 - 2019 SING Group (University of Vigo)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package org.nlpa.util;

/**
 * This class is to represent a babelfy Semantic annotation with all relevant
 * attributes to made intensive searches and discard the irrelevant information
 * achieved by Babelfy
 *
 * @author María Novo
 */
class BabelfyEntry {

    private int startIdx;
    private int endIdx;
    private double score;
    private String synsetId;
    private String text;

    /**
     * Default constructor
     */
    public BabelfyEntry() {
    }

    /**
     * Constructor that stablish all attributes of a BabelfyEntry
     *
     * @param endIdx The last index of the entry
     * @param synsetId The synset ID
     * @param score The score
     * @param startIdx The start index of an entry
     * @param text The text of an entry
     */
    public BabelfyEntry(int startIdx, int endIdx, Double score, String synsetId, String text) {
        this.startIdx = startIdx;
        this.endIdx = endIdx;
        this.score = score;
        this.synsetId = synsetId;
        this.text = text;
    }

    /**
     * Returns the start index of an entry
     *
     * @return The start index of an entry
     */
    public int getStartIdx() {
        return startIdx;
    }

    /**
     * Stablish the start index of an entry
     *
     * @param startIdx The start index of an entry
     */
    public void setStartIdx(int startIdx) {
        this.startIdx = startIdx;
    }

    /**
     * Return the end index for an entry
     *
     * @return he last index of the entry
     */
    public int getEndIdx() {
        return endIdx;
    }

    /**
     * Stablish the end index for an entry
     *
     * @param endIdx The last index of the entry
     */
    public void setEndIdx(int endIdx) {
        this.endIdx = endIdx;
    }

    /**
     * Returns the score of an entry
     *
     * @return the score of an entry
     */
    public double getScore() {
        return score;
    }

    /**
     * Change the score of an entry
     *
     * @param score the score of the entry
     */
    public void setScore(double score) {
        this.score = score;
    }

    /**
     * Returns the synsetId for the entry
     *
     * @return the synsetId for the entry
     */
    public String getSynsetId() {
        return synsetId;
    }

    /**
     * Stablish the synsetId for the entry
     *
     * @param synsetID the synsetId for the entry
     */
    public void setSynsetId(String synsetId) {
        this.synsetId = synsetId;
    }

    /**
     * Returns the text for an entrty
     *
     * @return the text for the entry
     */
    public String getText() {
        return this.text;
    }

    /**
     * Stablish the text for an entry
     *
     * @param text The text for the entry
     */
    public void setText(String text) {
        this.text = text;
    }

}
