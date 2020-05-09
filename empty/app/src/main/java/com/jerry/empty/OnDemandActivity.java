package com.jerry.empty;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import org.jsoup.nodes.Document;

import java.util.concurrent.ExecutionException;

public class OnDemandActivity extends AppCompatActivity {
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
        domain= findViewById(R.id.domainEditText);
        url = findViewById(R.id.linkEditText);
        display = findViewById(R.id.resultTextView);



    }


    private Document getHttpResponse(String link) throws ExecutionException, InterruptedException {
        JSoupTask task = new JSoupTask();
        return task.execute(link).get();
    }

    public void onButtonClick(View view)  {
        String newLine = System.getProperty("line.separator");
        String link = spinner.getSelectedItem().toString()+"://"+domain.getText()+url.getText();
//        String link = "http://www.totalwine.com";
        Log.i("RequestUrl : ",link);

        Document doc = null;
        try {
            doc = getHttpResponse(link);
//            doc = getHttpResponse("http://www.totalwine.com");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i("HttpResponse:  ", doc.title());
        Log.i("H2: ", String.valueOf(doc.select("h2").first()));
        Log.i("Alt: ", String.valueOf(doc.select("img").first()));
        StringBuilder sb = new StringBuilder();
        sb.append("1. Title: "+newLine+doc.title()+newLine)
                .append("2. H1: "+newLine+String.valueOf(doc.select("h1").first().text())+newLine)
                .append("3. H2: "+newLine+String.valueOf(doc.select("h2").first().text())+newLine)
                .append("4. Alt: "+newLine+String.valueOf(doc.select("img").first().attr("alt"))+newLine);
        display.setMovementMethod(new ScrollingMovementMethod());
        display.setText(sb.toString());

    }

}
