package com.neilsonmarcil.droidnewsreader.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.neilsonmarcil.droidnewsreader.R;
import com.neilsonmarcil.droidnewsreader.dto.NewsComment;
import com.neilsonmarcil.droidnewsreader.util.TimeFormatUtil;


/**
 * Implements an ArrayAdapter to display a comment from a story and the latest reply associated to it
 */
public class CommentsListArrayAdapter extends ArrayAdapter<NewsComment> {

    private static final String TAG = "droidnewsreader";

    /**
     * the Context
     */
    private Context mContext;

    /**
     * the Layout Resource
     */
    private int mResource;

    /**
     * the list of Comments, including the latest comment
     */
    private NewsComment[] mComments;

    /**
     * default constructor
     * @param c the Context
     * @param ress the Layout resource
     * @param comments the data to display
     */
    public CommentsListArrayAdapter(Context c, int ress, NewsComment[] comments) {
        super(c, ress, comments);
        mContext = c;
        mResource = ress;
        mComments = comments;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        CommentHolder holder = new CommentHolder();

        if (row == null) {

            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
            row = inflater.inflate(mResource, null);

            // set the view resources stuff here
            holder.date = (TextView)row.findViewById(R.id.comment_list_item_date);
            holder.author = (TextView)row.findViewById(R.id.comment_list_item_author);
            holder.message = (TextView)row.findViewById(R.id.comment_list_item_content);
            holder.replyLayout = (LinearLayout)row.findViewById(R.id.comment_list_item_reply_layout);
            holder.replyAuthor = (TextView)row.findViewById(R.id.comment_list_item_reply_author);
            holder.replyDate = (TextView)row.findViewById(R.id.comment_list_item_reply_date);
            holder.replyMessage = (TextView)row.findViewById(R.id.comment_list_item_reply_content);

            // set the tag
            row.setTag(holder);

        }
        else {
            holder = (CommentHolder)row.getTag();
        }

        // set the properties here
        NewsComment comment = mComments[position];
        holder.author.setText(comment.Author);
        holder.message.setText(Html.fromHtml(comment.Body));
        holder.date.setText(TimeFormatUtil.formatTimespanAgoFirstUnit(System.currentTimeMillis() -
                            (comment.Date * TimeFormatUtil.UNIX_TO_JVM)));

        // checking if there is a reply to display
        if (comment.Replies.length > 0) {
            holder.replyLayout.setVisibility(View.VISIBLE);
            holder.replyMessage.setText(Html.fromHtml(comment.Replies[0].Body));
            holder.replyAuthor.setText(comment.Replies[0].Author);

            holder.replyDate.setText(TimeFormatUtil.formatTimespanAgoFirstUnit(System.currentTimeMillis() -
                                     (comment.Replies[0].Date) * TimeFormatUtil.UNIX_TO_JVM));
        }

        return row;
    }

    /**
     * a static holder to populate the UI
     */
    static class CommentHolder {
        TextView date;
        TextView author;
        TextView message;
        LinearLayout replyLayout;
        TextView replyDate;
        TextView replyAuthor;
        TextView replyMessage;
    }
}