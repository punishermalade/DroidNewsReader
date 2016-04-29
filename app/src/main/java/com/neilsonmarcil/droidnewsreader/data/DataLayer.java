package com.neilsonmarcil.droidnewsreader.data;

import com.neilsonmarcil.droidnewsreader.dto.NewsComment;
import com.neilsonmarcil.droidnewsreader.dto.SingleNews;

/**
 * Responsible to retrieve the data.
 */
public interface DataLayer {

    /**
     * Returns a list of Top Stories
     * @return an array containing SingeNews instance.
     */
    SingleNews[] getTopStories();

    /**
     * Returns a news based on the ID
     * @param id the unique Id for
     * @return a SingleNews
     */
    SingleNews getSpecificNews(int id);

    /**
     * get a specific comment with the unique id
     * @param id the unique id
     * @return a NewsComment instance
     */
    NewsComment getSpecificComment(int id);

    /**
     * returns the list of comment for a single story
     * @param news the story from which to retreive the comments
     * @return the list of comment related to this story
     */
    NewsComment[] getCommentsFromNews(SingleNews news);

}
