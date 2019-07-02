package org.nlpa.util;

import java.util.Date;

import org.jetbrains.annotations.NotNull;

import twitter4j.ExtendedMediaEntity;
import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Place;
import twitter4j.RateLimitStatus;
import twitter4j.Scopes;
import twitter4j.Status;
import twitter4j.SymbolEntity;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserMentionEntity;

/**
 * Represents the status of a tweet holding the following information:
 * <ul>
 * <li> createdAt: The creation date </li>
 * <li> id: the tweet Id </li>
 * <li> text: the text for the tweet </li>
 * <li> lang: The language for the tweet </li>
 * </ul>
 *
 * @author Yeray Lage
 * @see twitter4j.Status
 * @see twitter4j.EntitySupport
 * @see twitter4j.TwitterResponse Link to javadoc documentation of twitter4j:
 * <a href="http://twitter4j.org/oldjavadocs/4.0.4-SNAPSHOT/index.html">http://twitter4j.org/oldjavadocs/4.0.4-SNAPSHOT/index.html</a>
 * Note: Most javadoc documentation has been taken from the original interfaces
 * from twitter4j API
 */
public class TweetStatus implements Status {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * The creation date of the tweet
     */
    private Date createdAt;

    /**
     * The identifier of the tweet
     */
    private long id;

    /**
     * The text of the tweet
     */
    private String text;

    /**
     * The lang of the tweet
     */
    private String lang;
    
    /**
     * Indicates if tweet is correct
     */
    private boolean error;

    /**
     * Create a TweetStatus
     *
     * @param createdAt The date of creation
     * @param id The identifier of the tweet
     * @param text The text of the tweet
     * @param lang The language of the tweet
     * @param error Indicates if the tweet is correct
     */
    public TweetStatus(Date createdAt, long id, String text, String lang, boolean error) {
        this.createdAt = createdAt;
        this.id = id;
        this.text = text;
        this.lang = lang;
        this.error = error;
        
    }

    /**
     * Return the creation date of the tweet
     *
     * @return the creation date
     */
    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * Return the identifier of the tweet
     *
     * @return the identifier of the tweet
     */
    @Override
    public long getId() {
        return id;
    }

    /**
     * Return the text of the tweet
     *
     * @return the text of the tweet
     */
    @Override
    public String getText() {
        return text;
    }

    /**
     * Return the source text of the tweet
     *
     * @return the source text of the tweet
     */
    @Override
    public String getSource() {
        return null;
    }

    /**
     * Determine whether the tweet is truncated or not
     *
     * @return True if the tweet is truncated or false otherwise
     */
    @Override
    public boolean isTruncated() {
        return false;
    }

    /**
     * Returns the in_reply_tostatus_id
     *
     * @return The in_reply_tostatus_id
     */
    @Override
    public long getInReplyToStatusId() {
        return 0;
    }

    /**
     * Returns the in_reply_user_id
     *
     * @return The in_reply_user_id
     */
    @Override
    public long getInReplyToUserId() {
        return 0;
    }

    /**
     * Returns the in_reply_to_screen_name
     *
     * @return The in_in_reply_to_screen_name
     */
    @Override
    public String getInReplyToScreenName() {
        return null;
    }

    /**
     * Returns The location that this tweet refers to if available.
     *
     * @return The location that this tweet refers to if available (can be null)
     */
    @Override
    public GeoLocation getGeoLocation() {
        return null;
    }

    /**
     * Returns the place attached to this status
     *
     * @return The place attached to this status
     */
    @Override
    public Place getPlace() {
        return null;
    }

    /**
     * Test if the status is favorited
     *
     * @return true if favorited
     */
    @Override
    public boolean isFavorited() {
        return false;
    }

    /**
     * Test if the status is retweeted
     *
     * @return true if retweeted
     */
    @Override
    public boolean isRetweeted() {
        return false;
    }

    /**
     * Indicates approximately how many times this Tweet has been "favorited" by
     * Twitter users.
     *
     * @return the favorite count
     */
    @Override
    public int getFavoriteCount() {
        return 0;
    }

    /**
     * Return the user associated with the status. This can be null if the
     * instance is from User.getStatus().
     *
     * @return The user
     */
    @Override
    public User getUser() {
        return null;
    }

    /**
     * Determine if the status is retweet or not
     *
     * @return true if this is a retweet
     */
    @Override
    public boolean isRetweet() {
        return false;
    }

    /**
     * Returns the retweeted status
     *
     * @return the retweeted status
     */
    @Override
    public Status getRetweetedStatus() {
        return null;
    }

    /**
     * Returns an array of contributors, or null if no contributor is associated
     * with this status.
     *
     * @return contributors
     */
    @Override
    public long[] getContributors() {
        return new long[0];
    }

    /**
     * Returns the number of times this tweet has been retweeted, or -1 when the
     * tweet was created before this feature was enabled.
     *
     * @return The retweet count
     */
    @Override
    public int getRetweetCount() {
        return 0;
    }

    /**
     * Returns true if the authenticating user has retweeted this tweet, or
     * false when the tweet was created before this feature was enabled.
     *
     * @return whether the authenticating user has retweeted this tweet.
     */
    @Override
    public boolean isRetweetedByMe() {
        return false;
    }

    /**
     * Returns the authenticating user's retweet's id of this tweet, or -1L when
     * the tweet was created before this feature was enabled.
     *
     * @return the authenticating user's retweet's id of this tweet
     */
    @Override
    public long getCurrentUserRetweetId() {
        return 0;
    }

    /**
     * Returns true if the status contains a link that is identified as
     * sensitive.
     *
     * @return whether the status contains sensitive links
     */
    @Override
    public boolean isPossiblySensitive() {
        return false;
    }

    /**
     * Returns the targeting scopes applied to a status.
     *
     * @return the targeting scopes applied to a status.
     */
    @Override
    public String getLang() {
        return lang;
    }
    
     /**
     * Returns the targeting scopes applied to a status.
     *
     * @return the targeting scopes applied to a status.
     */
    //@Override
    public boolean getError() {
        return this.error;
    }

    /**
     * Returns the targeting scopes applied to a status.
     *
     * @return The targeting scopes applied to a status.
     */
    @Override
    public Scopes getScopes() {
        return null;
    }

    /**
     * Returns the list of country codes where the tweet is withheld
     *
     * @return A list of country codes where the tweet is withheld - null if not
     * withheld
     */
    @Override
    public String[] getWithheldInCountries() {
        return new String[0];
    }

    /**
     * Returns the Tweet ID of the quoted Tweet
     *
     * @return the Tweet ID of the quoted Tweet
     */
    @Override
    public long getQuotedStatusId() {
        return 0;
    }

    /**
     * Returns the Tweet object of the original Tweet that was quoted. the
     * quoted Tweet object
     */
    @Override
    public Status getQuotedStatus() {
        return null;
    }

    /**
     * Determines if the tweet status is the same than other indicated (o)
     *
     * @param o a non null twitter status for comparison purposes
     * @return 0 if the o status is the same than the current one
     */
    @Override
    public int compareTo(@NotNull Status o) {
        return 0;
    }

    /**
     * Returns an array of user mentions in the tweet. This method will return
     * an empty array if no users were mentioned in the tweet.
     *
     * @return An array of user mention entities in the tweet.
     */
    @Override
    public UserMentionEntity[] getUserMentionEntities() {
        return new UserMentionEntity[0];
    }

    /**
     * Returns an array if URLEntity mentioned in the tweet. This method will
     * return an empty array if no url were mentioned in the tweet.
     *
     * @return An array of URLEntity mentioned in the tweet.
     */
    @Override
    public URLEntity[] getURLEntities() {
        return new URLEntity[0];
    }

    /**
     * Returns an array if hashtag mentioned in the tweet. This method will
     * return an empty array if no hashtags were mentioned in the tweet.
     *
     * @return An array of Hashtag mentioned in the tweet.
     */
    @Override
    public HashtagEntity[] getHashtagEntities() {
        return new HashtagEntity[0];
    }

    /**
     * Returns an array of MediaEntities if medias are available in the tweet.
     * This method will return an empty array if no medias were mentioned.
     *
     * @return an array of MediaEntities.
     */
    @Override
    public MediaEntity[] getMediaEntities() {
        return new MediaEntity[0];
    }

    /**
     * Returns an array of ExtendedMediaEntities if media of extended_entities
     * are available in the tweet. This method will an empty array if no
     * extended-medias were mentioned.
     *
     * @return an array of ExtendedMediaEntities.
     */
    @Override
    public ExtendedMediaEntity[] getExtendedMediaEntities() {
        return new ExtendedMediaEntity[0];
    }

    /**
     * Returns an array of SymbolEntities if medias are available in the tweet.
     * This method will return an empty array if no symbols were mentioned.
     *
     * @return an array of SymbolEntities.
     */
    @Override
    public SymbolEntity[] getSymbolEntities() {
        return new SymbolEntity[0];
    }

    /**
     * Returns the current rate limit status if available.
     *
     * @return current rate limit status
     */
    @Override
    public RateLimitStatus getRateLimitStatus() {
        return null;
    }

    /**
     * Determine the application permission model
     *
     * @return the application permission model
     */
    @Override
    public int getAccessLevel() {
        return 0;
    }
}
