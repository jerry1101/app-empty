package com.jerry.empty;

import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class SeoBrandActivity extends AppCompatActivity {
    public static final String ODD_LINE_COLOR = "#000080";
    public static final String EVEN_LINE_COLOR = "#800080";
    FirebaseFirestore db;
    TextView displayArea;
    AutoCompleteTextView brandSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seo_brand);
        displayArea = findViewById(R.id.display_tv);
        db = FirebaseFirestore.getInstance();

        //initiate an auto complete text view
        brandSearch= (AutoCompleteTextView) findViewById(R.id.brand_actv);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, BrandNameFactory.getBrandNames());

        brandSearch.setAdapter(adapter);
        brandSearch.setThreshold(1);//start searching from 1 character
        brandSearch.setAdapter(adapter);   //set the adapter for displaying country name list
    }

    public void searchByBrand(View button) {
        displayArea.setText("searching............");


        DocumentReference brand = db.collection("brands").document(brandSearch.getText().toString());
        brand.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot result = task.getResult();
                    StringBuilder fields = null;

                    String meta_description = null;
                    try {
                        meta_description = getAttribute(new StringBuilder("").append(result.get("meta_description")).toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SAXException e) {
                        e.printStackTrace();
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    }

                    fields = new StringBuilder("")
                            .append(getColoredSpanned(new StringBuilder("").append("<h3>check at: </h3>" + result.get("check_at")).toString(), ODD_LINE_COLOR))
                            .append(getColoredSpanned(new StringBuilder("").append("<h2>1. url: </h2>" + result.get("url")).toString(), ODD_LINE_COLOR))
                            .append(getColoredSpanned(new StringBuilder("").append("<h2>2. h1: </h2>" + result.get("h1")).toString(), EVEN_LINE_COLOR))
                            .append(getColoredSpanned(new StringBuilder("").append("<h2>3. title: </h2>" + result.get("title_tag")).toString(), ODD_LINE_COLOR))
                            .append(getColoredSpanned(new StringBuilder("").append("<h2>4. meta description: </h2>" + meta_description).toString(), EVEN_LINE_COLOR))
                            .append(getColoredSpanned(new StringBuilder("").append("<h2>5. h2: </h2>" + result.get("h2")).toString(), ODD_LINE_COLOR))
                            .append(getColoredSpanned(new StringBuilder("").append("<h2>6. schema: </h2>" + result.get("schema_markup")).toString(), EVEN_LINE_COLOR))
                    ;


                    displayArea.setMovementMethod(new ScrollingMovementMethod());
                    displayArea.setText(Html.fromHtml(fields.toString()));
//                    addDisplayContent("1.h1:",result.get("h1").toString());
//                    addDisplayContent("2.h2:",result.get("h2").toString());

//                    StringBuilder fields = new StringBuilder("").append("h1: " + result.get("h1")).append("\nh2: " + result.get("h2"));
//
//                    displayArea.setText(fields.toString());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }


    private String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }

    private String getAttribute(String xmlString) throws IOException, SAXException, ParserConfigurationException {

        Document document = parseXmlFromString(xmlString);
        NodeList nodeList = document.getElementsByTagName("meta");

        return nodeList.item(0).getAttributes().getNamedItem("content").getNodeValue();

    }

    private Document parseXmlFromString(String xmlString) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream inputStream = new ByteArrayInputStream(xmlString.getBytes());
        org.w3c.dom.Document document = builder.parse(inputStream);
        return document;
    }
}
