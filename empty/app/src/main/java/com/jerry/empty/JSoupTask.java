package com.jerry.empty;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class JSoupTask  extends AsyncTask<String, Void, Document> {
    @Override
    protected Document doInBackground(String... strings) {
        //This is the Firebase URL where data will be fetched from
        String url = strings[0];
//Connect to website
        Document document = null;
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return document;
    }
}
