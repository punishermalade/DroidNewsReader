package com.neilsonmarcil.droidnewsreader;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.neilsonmarcil.droidnewsreader.adapter.CommentsListArrayAdapter;
import com.neilsonmarcil.droidnewsreader.data.DataLayer;
import com.neilsonmarcil.droidnewsreader.data.RESTDataLayer;
import com.neilsonmarcil.droidnewsreader.dto.NewsComment;
import com.neilsonmarcil.droidnewsreader.dto.SingleNews;

/**
 * Display the comments and reply for a specific story
 */
public class NewsDetailsActivity extends AppCompatActivity {

    private static final String TAG = "droidnewsreader";

    /**
     * represents the flag to enable the refresh on start
     */
    public static final String REFRESH_ON_START_FLAG = "refreshOnStart";

    /**
     * the task that use the DataLayer to retrieve the comments from a story
     */
    private AsyncTask<Integer, Void, SingleNews> mLoadingTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);

        Toolbar bar = (Toolbar)findViewById(R.id.news_details_activity_toolbar);
        setSupportActionBar(bar);

        Log.d(TAG, "NewsDetailActivity.onCreate(...): getIntent().getExtra(): " +
                getIntent().getIntExtra("news_id", -1));
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getIntent().getBooleanExtra(REFRESH_ON_START_FLAG, true)) {
            // creating the loading task
            mLoadingTask = createLoadingTask();

            // getting the news id
            int id = getIntent().getIntExtra("news_id", -1);
            if (id != -1) {
                mLoadingTask.execute(id);
            }
        }
    }

    /**
     * creates the task to retrieve the comments
     * @return an AsyncTask ready to be used
     */
    private AsyncTask<Integer, Void, SingleNews> createLoadingTask() {
        return new AsyncTask<Integer, Void, SingleNews>() {

            private ProgressDialog mDialog;

            @Override
            protected void onPreExecute() {
                mDialog = new ProgressDialog(NewsDetailsActivity.this);
                mDialog.setTitle(R.string.dialog_loading_singlestory_title);
                mDialog.setMessage(getString(R.string.dialog_loading_singlestory_message));
                mDialog.setIndeterminate(true);
                mDialog.setCancelable(true);
                mDialog.show();
            }

            @Override
            protected SingleNews doInBackground(Integer... params) {
                DataLayer dataLayer = RESTDataLayer.getInstance();
                int index = params[0];

                SingleNews theNews = dataLayer.getSpecificNews(index);
                theNews.NewsComments = dataLayer.getCommentsFromNews(theNews);

                return theNews;
            }

            @Override
            protected void onPostExecute(SingleNews n) {

                setDetailsContent(n);

                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        };
    }

    /**
     * function that can fill the Views with the details from the SingleNews object.
     * @param n the SingleNews
     */
    public void setDetailsContent(SingleNews n) {

        TextView title = (TextView)findViewById(R.id.news_details_title);
        title.setText(n.Title);

        TextView nbComments = (TextView)findViewById(R.id.news_details_nb_comments);

        // bug fix with the getQuantityString. Will check if it's zero and get the string directly
        // otherwise will use the getquantityString method
        String nbCommentsStr = getString(R.string.news_list_header_no_comment);
        if (n.NewsComments.length > 0) {
            nbCommentsStr = getResources().getQuantityString(R.plurals.comments, n.NewsComments.length, n.NewsComments.length);
        }
        nbComments.setText(nbCommentsStr);

        ListView listView = (ListView)findViewById(R.id.news_details_comments_listview);
        ArrayAdapter<NewsComment> comments = new CommentsListArrayAdapter(NewsDetailsActivity.this,
                R.layout.comments_list_single_row,
                n.NewsComments);

        listView.setAdapter(comments);
    }
}
