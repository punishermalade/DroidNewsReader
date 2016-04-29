package com.neilsonmarcil.droidnewsreader;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.neilsonmarcil.droidnewsreader.adapter.NewsListArrayAdapter;
import com.neilsonmarcil.droidnewsreader.data.DataLayer;
import com.neilsonmarcil.droidnewsreader.data.RESTDataLayer;
import com.neilsonmarcil.droidnewsreader.dto.SingleNews;

/**
 * Application entry point, this Activity is the first one to be displayed. It handles all the logic
 * to retrieve the data and show it to the user. It also configure the ListView to reload and
 * navigation to the detailed view of a SingleNews
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "droidnewsreader";

    /**
     * represents the flag to be used in the Extra to activate the Refresh data on start
     */
    public static final String REFRESH_ON_START_FLAG = "refreshOnStart";

    /**
     * the ListView that contains the top stories item
     */
    private ListView mListView;

    /**
     * the Swipe layout to have the pull to refresh feature
     */
    private SwipeRefreshLayout mSwipeLayout;

    /**
     * represents the DataLayer to be used to retreive the data
     */
    private DataLayer mDataLayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // main activity content
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // top action bar
        Toolbar toolbar = (Toolbar)findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);

        // setting the DataLayer
        mDataLayer = RESTDataLayer.getInstance();

        // getting a refrence to the ListView
        mListView = (ListView)findViewById(R.id.main_activity_listview);
        mSwipeLayout = (SwipeRefreshLayout)findViewById(R.id.main_activity_swipe_layout);

        // configuring the listview to handle different events
        configureListViewReload();
        configureListViewItemClick();

        // loading the data if the flag is null or true
        boolean refreshOnStart = getIntent().getBooleanExtra(REFRESH_ON_START_FLAG, true);
        if (refreshOnStart) {
            createLoadingTask().execute();
        }

        Log.d(TAG, "MainActivity.onCreate(...) completed");
    }

    /**
     * This function creates a a new task to retrieve the data on a worker thread.
     * @return an AsyncTask to be executed later.
     */
    private AsyncTask<Void, Void, SingleNews[]> createLoadingTask() {
        return new AsyncTask<Void, Void, SingleNews[]>() {

            private ProgressDialog mDialog;

            @Override
            protected void onPreExecute() {

                if (!mSwipeLayout.isRefreshing()) {

                    mDialog = new ProgressDialog(MainActivity.this);
                    mDialog.setTitle(R.string.dialog_loading_topstories_title);
                    mDialog.setMessage(getString(R.string.dialog_loading_topstories_message));
                    mDialog.setIndeterminate(true);
                    mDialog.setCancelable(true);
                    mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            cancel(true);
                        }
                    });

                    mDialog.show();
                }
            }

            @Override
            protected SingleNews[] doInBackground(Void... params) {

                Log.i(TAG, "MainActivity.LoadingTask starting...");

                SingleNews[] news = new SingleNews[0];

                try {
                    news = mDataLayer.getTopStories();
                }
                catch (Exception ex) {
                    cancel(true);
                    Log.e(TAG, "MainActivity.LoadingTask threw an exception", ex);
                }

                Log.i(TAG, "MainActivity.LoadingTask done. Returning " + news.length + " items");
                return news;
            }

            @Override
            protected void onCancelled() {

                dismissDialog();

                // saying the refresh is done on the SwipeRefreshLayout
                mSwipeLayout.setRefreshing(false);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.dialog_loading_list_error_title);
                builder.setMessage(R.string.dialog_loading_list_error_message);
                builder.setNegativeButton(getString(android.R.string.ok), null);
                builder.show();


            }

            @Override
            protected void onPostExecute(SingleNews[] news) {

                // loading the data into the list view
                ArrayAdapter<SingleNews> adapter = new NewsListArrayAdapter(MainActivity.this,
                                                        R.layout.news_list_single_row,
                                                        news);
                mListView.setAdapter(adapter);

                dismissDialog();

                // saying the refresh is done on the SwipeRefreshLayout
                mSwipeLayout.setRefreshing(false);
            }

            private void dismissDialog() {
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        };
    }

    /**
     * Create and assign the OnItemClickListener for the ListView.
     */
    private void configureListViewItemClick() {
        ListViewClickListener listener = new ListViewClickListener();
        mListView.setOnItemClickListener(listener);
        Log.d(TAG, "MainActivity.configureListViewItemClick() completed");
    }

    /**
     * Retrieve the SwipeRefreshLayout and assign a listener to determine when the reload is necessary
     */
    private void configureListViewReload() {
        ListViewRefreshListener listener = new ListViewRefreshListener();
        mSwipeLayout.setOnRefreshListener(listener);
        Log.d(TAG, "MainActivity.configureListViewReload() completed");
    }

    /**
     * Encapsulates the creation of the Intent to display a SingleNews with more details.
     */
    protected class ListViewClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "ListViewClickListener.onItemClick(...): position=" + position + " id=" + id);

            // getting the SingleNews object from the list
            SingleNews item = (SingleNews)parent.getItemAtPosition(position);

            // creating the intent and send the Id as an Extra
            Intent intent = new Intent(view.getContext(), NewsDetailsActivity.class);
            intent.putExtra("news_id", item.Id);
            startActivity(intent);
        }
    }

    /**
     * Implements the OnRefresh event related to the Swipe Refresh Container Layout. The refresh
     * action launched the Loading Task.
     */
    protected class ListViewRefreshListener implements SwipeRefreshLayout.OnRefreshListener {

        @Override
        public void onRefresh() {
            createLoadingTask().execute();
        }
    }
}