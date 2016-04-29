package com.neilsonmarcil.droidnewsreader.dto;

/**
 * Represent a comment related to a story
 */
public class NewsComment {

    /**
     * Unique Id
     */
    public int Id;

    /**
     * parent Id
     */
    public int ParentId;

    /**
     * the author
     */
    public String Author;

    /**
     * the comment
     */
    public String Body;

    /**
     * the creation time in Unix time
     */
    public long Date;

    /**
     * the list of replies
     */
    public NewsComment[] Replies = new NewsComment[0];

    /**
     * the list of replies id
     */
    public int[] CommentsIds;
}
