package com.example.shannon.albumcover;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Shannon on 24/03/2017.
 */

public class RetrieveAlbumActivity extends AsyncTask<String, Void, String> {

    private Exception exception;

    protected String doInBackground(String... urls) {
        try {
            return readUrl(urls[0]);
        } catch (Exception e) {
            this.exception = e;

            return null;
        }
    }

    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            //URL of where to retrieve lsit of albums
            URL url = new URL(urlString);
            //Open the stream
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);
            //Return the JSON file
            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    protected void onPostExecute() {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}
