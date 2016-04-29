package com.neilsonmarcil.droidnewsreader.data;

import android.util.Log;
import com.neilsonmarcil.droidnewsreader.dto.NewsComment;
import com.neilsonmarcil.droidnewsreader.dto.SingleNews;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;

/**
 * This class specializes in getting content from a REST API. It is built to be a Singleton to
 * be able to cache data and do optimizing work later.
 */
public class RESTDataLayer implements DataLayer {

    private static final String TAG = "droidnewsreader";

    /**
     * the unique instance.
     */
    private static RESTDataLayer mInstance;

    /**
     * Returns a new RESTDataLayer instance.
     * @return
     */
    public static RESTDataLayer getInstance() {
        if (mInstance == null) {
            mInstance = new RESTDataLayer();
        }
        return mInstance;
    }

    /**
     * Represents the Top Stories Endpoint URL
     */
    private static final String TOP_STORIES_ENDPOINT = "https://hacker-news.firebaseio.com/v0/topstories.json";

    /**
     * Represents the Item Endpoint URL
     */
    private static final String ITEMS_ENDPOINT = "https://hacker-news.firebaseio.com/v0/item/%d.json";

    /**
     * Represents the default buffer size to read the response from the REST resource
     */
    private static final int BUFFER_SIZE = 1024;

    /**
     * Represents the byte value of the beginning of an Array
     */
    private static final int BEGINNING_ARRAY = 91;

    /**
     * Represents the byte value of the end of an Array
     */
    private static final int END_ARRAY = 93;

    /**
     * Represents the text representation for the beginning of an array in JSON
     */
    private static final String STR_BEGINNING_ARRAY = new String(new byte[] { BEGINNING_ARRAY });

    /**
     * Represents the text representation for the end of an array in JSON
     */
    private static final String STR_END_ARRAY = new String(new byte[] { END_ARRAY });

    /**
     * Represents the byte value of an element separator
     */
    private static final int ELEMENT_SEPARATOR = 44;

    /**
     * Represents the number of max stories to load by default
     */
    private static final int MAX_STORIES = 500;

    /**
     * Represents the number of comments to retreive
     */
    private static final int MAX_COMMENTS = 10;

    /**
     * Hold all the stories already downloaded
     */
    private HashMap<Integer, SingleNews> mStories;

    /**
     * Hold all the comments already downloaded
     */
    private HashMap<Integer, NewsComment> mComments;

    /**
     * protecting the constructor, usage of getInstance() is mandatory
     */
    protected RESTDataLayer() {
        mStories = new HashMap<>();
        mComments = new HashMap<>();
    }

    @Override
    public SingleNews[] getTopStories() {

        SingleNews[] topStories = new SingleNews[0];

        try {
            // call the URL over the internet
            URL url = new URL(TOP_STORIES_ENDPOINT);
            URLConnection conn = url.openConnection();

            // reading the stream
            byte[] content = getStreamContent(conn.getInputStream());

            // getting the item id from the endpoint
            int[] items = extractIntFromArray(content);
            int limit = Math.min(items.length, MAX_STORIES);
            topStories = new SingleNews[limit];

            for (int i = 0; i < limit; i++) {
                topStories[i] = getSpecificNews(items[i]);
            }
        }
        catch (MalformedURLException ex) {
            Log.e(TAG, "URL for Top Stories Endpoint malformed", ex);
        }
        catch (IOException ex) {
            Log.e(TAG, "Problem with the communication with the host", ex);
        }

        return topStories;
    }

    @Override
    public SingleNews getSpecificNews(int id) {

        Log.d(TAG, "RESTDataLayer.getSpecificNews(" + id + "): call");

        if (mStories.containsKey(id)) {
            return mStories.get(id);
        }
        else {
            try {
                // getting the specific id for
                URL url = new URL(String.format(ITEMS_ENDPOINT, id));
                URLConnection conn = url.openConnection();

                // reading the content
                byte[] content = getStreamContent(conn.getInputStream());

                // using JSON stuff
                JSONObject json = new JSONObject(new String(content));
                SingleNews news = new SingleNews();
                news.Id = id;
                news.Comments = extractIntArrayFromKey("kids", content);
                news.Url = getStringJsonValue("url", json);
                news.Author = getStringJsonValue("by", json);
                news.Score = getIntJsonValue("score", json);
                news.Title = getStringJsonValue("title", json);
                news.Date = getLongJsonValue("time", json);

                // adding to the list
                mStories.put(id, news);
                return news;
            }
            catch (MalformedURLException ex) {
                Log.e(TAG, "URL for Single Item Endpoint malformed", ex);
            }
            catch (IOException ex) {
                Log.e(TAG, "Problem with the communication with the host", ex);
            }
            catch (JSONException jsonex) {
                Log.e(TAG, "JSON received from host is not valid", jsonex);
            }
        }

        return new SingleNews();
    }

    @Override
    public NewsComment getSpecificComment(int id) {

        if (mComments.containsKey(id)) {
            return mComments.get(id);
        }

        try {
            // connecting
            URL url = new URL(String.format(ITEMS_ENDPOINT, id));
            URLConnection conn = url.openConnection();

            // reading the bytes
            byte[] content = getStreamContent(conn.getInputStream());

            // creating a JSON
            JSONObject json = new JSONObject(new String(content));
            NewsComment comment = new NewsComment();
            comment.Id = id;
            comment.CommentsIds = extractIntArrayFromKey("kids", content);
            comment.ParentId = getIntJsonValue("parent", json);
            comment.Author = getStringJsonValue("by", json);
            comment.Body = getStringJsonValue("text", json);
            comment.Date = getLongJsonValue("time", json);

            // keeping it in memory
            mComments.put(id, comment);
            return comment;

        }
        catch (MalformedURLException mfex) {
            Log.e(TAG, "URL for Items Endpoint not valid", mfex);
        }
        catch (IOException ioex) {
            Log.e(TAG, "Error while reading from the input stream", ioex);
        }
        catch (JSONException jsoex) {
            Log.e(TAG, "Error while using a JSON object", jsoex);
        }

        return new NewsComment();
    }

    @Override
    public NewsComment[] getCommentsFromNews(SingleNews news) {


        int max = Math.min(news.Comments.length, MAX_COMMENTS);
        NewsComment[] comments = new NewsComment[max];

        for (int i = 0; i < max; i++) {
            comments[i] = getSpecificComment(news.Comments[i]);

            // need to retreive the latest reply only
            if (comments[i].CommentsIds.length > 0) {
                comments[i].Replies = new NewsComment[1];
                comments[i].Replies[0] = getSpecificComment(comments[i].CommentsIds[0]);
            }
        }

        return comments;

    }

    /**
     * Return the string value from the JSONObject
     * @param key the key from the object
     * @param json the JSON Object
     * @return the string value, if not found, return new String()
     */
    private String getStringJsonValue(String key, JSONObject json) {
        try {
            return json.getString(key);
        }
        catch (JSONException ex) {
            Log.w(TAG, "Json object does not contain key: " + key, ex);
        }
        return new String();
    }

    /**
     * Returns a int value from the JSONObject
     * @param key the key from the object
     * @param json the JSON object
     * @return the int value, if not found, returns 0;
     */
    private int getIntJsonValue(String key, JSONObject json) {
        try {
            return json.getInt(key);
        }
        catch (JSONException ex) {
            Log.w(TAG, "Json object does not contain key: " + key, ex);
        }
        return 0;
    }

    /**
     * Returns a long value from the JSONObject
     * @param key the key from the object
     * @param json the JSON object
     * @return the value for the long key, if not found, return 0;
     */
    private long getLongJsonValue(String key, JSONObject json) {
        try {
            return json.getLong(key);
        }
        catch (Exception ex) {
            Log.w(TAG, "Json object does not contain key: " + key, ex);
        }
        return 0;
    }

    /**
     * extract an array of int from the byte array. Once the substrings are applied to the raw bytes.
     * this function will call extractIntFromArray(byte[]) to complete the operation.
     * @param key the key to start extraction
     * @param raw the byte content
     * @return an array on int
     */
    private int[] extractIntArrayFromKey(String key, byte[] raw) {
        // transforming the raw bytes to a string
        String content = new String(raw);

        // getting substring function do the magic
        int indexOfKey = content.indexOf(key);

        if (indexOfKey != -1) {
            String sub = content.substring(indexOfKey);

            int indexOfBeginning = sub.indexOf(STR_BEGINNING_ARRAY);
            int indexOfLast = sub.indexOf(STR_END_ARRAY);

            // call the byte functions to retreive the int array
            return extractIntFromArray(sub.substring(indexOfBeginning, indexOfLast + 1).getBytes());
        }

        return new int[0];

    }

    /**
     * Read the byte[] from the InputStream and closes it when done.
     * @param in the InputStream
     * @return a array of byte[] read from the InputStream
     */
    private byte[] getStreamContent(InputStream in) {
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            byte[] content = new byte[0];
            int read = 0;
            while ((read = in.read(buffer, 0, BUFFER_SIZE)) > 0) {
                content = accumulateBytes(content, buffer, read);
            }

            in.close();
            return content;
        }
        catch (IOException ex) {
            Log.e(TAG, "Could not read InputStream", ex);
        }

        return new byte[0];
    }

    /**
     * Read the content of a byte array and append it to the content array
     * @param content the content array that receive appended bytes
     * @param arr the bytes to append to content
     * @param read the number of bytes actually read. It can be different than the arr.length.
     * @return a new byte array containing the appended bytes (content + arr)
     */
    private byte[] accumulateBytes(byte[] content, byte[] arr, int read) {

        byte[] expended = Arrays.copyOf(content, content.length + read);

        for (int i = content.length, j = 0; i < expended.length && j < read; i++, j++) {
            expended[i] = arr[j];
        }

        return expended;
    }

    /**
     * This function reads the byte and extract the number from the represented array
     * @param content the byte array representing an array of integer
     * @return a int[] array, can be empty
     */
    private int[] extractIntFromArray(byte[] content) {

        // setting some control variables
        byte[] arrayContent = new byte[0];
        int[] items = new int[0];

        for (int i = 0; i < content.length; i++) {

            // array building mode
            if (content[i] != BEGINNING_ARRAY && content[i] != END_ARRAY) {

                // found a separator
                if (content[i] == ELEMENT_SEPARATOR) {
                    items = Arrays.copyOf(items, items.length + 1);
                    items[items.length - 1] = Integer.parseInt(new String(arrayContent));
                    arrayContent = new byte[0];
                }
                else {
                    arrayContent = Arrays.copyOf(arrayContent, arrayContent.length + 1);
                    arrayContent[arrayContent.length - 1] = content[i];
                }
            }
        }

        return items;
    }
}
