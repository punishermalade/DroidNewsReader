package com.neilsonmarcil.droidnewsreader.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.neilsonmarcil.droidnewsreader.R;
import com.neilsonmarcil.droidnewsreader.dto.SingleNews;
import com.neilsonmarcil.droidnewsreader.util.TimeFormatUtil;

/**
 * Encapsulates the logic to create a ArrayAdapter to display a list of SingleNews.
 */
public class NewsListArrayAdapter extends ArrayAdapter<SingleNews> {

    private static final String TAG = "droidnewsreader";

    /**
     * the Context
     */
    private Context mContext;

    /**
     * The layout to use
     */
    private int mResourceId;

    /**
     * the object to display
     */
    private SingleNews[] mNewsObjects;

    /**
     * Default constructor
      * @param context the Context from the calling Activity
     * @param resource the Resource Layout representing a single item
     * @param objects the list of SingleNews to be displayed
     */
    public NewsListArrayAdapter(Context context, int resource, SingleNews[] objects) {
        super(context, resource, objects);
        mContext = context;
        mResourceId = resource;
        mNewsObjects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SingleNewsHolder holder;
        View row = convertView;

        if (convertView == null) {
            row =  ((Activity)mContext).getLayoutInflater().inflate(mResourceId, parent, false);

            // filling the holder with UI stuff
            holder = new SingleNewsHolder();
            holder.date = (TextView)row.findViewById(R.id.news_list_item_date);
            holder.title = (TextView)row.findViewById(R.id.news_list_item_title);
            holder.author = (TextView)row.findViewById(R.id.news_list_item_author);
            holder.score = (TextView)row.findViewById(R.id.news_list_item_score);
            holder.url = (Button)row.findViewById(R.id.news_list_item_url);

            row.setTag(holder);
        }
        else {
            holder = (SingleNewsHolder)row.getTag();
        }

        SingleNews news = mNewsObjects[position];
        holder.title.setText(news.Title);
        holder.author.setText(news.Author);
        holder.score.setText(String.valueOf(news.Score));

        // set the date
        holder.date.setText(TimeFormatUtil.formatTimespanAgoFirstUnit(
                System.currentTimeMillis() - (news.Date * TimeFormatUtil.UNIX_TO_JVM)));

        // set the onclick listener here
        holder.url.setTag(news.Url);

        if (!news.Url.equalsIgnoreCase("")) {
            holder.url.setOnClickListener(configureURLClickListener());
        }
        else {
            holder.url.setOnClickListener(configureNoURLClickListener());
        }

        return row;
    }

    /**
     * Create a new instance of View.OnClickListener to be used on the Visit website button
     * @return a instance of View.OnClickListener
     */
    private View.OnClickListener configureURLClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = String.valueOf(v.getTag());

                if (url != null) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url));
                    v.getContext().startActivity(browserIntent);
                }
            }
        };
    }

    /**
     * Create a new instance of View.OnClickListener that display an error message due to an invalid
     * url
     * @return a instance of View.OnClickListener
     */
    private View.OnClickListener configureNoURLClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle(R.string.dialog_url_error_title);
                builder.setMessage(v.getContext().getString(R.string.dialog_url_error_message));
                builder.setPositiveButton(android.R.string.ok, null);
                builder.show();
            }
        };
    }

    /**
     * Simple holder to make it easier assigning data to the TextView
     */
    static class SingleNewsHolder {
        public TextView title;
        public TextView date;
        public TextView author;
        public TextView score;
        public Button url;
    }
}
