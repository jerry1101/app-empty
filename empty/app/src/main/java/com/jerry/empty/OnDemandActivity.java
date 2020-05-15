package com.jerry.empty;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class OnDemandActivity extends AppCompatActivity {
    public static final String NEW_LINE = System.getProperty("line.separator");
    Spinner spinner;
    EditText domain;
    EditText url;
    TextView display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.on_demand);
        spinner = findViewById(R.id.httpSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.http_protocol, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        domain = findViewById(R.id.domainEditText);
        url = findViewById(R.id.linkEditText);
        display = findViewById(R.id.resultTextView);
        display.setMovementMethod(new ScrollingMovementMethod());

    }


    public void onButtonClick(View view) throws ExecutionException, InterruptedException, JSONException {

        String link = spinner.getSelectedItem().toString() + "://" + domain.getText() + url.getText();
//        String link = "https://www.totalwine.com";
        Log.i("RequestUrl : ", link);

        (new WebPageTask()).execute(new String[]{link});


    }

    private String JsonToString(JSONObject obj) throws JSONException {
//        Log.i("JSON: ",obj.toString());
        StringBuilder sb = new StringBuilder();
        sb.append(obj.getString("response"))
                .append(NEW_LINE)
                .append("1. title")
                .append(NEW_LINE)
                .append(obj.getString("title"))
                .append(NEW_LINE)
                .append("2. h1")
                .append(NEW_LINE)
                .append(obj.getString("h1"))
                .append(NEW_LINE)
                .append("3. h2")
                .append(NEW_LINE)
                .append(obj.getString("h2"))
                .append(NEW_LINE)
                .append("4. meta description")
                .append(NEW_LINE)
                .append(obj.getString("meta_description"))
                .append(NEW_LINE)
                .append("5. img alt")
                .append(NEW_LINE)
                .append(obj.getString("img_alt"))
                .append(NEW_LINE)
                .append("6. markup")
                .append(NEW_LINE)
                .append(obj.getString("markup"));

        return sb.toString();


    }

    private String getTextFromFirst(Elements elements) {
        if (!elements.isEmpty()) {
            return elements.first().text();
        }
        return "";
    }

    private class WebPageTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {

            String url = strings[0];
            JSONObject result = null;
            Connection.Response response = null;
            Document doc = null;
            try {
                response = Jsoup.connect(url)
                        .ignoreHttpErrors(true)
                        .followRedirects(true)
                        .execute();
                int status = response.statusCode();
//                Log.i("response code: ", "is " + status);
                if (status < 399) {
                    doc = Jsoup.connect(url).get();
                    Log.i("document:", doc.toString());
                    result = new JSONObject();
                    result.put("response", response.url() + "    " + status)
                            .put("title", doc.title())
                            .put("h1", getTextFromFirst(doc.select("h1")))
                            .put("h2", getTextFromFirst(doc.select("h2")))
                            .put("meta_description", String.valueOf(doc.select("meta[name='description']").first().attr("content")))
                            .put("img_alt", String.valueOf(doc.select("img").first().attr("alt")))
                            .put("markup", getTextFromFirst(doc.select("script[type='application/ld+json']")))
                    ;

                    display.setText(JsonToString(result));
                } else {
                    display.setText("Wrong link: " + url + NEW_LINE + "response code: " + status);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
