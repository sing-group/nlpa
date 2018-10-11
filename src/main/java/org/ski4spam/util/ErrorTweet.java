package org.ski4spam.util;

/**
  * A POJO (plain old java object) to represent a Tweet error
  * @author Jeray Lage
  */
public class ErrorTweet {
	/**
	  * The tweet Id
	  */
    private Long tweetId;
	
	/**
	  * The error for the teetID
	  */
    private String error;

   /**
		* Build an ErrorTweet from the tweet id and the error
		* @param tweetId The id of the tweet
		* @param error A String representing the error
		*/
    public ErrorTweet(Long tweetId, String error) {
        this.tweetId = tweetId;
        this.error = error;
    }

    /**
		* Achieves the identifier of the tweet
		* @return the identifier of the tweet
		*/
    public Long getTweetId() {
        return tweetId;
    }

    /**
		* Achieves the error for the tweet
		* @return the tweet error
		*/
    public String getError() {
        return error;
    }
	 
	 /**
		* Sets the error of the tweet
		* @param error The error of the tweet
		*/
	 public void setError(String error){
		 this.error=error;
	 }
	 
	 /**
		* Sets the tweetId for the current tweet
		* @param tweetId the tweetId for the current tweet
		*/
	 public void setTweetId(Long tweetId){
		 this.tweetId=tweetId;
	 }
}
