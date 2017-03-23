package com.example.shannon.albumcover;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Shannon on 24/03/2017.
 */

public class RetrieveImage  extends AsyncTask<String, Void, Bitmap> {

    private Exception exception;

    protected Bitmap doInBackground(String... urls) {
        try {

            return readUrl(urls[0]);
        } catch (Exception e) {
            this.exception = e;

            return null;
        }
    }

    private static Bitmap readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            InputStream is = (InputStream) new URL(urlString).getContent();
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            return bitmap;
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
