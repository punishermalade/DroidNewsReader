package com.neilsonmarcil.droidnewsreader;

import android.content.ComponentName;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Button;
import android.widget.ListView;

import com.neilsonmarcil.droidnewsreader.adapter.NewsListArrayAdapter;
import com.neilsonmarcil.droidnewsreader.dto.NewsComment;
import com.neilsonmarcil.droidnewsreader.dto.SingleNews;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowActivity;

/**
 * Created by punisher on 2016-04-29.
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityUITest {

    /**
     * the data to be used in the test
     */
    private static SingleNews news1, news2, news3, news4, news5;

    /**
     * the array to be used in the test, contains the SingleNews instance
     */
    private static SingleNews[] mNewsArray;

    /**
     * reference to the MainActivity
     */
    private MainActivity mActivity;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<MainActivity>(MainActivity.class, false, false);


    @Test
    public void websiteButtonEvent() throws Throwable {
        /*
        mActivityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent("android.intent.category.LAUNCHER");
                i.putExtra(MainActivity.REFRESH_ON_START_FLAG, false);

                mActivity = Robolectric.buildActivity(MainActivity.class).withIntent(i).create().visible().get();
                //ShadowActivity shadow = Robolectric.shadowOf(mActivity);

                ListView list = (ListView)mActivity.findViewById(R.id.main_activity_listview);
                Assert.assertTrue(list != null);

                list.setAdapter(initAdapter());
                Assert.assertTrue(list.getCount() > 0);
            }
        });
        */




        /*
                //.create().start().visible().get();
        ShadowActivity shadowHome = Robolectric.shadowOf(homeActivity);
        Button btnLaunchSchedule = (Button) homeActivity.findViewById(R.id.btnLaunchSchedule);
        Robolectric.clickOn(btnLaunchSchedule);

        assertThat(shadowHome.peekNextStartedActivityForResult().intent.getComponent(), equalTo(new ComponentName(homeActivity, ScheduleActivity.class)));
        */
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