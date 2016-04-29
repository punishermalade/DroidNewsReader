package com.neilsonmarcil.droidnewsreader.dto;

/**
 * Represents a summary of a Single News. No get/set, access the data directly from the object.
 */
public class SingleNews {

    public NewsComment[] NewsComments;

    /**
     * String array representation of the comments Id
     */
    public int[] Comments;

    /**
     * Represents the Url to access the news
     */
    public String Url;

    /**
     * Represents the score for the news
     */
    public int Score;

    /**
     * Represents the author of the post
     */
    public String Author;

    /**
     * Represents the body of the news
     */
    public String Body;

    /**
     * Represents the news title
     */
    public String Title;

    /**
     * Represents the news date
     */
    public long Date;

    /**
     * Represents a unique ID
     */
    public int Id;

}
