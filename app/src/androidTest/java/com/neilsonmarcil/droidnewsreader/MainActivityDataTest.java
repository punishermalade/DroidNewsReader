package com.neilsonmarcil.droidnewsreader;

import android.content.Intent;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.neilsonmarcil.droidnewsreader.adapter.NewsListArrayAdapter;
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
 * a Test class for the MainActivity
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityDataTest {

    /**
     * the data to be used in the test
     */
    private static SingleNews news1, news2, news3, news4, news5;

    /**
     * the array to be used in the test, contains the SingleNews instance
     */
    private static SingleNews[] mNewsArray;

    /**
     * a reference to the activity
     */
    private MainActivity mActivity;

    /**
     * the ActivityTestRule for the MainActivity class
     */
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class, false, false);

    /**
     * Set the REFRESH_ON_START_FLAG to false and launch the LAUNCHER intent before every test
     */
    @Before
    public void init() {
        Intent i = new Intent("android.intent.category.LAUNCHER");
        i.putExtra(MainActivity.REFRESH_ON_START_FLAG, false);

        // launching the activity through the intent and keeping the reference for the tests
        mActivity = mActivityRule.launchActivity(i);
    }

    @Test
    public void numberOfItemInListView() throws Throwable {
        mActivityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView list = (ListView)mActivity.findViewById(R.id.main_activity_listview);
                Assert.assertTrue("ListView is null", list != null);

                // setting the adapter
                list.setAdapter(initAdapter());
                Assert.assertTrue("ListView count", list.getCount() == mNewsArray.length);
            }
        });
    }

    @Test
    public void orderOfItemInListView() throws Throwable {
        mActivityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView list = (ListView)mActivity.findViewById(R.id.main_activity_listview);
                Assert.assertTrue(list != null);

                list.setAdapter(initAdapter());
                Assert.assertTrue( (list.getItemAtPosition(0)).equals(news1) );
                Assert.assertTrue( (list.getItemAtPosition(1)).equals(news2) );
                Assert.assertTrue( (list.getItemAtPosition(2)).equals(news3) );
                Assert.assertTrue( (list.getItemAtPosition(3)).equals(news4) );
                Assert.assertTrue( (list.getItemAtPosition(4)).equals(news5) );
            }
        });
    }

    @Test
    public void contentInTheFirstNews() throws Throwable {
        mActivityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView list = (ListView)mActivity.findViewById(R.id.main_activity_listview);
                Assert.assertTrue(list != null);

                list.setAdapter(initAdapter());

                SingleNews firstNew = (SingleNews)list.getItemAtPosition(0);
                Assert.assertTrue(firstNew != null);
                Assert.assertTrue(firstNew.equals(news1));

                for (int i = 0; i < list.getCount(); i++) {
                    checkContentOnView(list.getAdapter().getView(i, null, list), mNewsArray[i]);
                }
            }
        });
    }



    /**
     * generic method to check a specific view from the listview
     * @param v the view from the listview
     * @param n the news associated to the view
     */
    private void checkContentOnView(View v, SingleNews n) {

        Assert.assertTrue(v != null);
        Assert.assertTrue(n != null);

        TextView title = (TextView)v.findViewById(R.id.news_list_item_title);
        TextView author = (TextView)v.findViewById(R.id.news_list_item_author);
        TextView score = (TextView)v.findViewById(R.id.news_list_item_score);
        TextView date = (TextView)v.findViewById(R.id.news_list_item_date);
        TextView url = (Button)v.findViewById(R.id.news_list_item_url);

        Assert.assertTrue(title.getText().equals(n.Title));
        Assert.assertTrue(author.getText().equals(n.Author));
        Assert.assertTrue(score.getText().equals(String.valueOf(n.Score)));

        long timeAgo = System.currentTimeMillis() - (n.Date * 1000L);
        String timeAgoStr = TimeFormatUtil.formatTimespanAgoFirstUnit(timeAgo);
        Assert.assertTrue(date.getText().equals(timeAgoStr));

        Object tag = url.getTag();
        Assert.assertTrue(tag.equals(n.Url));
    }

    /**
     * Init the ListAdapter, to avoid code redundancy in the test function
     * @return an initialized ListAdapter with data
     */
    private NewsListArrayAdapter initAdapter() {
        return new NewsListArrayAdapter(mActivity,
                                        R.layout.news_list_single_row,
                                        mNewsArray);
    }

    @BeforeClass
    public static void initDataSet() {
        news1 = new SingleNews();
        news1.Id = 1;
        news1.Title = "Title 1";
        news1.Score = 200;
        news1.Author = "User 1";
        news1.Date = 213412313;
        news1.Url = "http://omniscia.no-ip.biz";
        news1.Comments = new int[0];
        news1.NewsComments = new NewsComment[0];

        news2 = new SingleNews();
        news2.Id = 2;
        news2.Title = "Title 2";
        news2.Score = 100;
        news2.Author = "User 2";
        news2.Date = 32412313;
        news2.Url = "http://omniscia.no-ip.biz";
        news2.Comments = new int[0];
        news2.NewsComments = new NewsComment[0];

        news3 = new SingleNews();
        news3.Id = 3;
        news3.Title = "Title 3";
        news3.Score = 300;
        news3.Author = "User 3";
        news3.Date = 43242412;
        news3.Url = "";
        news3.Comments = new int[0];
        news3.NewsComments = new NewsComment[0];

        news4 = new SingleNews();
        news4.Id = 4;
        news4.Title = "Title 4";
        news4.Score = 300;
        news4.Author = "User 4";
        news4.Date = 324241122;
        news4.Url = "http://omniscia.no-ip.biz";
        news4.Comments = new int[0];
        news4.NewsComments = new NewsComment[0];

        news5 = new SingleNews();
        news5.Id = 5;
        news5.Title = "Title 5";
        news5.Score = 250;
        news5.Author = "User 5";
        news5.Date = 1232134124;
        news5.Url = "http://omniscia.no-ip.biz";
        news5.Comments = new int[0];
        news5.NewsComments = new NewsComment[0];

        mNewsArray = new SingleNews[] { news1, news2, news3, news4, news5 };
    }

}
