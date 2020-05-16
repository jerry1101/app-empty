package com.jerry.empty;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class OnDemandActivity extends AppCompatActivity {
    public static final String NEW_LINE = System.getProperty("line.separator");
    public static HashMap<String,String> USER_AGENTS=new HashMap<String,String>();
    JSONObject viewModel = null;
    Spinner spinner;
    EditText domain;
    EditText url;
    TextView display;
    String userAgent;
    ImageButton share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.on_demand);
        USER_AGENTS.put("mobile","Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_1 like Mac OS X) AppleWebKit/603.1.30 (KHTML, like Gecko) Version/10.0 Mobile/14E304 Safari/602.1");
        USER_AGENTS.put("desktop","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");

        spinner = findViewById(R.id.httpSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.user_agent_type, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        domain = findViewById(R.id.domainEditText);
        url = findViewById(R.id.linkEditText);
        display = findViewById(R.id.resultTextView);
        display.setMovementMethod(new ScrollingMovementMethod());

        share = findViewById(R.id.shareButton);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareSubject = "Share SEO analysis result";
                String shareBody = (viewModel != null)? viewModel.toString():"Nothing to share";

                shareIntent.putExtra(Intent.EXTRA_SUBJECT,shareSubject);
                shareIntent.putExtra(Intent.EXTRA_TEXT,shareBody);

                startActivity(Intent.createChooser(shareIntent,"Start share"));
            }
        });


    }


    public void onButtonClick(View view) throws ExecutionException, InterruptedException, JSONException {
        userAgent = USER_AGENTS.get(spinner.getSelectedItem().toString().toLowerCase());
        String link = new StringBuilder().append(domain.getText()).append(url.getText()).toString();
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

    private String getTextFromMany(Elements elements) {
        StringBuilder sb = new StringBuilder();
        int sbLength=0;
        if (elements.isEmpty()) {
            return "";
        }
        else {
            for(Element e : elements){
                sb.append(e.text()).append(NEW_LINE);
                sbLength = sbLength+(e.text().length());
            }
        }
        return (sbLength > 1)? sb.toString():NEW_LINE;
    }

    private String getJsonFromMany(Elements elements) {
        String result;
        StringBuilder sb = new StringBuilder();
        if (elements.isEmpty()) {
            return "";
        }
        else {
            for (Element e : elements) {
                sb.append(Html.fromHtml(e.html())).append(NEW_LINE+ NEW_LINE);
            }
        }
        return sb.toString();
    }

    private String getAttrFromFirst(Elements elements, String attr) {
        String result;
        if (elements.isEmpty()) {
            return "";
        }
        else {
            result = elements.first().attr(attr);
        }
        return result;
    }

    private class WebPageTask extends AsyncTask<String, Void, Void> {


        @Override
        protected Void doInBackground(String... strings) {

            String url = strings[0];

            Connection.Response response = null;
            Document doc = null;
            String responseUrl = null;
            try {
                response = Jsoup.connect(url)
                        .userAgent(userAgent)
                        .ignoreHttpErrors(true)
                        .followRedirects(true)
                        .execute();
                int responseStatus = response.statusCode();
                responseUrl = response.url().toString();
//                Log.i("response code: ", "is " + responseStatus);
                if (responseStatus < 399) {
                    doc = Jsoup.connect(url)
                            .userAgent(userAgent)
                            .ignoreHttpErrors(true)
                            .followRedirects(true)
                            .get();
                    Log.i("document:", doc.toString());
                    viewModel = new JSONObject();
                    viewModel.put("response", responseUrl + "    " + responseStatus)
                            .put("title", doc.title())
                            .put("h1", getTextFromFirst(doc.select("h1")))
                            .put("h2", getTextFromMany(doc.select("h2")))
                            .put("meta_description", getAttrFromFirst(doc.select("meta[name='description']"),"content"))
                            .put("img_alt", String.valueOf(doc.select("img").first().attr("alt")))
                            .put("markup", getJsonFromMany(doc.select("script[type=application/ld+json]")))
                    ;

                    display.setText(JsonToString(viewModel));
                } else {
                    display.setText("Wrong link: " + url + NEW_LINE + "response code: " + responseStatus);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
