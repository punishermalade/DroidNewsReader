package com.neilsonmarcil.droidnewsreader;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.neilsonmarcil.droidnewsreader.dto.NewsComment;
import com.neilsonmarcil.droidnewsreader.dto.SingleNews;
import com.neilsonmarcil.droidnewsreader.util.TimeFormatUtil;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test Class for the NewsDetailsActivity data
 */
@RunWith(AndroidJUnit4.class)
public class NewsDetailActivityDataTest {

    /**
     * represents the news that is displayed
     */
    private static SingleNews news1;

    /**
     * the different comments to be displayed
     */
    private static NewsComment comm1, comm2, comm3, comm4, comm5, rep11, rep31, rep41, rep51, rep52;

    /**
     * the instance of the activity
     */
    private NewsDetailsActivity mActivity;

    @Rule
    public ActivityTestRule<NewsDetailsActivity> mActivityRule = new ActivityTestRule<>(NewsDetailsActivity.class, false, false);

    @Before
    public void init() {
        // restoring the news comments
        news1.NewsComments = new NewsComment[] { comm1, comm2, comm3, comm4, comm5 };

        Intent i = new Intent("android.intent.category.NEWS_DETAILS");
        i.putExtra(NewsDetailsActivity.REFRESH_ON_START_FLAG, false);
        i.putExtra("news_id", 1);
        mActivity = mActivityRule.launchActivity(i);
    }

    @Test
    public void checkNumberOfComments() throws Throwable {
        mActivityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivity.setDetailsContent(news1);
                ListView list = (ListView)mActivity.findViewById(R.id.news_details_comments_listview);
                Assert.assertTrue(list.getCount() == news1.NewsComments.length);
            }
        });
    }

    @Test
    public void checkHeader() throws Throwable {
        mActivityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mActivity.setDetailsContent(news1);
                TextView title = (TextView)mActivity.findViewById(R.id.news_details_title);
                Assert.assertTrue(title.getText().equals(news1.Title));

                TextView comment = (TextView)mActivity.findViewById(R.id.news_details_nb_comments);
                Assert.assertTrue(comment.getText().equals("Last 5 comments"));

                // replacing comments with 1 comment only
                news1.NewsComments = new NewsComment[] { comm1 };
                mActivity.setDetailsContent(news1);
                Assert.assertTrue(comment.getText().equals("Last comment"));

                // replacing with no comments
                news1.NewsComments = new NewsComment[0];
                mActivity.setDetailsContent(news1);
                Assert.assertTrue(comment.getText().equals("No comments"));

            }
        });
    }

    @Test
    public void repliesOfComments() throws Throwable {
        mActivityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mActivity.setDetailsContent(news1);
                ListView list = (ListView)mActivity.findViewById(R.id.news_details_comments_listview);
                Assert.assertTrue(list != null);
                Assert.assertTrue(list.getCount() > 0);

                // checking the number of replies for each comment
                NewsComment comment = (NewsComment)list.getAdapter().getItem(0);
                Assert.assertEquals(1, comment.Replies.length);

                comment = (NewsComment)list.getAdapter().getItem(1);
                Assert.assertEquals(0, comment.Replies.length);

                comment = (NewsComment)list.getAdapter().getItem(2);
                Assert.assertEquals(1, comment.Replies.length);

                comment = (NewsComment)list.getAdapter().getItem(3);
                Assert.assertEquals(1, comment.Replies.length);

                // this one has more than 1 reply, but we only show 1 in the UI
                comment = (NewsComment)list.getAdapter().getItem(4);
                Assert.assertEquals(2, comment.Replies.length);
            }
        });
    }

    @Test
    public void dataRepliesFromComment() throws Throwable {
        mActivityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mActivity.setDetailsContent(news1);
                ListView list = (ListView)mActivity.findViewById(R.id.news_details_comments_listview);
                Assert.assertTrue(list.getCount() > 0);

                for (int i = 0; i < list.getCount(); i++) {
                    View row = list.getAdapter().getView(i, null, list);
                    NewsComment comment = (NewsComment)list.getAdapter().getItem(i);
                    dataRepliesFromCommentRow(row, comment);
                }
            }
        });
    }

    /**
     * Test the data from a single row from the comments list
     * @param v the view row
     * @param c the comment object
     */
    private void dataRepliesFromCommentRow(View v, NewsComment c) {

        TextView author = (TextView)v.findViewById(R.id.comment_list_item_author);
        Assert.assertTrue(author.getText().equals(c.Author));

        TextView message = (TextView)v.findViewById(R.id.comment_list_item_content);
        Assert.assertTrue(message.getText().equals(Html.fromHtml(c.Body)));

        TextView date = (TextView)v.findViewById(R.id.comment_list_item_date);
        long time = System.currentTimeMillis() - (c.Date * 1000);
        Assert.assertTrue(date.getText().equals(TimeFormatUtil.formatTimespanAgoFirstUnit(time)));

        if (c.Replies.length > 0) {

            // checking if it's visible
            LinearLayout layout = (LinearLayout)v.findViewById(R.id.comment_list_item_reply_layout);
            Assert.assertEquals(View.VISIBLE, layout.getVisibility());

            // checking the rest of the data
            NewsComment reply = c.Replies[0];
            TextView repAuthor = (TextView)v.findViewById(R.id.comment_list_item_reply_author);
            Assert.assertTrue(repAuthor.getText().equals(reply.Author));

            TextView repMessage = (TextView)v.findViewById(R.id.comment_list_item_reply_content);
            Assert.assertTrue(repMessage.getText().equals(Html.fromHtml(reply.Body)));

            TextView repDate = (TextView)v.findViewById(R.id.comment_list_item_reply_date);
            long repTime = System.currentTimeMillis() - (reply.Date * 1000);
            Assert.assertTrue(repDate.getText().equals(TimeFormatUtil.formatTimespanAgoFirstUnit(repTime)));
        }
    }

    /**
     * init the data set to be used in this test class
     */
    @BeforeClass
    public static void initDataSet() {

        rep11 = new NewsComment();
        rep11.Author = "Author Rep11";
        rep11.Body = "Body Rep11";
        rep11.Date = 233412413;
        rep11.Id = 1100;
        rep11.ParentId = 10;
        rep11.CommentsIds = new int[0];
        rep11.Replies = new NewsComment[0];

        rep31 = new NewsComment();
        rep31.Author = "Author Rep31";
        rep31.Body = "Body Rep31";
        rep31.Date = 982438763;
        rep31.Id = 3100;
        rep31.ParentId = 30;
        rep31.CommentsIds = new int[0];
        rep31.Replies = new NewsComment[0];

        rep41 = new NewsComment();
        rep41.Author = "Author Rep41";
        rep41.Body = "Body Rep41";
        rep41.Date = 111231234;
        rep41.Id = 4100;
        rep41.ParentId = 40;
        rep41.CommentsIds = new int[0];
        rep41.Replies = new NewsComment[0];

        rep51 = new NewsComment();
        rep51.Author = "Author Rep51";
        rep51.Body = "Body Rep51";
        rep51.Date = 564812927;
        rep51.Id = 5100;
        rep51.ParentId = 50;
        rep51.CommentsIds = new int[0];
        rep51.Replies = new NewsComment[0];

        rep52 = new NewsComment();
        rep52.Author = "Author Rep52";
        rep52.Body = "Body Rep52";
        rep52.Date = 432341111;
        rep52.Id = 5200;
        rep52.ParentId = 50;
        rep52.CommentsIds = new int[0];
        rep52.Replies = new NewsComment[0];

        comm1 = new NewsComment();
        comm1.Author = "Author 1";
        comm1.Body = "Body 1";
        comm1.Date = 213213413;
        comm1.Id = 10;
        comm1.ParentId = 1;
        comm1.CommentsIds = new int[] { 1100 };
        comm1.Replies = new NewsComment[] { rep11 };

        comm2 = new NewsComment();
        comm2.Author = "Author 2";
        comm2.Body = "Body 2";
        comm2.Date = 41453423;
        comm2.Id = 20;
        comm2.ParentId = 1;
        comm2.CommentsIds = new int[0];
        comm2.Replies = new NewsComment[0];

        comm3 = new NewsComment();
        comm3.Author = "Author 3";
        comm3.Body = "Body 3";
        comm3.Date = 977865978;
        comm3.Id = 30;
        comm3.ParentId = 1;
        comm3.CommentsIds = new int[] { 3100 };
        comm3.Replies = new NewsComment[] { rep31 };

        comm4 = new NewsComment();
        comm4.Author = "Author 4";
        comm4.Body = "Body 4";
        comm4.Date = 41453423;
        comm4.Id = 40;
        comm4.ParentId = 1;
        comm4.CommentsIds = new int[] { 4100 };
        comm4.Replies = new NewsComment[] { rep41 };

        comm5 = new NewsComment();
        comm5.Author = "Author 5";
        comm5.Body = "Body 5";
        comm5.Date = 421314423;
        comm5.Id = 50;
        comm5.ParentId = 1;
        comm5.CommentsIds = new int[] { 5100, 5200 };
        comm5.Replies = new NewsComment[] { rep51, rep52 };

        news1 = new SingleNews();
        news1.Id = 1;
        news1.Title = "Title 1";
        news1.Score = 200;
        news1.Author = "User 1";
        news1.Date = 213412313;
        news1.Url = "http://omniscia.no-ip.biz";
        news1.Comments = new int[] { 10, 20, 30, 40, 50 };
        news1.NewsComments = new NewsComment[] { comm1, comm2, comm3, comm4, comm5 };

    }
}
